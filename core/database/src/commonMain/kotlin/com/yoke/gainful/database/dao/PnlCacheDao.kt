package com.yoke.gainful.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yoke.gainful.database.model.PnlCacheEntity

@Dao
interface PnlCacheDao {
    @Query("SELECT * FROM pnl_cache WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getByDateRange(startDate: String, endDate: String): List<PnlCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<PnlCacheEntity>)

    @Query("DELETE FROM pnl_cache")
    suspend fun deleteAll()
}
