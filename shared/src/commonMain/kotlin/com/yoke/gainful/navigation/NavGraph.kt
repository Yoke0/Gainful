package com.yoke.gainful.navigation

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
                    else Modifier
                ),
            onBack = {
                navigator.goBack()
            },
            entries = navigationState.toEntries(entryProvider),
        )

        if (isTopLevel) {
            BottomBar(
                selectedKey = navigationState.currentTopLevelKey,
                onSelectKey = { navigator.navigate(it) },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun BottomBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Surface.copy(alpha = 0.88f))
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        TOP_LEVEL_NAV_ITEMS.forEach { (key, item) ->
            val selected = key == selectedKey
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectKey(key) }
                    .then(if (selected) Modifier.background(GoldDim) else Modifier)
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
