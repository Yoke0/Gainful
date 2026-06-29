package com.yoke.gainful.domain.usecase.holding

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.ClosedPosition
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetClosedPositionsUseCase(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
) {
    operator fun invoke(): Flow<List<ClosedPosition>> {
        return combine(
            transactionRepository.getTransactions(),
            assetRepository.getAssets(),
        ) { transactions, assets ->
            computeClosedPositions(transactions, assets)
        }
    }

    private fun computeClosedPositions(
        transactions: List<Transaction>,
        assets: List<Asset>,
    ): List<ClosedPosition> {
        val assetMap =
            assets
                .groupBy { it.unifiedCode.ifBlank { it.code } }
                .mapValues { (_, group) -> group.first() }

        return transactions
            .groupBy { it.assetId }
            .mapNotNull { (assetId, txList) ->
                computeSingleClosedPosition(assetId, txList, assetMap)
            }
    }

    private fun computeSingleClosedPosition(
        assetId: String,
        transactions: List<Transaction>,
        assetMap: Map<String, Asset>,
    ): ClosedPosition? {
        var quantity = 0.0
        var totalBuys = 0.0
        var totalSells = 0.0
        var totalDividends = 0.0
        var lastSellPrice = 0.0
        var lastSellDate = 0L

        transactions.sortedBy { it.tradeDate }.forEach { tx ->
            when (tx.type) {
                TransactionType.BUY -> {
                    totalBuys += tx.amount
                    quantity += tx.quantity
                }

                TransactionType.SELL -> {
                    totalSells += tx.amount
                    quantity -= tx.quantity
                    lastSellPrice = tx.price
                    lastSellDate = tx.tradeDate
                }

                TransactionType.DIVIDEND -> {
                    totalDividends += tx.amount
                }
            }
        }

        if (quantity > 0 || totalSells == 0.0) return null

        val asset = assetMap[assetId]
        return ClosedPosition(
            assetId = assetId,
            code = asset?.code ?: assetId,
            name = asset?.name ?: assetId,
            pinYin = asset?.pinYin ?: "",
            lastSellPrice = lastSellPrice,
            lastSellDate = lastSellDate,
            totalBuys = totalBuys,
            totalSells = totalSells,
            totalDividends = totalDividends,
        )
    }
}
