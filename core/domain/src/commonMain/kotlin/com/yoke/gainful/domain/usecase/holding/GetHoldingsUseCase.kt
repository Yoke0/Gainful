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
                    var totalCost = 0.0

                    assetTransactions.sortedBy { it.timestamp }.forEach { tx ->
                        when (tx.type) {
                            TransactionType.BUY -> {
                                totalCost += tx.price * tx.quantity + tx.fee
                                quantity += tx.quantity
                            }
                            TransactionType.SELL -> {
                                val avgCost = if (quantity > 0) totalCost / quantity else 0.0
                                totalCost -= avgCost * tx.quantity
                                quantity -= tx.quantity
                            }
                            else -> {}
                        }
                    }

                    if (quantity > 0) {
                        val avgCost = if (quantity > 0) totalCost / quantity else 0.0
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
