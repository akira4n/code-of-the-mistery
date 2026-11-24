package com.example.codeofthemistery.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberpunkDarkScheme = darkColorScheme(
    primary = PrimaryLavender,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryDarkGlow,
    onPrimaryContainer = TextPrimary,
    secondary = PrimaryDeepViolet,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = TextSecondary,
    tertiary = GoldAccent,
    onTertiary = BackgroundPrimary,
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = BorderMuted,
    outlineVariant = BorderHighlight,
    error = CrimsonDanger,
    onError = TextPrimary,
    errorContainer = CrimsonDark,
    onErrorContainer = TextPrimary
)

@Composable
fun CodeOfTheMisteryTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberpunkDarkScheme,
        typography = AppTypography,
        content = content
    )
}
