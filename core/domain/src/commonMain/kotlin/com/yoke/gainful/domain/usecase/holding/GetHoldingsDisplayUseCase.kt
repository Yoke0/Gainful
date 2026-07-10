package com.yoke.gainful.domain.usecase.holding

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.QuoteCacheRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.QuoteSnapshot
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Clock

class GetHoldingsDisplayUseCase(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
    private val marketRepository: MarketRepository,
    private val quoteCacheRepository: QuoteCacheRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<HoldingDisplay>> {
        return combine(
            transactionRepository.getTransactions(),
            assetRepository.getAssets(),
        ) { transactions, assets ->
            Pair(transactions, assets)
        }.flatMapLatest { (transactions, assets) ->
            val holdings = computeHoldings(transactions, assets)
            val quoteIds = resolveQuoteIds(holdings, assets)
            if (quoteIds.isNotEmpty()) {
                fetchMissingQuotes(quoteIds)
            }
            observeHoldingsWithQuotes(holdings, quoteIds, assets)
        }
    }

    private fun observeHoldingsWithQuotes(
        holdings: List<HoldingDisplay>,
        quoteIds: List<String>,
        assets: List<Asset>,
    ): Flow<List<HoldingDisplay>> {
        if (quoteIds.isEmpty()) {
            return flowOf(holdings.withEmptyQuotes())
        }

        return quoteCacheRepository.getByQuoteIdsFlow(quoteIds)
            .combine(flowOf(holdings)) { snapshots, hds ->
                mergeWithSnapshots(hds, snapshots, assets)
            }
    }

    private suspend fun fetchMissingQuotes(quoteIds: List<String>) {
        val cached = quoteCacheRepository.getByQuoteIds(quoteIds)
        val cachedIds = cached.map { it.quoteId }.toSet()
        val missingIds = quoteIds.filter { it !in cachedIds }

        for (quoteId in missingIds) {
            runCatching {
                val quote = marketRepository.getQuote(quoteId) ?: return@runCatching
                val trends = marketRepository.getTrends(quoteId)
                val snapshot =
                    QuoteSnapshot(
                        quoteId = quoteId,
                        code = quote.code,
                        name = quote.name,
                        quote = quote,
                        trends = trends,
                        lastUpdated = Clock.System.now().toEpochMilliseconds(),
                    )
                quoteCacheRepository.upsert(snapshot)
            }
        }
    }

    private fun mergeWithSnapshots(
        holdings: List<HoldingDisplay>,
        snapshots: List<QuoteSnapshot>,
        assets: List<Asset>,
    ): List<HoldingDisplay> {
        val snapshotMap = snapshots.associateBy { it.quoteId }
        val assetQuoteMap = buildAssetQuoteMap(holdings, assets)

        return holdings.map { hd ->
            val quoteId = assetQuoteMap[hd.id] ?: return@map hd
            val snapshot = snapshotMap[quoteId] ?: return@map hd
            hd.copy(
                currentPrice = snapshot.quote.latestPrice,
                preClose = snapshot.quote.preClose,
                changePercent = snapshot.quote.changePercent,
                changeAmount = snapshot.quote.changeAmount,
                trends = snapshot.trends,
            )
        }
    }

    private fun buildAssetQuoteMap(
        holdings: List<HoldingDisplay>,
        assets: List<Asset>,
    ): Map<String, String> {
        return holdings.associate { hd ->
            val asset = assets.firstOrNull { it.unifiedCode.ifBlank { it.code } == hd.assetId }
            hd.id to (asset?.quoteId ?: "")
        }
    }

    private fun List<HoldingDisplay>.withEmptyQuotes(): List<HoldingDisplay> {
        return map { it.copy(currentPrice = 0.0, changePercent = 0.0, changeAmount = 0.0, trends = emptyList()) }
    }

    private fun resolveQuoteIds(
        holdings: List<HoldingDisplay>,
        assets: List<Asset>,
    ): List<String> {
        return holdings.mapNotNull { hd ->
            val asset = assets.firstOrNull { it.unifiedCode.ifBlank { it.code } == hd.assetId }
            asset?.quoteId
        }.distinct()
    }

    private fun computeHoldings(
        transactions: List<Transaction>,
        assets: List<Asset>,
    ): List<HoldingDisplay> {
        val assetMap =
            assets
                .groupBy { it.unifiedCode.ifBlank { it.code } }
                .mapValues { (_, group) -> group.firstOrNull { it.quoteId.isNotBlank() } ?: group.first() }

        return transactions
            .groupBy { it.assetId }
            .mapNotNull { (assetId, txList) -> computeSingleHolding(assetId, txList, assetMap) }
    }

    private fun computeSingleHolding(
        assetId: String,
        transactions: List<Transaction>,
        assetMap: Map<String, Asset>,
    ): HoldingDisplay? {
        var quantity = 0.0
        var totalCost = 0.0
        var totalBuys = 0.0
        var totalSells = 0.0
        var totalDividends = 0.0

        transactions.sortedBy { it.timestamp }.forEach { tx ->
            when (tx.type) {
                TransactionType.BUY -> {
                    totalBuys += tx.amount
                    totalCost += tx.amount
                    quantity += tx.quantity
                }

                TransactionType.SELL -> {
                    totalSells += tx.amount
                    val avgCost = if (quantity > 0) totalCost / quantity else 0.0
                    totalCost -= avgCost * tx.quantity
                    quantity -= tx.quantity
                }

                TransactionType.DIVIDEND -> {
                    totalDividends += tx.amount
                    totalCost -= tx.amount
                }
            }
        }

        if (quantity <= 0) return null

        val asset = assetMap[assetId]
        return HoldingDisplay(
            id = assetId,
            assetId = assetId,
            code = asset?.code ?: assetId,
            name = asset?.name ?: assetId,
            pinYin = asset?.pinYin ?: "",
            quantity = quantity,
            averageCost = if (quantity > 0) totalCost / quantity else 0.0,
            currentPrice = 0.0,
            preClose = 0.0,
            changePercent = 0.0,
            changeAmount = 0.0,
            totalBuys = totalBuys,
            totalSells = totalSells,
            totalDividends = totalDividends,
        )
    }
}
