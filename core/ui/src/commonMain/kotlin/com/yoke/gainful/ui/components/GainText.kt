package com.yoke.gainful.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yoke.gainful.common.extensions.formatTwoDecimals
import com.yoke.gainful.ui.theme.gainColor
import com.yoke.gainful.ui.theme.lossColor

@Composable
fun GainText(
    value: Double,
    modifier: Modifier = Modifier,
    showSign: Boolean = true,
) {
    val color = when {
        value > 0 -> gainColor
        value < 0 -> lossColor
        else -> Color.Unspecified
    }

    val sign = when {
        showSign && value > 0 -> "+"
        else -> ""
    }

    Text(
        text = "$sign${value.formatTwoDecimals()}%",
        color = color,
        modifier = modifier,
    )
}
