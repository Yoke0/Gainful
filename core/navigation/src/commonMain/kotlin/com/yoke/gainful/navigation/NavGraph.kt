package com.yoke.gainful.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.yoke.gainful.ui.components.DashboardIcon
import com.yoke.gainful.ui.components.HoldingsIcon
import com.yoke.gainful.ui.components.RecordsIcon
import com.yoke.gainful.ui.components.SettingsIcon
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private data class TabItem(
    val screen: Screen,
    val label: String,
    val icon: @Composable (isSelected: Boolean) -> Unit,
)

private val tabs = listOf(
    TabItem(Dashboard, "仪表盘") { DashboardIcon(isSelected = it) },
    TabItem(Transactions, "记录") { RecordsIcon(isSelected = it) },
    TabItem(Holdings, "持仓") { HoldingsIcon(isSelected = it) },
    TabItem(Settings, "设置") { SettingsIcon(isSelected = it) },
)

private val tabScreens = tabs.map { it.screen }

private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Dashboard::class, Dashboard.serializer())
            subclass(Transactions::class, Transactions.serializer())
            subclass(Holdings::class, Holdings.serializer())
            subclass(Settings::class, Settings.serializer())
            subclass(AddTransaction::class, AddTransaction.serializer())
            subclass(StockDetail::class, StockDetail.serializer())
        }
    }
}

@Composable
fun GainfulNavGraph(
    screenContent: @Composable (Screen, onNavigate: (Screen) -> Unit, onBack: () -> Unit) -> Unit,
) {
    val backStack = rememberNavBackStack(navConfig, Dashboard)
    val isBottomBarVisible = (backStack.lastOrNull() ?: Dashboard) in tabScreens

    val onNavigate: (Screen) -> Unit = lambda@{ target ->
        val top = backStack.lastOrNull() ?: Dashboard
        if (target == top) return@lambda

        if (target in tabScreens) {
            backStack[backStack.lastIndex] = target
        } else {
            backStack.add(target)
        }
    }

    val onBack: () -> Unit = {
        backStack.removeLastOrNull()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding(),
    ) {
        NavDisplay(
            backStack = backStack,
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
            modifier = Modifier.weight(1f),
        )

        if (isBottomBarVisible) {
            BottomBar(backStack, onNavigate)
        }
    }
}

@Composable
private fun BottomBar(
    backStack: MutableList<NavKey>,
    onNavigate: (Screen) -> Unit,
) {
    val currentScreen = (backStack.lastOrNull() ?: Dashboard) as Screen
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
        tabs.forEach { tab ->
            val selected = currentScreen == tab.screen
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate(tab.screen) }
                    .then(if (selected) Modifier.background(GoldDim) else Modifier)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                tab.icon(selected)
                Text(
                    text = tab.label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (selected) Gold else TextMuted,
                )
            }
        }
    }
}
