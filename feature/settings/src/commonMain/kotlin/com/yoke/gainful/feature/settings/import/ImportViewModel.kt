
@file:Suppress("ktlint:standard:package-name")

package com.yoke.gainful.feature.settings.`import`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.datastore.AuthDataSource
import com.yoke.gainful.domain.usecase.asset.SearchAssetsUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsWithAssetsOnceUseCase
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.feature.settings.model.CsvPreviewData
import com.yoke.gainful.feature.settings.model.toDisplayItems
import com.yoke.gainful.feature.settings.util.CsvUtil
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.network.server.TransactionApi
import com.yoke.gainful.ui.TransactionDisplayItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ImportViewModel(
    private val transactionRepository: TransactionRepository,
    private val syncQueueRepository: SyncQueueRepository,
    private val transactionApi: TransactionApi,
    private val authDataSource: AuthDataSource,
    private val getTransactionsWithAssetsOnceUseCase: GetTransactionsWithAssetsOnceUseCase,
    private val assetRepository: AssetRepository,
    private val searchAssetsUseCase: SearchAssetsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    companion object {
        // Application-level scope for uploads — not tied to any ViewModel
        private val uploadScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    fun onIntent(intent: ImportIntent) {
        when (intent) {
            is ImportIntent.ParseCsv -> parseCsv(intent)
            is ImportIntent.DeleteItem -> deleteItem(intent.index)
            is ImportIntent.ConfirmImport -> confirmImport(intent.csvConfig)
            is ImportIntent.Reset -> resetState()
            is ImportIntent.ShowDeleteDialog -> showDeleteDialog(intent.index, intent.item)
            is ImportIntent.DismissDeleteDialog -> dismissDeleteDialog()
            is ImportIntent.ShowDuplicateConfirm -> showDuplicateConfirm()
            is ImportIntent.DismissDuplicateConfirm -> dismissDuplicateConfirm()
        }
    }

    private fun parseCsv(intent: ImportIntent.ParseCsv) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasParseError = false) }

            val txsWithAssets = getTransactionsWithAssetsOnceUseCase()
            val existingIds =
                txsWithAssets.map { tx ->
                    "${tx.transaction.assetId}_${tx.transaction.tradeDate}_${tx.transaction.type.value}"
                }.toSet()

            val result = CsvUtil.parseCsv(intent.csvContent, intent.csvConfig, existingIds)
            if (result != null) {
                _uiState.update {
                    it.copy(preview = result.copy(fileName = intent.fileName))
                }
                enrichAssets(result, intent.csvConfig)
            } else {
                _uiState.update {
                    it.copy(hasParseError = true, isLoading = false)
                }
            }
        }
    }

    private suspend fun enrichAssets(preview: CsvPreviewData, csvConfig: CsvConfig) {
        val codeIndex = preview.headers.indexOf(csvConfig.assetCodeHeader)
        if (codeIndex < 0) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        val displayItems = preview.toDisplayItems(csvConfig).toMutableList()

        val codesToEnrich =
            displayItems
                .filter { it.name.isBlank() || it.pinYin.isBlank() }
                .map { it.code }
                .distinct()

        val dbAssets =
            codesToEnrich.mapNotNull { code ->
                val assets = assetRepository.searchAssets(code)
                assets.find { it.innerCode == code || it.code == code }?.let { code to it }
            }.toMap()

        val codesToSearch = codesToEnrich.filter { it !in dbAssets }

        val searchResults =
            codesToSearch.map { code ->
                viewModelScope.async {
                    runCatching {
                        val results = searchAssetsUseCase(code)
                        code to results.find { it.innerCode == code || it.code == code }
                    }.getOrNull() ?: (code to null)
                }
            }.awaitAll().toMap()

        searchResults.forEach { (_, asset) ->
            if (asset != null) {
                assetRepository.insertAsset(asset)
            }
        }

        val assetMap = dbAssets + searchResults.filterValues { it != null }.mapValues { it.value!! }

        for ((index, item) in displayItems.withIndex()) {
            if (item.name.isNotBlank() && item.pinYin.isNotBlank()) continue
            val asset = assetMap[item.code] ?: continue
            displayItems[index] = item.copy(name = asset.name, pinYin = asset.pinYin)
        }

        _uiState.update {
            it.copy(displayItems = displayItems, isLoading = false)
        }
    }

    private fun deleteItem(index: Int) {
        _uiState.update {
            val preview = it.preview ?: return@update it
            val deletedIndices = preview.deletedIndices.toMutableSet()
            deletedIndices.add(index)
            val wasDuplicate = index in preview.duplicateIndices
            val newDuplicateCount = if (wasDuplicate) preview.duplicateCount - 1 else preview.duplicateCount
            it.copy(
                preview =
                    preview.copy(
                        deletedIndices = deletedIndices,
                        duplicateCount = newDuplicateCount.coerceAtLeast(0),
                    ),
                displayItems = it.displayItems.toMutableList().apply { removeAt(index) },
            )
        }
    }

    private fun confirmImport(csvConfig: CsvConfig) {
        viewModelScope.launch {
            val preview = _uiState.value.preview ?: return@launch
            val transactions =
                CsvUtil.parseToTransactions(
                    csvContent = preview.rawCsv,
                    config = csvConfig,
                    deletedIndices = preview.deletedIndices,
                )
            // Save locally first (fast)
            if (transactions.isNotEmpty()) {
                transactionRepository.insertTransactions(transactions)
            }
            _uiState.update {
                it.copy(
                    preview = null,
                    displayItems = emptyList(),
                    importSuccess = true,
                    importedCount = transactions.size,
                )
            }
            // Upload to server in independent scope (survives navigation)
            if (transactions.isNotEmpty()) {
                uploadToServer(transactions)
            }
        }
    }

    private fun uploadToServer(transactions: List<Transaction>) {
        uploadScope.launch {
            val state = authDataSource.authState.first()
            val token = state.token
            transactions.map { tx ->
                async {
                    if (token != null) {
                        val result =
                            runCatching {
                                transactionApi.createTransaction(token, tx.toCreateRequest())
                            }
                        if (result.isSuccess) return@async
                    }
                    syncQueueRepository.enqueue("transaction", tx.id, "CREATE")
                }
            }.awaitAll()
        }
    }

    private fun resetState() {
        _uiState.update { ImportUiState() }
    }

    private fun showDeleteDialog(index: Int, item: TransactionDisplayItem) {
        _uiState.update {
            it.copy(deleteDialog = DeleteDialogState(index, item))
        }
    }

    private fun dismissDeleteDialog() {
        _uiState.update {
            it.copy(deleteDialog = null)
        }
    }

    private fun showDuplicateConfirm() {
        _uiState.update { it.copy(showDuplicateConfirm = true) }
    }

    private fun dismissDuplicateConfirm() {
        _uiState.update { it.copy(showDuplicateConfirm = false) }
    }
}

private fun Transaction.toCreateRequest() =
    CreateTransactionRequest(
        assetCode = assetId,
        type = type.value,
        quantity = quantity,
        price = price,
        amount = amount,
        tradeDate = formatDate(tradeDate),
    )

private fun formatDate(millis: Long): String {
    val ldt =
        Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    return ldt.date.toString()
}

data class DeleteDialogState(
    val index: Int = -1,
    val item: TransactionDisplayItem? = null,
)

data class ImportUiState(
    val preview: CsvPreviewData? = null,
    val displayItems: List<TransactionDisplayItem> = emptyList(),
    val hasParseError: Boolean = false,
    val isLoading: Boolean = false,
    val importSuccess: Boolean = false,
    val importedCount: Int = 0,
    val deleteDialog: DeleteDialogState? = null,
    val showDuplicateConfirm: Boolean = false,
)
