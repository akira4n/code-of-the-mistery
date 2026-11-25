package com.example.codeofthemistery.feature.battle

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeofthemistery.R
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.MonoFontFamily
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.domain.model.BackgroundKey
import com.example.codeofthemistery.domain.model.EnemyAnimState
import com.example.codeofthemistery.domain.model.EnemyType
import com.example.codeofthemistery.domain.model.HeroAnimState
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun SpriteViewport(
    backgroundKey: BackgroundKey,
    heroAnim: HeroAnimState,
    enemyType: EnemyType,
    enemyAnim: EnemyAnimState,
    showAriaCompanion: Boolean,
    enemyName: String?,
    enemyHp: Int,
    enemyMaxHp: Int,
    enemyStatusLabel: String?,
    heroLungeTrigger: Int,
    heroHitTrigger: Int,
    enemyHitTrigger: Int,
    attackVfxTrigger: Int,
    screenShakeTrigger: Int,
    floatingCombatText: FloatingCombatText?,
    systemBubbleText: String?,
    showClaimButton: Boolean,
    isCompact: Boolean,
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
            EnemyType.SLIME -> if (enemyAnim == EnemyAnimState.DEATH) {
                listOf(R.drawable.slime_death_1)
            } else {
                listOf(R.drawable.slime_idle_1, R.drawable.slime_idle_2)
            }
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
            EnemyType.VEX -> listOf(R.drawable.vex_idle_1)
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

    val infiniteTransition = rememberInfiniteTransition(label = "aria_hover")
    val ariaHoverY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aria_y"
    )

    val heroOffsetX = remember { Animatable(0f) }
    val enemyOffsetX = remember { Animatable(0f) }
    val shakeOffsetX = remember { Animatable(0f) }
    val vfxProgress = remember { Animatable(0f) }
    val combatRiseY = remember { Animatable(0f) }
    val combatAlpha = remember { Animatable(0f) }

    LaunchedEffect(heroLungeTrigger) {
        if (heroLungeTrigger > 0) {
            heroOffsetX.animateTo(56f, tween(110))
            heroOffsetX.animateTo(0f, spring(dampingRatio = 0.65f))
        }
    }

    LaunchedEffect(heroHitTrigger) {
        if (heroHitTrigger > 0) {
            heroOffsetX.animateTo(-26f, tween(90))
            heroOffsetX.animateTo(0f, spring(dampingRatio = 0.7f))
        }
    }

    LaunchedEffect(enemyHitTrigger) {
        if (enemyHitTrigger > 0) {
            enemyOffsetX.animateTo(26f, tween(90))
            enemyOffsetX.animateTo(-8f, tween(70))
            enemyOffsetX.animateTo(0f, spring())
        }
    }

    LaunchedEffect(screenShakeTrigger) {
        if (screenShakeTrigger > 0) {
            for (amp in listOf(14f, -12f, 9f, -6f, 0f)) {
                shakeOffsetX.animateTo(amp, tween(45))
            }
        }
    }

    LaunchedEffect(attackVfxTrigger) {
        if (attackVfxTrigger > 0) {
            vfxProgress.snapTo(0.01f)
            vfxProgress.animateTo(1f, tween(420, easing = LinearOutSlowInEasing))
            vfxProgress.snapTo(0f)
        }
    }

    LaunchedEffect(floatingCombatText?.id) {
        if (floatingCombatText != null) {
            combatRiseY.snapTo(0f)
            combatAlpha.snapTo(1f)
            combatRiseY.animateTo(-42f, tween(750, easing = LinearOutSlowInEasing))
            combatAlpha.animateTo(0f, tween(220))
        }
    }

    val viewportHeight by animateDpAsState(
        targetValue = if (isCompact) 136.dp else 210.dp,
        animationSpec = tween(220),
        label = "viewport_h"
    )
    val heroSize = if (isCompact) 88.dp else 120.dp
    val enemySize = when {
        isCompact -> 94.dp
        enemyType == EnemyType.SLIME -> 108.dp
        else -> 130.dp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(viewportHeight)
            .offset { IntOffset(shakeOffsetX.value.roundToInt(), 0) }
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
                        colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.52f))
                    )
                )
        )

        if (backgroundKey != BackgroundKey.STORY_ERROR) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.offset { IntOffset(heroOffsetX.value.roundToInt(), 0) }
                ) {
                    if (showAriaCompanion) {
                        val ariaRes = if (frameTick % 2 == 0) R.drawable.aria_orb_1 else R.drawable.aria_orb_2
                        Image(
                            painter = painterResource(ariaRes),
                            contentDescription = "ARIA",
                            modifier = Modifier
                                .size(if (isCompact) 36.dp else 48.dp)
                                .offset { IntOffset(0, ariaHoverY.roundToInt()) }
                                .padding(bottom = 12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    val heroRes = heroFrames[frameTick % heroFrames.size]
                    Image(
                        painter = painterResource(heroRes),
                        contentDescription = "Hero",
                        modifier = Modifier.size(heroSize),
                        contentScale = ContentScale.Fit
                    )
                }

                if (enemyFrames.isNotEmpty()) {
                    val enemyRes = enemyFrames[frameTick % enemyFrames.size]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset { IntOffset(enemyOffsetX.value.roundToInt(), 0) }
                    ) {
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
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Image(
                            painter = painterResource(enemyRes),
                            contentDescription = enemyType.name,
                            modifier = Modifier.size(enemySize),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        // Particle & slash FX Canvas overlay
        val p = vfxProgress.value
        if (p > 0f && p < 1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width * if (enemyType != EnemyType.NONE) 0.74f else 0.30f
                val centerY = size.height * 0.62f
                val alpha = (1f - p).coerceIn(0f, 1f)

                drawLine(
                    color = PrimaryLavender.copy(alpha = alpha),
                    start = Offset(centerX - 64f, centerY - 52f),
                    end = Offset(centerX - 64f + 128f * p, centerY - 52f + 104f * p),
                    strokeWidth = 7f * (1f - p * 0.4f),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = CyanTerminal.copy(alpha = alpha * 0.9f),
                    start = Offset(centerX + 54f, centerY - 46f),
                    end = Offset(centerX + 54f - 108f * p, centerY - 46f + 92f * p),
                    strokeWidth = 5f * (1f - p * 0.4f),
                    cap = StrokeCap.Round
                )

                for (i in 0 until 8) {
                    val angle = (i * 45.0) * PI / 180.0
                    val dist = 16f + 78f * p
                    val sx = centerX + (cos(angle) * dist).toFloat()
                    val sy = centerY + (sin(angle) * dist).toFloat()
                    val sparkColor = if (i % 2 == 0) PrimaryLavender else GoldAccent
                    drawCircle(
                        color = sparkColor.copy(alpha = alpha),
                        radius = 5f * (1f - p * 0.5f),
                        center = Offset(sx, sy)
                    )
                }
            }
        }

        // Top HUD row: optional system alert bubble + Enemy HP bar (never overlaps on narrow widths)
        val hasEnemyHud = enemyMaxHp > 0 && enemyType != EnemyType.NONE && backgroundKey != BackgroundKey.STORY_ERROR
        if (hasEnemyHud || systemBubbleText != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = if (systemBubbleText != null && !hasEnemyHud) {
                    Arrangement.Center
                } else {
                    Arrangement.spacedBy(8.dp)
                },
                verticalAlignment = Alignment.Top
            ) {
                if (systemBubbleText != null) {
                    Surface(
                        modifier = if (hasEnemyHud) Modifier.weight(1f) else Modifier.fillMaxWidth(0.88f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xDD0A2E66),
                        border = BorderStroke(1.2.dp, CyanTerminal)
                    ) {
                        Text(
                            text = systemBubbleText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                } else if (hasEnemyHud) {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (hasEnemyHud) {
                    EnemyHpOverlayPill(
                        name = enemyName ?: enemyType.name,
                        hp = enemyHp,
                        maxHp = enemyMaxHp,
                        statusLabel = enemyStatusLabel,
                        modifier = Modifier.width(148.dp)
                    )
                }
            }
        }

        // Floating combat numbers
        floatingCombatText?.let { popup ->
            if (combatAlpha.value > 0.02f) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset { IntOffset(46, combatRiseY.value.roundToInt()) }
                        .alpha(combatAlpha.value),
                    shape = RoundedCornerShape(8.dp),
                    color = BackgroundPrimary.copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, if (popup.isPositive) GoldAccent else CrimsonDanger)
                ) {
                    Text(
                        text = popup.text,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        ),
                        color = if (popup.isPositive) GoldAccent else CrimsonDanger,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnemyHpOverlayPill(
    name: String,
    hp: Int,
    maxHp: Int,
    statusLabel: String?,
    modifier: Modifier = Modifier
) {
    val hpFraction by animateFloatAsState(
        targetValue = (hp.toFloat() / maxHp.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f),
        animationSpec = tween(300),
        label = "enemy_hp"
    )
    Surface(
        modifier = modifier.width(156.dp),
        shape = RoundedCornerShape(10.dp),
        color = BackgroundPrimary.copy(alpha = 0.86f),
        border = BorderStroke(1.dp, CrimsonDanger.copy(alpha = 0.65f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1
                )
                statusLabel?.let { tag ->
                    val tagColor = when (tag) {
                        "SHIELDED" -> CyanTerminal
                        "EXPOSED", "WEAKENED" -> GoldAccent
                        "DEFEATED" -> SuccessGreen
                        else -> PrimaryLavender
                    }
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = tagColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SurfaceElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(hpFraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (hpFraction <= 0.35f) GoldAccent else CrimsonDanger)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "$hp / $maxHp HP",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp
                ),
                color = CrimsonDanger
            )
        }
    }
}
