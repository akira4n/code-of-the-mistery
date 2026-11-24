package com.example.codeofthemistery.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionPreferences(private val context: Context) {

    private object Keys {
        val ACTIVE_USER_ID = stringPreferencesKey("active_user_id")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val LAST_PLAYED_CHAPTER_ID = intPreferencesKey("last_played_chapter_id")
    }

    val activeUserIdFlow: Flow<String?> = context.dataStore.data.map { it[Keys.ACTIVE_USER_ID] }
    val hapticsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.HAPTICS_ENABLED] ?: true }
    val soundEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.SOUND_ENABLED] ?: true }
    val lastChapterIdFlow: Flow<Int> = context.dataStore.data.map { it[Keys.LAST_PLAYED_CHAPTER_ID] ?: 1 }

    suspend fun getActiveUserId(): String? = activeUserIdFlow.first()

    suspend fun setActiveUserId(userId: String?) {
        context.dataStore.edit { prefs ->
            if (userId == null) prefs.remove(Keys.ACTIVE_USER_ID)
            else prefs[Keys.ACTIVE_USER_ID] = userId
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setLastChapterId(chapterId: Int) {
        context.dataStore.edit { it[Keys.LAST_PLAYED_CHAPTER_ID] = chapterId }
    }
}
