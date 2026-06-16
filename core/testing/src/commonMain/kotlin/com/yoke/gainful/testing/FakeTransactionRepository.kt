package com.yoke.gainful.testing

import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTransactionRepository : TransactionRepository {
    private val transactions = MutableStateFlow<List<Transaction>>(emptyList())

    override fun getTransactions(): Flow<List<Transaction>> = transactions

    override fun getTransactionsByAssetId(assetId: String): Flow<List<Transaction>> {
        return MutableStateFlow(transactions.value.filter { it.assetId == assetId })
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return transactions.value.find { it.id == id }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactions.value = transactions.value + transaction
    }

    override suspend fun deleteTransaction(id: String) {
        transactions.value = transactions.value.filter { it.id != id }
    }
}
