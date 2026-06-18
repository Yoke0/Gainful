package com.yoke.gainful.model

enum class ChartPeriod(
    val label: String,
    val klt: Int? = null,
    val ndays: Int = 1,
) {
    TRENDS("分时", ndays = 1),
    TRENDS_5D("五日", ndays = 5),
    DAILY("日K", klt = 101),
    WEEKLY("周K", klt = 102),
    MONTHLY("月K", klt = 103),
    MIN_1("1分钟", klt = 1),
    MIN_5("5分钟", klt = 5),
    MIN_15("15分钟", klt = 15),
    MIN_30("30分钟", klt = 30),
    MIN_60("60分钟", klt = 60);

    val isTrends: Boolean
        get() = klt == null
}
