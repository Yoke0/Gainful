package com.yoke.gainful.widget.domain

import com.yoke.gainful.common.extensions.formatSigned
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.widget.model.PnlWidgetData
import kotlinx.coroutines.flow.firstOrNull

class GetTodayPnlUseCase(
    private val getHoldingsDisplayUseCase: GetHoldingsDisplayUseCase,
) {
    suspend fun compute(title: String, noDataText: String): PnlWidgetData {
        val holdings = getHoldingsDisplayUseCase().firstOrNull() ?: emptyList()

        if (holdings.isEmpty()) return PnlWidgetData(title = title, noDataText = noDataText)

        val totalDailyGain = holdings.sumOf { it.changeAmount * it.quantity }
        val totalMarketValue = holdings.sumOf { it.currentPrice * it.quantity }
        val previousDayValue = totalMarketValue - totalDailyGain
        val totalDailyGainPercent =
            if (previousDayValue > 0) {
                (totalDailyGain / previousDayValue) * 100
            } else {
                0.0
            }

        return PnlWidgetData(
            dailyGain = totalDailyGain,
            dailyGainPercent = totalDailyGainPercent,
            totalMarketValue = totalMarketValue,
            holdingsCount = holdings.size,
            dailyGainText = totalDailyGain.formatSigned(),
            dailyGainPercentText = "${totalDailyGainPercent.formatSigned()}%",
            title = title,
            noDataText = noDataText,
        )
    }
}
