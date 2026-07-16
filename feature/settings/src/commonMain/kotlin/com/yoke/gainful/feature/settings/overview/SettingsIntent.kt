package com.yoke.gainful.feature.settings.overview

import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.model.GainLossColorScheme

sealed interface SettingsIntent {
    data class SetRefreshMinutes(val minutes: Int) : SettingsIntent

    data class SetOpenTime(val hour: Int, val minute: Int) : SettingsIntent

    data class SetCloseTime(val hour: Int, val minute: Int) : SettingsIntent

    data class ShowTimePicker(val target: TimePickerTarget) : SettingsIntent

    data object DismissTimePicker : SettingsIntent

    data class ShowFreqPicker(val show: Boolean) : SettingsIntent

    data class SetGainLossColorScheme(val scheme: GainLossColorScheme) : SettingsIntent

    data class ShowColorPicker(val show: Boolean) : SettingsIntent

    data class ShowExportDialog(val show: Boolean) : SettingsIntent

    data class ConfirmExport(val csvConfig: CsvConfig) : SettingsIntent

    data object DismissExportResult : SettingsIntent

    data object Logout : SettingsIntent
}

enum class TimePickerTarget { OPEN, CLOSE }
