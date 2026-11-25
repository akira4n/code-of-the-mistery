package com.example.codeofthemistery.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.data.local.datastore.SessionPreferences
import com.example.codeofthemistery.domain.mechanics.LevelCalculator
import com.example.codeofthemistery.domain.model.ChapterStatus
import com.example.codeofthemistery.domain.model.ChapterSummary
import com.example.codeofthemistery.domain.model.OwnedItem
import com.example.codeofthemistery.domain.model.User
import com.example.codeofthemistery.domain.repository.AchievementRepository
import com.example.codeofthemistery.domain.repository.AuthRepository
import com.example.codeofthemistery.domain.repository.ChapterRepository
import com.example.codeofthemistery.domain.repository.InventoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val user: User? = null,
    val rankTitle: String = "INITIATE CODER",
    val nextLevelXp: Int = 750,
    val completedChaptersCount: Int = 0,
    val totalChaptersCount: Int = 7,
    val unlockedAchievementsCount: Int = 0,
    val totalAchievementsCount: Int = 5,
    val cluesCount: Int = 0,
    val currentChapter: ChapterSummary? = null,
    val equippedSlots: List<OwnedItem?> = listOf(null, null, null)
)

class HomeViewModel(
    authRepository: AuthRepository,
    private val chapterRepository: ChapterRepository,
    private val achievementRepository: AchievementRepository,
    private val inventoryRepository: InventoryRepository,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = authRepository.observeActiveUser()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(HomeUiState())
            } else {
                combine(
                    chapterRepository.observeChapters(user.id),
                    achievementRepository.observeAchievements(user.id),
                    achievementRepository.observeClues(user.id),
                    inventoryRepository.observeInventory(user.id),
                    sessionPreferences.lastChapterIdFlow
                ) { chapters, achievements, clues, inventory, lastChapterId ->
                    val current = chapters.find { it.id == lastChapterId && it.status != ChapterStatus.LOCKED }
                        ?: chapters.lastOrNull { it.status == ChapterStatus.UNLOCKED && it.isPlayable }
                        ?: chapters.firstOrNull()
                    val slots = listOf(1, 2, 3).map { slot ->
                        inventory.find { it.equippedSlot == slot && it.isUnlocked }
                    }
                    HomeUiState(
                        user = user,
                        rankTitle = LevelCalculator.rankTitleForLevel(user.level),
                        nextLevelXp = LevelCalculator.xpRequiredForNextLevel(user.level),
                        completedChaptersCount = chapters.count { it.status == ChapterStatus.COMPLETED },
                        totalChaptersCount = chapters.size,
                        unlockedAchievementsCount = achievements.count { it.isUnlocked },
                        totalAchievementsCount = achievements.size,
                        cluesCount = clues.size,
                        currentChapter = current,
                        equippedSlots = slots
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
