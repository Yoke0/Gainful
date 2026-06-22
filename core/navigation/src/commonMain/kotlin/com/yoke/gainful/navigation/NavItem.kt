package com.yoke.gainful.navigation

import androidx.compose.runtime.Composable
import com.yoke.gainful.ui.components.DashboardIcon
import com.yoke.gainful.ui.components.HoldingsIcon
import com.yoke.gainful.ui.components.RecordsIcon
import com.yoke.gainful.ui.components.SettingsIcon
import gainful.core.navigation.generated.resources.Res
import gainful.core.navigation.generated.resources.nav_dashboard
import gainful.core.navigation.generated.resources.nav_holdings
import gainful.core.navigation.generated.resources.nav_settings
import gainful.core.navigation.generated.resources.nav_transactions
import org.jetbrains.compose.resources.stringResource

internal data class NavItem(
    val screen: Screen,
    val label: @Composable () -> String,
    val icon: @Composable (isSelected: Boolean) -> Unit,
)

internal val navItems = listOf(
    NavItem(Dashboard, { stringResource(Res.string.nav_dashboard) }) { DashboardIcon(isSelected = it) },
    NavItem(Transactions, { stringResource(Res.string.nav_transactions) }) { RecordsIcon(isSelected = it) },
    NavItem(Holdings, { stringResource(Res.string.nav_holdings) }) { HoldingsIcon(isSelected = it) },
    NavItem(Settings, { stringResource(Res.string.nav_settings) }) { SettingsIcon(isSelected = it) },
)

internal val navScreens = navItems.map { it.screen }
