package com.yoke.gainful.model

data class StockQuote(
    val code: String,
    val market: Int,
    val name: String,
    val latestPrice: Double,
    val changePercent: Double,
    val changeAmount: Double,
    val volume: Long,
    val turnover: Double,
    val amplitude: Double,
    val turnoverRate: Double,
    val peDynamic: Double,
    val pb: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val preClose: Double,
    val totalMarketCap: Double,
    val circulatingMarketCap: Double,
    val industry: String,
)
