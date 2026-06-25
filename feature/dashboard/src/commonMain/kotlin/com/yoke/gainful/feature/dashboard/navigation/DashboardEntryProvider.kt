package com.yoke.gainful.feature.dashboard.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yoke.gainful.feature.dashboard.DashboardScreen
import com.yoke.gainful.feature.dashboard.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

fun EntryProviderScope<NavKey>.dashboardEntry() {
    entry<DashboardNavKey> {
        val viewModel = koinViewModel<DashboardViewModel>()
        DashboardScreen(viewModel = viewModel)
    }
}
