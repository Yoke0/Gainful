package com.yoke.gainful.testing

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.model.Asset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAssetRepository : AssetRepository {
    private val assets = MutableStateFlow<List<Asset>>(emptyList())

    override fun getAssets(): Flow<List<Asset>> = assets

    override suspend fun getAssetByInnerCode(innerCode: String): Asset? {
        return assets.value.find { it.innerCode == innerCode }
    }

    override fun searchAssets(keyword: String): Flow<List<Asset>> {
        return MutableStateFlow(
            assets.value.filter {
                it.code.contains(keyword, ignoreCase = true) ||
                    it.name.contains(keyword, ignoreCase = true) ||
                    it.pinYin.contains(keyword, ignoreCase = true)
            }
        )
    }

    override suspend fun insertAssets(assets: List<Asset>) {
        this.assets.value = this.assets.value + assets
    }

    override suspend fun insertAsset(asset: Asset) {
        assets.value = assets.value + asset
    }
}
