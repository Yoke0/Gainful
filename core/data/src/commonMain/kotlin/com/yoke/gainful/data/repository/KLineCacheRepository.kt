package com.yoke.gainful.data.repository

import com.yoke.gainful.model.KLine

interface KLineCacheRepository {
    suspend fun getByAssetId(assetId: String): List<KLine>

    suspend fun getByAssetIdAndDate(assetId: String, date: String): KLine?

    suspend fun insertAll(assetId: String, klines: List<KLine>)

    suspend fun deleteByAssetId(assetId: String)
}
