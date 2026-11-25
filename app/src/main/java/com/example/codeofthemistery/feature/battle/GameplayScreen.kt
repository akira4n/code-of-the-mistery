package com.example.codeofthemistery.feature.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.TerminalSheetModal
import com.example.codeofthemistery.domain.model.SceneStep
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GameplayScreen(
    chapterId: Int,
    onExitToHome: () -> Unit,
    viewModel: GameplayViewModel = koinViewModel(parameters = { parametersOf(chapterId) })
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0
    var showCodexSheet by remember { mutableStateOf(false) }

    LaunchedEffect(state.hapticTriggerCount) {
        if (state.hapticTriggerCount > 0 && state.hapticsEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    if (state.isCompleted) {
        ChapterCompletedScreen(
            chapterId = state.chapterId,
            earnedXp = state.earnedXp,
            earnedPoints = state.earnedPoints,
            questionsAnswered = state.questionsAnswered,
            alignmentChoice = state.selectedAlignment,
            secretClueUnlocked = state.secretClueUnlocked,
            cliffhangerText = state.cliffhangerText,
            unlockedAchievements = state.newlyUnlockedAchievements,
            onBackHome = onExitToHome
        )
        return
    }

    if (state.isGameOver) {
        MissionFailedScreen(
            chapterId = state.chapterId,
            onRetry = viewModel::retryChapter,
            onExitHome = onExitToHome
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        GameplayHudBar(
            chapterTitle = state.chapterTitle,
            hp = state.hp,
            maxHp = state.maxHp,
            hasFirewallShield = state.hasFirewallShield,
            shieldChargeUsed = state.shieldChargeUsed,
            forcePoints = state.forcePoints,
            espritPoints = state.espritPoints,
            onPauseClick = { viewModel.togglePause(true) }
        )

        val step = state.currentStep
        if (step != null) {
            // Pinned visual battle stage so sprites, Enemy HP Bar, and FX remain visible
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpriteViewport(
                    backgroundKey = step.backgroundKey,
                    heroAnim = state.heroAnim,
                    enemyType = step.enemyType,
                    enemyAnim = state.enemyAnim,
                    showAriaCompanion = step.showsAriaCompanion(),
                    enemyName = state.enemyName,
                    enemyHp = state.enemyHp,
                    enemyMaxHp = state.enemyMaxHp,
                    enemyStatusLabel = state.enemyStatusLabel,
                    heroLungeTrigger = state.heroLungeTrigger,
                    heroHitTrigger = state.heroHitTrigger,
                    enemyHitTrigger = state.enemyHitTrigger,
                    attackVfxTrigger = state.attackVfxTrigger,
                    screenShakeTrigger = state.screenShakeTrigger,
                    floatingCombatText = state.floatingCombatText,
                    systemBubbleText = (step as? SceneStep.NarrationStep)?.systemBubbleText,
                    showClaimButton = step is SceneStep.LootClaimStep,
                    isCompact = isImeVisible,
                    onClaimClick = viewModel::claimLootAndComplete
                )

                state.battleBanner?.let { banner ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCard)
                            .border(1.dp, PrimaryLavender.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(banner, style = MaterialTheme.typography.bodySmall, color = PrimaryLavender)
                    }
                }
            }

            // Independently scrollable interactive narration / challenge panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (step) {
                    is SceneStep.NarrationStep -> {
                        VisualNovelPanel(
                            step = step,
                            clueToast = state.clueSavedToast,
                            onAdvance = viewModel::advanceStep
                        )
                    }
                    is SceneStep.BranchChoiceStep -> {
                        BranchChoicePanel(
                            step = step,
                            selectedOption = state.branchSelectedOption,
                            clueToast = state.clueSavedToast,
                            onSelectOption = viewModel::selectBranchOption,
                            onContinue = viewModel::advanceStep
                        )
                    }
                    is SceneStep.BugHuntStep -> {
                        BugHuntPanel(
                            step = step,
                            selectedLine = state.selectedBugLine,
                            isPassed = state.bugHuntPassed,
                            feedback = state.bugHuntFeedback,
                            onSelectLine = viewModel::submitBugHuntLine,
                            onOpenHelp = { viewModel.openHelpMateri(step.helpMateri) },
                            onContinue = viewModel::advanceStep
                        )
                    }
                    is SceneStep.CodeChipsStep -> {
                        CodeChipsPanel(
                            step = step,
                            selectedChips = state.selectedChips,
                            isPassed = state.chipsPassed,
                            feedback = state.chipsFeedback,
                            onSelectChip = viewModel::selectCodeChip,
                            onClearSlot = viewModel::clearCodeChipSlot,
                            onSubmit = viewModel::submitCodeChips,
                            onOpenHelp = { viewModel.openHelpMateri(step.helpMateri) },
                            onContinue = viewModel::advanceStep
                        )
                    }
                    is SceneStep.QuizBattleStep -> {
                        QuizBattlePanel(
                            step = step,
                            selectedOption = state.selectedQuizOption,
                            isPassed = state.quizPassed,
                            feedback = state.quizFeedback,
                            onSelectOption = viewModel::submitQuizOption,
                            onOpenHelp = step.helpMateri?.let { info -> { viewModel.openHelpMateri(info) } },
                            onContinue = viewModel::advanceStep
                        )
                    }
                    is SceneStep.CodeBattleStep -> {
                        PythonCodeEditorPanel(
                            step = step,
                            codeText = state.codeText,
                            executionResult = state.executionResult,
                            isShowingOutput = state.isShowingOutput,
                            showHint = state.showHint,
                            hasLaptop = state.hasLaptop,
                            bossWeakened = state.bossWeakened,
                            isEvaluating = state.isEvaluating,
                            onCodeChange = viewModel::updateCode,
                            onInsertChip = viewModel::insertChipIntoMainPy,
                            onRunClick = viewModel::runPythonCode,
                            onResetClick = viewModel::resetStarterCode,
                            onHintClick = viewModel::toggleHint,
                            onOpenHelp = step.helpMateri?.let { info -> { viewModel.openHelpMateri(info) } },
                            onBackToEditor = viewModel::switchBackToEditor,
                            onContinueAfterSuccess = viewModel::advanceStep
                        )
                    }
                    is SceneStep.LootClaimStep -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceCard)
                                .padding(16.dp)
                        ) {
                            Text(step.message, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        if (!isImeVisible) {
            QuickItemBar(
                slots = state.equippedSlots,
                onItemClick = viewModel::useQuickItem
            )
        }
    }

    if (state.isPaused) {
        PauseModal(
            onResume = { viewModel.togglePause(false) },
            onOpenCodex = {
                viewModel.togglePause(false)
                showCodexSheet = true
            },
            onExitToMenu = {
                viewModel.togglePause(false)
                onExitToHome()
            }
        )
    }

    state.activeHelpMateri?.let { info ->
        HelpMateriDialog(
            info = info,
            onDismiss = viewModel::dismissHelpMateri
        )
    }

    state.lootRewardModalItem?.let { dropped ->
        LootRewardModal(
            item = dropped,
            onContinue = viewModel::dismissLootAndFinish
        )
    }

    if (showCodexSheet) {
        TerminalSheetModal(onDismiss = { showCodexSheet = false })
    }
}

private fun SceneStep.showsAriaCompanion(): Boolean = when (this) {
    is SceneStep.NarrationStep -> showAriaCompanion
    is SceneStep.BranchChoiceStep -> showAriaCompanion
    is SceneStep.BugHuntStep -> showAriaCompanion
    is SceneStep.CodeChipsStep -> showAriaCompanion
    is SceneStep.QuizBattleStep -> showAriaCompanion
    is SceneStep.CodeBattleStep -> showAriaCompanion
    is SceneStep.LootClaimStep -> false
}
