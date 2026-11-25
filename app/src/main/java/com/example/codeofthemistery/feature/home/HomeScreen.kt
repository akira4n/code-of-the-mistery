package com.example.codeofthemistery.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.R
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.MonoFontFamily
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.theme.TextSecondary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.NeonButton
import com.example.codeofthemistery.core.ui.TerminalSheetModal
import com.example.codeofthemistery.core.ui.TerminalTopBar
import com.example.codeofthemistery.core.ui.XpProgressBar
import com.example.codeofthemistery.domain.model.ChapterStatus
import com.example.codeofthemistery.domain.model.ChapterSummary
import com.example.codeofthemistery.domain.model.OwnedItem
import com.example.codeofthemistery.feature.items.itemAccentColor
import com.example.codeofthemistery.feature.items.itemIcon
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onPlayChapter: (Int) -> Unit,
    onNavigateToChapterMap: () -> Unit,
    onNavigateToItems: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCodexSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        TerminalTopBar(title = "Code of the Mystery")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PlayerOverviewBlock(
                state = state,
                onNavigateToChapterMap = onNavigateToChapterMap,
                onNavigateToAchievements = onNavigateToAchievements
            )

            HeroMissionBanner(
                chapter = state.currentChapter,
                onPlayChapter = onPlayChapter,
                onNavigateToChapterMap = onNavigateToChapterMap
            )

            LoadoutAndQuickGrid(
                equippedSlots = state.equippedSlots,
                cluesCount = state.cluesCount,
                unlockedAchievementsCount = state.unlockedAchievementsCount,
                totalAchievementsCount = state.totalAchievementsCount,
                onNavigateToItems = onNavigateToItems,
                onNavigateToAchievements = onNavigateToAchievements,
                onOpenCodex = { showCodexSheet = true }
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    if (showCodexSheet) {
        TerminalSheetModal(onDismiss = { showCodexSheet = false })
    }
}

@Composable
private fun PlayerOverviewBlock(
    state: HomeUiState,
    onNavigateToChapterMap: () -> Unit,
    onNavigateToAchievements: () -> Unit
) {
    val user = state.user
    val xpTarget = state.nextLevelXp.coerceAtLeast(1)
    val xpProgress = (user?.currentXp ?: 0).toFloat() / xpTarget.toFloat()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CyberCard(
            borderColor = BorderHighlight,
            containerColor = SurfaceCard,
            cornerRadius = 16.dp,
            contentPadding = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BackgroundPrimary)
                        .border(1.5.dp, PrimaryLavender.copy(alpha = 0.65f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_idle_1),
                        contentDescription = "Hero Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = user?.name ?: "Novice Coder",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    MonoBadge(
                        text = "LVL ${user?.level ?: 1} • ${state.rankTitle.uppercase()}",
                        color = PrimaryLavender,
                        containerColor = PrimaryLavender.copy(alpha = 0.14f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "XP PROGRESS",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = MonoFontFamily),
                    color = TextMuted
                )
                Text(
                    text = "${user?.currentXp ?: 0} / ${state.nextLevelXp} XP",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = PrimaryLavender
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            XpProgressBar(
                progress = xpProgress,
                fillColor = PrimaryLavender,
                trackColor = SurfaceElevated,
                height = 8.dp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatKpiTile(
                label = "Poin",
                value = "${user?.points ?: 0}",
                icon = Icons.Rounded.Stars,
                accentColor = GoldAccent,
                modifier = Modifier.weight(1f)
            )
            StatKpiTile(
                label = "Chapter",
                value = "${state.completedChaptersCount}/${state.totalChaptersCount}",
                icon = Icons.Rounded.MenuBook,
                accentColor = CyanTerminal,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToChapterMap
            )
            StatKpiTile(
                label = "Badge",
                value = "${state.unlockedAchievementsCount}/${state.totalAchievementsCount}",
                icon = Icons.Rounded.EmojiEvents,
                accentColor = SuccessGreen,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAchievements
            )
        }
    }
}

