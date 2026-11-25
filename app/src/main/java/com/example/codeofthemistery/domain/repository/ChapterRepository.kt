package com.example.codeofthemistery.domain.repository

import com.example.codeofthemistery.domain.model.AlignmentChoice
import com.example.codeofthemistery.domain.model.AlignmentSummary
import com.example.codeofthemistery.domain.model.ChapterScript
import com.example.codeofthemistery.domain.model.ChapterSummary
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {
    fun observeChapters(userId: String): Flow<List<ChapterSummary>>
    fun observeAlignmentSummary(userId: String): Flow<AlignmentSummary>
    fun getChapterScript(chapterId: Int): ChapterScript
    suspend fun completeChapter(
        userId: String,
        chapterId: Int,
        hpRemaining: Int,
        questionsAnswered: Int,
        earnedXp: Int,
        earnedPoints: Int,
        alignmentChoice: AlignmentChoice? = null
    )
}
