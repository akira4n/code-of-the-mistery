package com.example.codeofthemistery.domain.model

enum class ChapterStatus {
    LOCKED,
    UNLOCKED,
    COMPLETED
}

enum class EnemyType {
    NONE,
    DRONE,
    GOLEM
}

enum class HeroAnimState {
    IDLE,
    RUN,
    SKILL_1,
    SKILL_2,
    DEATH
}

enum class EnemyAnimState {
    IDLE,
    ATTACK,
    DEATH
}

enum class BackgroundKey {
    STORY_ERROR,
    DOKJA_STREET
}

data class ChapterSummary(
    val id: Int,
    val title: String,
    val subtitle: String,
    val topicLabel: String,
    val status: ChapterStatus,
    val isPlayable: Boolean,
    val rewardXp: Int,
    val rewardPoints: Int,
    val bestHpRemaining: Int = 0,
    val questionsAnswered: Int = 0
)

data class ClueDefinition(
    val id: String,
    val chapterId: Int,
    val title: String,
    val content: String
)

/** Validation constraints checked after running user Python code in [MiniPythonEvaluator]. */
data class CodeValidationRule(
    val requiredKeywords: List<String> = emptyList(),
    val nonEmptyStringVars: List<String> = emptyList(),
    val expectedVars: Map<String, Any> = emptyMap(),
    val expectedStdoutContains: List<String> = emptyList(),
    val minStdoutLines: Int = 0
)

sealed interface SceneStep {
    val id: String
    val backgroundKey: BackgroundKey
    val enemyType: EnemyType

    data class NarrationStep(
        override val id: String,
        val speaker: String,
        val text: String,
        override val backgroundKey: BackgroundKey = BackgroundKey.DOKJA_STREET,
        val heroAnim: HeroAnimState = HeroAnimState.IDLE,
        override val enemyType: EnemyType = EnemyType.NONE,
        val enemyAnim: EnemyAnimState = EnemyAnimState.IDLE,
        val systemBubbleText: String? = null,
        val unlockClue: ClueDefinition? = null
    ) : SceneStep

    data class QuizBattleStep(
        override val id: String,
        val prompt: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String,
        override val backgroundKey: BackgroundKey = BackgroundKey.DOKJA_STREET,
        override val enemyType: EnemyType = EnemyType.DRONE,
        val isBoss: Boolean = false
    ) : SceneStep

    data class CodeBattleStep(
        override val id: String,
        val title: String,
        val lessonSummary: String,
        val starterCode: String,
        val hintText: String,
        val validationRule: CodeValidationRule,
        override val backgroundKey: BackgroundKey = BackgroundKey.DOKJA_STREET,
        override val enemyType: EnemyType = EnemyType.GOLEM,
        val isBoss: Boolean = false
    ) : SceneStep

    data class LootClaimStep(
        override val id: String,
        val message: String,
        override val backgroundKey: BackgroundKey = BackgroundKey.DOKJA_STREET,
        override val enemyType: EnemyType = EnemyType.GOLEM
    ) : SceneStep
}

data class ChapterScript(
    val chapterId: Int,
    val title: String,
    val subtitle: String,
    val rewardXp: Int,
    val rewardPoints: Int,
    val steps: List<SceneStep>
)
