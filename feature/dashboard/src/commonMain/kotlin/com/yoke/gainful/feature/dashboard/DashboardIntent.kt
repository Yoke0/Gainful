package com.yoke.gainful.feature.dashboard

import com.yoke.gainful.model.PnlPeriodType

sealed interface DashboardIntent {
    data object LoadPortfolioSummary : DashboardIntent

    data object Refresh : DashboardIntent

    data class SelectPnlPeriod(val periodType: PnlPeriodType) : DashboardIntent

    data class NavigatePnlPeriod(val direction: Int) : DashboardIntent

    data class SelectPnlCell(
        val year: Int,
        val month: Int,
        val day: Int,
        val periodType: PnlPeriodType,
        val weekStartDay: Int = 0,
        val weekEndDay: Int = 0,
    ) : DashboardIntent

    data object DismissPnlDetail : DashboardIntent
}
