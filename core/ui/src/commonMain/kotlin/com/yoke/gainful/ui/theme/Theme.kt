package com.yoke.gainful.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = NeutralGray,
    error = GainRed,
    background = Background,
    surface = Surface,
    surfaceVariant = Surface2,
    onPrimary = Background,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = Border,
)

@Composable
fun GainfulTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content,
    )
}
