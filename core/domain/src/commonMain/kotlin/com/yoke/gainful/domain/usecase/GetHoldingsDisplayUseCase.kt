package com.yoke.gainful.domain.usecase

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetHoldingsDisplayUseCase(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
    private val marketRepository: MarketRepository,
) {
    operator fun invoke(): Flow<List<HoldingDisplay>> {
        return combine(
            transactionRepository.getTransactions(),
            assetRepository.getAssets(),
        ) { transactions, assets ->
            val assetMap = assets.associateBy { it.unifiedCode.ifBlank { it.code } }

            val holdings = transactions
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
                        val asset = assetMap[assetId]
                        HoldingDisplay(
                            id = assetId,
                            assetId = assetId,
                            code = asset?.code ?: assetId,
                            name = asset?.name ?: assetId,
                            quantity = quantity,
                            averageCost = avgCost,
                            currentPrice = 0.0,
                            changePercent = 0.0,
                            changeAmount = 0.0,
                        )
                    } else {
                        null
                    }
                }

            val quoteIds = holdings.mapNotNull { holding ->
                val asset = assetMap[holding.assetId]
                asset?.quoteId
            }.distinct()

            val quotes = if (quoteIds.isNotEmpty()) {
                try {
                    marketRepository.getBatchQuotes(quoteIds)
                } catch (_: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            val quoteMap = quotes.associateBy { it.code }

            val assetQuoteMap = holdings.associate { holding ->
                val asset = assetMap[holding.assetId]
                holding.id to asset?.quoteId
            }

            val trendDataMap = mutableMapOf<String, List<Double>>()
            for (holding in holdings) {
                val qid = assetQuoteMap[holding.id] ?: continue
                try {
                    val trends = marketRepository.getTrends(qid, ndays = 1)
                    if (trends.isNotEmpty()) {
                        trendDataMap[holding.id] = trends.map { it.price }
                    }
                } catch (_: Exception) {
                }
            }

            holdings.map { holding ->
                val quote = quoteMap[holding.code]
                if (quote != null) {
                    holding.copy(
                        currentPrice = quote.latestPrice,
                        changePercent = quote.changePercent,
                        changeAmount = quote.changeAmount,
                        trendPrices = trendDataMap[holding.id] ?: emptyList(),
                    )
                } else {
                    holding
                }
            }
        }
    }
}
