package com.example.codeofthemistery.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.feature.achievement.AchievementScreen
import com.example.codeofthemistery.feature.auth.ForgotPasswordScreen
import com.example.codeofthemistery.feature.auth.LoginScreen
import com.example.codeofthemistery.feature.auth.SignUpScreen
import com.example.codeofthemistery.feature.auth.SplashScreen
import com.example.codeofthemistery.feature.auth.TerminalLoadingScreen
import com.example.codeofthemistery.feature.battle.GameplayScreen
import com.example.codeofthemistery.feature.config.ConfigScreen
import com.example.codeofthemistery.feature.home.HomeScreen
import com.example.codeofthemistery.feature.items.ItemsScreen
import com.example.codeofthemistery.feature.play.ChapterMapScreen
import com.example.codeofthemistery.feature.play.GameLoadingScreen
import com.example.codeofthemistery.feature.redeem.RedeemScreen

private data class HubTab(
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(TerminalLoadingRoute("hub")) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(TerminalLoadingRoute("hub")) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(SignUpRoute) },
                onNavigateToForgot = { navController.navigate(ForgotPasswordRoute) }
            )
        }

        composable<SignUpRoute> {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(TerminalLoadingRoute("hub")) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable<ForgotPasswordRoute> {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onResetComplete = {
                    navController.navigate(TerminalLoadingRoute("login")) {
                        popUpTo<ForgotPasswordRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<TerminalLoadingRoute> { entry ->
            val route = entry.toRoute<TerminalLoadingRoute>()
            TerminalLoadingScreen(
                statusHeadline = if (route.destination == "login") {
                    "Updating Terminal Credentials..."
                } else {
                    "Entering the Terminal Code..."
                },
                onFinished = {
                    if (route.destination == "login") {
                        navController.navigate(LoginRoute) {
                            popUpTo<TerminalLoadingRoute> { inclusive = true }
                        }
                    } else {
                        navController.navigate(MainHubRoute) {
                            popUpTo<TerminalLoadingRoute> { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<MainHubRoute> {
            MainHubScaffold(
                onNavigateToChapterMap = { navController.navigate(ChapterMapRoute) },
                onNavigateToAchievements = { navController.navigate(AchievementRoute) },
                onLoggedOut = {
                    navController.navigate(SplashRoute) {
                        popUpTo<MainHubRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<AchievementRoute> {
            AchievementScreen(onBack = { navController.popBackStack() })
        }

        composable<ChapterMapRoute> {
            ChapterMapScreen(
                onBack = { navController.popBackStack() },
                onSelectChapter = { chapterId ->
                    navController.navigate(GameLoadingRoute(chapterId))
                }
            )
        }

        composable<GameLoadingRoute> { entry ->
            val route = entry.toRoute<GameLoadingRoute>()
            GameLoadingScreen(
                chapterId = route.chapterId,
                onLoaded = {
                    navController.navigate(GameplayRoute(route.chapterId)) {
                        popUpTo<GameLoadingRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<GameplayRoute> { entry ->
            val route = entry.toRoute<GameplayRoute>()
            GameplayScreen(
                chapterId = route.chapterId,
                onExitToHome = {
                    navController.navigate(MainHubRoute) {
                        popUpTo<MainHubRoute> { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun MainHubScaffold(
    onNavigateToChapterMap: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onLoggedOut: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        HubTab("HOME", Icons.Rounded.Code),
        HubTab("ITEMS", Icons.Rounded.Inventory2),
        HubTab("REDEEM", Icons.Rounded.WorkspacePremium),
        HubTab("CONFIG", Icons.Rounded.Settings)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding(),
        containerColor = BackgroundPrimary,
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = SurfaceCard,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { idx, tab ->
                        val active = selectedTab == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) PrimaryLavender else SurfaceCard)
                                .clickable { selectedTab = idx }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    tint = if (active) OnPrimaryDark else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (active) OnPrimaryDark else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onNavigateToPlay = onNavigateToChapterMap,
                    onNavigateToItems = { selectedTab = 1 },
                    onNavigateToAchievements = onNavigateToAchievements,
                    onNavigateToRedeem = { selectedTab = 2 }
                )
                1 -> ItemsScreen()
                2 -> RedeemScreen()
                else -> ConfigScreen(onLoggedOut = onLoggedOut)
            }
        }
    }
}
