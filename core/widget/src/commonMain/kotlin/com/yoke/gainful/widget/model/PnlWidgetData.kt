package com.yoke.gainful.widget.model

data class PnlWidgetData(
    val dailyGain: Double = 0.0,
    val dailyGainPercent: Double = 0.0,
    val totalMarketValue: Double = 0.0,
    val holdingsCount: Int = 0,
    val dailyGainText: String = "",
    val dailyGainPercentText: String = "",
    val title: String = "",
    val noDataText: String = "",
) {
    val isPositive: Boolean get() = dailyGain >= 0
    val hasData: Boolean get() = holdingsCount > 0
}
