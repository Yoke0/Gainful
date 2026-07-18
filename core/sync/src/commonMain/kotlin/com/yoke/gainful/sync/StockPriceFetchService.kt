package com.yoke.gainful.sync

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.QuoteCacheRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.model.QuoteSnapshot
import com.yoke.gainful.widget.domain.GetTodayPnlUseCase
import com.yoke.gainful.widget.syncWidgetData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

class StockPriceFetchService(
    private val assetRepository: AssetRepository,
    private val marketRepository: MarketRepository,
    private val quoteCacheRepository: QuoteCacheRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend fun fetchAllHoldings() {
        val assets = runCatching { assetRepository.getAssets().first() }.getOrNull() ?: return
        val holdings = assets.filter { it.quoteId.isNotBlank() }

        val snapshots =
            coroutineScope {
                holdings.map { asset ->
                    async { fetchSingleQuote(asset.quoteId) }
                }.mapNotNull { it.await() }
            }

        if (snapshots.isNotEmpty()) {
            quoteCacheRepository.upsertAll(snapshots)
            syncWidgetDataIfNeeded()
        }
    }

    private suspend fun syncWidgetDataIfNeeded() {
        runCatching {
            val holdingsUseCase = GetHoldingsDisplayUseCase(transactionRepository, assetRepository, marketRepository, quoteCacheRepository)
            val useCase = GetTodayPnlUseCase(holdingsUseCase)
            val pnl = useCase.compute(title = "", noDataText = "")
            syncWidgetData(pnl)
        }
    }

    private suspend fun fetchSingleQuote(quoteId: String): QuoteSnapshot? =
        runCatching {
            coroutineScope {
                val quote = marketRepository.getQuote(quoteId) ?: return@coroutineScope null
                val trends = marketRepository.getTrends(quoteId)
                QuoteSnapshot(
                    quoteId = quoteId,
                    code = quote.code,
                    name = quote.name,
                    quote = quote,
                    trends = trends,
                    lastUpdated = Clock.System.now().toEpochMilliseconds(),
                )
            }
        }.getOrNull()
}
