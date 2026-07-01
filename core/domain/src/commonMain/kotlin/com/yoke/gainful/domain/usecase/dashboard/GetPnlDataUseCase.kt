package com.yoke.gainful.domain.usecase.dashboard

import com.yoke.gainful.data.repository.KLineCacheRepository
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
    private val kLineCacheRepository: KLineCacheRepository,
) {
    private fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    suspend operator fun invoke(
        transactions: List<Transaction>,
        periodType: PnlPeriodType,
        year: Int,
        month: Int,
    ): PnlData {
        val currentDate = today()
        val dailyPositions = computeDailyPositions(transactions)
        val klineData = fetchKlineData(transactions)
        val dailyPnl = computeDailyPnl(transactions, klineData, dailyPositions)

        return when (periodType) {
            PnlPeriodType.DAY -> getDayPnl(dailyPnl, year, month, currentDate)
            PnlPeriodType.WEEK -> getWeekPnl(dailyPnl, year, month, currentDate)
            PnlPeriodType.MONTH -> getMonthPnl(dailyPnl, year, currentDate)
            PnlPeriodType.YEAR -> getYearPnl(dailyPnl, currentDate)
        }
    }

    private suspend fun fetchKlineData(transactions: List<Transaction>): Map<String, List<KLine>> {
        val assetIds = transactions.map { it.assetId }.distinct()
        val klineData = mutableMapOf<String, List<KLine>>()

        for (assetId in assetIds) {
            val cachedKlines = kLineCacheRepository.getByAssetId(assetId)
            klineData[assetId] = cachedKlines
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
            val pnl = calculateDayPnlWithKline(transactions, klineData, dailyPositions, date)
            if (pnl != null && pnl != 0.0) {
                result[date] = pnl
            }
        }
        return result
    }

    suspend fun getStockPnlDetails(
        transactions: List<Transaction>,
        date: LocalDate,
        stockNames: Map<String, String> = emptyMap(),
    ): List<StockPnlDetail> {
        val dailyPositions = computeDailyPositions(transactions)
        val klineData = fetchKlineData(transactions)
        val positions = dailyPositions[date] ?: emptyMap()
        val dayTransactions =
            transactions.filter { tx ->
                Instant.fromEpochMilliseconds(tx.tradeDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date == date
            }

        val details = mutableListOf<StockPnlDetail>()

        // Position-based PnL (stocks held at start of day)
        for ((assetId, quantity) in positions) {
            if (quantity <= 0) continue

            val kline = klineData[assetId]?.find { it.date == date.toString() }

            // Find sell quantity for this stock today
            val sellTx = dayTransactions.firstOrNull { it.assetId == assetId && it.type == TransactionType.SELL }
            val buyTx = dayTransactions.firstOrNull { it.assetId == assetId && it.type == TransactionType.BUY }
            val sellQty = sellTx?.quantity ?: 0.0

            // Position PnL only for shares still held (subtract sold shares)
            val heldQuantity = quantity - sellQty
            val positionPnl = if (kline != null && heldQuantity > 0) kline.changeAmount * heldQuantity else 0.0

            val dateStr = date.toString()
            val klines = klineData[assetId]
            val yesterdayClose = klines?.findLast { it.date < dateStr }?.close

            val tx = buyTx ?: sellTx
            val tradePnl =
                when {
                    buyTx != null -> {
                        val execPnl = if (kline != null) (kline.close - buyTx.price) * buyTx.quantity else 0.0
                        execPnl
                    }

                    sellTx != null -> {
                        if (yesterdayClose != null) (sellTx.price - yesterdayClose) * sellTx.quantity else 0.0
                    }

                    else -> {
                        0.0
                    }
                }

            val fee = tx?.fee ?: 0.0

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
                    fee = fee,
                ),
            )
        }

        // Stocks only traded today (not in position)
        for (tx in dayTransactions) {
            if (details.any { it.assetId == tx.assetId }) continue

            val dateStr = date.toString()
            val klines = klineData[tx.assetId]
            val yesterdayClose = klines?.findLast { it.date < dateStr }?.close
            val kline = klines?.find { it.date == dateStr }

            val tradePnl =
                when (tx.type) {
                    TransactionType.BUY -> {
                        if (kline != null) (kline.close - tx.price) * tx.quantity else 0.0
                    }

                    TransactionType.SELL -> {
                        if (yesterdayClose != null) (tx.price - yesterdayClose) * tx.quantity else 0.0
                    }

                    TransactionType.DIVIDEND -> {
                        tx.amount
                    }
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

    private fun getDayPnl(dailyPnl: Map<LocalDate, Double>, year: Int, month: Int, today: LocalDate): PnlData {
        val daysInMonth = daysInMonth(year, month)
        val cells = mutableListOf<PnlCell>()

        val firstDayOfMonth = LocalDate(year, month, 1)
        val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.ordinal + 1) % 7

        for (i in 0 until firstDayOfWeek) {
            cells.add(
                PnlCell(
                    year = year,
                    month = month,
                    day = 0,
                    dayOfWeek = i,
                    value = null,
                    isEmpty = true,
                    isPadding = true,
                ),
            )
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

        val totalPnl = cells.mapNotNull { it.value }.sum()

        return PnlData(
            period = PnlPeriod(type = PnlPeriodType.DAY, year = year, month = month),
            totalPnl = totalPnl,
            cells = cells,
        )
    }

    private fun getWeekPnl(dailyPnl: Map<LocalDate, Double>, year: Int, month: Int, today: LocalDate): PnlData {
        val firstDayOfMonth = LocalDate(year, month, 1)
        val lastDayOfMonth = LocalDate(year, month, 1).plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
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
            val weekPnl = week.sumOf { date -> dailyPnl[date] ?: 0.0 }
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

        val totalPnl = cells.mapNotNull { it.value }.sum()

        return PnlData(
            period =
                PnlPeriod(
                    type = PnlPeriodType.WEEK,
                    year = year,
                    month = month,
                    startDay = firstDayOfMonth.day,
                    endDay = lastDayOfMonth.day,
                ),
            totalPnl = totalPnl,
            cells = cells,
        )
    }

    private fun getMonthPnl(dailyPnl: Map<LocalDate, Double>, year: Int, today: LocalDate): PnlData {
        val cells = mutableListOf<PnlCell>()

        for (month in 1..12) {
            val isFuture = LocalDate(year, month, 1) > today
            val monthPnl =
                if (isFuture) {
                    0.0
                } else {
                    (1..daysInMonth(year, month)).sumOf { day -> dailyPnl[LocalDate(year, month, day)] ?: 0.0 }
                }

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

        val totalPnl = cells.mapNotNull { it.value }.sum()

        return PnlData(
            period = PnlPeriod(type = PnlPeriodType.MONTH, year = year),
            totalPnl = totalPnl,
            cells = cells,
        )
    }

    private fun getYearPnl(dailyPnl: Map<LocalDate, Double>, today: LocalDate): PnlData {
        val cells = mutableListOf<PnlCell>()
        val firstYear = getFirstYear(dailyPnl)

        for (year in firstYear..today.year) {
            val isFuture = year > today.year
            val yearPnl =
                if (isFuture) {
                    0.0
                } else {
                    (1..12).sumOf { month ->
                        (1..daysInMonth(year, month)).sumOf { day -> dailyPnl[LocalDate(year, month, day)] ?: 0.0 }
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

        val totalPnl = cells.mapNotNull { it.value }.sum()

        return PnlData(
            period = PnlPeriod(type = PnlPeriodType.YEAR, year = today.year),
            totalPnl = totalPnl,
            cells = cells,
        )
    }

    private fun computeDailyPositions(transactions: List<Transaction>): Map<LocalDate, Map<String, Double>> {
        if (transactions.isEmpty()) return emptyMap()

        val sortedTransactions = transactions.sortedBy { it.tradeDate }
        val positions = mutableMapOf<String, Double>()
        val startOfDayPositions = mutableMapOf<LocalDate, Map<String, Double>>()

        var currentDate: LocalDate? = null

        for (tx in sortedTransactions) {
            val txDate =
                Instant.fromEpochMilliseconds(tx.tradeDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date

            if (currentDate != txDate) {
                // New day: record start-of-day position (before this transaction)
                if (currentDate != null) {
                    startOfDayPositions[txDate] = positions.toMap()
                } else {
                    // First transaction ever: start-of-day is empty
                    startOfDayPositions[txDate] = emptyMap()
                }
                currentDate = txDate
            } else if (txDate !in startOfDayPositions) {
                // Same day, first transaction: record start-of-day
                startOfDayPositions[txDate] = positions.toMap()
            }

            when (tx.type) {
                TransactionType.BUY -> {
                    positions[tx.assetId] = (positions[tx.assetId] ?: 0.0) + tx.quantity
                }

                TransactionType.SELL -> {
                    positions[tx.assetId] = (positions[tx.assetId] ?: 0.0) - tx.quantity
                }

                TransactionType.DIVIDEND -> {
                }
            }
        }

        // Carry forward: for dates without transactions, use previous day's end-of-day position
        val firstDate = startOfDayPositions.keys.minOrNull() ?: return startOfDayPositions
        val today = today()
        var lastPosition = emptyMap<String, Double>()

        var currentDateIter = firstDate
        while (currentDateIter <= today) {
            if (startOfDayPositions.containsKey(currentDateIter)) {
                // This date has transactions: start-of-day is already recorded
                // Update lastPosition to end-of-day (apply transactions)
                lastPosition = startOfDayPositions[currentDateIter]!!.toMutableMap()
                // Apply all transactions on this date to get end-of-day
                val dayTx =
                    sortedTransactions.filter { tx ->
                        Instant.fromEpochMilliseconds(tx.tradeDate)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date == currentDateIter
                    }
                for (tx in dayTx) {
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

    private fun calculateDayPnlWithKline(
        transactions: List<Transaction>,
        klineData: Map<String, List<KLine>>,
        dailyPositions: Map<LocalDate, Map<String, Double>>,
        date: LocalDate,
    ): Double? {
        val positions = dailyPositions[date]
        val dayTransactions =
            transactions.filter { tx ->
                val txDate =
                    Instant.fromEpochMilliseconds(tx.tradeDate)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                txDate == date
            }

        if (positions.isNullOrEmpty() && dayTransactions.isEmpty()) {
            return null
        }

        var totalPnl = 0.0

        // Calculate PnL for all held stocks using changeAmount
        // Subtract sell quantity from position to avoid double-counting
        if (positions != null) {
            for ((assetId, quantity) in positions) {
                if (quantity <= 0) continue

                val kline = klineData[assetId]?.find { it.date == date.toString() }
                if (kline == null) continue

                // Find sell quantity for this stock today
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

        // Calculate PnL for trades today
        for (tx in dayTransactions) {
            val kline = klineData[tx.assetId]?.find { it.date == date.toString() }

            when (tx.type) {
                TransactionType.BUY -> {
                    // Buy: execution quality vs close
                    if (kline != null) {
                        val execPnl = (kline.close - tx.price) * tx.quantity
                        totalPnl += execPnl
                    }
                    totalPnl -= tx.fee
                }

                TransactionType.SELL -> {
                    // Sell: sell price vs yesterday's close
                    val klines = klineData[tx.assetId]
                    val dateStr = date.toString()
                    val yesterdayClose = klines?.findLast { it.date < dateStr }?.close
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

    private fun getWeekNumber(date: LocalDate): Int {
        val firstDayOfYear = LocalDate(date.year, 1, 1)
        val daysSinceFirstDay = date.dayOfYear - firstDayOfYear.dayOfYear
        return (daysSinceFirstDay / 7) + 1
    }

    private fun getFirstYear(dailyPnl: Map<LocalDate, Double>): Int {
        return dailyPnl.keys.minOfOrNull { it.year } ?: today().year
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }
}
