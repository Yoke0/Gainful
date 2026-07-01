package com.yoke.gainful.data.repository

import com.yoke.gainful.database.dao.KLineCacheDao
import com.yoke.gainful.database.model.KLineCacheEntity
import com.yoke.gainful.model.KLine
import kotlin.time.Clock

class OfflineKLineCacheRepository(
    private val dao: KLineCacheDao,
) : KLineCacheRepository {
    override suspend fun getByAssetId(assetId: String): List<KLine> {
        return dao.getByAssetId(assetId).map { it.toDomain() }
    }

    override suspend fun getByAssetIdAndDate(assetId: String, date: String): KLine? {
        return dao.getByAssetIdAndDate(assetId, date)?.toDomain()
    }

    override suspend fun insertAll(assetId: String, klines: List<KLine>) {
        val now = Clock.System.now().toEpochMilliseconds()
        dao.insertAll(klines.map { it.toEntity(assetId, now) })
    }

    override suspend fun deleteByAssetId(assetId: String) {
        dao.deleteByAssetId(assetId)
    }

    private fun KLineCacheEntity.toDomain(): KLine {
        return KLine(
            date = date,
            open = open,
            close = close,
            high = high,
            low = low,
            volume = volume,
            turnover = turnover,
            amplitude = 0.0,
            changePercent = changePercent,
            changeAmount = changeAmount,
            turnoverRate = 0.0,
        )
    }

    private fun KLine.toEntity(assetId: String, lastUpdated: Long): KLineCacheEntity {
        return KLineCacheEntity(
            assetId = assetId,
            date = date,
            open = open,
            close = close,
            high = high,
            low = low,
            volume = volume,
            turnover = turnover,
            changePercent = changePercent,
            changeAmount = changeAmount,
            lastUpdated = lastUpdated,
        )
    }
}
