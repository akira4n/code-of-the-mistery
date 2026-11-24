package com.example.codeofthemistery.feature.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.theme.TextSecondary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.TerminalBadge
import com.example.codeofthemistery.core.ui.TerminalTopBar
import com.example.codeofthemistery.core.ui.XpProgressBar
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToPlay: () -> Unit,
    onNavigateToItems: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToRedeem: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val user = state.user
    val numberFmt = NumberFormat.getNumberInstance(Locale.US)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        TerminalTopBar(title = "Code of the Mistery")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CyberCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(SurfaceElevated)
                                .border(1.5.dp, PrimaryLavender.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = user?.name ?: "Player Name",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "LEVEL ${user?.level ?: 1} ${state.rankTitle}",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryLavender.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    val xpProgress = if (state.nextLevelXp > 0) {
                        (user?.currentXp ?: 0).toFloat() / state.nextLevelXp.toFloat()
                    } else 0f
                    XpProgressBar(progress = xpProgress, height = 10.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${numberFmt.format(user?.currentXp ?: 0)} XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Text(
                            text = "${numberFmt.format(state.nextLevelXp)} XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    StatMiniBar(
                        label = "Poin Terkumpul",
                        valueText = numberFmt.format(user?.points ?: 0),
                        progress = ((user?.points ?: 0) / 20000f).coerceIn(0.05f, 1f),
                        accentColor = PrimaryLavender
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StatMiniBar(
                        label = "Chapter Terselesaikan",
                        valueText = "${state.completedChaptersCount}/${state.totalChaptersCount}",
                        progress = if (state.totalChaptersCount > 0) state.completedChaptersCount.toFloat() / state.totalChaptersCount else 0f,
                        accentColor = CrimsonDanger
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StatMiniBar(
                        label = "Pencapaian Didapatkan",
                        valueText = "${state.unlockedAchievementsCount}/${state.totalAchievementsCount}",
                        progress = if (state.totalAchievementsCount > 0) state.unlockedAchievementsCount.toFloat() / state.totalAchievementsCount else 0f,
                        accentColor = GoldAccent
                    )
                }
            }

            item {
                val chapter = state.currentChapter
                CyberCard {
                    TerminalBadge(
                        text = "Current Chapter",
                        color = PrimaryLavender,
                        backgroundColor = BackgroundPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = chapter?.title ?: "Chapter I : Mastering Logic, Surviving Syntax",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chapter?.subtitle ?: "Perkenalan kepada bahasa pemrograman Python",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onNavigateToPlay,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryLavender,
                                contentColor = OnPrimaryDark
                            )
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PLAY", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item { HorizontalDivider(color = BorderMuted) }

            item {
                HomeShortcutCard(
                    title = "Items",
                    description = "Gunakan beberapa item yang bisa digunakan in-game untuk membantu Anda.",
                    icon = Icons.Rounded.Inventory2,
                    accentColor = CrimsonDanger,
                    onClick = onNavigateToItems
                )
            }

            item {
                HomeShortcutCard(
                    title = "Achievements",
                    description = "Lihat badge pencapaian dan progres mastery yang telah Anda dapatkan.",
                    icon = Icons.Rounded.WorkspacePremium,
                    accentColor = GoldAccent,
                    onClick = onNavigateToAchievements
                )
            }

            item {
                HomeShortcutCard(
                    title = "Redeem Your Points",
                    description = "Tukarkan poinmu dengan voucher Shopee, Tokopedia, Dana, atau akses AI.",
                    icon = Icons.Rounded.WorkspacePremium,
                    accentColor = PrimaryLavender,
                    onClick = onNavigateToRedeem
                )
            }
        }
    }
}

@Composable
private fun StatMiniBar(
    label: String,
    valueText: String,
    progress: Float,
    accentColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(valueText, style = MaterialTheme.typography.labelSmall, color = accentColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        XpProgressBar(progress = progress, fillColor = accentColor, height = 5.dp)
    }
}

@Composable
private fun HomeShortcutCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BackgroundPrimary)
                    .border(1.dp, BorderHighlight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}
