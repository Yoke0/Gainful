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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import com.yoke.gainful.data.repository.UserPreferencesRepository
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.GainfulTheme
import com.yoke.gainful.di.initKoin
import com.yoke.gainful.feature.account.navigation.AvatarNavKey
import com.yoke.gainful.feature.account.navigation.LoginNavKey
import com.yoke.gainful.feature.account.navigation.accountEntry
import com.yoke.gainful.feature.dashboard.navigation.DashboardNavKey
import com.yoke.gainful.feature.dashboard.navigation.dashboardEntry
import com.yoke.gainful.feature.holdings.navigation.holdingsEntry
import com.yoke.gainful.feature.settings.navigation.settingsEntry
import com.yoke.gainful.feature.transactions.navigation.transactionsEntry
import com.yoke.gainful.navigation.GainfulNavGraph
import com.yoke.gainful.navigation.Navigator
import com.yoke.gainful.navigation.TOP_LEVEL_NAV_ITEMS
import com.yoke.gainful.navigation.rememberNavigationState
import com.yoke.gainful.navigation.serializersConfig
import com.yoke.gainful.sync.KLineFetchService
import com.yoke.gainful.sync.StockPriceFetchService
import com.yoke.gainful.ui.ProvideGainLossColors
import org.koin.compose.koinInject

@Composable
fun App() {
    initKoin()

    val fetchService = koinInject<StockPriceFetchService>()
    val kLineFetchService = koinInject<KLineFetchService>()

    GainfulTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Background),
        ) {
            val repository = koinInject<UserPreferencesRepository>()
            val userPreferences by repository.userPreferences.collectAsState(
                initial = com.yoke.gainful.model.UserPreferences(),
            )

            ProvideGainLossColors(scheme = userPreferences.gainLossColorScheme) {
                var showSplash by rememberSaveable { mutableStateOf(true) }

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
                            onInit = {
                                fetchService.fetchOnce()
                                kLineFetchService.fetchAll()
                            },
                            onSplashFinished = {
                                showSplash = false
                                fetchService.start()
                            },
                        )
                    } else {
                        val navigationState =
                            rememberNavigationState(
                                startKey = DashboardNavKey,
                                topLevelKeys = TOP_LEVEL_NAV_ITEMS.keys,
                                configuration = serializersConfig,
                            )
                        val navigator = remember { Navigator(navigationState) }

                        val entryProvider =
                            entryProvider {
                                dashboardEntry()
                                transactionsEntry(navigator)
                                holdingsEntry(navigator)
                                settingsEntry(
                                    navigator,
                                    onNavigateToLogin = { navigator.navigate(LoginNavKey) },
                                    onNavigateToAvatar = { navigator.navigate(AvatarNavKey) },
                                )
                                accountEntry(navigator)
                            }

                        GainfulNavGraph(
                            navigationState = navigationState,
                            navigator = navigator,
                            entryProvider = entryProvider,
                        )
                    }
                }
            }
        }
    }
}
