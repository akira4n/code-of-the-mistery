package com.example.codeofthemistery.domain.mechanics

import com.example.codeofthemistery.domain.model.AchievementProgress
import com.example.codeofthemistery.domain.model.GameEvent

/** Evaluates achievement counters from gameplay and redeem events. */
object AchievementEngine {

    data class AchievementUpdate(
        val updated: AchievementProgress,
        val newlyUnlocked: Boolean
    )

    fun applyEvent(
        existing: List<AchievementProgress>,
        event: GameEvent
    ): List<AchievementUpdate> {
        return existing.mapNotNull { item ->
            val increment = when (item.id) {
                "ach_var_1", "ach_var_2" -> if (event is GameEvent.CodeChallengePassed) 1 else 0
                "ach_bug_hunter" -> if (event is GameEvent.ChapterCleared && event.hpRemaining >= 5) 1 else 0
                "ach_algo_wizard" -> if (event is GameEvent.ChapterCleared && event.chapterId >= 2) 1 else 0
                "ach_first_redeem" -> if (event is GameEvent.VoucherRedeemed) 1 else 0
                else -> 0
            }
            if (increment <= 0 || item.isUnlocked) {
                null
            } else {
                val nextCount = (item.currentCount + increment).coerceAtMost(item.targetCount)
                val nowUnlocked = nextCount >= item.targetCount
                AchievementUpdate(
                    updated = item.copy(currentCount = nextCount, isUnlocked = nowUnlocked),
                    newlyUnlocked = nowUnlocked
                )
            }
        }
    }
}
