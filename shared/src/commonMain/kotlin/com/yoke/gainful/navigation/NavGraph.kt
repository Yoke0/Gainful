package com.yoke.gainful.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import com.yoke.gainful.designsystem.components.BottomBar
import com.yoke.gainful.designsystem.components.BottomBarItem
import com.yoke.gainful.designsystem.components.NavigationRail
import com.yoke.gainful.designsystem.components.NavigationRailItem
import com.yoke.gainful.designsystem.components.platformNavigationBarsPadding
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.TextMuted

@Composable
fun GainfulNavGraph(
    navigationState: NavigationState,
    navigator: Navigator,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
) {
    val entries = navigationState.toEntries(entryProvider)

    val keys = TOP_LEVEL_NAV_ITEMS.keys.toList()
    val navItems = TOP_LEVEL_NAV_ITEMS.values.toList()
    val visible = navigationState.currentKey == navigationState.currentTopLevelKey
    val selectedIndex = keys.indexOf(navigationState.currentTopLevelKey).coerceAtLeast(0)

    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isLargeScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Background)
                .statusBarsPadding(),
    ) {
        NavigationRail(
            visible = isLargeScreen && visible,
        ) {
            navItems.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                NavigationRailItem(
                    onClick = { navigator.navigate(keys[index]) },
                    isSelected = selected,
                    icon = { item.icon(selected) },
                    label = {
                        Text(
                            text = item.label(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selected) Gold else TextMuted,
                        )
                    },
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                onBack = { navigator.goBack() },
                entries = entries,
            )

            BottomBar(
                itemCount = navItems.size,
                selectedIndex = selectedIndex,
                visible = isLargeScreen.not() && visible,
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .platformNavigationBarsPadding()
                        .align(Alignment.BottomCenter),
            ) {
                navItems.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    BottomBarItem(
                        onClick = { navigator.navigate(keys[index]) },
                        modifier = Modifier.weight(1f),
                        icon = { item.icon(selected) },
                        label = {
                            Text(
                                text = item.label(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selected) Gold else TextMuted,
                            )
                        },
                    )
                }
            }
        }
    }
}
