package com.yoke.gainful

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yoke.gainful.data.repository.UserPreferencesRepository
import com.yoke.gainful.di.initKoin
import com.yoke.gainful.feature.dashboard.DashboardRoute
import com.yoke.gainful.feature.holdings.detail.StockDetailRoute
import com.yoke.gainful.feature.holdings.overview.HoldingsRoute
import com.yoke.gainful.feature.settings.overview.SettingsRoute
import com.yoke.gainful.feature.settings.`import`.ImportRoute
import com.yoke.gainful.feature.transactions.add.AddTransactionRoute
import com.yoke.gainful.feature.transactions.overview.TransactionsRoute
import com.yoke.gainful.navigation.AddTransaction
import com.yoke.gainful.navigation.Dashboard
import com.yoke.gainful.navigation.GainfulNavGraph
import com.yoke.gainful.navigation.Holdings
import com.yoke.gainful.navigation.ImportTransactions
import com.yoke.gainful.navigation.Settings
import com.yoke.gainful.navigation.StockDetail
import com.yoke.gainful.navigation.Transactions
import com.yoke.gainful.sync.StockPriceFetchService
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.ProvideGainLossColors
import org.koin.compose.koinInject

@Composable
fun App() {
    initKoin()

    val fetchService = koinInject<StockPriceFetchService>()

    GainfulTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            val repository = koinInject<UserPreferencesRepository>()
            val userPreferences by repository.userPreferences.collectAsState(
                initial = com.yoke.gainful.model.UserPreferences()
            )

            ProvideGainLossColors(scheme = userPreferences.gainLossColorScheme) {
                var showSplash by remember { mutableStateOf(true) }

                DisposableEffect(Unit) {
                    onDispose {
                        fetchService.stop()
                    }
                }

                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(durationMillis = 300),
                    label = "splash",
                ) { isSplash ->
                    if (isSplash) {
                        SplashScreen(
                            onInit = { fetchService.fetchOnce() },
                            onSplashFinished = {
                                showSplash = false
                                fetchService.start()
                            },
                        )
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
                                Settings -> SettingsRoute(
                                    onNavigateToImport = { onNavigate(ImportTransactions) },
                                )
                                AddTransaction -> AddTransactionRoute(onBack = onBack)
                                ImportTransactions -> ImportRoute(onBack = onBack)
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
}
