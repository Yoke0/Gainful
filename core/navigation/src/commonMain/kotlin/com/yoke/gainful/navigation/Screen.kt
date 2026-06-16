package com.yoke.gainful.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Holdings : Screen("holdings")
    data object Transactions : Screen("transactions")
    data object Settings : Screen("settings")
}
