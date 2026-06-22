package com.yoke.gainful.model

data class UserPreferences(
    val refreshMinutes: Int = 3,
    val openHour: Int = 9,
    val openMinute: Int = 30,
    val closeHour: Int = 15,
    val closeMinute: Int = 0,
)
