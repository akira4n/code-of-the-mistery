package com.example.codeofthemistery.feature.play

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.PrimaryDeepViolet
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.domain.model.ChapterStatus
import com.example.codeofthemistery.domain.model.ChapterSummary
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChapterMapScreen(
    onBack: () -> Unit,
    onSelectChapter: (Int) -> Unit,
    viewModel: PlayViewModel = koinViewModel()
) {
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()
    var previewLockedChapter by remember { mutableStateOf<ChapterSummary?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable(onClick = onBack),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.ChevronLeft, contentDescription = "Back", tint = PrimaryLavender)
                Text("Back", color = PrimaryLavender, style = MaterialTheme.typography.titleSmall)
            }
            Text("PLAY", color = PrimaryLavender, style = MaterialTheme.typography.titleLarge)
        }
        HorizontalDivider(color = BorderMuted)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp)
        ) {
            itemsIndexed(chapters, key = { _, item -> item.id }) { index, chapter ->
                val isLeft = index % 2 == 0
                val isUnlocked = chapter.status != ChapterStatus.LOCKED && chapter.isPlayable
                val isCompleted = chapter.status == ChapterStatus.COMPLETED

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 18.dp)
                                .clickable {
                                    if (isUnlocked) onSelectChapter(chapter.id)
                                    else previewLockedChapter = chapter
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .then(
                                        if (isUnlocked) {
                                            Modifier.shadow(20.dp, CircleShape, ambientColor = PrimaryDeepViolet, spotColor = PrimaryDeepViolet)
                                        } else Modifier
                                    )
                                    .clip(CircleShape)
                                    .background(if (isUnlocked) SurfaceElevated else SurfaceCard)
                                    .border(
                                        width = if (isUnlocked) 2.dp else 1.dp,
                                        color = when {
                                            isCompleted -> SuccessGreen
                                            isUnlocked -> PrimaryLavender
                                            else -> BorderHighlight
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUnlocked) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "${chapter.id}",
                                            color = if (isCompleted) SuccessGreen else PrimaryLavender,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Chapter",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextPrimary
                                        )
                                        if (isCompleted) {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = null,
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = "Locked",
                                        tint = TextMuted,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = chapter.topicLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isUnlocked) PrimaryLavender else TextMuted,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (isCompleted) "Selesai • Tap untuk ulang"
                                else if (isUnlocked) "Tap untuk mulai"
                                else if (!chapter.isPlayable) "Roadmap Ekspansi"
                                else "Selesaikan Chapter ${chapter.id - 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (index < chapters.lastIndex) {
                        CurvedConnector(fromLeftToRight = isLeft)
                    }
                }
            }
        }
    }

    previewLockedChapter?.let { ch ->
        AlertDialog(
            onDismissRequest = { previewLockedChapter = null },
            containerColor = SurfaceCard,
            title = { Text(ch.title, color = PrimaryLavender) },
            text = {
                val reason = if (!ch.isPlayable) {
                    "${ch.subtitle}\n\nChapter ekspansi ini disiapkan untuk Phase 2 bersama backend Express. Selesaikan Chapter 1–3 untuk menaklukkan seluruh konten UAS!"
                } else {
                    "${ch.subtitle}\n\nSelesaikan Chapter ${ch.id - 1} terlebih dahulu untuk membuka akses ke chapter ini."
                }
                Text(reason, color = TextPrimary, style = MaterialTheme.typography.bodySmall)
            },
            confirmButton = {
                TextButton(onClick = { previewLockedChapter = null }) {
                    Text("Tutup", color = PrimaryLavender)
                }
            }
        )
    }
}

@Composable
private fun CurvedConnector(fromLeftToRight: Boolean) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
    ) {
        val startX = if (fromLeftToRight) size.width * 0.25f else size.width * 0.75f
        val endX = if (fromLeftToRight) size.width * 0.75f else size.width * 0.25f
        val path = Path().apply {
            moveTo(startX, 4f)
            cubicTo(
                startX, size.height * 0.65f,
                endX, size.height * 0.35f,
                endX, size.height - 4f
            )
        }
        drawPath(
            path = path,
            color = BorderHighlight,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
        drawCircle(color = PrimaryLavender.copy(alpha = 0.3f), radius = 4f, center = Offset(startX, 4f))
    }
}
