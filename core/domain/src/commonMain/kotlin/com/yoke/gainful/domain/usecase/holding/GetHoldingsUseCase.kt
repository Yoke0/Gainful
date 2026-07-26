package com.yoke.gainful.domain.usecase.holding

import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.Holding
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetHoldingsUseCase(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(): Flow<List<Holding>> {
        return transactionRepository.getTransactions().map { transactions ->
            transactions
                .groupBy { it.assetId }
                .mapNotNull { (assetId, assetTransactions) ->
                    var quantity = 0.0
                    var totalBuys = 0.0
                    var totalSells = 0.0
                    var totalDividends = 0.0

                    assetTransactions.sortedBy { it.timestamp }.forEach { tx ->
                        when (tx.type) {
                            TransactionType.BUY -> {
                                totalBuys += tx.amount
                                quantity += tx.quantity
                            }

                            TransactionType.SELL -> {
                                totalSells += tx.amount
                                quantity -= tx.quantity
                            }

                            TransactionType.DIVIDEND -> {
                                totalDividends += tx.amount
                            }
                        }
                    }

                    if (quantity > 0) {
                        val avgCost = (totalBuys - totalDividends - totalSells) / quantity
                        Holding(
                            id = assetId,
                            assetId = assetId,
                            quantity = quantity,
                            averageCost = avgCost,
                        )
                    } else {
                        null
                    }
                }
        }
    }
}
