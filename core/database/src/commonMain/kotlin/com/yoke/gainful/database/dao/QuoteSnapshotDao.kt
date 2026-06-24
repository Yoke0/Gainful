package com.yoke.gainful.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yoke.gainful.database.model.QuoteSnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteSnapshotDao {
    @Query("SELECT * FROM quote_snapshots WHERE quote_id IN (:quoteIds)")
    suspend fun getByQuoteIds(quoteIds: List<String>): List<QuoteSnapshotEntity>

    @Query("SELECT * FROM quote_snapshots WHERE quote_id IN (:quoteIds)")
    fun getByQuoteIdsFlow(quoteIds: List<String>): Flow<List<QuoteSnapshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(snapshot: QuoteSnapshotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(snapshots: List<QuoteSnapshotEntity>)
}
