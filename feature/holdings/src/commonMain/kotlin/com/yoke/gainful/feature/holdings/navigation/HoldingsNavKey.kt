package com.yoke.gainful.feature.holdings.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HoldingsNavKey : NavKey

@Serializable
data class StockDetailNavKey(val code: String) : NavKey