@Composable
private fun StatKpiTile(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val tileShape = RoundedCornerShape(14.dp)
    val baseModifier = modifier
        .clip(tileShape)
        .background(SurfaceCard)
        .border(1.dp, BorderMuted, tileShape)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        .padding(horizontal = 12.dp, vertical = 12.dp)

    Column(
        modifier = baseModifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = MonoFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun HeroMissionBanner(
    chapter: ChapterSummary?,
    onPlayChapter: (Int) -> Unit,
    onNavigateToChapterMap: () -> Unit
) {
    val bannerShape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(bannerShape)
            .border(1.5.dp, PrimaryLavender.copy(alpha = 0.75f), bannerShape)
            .background(SurfaceCard)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_dokja_street),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            alpha = 0.45f
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundPrimary.copy(alpha = 0.55f),
                            BackgroundPrimary.copy(alpha = 0.88f),
                            SurfaceCard.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            MonoBadge(
                text = "MISI AKTIF",
                color = PrimaryLavender,
                containerColor = BackgroundPrimary.copy(alpha = 0.78f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (chapter != null) {
                Text(
                    text = "CHAPTER ${chapter.id} • ${chapter.topicLabel.uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = CyanTerminal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chapter.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MonoBadge(
                        text = "+${chapter.rewardXp} XP",
                        color = PrimaryLavender,
                        containerColor = BackgroundPrimary.copy(alpha = 0.78f)
                    )
                    MonoBadge(
                        text = "+${chapter.rewardPoints} PTS",
                        color = GoldAccent,
                        containerColor = BackgroundPrimary.copy(alpha = 0.78f)
                    )
                    if (chapter.status == ChapterStatus.COMPLETED) {
                        MonoBadge(
                            text = "SELESAI",
                            color = SuccessGreen,
                            containerColor = BackgroundPrimary.copy(alpha = 0.78f)
                        )
                    }
                }
            } else {
                Text(
                    text = "Siap Memulai Petualangan",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pilih chapter di peta misi untuk mulai belajar Python.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                onClick = onNavigateToChapterMap,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = BackgroundPrimary.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, PrimaryLavender.copy(alpha = 0.45f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Peta Chapter →",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = PrimaryLavender
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            NeonButton(
                text = "MAIN SEKARANG",
                onClick = {
                    if (chapter != null) {
                        onPlayChapter(chapter.id)
                    } else {
                        onNavigateToChapterMap()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                containerColor = PrimaryLavender,
                contentColor = OnPrimaryDark,
                cornerRadius = 14.dp
            )
        }
    }
}

@Composable
private fun LoadoutAndQuickGrid(
    equippedSlots: List<OwnedItem?>,
    cluesCount: Int,
    unlockedAchievementsCount: Int,
    totalAchievementsCount: Int,
    onNavigateToItems: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onOpenCodex: () -> Unit
) {
    val normalizedSlots = (0 until 3).map { index -> equippedSlots.getOrNull(index) }
    val equippedCount = normalizedSlots.count { it != null }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            onClick = onNavigateToItems,
            shape = RoundedCornerShape(14.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, BorderMuted)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Loadout Aktif",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$equippedCount/3 slot terpasang",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    normalizedSlots.forEach { item ->
                        LoadoutMiniSlot(item = item)
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Ubah →",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = PrimaryLavender
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionTile(
                title = "Achievements",
                subtitle = "$unlockedAchievementsCount/$totalAchievementsCount Badge",
                tag = "BADGE",
                icon = Icons.Rounded.EmojiEvents,
                accentColor = GoldAccent,
                onClick = onNavigateToAchievements,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            QuickActionTile(
                title = "Terminal Codex",
                subtitle = "$cluesCount Clue • Skill Tree",
                tag = ">_ CODEX",
                icon = Icons.Rounded.Terminal,
                accentColor = CyanTerminal,
                onClick = onOpenCodex,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun LoadoutMiniSlot(item: OwnedItem?) {
    val accent = if (item != null) itemAccentColor(item.itemId, item.rarity) else BorderMuted
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(BackgroundPrimary)
            .border(
                width = 1.dp,
                color = if (item != null) accent.copy(alpha = 0.65f) else BorderMuted,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (item != null) {
            Icon(
                imageVector = itemIcon(item.itemId),
                contentDescription = item.name,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Slot kosong",
                tint = TextMuted.copy(alpha = 0.55f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    tag: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderMuted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.14f))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MonoBadge(
    text: String,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = MonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = color
        )
    }
}
