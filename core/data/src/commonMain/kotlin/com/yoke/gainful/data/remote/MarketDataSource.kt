package com.yoke.gainful.data.remote

import com.yoke.gainful.model.Asset

interface MarketDataSource {
    suspend fun searchAssets(query: String): List<Asset>
    suspend fun getAssetPrice(assetId: String): Double?
}
