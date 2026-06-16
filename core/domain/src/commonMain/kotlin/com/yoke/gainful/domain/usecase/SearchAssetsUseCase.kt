package com.yoke.gainful.domain.usecase

import com.yoke.gainful.data.remote.MarketDataSource
import com.yoke.gainful.model.Asset

class SearchAssetsUseCase(
    private val marketDataSource: MarketDataSource,
) {
    suspend operator fun invoke(query: String): List<Asset> {
        return marketDataSource.searchAssets(query)
    }
}
