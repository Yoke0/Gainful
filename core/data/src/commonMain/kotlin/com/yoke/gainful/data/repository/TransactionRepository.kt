package com.yoke.gainful.data.repository

import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
    fun getTransactionsByAssetId(assetId: String): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)
}
