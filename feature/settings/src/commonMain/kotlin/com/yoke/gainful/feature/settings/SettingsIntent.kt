package com.yoke.gainful.feature.settings

sealed interface SettingsIntent {
    data class ToggleDarkMode(val enabled: Boolean) : SettingsIntent
    data class ToggleNotifications(val enabled: Boolean) : SettingsIntent
    data class ToggleMarketAlert(val enabled: Boolean) : SettingsIntent
    data class ToggleFaceId(val enabled: Boolean) : SettingsIntent
    data class SetCurrency(val currency: String) : SettingsIntent
    data class SetRefreshInterval(val interval: String) : SettingsIntent
}
