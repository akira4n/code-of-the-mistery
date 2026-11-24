package com.example.codeofthemistery.feature.battle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.TerminalBadge

@Composable
fun GameplayHudBar(
    chapterTitle: String,
    hp: Int,
    maxHp: Int,
    hasFirewallShield: Boolean,
    shieldChargeUsed: Boolean,
    forcePoints: Int,
    espritPoints: Int,
    onPauseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard)
            .border(1.dp, BorderHighlight)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevated)
            ) {
                Icon(Icons.Rounded.Pause, contentDescription = "Pause", tint = TextPrimary)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (hasFirewallShield) {
                    val shieldColor = if (shieldChargeUsed) TextMuted else CyanTerminal
                    TerminalBadge(
                        text = if (shieldChargeUsed) "DEF REDUCED" else "SHIELD READY",
                        color = shieldColor
                    )
                }
                Text(
                    text = chapterTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryLavender
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(
                    text = "HP $hp/$maxHp",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (hp <= 2) CrimsonDanger else TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (seg in 1..maxHp) {
                        Box(
                            modifier = Modifier
                                .size(width = 20.dp, height = 10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (seg <= hp) CrimsonDanger else SurfaceElevated)
                                .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                        )
                    }
                }
                if (hasFirewallShield) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = if (shieldChargeUsed) TextMuted else CyanTerminal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("+$forcePoints FORCE", style = MaterialTheme.typography.labelSmall, color = GoldAccent)
                Text("+$espritPoints ESPRIT", style = MaterialTheme.typography.labelSmall, color = PrimaryLavender)
            }
        }
    }
}
