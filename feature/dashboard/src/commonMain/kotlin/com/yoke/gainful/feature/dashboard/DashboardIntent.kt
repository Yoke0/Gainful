package com.yoke.gainful.feature.dashboard

sealed interface DashboardIntent {
    data object LoadPortfolioSummary : DashboardIntent
    data object Refresh : DashboardIntent
}
