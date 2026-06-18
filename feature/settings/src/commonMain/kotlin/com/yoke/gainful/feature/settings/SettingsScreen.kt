package com.yoke.gainful.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "设置",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
            )
            Text(
                text = "v2.1.0",
                fontSize = 12.sp,
                color = TextMuted,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preferences Group
        SettingsGroup(title = "偏好") {
            SettingToggleRow(
                label = "深色模式",
                isChecked = uiState.darkMode,
                onToggle = { viewModel.onIntent(SettingsIntent.ToggleDarkMode(it)) },
            )
            SettingSelectRow(
                label = "货币单位",
                description = "显示金额的基准货币",
                options = listOf("CNY", "USD", "HKD"),
                selectedIndex = listOf("CNY", "USD", "HKD").indexOf(uiState.currency),
                onSelected = { viewModel.onIntent(SettingsIntent.SetCurrency(it)) },
            )
            SettingSelectRow(
                label = "盈亏显示",
                description = "首页默认展示周期",
                options = listOf("今日", "本周", "本月", "今年以来"),
                selectedIndex = 1,
            )
            SettingToggleRow(
                label = "推送通知",
                description = "大额波动、成交提醒",
                isChecked = uiState.notifications,
                onToggle = { viewModel.onIntent(SettingsIntent.ToggleNotifications(it)) },
            )
            SettingToggleRow(
                label = "市值警报",
                description = "单日跌幅超过 5% 时提醒",
                isChecked = uiState.marketAlert,
                onToggle = { viewModel.onIntent(SettingsIntent.ToggleMarketAlert(it)) },
                showBorder = false,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Data & Privacy Group
        SettingsGroup(title = "数据与隐私") {
            SettingSelectRow(
                label = "数据自动刷新",
                options = listOf("每 30 秒", "每 1 分钟", "每 5 分钟", "手动"),
                selectedIndex = listOf("每 30 秒", "每 1 分钟", "每 5 分钟", "手动").indexOf(uiState.refreshInterval),
                onSelected = { viewModel.onIntent(SettingsIntent.SetRefreshInterval(it)) },
            )
            SettingToggleRow(
                label = "使用面容 ID 解锁",
                isChecked = uiState.faceId,
                onToggle = { viewModel.onIntent(SettingsIntent.ToggleFaceId(it)) },
            )
            SettingActionRow(
                label = "导出交易记录",
                actionText = "导出 CSV",
                showBorder = false,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // About Group
        SettingsGroup(title = "关于") {
            InfoRow("版本", "2.1.0 (Build 2025.03)")
            InfoRow("数据来源", "Yahoo Finance \u00B7 新浪财经")
            InfoRow("隐私政策", "查看", isLink = true)
            InfoRow("用户协议", "查看", isLink = true, showBorder = false)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Border),
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    description: String? = null,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    showBorder: Boolean = true,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(!isChecked) }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = TextPrimary,
                )
                if (description != null) {
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            ToggleSwitch(isChecked = isChecked)
        }
        if (showBorder) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Border.copy(alpha = 0.03f)),
            )
        }
    }
}

@Composable
private fun ToggleSwitch(isChecked: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(if (isChecked) Gold else Border)
            .clickable { }
            .padding(2.dp),
        contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .height(26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (isChecked) com.yoke.gainful.ui.theme.TextPrimary else TextMuted)
                .padding(horizontal = 2.dp),
        )
    }
}

@Composable
private fun SettingSelectRow(
    label: String,
    description: String? = null,
    options: List<String>,
    selectedIndex: Int = 0,
    onSelected: (String) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextPrimary,
            )
            if (description != null) {
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .clickable {
                    val nextIndex = (selectedIndex + 1) % options.size
                    onSelected(options[nextIndex])
                }
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(
                text = options[selectedIndex],
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Border.copy(alpha = 0.03f)),
    )
}

@Composable
private fun SettingActionRow(
    label: String,
    actionText: String,
    showBorder: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextPrimary,
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .clickable { }
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Text(
                text = actionText,
                fontSize = 12.sp,
                color = TextPrimary,
            )
        }
    }
    if (showBorder) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Border.copy(alpha = 0.03f)),
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isLink: Boolean = false,
    showBorder: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextPrimary,
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = if (isLink) Gold else TextMuted,
            modifier = if (isLink) Modifier.clickable { } else Modifier,
        )
    }
    if (showBorder) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Border.copy(alpha = 0.03f)),
        )
    }
}
