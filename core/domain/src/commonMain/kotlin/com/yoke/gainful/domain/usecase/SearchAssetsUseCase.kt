package com.yoke.gainful.domain.usecase

import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.model.SearchResult

class SearchAssetsUseCase(
    private val marketRepository: MarketRepository,
) {
    suspend operator fun invoke(query: String): List<SearchResult> {
        return marketRepository.search(query)
    }
}
