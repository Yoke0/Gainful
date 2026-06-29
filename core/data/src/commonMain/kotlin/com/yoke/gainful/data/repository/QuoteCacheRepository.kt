package com.yoke.gainful.data.repository

import com.yoke.gainful.model.QuoteSnapshot
import kotlinx.coroutines.flow.Flow

interface QuoteCacheRepository {
    suspend fun getByQuoteIds(quoteIds: List<String>): List<QuoteSnapshot>

    fun getByQuoteIdsFlow(quoteIds: List<String>): Flow<List<QuoteSnapshot>>

    suspend fun upsert(snapshot: QuoteSnapshot)

    suspend fun upsertAll(snapshots: List<QuoteSnapshot>)
}
