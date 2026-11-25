package com.example.codeofthemistery.feature.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.domain.evaluator.ExecutionResult
import com.example.codeofthemistery.domain.evaluator.MiniPythonEvaluator
import com.example.codeofthemistery.domain.mechanics.BattleCalculator
import com.example.codeofthemistery.domain.model.AchievementProgress
import com.example.codeofthemistery.domain.model.AlignmentChoice
import com.example.codeofthemistery.domain.model.BranchOption
import com.example.codeofthemistery.domain.model.ChapterScript
import com.example.codeofthemistery.domain.model.EnemyAnimState
import com.example.codeofthemistery.domain.model.EnemyType
import com.example.codeofthemistery.domain.model.GameEvent
import com.example.codeofthemistery.domain.model.HelpMateriInfo
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
import kotlin.math.max

data class FloatingCombatText(
    val id: Int,
    val text: String,
    val isPositive: Boolean
)

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
    val enemyName: String? = null,
    val enemyHp: Int = 0,
    val enemyMaxHp: Int = 0,
    val enemyStatusLabel: String? = null,
    val forcePoints: Int = 0,
    val espritPoints: Int = 0,
    val questionsAnswered: Int = 0,
    val battleBanner: String? = null,
    val clueSavedToast: String? = null,
    val hapticsEnabled: Boolean = true,
    // Branching & alignment
    val selectedAlignment: AlignmentChoice? = null,
    val secretClueUnlocked: Boolean = false,
    val bossWeakened: Boolean = false,
    val branchSelectedOption: BranchOption? = null,
    // Bug hunt
    val selectedBugLine: Int? = null,
    val bugHuntPassed: Boolean = false,
    val bugHuntFeedback: String? = null,
    // Code chips
    val selectedChips: List<String?> = emptyList(),
    val chipsPassed: Boolean = false,
    val chipsFeedback: String? = null,
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
    // Stage FX triggers
    val heroLungeTrigger: Int = 0,
    val heroHitTrigger: Int = 0,
    val enemyHitTrigger: Int = 0,
    val attackVfxTrigger: Int = 0,
    val screenShakeTrigger: Int = 0,
    val floatingCombatText: FloatingCombatText? = null,
    // Help Materi ("?")
    val activeHelpMateri: HelpMateriInfo? = null,
    // Modals & end states
    val isPaused: Boolean = false,
    val lootRewardModalItem: OwnedItem? = null,
    val isCompleted: Boolean = false,
    val isGameOver: Boolean = false,
    val earnedXp: Int = 0,
    val earnedPoints: Int = 0,
    val cliffhangerText: String = "",
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
    private var combatTextSeq = 0

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
            val prevStep = state.currentStep
            val (nextMaxHp, nextEnemyName) = resolveEnemyMeta(step)
            val isSameEnemy = prevStep != null &&
                prevStep.enemyType == step.enemyType &&
                step.enemyType != EnemyType.NONE &&
                state.enemyMaxHp == nextMaxHp &&
                nextMaxHp > 0

            val nextEnemyHp = when {
                step is SceneStep.LootClaimStep -> 0
                nextMaxHp <= 0 -> 0
                isSameEnemy && state.enemyHp > 0 -> state.enemyHp
                else -> nextMaxHp
            }

            val nextStatusLabel = when {
                step is SceneStep.LootClaimStep -> "DEFEATED"
                nextMaxHp <= 0 -> null
                isSameEnemy -> state.enemyStatusLabel
                step.enemyType == EnemyType.GOLEM -> if (state.bossWeakened) "WEAKENED" else "SHIELDED"
                else -> null
            }

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
                enemyName = nextEnemyName,
                enemyHp = nextEnemyHp,
                enemyMaxHp = nextMaxHp,
                enemyStatusLabel = nextStatusLabel,
                clueSavedToast = (step as? SceneStep.NarrationStep)?.unlockClue?.let { "Clue Tersimpan: ${it.title}" },
                battleBanner = (step as? SceneStep.NarrationStep)?.let { null } ?: state.battleBanner,
                // Reset step-scoped controls
                branchSelectedOption = null,
                selectedBugLine = null,
                bugHuntPassed = false,
                bugHuntFeedback = null,
                selectedChips = if (step is SceneStep.CodeChipsStep) List(step.expectedChipsInOrder.size) { null } else emptyList(),
                chipsPassed = false,
                chipsFeedback = null,
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

    private fun resolveEnemyMeta(step: SceneStep): Pair<Int, String?> = when (step) {
        is SceneStep.NarrationStep -> step.enemyMaxHp to step.enemyName
        is SceneStep.BranchChoiceStep -> 0 to "Vex (\"Null\")"
        is SceneStep.BugHuntStep -> step.enemyMaxHp to step.enemyName
        is SceneStep.CodeChipsStep -> step.enemyMaxHp to step.enemyName
        is SceneStep.QuizBattleStep -> step.enemyMaxHp to step.enemyName
        is SceneStep.CodeBattleStep -> step.enemyMaxHp to step.enemyName
        is SceneStep.LootClaimStep -> step.enemyMaxHp to step.enemyName
    }

    fun advanceStep() {
        val currentIdx = _uiState.value.stepIndex
        if (currentIdx + 1 < script.steps.size) {
            loadStepAt(currentIdx + 1)
        }
    }

    fun openHelpMateri(info: HelpMateriInfo) {
        _uiState.update { it.copy(activeHelpMateri = info) }
    }

    fun dismissHelpMateri() {
        _uiState.update { it.copy(activeHelpMateri = null) }
    }

    fun selectBranchOption(option: BranchOption) {
        val state = _uiState.value
        if (state.branchSelectedOption != null || state.isGameOver) return

        val uid = activeUserId
        if (uid != null && option.unlockClue != null) {
            viewModelScope.launch {
                achievementRepository.recordClue(uid, option.unlockClue)
            }
        }

        _uiState.update {
            it.copy(
                branchSelectedOption = option,
                selectedAlignment = option.alignment,
                secretClueUnlocked = it.secretClueUnlocked || (option.unlockClue != null),
                bossWeakened = it.bossWeakened || option.weakensBoss,
                forcePoints = it.forcePoints + option.bonusForce,
                clueSavedToast = option.unlockClue?.let { clue -> "Secret Clue Tersimpan: ${clue.title}" },
                battleBanner = option.consequenceBadge,
                floatingCombatText = if (option.bonusForce > 0) {
                    FloatingCombatText(++combatTextSeq, "+${option.bonusForce} FORCE", isPositive = true)
                } else null
            )
        }
    }

    fun submitBugHuntLine(lineIndex: Int) {
        val state = _uiState.value
        val step = state.currentStep as? SceneStep.BugHuntStep ?: return
        if (state.bugHuntPassed || state.isGameOver) return

        if (lineIndex == step.bugLineIndex) {
            val nextEnemyHp = max(0, state.enemyHp - step.damageToEnemy)
            _uiState.update {
                it.copy(
                    selectedBugLine = lineIndex,
                    bugHuntPassed = true,
                    bugHuntFeedback = "Tepat! ${step.explanation}",
                    heroAnim = HeroAnimState.SKILL_1,
                    enemyAnim = if (nextEnemyHp == 0) EnemyAnimState.DEATH else EnemyAnimState.IDLE,
                    enemyHp = nextEnemyHp,
                    enemyHitTrigger = it.enemyHitTrigger + 1,
                    heroLungeTrigger = it.heroLungeTrigger + 1,
                    attackVfxTrigger = it.attackVfxTrigger + 1,
                    floatingCombatText = FloatingCombatText(++combatTextSeq, "-${step.damageToEnemy} DMG", isPositive = true),
                    espritPoints = it.espritPoints + 15,
                    questionsAnswered = it.questionsAnswered + 1,
                    battleBanner = "Bug Ditemukan! -${step.damageToEnemy} HP ke ${step.enemyName} (+15 ESPRIT)"
                )
            }
            scheduleHeroIdleReset()
        } else {
            applyBattleDamage(
                isBoss = step.isBoss,
                errorBanner = "Baris ${lineIndex + 1} valid — bukan penyebab error!"
            ) { res, banner ->
                _uiState.update {
                    it.copy(
                        selectedBugLine = lineIndex,
                        bugHuntFeedback = "Bukan baris ini — $banner",
                        hp = res.newHp,
                        shieldChargeUsed = it.shieldChargeUsed || res.shieldConsumedCharge,
                        isGameOver = res.isDead,
                        heroAnim = if (res.isDead) HeroAnimState.DEATH else HeroAnimState.IDLE,
                        enemyAnim = EnemyAnimState.ATTACK,
                        heroHitTrigger = it.heroHitTrigger + 1,
                        screenShakeTrigger = it.screenShakeTrigger + 1,
                        floatingCombatText = FloatingCombatText(
                            ++combatTextSeq,
                            if (res.shieldBlocked) "BLOCKED!" else "-${res.damageTaken} HP",
                            isPositive = false
                        ),
                        battleBanner = banner,
                        hapticTriggerCount = it.hapticTriggerCount + 1
                    )
                }
            }
        }
    }

    fun selectCodeChip(chip: String) {
        val state = _uiState.value
        val step = state.currentStep as? SceneStep.CodeChipsStep ?: return
        if (state.chipsPassed || state.isGameOver) return

        val firstEmpty = state.selectedChips.indexOfFirst { it == null }
        if (firstEmpty >= 0) {
            val next = state.selectedChips.toMutableList().apply { this[firstEmpty] = chip }
            _uiState.update { it.copy(selectedChips = next, chipsFeedback = null) }
        }
    }

    fun clearCodeChipSlot(slotIndex: Int) {
        val state = _uiState.value
        if (state.chipsPassed || slotIndex !in state.selectedChips.indices) return
        val next = state.selectedChips.toMutableList().apply { this[slotIndex] = null }
        _uiState.update { it.copy(selectedChips = next, chipsFeedback = null) }
    }

    fun submitCodeChips() {
        val state = _uiState.value
        val step = state.currentStep as? SceneStep.CodeChipsStep ?: return
        if (state.chipsPassed || state.isGameOver) return

        if (state.selectedChips.any { it == null }) {
            _uiState.update { it.copy(chipsFeedback = "Isi semua slot kode terlebih dahulu!") }
            return
        }

        if (state.selectedChips == step.expectedChipsInOrder) {
            val nextEnemyHp = max(0, state.enemyHp - step.damageToEnemy)
            _uiState.update {
                it.copy(
                    chipsPassed = true,
                    chipsFeedback = "Benar! ${step.explanation}",
                    heroAnim = HeroAnimState.SKILL_1,
                    enemyAnim = if (nextEnemyHp == 0) EnemyAnimState.DEATH else EnemyAnimState.IDLE,
                    enemyHp = nextEnemyHp,
                    enemyStatusLabel = if (nextEnemyHp == 0) "DEFEATED" else "EXPOSED",
                    heroLungeTrigger = it.heroLungeTrigger + 1,
                    enemyHitTrigger = it.enemyHitTrigger + 1,
                    attackVfxTrigger = it.attackVfxTrigger + 1,
                    screenShakeTrigger = it.screenShakeTrigger + 1,
                    floatingCombatText = FloatingCombatText(++combatTextSeq, "-${step.damageToEnemy} CRACK!", isPositive = true),
                    espritPoints = it.espritPoints + 20,
                    questionsAnswered = it.questionsAnswered + 1,
                    battleBanner = "Perisai ${step.enemyName} Retak! (+20 ESPRIT)"
                )
            }
            scheduleHeroIdleReset()
        } else {
            applyBattleDamage(
                isBoss = step.isBoss,
                errorBanner = "Kombinasi chip logika belum tepat!"
            ) { res, banner ->
                _uiState.update {
                    it.copy(
                        chipsFeedback = "Kurang tepat — $banner",
                        hp = res.newHp,
                        shieldChargeUsed = it.shieldChargeUsed || res.shieldConsumedCharge,
                        isGameOver = res.isDead,
                        heroAnim = if (res.isDead) HeroAnimState.DEATH else HeroAnimState.IDLE,
                        enemyAnim = EnemyAnimState.ATTACK,
                        heroHitTrigger = it.heroHitTrigger + 1,
                        screenShakeTrigger = it.screenShakeTrigger + 1,
                        floatingCombatText = FloatingCombatText(
                            ++combatTextSeq,
                            if (res.shieldBlocked) "BLOCKED!" else "-${res.damageTaken} HP",
                            isPositive = false
                        ),
                        battleBanner = banner,
                        hapticTriggerCount = it.hapticTriggerCount + 1
                    )
                }
            }
        }
    }

    fun submitQuizOption(index: Int) {
        val state = _uiState.value
        val step = state.currentStep as? SceneStep.QuizBattleStep ?: return
        if (state.quizPassed || state.isGameOver) return

        if (index == step.correctIndex) {
            val dealt = if (state.enemyMaxHp > 0) step.damageToEnemy else 0
            val nextEnemyHp = if (state.enemyMaxHp > 0) max(0, state.enemyHp - dealt) else state.enemyHp
            val defeated = state.enemyMaxHp > 0 && nextEnemyHp == 0
            _uiState.update {
                it.copy(
                    selectedQuizOption = index,
                    quizPassed = true,
                    quizFeedback = "Benar! ${step.explanation}",
                    heroAnim = HeroAnimState.SKILL_1,
                    enemyAnim = if (defeated) EnemyAnimState.DEATH else EnemyAnimState.IDLE,
                    enemyHp = nextEnemyHp,
                    enemyStatusLabel = if (defeated) "DEFEATED" else it.enemyStatusLabel,
                    heroLungeTrigger = it.heroLungeTrigger + 1,
                    enemyHitTrigger = if (dealt > 0) it.enemyHitTrigger + 1 else it.enemyHitTrigger,
                    attackVfxTrigger = if (dealt > 0) it.attackVfxTrigger + 1 else it.attackVfxTrigger,
                    screenShakeTrigger = if (defeated) it.screenShakeTrigger + 1 else it.screenShakeTrigger,
                    floatingCombatText = if (dealt > 0) {
                        FloatingCombatText(++combatTextSeq, "-$dealt DMG", isPositive = true)
                    } else null,
                    espritPoints = it.espritPoints + 15,
                    questionsAnswered = it.questionsAnswered + 1,
                    battleBanner = if (defeated) "${step.enemyName} Kalah! (+15 ESPRIT)"
                    else "Serangan Logika Mengenai Target! (+15 ESPRIT)"
                )
            }
            scheduleHeroIdleReset()
        } else {
            applyBattleDamage(
                isBoss = step.isBoss,
                errorBanner = "Jawaban kurang tepat!"
            ) { res, banner ->
                _uiState.update {
                    it.copy(
                        selectedQuizOption = index,
                        quizFeedback = "Salah — $banner",
                        hp = res.newHp,
                        shieldChargeUsed = it.shieldChargeUsed || res.shieldConsumedCharge,
                        isGameOver = res.isDead,
                        heroAnim = if (res.isDead) HeroAnimState.DEATH else HeroAnimState.IDLE,
                        enemyAnim = EnemyAnimState.ATTACK,
                        heroHitTrigger = it.heroHitTrigger + 1,
                        screenShakeTrigger = it.screenShakeTrigger + 1,
                        floatingCombatText = FloatingCombatText(
                            ++combatTextSeq,
                            if (res.shieldBlocked) "BLOCKED!" else "-${res.damageTaken} HP",
                            isPositive = false
                        ),
                        battleBanner = banner,
                        hapticTriggerCount = it.hapticTriggerCount + 1
                    )
                }
            }
        }
    }

    fun updateCode(newCode: String) {
        _uiState.update { it.copy(codeText = newCode) }
    }

    fun insertChipIntoMainPy(chip: String) {
        _uiState.update { state ->
            val targetIdx = state.codeText.indexOf("____")
            if (targetIdx >= 0) {
                state.copy(codeText = state.codeText.replaceFirst("____", chip))
            } else {
                state
            }
        }
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

                val isOverdriveFinish = result.variables["total_power"] == 150 && state.bossWeakened
                val bonusForce = if (isOverdriveFinish) 35 else 25
                val dealt = if (state.enemyMaxHp > 0) step.damageToEnemy else 0
                val nextEnemyHp = if (state.enemyMaxHp > 0) max(0, state.enemyHp - dealt) else state.enemyHp
                val defeated = state.enemyMaxHp > 0 && nextEnemyHp == 0

                _uiState.update {
                    it.copy(
                        isEvaluating = false,
                        executionResult = result,
                        isShowingOutput = true,
                        heroAnim = HeroAnimState.SKILL_2,
                        enemyAnim = if (step.enemyType != EnemyType.NONE && (defeated || state.enemyMaxHp == 0)) EnemyAnimState.DEATH else EnemyAnimState.IDLE,
                        enemyHp = nextEnemyHp,
                        enemyStatusLabel = if (defeated) "DEFEATED" else it.enemyStatusLabel,
                        heroLungeTrigger = it.heroLungeTrigger + 1,
                        enemyHitTrigger = if (step.enemyType != EnemyType.NONE) it.enemyHitTrigger + 1 else it.enemyHitTrigger,
                        attackVfxTrigger = it.attackVfxTrigger + 1,
                        screenShakeTrigger = if (step.enemyType != EnemyType.NONE) it.screenShakeTrigger + 1 else it.screenShakeTrigger,
                        floatingCombatText = if (dealt > 0) {
                            FloatingCombatText(
                                ++combatTextSeq,
                                if (isOverdriveFinish) "OVERDRIVE CRIT!" else "-$dealt FINISHER!",
                                isPositive = true
                            )
                        } else {
                            FloatingCombatText(++combatTextSeq, "VERIFIED!", isPositive = true)
                        },
                        forcePoints = it.forcePoints + bonusForce,
                        questionsAnswered = it.questionsAnswered + 1,
                        battleBanner = if (isOverdriveFinish) "OVERDRIVE 150! Exit Code 0 (+$bonusForce FORCE)"
                        else "Kode Valid! Exit Code 0 (+$bonusForce FORCE)",
                        newlyUnlockedAchievements = it.newlyUnlockedAchievements + unlocked
                    )
                }
            } else {
                applyBattleDamage(
                    isBoss = step.isBoss,
                    errorBanner = result.errorMessage ?: "Kode belum memenuhi target"
                ) { dmgRes, banner ->
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
                            heroHitTrigger = it.heroHitTrigger + 1,
                            screenShakeTrigger = it.screenShakeTrigger + 1,
                            floatingCombatText = FloatingCombatText(
                                ++combatTextSeq,
                                if (dmgRes.shieldBlocked) "BLOCKED!" else "-${dmgRes.damageTaken} HP",
                                isPositive = false
                            ),
                            battleBanner = banner,
                            hapticTriggerCount = it.hapticTriggerCount + 1
                        )
                    }
                }
            }
        }
    }

    private fun scheduleHeroIdleReset() {
        viewModelScope.launch {
            delay(850)
            _uiState.update { it.copy(heroAnim = HeroAnimState.IDLE) }
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
                                floatingCombatText = FloatingCombatText(++combatTextSeq, "+4 HP", isPositive = true),
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
                    earnedPoints = script.rewardPoints,
                    alignmentChoice = state.selectedAlignment
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
                    cliffhangerText = script.cliffhangerText,
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
