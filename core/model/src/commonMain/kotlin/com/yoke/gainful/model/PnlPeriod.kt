package com.yoke.gainful.model

enum class PnlPeriodType {
    DAY,
    WEEK,
    MONTH,
    YEAR,
}

data class PnlPeriod(
    val type: PnlPeriodType,
    val year: Int,
    val month: Int = 1,
    val week: Int = 1,
    val startDay: Int = 1,
    val endDay: Int = 1,
)

data class PnlCell(
    val year: Int,
    val month: Int = 0,
    val day: Int = 0,
    val dayOfWeek: Int = 0,
    val week: Int = 0,
    val weekStartDay: Int = 0,
    val weekEndDay: Int = 0,
    val value: Double?,
    val isCurrent: Boolean = false,
    val isEmpty: Boolean = true,
    val isPadding: Boolean = false,
    val isFuture: Boolean = false,
)

data class PnlData(
    val period: PnlPeriod,
    val totalPnl: Double,
    val cells: List<PnlCell>,
)
