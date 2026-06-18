package com.yoke.gainful.feature.transactions.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.transaction.DeleteTransactionUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsWithAssetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.yoke.gainful.model.TransactionType

class TransactionsViewModel(
    private val getTransactionsWithAssetsUseCase: GetTransactionsWithAssetsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            getTransactionsWithAssetsUseCase()
                .map { list ->
                    list.map { item ->
                        val tx = item.transaction
                        TransactionItem(
                            id = tx.id,
                            assetId = tx.assetId,
                            code = item.code,
                            name = item.name,
                            pinYin = item.pinYin,
                            type = tx.type,
                            price = tx.price,
                            quantity = tx.quantity,
                            fee = tx.fee,
                            timestamp = tx.timestamp,
                        )
                    }
                }
                .collect { items ->
                    _uiState.value = TransactionsUiState(transactions = items)
                }
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }
}

data class TransactionsUiState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionItem> = emptyList(),
    val error: String? = null,
)

data class TransactionItem(
    val id: String,
    val assetId: String,
    val code: String,
    val name: String,
    val pinYin: String,
    val type: TransactionType,
    val price: Double,
    val quantity: Double,
    val fee: Double,
    val timestamp: Long,
) {
    val amount: Double get() = price * quantity
    val typeLabel: String get() = when (type) {
        TransactionType.BUY -> "买入"
        TransactionType.SELL -> "卖出"
        TransactionType.DIVIDEND -> "股息"
    }
}
