package com.example.codeofthemistery.feature.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.domain.evaluator.ExecutionResult
import com.example.codeofthemistery.domain.evaluator.MiniPythonEvaluator
import com.example.codeofthemistery.domain.mechanics.BattleCalculator
import com.example.codeofthemistery.domain.model.AchievementProgress
import com.example.codeofthemistery.domain.model.ChapterScript
import com.example.codeofthemistery.domain.model.EnemyAnimState
import com.example.codeofthemistery.domain.model.EnemyType
import com.example.codeofthemistery.domain.model.GameEvent
import com.example.codeofthemistery.domain.model.HeroAnimState
import com.example.codeofthemistery.domain.model.OwnedItem
import com.example.codeofthemistery.domain.model.SceneStep
import com.example.codeofthemistery.domain.repository.AchievementRepository
import com.example.codeofthemistery.domain.repository.AuthRepository
import com.example.codeofthemistery.domain.repository.ChapterRepository
import com.example.codeofthemistery.domain.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameplayUiState(
    val chapterId: Int = 1,
    val chapterTitle: String = "Chapter 1",
    val stepIndex: Int = 0,
    val totalSteps: Int = 1,
    val currentStep: SceneStep? = null,
    val hp: Int = BattleCalculator.MAX_HP,
    val maxHp: Int = BattleCalculator.MAX_HP,
    val hasLaptop: Boolean = true,
    val hasFirewallShield: Boolean = false,
    val shieldChargeUsed: Boolean = false,
    val equippedSlots: List<OwnedItem?> = listOf(null, null, null),
    val heroAnim: HeroAnimState = HeroAnimState.IDLE,
    val enemyAnim: EnemyAnimState = EnemyAnimState.IDLE,
    val forcePoints: Int = 0,
    val espritPoints: Int = 0,
    val questionsAnswered: Int = 0,
    val battleBanner: String? = null,
    val clueSavedToast: String? = null,
    val hapticsEnabled: Boolean = true,
    // Quiz state
    val selectedQuizOption: Int? = null,
    val quizPassed: Boolean = false,
    val quizFeedback: String? = null,
    // Code editor state
    val codeText: String = "",
    val executionResult: ExecutionResult? = null,
    val isShowingOutput: Boolean = false,
    val showHint: Boolean = false,
    val isEvaluating: Boolean = false,
    // Modals & end states
    val isPaused: Boolean = false,
    val lootRewardModalItem: OwnedItem? = null,
    val isCompleted: Boolean = false,
    val isGameOver: Boolean = false,
    val earnedXp: Int = 0,
    val earnedPoints: Int = 0,
    val newlyUnlockedAchievements: List<AchievementProgress> = emptyList(),
    val hapticTriggerCount: Int = 0
)

