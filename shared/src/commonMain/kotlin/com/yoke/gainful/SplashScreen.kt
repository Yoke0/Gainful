package com.yoke.gainful

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.designsystem.components.LoadingDots
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import gainful.core.designsystem.generated.resources.app_icon
import gainful.shared.generated.resources.Res
import gainful.shared.generated.resources.app_name
import gainful.shared.generated.resources.splash_subtitle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import gainful.core.designsystem.generated.resources.Res as DsRes

private const val MIN_SPLASH_DURATION_MS = 1500L

@Composable
fun SplashScreen(
    onInit: (suspend () -> Unit)? = null,
    onSplashFinished: () -> Unit,
) {
    LaunchedEffect(Unit) {
        val startTime = Clock.System.now().toEpochMilliseconds()
        onInit?.invoke()
        val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
        val remaining = MIN_SPLASH_DURATION_MS - elapsed
        if (remaining > 0) {
            delay(remaining.milliseconds)
        }
        onSplashFinished()
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(DsRes.drawable.app_icon),
                contentDescription = stringResource(Res.string.app_name),
                modifier =
                    Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(22.dp)),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.app_name),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.02).sp,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(Res.string.splash_subtitle),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                letterSpacing = 0.02.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            LoadingDots()
        }
    }
}
