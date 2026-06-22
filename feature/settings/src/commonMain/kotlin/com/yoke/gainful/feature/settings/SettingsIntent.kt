package com.yoke.gainful.feature.settings

sealed interface SettingsIntent {
    data class SetRefreshMinutes(val minutes: Int) : SettingsIntent
    data class SetOpenTime(val hour: Int, val minute: Int) : SettingsIntent
    data class SetCloseTime(val hour: Int, val minute: Int) : SettingsIntent
    data class ShowTimePicker(val target: TimePickerTarget) : SettingsIntent
    data object DismissTimePicker : SettingsIntent
    data class ShowFreqPicker(val show: Boolean) : SettingsIntent
}

enum class TimePickerTarget { OPEN, CLOSE }
