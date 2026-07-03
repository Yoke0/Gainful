package com.yoke.gainful.sync

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.KLineCacheRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.domain.usecase.dashboard.ComputePnlUseCase
import com.yoke.gainful.model.KLinePeriod
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class KLineFetchService(
    private val assetRepository: AssetRepository,
    private val marketRepository: MarketRepository,
    private val transactionRepository: TransactionRepository,
    private val kLineCacheRepository: KLineCacheRepository,
    private val computePnlUseCase: ComputePnlUseCase,
) {
    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    @OptIn(FormatStringsInDatetimeFormats::class)
    private fun formatGuidId(date: LocalDate): String = date.format(LocalDate.Format { byUnicodePattern("yyyyMMdd") })

    private fun Instant.toLocalDate(): LocalDate =
        this.toLocalDateTime(TimeZone.currentSystemDefault()).date

    private data class AssetInfo(
        val assetId: String,
        val firstBuyDate: LocalDate,
        val endDate: LocalDate?,
    )

    private fun computeAssetInfos(transactions: List<Transaction>): Map<String, AssetInfo> {
        val sorted = transactions.sortedBy { it.tradeDate }
        val quantities = mutableMapOf<String, Double>()
        val firstBuyDates = mutableMapOf<String, LocalDate>()
        val lastSellDates = mutableMapOf<String, LocalDate>()

        for (tx in sorted) {
            val date = Instant.fromEpochMilliseconds(tx.tradeDate).toLocalDate()
            when (tx.type) {
                TransactionType.BUY -> {
                    if (tx.assetId !in firstBuyDates) {
                        firstBuyDates[tx.assetId] = date
                    }
                    quantities[tx.assetId] = (quantities[tx.assetId] ?: 0.0) + tx.quantity
                }

                TransactionType.SELL -> {
                    quantities[tx.assetId] = (quantities[tx.assetId] ?: 0.0) - tx.quantity
                    lastSellDates[tx.assetId] = date
                }

                TransactionType.DIVIDEND -> {}
            }
        }

        val result = mutableMapOf<String, AssetInfo>()
        for (assetId in firstBuyDates.keys) {
            val qty = quantities[assetId] ?: 0.0
            val endDate = if (qty <= 0.0) lastSellDates[assetId] else null
            result[assetId] =
                AssetInfo(
                    assetId = assetId,
                    firstBuyDate = firstBuyDates[assetId]!!,
                    endDate = endDate,
                )
        }
        return result
    }

    suspend fun fetchAll(): Boolean =
        runCatching {
            val transactions = transactionRepository.getTransactions().first()
            if (transactions.isEmpty()) return@runCatching false

            val assets = assetRepository.getAssets().first()
            val assetsWithQuoteId = assets.filter { it.quoteId.isNotBlank() }
            val assetInfos = computeAssetInfos(transactions)

            coroutineScope {
                assetInfos.map { (assetId, info) ->
                    async {
                        val asset =
                            assetsWithQuoteId.firstOrNull {
                                it.unifiedCode.ifBlank { it.code } == assetId
                            } ?: return@async

                        val endDate = info.endDate ?: today()

                        val cached = kLineCacheRepository.getByAssetId(assetId)
                        val startDate =
                            if (cached.isNotEmpty()) {
                                LocalDate.parse(cached.first().date)
                            } else {
                                info.firstBuyDate
                            }

                        runCatching {
                            val klines =
                                marketRepository.getKLines(
                                    secId = asset.quoteId,
                                    period = KLinePeriod.DAILY,
                                    startDate = formatGuidId(startDate),
                                    endDate = formatGuidId(endDate),
                                )
                            if (klines.isNotEmpty()) {
                                kLineCacheRepository.insertAll(assetId, klines)
                            }
                        }
                    }
                }.forEach { it.await() }
            }

            computePnlUseCase()
            true
        }.getOrDefault(false)
}
