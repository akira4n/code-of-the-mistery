package com.example.codeofthemistery.feature.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.domain.mechanics.LevelCalculator
import com.example.codeofthemistery.domain.model.AchievementProgress
import com.example.codeofthemistery.domain.model.User
import com.example.codeofthemistery.domain.repository.AchievementRepository
import com.example.codeofthemistery.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AchievementUiState(
    val user: User? = null,
    val nextLevelXp: Int = 750,
    val achievements: List<AchievementProgress> = emptyList(),
    val unlockedCount: Int = 0
)

class AchievementViewModel(
    authRepository: AuthRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AchievementUiState> = authRepository.observeActiveUser()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(AchievementUiState())
            } else {
                achievementRepository.observeAchievements(user.id).map { list ->
                    AchievementUiState(
                        user = user,
                        nextLevelXp = LevelCalculator.xpRequiredForNextLevel(user.level),
                        achievements = list,
                        unlockedCount = list.count { it.isUnlocked }
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AchievementUiState())
}
