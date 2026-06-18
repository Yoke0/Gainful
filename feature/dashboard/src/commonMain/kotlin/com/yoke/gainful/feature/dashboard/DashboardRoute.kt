package com.yoke.gainful.feature.dashboard

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardRoute() {
    val viewModel = koinViewModel<DashboardViewModel>()
    DashboardScreen(
        viewModel = viewModel,
    )
}
