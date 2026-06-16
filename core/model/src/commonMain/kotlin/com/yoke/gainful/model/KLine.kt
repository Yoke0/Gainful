package com.yoke.gainful.model

data class KLine(
    val date: String,
    val open: Double,
    val close: Double,
    val high: Double,
    val low: Double,
    val volume: Long,
    val turnover: Double,
    val amplitude: Double,
    val changePercent: Double,
    val changeAmount: Double,
    val turnoverRate: Double,
)
