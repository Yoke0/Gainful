package com.yoke.gainful.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.data.repository.UserPreferencesRepository
import com.yoke.gainful.model.GainLossColorScheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.userPreferences.collect { prefs ->
                _uiState.update {
                    it.copy(
                        refreshMinutes = prefs.refreshMinutes,
                        openHour = prefs.openHour,
                        openMinute = prefs.openMinute,
                        closeHour = prefs.closeHour,
                        closeMinute = prefs.closeMinute,
                        gainLossColorScheme = prefs.gainLossColorScheme,
                    )
                }
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetRefreshMinutes -> viewModelScope.launch {
                repository.setRefreshMinutes(intent.minutes)
            }
            is SettingsIntent.SetOpenTime -> viewModelScope.launch {
                repository.setOpenTime(intent.hour, intent.minute)
            }
            is SettingsIntent.SetCloseTime -> viewModelScope.launch {
                repository.setCloseTime(intent.hour, intent.minute)
            }
            is SettingsIntent.ShowTimePicker -> _uiState.update {
                it.copy(timePickerTarget = intent.target, showTimePicker = true)
            }
            is SettingsIntent.DismissTimePicker -> _uiState.update {
                it.copy(showTimePicker = false)
            }
            is SettingsIntent.ShowFreqPicker -> _uiState.update {
                it.copy(showFreqPicker = intent.show)
            }
            is SettingsIntent.SetGainLossColorScheme -> viewModelScope.launch {
                repository.setGainLossColorScheme(intent.scheme)
            }
            is SettingsIntent.ShowColorPicker -> _uiState.update {
                it.copy(showColorPicker = intent.show)
            }
        }
    }
}

data class SettingsUiState(
    val refreshMinutes: Int = 3,
    val openHour: Int = 9,
    val openMinute: Int = 30,
    val closeHour: Int = 15,
    val closeMinute: Int = 0,
    val gainLossColorScheme: GainLossColorScheme = GainLossColorScheme.RED_UP,
    val showTimePicker: Boolean = false,
    val timePickerTarget: TimePickerTarget = TimePickerTarget.OPEN,
    val showFreqPicker: Boolean = false,
    val showColorPicker: Boolean = false,
) {
    val openTimeDisplay: String get() = "${openHour.pad2()}:${openMinute.pad2()}"
    val closeTimeDisplay: String get() = "${closeHour.pad2()}:${closeMinute.pad2()}"
}

private fun Int.pad2(): String = if (this < 10) "0$this" else "$this"
