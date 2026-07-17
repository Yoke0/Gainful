package com.yoke.gainful.data.repository

import com.yoke.gainful.data.model.toDomain
import com.yoke.gainful.data.model.toEntity
import com.yoke.gainful.database.dao.TransactionDao
import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineTransactionRepository(
    private val dao: TransactionDao,
) : TransactionRepository {
    override fun getTransactions(): Flow<List<Transaction>> {
        return dao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTransactionsByAssetId(assetId: String): Flow<List<Transaction>> {
        return dao.getByAssetId(assetId).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getAllTransactions(): List<Transaction> {
        return dao.getAllList().map { it.toDomain() }
    }

    override suspend fun getTransactionsByDateRange(startDate: Long, endDate: Long): List<Transaction> {
        return dao.getAllByDateRange(startDate, endDate).map { it.toDomain() }
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        dao.insert(transaction.toEntity())
    }

    override suspend fun insertTransactions(transactions: List<Transaction>) {
        dao.insertAll(transactions.map { it.toEntity() })
    }

    override suspend fun deleteTransaction(id: String) {
        dao.deleteById(id)
    }

    override suspend fun deleteByIds(ids: List<String>) {
        dao.deleteByIds(ids)
    }

    override suspend fun mergeServerTransactions(transactions: List<Transaction>) {
        for (tx in transactions) {
            val local = dao.getById(tx.id)
            if (local == null) {
                dao.insert(tx.toEntity())
            } else if (tx.updatedAt >= local.updatedAt) {
                dao.insert(tx.toEntity())
            }
        }
    }

    override suspend fun updateId(oldId: String, newId: String) {
        dao.updateId(oldId, newId)
    }
}
