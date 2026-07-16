package com.yoke.gainful.data.repository

import com.yoke.gainful.database.dao.SyncQueueDao
import com.yoke.gainful.database.model.SyncQueueEntity
import kotlin.time.Clock

class OfflineSyncQueueRepository(
    private val dao: SyncQueueDao,
) : SyncQueueRepository {
    override suspend fun getAll(): List<SyncQueueItem> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun enqueue(entityType: String, entityId: String, operation: String) {
        dao.insert(
            SyncQueueEntity(
                entityType = entityType,
                entityId = entityId,
                operation = operation,
                createdAt = Clock.System.now().toEpochMilliseconds(),
            ),
        )
    }

    override suspend fun remove(id: Int) {
        dao.deleteById(id)
    }

    override suspend fun removeByEntityId(entityType: String, entityId: String) {
        dao.deleteByEntityId(entityType, entityId)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    private fun SyncQueueEntity.toDomain() =
        SyncQueueItem(
            id = id,
            entityType = entityType,
            entityId = entityId,
            operation = operation,
            createdAt = createdAt,
        )
}
