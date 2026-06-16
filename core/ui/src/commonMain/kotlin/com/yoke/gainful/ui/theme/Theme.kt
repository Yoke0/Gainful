package com.yoke.gainful.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GainGreen,
    secondary = NeutralGray,
    error = GainRed,
)

private val LightColorScheme = lightColorScheme(
    primary = GainGreen,
    secondary = NeutralGray,
    error = GainRed,
)

@Composable
fun GainfulTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
