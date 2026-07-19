package com.yoke.gainful

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import com.yoke.gainful.data.repository.AppSettingsRepository
import com.yoke.gainful.data.repository.AuthRepository
import com.yoke.gainful.datastore.UserDataSource
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
import com.yoke.gainful.model.AppSettings
import com.yoke.gainful.navigation.GainfulNavGraph
import com.yoke.gainful.navigation.Navigator
import com.yoke.gainful.navigation.TOP_LEVEL_NAV_ITEMS
import com.yoke.gainful.navigation.rememberNavigationState
import com.yoke.gainful.navigation.serializersConfig
import com.yoke.gainful.sync.KLineFetchService
import com.yoke.gainful.sync.StockPriceFetchService
import com.yoke.gainful.sync.TransactionSyncService
import com.yoke.gainful.ui.ProvideGainLossColors
import gainful.shared.generated.resources.Res
import gainful.shared.generated.resources.app_name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun App(onTitleReady: (String) -> Unit = {}) {
    initKoin()

    val title = stringResource(Res.string.app_name)
    LaunchedEffect(title) { onTitleReady(title) }

    val fetchService = koinInject<StockPriceFetchService>()
    val kLineFetchService = koinInject<KLineFetchService>()
    val transactionSyncService = koinInject<TransactionSyncService>()
    val authRepository = koinInject<AuthRepository>()
    val userDataSource = koinInject<UserDataSource>()

    GainfulTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Background),
        ) {
            val repository = koinInject<AppSettingsRepository>()
            val appSettings by repository.appSettings.collectAsState(
                initial = AppSettings(),
            )

            ProvideGainLossColors(scheme = appSettings.gainLossColorScheme) {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                var navigateToLoginWithUsername by remember { mutableStateOf<String?>(null) }

                DisposableEffect(Unit) {
                    onDispose {
                        transactionSyncService.stopPeriodicSync()
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
                                fetchService.fetchAllHoldings()
                                kLineFetchService.fetchAll()
                                // Validate token and sync transactions on startup
                                CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
                                    val result = runCatching { authRepository.refreshProfile() }
                                    if (result.isFailure) {
                                        // Token invalid - get username then clear auth
                                        val username = userDataSource.userState.first().username
                                        authRepository.logout()
                                        if (username != null) {
                                            navigateToLoginWithUsername = username
                                        }
                                    } else {
                                        transactionSyncService.sync()
                                    }
                                }
                            },
                            onSplashFinished = {
                                showSplash = false
                                transactionSyncService.startPeriodicSync()
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

                        // Navigate to login on token expiry
                        navigateToLoginWithUsername?.let { username ->
                            navigateToLoginWithUsername = null
                            navigator.navigate(
                                com.yoke.gainful.feature.account.navigation.LoginWithUsernameNavKey(username, sessionExpired = true),
                            )
                        }
                    }
                }
            }
        }
    }
}
