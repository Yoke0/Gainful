package com.yoke.gainful.feature.transactions.overview

import com.yoke.gainful.model.TransactionType

sealed interface TransactionsIntent {
    data object LoadTransactions : TransactionsIntent

    data class DeleteTransaction(val id: String) : TransactionsIntent

    data class SetFilter(val type: TransactionType?) : TransactionsIntent
}
