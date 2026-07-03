package com.yoke.gainful.data.repository

import com.yoke.gainful.database.dao.PnlCacheDao
import com.yoke.gainful.database.model.PnlCacheEntity
import kotlin.time.Clock

class OfflinePnlCacheRepository(
    private val dao: PnlCacheDao,
) : PnlCacheRepository {
    override suspend fun getDailyPnl(startDate: String, endDate: String): Map<String, Double> {
        return dao.getByDateRange(startDate, endDate).associate { it.date to it.pnl }
    }

    override suspend fun saveDailyPnl(dailyPnl: Map<String, Double>) {
        if (dailyPnl.isEmpty()) return
        val now = Clock.System.now().toEpochMilliseconds()
        dao.insertAll(
            dailyPnl.map { (date, pnl) ->
                PnlCacheEntity(
                    date = date,
                    pnl = pnl,
                    lastUpdated = now,
                )
            },
        )
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}
