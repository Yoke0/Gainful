package com.yoke.gainful.domain.usecase

import com.yoke.gainful.data.repository.HoldingRepository
import com.yoke.gainful.model.PortfolioSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPortfolioSummaryUseCase(
    private val holdingRepository: HoldingRepository,
) {
    operator fun invoke(): Flow<PortfolioSummary> {
        return holdingRepository.getHoldings().map { holdings ->
            val totalCost = holdings.sumOf { it.averageCost * it.quantity }
            val totalValue = holdings.sumOf { it.averageCost * it.quantity }
            val totalGain = totalValue - totalCost
            val totalGainPercent = if (totalCost > 0) (totalGain / totalCost) * 100 else 0.0

            PortfolioSummary(
                totalValue = totalValue,
                totalCost = totalCost,
                totalGain = totalGain,
                totalGainPercent = totalGainPercent,
                currency = "USD",
            )
        }
    }
}
