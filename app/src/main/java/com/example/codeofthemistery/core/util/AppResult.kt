package com.example.codeofthemistery.core.util

/** Generic outcome wrapper for repository operations. */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val message: String) : AppResult<Nothing>
}
