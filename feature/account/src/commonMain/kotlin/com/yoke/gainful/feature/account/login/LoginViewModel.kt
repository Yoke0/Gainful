package com.yoke.gainful.feature.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.data.repository.AuthRepository
import com.yoke.gainful.sync.TransactionSyncService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val transactionSyncService: TransactionSyncService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun setUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun showSessionExpired() {
        _uiState.update { it.copy(error = LoginError.SESSION_EXPIRED) }
    }

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SetUsername -> _uiState.update { it.copy(username = intent.value) }
            is LoginIntent.SetPassword -> _uiState.update { it.copy(password = intent.value) }
            is LoginIntent.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            is LoginIntent.Submit -> submit()
            is LoginIntent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun submit() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = LoginError.EMPTY_FIELDS) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.login(state.username, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                    // Trigger transaction sync after login
                    viewModelScope.launch { transactionSyncService.sync() }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = LoginError.INVALID_CREDENTIALS,
                        )
                    }
                }
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: LoginError? = null,
    val loginSuccess: Boolean = false,
)

enum class LoginError {
    EMPTY_FIELDS,
    INVALID_CREDENTIALS,
    SESSION_EXPIRED,
}
