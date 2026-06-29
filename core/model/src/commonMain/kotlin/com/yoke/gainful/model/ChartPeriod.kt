package com.yoke.gainful.model

enum class ChartPeriod(
    val klt: Int? = null,
    val ndays: Int = 1,
) {
    TRENDS(ndays = 1),
    TRENDS_5D(ndays = 5),
    DAILY(klt = 101),
    WEEKLY(klt = 102),
    MONTHLY(klt = 103),
    MIN_1(klt = 1),
    MIN_5(klt = 5),
    MIN_15(klt = 15),
    MIN_30(klt = 30),
    MIN_60(klt = 60),
    ;

    val isTrends: Boolean
        get() = klt == null
}
