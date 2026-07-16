package com.yoke.gainful.feature.account.register

sealed interface RegisterIntent {
    data class SetNickname(val value: String) : RegisterIntent

    data class SetEmail(val value: String) : RegisterIntent

    data class SetPassword(val value: String) : RegisterIntent

    data class SetConfirmPassword(val value: String) : RegisterIntent

    data object TogglePasswordVisibility : RegisterIntent

    data object ToggleAgreement : RegisterIntent

    data object Submit : RegisterIntent

    data object ClearError : RegisterIntent
}
