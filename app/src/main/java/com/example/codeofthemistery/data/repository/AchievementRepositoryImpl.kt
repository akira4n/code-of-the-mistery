package com.example.codeofthemistery.data.repository

import com.example.codeofthemistery.data.local.dao.AchievementDao
import com.example.codeofthemistery.data.local.dao.ChapterDao
import com.example.codeofthemistery.data.local.dao.UserDao
import com.example.codeofthemistery.data.local.entity.UserAchievementEntity
import com.example.codeofthemistery.data.local.entity.UserClueEntity
import com.example.codeofthemistery.data.local.seed.CatalogSeeder
import com.example.codeofthemistery.domain.mechanics.AchievementEngine
import com.example.codeofthemistery.domain.mechanics.LevelCalculator
import com.example.codeofthemistery.domain.model.AchievementProgress
import com.example.codeofthemistery.domain.model.ChapterStatus
import com.example.codeofthemistery.domain.model.ClueDefinition
import com.example.codeofthemistery.domain.model.ClueEntry
import com.example.codeofthemistery.domain.model.GameEvent
import com.example.codeofthemistery.domain.model.SkillNode
import com.example.codeofthemistery.domain.model.SkillStatus
import com.example.codeofthemistery.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AchievementRepositoryImpl(
    private val achievementDao: AchievementDao,
    private val chapterDao: ChapterDao,
    private val userDao: UserDao,
    private val catalogSeeder: CatalogSeeder
) : AchievementRepository {

    override fun observeAchievements(userId: String): Flow<List<AchievementProgress>> =
        achievementDao.observeAchievements(userId).map { rows ->
            rows.map { row ->
                AchievementProgress(
                    id = row.id,
                    title = row.title,
                    description = row.description,
                    rewardXp = row.rewardXp,
                    currentCount = row.currentCount ?: 0,
                    targetCount = row.targetCount,
                    isUnlocked = row.isUnlocked ?: false,
                    iconKey = row.iconKey
                )
            }
        }

    override fun observeClues(userId: String): Flow<List<ClueEntry>> =
        achievementDao.observeClues(userId).map { rows ->
            rows.map {
                ClueEntry(
                    clueId = it.clueId,
                    chapterId = it.chapterId,
                    title = it.title,
                    content = it.content,
                    discoveredAt = it.discoveredAt
                )
            }
        }

    override fun observeSkillTree(userId: String): Flow<List<SkillNode>> =
        chapterDao.observeProgress(userId).map { progressList ->
            val statusByChapter = progressList.associate { it.chapterId to it.status }
            fun skillStatusFor(chapterId: Int): SkillStatus = when (statusByChapter[chapterId]) {
                ChapterStatus.COMPLETED.name -> SkillStatus.MASTERED
                ChapterStatus.UNLOCKED.name -> SkillStatus.LEARNING
                else -> SkillStatus.LOCKED
            }
            listOf(
                SkillNode("skill_vars", "Syntax & Variables", "Variabel, String, Int, Float, Boolean", 1, skillStatusFor(1)),
                SkillNode("skill_cond", "Conditional Logic", "Operator Perbandingan & if / elif / else", 2, skillStatusFor(2)),
                SkillNode("skill_loop", "Loops & Functions", "Perulangan for/while & def fungsi", 3, skillStatusFor(3)),
                SkillNode("skill_oop", "OOP & Data Structures", "List, Dictionary, Class & Object (Roadmap)", 4, skillStatusFor(4))
            )
        }

    override suspend fun recordClue(userId: String, clue: ClueDefinition) {
        achievementDao.insertClueIfNew(
            UserClueEntity(
                userId = userId,
                clueId = clue.id,
                chapterId = clue.chapterId,
                title = clue.title,
                content = clue.content,
                discoveredAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun evaluateEvent(userId: String, event: GameEvent): List<AchievementProgress> {
        catalogSeeder.ensureSeeded()
        val current = observeAchievements(userId).first()
        val updates = AchievementEngine.applyEvent(current, event)
        if (updates.isEmpty()) return emptyList()

        val now = System.currentTimeMillis()
        achievementDao.upsertUserAchievements(
            updates.map { upd ->
                UserAchievementEntity(
                    userId = userId,
                    achievementId = upd.updated.id,
                    currentCount = upd.updated.currentCount,
                    isUnlocked = upd.updated.isUnlocked,
                    unlockedAt = if (upd.newlyUnlocked) now else null
                )
            }
        )

        val bonusXp = updates.filter { it.newlyUnlocked }.sumOf { it.updated.rewardXp }
        if (bonusXp > 0) {
            userDao.getById(userId)?.let { user ->
                val lvl = LevelCalculator.addXp(user.level, user.currentXp, bonusXp)
                userDao.updateProgression(userId, lvl.level, lvl.currentXp, addedPoints = 0)
            }
        }
        return updates.filter { it.newlyUnlocked }.map { it.updated }
    }
}
