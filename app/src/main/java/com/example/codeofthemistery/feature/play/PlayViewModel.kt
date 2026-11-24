package com.example.codeofthemistery.feature.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.domain.model.ChapterSummary
import com.example.codeofthemistery.domain.repository.AuthRepository
import com.example.codeofthemistery.domain.repository.ChapterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class PlayViewModel(
    authRepository: AuthRepository,
    private val chapterRepository: ChapterRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val chapters: StateFlow<List<ChapterSummary>> = authRepository.observeActiveUser()
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else chapterRepository.observeChapters(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
