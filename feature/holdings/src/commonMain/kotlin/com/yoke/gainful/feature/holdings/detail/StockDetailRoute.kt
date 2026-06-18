package com.yoke.gainful.feature.holdings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

@Composable
fun StockDetailRoute(
    code: String,
    onBack: () -> Unit,
) {
    val viewModel = remember(code) {
        object : KoinComponent {
            fun getViewModel() = get<StockDetailViewModel> { parametersOf(code) }
        }.getViewModel()
    }
    StockDetailScreen(
        viewModel = viewModel,
        onBack = onBack,
    )
}
