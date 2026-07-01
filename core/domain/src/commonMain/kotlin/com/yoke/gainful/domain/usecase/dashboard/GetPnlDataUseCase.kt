package com.yoke.gainful.domain.usecase.dashboard

import com.yoke.gainful.model.PnlCell
import com.yoke.gainful.model.PnlData
import com.yoke.gainful.model.PnlPeriod
import com.yoke.gainful.model.PnlPeriodType
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class GetPnlDataUseCase {
    private fun today(): LocalDate =
        kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    operator fun invoke(
        transactions: List<Transaction>,
        periodType: PnlPeriodType,
        year: Int,
        month: Int,
    ): PnlData {
        val currentDate = today()

        return when (periodType) {
            PnlPeriodType.DAY -> getDayPnl(transactions, year, month, currentDate)
            PnlPeriodType.WEEK -> getWeekPnl(transactions, year, month, currentDate)
            PnlPeriodType.MONTH -> getMonthPnl(transactions, year, currentDate)
            PnlPeriodType.YEAR -> getYearPnl(transactions, currentDate)
        }
    }

    private fun getDayPnl(
        transactions: List<Transaction>,
        year: Int,
        month: Int,
        today: LocalDate,
    ): PnlData {
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
            val isCurrent = date == today
            val isFuture = date > today
            val dayPnl = calculateDayPnl(transactions, date)

            cells.add(
                PnlCell(
                    year = year,
                    month = month,
                    day = day,
                    dayOfWeek = (date.dayOfWeek.ordinal + 1) % 7,
                    value = if (isFuture) null else dayPnl,
                    isCurrent = isCurrent,
                    isEmpty = isFuture || dayPnl == null,
                    isFuture = isFuture,
                ),
            )
        }

        val totalPnl = cells.mapNotNull { it.value }.sum()

        return PnlData(
            period =
                PnlPeriod(
                    type = PnlPeriodType.DAY,
                    year = year,
                    month = month,
                ),
            totalPnl = totalPnl,
            cells = cells,
        )
    }

    private fun getWeekPnl(
        transactions: List<Transaction>,
        year: Int,
        month: Int,
        today: LocalDate,
    ): PnlData {
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
            val weekPnl =
                week.sumOf { date ->
                    calculateDayPnl(transactions, date) ?: 0.0
                }
            val isCurrentWeek = week.any { it == today }

            cells.add(
                PnlCell(
                    year = year,
                    month = month,
                    week = weekNumber,
                    weekStartDay = week.first().day,
                    weekEndDay = week.last().day,
                    value = if (week.any { it > today }) null else weekPnl,
                    isCurrent = isCurrentWeek,
                    isEmpty = week.any { it > today } || weekPnl == 0.0,
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

    private fun getMonthPnl(
        transactions: List<Transaction>,
        year: Int,
        today: LocalDate,
    ): PnlData {
        val cells = mutableListOf<PnlCell>()

        for (month in 1..12) {
            val monthPnl =
                (1..daysInMonth(year, month)).sumOf { day ->
                    calculateDayPnl(transactions, LocalDate(year, month, day)) ?: 0.0
                }
            val isCurrentMonth = year == today.year && month == today.month.ordinal + 1
            val isFuture = LocalDate(year, month, 1) > today

            cells.add(
                PnlCell(
                    year = year,
                    month = month,
                    value = if (isFuture) null else monthPnl,
                    isCurrent = isCurrentMonth,
                    isEmpty = isFuture || monthPnl == 0.0,
                ),
            )
        }

        val totalPnl = cells.mapNotNull { it.value }.sum()

        return PnlData(
            period =
                PnlPeriod(
                    type = PnlPeriodType.MONTH,
                    year = year,
                ),
            totalPnl = totalPnl,
            cells = cells,
        )
    }

    private fun getYearPnl(
        transactions: List<Transaction>,
        today: LocalDate,
    ): PnlData {
        val cells = mutableListOf<PnlCell>()
        val firstYear = getFirstYear(transactions)

        for (year in firstYear..today.year) {
            val yearPnl =
                (1..12).sumOf { month ->
                    (1..daysInMonth(year, month)).sumOf { day ->
                        calculateDayPnl(transactions, LocalDate(year, month, day)) ?: 0.0
                    }
                }
            val isCurrentYear = year == today.year
            val isFuture = year > today.year

            cells.add(
                PnlCell(
                    year = year,
                    value = if (isFuture) null else yearPnl,
                    isCurrent = isCurrentYear,
                    isEmpty = isFuture || yearPnl == 0.0,
                ),
            )
        }

        val totalPnl = cells.mapNotNull { it.value }.sum()

        return PnlData(
            period =
                PnlPeriod(
                    type = PnlPeriodType.YEAR,
                    year = today.year,
                ),
            totalPnl = totalPnl,
            cells = cells,
        )
    }

    private fun calculateDayPnl(transactions: List<Transaction>, date: LocalDate): Double? {
        val dayTransactions =
            transactions.filter { tx ->
                val txDate =
                    Instant.fromEpochMilliseconds(tx.tradeDate)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                txDate == date
            }

        if (dayTransactions.isEmpty()) return null

        return dayTransactions.sumOf { tx ->
            when (tx.type) {
                TransactionType.BUY -> -tx.amount
                TransactionType.SELL -> tx.amount
                TransactionType.DIVIDEND -> tx.amount
            }
        }
    }

    private fun getWeekNumber(date: LocalDate): Int {
        val firstDayOfYear = LocalDate(date.year, 1, 1)
        val daysSinceFirstDay = date.dayOfYear - firstDayOfYear.dayOfYear
        return (daysSinceFirstDay / 7) + 1
    }

    private fun getFirstYear(transactions: List<Transaction>): Int {
        if (transactions.isEmpty()) return today().year

        val earliestTransaction = transactions.minByOrNull { it.tradeDate } ?: return today().year
        val date =
            Instant.fromEpochMilliseconds(earliestTransaction.tradeDate)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
        return date.year
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
