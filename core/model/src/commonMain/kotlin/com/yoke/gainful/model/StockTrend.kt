package com.yoke.gainful.model

data class StockTrend(
    val time: String,
    val price: Double,
    val volume: Int,
    val averagePrice: Double,
)
