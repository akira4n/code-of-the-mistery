package com.example.codeofthemistery.domain.repository

import com.example.codeofthemistery.domain.model.AchievementProgress
import com.example.codeofthemistery.domain.model.ClueDefinition
import com.example.codeofthemistery.domain.model.ClueEntry
import com.example.codeofthemistery.domain.model.GameEvent
import com.example.codeofthemistery.domain.model.SkillNode
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeAchievements(userId: String): Flow<List<AchievementProgress>>
    fun observeClues(userId: String): Flow<List<ClueEntry>>
    fun observeSkillTree(userId: String): Flow<List<SkillNode>>
    suspend fun recordClue(userId: String, clue: ClueDefinition)
    suspend fun evaluateEvent(userId: String, event: GameEvent): List<AchievementProgress>
}
