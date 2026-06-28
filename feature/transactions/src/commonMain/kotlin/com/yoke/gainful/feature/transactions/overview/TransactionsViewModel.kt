package com.yoke.gainful.feature.transactions.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.transaction.DeleteTransactionUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsWithAssetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.yoke.gainful.model.TransactionType

class TransactionsViewModel(
    private val getTransactionsWithAssetsUseCase: GetTransactionsWithAssetsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        onIntent(TransactionsIntent.LoadTransactions)
    }

    fun onIntent(intent: TransactionsIntent) {
        when (intent) {
            is TransactionsIntent.LoadTransactions -> loadTransactions()
            is TransactionsIntent.DeleteTransaction -> deleteTransaction(intent.id)
            is TransactionsIntent.SetFilter -> _uiState.update { it.copy(filterType = intent.type) }
        }
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
                            amount = tx.amount,
                            fee = tx.fee,
                            tradeDate = tx.tradeDate,
                            timestamp = tx.timestamp,
                        )
                    }
                }
                .collect { items ->
                    _uiState.value = TransactionsUiState(transactions = items)
                }
        }
    }

    private fun deleteTransaction(id: String) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }
}

data class TransactionsUiState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionItem> = emptyList(),
    val filterType: TransactionType? = null,
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
    val amount: Double,
    val fee: Double,
    val tradeDate: Long,
    val timestamp: Long,
) {
    val displayAmount: Double get() = when (type) {
        TransactionType.BUY -> amount
        TransactionType.SELL -> amount
        TransactionType.DIVIDEND -> amount
    }
}
