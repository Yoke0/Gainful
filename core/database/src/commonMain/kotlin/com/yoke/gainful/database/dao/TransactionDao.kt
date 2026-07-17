package com.yoke.gainful.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yoke.gainful.database.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY trade_date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE asset_id = :assetId ORDER BY trade_date DESC")
    fun getByAssetId(assetId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY trade_date DESC")
    suspend fun getAllList(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE trade_date BETWEEN :startDate AND :endDate ORDER BY trade_date DESC")
    suspend fun getAllByDateRange(startDate: Long, endDate: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM transactions WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("UPDATE transactions SET id = :newId WHERE id = :oldId")
    suspend fun updateId(oldId: String, newId: String)
}
