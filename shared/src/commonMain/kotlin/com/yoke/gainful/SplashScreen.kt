package com.yoke.gainful

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var startFadeOut by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (startFadeOut) 0f else 1f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing),
        label = "fade",
    )

    LaunchedEffect(Unit) {
        delay(1800)
        startFadeOut = true
        delay(400)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // App icon
            AppIcon(modifier = Modifier.size(88.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Gainful",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.02).sp,
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "智能投资，数据驱动",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                letterSpacing = 0.02.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Loading dots
            LoadingDots()
        }
    }
}

@Composable
private fun AppIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val s = size.width
        val bgRadius = s * 0.22f

        // Background with gradient
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF0A1120), Color(0xFF070B15)),
                start = Offset(0f, 0f),
                end = Offset(s, s),
            ),
            cornerRadius = CornerRadius(bgRadius),
            size = Size(s, s),
        )

        // Border
        drawRoundRect(
            color = Color(0xFF1C2640),
            cornerRadius = CornerRadius(bgRadius),
            size = Size(s, s),
            style = Stroke(width = s * 0.016f),
        )

        // Scale factor from 512 viewBox
        val scale = s / 512f

        // Gold letter G
        val gPath = Path().apply {
            // M 175 170 H 330 C 355 170 370 185 370 210 V 260 H 300 V 220 H 240 V 300 H 330 V 280 H 370 V 320 C 370 345 355 360 330 360 H 175 C 150 360 135 345 135 320 V 210 C 135 185 150 170 175 170 Z
            moveTo(175f * scale, 170f * scale)
            lineTo(330f * scale, 170f * scale)
            cubicTo(355f * scale, 170f * scale, 370f * scale, 185f * scale, 370f * scale, 210f * scale)
            lineTo(370f * scale, 260f * scale)
            lineTo(300f * scale, 260f * scale)
            lineTo(300f * scale, 220f * scale)
            lineTo(240f * scale, 220f * scale)
            lineTo(240f * scale, 300f * scale)
            lineTo(330f * scale, 300f * scale)
            lineTo(330f * scale, 280f * scale)
            lineTo(370f * scale, 280f * scale)
            lineTo(370f * scale, 320f * scale)
            cubicTo(370f * scale, 345f * scale, 355f * scale, 360f * scale, 330f * scale, 360f * scale)
            lineTo(175f * scale, 360f * scale)
            cubicTo(150f * scale, 360f * scale, 135f * scale, 345f * scale, 135f * scale, 320f * scale)
            lineTo(135f * scale, 210f * scale)
            cubicTo(135f * scale, 185f * scale, 150f * scale, 170f * scale, 175f * scale, 170f * scale)
            close()
        }
        drawPath(gPath, color = Gold)

        // Green growth arrow
        val arrowPath = Path().apply {
            // M 380 340 L 420 300 L 405 300 L 420 285 L 435 300 L 420 315 L 420 300 L 380 340 Z
            moveTo(380f * scale, 340f * scale)
            lineTo(420f * scale, 300f * scale)
            lineTo(405f * scale, 300f * scale)
            lineTo(420f * scale, 285f * scale)
            lineTo(435f * scale, 300f * scale)
            lineTo(420f * scale, 315f * scale)
            lineTo(420f * scale, 300f * scale)
            lineTo(380f * scale, 340f * scale)
            close()
        }
        drawPath(arrowPath, color = GainGreen)

        // Arrow tail decoration line (rotated rect at (412, 284) by -45deg)
        val cx = 412f * scale
        val cy = 284f * scale
        val hw = 2f * scale  // half width
        val hh = 9f * scale  // half height
        val angle = Math.toRadians(-45.0).toFloat()
        val cosA = kotlin.math.cos(angle)
        val sinA = kotlin.math.sin(angle)

        fun rotPoint(lx: Float, ly: Float): Offset {
            return Offset(cx + lx * cosA - ly * sinA, cy + lx * sinA + ly * cosA)
        }

        val tailPath = Path().apply {
            val p1 = rotPoint(-hw, -hh)
            val p2 = rotPoint(hw, -hh)
            val p3 = rotPoint(hw, hh)
            val p4 = rotPoint(-hw, hh)
            moveTo(p1.x, p1.y)
            lineTo(p2.x, p2.y)
            lineTo(p3.x, p3.y)
            lineTo(p4.x, p4.y)
            close()
        }
        drawPath(tailPath, color = GainGreen)
    }
}

@Composable
private fun LoadingDots() {
    val transition = rememberInfiniteTransition(label = "dots")

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (i in 0..2) {
            val delay = i * 200
            val alpha by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 700, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dot$i",
            )
            val scale by transition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 700, delayMillis = delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dotScale$i",
            )

            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(
                    color = Gold.copy(alpha = alpha),
                    radius = size.width / 2f * scale,
                )
            }
        }
    }
}
