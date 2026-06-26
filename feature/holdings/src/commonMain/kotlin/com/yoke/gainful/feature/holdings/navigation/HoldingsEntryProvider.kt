package com.yoke.gainful.feature.holdings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yoke.gainful.feature.holdings.detail.StockDetailScreen
import com.yoke.gainful.feature.holdings.detail.StockDetailViewModel
import com.yoke.gainful.feature.holdings.overview.HoldingsScreen
import com.yoke.gainful.feature.holdings.overview.HoldingsViewModel
import com.yoke.gainful.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun EntryProviderScope<NavKey>.holdingsEntry(navigator: Navigator) {
    entry<HoldingsNavKey> {
        val viewModel = koinViewModel<HoldingsViewModel>()
        HoldingsScreen(
            viewModel = viewModel,
            onStockClick = { code, name, pinYin -> navigator.navigate(StockDetailNavKey(code, name, pinYin)) },
        )
    }
    entry<StockDetailNavKey> { key ->
        val viewModel = koinViewModel<StockDetailViewModel> { parametersOf(key.code, key.name, key.pinYin) }
        StockDetailScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
        )
    }
}
