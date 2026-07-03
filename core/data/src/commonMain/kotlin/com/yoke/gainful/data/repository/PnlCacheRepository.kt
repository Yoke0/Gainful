package com.yoke.gainful.data.repository

interface PnlCacheRepository {
    suspend fun getDailyPnl(startDate: String, endDate: String): Map<String, Double>

    suspend fun saveDailyPnl(dailyPnl: Map<String, Double>)

    suspend fun clear()
}
