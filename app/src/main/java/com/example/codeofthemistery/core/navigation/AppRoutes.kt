package com.example.codeofthemistery.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

@Serializable
data object LoginRoute

@Serializable
data object SignUpRoute

@Serializable
data object ForgotPasswordRoute

@Serializable
data class TerminalLoadingRoute(val destination: String = "hub")

@Serializable
data object MainHubRoute

@Serializable
data object AchievementRoute

@Serializable
data object ChapterMapRoute

@Serializable
data class GameLoadingRoute(val chapterId: Int)

@Serializable
data class GameplayRoute(val chapterId: Int)
