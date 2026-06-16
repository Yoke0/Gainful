package com.yoke.gainful.data.repository

import com.yoke.gainful.model.Asset
import kotlinx.coroutines.flow.Flow

interface AssetRepository {
    fun getAssets(): Flow<List<Asset>>
    suspend fun getAssetById(id: String): Asset?
    suspend fun searchAssets(query: String): List<Asset>
    suspend fun insertAsset(asset: Asset)
    suspend fun deleteAsset(id: String)
}
