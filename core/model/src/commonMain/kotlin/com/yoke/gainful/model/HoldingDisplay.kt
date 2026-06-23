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
    val trendPrices: List<Double> = emptyList(),
) {
    val totalMarketValue: Double
        get() = currentPrice * quantity

    val totalCost: Double
        get() = averageCost * quantity

    val totalGain: Double
        get() = -totalBuys + totalSells + totalDividends + totalMarketValue

    val totalGainPercent: Double
        get() = if (totalBuys > 0) (totalGain / totalBuys) * 100 else 0.0
}
