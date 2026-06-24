package com.yoke.gainful.data.repository

import com.yoke.gainful.model.Asset
import kotlinx.coroutines.flow.Flow

interface AssetRepository {
    fun getAssets(): Flow<List<Asset>>
    suspend fun getAssetByInnerCode(innerCode: String): Asset?
    suspend fun searchAssets(keyword: String): List<Asset>
    suspend fun insertAssets(assets: List<Asset>)
    suspend fun insertAsset(asset: Asset)
}
