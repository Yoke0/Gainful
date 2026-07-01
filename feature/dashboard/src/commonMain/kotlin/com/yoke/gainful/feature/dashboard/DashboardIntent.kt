package com.yoke.gainful.feature.dashboard

import com.yoke.gainful.model.PnlPeriodType

sealed interface DashboardIntent {
    data object LoadPortfolioSummary : DashboardIntent

    data object Refresh : DashboardIntent

    data class SelectPnlPeriod(val periodType: PnlPeriodType) : DashboardIntent

    data class NavigatePnlPeriod(val direction: Int) : DashboardIntent
}
