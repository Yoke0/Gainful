package com.yoke.gainful.feature.account.avatar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.data.repository.AuthRepository
import com.yoke.gainful.file.cropImageToSquare
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val AVATAR_SIZE = 296

class AvatarViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AvatarUiState())
    val uiState: StateFlow<AvatarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Load existing avatar: prefer avatarUrl from profile, fallback to emoji
            val profile = authRepository.userProfile.first()
            val avatarUrl = profile?.avatarUrl
            if (avatarUrl != null) {
                _uiState.update { it.copy(avatarUrl = avatarUrl) }
            } else {
                authRepository.avatarEmoji.first()?.let { emoji ->
                    _uiState.update { it.copy(selectedEmoji = emoji) }
                }
            }
        }
    }

    fun onIntent(intent: AvatarIntent) {
        when (intent) {
            is AvatarIntent.SelectPreset -> {
                _uiState.update {
                    it.copy(selectedEmoji = intent.emoji, previewImageBytes = null, previewFileName = null)
                }
            }

            is AvatarIntent.SelectImage -> {
                selectImage(intent.imageBytes, intent.fileName)
            }

            is AvatarIntent.Confirm -> {
                confirm()
            }

            is AvatarIntent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun selectImage(imageBytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { cropImageToSquare(imageBytes, AVATAR_SIZE) }
                .onSuccess { cropped ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            previewImageBytes = cropped,
                            previewFileName = fileName,
                            selectedEmoji = null,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AvatarError.IMAGE_PROCESS,
                        )
                    }
                }
        }
    }

    private fun confirm() {
        val state = _uiState.value
        val imageBytes = state.previewImageBytes
        val fileName = state.previewFileName

        if (imageBytes != null && fileName != null) {
            uploadAvatar(imageBytes, fileName)
        } else {
            saveEmoji()
        }
    }

    private fun saveEmoji() {
        val emoji = _uiState.value.selectedEmoji ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { authRepository.setAvatarEmoji(emoji) }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AvatarError.SAVE,
                        )
                    }
                }
        }
    }

    private fun uploadAvatar(imageBytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.uploadAvatar(imageBytes, fileName)
                .onSuccess {
                    val url = authRepository.userProfile.first()?.avatarUrl
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            saveSuccess = true,
                            avatarUrl = url,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AvatarError.UPLOAD,
                        )
                    }
                }
        }
    }
}

data class AvatarUiState(
    val selectedEmoji: String? = null,
    val avatarUrl: String? = null,
    val previewImageBytes: ByteArray? = null,
    val previewFileName: String? = null,
    val isLoading: Boolean = false,
    val error: AvatarError? = null,
    val saveSuccess: Boolean = false,
)
