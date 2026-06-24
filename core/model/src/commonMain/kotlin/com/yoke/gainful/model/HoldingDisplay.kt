package com.yoke.gainful.model

data class HoldingDisplay(
    val id: String,
    val assetId: String,
    val code: String,
    val name: String,
    val pinYin: String = "",
    val quantity: Double,
    val averageCost: Double,
    val currentPrice: Double,
    val changePercent: Double,
    val changeAmount: Double,
    val totalBuys: Double = 0.0,
    val totalSells: Double = 0.0,
    val totalDividends: Double = 0.0,
    val trends: List<StockTrend> = emptyList(),
) {
    val totalMarketValue: Double
        get() = currentPrice * quantity

    val totalCost: Double
        get() = averageCost * quantity

    val totalGain: Double
        get() = -totalBuys + totalSells + totalDividends + totalMarketValue
}
