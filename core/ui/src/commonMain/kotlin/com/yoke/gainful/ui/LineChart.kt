package com.yoke.gainful.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GridLine

@Composable
fun LineChart(
    data: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier,
    lineColor: Color = Gold,
    showBaseline: Boolean = false,
    baselineY: Float = 0f,
    baselineColor: Color = Color.Unspecified,
    showGridLines: Boolean = false,
    gradientFill: Boolean = false,
) {
    if (data.size < 2) return

    val yValues = data.map { it.second }
    val minVal = yValues.min()
    val maxVal = yValues.max()
    val range = maxVal - minVal

    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(342f / 140f),
    ) {
        val w = size.width
        val h = size.height
        val padTop = 10f
        val padBottom = 10f
        val chartH = h - padTop - padBottom

        if (showGridLines) {
            for (i in 0..2) {
                val y = padTop + chartH * i / 2f
                drawLine(
                    color = GridLine,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f,
                )
            }
        }

        if (showBaseline) {
            val normalizedBaseline =
                if (range > 0) (baselineY - minVal) / range else 0.5f
            val baselineScreenY = padTop + chartH * (1f - normalizedBaseline)
            val resolvedBaselineColor = if (baselineColor != Color.Unspecified) baselineColor else lineColor
            drawLine(
                color = resolvedBaselineColor,
                start = Offset(0f, baselineScreenY),
                end = Offset(w, baselineScreenY),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
            )
        }

        val linePath = Path()
        val fillPath = Path()
        val stepX = w / (data.size - 1).toFloat()

        data.forEachIndexed { index, (_, yValue) ->
            val x = index * stepX
            val normalized = if (range > 0) (yValue - minVal) / range else 0.5f
            val y = padTop + chartH * (1f - normalized)

            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        if (gradientFill) {
            fillPath.lineTo(w, h)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush =
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                lineColor.copy(alpha = 0.45f),
                                lineColor.copy(alpha = 0.12f),
                                lineColor.copy(alpha = 0.02f),
                            ),
                        startY = 0f,
                        endY = h,
                    ),
            )
        }

        drawPath(
            path = linePath,
            color = lineColor,
            style =
                Stroke(
                    width = 2.5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
        )
    }
}

@Preview(name = "Basic")
@Composable
private fun LineChartBasicPreview() {
    val data = listOf(10f, 15f, 8f, 20f, 12f, 18f, 25f, 14f).mapIndexed { i, v -> i.toFloat() to v }
    Column(
        modifier = Modifier.padding(16.dp).background(Background),
    ) {
        LineChart(data = data)
    }
}

@Preview(name = "With Baseline")
@Composable
private fun LineChartBaselinePreview() {
    val data = listOf(-5f, 3f, -2f, 8f, 1f, -3f, 6f, -1f).mapIndexed { i, v -> i.toFloat() to v }
    Column(
        modifier = Modifier.padding(16.dp).background(Background),
    ) {
        LineChart(
            data = data,
            showBaseline = true,
            baselineY = 0f,
        )
    }
}

@Preview(name = "With Grid Lines & Gradient")
@Composable
private fun LineChartGridAndGradientPreview() {
    val data = listOf(100f, 110f, 95f, 120f, 105f, 130f, 115f, 140f, 125f).mapIndexed { i, v -> i.toFloat() to v }
    Column(
        modifier = Modifier.padding(16.dp).background(Background),
    ) {
        LineChart(
            data = data,
            showGridLines = true,
            gradientFill = true,
        )
    }
}

@Preview(name = "Red Line With Baseline")
@Composable
private fun LineChartRedBaselinePreview() {
    val data = listOf(-8f, -3f, 2f, -5f, 4f, -1f, 6f, -2f, 3f).mapIndexed { i, v -> i.toFloat() to v }
    Column(
        modifier = Modifier.padding(16.dp).background(Background),
    ) {
        LineChart(
            data = data,
            lineColor = GainRed,
            showBaseline = true,
            baselineY = 0f,
            showGridLines = true,
            gradientFill = true,
        )
    }
}
