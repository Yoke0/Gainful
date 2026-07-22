package com.yoke.gainful.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary

@Composable
fun GainfulErrorPage(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    errorCode: String? = null,
    onRetry: (() -> Unit)? = null,
    retryLabel: String,
    onBack: (() -> Unit)? = null,
    backLabel: String,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(GainRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(40.dp)) {
                    val stroke =
                        Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        )
                    drawCircle(color = GainRed, style = stroke)
                    drawLine(
                        color = GainRed,
                        start = Offset(size.width / 2, size.height * 0.33f),
                        end = Offset(size.width / 2, size.height * 0.58f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawCircle(
                        color = GainRed,
                        radius = 1.2.dp.toPx(),
                        center = Offset(size.width / 2, size.height * 0.73f),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.widthIn(max = 260.dp),
                )
            }

            if (errorCode != null) {
                Text(
                    text = errorCode,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted,
                    letterSpacing = 0.3.sp,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(Surface)
                            .border(1.dp, Border, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                )
            }

            if (onRetry != null || onBack != null) {
                Column(
                    modifier = Modifier.widthIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (onRetry != null) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(Gold)
                                    .clickable(onClick = onRetry)
                                    .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = retryLabel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Background,
                            )
                        }
                    }
                    if (onBack != null) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(9999.dp))
                                    .border(1.dp, Border, RoundedCornerShape(9999.dp))
                                    .clickable(onClick = onBack)
                                    .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = backLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}
