package com.yoke.gainful.data.repository

import com.yoke.gainful.data.model.toDomain
import com.yoke.gainful.data.model.toEntity
import com.yoke.gainful.database.dao.AssetDao
import com.yoke.gainful.model.Asset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineAssetRepository(
    private val dao: AssetDao,
) : AssetRepository {

    override fun getAssets(): Flow<List<Asset>> {
        return dao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getAssetByInnerCode(innerCode: String): Asset? {
        return dao.getByInnerCode(innerCode)?.toDomain()
    }

    override suspend fun searchAssets(keyword: String): List<Asset> {
        return dao.search(keyword).map { it.toDomain() }
    }

    override suspend fun insertAssets(assets: List<Asset>) {
        dao.insertAll(assets.map { it.toEntity() })
    }

    override suspend fun insertAsset(asset: Asset) {
        dao.insert(asset.toEntity())
    }
}
