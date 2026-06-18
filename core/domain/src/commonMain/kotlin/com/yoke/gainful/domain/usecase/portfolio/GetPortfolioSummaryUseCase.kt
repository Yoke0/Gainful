package com.yoke.gainful.domain.usecase.portfolio

import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.PortfolioSummary
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPortfolioSummaryUseCase(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(): Flow<PortfolioSummary> {
        return transactionRepository.getTransactions().map { transactions ->
            var totalCost = 0.0
            var totalValue = 0.0

            transactions.groupBy { it.assetId }.forEach { (_, assetTransactions) ->
                var quantity = 0.0
                var avgCost = 0.0

                assetTransactions.sortedBy { it.timestamp }.forEach { tx ->
                    when (tx.type) {
                        TransactionType.BUY -> {
                            val totalQty = quantity + tx.quantity
                            avgCost = if (totalQty > 0) {
                                (avgCost * quantity + tx.price * tx.quantity) / totalQty
                            } else 0.0
                            quantity = totalQty
                            totalCost += tx.price * tx.quantity + tx.fee
                        }
                        TransactionType.SELL -> {
                            quantity -= tx.quantity
                            totalValue += tx.price * tx.quantity - tx.fee
                        }
                        else -> {}
                    }
                }

                totalValue += avgCost * maxOf(quantity, 0.0)
            }

            val totalGain = totalValue - totalCost
            val totalGainPercent = if (totalCost > 0) (totalGain / totalCost) * 100 else 0.0

            PortfolioSummary(
                totalValue = totalValue,
                totalCost = totalCost,
                totalGain = totalGain,
                totalGainPercent = totalGainPercent,
            )
        }
    }
}
