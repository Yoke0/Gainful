package com.yoke.gainful

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.feature.dashboard.DashboardScreen
import com.yoke.gainful.feature.holdings.HoldingsScreen
import com.yoke.gainful.feature.settings.SettingsScreen
import com.yoke.gainful.feature.transactions.TransactionsScreen
import com.yoke.gainful.ui.components.DashboardIcon
import com.yoke.gainful.ui.components.HoldingsIcon
import com.yoke.gainful.ui.components.RecordsIcon
import com.yoke.gainful.ui.components.SettingsIcon
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted

private enum class BottomNavItem(
    val label: String,
) {
    Dashboard("仪表盘"),
    Records("记录"),
    Holdings("持仓"),
    Settings("设置"),
}

@Composable
fun App() {
    GainfulTheme {
        var showSplash by remember { mutableStateOf(true) }

        AnimatedContent(
            targetState = showSplash,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "splash",
        ) { isSplash ->
            if (isSplash) {
                SplashScreen(onSplashFinished = { showSplash = false })
            } else {
                MainContent()
            }
        }
    }
}

@Composable
private fun MainContent() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "page",
            ) { tab ->
                when (tab) {
                    0 -> DashboardScreen()
                    1 -> TransactionsScreen()
                    2 -> HoldingsScreen()
                    3 -> SettingsScreen()
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Surface.copy(alpha = 0.88f))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavItem.entries.forEachIndexed { index, item ->
                val selected = selectedTab == index
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedTab = index }
                        .then(
                            if (selected) Modifier.background(GoldDim) else Modifier
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (index) {
                        0 -> DashboardIcon(isSelected = selected)
                        1 -> RecordsIcon(isSelected = selected)
                        2 -> HoldingsIcon(isSelected = selected)
                        3 -> SettingsIcon(isSelected = selected)
                    }
                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selected) Gold else TextMuted,
                    )
                }
            }
        }
    }
}
