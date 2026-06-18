package com.yoke.gainful.model

data class HoldingDisplay(
    val id: String,
    val assetId: String,
    val code: String,
    val name: String,
    val quantity: Double,
    val averageCost: Double,
    val currentPrice: Double,
    val changePercent: Double,
    val changeAmount: Double,
    val trendPrices: List<Double> = emptyList(),
) {
    val totalMarketValue: Double
        get() = currentPrice * quantity

    val totalCost: Double
        get() = averageCost * quantity

    val totalGain: Double
        get() = totalMarketValue - totalCost

    val totalGainPercent: Double
        get() = if (totalCost > 0) (totalGain / totalCost) * 100 else 0.0
}
