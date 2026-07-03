package com.yoke.gainful.domain.usecase.dashboard

import com.yoke.gainful.data.repository.KLineCacheRepository
import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.PnlCell
import com.yoke.gainful.model.PnlData
import com.yoke.gainful.model.PnlPeriod
import com.yoke.gainful.model.PnlPeriodType
import com.yoke.gainful.model.StockPnlDetail
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class GetPnlDataUseCase(
    private val pnlCacheRepository: PnlCacheRepository,
    private val kLineCacheRepository: KLineCacheRepository,
) {
    suspend operator fun invoke(
        transactions: List<Transaction>,
        periodType: PnlPeriodType,
        year: Int,
        month: Int,
    ): PnlData {
        val dailyPnl = getDailyPnlFromCache(transactions)

        return when (periodType) {
            PnlPeriodType.DAY -> getDayPnl(dailyPnl, year, month)
            PnlPeriodType.WEEK -> getWeekPnl(dailyPnl, year, month)
            PnlPeriodType.MONTH -> getMonthPnl(dailyPnl, year)
            PnlPeriodType.YEAR -> getYearPnl(dailyPnl)
        }
    }

    suspend fun getStockPnlDetails(
        transactions: List<Transaction>,
        date: LocalDate,
        stockNames: Map<String, String> = emptyMap(),
    ): List<StockPnlDetail> {
        val dailyPositions = computeDailyPositions(transactions)
        val klineData = fetchKlineData(transactions)
        val positions = dailyPositions[date] ?: emptyMap()
        val dayTransactions = transactions.filter { it.tradeDate.toLocalDate() == date }

        val details = mutableListOf<StockPnlDetail>()

        // Position-based PnL
        for ((assetId, quantity) in positions) {
            if (quantity <= 0) continue

            val kline = klineData[assetId]?.find { it.date == date.toString() }
            val sellTx = dayTransactions.firstOrNull { it.assetId == assetId && it.type == TransactionType.SELL }
            val buyTx = dayTransactions.firstOrNull { it.assetId == assetId && it.type == TransactionType.BUY }
            val sellQty = sellTx?.quantity ?: 0.0

            val heldQuantity = quantity - sellQty
            val positionPnl = if (kline != null && heldQuantity > 0) kline.changeAmount * heldQuantity else 0.0

            val dateStr = date.toString()
            val klines = klineData[assetId]
            val yesterdayClose = klines?.findLast { it.date < dateStr }?.close

            val tx = buyTx ?: sellTx
            val tradePnl =
                when {
                    buyTx != null -> if (kline != null) (kline.close - buyTx.price) * buyTx.quantity else 0.0
                    sellTx != null -> if (yesterdayClose != null) (sellTx.price - yesterdayClose) * sellTx.quantity else 0.0
                    else -> 0.0
                }

            details.add(
                StockPnlDetail(
                    assetId = assetId,
                    stockName = stockNames[assetId] ?: assetId,
                    positionPnl = positionPnl,
                    positionQuantity = heldQuantity,
                    tradePnl = tradePnl,
                    tradeType = tx?.type,
                    tradePrice = tx?.price,
                    tradeQuantity = tx?.quantity ?: 0.0,
                    fee = tx?.fee ?: 0.0,
                ),
            )
        }

        // Trade-only PnL (stocks not in position)
        for (tx in dayTransactions) {
            if (details.any { it.assetId == tx.assetId }) continue

            val dateStr = date.toString()
            val klines = klineData[tx.assetId]
            val yesterdayClose = klines?.findLast { it.date < dateStr }?.close
            val kline = klines?.find { it.date == dateStr }

            val tradePnl =
                when (tx.type) {
                    TransactionType.BUY -> if (kline != null) (kline.close - tx.price) * tx.quantity else 0.0
                    TransactionType.SELL -> if (yesterdayClose != null) (tx.price - yesterdayClose) * tx.quantity else 0.0
                    TransactionType.DIVIDEND -> tx.amount
                }

            details.add(
                StockPnlDetail(
                    assetId = tx.assetId,
                    stockName = stockNames[tx.assetId] ?: tx.assetId,
                    tradePnl = tradePnl,
                    tradeType = tx.type,
                    tradePrice = tx.price,
                    tradeQuantity = tx.quantity,
                    fee = tx.fee,
                ),
            )
        }

        return details.sortedByDescending { it.pnl }
    }

    // region Cache reading

    private suspend fun getDailyPnlFromCache(transactions: List<Transaction>): Map<LocalDate, Double> {
        val firstTxDate = getFirstTransactionDate(transactions) ?: return emptyMap()
        val cached = pnlCacheRepository.getDailyPnl(firstTxDate.toString(), today().toString())
        return cached.mapKeys { LocalDate.parse(it.key) }
    }

    private fun getFirstTransactionDate(transactions: List<Transaction>): LocalDate? {
        if (transactions.isEmpty()) return null
        return transactions.minOfOrNull { it.tradeDate.toLocalDate() }
    }

    // endregion

    // region Period rendering

    private fun getDayPnl(dailyPnl: Map<LocalDate, Double>, year: Int, month: Int): PnlData {
        val today = today()
        val daysInMonth = daysInMonth(year, month)
        val cells = mutableListOf<PnlCell>()

        val firstDayOfMonth = LocalDate(year, month, 1)
        val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.ordinal + 1) % 7

        for (i in 0 until firstDayOfWeek) {
            cells.add(PnlCell(year = year, month = month, day = 0, dayOfWeek = i, value = null, isEmpty = true, isPadding = true))
        }

        for (day in 1..daysInMonth) {
            val date = LocalDate(year, month, day)
            val isFuture = date > today
            val dayPnl = if (isFuture) null else dailyPnl[date]

            cells.add(
                PnlCell(
                    year = year,
                    month = month,
                    day = day,
                    dayOfWeek = (date.dayOfWeek.ordinal + 1) % 7,
                    value = dayPnl,
                    isCurrent = date == today,
                    isEmpty = dayPnl == null || dayPnl == 0.0,
                    isFuture = isFuture,
                ),
            )
        }

        return PnlData(
            period = PnlPeriod(type = PnlPeriodType.DAY, year = year, month = month),
            totalPnl = cells.mapNotNull { it.value }.sum(),
            cells = cells,
        )
    }

    private fun getWeekPnl(dailyPnl: Map<LocalDate, Double>, year: Int, month: Int): PnlData {
        val today = today()
        val firstDayOfMonth = LocalDate(year, month, 1)
        val lastDayOfMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
        val weeksInMonth = mutableListOf<List<LocalDate>>()

        var currentWeekStart = firstDayOfMonth
        while (currentWeekStart <= lastDayOfMonth) {
            val week =
                (0..6).mapNotNull { offset ->
                    val date = currentWeekStart.plus(offset.toLong(), DateTimeUnit.DAY)
                    if (date <= lastDayOfMonth) date else null
                }
            weeksInMonth.add(week)
            currentWeekStart = currentWeekStart.plus(7, DateTimeUnit.DAY)
        }

        val cells = mutableListOf<PnlCell>()
        var weekNumber = getWeekNumber(firstDayOfMonth)

        for (week in weeksInMonth) {
            val weekPnl = week.sumOf { dailyPnl[it] ?: 0.0 }
            val hasFuture = week.any { it > today }

            cells.add(
                PnlCell(
                    year = year,
                    month = month,
                    week = weekNumber,
                    weekStartDay = week.first().day,
                    weekEndDay = week.last().day,
                    value = if (hasFuture) null else weekPnl,
                    isCurrent = week.any { it == today },
                    isEmpty = hasFuture || weekPnl == 0.0,
                ),
            )
            weekNumber++
        }

        return PnlData(
            period =
                PnlPeriod(
                    type = PnlPeriodType.WEEK,
                    year = year,
                    month = month,
                    startDay = firstDayOfMonth.day,
                    endDay = lastDayOfMonth.day,
                ),
            totalPnl = cells.mapNotNull { it.value }.sum(),
            cells = cells,
        )
    }

    private fun getMonthPnl(dailyPnl: Map<LocalDate, Double>, year: Int): PnlData {
        val today = today()
        val cells = mutableListOf<PnlCell>()

        for (month in 1..12) {
            val isFuture = LocalDate(year, month, 1) > today
            val monthPnl = if (isFuture) 0.0 else (1..daysInMonth(year, month)).sumOf { dailyPnl[LocalDate(year, month, it)] ?: 0.0 }

            cells.add(
                PnlCell(
                    year = year,
                    month = month,
                    value = if (isFuture) null else monthPnl,
                    isCurrent = year == today.year && month == today.month.ordinal + 1,
                    isEmpty = isFuture || monthPnl == 0.0,
                ),
            )
        }

        return PnlData(
            period = PnlPeriod(type = PnlPeriodType.MONTH, year = year),
            totalPnl = cells.mapNotNull { it.value }.sum(),
            cells = cells,
        )
    }

    private fun getYearPnl(dailyPnl: Map<LocalDate, Double>): PnlData {
        val today = today()
        val cells = mutableListOf<PnlCell>()
        val firstYear = dailyPnl.keys.minOfOrNull { it.year } ?: today.year

        for (year in firstYear..today.year) {
            val isFuture = year > today.year
            val yearPnl =
                if (isFuture) {
                    0.0
                } else {
                    (1..12).sumOf {
                        month ->
                        (1..daysInMonth(year, month)).sumOf { dailyPnl[LocalDate(year, month, it)] ?: 0.0 }
                    }
                }

            cells.add(
                PnlCell(
                    year = year,
                    value = if (isFuture) null else yearPnl,
                    isCurrent = year == today.year,
                    isEmpty = isFuture || yearPnl == 0.0,
                ),
            )
        }

        return PnlData(
            period = PnlPeriod(type = PnlPeriodType.YEAR, year = today.year),
            totalPnl = cells.mapNotNull { it.value }.sum(),
            cells = cells,
        )
    }

    // endregion

    // region Stock detail helpers

    private suspend fun fetchKlineData(transactions: List<Transaction>): Map<String, List<KLine>> {
        val assetIds = transactions.map { it.assetId }.distinct()
        return assetIds.associateWith { kLineCacheRepository.getByAssetId(it) }
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

    // endregion

    // region Utilities

    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun Long.toLocalDate(): LocalDate =
        Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun getWeekNumber(date: LocalDate): Int {
        val firstDayOfYear = LocalDate(date.year, 1, 1)
        return (date.dayOfYear - firstDayOfYear.dayOfYear) / 7 + 1
    }

    private fun daysInMonth(year: Int, month: Int): Int =
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            else -> 30
        }

    // endregion
}
