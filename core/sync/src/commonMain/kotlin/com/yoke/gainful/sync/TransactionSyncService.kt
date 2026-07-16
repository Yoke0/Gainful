package com.yoke.gainful.sync

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.datastore.AuthDataSource
import com.yoke.gainful.domain.usecase.asset.SearchAssetsUseCase
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.network.TransactionApi
import com.yoke.gainful.network.model.CreateTransactionRequestDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class TransactionSyncService(
    private val transactionApi: TransactionApi,
    private val transactionRepository: TransactionRepository,
    private val syncQueueRepository: SyncQueueRepository,
    private val authDataSource: AuthDataSource,
    private val assetRepository: AssetRepository,
    private val searchAssetsUseCase: SearchAssetsUseCase,
) {
    private var syncJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun startPeriodicSync() {
        syncJob?.cancel()
        syncJob =
            scope.launch {
                while (isActive) {
                    delay(PERIODIC_SYNC_INTERVAL_MS)
                    sync()
                }
            }
    }

    fun stopPeriodicSync() {
        syncJob?.cancel()
        syncJob = null
    }

    suspend fun sync() {
        val state = authDataSource.authState.first()
        val token = state.token ?: return

        println("TransactionSync: Starting sync, token=${token?.take(10)}...")
        try {
            // 1. Process sync queue (upload pending operations)
            processSyncQueue(token)

            // 2. Get max updatedAt from local transactions (more accurate than DataStore)
            val lastSyncTime = transactionRepository.getMaxUpdatedAt() ?: 0L

            // 3. Fetch from server (incremental or full)
            val serverTransactions =
                if (lastSyncTime > 0) {
                    transactionApi.getTransactions(token, since = lastSyncTime)
                } else {
                    transactionApi.getTransactions(token)
                }
            println("TransactionSync: Got ${serverTransactions.size} transactions from server, since=$lastSyncTime")

            // 4. Get local transactions
            val localTransactions = transactionRepository.getAllTransactions()
            val localMap = localTransactions.associateBy { it.id }
            val serverMap = serverTransactions.associateBy { it.id }

            // 5. Merge: server data overwrites local (INSERT OR REPLACE)
            val toUpsert =
                serverTransactions
                    .filter { it.deletedAt == null }
                    .map { it.toDomain() }
            println("TransactionSync: Upserting ${toUpsert.size} transactions to local DB")
            if (toUpsert.isNotEmpty()) {
                transactionRepository.insertTransactions(toUpsert)
                enrichAssets(toUpsert)
            }

            // 6. Handle soft deletes
            val toDeleteLocally =
                serverTransactions
                    .filter { it.deletedAt != null }
                    .map { it.id }
            if (toDeleteLocally.isNotEmpty()) {
                transactionRepository.deleteByIds(toDeleteLocally)
            }

            // 7. Upload local-only transactions (orphan from offline mode)
            val pendingCreates =
                syncQueueRepository.getAll()
                    .filter { it.operation == "CREATE" }
                    .map { it.entityId }
                    .toSet()

            val localOnly =
                localTransactions.filter {
                    it.id !in serverMap && it.id !in pendingCreates
                }
            for (tx in localOnly) {
                runCatching {
                    val result =
                        runCatching {
                            transactionApi.createTransaction(token, tx.toCreateRequest())
                        }
                    if (result.isFailure) {
                        syncQueueRepository.enqueue("transaction", tx.id, "CREATE")
                    }
                }
            }

            // 8. Cleanup: local has, server doesn't, not in sync queue -> delete
            val serverIds = serverTransactions.filter { it.deletedAt == null }.map { it.id }.toSet()
            val allPendingIds = pendingCreates
            val toCleanup =
                localTransactions.filter {
                    it.id !in serverIds && it.id !in allPendingIds
                }.map { it.id }
            if (toCleanup.isNotEmpty()) {
                transactionRepository.deleteByIds(toCleanup)
            }

            println("TransactionSync: Sync completed successfully")
        } catch (e: Exception) {
            println("TransactionSync: Sync failed: ${e.message}")
            // Sync failed, will retry on next periodic sync
        }
    }

    private suspend fun processSyncQueue(token: String) {
        val pendingItems = syncQueueRepository.getAll()
        for (item in pendingItems) {
            when (item.operation) {
                "CREATE" -> {
                    // Check if transaction still exists locally
                    val tx = transactionRepository.getTransactionById(item.entityId)
                    if (tx != null) {
                        runCatching {
                            transactionApi.createTransaction(token, tx.toCreateRequest())
                            syncQueueRepository.remove(item.id)
                        }
                    } else {
                        // Transaction deleted locally, remove from queue
                        syncQueueRepository.remove(item.id)
                    }
                }

                "DELETE" -> {
                    runCatching {
                        transactionApi.deleteTransaction(token, item.entityId)
                        syncQueueRepository.remove(item.id)
                    }
                }
            }
        }
    }

    private fun com.yoke.gainful.network.model.TransactionDto.toDomain() =
        com.yoke.gainful.model.Transaction(
            id = id,
            assetId = assetCode,
            type = TransactionType.fromCode(type),
            quantity = quantity,
            price = price,
            amount = amount,
            tradeDate = parseDate(tradeDate),
            timestamp = parseDateTime(createdAt),
            updatedAt = parseDateTime(updatedAt),
        )

    private fun com.yoke.gainful.model.Transaction.toCreateRequest() =
        CreateTransactionRequestDto(
            assetCode = assetId,
            type = type.value,
            quantity = quantity,
            price = price,
            amount = amount,
            tradeDate = formatDate(tradeDate),
        )

    private fun parseDate(dateStr: String): Long {
        return try {
            val parts = dateStr.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val day = parts[2].toInt()
            val ldt = kotlinx.datetime.LocalDateTime(year, month + 1, day, 0, 0, 0)
            ldt.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds()
        } catch (_: Exception) {
            0L
        }
    }

    private fun parseDateTime(dateTimeStr: String): Long {
        return try {
            val ldt = kotlinx.datetime.LocalDateTime.parse(dateTimeStr)
            ldt.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds()
        } catch (_: Exception) {
            0L
        }
    }

    private fun formatDate(millis: Long): String {
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        val ldt = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        return ldt.date.toString()
    }

    private suspend fun enrichAssets(transactions: List<com.yoke.gainful.model.Transaction>) {
        val codes = transactions.map { it.assetId }.distinct()
        val dbAssets =
            codes.mapNotNull { code ->
                val assets = assetRepository.searchAssets(code)
                assets.find { it.innerCode == code || it.code == code }?.let { code to it }
            }.toMap()
        val codesToSearch = codes.filter { it !in dbAssets }
        val searchResults =
            kotlinx.coroutines.coroutineScope {
                codesToSearch.map { code ->
                    async {
                        runCatching {
                            val results = searchAssetsUseCase(code)
                            code to results.find { it.innerCode == code || it.code == code }
                        }.getOrNull() ?: (code to null)
                    }
                }.awaitAll().toMap()
            }
        searchResults.values.filterNotNull().forEach { asset ->
            assetRepository.insertAsset(asset)
        }
        println("TransactionSync: Enriched ${searchResults.values.count { it != null }} assets")
    }

    companion object {
        private const val PERIODIC_SYNC_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
    }
}
