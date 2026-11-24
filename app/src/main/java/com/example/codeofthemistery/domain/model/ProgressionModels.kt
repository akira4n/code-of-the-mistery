package com.example.codeofthemistery.domain.model

data class AchievementProgress(
    val id: String,
    val title: String,
    val description: String,
    val rewardXp: Int,
    val currentCount: Int,
    val targetCount: Int,
    val isUnlocked: Boolean,
    val iconKey: String
)

enum class SkillStatus {
    LOCKED,
    LEARNING,
    MASTERED
}

data class SkillNode(
    val id: String,
    val title: String,
    val topic: String,
    val chapterId: Int,
    val status: SkillStatus
)

data class ClueEntry(
    val clueId: String,
    val chapterId: Int,
    val title: String,
    val content: String,
    val discoveredAt: Long
)

sealed interface GameEvent {
    data object CodeChallengePassed : GameEvent
    data class ChapterCleared(val chapterId: Int, val hpRemaining: Int) : GameEvent
    data object VoucherRedeemed : GameEvent
}
