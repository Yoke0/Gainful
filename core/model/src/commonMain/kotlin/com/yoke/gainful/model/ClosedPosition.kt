package com.yoke.gainful.model

data class ClosedPosition(
    val assetId: String,
    val code: String,
    val name: String,
    val pinYin: String = "",
    val lastSellPrice: Double,
    val lastSellDate: Long,
    val totalBuys: Double,
    val totalSells: Double,
    val totalDividends: Double,
) {
    val realizedGain: Double
        get() = -totalBuys + totalSells + totalDividends

    val realizedGainPercent: Double
        get() = if (totalBuys > 0) (realizedGain / totalBuys) * 100 else 0.0
}
