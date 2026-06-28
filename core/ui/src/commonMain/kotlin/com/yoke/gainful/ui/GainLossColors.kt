package com.yoke.gainful.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.yoke.gainful.designsystem.theme.GainGreen
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.GreenDim
import com.yoke.gainful.designsystem.theme.RedDim
import com.yoke.gainful.model.GainLossColorScheme

@Immutable
data class GainLossColors(
    val gain: Color,
    val loss: Color,
    val gainDim: Color,
    val lossDim: Color,
)

val LocalGainLossColors = staticCompositionLocalOf {
    GainLossColors(
        gain = GainGreen,
        loss = GainRed,
        gainDim = GreenDim,
        lossDim = RedDim,
    )
}

val gainColor: Color
    @Composable get() = LocalGainLossColors.current.gain

val lossColor: Color
    @Composable get() = LocalGainLossColors.current.loss

val gainDimColor: Color
    @Composable get() = LocalGainLossColors.current.gainDim

val lossDimColor: Color
    @Composable get() = LocalGainLossColors.current.lossDim

fun GainLossColorScheme.toColors(): GainLossColors = when (this) {
    GainLossColorScheme.RED_UP -> GainLossColors(
        gain = GainRed,
        loss = GainGreen,
        gainDim = RedDim,
        lossDim = GreenDim,
    )
    GainLossColorScheme.GREEN_UP -> GainLossColors(
        gain = GainGreen,
        loss = GainRed,
        gainDim = GreenDim,
        lossDim = RedDim,
    )
}

@Composable
fun ProvideGainLossColors(
    scheme: GainLossColorScheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalGainLossColors provides scheme.toColors()) {
        content()
    }
}