class GameplayViewModel(
    private val chapterId: Int,
    private val authRepository: AuthRepository,
    private val chapterRepository: ChapterRepository,
    private val inventoryRepository: InventoryRepository,
    private val achievementRepository: AchievementRepository,
    private val evaluator: MiniPythonEvaluator
) : ViewModel() {

    private val script: ChapterScript = chapterRepository.getChapterScript(chapterId)
    private val _uiState = MutableStateFlow(GameplayUiState())
    val uiState: StateFlow<GameplayUiState> = _uiState.asStateFlow()

    private var activeUserId: String? = null

    init {
        loadStepAt(0)
        observeSessionData()
    }

    private fun observeSessionData() {
        viewModelScope.launch {
            authRepository.observeHapticsEnabled().collectLatest { enabled ->
                _uiState.update { it.copy(hapticsEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            authRepository.observeActiveUser().collectLatest { user ->
                activeUserId = user?.id
                if (user != null) {
                    val currentStep = script.steps.getOrNull(_uiState.value.stepIndex)
                    if (currentStep is SceneStep.NarrationStep && currentStep.unlockClue != null) {
                        achievementRepository.recordClue(user.id, currentStep.unlockClue)
                    }
                    inventoryRepository.observeInventory(user.id).collectLatest { items ->
                        val slots = listOf(1, 2, 3).map { slot -> items.find { it.equippedSlot == slot && it.isUnlocked } }
                        val hasShield = slots.any { it?.itemId == "item_firewall_shield" }
                        val hasLaptop = slots.any { it?.itemId == "item_laptop" }
                        _uiState.update {
                            it.copy(
                                equippedSlots = slots,
                                hasFirewallShield = hasShield,
                                hasLaptop = hasLaptop
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadStepAt(index: Int) {
        val steps = script.steps
        if (index !in steps.indices) return
        val step = steps[index]

        val uid = activeUserId
        if (uid != null && step is SceneStep.NarrationStep && step.unlockClue != null) {
            viewModelScope.launch {
                achievementRepository.recordClue(uid, step.unlockClue)
            }
        }

        _uiState.update { state ->
            state.copy(
                chapterId = script.chapterId,
                chapterTitle = script.title,
                stepIndex = index,
                totalSteps = steps.size,
                currentStep = step,
                heroAnim = (step as? SceneStep.NarrationStep)?.heroAnim ?: HeroAnimState.IDLE,
                enemyAnim = when (step) {
                    is SceneStep.NarrationStep -> step.enemyAnim
                    is SceneStep.LootClaimStep -> EnemyAnimState.DEATH
                    else -> EnemyAnimState.IDLE
                },
                clueSavedToast = (step as? SceneStep.NarrationStep)?.unlockClue?.let { "Clue Baru Tersimpan: ${it.title}" },
                selectedQuizOption = null,
                quizPassed = false,
                quizFeedback = null,
                codeText = (step as? SceneStep.CodeBattleStep)?.starterCode ?: state.codeText,
                executionResult = null,
                isShowingOutput = false,
                showHint = false
            )
        }
    }

    fun advanceStep() {
        val currentIdx = _uiState.value.stepIndex
        if (currentIdx + 1 < script.steps.size) {
            loadStepAt(currentIdx + 1)
        }
    }

    fun submitQuizOption(index: Int) {
        val state = _uiState.value
        val step = state.currentStep as? SceneStep.QuizBattleStep ?: return
        if (state.quizPassed || state.isGameOver) return

        if (index == step.correctIndex) {
            _uiState.update {
                it.copy(
                    selectedQuizOption = index,
                    quizPassed = true,
                    quizFeedback = "Benar! ${step.explanation}",
                    heroAnim = HeroAnimState.SKILL_1,
                    enemyAnim = EnemyAnimState.IDLE,
                    espritPoints = it.espritPoints + 15,
                    questionsAnswered = it.questionsAnswered + 1,
                    battleBanner = "Serangan Logika Mengenai Target! (+15 ESPRIT)"
                )
            }
            viewModelScope.launch {
                delay(850)
                _uiState.update { it.copy(heroAnim = HeroAnimState.IDLE) }
            }
        } else {
            applyBattleDamage(
                isBoss = step.isBoss,
                errorBanner = "Jawaban kurang tepat!",
                onUpdate = { res, banner ->
                    _uiState.update {
                        it.copy(
                            selectedQuizOption = index,
                            quizFeedback = "Salah — $banner",
                            hp = res.newHp,
                            shieldChargeUsed = it.shieldChargeUsed || res.shieldConsumedCharge,
                            isGameOver = res.isDead,
                            heroAnim = if (res.isDead) HeroAnimState.DEATH else HeroAnimState.IDLE,
                            enemyAnim = EnemyAnimState.ATTACK,
                            battleBanner = banner,
                            hapticTriggerCount = it.hapticTriggerCount + 1
                        )
                    }
                }
            )
        }
    }

    fun updateCode(newCode: String) {
        _uiState.update { it.copy(codeText = newCode) }
    }

    fun resetStarterCode() {
        val step = _uiState.value.currentStep as? SceneStep.CodeBattleStep ?: return
        _uiState.update { it.copy(codeText = step.starterCode, executionResult = null, isShowingOutput = false) }
    }

    fun toggleHint() {
        _uiState.update { it.copy(showHint = !it.showHint) }
    }

    fun switchBackToEditor() {
        _uiState.update { it.copy(isShowingOutput = false) }
    }

    fun runPythonCode() {
        val state = _uiState.value
        val step = state.currentStep as? SceneStep.CodeBattleStep ?: return
        if (state.isEvaluating || state.isGameOver) return

        viewModelScope.launch {
            _uiState.update { it.copy(isEvaluating = true) }
            val result = withContext(Dispatchers.Default) {
                evaluator.evaluate(state.codeText, step.validationRule)
            }

            if (result.isRulePassed) {
                val uid = activeUserId
                val unlocked = if (uid != null) {
                    achievementRepository.evaluateEvent(uid, GameEvent.CodeChallengePassed)
                } else emptyList()

                _uiState.update {
                    it.copy(
                        isEvaluating = false,
                        executionResult = result,
                        isShowingOutput = true,
                        heroAnim = HeroAnimState.SKILL_2,
                        enemyAnim = if (step.enemyType != EnemyType.NONE) EnemyAnimState.DEATH else EnemyAnimState.IDLE,
                        forcePoints = it.forcePoints + 25,
                        questionsAnswered = it.questionsAnswered + 1,
                        battleBanner = "Kode Valid! Exit Code 0 (+25 FORCE)",
                        newlyUnlockedAchievements = it.newlyUnlockedAchievements + unlocked
                    )
                }
            } else {
                applyBattleDamage(
                    isBoss = step.isBoss,
                    errorBanner = result.errorMessage ?: "Kode belum memenuhi target",
                    onUpdate = { dmgRes, banner ->
                        _uiState.update {
                            it.copy(
                                isEvaluating = false,
                                executionResult = result,
                                isShowingOutput = true,
                                hp = dmgRes.newHp,
                                shieldChargeUsed = it.shieldChargeUsed || dmgRes.shieldConsumedCharge,
                                isGameOver = dmgRes.isDead,
                                heroAnim = if (dmgRes.isDead) HeroAnimState.DEATH else HeroAnimState.IDLE,
                                enemyAnim = if (step.enemyType != EnemyType.NONE) EnemyAnimState.ATTACK else EnemyAnimState.IDLE,
                                battleBanner = banner,
                                hapticTriggerCount = it.hapticTriggerCount + 1
                            )
                        }
                    }
                )
            }
        }
    }

    private fun applyBattleDamage(
        isBoss: Boolean,
        errorBanner: String,
        onUpdate: (BattleCalculator.DamageResult, String) -> Unit
    ) {
        val state = _uiState.value
        val dmg = BattleCalculator.applyWrongAnswer(
            currentHp = state.hp,
            isBossBattle = isBoss,
            hasFirewallShield = state.hasFirewallShield,
            shieldChargeUsed = state.shieldChargeUsed
        )
        val banner = when {
            dmg.shieldBlocked -> "Firewall Shield menyerap error! (0 Damage)"
            dmg.isDead -> "HP habis (0/6)! Sistem mengalami Crash."
            else -> "$errorBanner (-${dmg.damageTaken} HP)"
        }
        onUpdate(dmg, banner)
    }

    fun useQuickItem(item: OwnedItem) {
        val state = _uiState.value
        if (state.isGameOver || state.isCompleted) return
        if (item.itemId == "item_syntax_potion") {
            if (state.hp >= state.maxHp) {
                _uiState.update { it.copy(battleBanner = "HP sudah penuh (6/6)!") }
                return
            }
            val uid = activeUserId ?: return
            viewModelScope.launch {
                when (val res = inventoryRepository.consumeItem(uid, item.itemId)) {
                    is AppResult.Success -> {
                        val healed = BattleCalculator.applySyntaxPotion(state.hp)
                        _uiState.update {
                            it.copy(
                                hp = healed,
                                battleBanner = "Syntax Potion digunakan! HP pulih menjadi $healed/6"
                            )
                        }
                    }
                    is AppResult.Error -> _uiState.update { it.copy(battleBanner = res.message) }
                }
            }
        } else if (item.itemId == "item_laptop") {
            if (state.currentStep is SceneStep.CodeBattleStep) toggleHint()
            else _uiState.update { it.copy(battleBanner = "Laptop aktif: Editor MAIN.PY & Hint siap!") }
        } else if (item.itemId == "item_firewall_shield") {
            val status = if (state.shieldChargeUsed) "Charge penahan pertama sudah terpakai di chapter ini."
            else "Firewall Shield siap memblokir 1 error pertama!"
            _uiState.update { it.copy(battleBanner = status) }
        }
    }

    fun claimLootAndComplete() {
        val uid = activeUserId
        val hpRemaining = _uiState.value.hp
        viewModelScope.launch {
            val dropped = if (uid != null) inventoryRepository.grantLootByHp(uid, hpRemaining) else null
            _uiState.update { it.copy(lootRewardModalItem = dropped) }
            if (dropped == null) {
                finalizeChapterCompletion()
            }
        }
    }

    fun dismissLootAndFinish() {
        _uiState.update { it.copy(lootRewardModalItem = null) }
        finalizeChapterCompletion()
    }

    private fun finalizeChapterCompletion() {
        val state = _uiState.value
        if (state.isCompleted) return
        val uid = activeUserId
        viewModelScope.launch {
            val unlocked = if (uid != null) {
                chapterRepository.completeChapter(
                    userId = uid,
                    chapterId = script.chapterId,
                    hpRemaining = state.hp,
                    questionsAnswered = state.questionsAnswered,
                    earnedXp = script.rewardXp,
                    earnedPoints = script.rewardPoints
                )
                achievementRepository.evaluateEvent(
                    uid,
                    GameEvent.ChapterCleared(script.chapterId, state.hp)
                )
            } else emptyList()

            _uiState.update {
                it.copy(
                    isCompleted = true,
                    earnedXp = script.rewardXp,
                    earnedPoints = script.rewardPoints,
                    newlyUnlockedAchievements = (it.newlyUnlockedAchievements + unlocked).distinctBy { a -> a.id }
                )
            }
        }
    }

    fun togglePause(paused: Boolean) {
        _uiState.update { it.copy(isPaused = paused) }
    }

    fun retryChapter() {
        _uiState.update {
            GameplayUiState(
                hasLaptop = it.hasLaptop,
                hasFirewallShield = it.hasFirewallShield,
                equippedSlots = it.equippedSlots,
                hapticsEnabled = it.hapticsEnabled
            )
        }
        loadStepAt(0)
    }
}
