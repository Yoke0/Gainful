package com.yoke.gainful.feature.transactions.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.asset.InsertAssetUseCase
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
import kotlin.time.Clock
import kotlin.uuid.Uuid

class AddTransactionViewModel(
    private val searchAssetsUseCase: SearchAssetsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getHoldingsDisplayUseCase: GetHoldingsDisplayUseCase,
    private val insertAssetUseCase: InsertAssetUseCase,
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
        viewModelScope.launch {
            insertAssetUseCase(asset)
        }
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
        validateFields()
    }

    fun onPriceChanged(price: String) {
        _uiState.update { it.copy(price = price) }
        validateFields()
    }

    fun onQuantityChanged(quantity: String) {
        _uiState.update { it.copy(quantity = quantity) }
        validateFields()
    }

    private fun validateFields() {
        val state = _uiState.value
        if (state.type == TransactionType.DIVIDEND) {
            val amount = state.amount.toDoubleOrNull()
            _uiState.update {
                it.copy(fieldError = if (state.amount.isNotBlank() && (amount == null || amount <= 0)) FieldError.AMOUNT else null)
            }
            return
        }
        val amount = state.amount.toDoubleOrNull()
        val price = state.price.toDoubleOrNull()
        val qty = state.quantity.toDoubleOrNull()

        val error = when {
            state.amount.isNotBlank() && (amount == null || amount <= 0) -> FieldError.AMOUNT
            state.price.isNotBlank() && (price == null || price <= 0) -> FieldError.PRICE
            state.quantity.isNotBlank() && (qty == null || qty <= 0 || qty != qty.toLong().toDouble()) -> FieldError.QUANTITY
            amount != null && price != null && qty != null && amount > 0 && price > 0 && qty > 0 -> {
                val marketValue = price * qty
                val fee = when (state.type) {
                    TransactionType.BUY -> amount - marketValue
                    TransactionType.SELL -> marketValue - amount
                }
                if (fee < 0) FieldError.FEE else null
            }
            else -> null
        }
        _uiState.update { it.copy(fieldError = error) }
    }

    fun onDateChanged(date: String) {
        _uiState.update { it.copy(date = date) }
    }

    fun showCalendar() {
        _uiState.update { it.copy(showCalendar = true) }
    }

    fun hideCalendar() {
        _uiState.update { it.copy(showCalendar = false) }
    }

    fun computeFee(): String {
        val state = _uiState.value
        val amt = state.amount.toDoubleOrNull() ?: return ""
        val price = state.price.toDoubleOrNull() ?: return ""
        val qty = state.quantity.toDoubleOrNull() ?: return ""
        if (amt <= 0 || price <= 0 || qty <= 0) return ""
        val marketValue = price * qty
        val fee = when (state.type) {
            TransactionType.BUY -> amt - marketValue
            TransactionType.SELL -> marketValue - amt
            TransactionType.DIVIDEND -> 0.0
        }
        return fee.formatTwoDecimals()
    }

    suspend fun saveTransaction(): Boolean {
        validateFields()
        val state = _uiState.value
        if (state.fieldError != null) return false

        val asset = state.selectedAsset ?: return false
        val tradeDateMs = state.date.toTradeDateMs()

        if (state.type == TransactionType.DIVIDEND) {
            val amount = state.amount.toDoubleOrNull() ?: return false
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val id = Uuid.random().toString()
            val transaction = Transaction(
                id = id,
                assetId = asset.unifiedCode.ifBlank { asset.code },
                type = TransactionType.DIVIDEND,
                quantity = 0.0,
                price = 0.0,
                amount = amount,
                tradeDate = tradeDateMs,
                timestamp = timestamp,
            )
            addTransactionUseCase(transaction)
            return true
        }

        val amount = state.amount.toDoubleOrNull() ?: return false
        val price = state.price.toDoubleOrNull() ?: return false
        val qty = state.quantity.toDoubleOrNull() ?: return false

        val timestamp = Clock.System.now().toEpochMilliseconds()
        val id = Uuid.random().toString()
        val transaction = Transaction(
            id = id,
            assetId = asset.unifiedCode.ifBlank { asset.code },
            type = state.type,
            quantity = qty,
            price = price,
            amount = amount,
            tradeDate = tradeDateMs,
            timestamp = timestamp,
        )
        addTransactionUseCase(transaction)
        return true
    }

    private fun String.toTradeDateMs(): Long {
        return try {
            val parts = split("-")
            val date = kotlinx.datetime.LocalDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            val epochDays = date.toEpochDays()
            epochDays * 86_400_000L
        } catch (_: Exception) {
            Clock.System.now().toEpochMilliseconds()
        }
    }

    private fun todayDateString(): String {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today.toString()
    }
}

enum class FieldError {
    AMOUNT, PRICE, QUANTITY, FEE
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
    val fieldError: FieldError? = null,
    val showCalendar: Boolean = false,
) {
    val amountError: Boolean get() = fieldError == FieldError.AMOUNT
    val priceError: Boolean get() = fieldError == FieldError.PRICE
    val quantityError: Boolean get() = fieldError == FieldError.QUANTITY
    val feeError: Boolean get() = fieldError == FieldError.FEE
    val canSave: Boolean get() = when {
        selectedAsset == null -> false
        type == TransactionType.DIVIDEND -> amount.isNotBlank() && fieldError == null
        else -> amount.isNotBlank() && price.isNotBlank() && quantity.isNotBlank() && fieldError == null
    }
}
