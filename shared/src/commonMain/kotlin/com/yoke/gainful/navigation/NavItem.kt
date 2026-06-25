package com.yoke.gainful.navigation

import androidx.compose.runtime.Composable
import com.yoke.gainful.feature.dashboard.navigation.DashboardNavKey
import com.yoke.gainful.feature.holdings.navigation.HoldingsNavKey
import com.yoke.gainful.feature.settings.navigation.SettingsNavKey
import com.yoke.gainful.feature.transactions.navigation.TransactionsNavKey
import com.yoke.gainful.ui.components.DashboardIcon
import com.yoke.gainful.ui.components.HoldingsIcon
import com.yoke.gainful.ui.components.RecordsIcon
import com.yoke.gainful.ui.components.SettingsIcon
import gainful.shared.generated.resources.Res
import gainful.shared.generated.resources.nav_dashboard
import gainful.shared.generated.resources.nav_holdings
import gainful.shared.generated.resources.nav_settings
import gainful.shared.generated.resources.nav_transactions
import org.jetbrains.compose.resources.stringResource

data class TopLevelNavItem(
    val label: @Composable () -> String,
    val icon: @Composable (isSelected: Boolean) -> Unit,
)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    DashboardNavKey to TopLevelNavItem(
        label = { stringResource(Res.string.nav_dashboard) },
        icon = { DashboardIcon(isSelected = it) },
    ),
    TransactionsNavKey to TopLevelNavItem(
        label = { stringResource(Res.string.nav_transactions) },
        icon = { RecordsIcon(isSelected = it) },
    ),
    HoldingsNavKey to TopLevelNavItem(
        label = { stringResource(Res.string.nav_holdings) },
        icon = { HoldingsIcon(isSelected = it) },
    ),
    SettingsNavKey to TopLevelNavItem(
        label = { stringResource(Res.string.nav_settings) },
        icon = { SettingsIcon(isSelected = it) },
    ),
)
