package com.example.codeofthemistery.feature.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val step = state.currentStep
            if (step != null) {
                SpriteViewport(
                    backgroundKey = step.backgroundKey,
                    heroAnim = state.heroAnim,
                    enemyType = step.enemyType,
                    enemyAnim = state.enemyAnim,
                    systemBubbleText = (step as? SceneStep.NarrationStep)?.systemBubbleText,
                    showClaimButton = step is SceneStep.LootClaimStep,
                    onClaimClick = viewModel::claimLootAndComplete
                )

                state.battleBanner?.let { banner ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCard)
                            .border(1.dp, PrimaryLavender.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(banner, style = MaterialTheme.typography.bodySmall, color = PrimaryLavender)
                    }
                }

                when (step) {
                    is SceneStep.NarrationStep -> {
                        VisualNovelPanel(
                            step = step,
                            clueToast = state.clueSavedToast,
                            onAdvance = viewModel::advanceStep
                        )
                    }
                    is SceneStep.QuizBattleStep -> {
                        QuizBattlePanel(
                            step = step,
                            selectedOption = state.selectedQuizOption,
                            isPassed = state.quizPassed,
                            feedback = state.quizFeedback,
                            onSelectOption = viewModel::submitQuizOption,
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
                            isEvaluating = state.isEvaluating,
                            onCodeChange = viewModel::updateCode,
                            onRunClick = viewModel::runPythonCode,
                            onResetClick = viewModel::resetStarterCode,
                            onHintClick = viewModel::toggleHint,
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
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        QuickItemBar(
            slots = state.equippedSlots,
            onItemClick = viewModel::useQuickItem
        )
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
