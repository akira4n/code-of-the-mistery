package com.example.codeofthemistery.data.repository

import com.example.codeofthemistery.data.local.dao.ChapterDao
import com.example.codeofthemistery.data.local.dao.InventoryDao
import com.example.codeofthemistery.data.local.dao.UserDao
import com.example.codeofthemistery.data.local.datastore.SessionPreferences
import com.example.codeofthemistery.data.local.entity.ChapterProgressEntity
import com.example.codeofthemistery.data.local.seed.AppSeedData
import com.example.codeofthemistery.domain.mechanics.LevelCalculator
import com.example.codeofthemistery.domain.model.AlignmentChoice
import com.example.codeofthemistery.domain.model.AlignmentSummary
import com.example.codeofthemistery.domain.model.ChapterScript
import com.example.codeofthemistery.domain.model.ChapterStatus
import com.example.codeofthemistery.domain.model.ChapterSummary
import com.example.codeofthemistery.domain.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.max
import kotlin.math.roundToInt

class ChapterRepositoryImpl(
    private val chapterDao: ChapterDao,
    private val userDao: UserDao,
    private val inventoryDao: InventoryDao,
    private val sessionPreferences: SessionPreferences
) : ChapterRepository {

    override fun observeChapters(userId: String): Flow<List<ChapterSummary>> =
        chapterDao.observeProgress(userId).map { rows ->
            val byId = rows.associateBy { it.chapterId }
            AppSeedData.chaptersMeta.map { meta ->
                val row = byId[meta.id]
                val status = row?.status?.let { runCatching { ChapterStatus.valueOf(it) }.getOrNull() }
                    ?: if (meta.id == 1) ChapterStatus.UNLOCKED else ChapterStatus.LOCKED
                val alignment = row?.lastAlignmentChoice?.let { runCatching { AlignmentChoice.valueOf(it) }.getOrNull() }
                ChapterSummary(
                    id = meta.id,
                    title = meta.title,
                    subtitle = meta.subtitle,
                    topicLabel = meta.topicLabel,
                    status = status,
                    isPlayable = meta.isPlayable,
                    rewardXp = meta.rewardXp,
                    rewardPoints = meta.rewardPoints,
                    bestHpRemaining = row?.bestHpRemaining ?: 0,
                    questionsAnswered = row?.questionsAnswered ?: 0,
                    lastAlignmentChoice = alignment
                )
            }
        }

    override fun observeAlignmentSummary(userId: String): Flow<AlignmentSummary> =
        chapterDao.observeProgress(userId).map { rows ->
            val patch = rows.count { it.lastAlignmentChoice == AlignmentChoice.PATCH.name }
            val purge = rows.count { it.lastAlignmentChoice == AlignmentChoice.PURGE.name }
            AlignmentSummary(patchCount = patch, purgeCount = purge)
        }

    override fun getChapterScript(chapterId: Int): ChapterScript = AppSeedData.getChapterScript(chapterId)

    override suspend fun completeChapter(
        userId: String,
        chapterId: Int,
        hpRemaining: Int,
        questionsAnswered: Int,
        earnedXp: Int,
        earnedPoints: Int,
        alignmentChoice: AlignmentChoice?
    ) {
        val now = System.currentTimeMillis()
        val current = chapterDao.getChapterProgress(userId, chapterId)
        chapterDao.upsert(
            ChapterProgressEntity(
                userId = userId,
                chapterId = chapterId,
                status = ChapterStatus.COMPLETED.name,
                bestHpRemaining = max(current?.bestHpRemaining ?: 0, hpRemaining),
                questionsAnswered = max(current?.questionsAnswered ?: 0, questionsAnswered),
                updatedAt = now,
                lastAlignmentChoice = alignmentChoice?.name ?: current?.lastAlignmentChoice
            )
        )

        val nextChapterId = chapterId + 1
        val nextMeta = AppSeedData.chaptersMeta.find { it.id == nextChapterId }
        if (nextMeta != null && nextMeta.isPlayable) {
            val nextRow = chapterDao.getChapterProgress(userId, nextChapterId)
            if (nextRow == null || nextRow.status == ChapterStatus.LOCKED.name) {
                chapterDao.upsert(
                    ChapterProgressEntity(
                        userId = userId,
                        chapterId = nextChapterId,
                        status = ChapterStatus.UNLOCKED.name,
                        bestHpRemaining = nextRow?.bestHpRemaining ?: 0,
                        questionsAnswered = nextRow?.questionsAnswered ?: 0,
                        updatedAt = now
                    )
                )
            }
            sessionPreferences.setLastChapterId(nextChapterId)
        }

        val equippedIds = inventoryDao.getUserItems(userId)
            .filter { it.equippedSlot != null && it.isUnlocked }
            .map { it.itemId }
            .toSet()

        val finalXp = if ("item_debugger_visor" in equippedIds) (earnedXp * 1.15).roundToInt() else earnedXp
        val finalPoints = if ("item_overclock_core" in equippedIds) earnedPoints + 250 else earnedPoints

        val user = userDao.getById(userId) ?: return
        val nextLevel = LevelCalculator.addXp(user.level, user.currentXp, finalXp)
        userDao.updateProgression(
            userId = userId,
            level = nextLevel.level,
            xp = nextLevel.currentXp,
            addedPoints = finalPoints
        )
    }
}
