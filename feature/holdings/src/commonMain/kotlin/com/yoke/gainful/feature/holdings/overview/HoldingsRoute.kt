package com.yoke.gainful.feature.holdings.overview

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HoldingsRoute(
    onStockClick: (String) -> Unit,
) {
    val viewModel = koinViewModel<HoldingsViewModel>()
    HoldingsScreen(
        viewModel = viewModel,
        onStockClick = onStockClick,
    )
}
