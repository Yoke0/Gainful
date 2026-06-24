package com.yoke.gainful.data.repository

import com.yoke.gainful.data.model.toDomain
import com.yoke.gainful.data.model.toEntity
import com.yoke.gainful.database.dao.QuoteSnapshotDao
import com.yoke.gainful.model.QuoteSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineQuoteCacheRepository(
    private val dao: QuoteSnapshotDao,
) : QuoteCacheRepository {

    override suspend fun getByQuoteIds(quoteIds: List<String>): List<QuoteSnapshot> {
        return dao.getByQuoteIds(quoteIds).map { it.toDomain() }
    }

    override fun getByQuoteIdsFlow(quoteIds: List<String>): Flow<List<QuoteSnapshot>> {
        return dao.getByQuoteIdsFlow(quoteIds).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun upsert(snapshot: QuoteSnapshot) {
        dao.upsert(snapshot.toEntity())
    }

    override suspend fun upsertAll(snapshots: List<QuoteSnapshot>) {
        dao.upsertAll(snapshots.map { it.toEntity() })
    }
}
