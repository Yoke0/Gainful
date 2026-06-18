package com.yoke.gainful.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleDarkMode -> _uiState.update { it.copy(darkMode = intent.enabled) }
            is SettingsIntent.ToggleNotifications -> _uiState.update { it.copy(notifications = intent.enabled) }
            is SettingsIntent.ToggleMarketAlert -> _uiState.update { it.copy(marketAlert = intent.enabled) }
            is SettingsIntent.ToggleFaceId -> _uiState.update { it.copy(faceId = intent.enabled) }
            is SettingsIntent.SetCurrency -> _uiState.update { it.copy(currency = intent.currency) }
            is SettingsIntent.SetRefreshInterval -> _uiState.update { it.copy(refreshInterval = intent.interval) }
        }
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val darkMode: Boolean = true,
    val notifications: Boolean = true,
    val marketAlert: Boolean = false,
    val faceId: Boolean = true,
    val currency: String = "CNY",
    val refreshInterval: String = "每 1 分钟",
    val error: String? = null,
)
