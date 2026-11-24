package com.example.codeofthemistery.feature.play

import androidx.compose.runtime.Composable
import com.example.codeofthemistery.feature.auth.TerminalLoadingScreen

@Composable
fun GameLoadingScreen(
    chapterId: Int,
    onLoaded: () -> Unit
) {
    TerminalLoadingScreen(
        statusHeadline = "Loading Chapter $chapterId Environment...",
        onFinished = onLoaded
    )
}
