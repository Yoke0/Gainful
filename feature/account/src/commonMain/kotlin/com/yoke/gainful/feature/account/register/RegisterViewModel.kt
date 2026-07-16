package com.yoke.gainful.feature.account.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.SetNickname -> {
                _uiState.update { it.copy(nickname = intent.value) }
            }

            is RegisterIntent.SetEmail -> {
                _uiState.update { it.copy(email = intent.value) }
            }

            is RegisterIntent.SetPassword -> {
                _uiState.update { it.copy(password = intent.value) }
                updatePasswordStrength(intent.value)
            }

            is RegisterIntent.SetConfirmPassword -> {
                _uiState.update { it.copy(confirmPassword = intent.value) }
            }

            is RegisterIntent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            is RegisterIntent.ToggleAgreement -> {
                _uiState.update { it.copy(agreedToTerms = !it.agreedToTerms) }
            }

            is RegisterIntent.Submit -> {
                submit()
            }

            is RegisterIntent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun updatePasswordStrength(password: String) {
        var score = 0
        if (password.length >= 6) score++
        if (password.length >= 10) score++
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        val strength =
            when {
                score <= 1 -> PasswordStrength.WEAK
                score <= 2 -> PasswordStrength.FAIR
                score <= 3 -> PasswordStrength.MEDIUM
                else -> PasswordStrength.STRONG
            }
        _uiState.update { it.copy(passwordStrength = strength) }
    }

    private fun submit() {
        val state = _uiState.value
        if (state.nickname.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = RegisterError.EMPTY_FIELDS) }
            return
        }
        if (state.nickname.length < 2 || state.nickname.length > 20) {
            _uiState.update { it.copy(error = RegisterError.NICKNAME_LENGTH) }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = RegisterError.PASSWORD_MISMATCH) }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(error = RegisterError.PASSWORD_TOO_SHORT) }
            return
        }
        if (!state.agreedToTerms) {
            _uiState.update { it.copy(error = RegisterError.AGREEMENT_REQUIRED) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.register(state.nickname, state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = RegisterError.EMPTY_FIELDS,
                        )
                    }
                }
        }
    }
}

data class RegisterUiState(
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val agreedToTerms: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK,
    val isLoading: Boolean = false,
    val error: RegisterError? = null,
    val registerSuccess: Boolean = false,
) {
    val passwordMismatch: Boolean
        get() = confirmPassword.isNotEmpty() && password != confirmPassword
}

enum class PasswordStrength(val level: Int) {
    WEAK(1),
    FAIR(2),
    MEDIUM(3),
    STRONG(4),
}

enum class RegisterError {
    EMPTY_FIELDS,
    NICKNAME_LENGTH,
    PASSWORD_MISMATCH,
    PASSWORD_TOO_SHORT,
    AGREEMENT_REQUIRED,
}
