package com.example.codeofthemistery.feature.battle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.codeofthemistery.R
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.domain.model.BackgroundKey
import com.example.codeofthemistery.domain.model.EnemyAnimState
import com.example.codeofthemistery.domain.model.EnemyType
import com.example.codeofthemistery.domain.model.HeroAnimState
import kotlinx.coroutines.delay

@Composable
fun SpriteViewport(
    backgroundKey: BackgroundKey,
    heroAnim: HeroAnimState,
    enemyType: EnemyType,
    enemyAnim: EnemyAnimState,
    systemBubbleText: String?,
    showClaimButton: Boolean,
    onClaimClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgRes = when (backgroundKey) {
        BackgroundKey.STORY_ERROR -> R.drawable.bg_story_error
        BackgroundKey.DOKJA_STREET -> R.drawable.bg_dokja_street
    }

    val heroFrames = remember(heroAnim) {
        when (heroAnim) {
            HeroAnimState.IDLE -> listOf(R.drawable.hero_idle_1, R.drawable.hero_idle_2, R.drawable.hero_idle_3)
            HeroAnimState.RUN -> listOf(R.drawable.hero_run_1, R.drawable.hero_run_2)
            HeroAnimState.SKILL_1 -> listOf(R.drawable.hero_skill1_1, R.drawable.hero_skill1_2)
            HeroAnimState.SKILL_2 -> listOf(R.drawable.hero_skill2_1, R.drawable.hero_skill2_2)
            HeroAnimState.DEATH -> listOf(R.drawable.hero_death_1, R.drawable.hero_death_2)
        }
    }

    val enemyFrames = remember(enemyType, enemyAnim) {
        when (enemyType) {
            EnemyType.NONE -> emptyList()
            EnemyType.DRONE -> if (enemyAnim == EnemyAnimState.DEATH) {
                listOf(R.drawable.drone_death_1, R.drawable.drone_death_2)
            } else {
                listOf(R.drawable.drone_atk_1, R.drawable.drone_atk_2)
            }
            EnemyType.GOLEM -> if (enemyAnim == EnemyAnimState.DEATH) {
                listOf(R.drawable.golem_death_1)
            } else {
                listOf(R.drawable.golem_idle_1, R.drawable.golem_idle_2)
            }
        }
    }

    var frameTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(heroAnim, enemyType, enemyAnim) {
        frameTick = 0
        while (true) {
            delay(180)
            frameTick++
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(235.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderHighlight, RoundedCornerShape(14.dp))
    ) {
        Image(
            painter = painterResource(bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.45f))
                    )
                )
        )

        if (backgroundKey != BackgroundKey.STORY_ERROR) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val heroRes = heroFrames[frameTick % heroFrames.size]
                Image(
                    painter = painterResource(heroRes),
                    contentDescription = "Hero",
                    modifier = Modifier.size(width = 112.dp, height = 132.dp),
                    contentScale = ContentScale.Fit
                )

                if (enemyFrames.isNotEmpty()) {
                    val enemyRes = enemyFrames[frameTick % enemyFrames.size]
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (showClaimButton) {
                            Button(
                                onClick = onClaimClick,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent,
                                    contentColor = OnPrimaryDark
                                )
                            ) {
                                Text("CLAIM >>", fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        Image(
                            painter = painterResource(enemyRes),
                            contentDescription = enemyType.name,
                            modifier = Modifier.size(width = 136.dp, height = 136.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        systemBubbleText?.let { msg ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xD90A2E66),
                border = BorderStroke(1.5.dp, CyanTerminal)
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }
}
