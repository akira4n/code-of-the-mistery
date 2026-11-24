package com.example.codeofthemistery.feature.auth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun TerminalLoadingScreen(
    statusHeadline: String = "Entering the Terminal Code...",
    onFinished: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        delay(320)
        step = 2
        delay(340)
        step = 3
        delay(420)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(84.dp),
                    color = PrimaryLavender,
                    trackColor = BorderHighlight,
                    strokeWidth = 5.dp
                )
                Text(
                    text = ">_",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryLavender
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .border(1.dp, BorderHighlight, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(CrimsonDanger))
                        Box(Modifier.size(8.dp).clip(CircleShape).background(GoldAccent))
                        Box(Modifier.size(8.dp).clip(CircleShape).background(SuccessGreen))
                    }
                    Text("SYS_BOOT_ENV_V1.0", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                HorizontalDivider(color = BorderHighlight)
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TerminalLogRow("01", "Initializing Terminal...", if (step >= 1) "[OK]" else "...", SuccessGreen)
                    TerminalLogRow("02", "Decrypting User Profile...", if (step >= 2) "[OK]" else "...", SuccessGreen)
                    TerminalLogRow("03", "Synchronizing XP...", if (step >= 3) "0x7F" else "...", PrimaryLavender)
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = statusHeadline,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TerminalLogRow(num: String, label: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$num  $label", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(status, style = MaterialTheme.typography.bodySmall, color = statusColor)
    }
}
