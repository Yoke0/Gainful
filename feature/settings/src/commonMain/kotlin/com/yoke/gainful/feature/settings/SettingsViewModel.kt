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
            is SettingsIntent.SetRefreshMinutes ->
                _uiState.update { it.copy(refreshMinutes = intent.minutes) }
            is SettingsIntent.SetOpenTime ->
                _uiState.update { it.copy(openHour = intent.hour, openMinute = intent.minute) }
            is SettingsIntent.SetCloseTime ->
                _uiState.update { it.copy(closeHour = intent.hour, closeMinute = intent.minute) }
            is SettingsIntent.ShowTimePicker ->
                _uiState.update { it.copy(timePickerTarget = intent.target, showTimePicker = true) }
            is SettingsIntent.DismissTimePicker ->
                _uiState.update { it.copy(showTimePicker = false) }
            is SettingsIntent.ShowFreqPicker ->
                _uiState.update { it.copy(showFreqPicker = intent.show) }
        }
    }
}

data class SettingsUiState(
    val refreshMinutes: Int = 3,
    val openHour: Int = 9,
    val openMinute: Int = 30,
    val closeHour: Int = 15,
    val closeMinute: Int = 0,
    val showTimePicker: Boolean = false,
    val timePickerTarget: TimePickerTarget = TimePickerTarget.OPEN,
    val showFreqPicker: Boolean = false,
) {
    val openTimeDisplay: String get() = "${openHour.pad2()}:${openMinute.pad2()}"
    val closeTimeDisplay: String get() = "${closeHour.pad2()}:${closeMinute.pad2()}"
    val refreshDisplay: String get() = "$refreshMinutes 分钟"
}

private fun Int.pad2(): String = if (this < 10) "0$this" else "$this"
