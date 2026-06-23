package com.yoke.gainful

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yoke.gainful.data.repository.UserPreferencesRepository
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
import com.yoke.gainful.ui.theme.ProvideGainLossColors
import org.koin.compose.koinInject

@Composable
fun App() {
    initKoin()

    GainfulTheme {
        val repository = koinInject<UserPreferencesRepository>()
        val userPreferences by repository.userPreferences.collectAsState(
            initial = com.yoke.gainful.model.UserPreferences()
        )

        ProvideGainLossColors(scheme = userPreferences.gainLossColorScheme) {
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
}
