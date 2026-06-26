package com.yoke.gainful.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted

@Composable
fun GainfulNavGraph(
    navigationState: NavigationState,
    navigator: Navigator,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
) {
    val isTopLevel = navigationState.currentKey == navigationState.currentTopLevelKey

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding(),
    ) {
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isTopLevel) Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(bottom = 80.dp)
                    else Modifier,
                ),
            onBack = { navigator.goBack() },
            entries = navigationState.toEntries(entryProvider),
        )

        BottomBar(
            selectedKey = navigationState.currentTopLevelKey,
            onSelectKey = { navigator.navigate(it) },
            visible = isTopLevel,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun BottomBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val keys = TOP_LEVEL_NAV_ITEMS.keys.toList()
    val selectedIndex = keys.indexOf(selectedKey).coerceAtLeast(0)
    val tabsCount = TOP_LEVEL_NAV_ITEMS.size

    var rowSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val tabWidthDp = if (rowSize.width > 0) with(density) { (rowSize.width / tabsCount).toDp() } else 0.dp
    val rowHeightDp = if (rowSize.height > 0) with(density) { rowSize.height.toDp() } else 0.dp

    val animatedOffset by animateDpAsState(
        targetValue = tabWidthDp * selectedIndex,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (visible) 1f else 0f)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Surface.copy(alpha = 0.88f))
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        if (rowSize.width > 0) {
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset)
                    .size(tabWidthDp, rowHeightDp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GoldDim),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { rowSize = it.size },
        ) {
            TOP_LEVEL_NAV_ITEMS.forEach { (key, item) ->
                val selected = key == selectedKey
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (visible) Modifier.clickable { onSelectKey(key) } else Modifier,
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item.icon(selected)
                    Text(
                        text = item.label(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selected) Gold else TextMuted,
                    )
                }
            }
        }
    }
}
