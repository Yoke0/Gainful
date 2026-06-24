package com.yoke.gainful.feature.settings.`import`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.domain.usecase.asset.SearchAssetsUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsWithAssetsOnceUseCase
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.feature.settings.model.CsvPreviewData
import com.yoke.gainful.feature.settings.model.toDisplayItems
import com.yoke.gainful.feature.settings.util.CsvUtil
import com.yoke.gainful.ui.components.TransactionDisplayItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImportViewModel(
    private val transactionRepository: TransactionRepository,
    private val getTransactionsWithAssetsOnceUseCase: GetTransactionsWithAssetsOnceUseCase,
    private val assetRepository: AssetRepository,
    private val searchAssetsUseCase: SearchAssetsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ImportIntent) {
        when (intent) {
            is ImportIntent.ParseCsv -> parseCsv(intent)
            is ImportIntent.DeleteItem -> deleteItem(intent.index)
            is ImportIntent.ConfirmImport -> confirmImport(intent.csvConfig)
            is ImportIntent.Reset -> resetState()
        }
    }

    private fun parseCsv(intent: ImportIntent.ParseCsv) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasParseError = false) }

            val txsWithAssets = getTransactionsWithAssetsOnceUseCase()
            val existingIds = txsWithAssets.map { tx ->
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

        for ((index, item) in displayItems.withIndex()) {
            if (item.name.isNotBlank() && item.pinYin.isNotBlank()) continue

            val asset = assetRepository.getAssetByInnerCode(item.code)
            if (asset != null) {
                displayItems[index] = item.copy(name = asset.name, pinYin = asset.pinYin)
            } else {
                runCatching {
                    val results = searchAssetsUseCase(item.code)
                    results.find { it.innerCode == item.code || it.code == item.code }
                }.getOrNull()?.let { matched ->
                    assetRepository.insertAsset(matched)
                    displayItems[index] = item.copy(name = matched.name, pinYin = matched.pinYin)
                }
            }
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
            val newDuplicateCount = preview.duplicateCount -
                deletedIndices.count { idx -> idx in preview.duplicateIndices }
            it.copy(
                preview = preview.copy(
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
            val transactions = CsvUtil.parseToTransactions(
                csvContent = preview.rawCsv,
                config = csvConfig,
                deletedIndices = preview.deletedIndices,
            )
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
        }
    }

    private fun resetState() {
        _uiState.update { ImportUiState() }
    }
}

data class ImportUiState(
    val preview: CsvPreviewData? = null,
    val displayItems: List<TransactionDisplayItem> = emptyList(),
    val hasParseError: Boolean = false,
    val isLoading: Boolean = false,
    val importSuccess: Boolean = false,
    val importedCount: Int = 0,
)
