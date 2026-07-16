package com.yoke.gainful.feature.account.login

sealed interface LoginIntent {
    data class SetUsername(val value: String) : LoginIntent

    data class SetPassword(val value: String) : LoginIntent

    data object TogglePasswordVisibility : LoginIntent

    data object Submit : LoginIntent

    data object ClearError : LoginIntent
}
