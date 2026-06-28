package com.yoke.gainful.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.yoke.gainful.designsystem.components.BottomBar
import com.yoke.gainful.designsystem.components.BottomBarItem
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding(),
    ) {
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            onBack = { navigator.goBack() },
            entries = entries,
        )

        BottomBar(
            itemCount = navItems.size,
            selectedIndex = selectedIndex,
            visible = visible,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomCenter)
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
