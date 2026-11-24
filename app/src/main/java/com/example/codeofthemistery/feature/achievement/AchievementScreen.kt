package com.example.codeofthemistery.feature.achievement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.TerminalBadge
import com.example.codeofthemistery.core.ui.TerminalTopBar
import com.example.codeofthemistery.core.ui.XpProgressBar
import com.example.codeofthemistery.domain.model.AchievementProgress
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AchievementScreen(
    onBack: () -> Unit,
    viewModel: AchievementViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val user = state.user
    val fmt = NumberFormat.getNumberInstance(Locale.US)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
    ) {
        TerminalTopBar(title = "Achievement")

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = onBack),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Back", tint = PrimaryLavender)
            Text("Kembali", color = PrimaryLavender, style = MaterialTheme.typography.titleSmall)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                CyberCard {
                    Text("STATUS OPERASIONAL", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Level ", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                        Text("${user?.level ?: 1}", style = MaterialTheme.typography.headlineMedium, color = PrimaryLavender)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    val xpProgress = if (state.nextLevelXp > 0) (user?.currentXp ?: 0).toFloat() / state.nextLevelXp.toFloat() else 0f
                    XpProgressBar(progress = xpProgress)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${fmt.format(user?.currentXp ?: 0)} XP", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        Text("${fmt.format(state.nextLevelXp)} XP", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BackgroundPrimary)
                            .border(1.dp, PrimaryLavender.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 28.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = PrimaryLavender)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${state.unlockedCount}/${state.achievements.size}",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text("Badge Aktif", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "{ } Log Pencapaian",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryLavender
                )
            }

            items(state.achievements, key = { it.id }) { item ->
                AchievementCard(item)
            }
        }
    }
}

@Composable
private fun AchievementCard(item: AchievementProgress) {
    val (icon, tint) = when (item.iconKey) {
        "explore" -> Icons.Rounded.Explore to PrimaryLavender
        "bug" -> Icons.Rounded.BugReport to CrimsonDanger
        "wizard" -> Icons.Rounded.AutoAwesome to GoldAccent
        "redeem" -> Icons.Rounded.WorkspacePremium to SuccessGreen
        else -> Icons.Rounded.Code to CyanTerminal
    }
    val pct = if (item.targetCount > 0) ((item.currentCount * 100) / item.targetCount).coerceIn(0, 100) else 0

    CyberCard(
        borderColor = if (item.isUnlocked) tint.copy(alpha = 0.65f) else BorderHighlight
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundPrimary)
                    .border(1.dp, tint.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.description, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TerminalBadge(text = "+${item.rewardXp} XP", color = tint)
                    Text(
                        text = if (item.isUnlocked) "100% Selesai" else "${item.currentCount}/${item.targetCount} ($pct%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.isUnlocked) SuccessGreen else TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                XpProgressBar(
                    progress = if (item.targetCount > 0) item.currentCount.toFloat() / item.targetCount else 0f,
                    fillColor = if (item.isUnlocked) SuccessGreen else tint,
                    trackColor = SurfaceElevated,
                    height = 5.dp
                )
            }
        }
    }
}
