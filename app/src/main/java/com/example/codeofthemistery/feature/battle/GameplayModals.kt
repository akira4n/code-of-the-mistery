package com.example.codeofthemistery.feature.battle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.MonoFontFamily
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.theme.TextSecondary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.NeonButton
import com.example.codeofthemistery.core.ui.TerminalBadge
import com.example.codeofthemistery.domain.model.AchievementProgress
import com.example.codeofthemistery.domain.model.AlignmentChoice
import com.example.codeofthemistery.domain.model.HelpMateriInfo
import com.example.codeofthemistery.domain.model.OwnedItem
import com.example.codeofthemistery.feature.items.itemAccentColor
import com.example.codeofthemistery.feature.items.itemIcon

@Composable
fun HelpMateriDialog(
    info: HelpMateriInfo,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = BorderStroke(1.5.dp, CyanTerminal.copy(alpha = 0.7f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.HelpOutline, contentDescription = null, tint = CyanTerminal)
                    Text(
                        text = info.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyanTerminal
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = info.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF101218))
                        .border(1.dp, BorderHighlight, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = info.codeExample,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = MonoFontFamily),
                        color = PrimaryLavender
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                NeonButton(
                    text = "Mengerti",
                    onClick = onDismiss,
                    cornerRadius = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PauseModal(
    onResume: () -> Unit,
    onOpenCodex: () -> Unit,
    onExitToMenu: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, BorderHighlight)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Paused", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderMuted)
                Spacer(modifier = Modifier.height(18.dp))

                NeonButton(
                    text = "Lanjutkan",
                    onClick = onResume,
                    cornerRadius = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onOpenCodex,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, PrimaryLavender.copy(alpha = 0.5f))
                ) {
                    Text("Skill Tree & Clue Log", color = PrimaryLavender)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onExitToMenu,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryLavender,
                        contentColor = OnPrimaryDark
                    )
                ) {
                    Text("Keluar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LootRewardModal(
    item: OwnedItem,
    onContinue: () -> Unit
) {
    val accent = itemAccentColor(item.itemId, item.rarity)
    Dialog(onDismissRequest = onContinue) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = BorderStroke(1.5.dp, accent.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = GoldAccent)
                    Text("LOOT BOX TERBUKA!", style = MaterialTheme.typography.titleMedium, color = GoldAccent)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BackgroundPrimary)
                        .border(1.5.dp, accent, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(itemIcon(item.itemId), contentDescription = null, tint = accent, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                TerminalBadge(text = "${item.rarity.name} • ${item.statLabel}", color = accent)
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))
                NeonButton(
                    text = "Klaim & Selesaikan Chapter",
                    onClick = onContinue,
                    cornerRadius = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ChapterCompletedScreen(
    chapterId: Int,
    earnedXp: Int,
    earnedPoints: Int,
    questionsAnswered: Int,
    alignmentChoice: AlignmentChoice?,
    secretClueUnlocked: Boolean,
    cliffhangerText: String,
    unlockedAchievements: List<AchievementProgress>,
    onBackHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CyberCard(borderColor = PrimaryLavender.copy(alpha = 0.6f)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Chapter $chapterId\nCompleted",
                    style = MaterialTheme.typography.displaySmall,
                    color = PrimaryLavender,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderMuted)
                Spacer(modifier = Modifier.height(16.dp))

                ResultStatRow("XP", "+$earnedXp")
                Spacer(modifier = Modifier.height(8.dp))
                ResultStatRow("Points", "+$earnedPoints")
                Spacer(modifier = Modifier.height(8.dp))
                ResultStatRow("Soal Terjawab", "$questionsAnswered")

                if (alignmentChoice != null || secretClueUnlocked) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        alignmentChoice?.let { align ->
                            val alignColor = if (align == AlignmentChoice.PATCH) CyanTerminal else CrimsonDanger
                            TerminalBadge(text = "JALUR: ${align.name}", color = alignColor)
                        }
                        if (secretClueUnlocked) {
                            TerminalBadge(text = "SECRET CLUE +1", color = GoldAccent)
                        }
                    }
                }

                if (unlockedAchievements.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Achievement Terbuka:", style = MaterialTheme.typography.labelMedium, color = SuccessGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    unlockedAchievements.forEach { ach ->
                        TerminalBadge(text = "${ach.title} (+${ach.rewardXp} XP)", color = SuccessGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (cliffhangerText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundPrimary)
                            .border(1.dp, BorderHighlight, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "BERSAMBUNG KE CHAPTER ${chapterId + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = PrimaryLavender
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cliffhangerText,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))
                NeonButton(
                    text = "Kembali ke beranda",
                    onClick = onBackHome,
                    cornerRadius = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MissionFailedScreen(
    chapterId: Int,
    onRetry: () -> Unit,
    onExitHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CyberCard(borderColor = CrimsonDanger.copy(alpha = 0.8f)) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = CrimsonDanger,
                modifier = Modifier.size(48.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "SYSTEM CRASH\n(HP 0/6)",
                style = MaterialTheme.typography.headlineMedium,
                color = CrimsonDanger,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Runtime error menghabiskan seluruh HP kamu di Chapter $chapterId. Pasang Firewall Shield atau siapkan Syntax Potion sebelum mencoba lagi!",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(22.dp))
            NeonButton(
                text = "Ulangi Chapter $chapterId",
                onClick = onRetry,
                cornerRadius = 10.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onExitHome,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Kembali ke beranda", color = TextPrimary)
            }
        }
    }
}

@Composable
private fun ResultStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = PrimaryLavender)
    }
}
