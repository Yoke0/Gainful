package com.yoke.gainful.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.Gold

@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    size: Dp = 6.dp,
    spacing: Dp = 6.dp,
    count: Int = 3,
) {
    val transition = rememberInfiniteTransition()

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier = modifier,
    ) {
        for (i in 0 until count) {
            val delay = i * 180
            val alpha by transition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 700, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            )
            val scale by transition.animateFloat(
                initialValue = 0.75f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 700, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            )

            Canvas(modifier = Modifier.size(size)) {
                drawCircle(
                    color = Gold.copy(alpha = alpha),
                    radius = this.size.width / 2f * scale,
                )
            }
        }
    }
}

@Composable
fun LoadingSpinner(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )
    val midRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )
    val coreAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val coreScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Box(
        modifier = modifier.size(128.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(128.dp)) {
            val stroke = Stroke(width = 2.dp.toPx())
            rotate(outerRotation) {
                drawArc(
                    color = Gold,
                    startAngle = 225f,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = stroke,
                )
                drawArc(
                    color = Gold.copy(alpha = 0.3f),
                    startAngle = 315f,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = stroke,
                )
            }
        }
        Canvas(modifier = Modifier.size(88.dp)) {
            val stroke = Stroke(width = 2.dp.toPx())
            rotate(midRotation) {
                drawArc(
                    color = Gold.copy(alpha = 0.6f),
                    startAngle = 225f,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = stroke,
                )
                drawArc(
                    color = Gold.copy(alpha = 0.2f),
                    startAngle = 135f,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = stroke,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(coreScale)
                .alpha(coreAlpha)
                .clip(CircleShape)
                .background(Gold),
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF070B15)),
        )
    }
}

@Preview
@Composable
private fun LoadingDotsPreview() {
    GainfulTheme {
        Column(
            modifier = Modifier
                .size(200.dp)
                .background(Background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LoadingDots()
            Spacer(modifier = Modifier.height(16.dp))
            LoadingDots(size = 8.dp, spacing = 8.dp, count = 5)
        }
    }
}

@Preview
@Composable
private fun LoadingSpinnerPreview() {
    GainfulTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Background),
            contentAlignment = Alignment.Center,
        ) {
            LoadingSpinner()
        }
    }
}
