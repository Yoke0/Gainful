package com.yoke.gainful.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface

val BottomBarHeight = 80.dp

@Composable
fun Modifier.bottomBarPadding(): Modifier =
    this
        .padding(bottom = BottomBarHeight)
        .platformNavigationBarsPadding()

@Composable
fun BottomBar(
    itemCount: Int,
    selectedIndex: Int,
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val slideOffset = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(visible) {
        if (visible) {
            alpha.animateTo(1f, tween(150))
            slideOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        } else {
            slideOffset.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            alpha.animateTo(0f, tween(150))
        }
    }

    var rowSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val tabWidthDp = if (rowSize.width > 0) with(density) { (rowSize.width / itemCount).toDp() } else 0.dp

    val animatedIndicatorOffset by animateDpAsState(
        targetValue = tabWidthDp * selectedIndex,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    )

    Box(
        modifier =
            modifier
                .offset(y = (slideOffset.value * BottomBarHeight.value).dp)
                .alpha(alpha.value)
                .fillMaxWidth()
                .height(BottomBarHeight)
                .clip(RoundedCornerShape(22.dp))
                .background(Surface.copy(alpha = 0.88f))
                .padding(8.dp),
    ) {
        if (rowSize.width > 0) {
            Box(
                modifier =
                    Modifier
                        .offset(x = animatedIndicatorOffset)
                        .width(tabWidthDp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GoldDim),
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { rowSize = it.size },
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
fun BottomBarItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon()
        label()
    }
}
