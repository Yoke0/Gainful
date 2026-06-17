package com.yoke.gainful.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey

@Serializable
data object Dashboard : Screen

@Serializable
data object Transactions : Screen

@Serializable
data object Holdings : Screen

@Serializable
data object Settings : Screen

@Serializable
data object AddTransaction : Screen
