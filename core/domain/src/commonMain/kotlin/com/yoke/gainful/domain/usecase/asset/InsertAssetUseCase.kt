package com.yoke.gainful.domain.usecase.asset

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.model.Asset

class InsertAssetUseCase(
    private val assetRepository: AssetRepository,
) {
    suspend operator fun invoke(asset: Asset) {
        assetRepository.insertAsset(asset)
    }
}
