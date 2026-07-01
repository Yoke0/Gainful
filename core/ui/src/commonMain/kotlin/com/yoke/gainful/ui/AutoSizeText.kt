package com.yoke.gainful.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 12.sp,
    minFontSize: TextUnit = 6.sp,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign = TextAlign.Center,
) {
    BoxWithConstraints(modifier = modifier) {
        val measuredFontSize =
            measureFontSize(
                text = text,
                maxFontSize = maxFontSize,
                minFontSize = minFontSize,
                fontWeight = fontWeight,
            )
        Text(
            text = text,
            fontSize = measuredFontSize,
            color = color,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = 1,
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.measureFontSize(
    text: String,
    maxFontSize: TextUnit,
    minFontSize: TextUnit,
    fontWeight: FontWeight?,
): TextUnit {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    return remember(text, maxFontSize, minFontSize, fontWeight, maxWidth) {
        val maxWidthPx = with(density) { maxWidth.toPx() } - 1f

        var low = minFontSize.value
        var high = maxFontSize.value
        var best = minFontSize.value

        while (low <= high) {
            val mid = (low + high) / 2f
            val style = TextStyle(fontSize = mid.sp, fontWeight = fontWeight)
            val result = textMeasurer.measure(text, style)
            val widthPx = result.size.width

            if (widthPx <= maxWidthPx) {
                best = mid
                low = mid + 0.1f
            } else {
                high = mid - 0.1f
            }
        }

        best.sp
    }
}
