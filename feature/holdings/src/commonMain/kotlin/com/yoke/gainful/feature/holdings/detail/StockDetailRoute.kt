package com.yoke.gainful.feature.holdings.detail

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun StockDetailRoute(
    code: String,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<StockDetailViewModel> { parametersOf(code) }
    StockDetailScreen(
        viewModel = viewModel,
        onBack = onBack,
    )
}
