package com.example.codeofthemistery.domain.repository

import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeActiveUser(): Flow<User?>
    fun observeHapticsEnabled(): Flow<Boolean>
    fun observeSoundEnabled(): Flow<Boolean>

    suspend fun hasActiveSession(): Boolean
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun register(name: String, email: String, password: String): AppResult<User>
    suspend fun requestPasswordResetOtp(email: String): AppResult<String>
    suspend fun resetPassword(email: String, otp: String, newPassword: String): AppResult<Unit>
    suspend fun updateProfileName(userId: String, newName: String): AppResult<Unit>
    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): AppResult<Unit>
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun logout()
}
