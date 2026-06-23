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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted

@Composable
fun GainfulNavGraph(
    screenContent: @Composable (Screen, onNavigate: (Screen) -> Unit, onBack: () -> Unit) -> Unit,
) {
    val topLevelBackStack = remember { TopLevelBackStack<Screen>(Dashboard) }
    val currentScreen = topLevelBackStack.backStack.lastOrNull()
    val isBottomBarVisible = currentScreen in navScreens

    val onNavigate: (Screen) -> Unit = lambda@{ target ->
        if (target == topLevelBackStack.backStack.lastOrNull()) return@lambda

        if (target in navScreens) {
            topLevelBackStack.addTopLevel(target)
        } else {
            topLevelBackStack.add(target)
        }
    }

    val onBack: () -> Unit = {
        topLevelBackStack.removeLast()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding(),
    ) {
        NavDisplay(
            backStack = topLevelBackStack.backStack,
            onBack = onBack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Dashboard> { screenContent(Dashboard, onNavigate, onBack) }
                entry<Transactions> { screenContent(Transactions, onNavigate, onBack) }
                entry<Holdings> { screenContent(Holdings, onNavigate, onBack) }
                entry<Settings> { screenContent(Settings, onNavigate, onBack) }
                entry<AddTransaction> { screenContent(AddTransaction, onNavigate, onBack) }
                entry<StockDetail> { key -> screenContent(StockDetail(key.code), onNavigate, onBack) }
            },
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isBottomBarVisible) Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    else Modifier
                ),
        )

        if (isBottomBarVisible) {
            BottomBar(
                currentScreen = currentScreen,
                onNavigate = onNavigate,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun BottomBar(
    currentScreen: Screen?,
    onNavigate: (Screen) -> Unit,
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
        navItems.forEach { tab ->
            val selected = currentScreen == tab.screen
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate(tab.screen) }
                    .then(if (selected) Modifier.background(GoldDim) else Modifier)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                tab.icon(selected)
                Text(
                    text = tab.label(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selected) Gold else TextMuted,
                )
            }
        }
    }
}
