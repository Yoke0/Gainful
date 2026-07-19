package com.yoke.gainful.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import gainful.core.designsystem.generated.resources.Res
import gainful.core.designsystem.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource

val NavigationRailWidth = 80.dp

@Composable
fun NavigationRail(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val animatedWidth by animateDpAsState(
        targetValue = if (visible) NavigationRailWidth else 0.dp,
        animationSpec = tween(durationMillis = 300),
    )

    Box(modifier = modifier.width(animatedWidth).fillMaxHeight().clipToBounds()) {
        Column(
            modifier =
                Modifier
                    .width(NavigationRailWidth)
                    .fillMaxHeight()
                    .background(Surface.copy(alpha = 0.96f))
                    .drawBehind {
                        drawLine(
                            color = Border,
                            start = Offset(size.width, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                    .padding(top = 24.dp, bottom = 20.dp)
                    .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Brand
            Image(
                painter = painterResource(Res.drawable.app_icon),
                contentDescription = "Gainful",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nav items
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = content,
            )
        }
    }
}

@Composable
fun NavigationRailItem(
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
) {
    val bgAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.15f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GoldDim.copy(alpha = bgAlpha))
                .clickable(onClick = onClick)
                .drawBehind {
                    if (isSelected) {
                        drawRoundRect(
                            color = Gold,
                            topLeft = Offset(4.dp.toPx(), (size.height / 2) - 14.dp.toPx()),
                            size = Size(3.dp.toPx(), 28.dp.toPx()),
                            cornerRadius = CornerRadius(4.dp.toPx()),
                        )
                    }
                }
                .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            icon()
            label()
        }
    }
}
