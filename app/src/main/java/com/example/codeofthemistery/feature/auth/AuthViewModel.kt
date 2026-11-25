package com.example.codeofthemistery.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val forgotStep: Int = 1,
    val resetEmail: String = "",
    val simulatedOtpHint: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    suspend fun hasActiveSession(): Boolean = authRepository.hasActiveSession()

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val res = authRepository.login(email, password)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = res.message) }
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Konfirmasi password tidak cocok.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val res = authRepository.register(name, email, password)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = res.message) }
                }
            }
        }
    }

    fun requestResetOtp(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val res = authRepository.requestPasswordResetOtp(email)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            forgotStep = 2,
                            resetEmail = email.trim(),
                            simulatedOtpHint = res.data
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = res.message) }
                }
            }
        }
    }

    fun verifyOtpAndProceed(otpInput: String) {
        val expected = _uiState.value.simulatedOtpHint
        if (otpInput.trim().length != 6 || otpInput.trim() != expected) {
            _uiState.update { it.copy(errorMessage = "Kode OTP tidak sesuai. Cek kode simulasi di atas.") }
            return
        }
        _uiState.update { it.copy(forgotStep = 3, errorMessage = null) }
    }

    fun submitNewPassword(otp: String, newPassword: String, confirmNewPassword: String, onSuccess: () -> Unit) {
        if (newPassword != confirmNewPassword) {
            _uiState.update { it.copy(errorMessage = "Konfirmasi password baru tidak cocok.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val res = authRepository.resetPassword(_uiState.value.resetEmail, otp, newPassword)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            forgotStep = 1,
                            simulatedOtpHint = null,
                            infoMessage = "Password berhasil diperbarui. Silakan login."
                        )
                    }
                    onSuccess()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = res.message) }
                }
            }
        }
    }

    fun resetForgotFlow() {
        _uiState.update { it.copy(forgotStep = 1, simulatedOtpHint = null, errorMessage = null) }
    }
}
