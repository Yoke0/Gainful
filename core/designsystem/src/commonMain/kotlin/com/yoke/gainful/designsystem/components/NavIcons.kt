package com.yoke.gainful.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.TextMuted
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DashboardIcon(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 26.dp,
) {
    val color = if (isSelected) Gold else TextMuted
    val progress = remember { Animatable(0f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(400))
        } else {
            progress.snapTo(0f)
        }
    }

    Canvas(modifier = modifier.size(iconSize)) {
        val s = this.size.width
        val sw = 1.6f
        val gap = s * 0.08f
        val rx = CornerRadius(s * 0.12f, s * 0.12f)
        val p = progress.value
        val dashEffect = if (p in 0.01f..0.99f) {
            val dashLen = s * 0.3f * p
            PathEffect.dashPathEffect(floatArrayOf(dashLen, s * 3f))
        } else null

        val stroke = Stroke(
            width = sw,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = dashEffect,
        )

        drawRoundRect(
            color = color,
            topLeft = Offset(gap, gap),
            size = Size(s * 0.39f, s * 0.39f),
            cornerRadius = rx,
            style = stroke,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(s * 0.53f, gap),
            size = Size(s * 0.39f, s * 0.24f),
            cornerRadius = rx,
            style = stroke,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(s * 0.53f, s * 0.53f),
            size = Size(s * 0.39f, s * 0.39f),
            cornerRadius = rx,
            style = stroke,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(gap, s * 0.58f),
            size = Size(s * 0.39f, s * 0.34f),
            cornerRadius = rx,
            style = stroke,
        )
    }
}

@Composable
fun RecordsIcon(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 26.dp,
) {
    val color = if (isSelected) Gold else TextMuted
    val progress = remember { Animatable(0f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(400))
        } else {
            progress.snapTo(0f)
        }
    }

    Canvas(modifier = modifier.size(iconSize)) {
        val s = this.size.width
        val sw = 1.6f
        val p = progress.value
        val dashEffect = if (p in 0.01f..0.99f) {
            val dashLen = s * 0.3f * p
            PathEffect.dashPathEffect(floatArrayOf(dashLen, s * 3f))
        } else null

        val stroke = Stroke(
            width = sw,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = dashEffect,
        )

        val docLeft = s * 0.18f
        val docTop = s * 0.08f
        val docRight = s * 0.82f
        val docBottom = s * 0.92f
        val fold = s * 0.22f

        val path = Path().apply {
            moveTo(docLeft, docTop)
            lineTo(docRight - fold, docTop)
            lineTo(docRight, docTop + fold)
            lineTo(docRight, docBottom)
            lineTo(docLeft, docBottom)
            close()
        }
        drawPath(path, color = color, style = stroke)

        val lineLeft = s * 0.30f
        val lineRight = s * 0.70f
        drawLine(color, Offset(lineLeft, s * 0.48f), Offset(lineRight, s * 0.48f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(lineLeft, s * 0.62f), Offset(lineRight, s * 0.62f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
fun HoldingsIcon(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 26.dp,
) {
    val color = if (isSelected) Gold else TextMuted
    val progress = remember { Animatable(0f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(400))
        } else {
            progress.snapTo(0f)
        }
    }

    Canvas(modifier = modifier.size(iconSize)) {
        val s = this.size.width
        val sw = 1.6f
        val p = progress.value
        val dashEffect = if (p in 0.01f..0.99f) {
            val dashLen = s * 0.3f * p
            PathEffect.dashPathEffect(floatArrayOf(dashLen, s * 3f))
        } else null

        val stroke = Stroke(
            width = sw,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = dashEffect,
        )

        drawRoundRect(
            color = color,
            topLeft = Offset(s * 0.08f, s * 0.32f),
            size = Size(s * 0.84f, s * 0.56f),
            cornerRadius = CornerRadius(s * 0.06f),
            style = stroke,
        )

        val handle = Path().apply {
            moveTo(s * 0.36f, s * 0.32f)
            lineTo(s * 0.36f, s * 0.22f)
            quadraticTo(s * 0.36f, s * 0.14f, s * 0.50f, s * 0.14f)
            quadraticTo(s * 0.64f, s * 0.14f, s * 0.64f, s * 0.22f)
            lineTo(s * 0.64f, s * 0.32f)
        }
        drawPath(handle, color = color, style = stroke)

        drawCircle(
            color = color,
            radius = s * 0.06f,
            center = Offset(s * 0.50f, s * 0.62f),
        )
    }
}

@Composable
fun SettingsIcon(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 26.dp,
) {
    val color = if (isSelected) Gold else TextMuted
    val progress = remember { Animatable(0f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(400))
        } else {
            progress.snapTo(0f)
        }
    }

    Canvas(modifier = modifier.size(iconSize)) {
        val s = this.size.width
        val sw = 1.6f
        val cx = s * 0.50f
        val cy = s * 0.50f
        val p = progress.value
        val dashEffect = if (p in 0.01f..0.99f) {
            val dashLen = s * 0.15f * p
            PathEffect.dashPathEffect(floatArrayOf(dashLen, s * 3f))
        } else null

        val stroke = Stroke(
            width = sw,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = dashEffect,
        )

        drawCircle(
            color = color,
            radius = s * 0.14f,
            center = Offset(cx, cy),
            style = stroke,
        )

        val rayInner = s * 0.24f
        val rayOuter = s * 0.42f
        for (i in 0 until 8) {
            val angle = (i * 45.0) * PI / 180.0
            val x1 = cx + (rayInner * cos(angle)).toFloat()
            val y1 = cy + (rayInner * sin(angle)).toFloat()
            val x2 = cx + (rayOuter * cos(angle)).toFloat()
            val y2 = cy + (rayOuter * sin(angle)).toFloat()
            drawLine(color, Offset(x1, y1), Offset(x2, y2), strokeWidth = sw, cap = StrokeCap.Round)
        }
    }
}
