package com.example.codeofthemistery.data.remote

import kotlinx.serialization.Serializable

/**
 * Phase 2 API contract prepared for future Express.js + JWT backend integration.
 * Repositories in `data/repository/` can swap local Room/DataStore calls with this client
 * without altering any Domain or Presentation code.
 */
@Serializable
data class LoginRequestDto(val email: String, val password: String)

@Serializable
data class RegisterRequestDto(val name: String, val email: String, val password: String)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String? = null,
    val userId: String,
    val name: String,
    val email: String
)

interface ExpressApiService {
    suspend fun login(request: LoginRequestDto): AuthResponseDto
    suspend fun register(request: RegisterRequestDto): AuthResponseDto
}
