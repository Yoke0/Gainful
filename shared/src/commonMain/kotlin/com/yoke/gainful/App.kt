package com.yoke.gainful

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.yoke.gainful.di.initKoin
import com.yoke.gainful.feature.dashboard.DashboardScreen
import com.yoke.gainful.feature.holdings.HoldingsScreen
import com.yoke.gainful.feature.holdings.HoldingsViewModel
import com.yoke.gainful.feature.holdings.StockDetailScreen
import com.yoke.gainful.feature.holdings.StockDetailViewModel
import com.yoke.gainful.feature.settings.SettingsScreen
import com.yoke.gainful.feature.transactions.AddTransactionScreen
import com.yoke.gainful.feature.transactions.AddTransactionViewModel
import com.yoke.gainful.feature.transactions.TransactionsScreen
import com.yoke.gainful.feature.transactions.TransactionsViewModel
import com.yoke.gainful.navigation.AddTransaction
import com.yoke.gainful.navigation.Dashboard
import com.yoke.gainful.navigation.GainfulNavGraph
import com.yoke.gainful.navigation.Holdings
import com.yoke.gainful.navigation.Settings
import com.yoke.gainful.navigation.StockDetail
import com.yoke.gainful.navigation.Transactions
import com.yoke.gainful.ui.theme.GainfulTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

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
                        Dashboard -> DashboardScreen()
                        Transactions -> {
                            val viewModel = koinViewModel<TransactionsViewModel>()
                            TransactionsScreen(
                                viewModel = viewModel,
                                onAddTransaction = { onNavigate(AddTransaction) },
                            )
                        }
                        Holdings -> {
                            val viewModel = koinViewModel<HoldingsViewModel>()
                            HoldingsScreen(
                                viewModel = viewModel,
                                onStockClick = { code -> onNavigate(StockDetail(code)) },
                            )
                        }
                        Settings -> SettingsScreen()
                        AddTransaction -> {
                            val viewModel = koinViewModel<AddTransactionViewModel>()
                            AddTransactionScreen(
                                viewModel = viewModel,
                                todayDate = viewModel.todayDateString(),
                                onBack = onBack,
                            )
                        }
                        is StockDetail -> {
                            val viewModel = remember(screen.code) {
                                object : KoinComponent {
                                    fun getViewModel() = get<StockDetailViewModel> { parametersOf(screen.code) }
                                }.getViewModel()
                            }
                            StockDetailScreen(
                                viewModel = viewModel,
                                onBack = onBack,
                            )
                        }
                    }
                }
            }
        }
    }
}
