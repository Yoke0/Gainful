package com.yoke.gainful.domain.usecase

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.ChartPeriod
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.KLinePeriod
import com.yoke.gainful.model.StockQuote
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.flow.firstOrNull

class GetStockDetailUseCase(
    private val assetRepository: AssetRepository,
    private val marketRepository: MarketRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(stockCode: String): StockDetailResult {
        val assets = assetRepository.getAssets().firstOrNull() ?: emptyList()
        val asset = assets.find {
            it.code == stockCode || it.unifiedCode == stockCode || it.innerCode == stockCode
        }

        val quote = if (asset?.quoteId != null) {
            try {
                marketRepository.getQuote(asset.quoteId)
            } catch (_: Exception) {
                null
            }
        } else null

        val transactions = transactionRepository.getTransactions().firstOrNull() ?: emptyList()
        val assetTransactions = transactions.filter {
            it.assetId == stockCode || it.assetId == asset?.unifiedCode || it.assetId == asset?.innerCode
        }

        var quantity = 0.0
        var totalCost = 0.0
        var avgCost = 0.0

        assetTransactions.sortedBy { it.timestamp }.forEach { tx ->
            when (tx.type) {
                TransactionType.BUY -> {
                    totalCost += tx.price * tx.quantity + tx.fee
                    quantity += tx.quantity
                }
                TransactionType.SELL -> {
                    avgCost = if (quantity > 0) totalCost / quantity else 0.0
                    totalCost -= avgCost * tx.quantity
                    quantity -= tx.quantity
                }
                else -> {}
            }
        }

        if (quantity > 0) {
            avgCost = totalCost / quantity
        }

        val kLines = if (asset?.quoteId != null) {
            try {
                marketRepository.getKLines(asset.quoteId, KLinePeriod.DAILY, limit = 30)
            } catch (_: Exception) {
                emptyList()
            }
        } else emptyList()

        return StockDetailResult(
            code = stockCode,
            name = asset?.name ?: stockCode,
            quoteId = asset?.quoteId,
            quote = quote,
            quantity = quantity,
            averageCost = avgCost,
            transactions = assetTransactions.sortedByDescending { it.timestamp },
            kLines = kLines,
        )
    }

    suspend fun fetchKLines(quoteId: String, period: ChartPeriod): List<KLine> {
        if (period.isTrends) return emptyList()
        val klt = period.klt ?: return emptyList()
        val kLinePeriod = when (klt) {
            1 -> KLinePeriod.MIN_1
            5 -> KLinePeriod.MIN_5
            15 -> KLinePeriod.MIN_15
            30 -> KLinePeriod.MIN_30
            60 -> KLinePeriod.MIN_60
            101 -> KLinePeriod.DAILY
            102 -> KLinePeriod.WEEKLY
            103 -> KLinePeriod.MONTHLY
            else -> KLinePeriod.DAILY
        }
        return try {
            marketRepository.getKLines(quoteId, kLinePeriod, limit = 120)
        } catch (_: Exception) {
            emptyList()
        }
    }
}

data class StockDetailResult(
    val code: String,
    val name: String,
    val quoteId: String?,
    val quote: StockQuote?,
    val quantity: Double,
    val averageCost: Double,
    val transactions: List<Transaction>,
    val kLines: List<KLine>,
)
