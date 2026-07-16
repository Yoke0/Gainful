package com.yoke.gainful.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yoke.gainful.database.model.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue ORDER BY created_at ASC")
    suspend fun getAll(): List<SyncQueueEntity>

    @Insert
    suspend fun insert(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM sync_queue WHERE entity_type = :entityType AND entity_id = :entityId AND operation = :operation")
    suspend fun deleteByEntity(entityType: String, entityId: String, operation: String)

    @Query("DELETE FROM sync_queue WHERE entity_type = :entityType AND entity_id = :entityId")
    suspend fun deleteByEntityId(entityType: String, entityId: String)

    @Query("DELETE FROM sync_queue")
    suspend fun deleteAll()
}
