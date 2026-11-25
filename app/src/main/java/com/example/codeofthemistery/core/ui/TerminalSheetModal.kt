package com.example.codeofthemistery.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.MonoFontFamily
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.theme.TextSecondary
import com.example.codeofthemistery.domain.model.SkillStatus
import org.koin.androidx.compose.koinViewModel

@Composable
fun TerminalTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showSheetAction: Boolean = true
) {
    var showTerminalSheet by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryLavender
            )
            if (showSheetAction) {
                Surface(
                    onClick = { showTerminalSheet = true },
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, PrimaryLavender.copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Terminal,
                            contentDescription = "Skill Tree & Clue Log",
                            tint = PrimaryLavender,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Codex",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = PrimaryLavender
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = BorderMuted, thickness = 1.dp)
    }

    if (showTerminalSheet) {
        TerminalSheetModal(onDismiss = { showTerminalSheet = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalSheetModal(
    onDismiss: () -> Unit,
    viewModel: TerminalSheetViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = ">_ TERMINAL CODEX",
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryLavender
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pantau Skill Tree penguasaan Python dan catatan Clue materi.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BackgroundPrimary,
                contentColor = PrimaryLavender,
                indicator = { positions ->
                    if (selectedTab < positions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(positions[selectedTab]),
                            color = PrimaryLavender
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Skill Tree (${state.skills.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Clue Log (${state.clues.size})") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (selectedTab == 0) {
                    items(state.skills, key = { it.id }) { node ->
                        val (badgeText, badgeColor) = when (node.status) {
                            SkillStatus.MASTERED -> "MASTERED" to SuccessGreen
                            SkillStatus.LEARNING -> "LEARNING" to PrimaryLavender
                            SkillStatus.LOCKED -> "LOCKED" to TextMuted
                        }
                        CyberCard(
                            borderColor = if (node.status == SkillStatus.LOCKED) BorderMuted else badgeColor.copy(alpha = 0.5f),
                            containerColor = BackgroundPrimary
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Ch.${node.chapterId} • ${node.title}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = node.topic,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TerminalBadge(text = badgeText, color = badgeColor)
                                    Icon(
                                        imageVector = if (node.status == SkillStatus.LOCKED) Icons.Rounded.Lock else Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = badgeColor
                                    )
                                }
                            }
                        }
                    }
                } else {
                    if (state.clues.isEmpty()) {
                        item {
                            CyberCard(containerColor = BackgroundPrimary) {
                                Text(
                                    text = "Belum ada Clue tersimpan. Mainkan Chapter di menu PLAY untuk mengumpulkan catatan materi Python!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        items(state.clues, key = { it.clueId }) { clue ->
                            CyberCard(
                                borderColor = CyanTerminal.copy(alpha = 0.4f),
                                containerColor = BackgroundPrimary
                            ) {
                                TerminalBadge(text = "CLUE • CHAPTER ${clue.chapterId}", color = CyanTerminal)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = clue.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = clue.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
