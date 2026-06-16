package com.yoke.gainful.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yoke.gainful.database.model.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets")
    fun getAll(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE inner_code = :innerCode")
    suspend fun getByInnerCode(innerCode: String): AssetEntity?

    @Query("SELECT * FROM assets WHERE code LIKE '%' || :keyword || '%' OR name LIKE '%' || :keyword || '%' OR pinYin LIKE '%' || :keyword || '%'")
    fun search(keyword: String): Flow<List<AssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<AssetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: AssetEntity)
}
