package com.yoke.gainful.data.repository

data class SyncQueueItem(
    val id: Int,
    val entityType: String,
    val entityId: String,
    val operation: String,
    val createdAt: Long,
)

interface SyncQueueRepository {
    suspend fun getAll(): List<SyncQueueItem>

    suspend fun enqueue(entityType: String, entityId: String, operation: String)

    suspend fun remove(id: Int)

    suspend fun removeByEntityId(entityType: String, entityId: String)

    suspend fun deleteAll()
}
