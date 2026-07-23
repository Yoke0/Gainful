package com.yoke.gainful.sync

import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.api.TransactionResponse
import com.yoke.gainful.common.extensions.formatLocalizedDateTime
import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.datastore.SyncDataSource
import com.yoke.gainful.datastore.UserDataSource
import com.yoke.gainful.domain.usecase.asset.SearchAssetsUseCase
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.network.server.TransactionApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.toInstant
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class TransactionSyncService(
    private val transactionApi: TransactionApi,
    private val transactionRepository: TransactionRepository,
    private val syncQueueRepository: SyncQueueRepository,
    private val userDataSource: UserDataSource,
    private val syncDataSource: SyncDataSource,
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
                    delay(PERIODIC_SYNC_INTERVAL_MS.milliseconds)
                    sync()
                }
            }
    }

    fun stopPeriodicSync() {
        syncJob?.cancel()
        syncJob = null
    }

    suspend fun sync() {
        val state = userDataSource.userState.first()
        if (!state.isLoggedIn) return

        println("TransactionSync: Starting sync...")
        try {
            // 1. PULL — fetch from server and merge into local DB
            val lastSyncTime = syncDataSource.getLastTransactionSyncTime()
            val serverTransactions =
                if (lastSyncTime > 0) {
                    transactionApi.getTransactions(since = lastSyncTime)
                } else {
                    transactionApi.getTransactions()
                }
            println("TransactionSync: Got ${serverTransactions.size} transactions from server, since=$lastSyncTime")

            val toMerge =
                serverTransactions
                    .filter { it.deletedAt == null }
                    .map { it.toDomain() }
            if (toMerge.isNotEmpty()) {
                transactionRepository.mergeServerTransactions(toMerge)
                enrichAssets(toMerge)
            }

            val toDeleteLocally =
                serverTransactions
                    .filter { it.deletedAt != null }
                    .map { it.id }
            if (toDeleteLocally.isNotEmpty()) {
                transactionRepository.deleteByIds(toDeleteLocally)
            }

            // Update timestamp ONLY after successful merge
            syncDataSource.setLastTransactionSyncTime(Clock.System.now().toEpochMilliseconds())

            // 2. PUSH — upload pending local operations
            processSyncQueue()

            println("TransactionSync: Sync completed successfully")
        } catch (e: Exception) {
            println("TransactionSync: Sync failed: ${e.message}")
        }
    }

    private suspend fun processSyncQueue() {
        val pendingItems = syncQueueRepository.getAll()
        for (item in pendingItems) {
            when (item.operation) {
                "CREATE" -> {
                    val tx = transactionRepository.getTransactionById(item.entityId)
                    if (tx != null) {
                        runCatching {
                            val result = transactionApi.createTransaction(tx.toCreateRequest())
                            syncQueueRepository.remove(item.id)
                            // Update local ID to match server-assigned ID
                            transactionRepository.updateId(item.entityId, result.id)
                        }
                    } else {
                        syncQueueRepository.remove(item.id)
                    }
                }

                "DELETE" -> {
                    runCatching {
                        transactionApi.deleteTransaction(item.entityId)
                        syncQueueRepository.remove(item.id)
                    }
                }
            }
        }
    }

    private fun TransactionResponse.toDomain() =
        Transaction(
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

    private fun Transaction.toCreateRequest() =
        CreateTransactionRequest(
            assetCode = assetId,
            type = type.value,
            quantity = quantity,
            price = price,
            amount = amount,
            tradeDate = tradeDate.formatLocalizedDateTime(),
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

    private suspend fun enrichAssets(transactions: List<Transaction>) {
        val codes = transactions.map { it.assetId }.distinct()
        val dbAssets =
            codes.mapNotNull { code ->
                val assets = assetRepository.searchAssets(code)
                assets.find { it.innerCode == code || it.code == code }?.let { code to it }
            }.toMap()
        val codesToSearch = codes.filter { it !in dbAssets }
        val searchResults =
            coroutineScope {
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
