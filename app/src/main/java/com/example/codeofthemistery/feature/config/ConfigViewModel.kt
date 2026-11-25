package com.example.codeofthemistery.feature.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.domain.model.User
import com.example.codeofthemistery.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConfigUiState(
    val user: User? = null,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val message: String? = null
)

class ConfigViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val messageFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ConfigUiState> = combine(
        authRepository.observeActiveUser(),
        authRepository.observeHapticsEnabled(),
        authRepository.observeSoundEnabled(),
        messageFlow
    ) { user, haptics, sound, msg ->
        ConfigUiState(
            user = user,
            hapticsEnabled = haptics,
            soundEnabled = sound,
            message = msg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConfigUiState())

    fun clearMessage() {
        messageFlow.value = null
    }

    fun updateName(newName: String) {
        val uid = uiState.value.user?.id ?: return
        viewModelScope.launch {
            when (val res = authRepository.updateProfileName(uid, newName)) {
                is AppResult.Success -> messageFlow.value = "Nama profil diperbarui."
                is AppResult.Error -> messageFlow.value = res.message
            }
        }
    }

    fun changePassword(currentPw: String, newPw: String) {
        val uid = uiState.value.user?.id ?: return
        viewModelScope.launch {
            when (val res = authRepository.changePassword(uid, currentPw, newPw)) {
                is AppResult.Success -> messageFlow.value = "Password berhasil diganti."
                is AppResult.Error -> messageFlow.value = res.message
            }
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        viewModelScope.launch { authRepository.setHapticsEnabled(enabled) }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }
}
