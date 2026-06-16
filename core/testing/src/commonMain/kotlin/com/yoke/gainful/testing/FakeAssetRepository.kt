package com.yoke.gainful.testing

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.AssetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAssetRepository : AssetRepository {
    private val assets = MutableStateFlow<List<Asset>>(emptyList())

    override fun getAssets(): Flow<List<Asset>> = assets

    override suspend fun getAssetById(id: String): Asset? {
        return assets.value.find { it.id == id }
    }

    override suspend fun searchAssets(query: String): List<Asset> {
        return assets.value.filter {
            it.symbol.contains(query, ignoreCase = true) ||
                it.name.contains(query, ignoreCase = true)
        }
    }

    override suspend fun insertAsset(asset: Asset) {
        assets.value = assets.value + asset
    }

    override suspend fun deleteAsset(id: String) {
        assets.value = assets.value.filter { it.id != id }
    }
}
