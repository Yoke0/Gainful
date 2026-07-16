package com.yoke.gainful.feature.settings.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.common.extensions.pad2
import com.yoke.gainful.data.repository.AuthRepository
import com.yoke.gainful.data.repository.UserPreferencesRepository
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsWithAssetsOnceUseCase
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.feature.settings.util.CsvUtil
import com.yoke.gainful.model.GainLossColorScheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
    private val getTransactionsWithAssetsOnceUseCase: GetTransactionsWithAssetsOnceUseCase,
    private val authRepository: AuthRepository,
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

        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                _uiState.update {
                    it.copy(
                        isLoggedIn = authState.isLoggedIn,
                        userId = authState.userId,
                        userNickname = authState.username,
                    )
                }
            }
        }

        viewModelScope.launch {
            authRepository.avatarEmoji.collect { emoji ->
                _uiState.update { it.copy(avatarEmoji = emoji) }
            }
        }

        viewModelScope.launch {
            authRepository.userProfile.collect { profile ->
                _uiState.update { it.copy(avatarUrl = profile?.avatarUrl) }
            }
        }

        viewModelScope.launch {
            authRepository.authState.first().let { authState ->
                if (authState.isLoggedIn) {
                    authRepository.refreshProfile()
                }
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetRefreshMinutes -> {
                viewModelScope.launch {
                    repository.setRefreshMinutes(intent.minutes)
                }
            }

            is SettingsIntent.SetOpenTime -> {
                viewModelScope.launch {
                    repository.setOpenTime(intent.hour, intent.minute)
                }
            }

            is SettingsIntent.SetCloseTime -> {
                viewModelScope.launch {
                    repository.setCloseTime(intent.hour, intent.minute)
                }
            }

            is SettingsIntent.ShowTimePicker -> {
                _uiState.update {
                    it.copy(timePickerTarget = intent.target, showTimePicker = true)
                }
            }

            is SettingsIntent.DismissTimePicker -> {
                _uiState.update {
                    it.copy(showTimePicker = false)
                }
            }

            is SettingsIntent.ShowFreqPicker -> {
                _uiState.update {
                    it.copy(showFreqPicker = intent.show)
                }
            }

            is SettingsIntent.SetGainLossColorScheme -> {
                viewModelScope.launch {
                    repository.setGainLossColorScheme(intent.scheme)
                }
            }

            is SettingsIntent.ShowColorPicker -> {
                _uiState.update {
                    it.copy(showColorPicker = intent.show)
                }
            }

            is SettingsIntent.ShowExportDialog -> {
                showExportDialog(intent.show)
            }

            is SettingsIntent.ConfirmExport -> {
                confirmExport(intent.csvConfig)
            }

            is SettingsIntent.DismissExportResult -> {
                _uiState.update {
                    it.copy(showExportResult = false, exportResult = null)
                }
            }

            is SettingsIntent.Logout -> {
                viewModelScope.launch {
                    authRepository.logout()
                }
            }
        }
    }

    private fun generateFileName(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return "trades_${now.date.year}${now.date.month.number.pad2()}${now.date.day.pad2()}${now.hour.pad2()}${now.minute.pad2()}"
    }

    private fun showExportDialog(show: Boolean) {
        if (!show) {
            _uiState.update { it.copy(showExportDialog = false) }
            return
        }
        viewModelScope.launch {
            val txsWithAssets = getTransactionsWithAssetsOnceUseCase()
            val fileName = generateFileName()
            _uiState.update {
                it.copy(
                    showExportDialog = true,
                    exportRecordCount = txsWithAssets.size,
                    exportFileName = fileName,
                )
            }
        }
    }

    private fun confirmExport(csvConfig: CsvConfig) {
        viewModelScope.launch {
            val txsWithAssets = getTransactionsWithAssetsOnceUseCase()
            val csv = CsvUtil.generateCsv(txsWithAssets, csvConfig)
            val fileName = generateFileName()
            _uiState.update {
                it.copy(
                    exportCsvContent = csv,
                    exportFileName = fileName,
                    showExportDialog = false,
                )
            }
        }
    }

    fun onExportFileSaved(success: Boolean) {
        if (success) {
            _uiState.update {
                it.copy(
                    exportCsvContent = null,
                    showExportResult = true,
                    exportResult =
                        ExportResult(
                            fileName = it.exportFileName,
                            recordCount = it.exportRecordCount,
                        ),
                )
            }
        } else {
            _uiState.update { it.copy(exportCsvContent = null) }
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
    val showExportDialog: Boolean = false,
    val exportRecordCount: Int = 0,
    val exportFileName: String = "",
    val exportCsvContent: String? = null,
    val showExportResult: Boolean = false,
    val exportResult: ExportResult? = null,
    val isLoggedIn: Boolean = false,
    val userId: String? = null,
    val userNickname: String? = null,
    val avatarEmoji: String? = null,
    val avatarUrl: String? = null,
) {
    val openTimeDisplay: String get() = "${openHour.pad2()}:${openMinute.pad2()}"
    val closeTimeDisplay: String get() = "${closeHour.pad2()}:${closeMinute.pad2()}"
}

data class ExportResult(
    val fileName: String,
    val recordCount: Int,
)
