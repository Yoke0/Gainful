package com.yoke.gainful.domain.usecase

import com.yoke.gainful.data.repository.HoldingRepository
import com.yoke.gainful.model.Holding
import kotlinx.coroutines.flow.Flow

class GetHoldingsUseCase(
    private val holdingRepository: HoldingRepository,
) {
    operator fun invoke(): Flow<List<Holding>> {
        return holdingRepository.getHoldings()
    }
}
