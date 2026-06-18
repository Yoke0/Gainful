package com.yoke.gainful.feature.transactions.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.asset.SearchAssetsUseCase
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.domain.usecase.transaction.AddTransactionUseCase
import com.yoke.gainful.common.extensions.formatTwoDecimals
import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddTransactionViewModel(
    private val searchAssetsUseCase: SearchAssetsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getHoldingsDisplayUseCase: GetHoldingsDisplayUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddTransactionUiState(date = todayDateString()),
    )
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadHoldings()
    }

    private fun loadHoldings() {
        viewModelScope.launch {
            getHoldingsDisplayUseCase().collect { holdings ->
                _uiState.update { it.copy(holdings = holdings) }
            }
        }
    }

    fun onTypeSelected(type: TransactionType) {
        _uiState.update {
            it.copy(
                type = type,
                showSearch = false,
                searchQuery = "",
                suggestions = emptyList(),
                showSuggestions = false,
            )
        }
    }

    fun onToggleSearch() {
        _uiState.update {
            it.copy(
                showSearch = !it.showSearch,
                searchQuery = "",
                suggestions = emptyList(),
                showSuggestions = false,
            )
        }
    }

    fun onCollapseSearch() {
        _uiState.update {
            it.copy(
                showSearch = false,
                searchQuery = "",
                suggestions = emptyList(),
                showSuggestions = false,
            )
        }
    }

    fun onAssetSelectedFromHolding(holding: HoldingDisplay) {
        val asset = Asset(
            innerCode = holding.code,
            code = holding.code,
            name = holding.name,
            pinYin = holding.pinYin,
            id = holding.assetId,
            jys = "",
            classify = "",
            marketType = "",
            typeName = "",
            securityType = "",
            market = 0,
            typeUS = "",
            quoteId = "",
            unifiedCode = holding.assetId,
        )
        _uiState.update {
            it.copy(
                selectedAsset = asset,
                showSearch = false,
                searchQuery = "",
                suggestions = emptyList(),
                showSuggestions = false,
            )
        }
    }

    fun onAssetSelected(asset: Asset) {
        _uiState.update {
            it.copy(
                selectedAsset = asset,
                searchQuery = "",
                suggestions = emptyList(),
                showSuggestions = false,
                showSearch = false,
            )
        }
    }

    fun onAssetCleared() {
        _uiState.update {
            it.copy(
                selectedAsset = null,
                searchQuery = "",
                suggestions = emptyList(),
                showSuggestions = false,
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(searchQuery = query, showSuggestions = query.isNotBlank())
        }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(suggestions = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            try {
                val results = searchAssetsUseCase(query)
                _uiState.update { it.copy(suggestions = results) }
            } catch (_: Exception) {
                _uiState.update { it.copy(suggestions = emptyList()) }
            }
        }
    }

    fun onAmountChanged(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onPriceChanged(price: String) {
        _uiState.update { it.copy(price = price) }
    }

    fun onQuantityChanged(quantity: String) {
        _uiState.update { it.copy(quantity = quantity) }
    }

    fun onDateChanged(date: String) {
        _uiState.update { it.copy(date = date) }
    }

    fun computeFee(): String {
        val state = _uiState.value
        val amt = state.amount.toDoubleOrNull() ?: return ""
        val price = state.price.toDoubleOrNull() ?: return ""
        val qty = state.quantity.toDoubleOrNull() ?: return ""
        if (amt <= 0 || price <= 0 || qty <= 0) return ""
        val fee = amt - price * qty
        return if (fee >= 0) fee.formatTwoDecimals() else ""
    }

    suspend fun saveTransaction(): Boolean {
        val state = _uiState.value
        val asset = state.selectedAsset ?: return false

        val timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val id = buildString {
            append(timestamp)
            append('-')
            repeat(8) { append(('a'..'z').random()) }
        }

        return try {
            if (state.type == TransactionType.DIVIDEND) {
                val amount = state.amount.toDoubleOrNull() ?: return false
                if (amount <= 0) return false
                val transaction = Transaction(
                    id = id,
                    assetId = asset.unifiedCode.ifBlank { asset.code },
                    type = TransactionType.DIVIDEND,
                    quantity = 0.0,
                    price = 0.0,
                    fee = 0.0,
                    timestamp = timestamp,
                )
                addTransactionUseCase(transaction, asset)
            } else {
                val amount = state.amount.toDoubleOrNull() ?: return false
                if (amount <= 0) return false
                val price = state.price.toDoubleOrNull() ?: 0.0
                val qty = state.quantity.toDoubleOrNull() ?: 0.0
                val fee = computeFee().toDoubleOrNull() ?: 0.0
                val transaction = Transaction(
                    id = id,
                    assetId = asset.unifiedCode.ifBlank { asset.code },
                    type = state.type,
                    quantity = qty,
                    price = price,
                    fee = fee,
                    timestamp = timestamp,
                )
                addTransactionUseCase(transaction, asset)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun todayDateString(): String {
        val now = kotlin.time.Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today.toString()
    }
}

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.BUY,
    val selectedAsset: Asset? = null,
    val searchQuery: String = "",
    val suggestions: List<Asset> = emptyList(),
    val showSuggestions: Boolean = false,
    val showSearch: Boolean = false,
    val holdings: List<HoldingDisplay> = emptyList(),
    val amount: String = "",
    val price: String = "",
    val quantity: String = "",
    val date: String = "",
    val error: String? = null,
)
