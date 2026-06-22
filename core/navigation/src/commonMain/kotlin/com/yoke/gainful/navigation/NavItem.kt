package com.yoke.gainful.navigation

import androidx.compose.runtime.Composable
import com.yoke.gainful.ui.components.DashboardIcon
import com.yoke.gainful.ui.components.HoldingsIcon
import com.yoke.gainful.ui.components.RecordsIcon
import com.yoke.gainful.ui.components.SettingsIcon

internal data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: @Composable (isSelected: Boolean) -> Unit,
)

internal val navItems = listOf(
    NavItem(Dashboard, "仪表盘") { DashboardIcon(isSelected = it) },
    NavItem(Transactions, "记录") { RecordsIcon(isSelected = it) },
    NavItem(Holdings, "持仓") { HoldingsIcon(isSelected = it) },
    NavItem(Settings, "设置") { SettingsIcon(isSelected = it) },
)

internal val navScreens = navItems.map { it.screen }
