package com.yoke.gainful.domain.usecase.dashboard

import com.yoke.gainful.data.repository.KLineCacheRepository
import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class ComputePnlUseCase(
    private val transactionRepository: TransactionRepository,
    private val kLineCacheRepository: KLineCacheRepository,
    private val pnlCacheRepository: PnlCacheRepository,
) {
    suspend operator fun invoke(): Boolean =
        runCatching {
            val transactions = transactionRepository.getTransactions().first()
            if (transactions.isEmpty()) return@runCatching false

            val klineData = fetchKlineData(transactions)
            val dailyPositions = computeDailyPositions(transactions)
            val dailyPnl = computeDailyPnl(transactions, klineData, dailyPositions)

            pnlCacheRepository.saveDailyPnl(dailyPnl.mapKeys { it.key.toString() })
            true
        }.getOrDefault(false)

    private suspend fun fetchKlineData(transactions: List<Transaction>): Map<String, List<KLine>> {
        val assetIds = transactions.map { it.assetId }.distinct()
        val klineData = mutableMapOf<String, List<KLine>>()
        for (assetId in assetIds) {
            klineData[assetId] = kLineCacheRepository.getByAssetId(assetId)
        }
        return klineData
    }

    private fun computeDailyPnl(
        transactions: List<Transaction>,
        klineData: Map<String, List<KLine>>,
        dailyPositions: Map<LocalDate, Map<String, Double>>,
    ): Map<LocalDate, Double> {
        val result = mutableMapOf<LocalDate, Double>()
        val dates = dailyPositions.keys.sorted()
        if (dates.isEmpty()) return result

        for (date in dates) {
            val pnl = calculateDayPnl(transactions, klineData, dailyPositions, date)
            if (pnl != null && pnl != 0.0) {
                result[date] = pnl
            }
        }
        return result
    }

    private fun computeDailyPositions(transactions: List<Transaction>): Map<LocalDate, Map<String, Double>> {
        if (transactions.isEmpty()) return emptyMap()

        val sortedTransactions = transactions.sortedBy { it.tradeDate }
        val positions = mutableMapOf<String, Double>()
        val startOfDayPositions = mutableMapOf<LocalDate, Map<String, Double>>()

        var currentDate: LocalDate? = null

        for (tx in sortedTransactions) {
            val txDate = tx.tradeDate.toLocalDate()

            if (currentDate != txDate) {
                if (currentDate != null) {
                    startOfDayPositions[txDate] = positions.toMap()
                } else {
                    startOfDayPositions[txDate] = emptyMap()
                }
                currentDate = txDate
            } else if (txDate !in startOfDayPositions) {
                startOfDayPositions[txDate] = positions.toMap()
            }

            when (tx.type) {
                TransactionType.BUY -> {
                    positions[tx.assetId] = (positions[tx.assetId] ?: 0.0) + tx.quantity
                }

                TransactionType.SELL -> {
                    positions[tx.assetId] = (positions[tx.assetId] ?: 0.0) - tx.quantity
                }

                TransactionType.DIVIDEND -> {}
            }
        }

        val firstDate = startOfDayPositions.keys.minOrNull() ?: return startOfDayPositions
        val today = today()
        var lastPosition = emptyMap<String, Double>()

        var currentDateIter = firstDate
        while (currentDateIter <= today) {
            if (startOfDayPositions.containsKey(currentDateIter)) {
                lastPosition = startOfDayPositions[currentDateIter]!!.toMutableMap()
                sortedTransactions
                    .filter { it.tradeDate.toLocalDate() == currentDateIter }
                    .forEach { tx ->
                        when (tx.type) {
                            TransactionType.BUY -> {
                                lastPosition[tx.assetId] = (lastPosition[tx.assetId] ?: 0.0) + tx.quantity
                            }

                            TransactionType.SELL -> {
                                lastPosition[tx.assetId] = (lastPosition[tx.assetId] ?: 0.0) - tx.quantity
                            }

                            TransactionType.DIVIDEND -> {}
                        }
                    }
            } else if (lastPosition.isNotEmpty()) {
                startOfDayPositions[currentDateIter] = lastPosition.toMap()
            }
            currentDateIter = currentDateIter.plus(1, DateTimeUnit.DAY)
        }

        return startOfDayPositions
    }

    private fun calculateDayPnl(
        transactions: List<Transaction>,
        klineData: Map<String, List<KLine>>,
        dailyPositions: Map<LocalDate, Map<String, Double>>,
        date: LocalDate,
    ): Double? {
        val positions = dailyPositions[date]
        val dayTransactions = transactions.filter { it.tradeDate.toLocalDate() == date }

        if (positions.isNullOrEmpty() && dayTransactions.isEmpty()) return null

        var totalPnl = 0.0

        // Position PnL: market movement on held shares
        if (positions != null) {
            for ((assetId, quantity) in positions) {
                if (quantity <= 0) continue

                val kline = klineData[assetId]?.find { it.date == date.toString() }
                if (kline == null) continue

                val sellQty =
                    dayTransactions
                        .filter { it.assetId == assetId && it.type == TransactionType.SELL }
                        .sumOf { it.quantity }

                val heldQuantity = quantity - sellQty
                if (heldQuantity > 0) {
                    totalPnl += kline.changeAmount * heldQuantity
                }
            }
        }

        // Trade PnL: execution quality
        for (tx in dayTransactions) {
            val kline = klineData[tx.assetId]?.find { it.date == date.toString() }

            when (tx.type) {
                TransactionType.BUY -> {
                    if (kline != null) {
                        totalPnl += (kline.close - tx.price) * tx.quantity
                    }
                    totalPnl -= tx.fee
                }

                TransactionType.SELL -> {
                    val dateStr = date.toString()
                    val yesterdayClose = klineData[tx.assetId]?.findLast { it.date < dateStr }?.close
                    if (yesterdayClose != null) {
                        totalPnl += (tx.price - yesterdayClose) * tx.quantity
                    }
                    totalPnl -= tx.fee
                }

                TransactionType.DIVIDEND -> {
                    totalPnl += tx.amount
                }
            }
        }

        return totalPnl
    }

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun Long.toLocalDate(): LocalDate =
        Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault()).date
}
