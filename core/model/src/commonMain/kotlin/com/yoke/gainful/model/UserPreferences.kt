package com.yoke.gainful.model

enum class GainLossColorScheme {
    RED_UP,
    GREEN_UP,
}

data class UserPreferences(
    val refreshMinutes: Int = 3,
    val openHour: Int = 9,
    val openMinute: Int = 30,
    val closeHour: Int = 15,
    val closeMinute: Int = 0,
    val gainLossColorScheme: GainLossColorScheme = GainLossColorScheme.RED_UP,
)
