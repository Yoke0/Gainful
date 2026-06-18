package com.yoke.gainful

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.yoke.gainful.di.initKoin
import com.yoke.gainful.feature.dashboard.DashboardRoute
import com.yoke.gainful.feature.holdings.detail.StockDetailRoute
import com.yoke.gainful.feature.holdings.overview.HoldingsRoute
import com.yoke.gainful.feature.settings.SettingsRoute
import com.yoke.gainful.feature.transactions.add.AddTransactionRoute
import com.yoke.gainful.feature.transactions.overview.TransactionsRoute
import com.yoke.gainful.navigation.AddTransaction
import com.yoke.gainful.navigation.Dashboard
import com.yoke.gainful.navigation.GainfulNavGraph
import com.yoke.gainful.navigation.Holdings
import com.yoke.gainful.navigation.Settings
import com.yoke.gainful.navigation.StockDetail
import com.yoke.gainful.navigation.Transactions
import com.yoke.gainful.ui.theme.GainfulTheme

@Composable
fun App() {
    initKoin()

    GainfulTheme {
        var showSplash by remember { mutableStateOf(true) }

        AnimatedContent(
            targetState = showSplash,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "splash",
        ) { isSplash ->
            if (isSplash) {
                SplashScreen(onSplashFinished = { showSplash = false })
            } else {
                GainfulNavGraph { screen, onNavigate, onBack ->
                    when (screen) {
                        Dashboard -> DashboardRoute()
                        Transactions -> TransactionsRoute(
                            onAddTransaction = { onNavigate(AddTransaction) },
                        )
                        Holdings -> HoldingsRoute(
                            onStockClick = { code -> onNavigate(StockDetail(code)) },
                        )
                        Settings -> SettingsRoute()
                        AddTransaction -> AddTransactionRoute(onBack = onBack)
                        is StockDetail -> StockDetailRoute(
                            code = screen.code,
                            onBack = onBack,
                        )
                    }
                }
            }
        }
    }
}
