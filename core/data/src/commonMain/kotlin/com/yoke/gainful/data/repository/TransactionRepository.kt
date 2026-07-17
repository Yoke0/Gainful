package com.yoke.gainful.data.repository

import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>

    fun getTransactionsByAssetId(assetId: String): Flow<List<Transaction>>

    suspend fun getAllTransactions(): List<Transaction>

    suspend fun getTransactionsByDateRange(startDate: Long, endDate: Long): List<Transaction>

    suspend fun getTransactionById(id: String): Transaction?

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun insertTransactions(transactions: List<Transaction>)

    suspend fun deleteTransaction(id: String)

    suspend fun deleteByIds(ids: List<String>)

    suspend fun mergeServerTransactions(transactions: List<Transaction>)

    suspend fun updateId(oldId: String, newId: String)
}
