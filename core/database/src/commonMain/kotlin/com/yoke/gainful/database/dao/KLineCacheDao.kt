package com.yoke.gainful.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yoke.gainful.database.model.KLineCacheEntity

@Dao
interface KLineCacheDao {
    @Query("SELECT * FROM kline_cache WHERE asset_id = :assetId ORDER BY date ASC")
    suspend fun getByAssetId(assetId: String): List<KLineCacheEntity>

    @Query("SELECT * FROM kline_cache WHERE asset_id = :assetId AND date = :date")
    suspend fun getByAssetIdAndDate(assetId: String, date: String): KLineCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<KLineCacheEntity>)

    @Query("DELETE FROM kline_cache WHERE asset_id = :assetId")
    suspend fun deleteByAssetId(assetId: String)

    @Query("DELETE FROM kline_cache WHERE last_updated < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
