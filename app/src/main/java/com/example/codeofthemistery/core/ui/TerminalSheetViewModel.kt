package com.example.codeofthemistery.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.domain.model.ClueEntry
import com.example.codeofthemistery.domain.model.SkillNode
import com.example.codeofthemistery.domain.repository.AchievementRepository
import com.example.codeofthemistery.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class TerminalSheetUiState(
    val skills: List<SkillNode> = emptyList(),
    val clues: List<ClueEntry> = emptyList()
)

class TerminalSheetViewModel(
    authRepository: AuthRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TerminalSheetUiState> = authRepository.observeActiveUser()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(TerminalSheetUiState())
            } else {
                combine(
                    achievementRepository.observeSkillTree(user.id),
                    achievementRepository.observeClues(user.id)
                ) { skills, clues ->
                    TerminalSheetUiState(skills = skills, clues = clues)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TerminalSheetUiState())
}
