package com.yoke.gainful.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.BuildConfig
import com.yoke.gainful.ui.components.TimePickerDialog
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
        Text(
            text = "设置",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsGroup(title = "数据") {
            SettingRow(
                label = "股票数据刷新频率",
                value = uiState.refreshDisplay,
                onClick = { viewModel.onIntent(SettingsIntent.ShowFreqPicker(true)) },
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        SettingsGroup(title = "交易时段") {
            SettingRow(
                label = "开盘时间",
                value = uiState.openTimeDisplay,
                valueColor = Gold,
                onClick = { viewModel.onIntent(SettingsIntent.ShowTimePicker(TimePickerTarget.OPEN)) },
            )
            SettingRow(
                label = "收盘时间",
                value = uiState.closeTimeDisplay,
                valueColor = Gold,
                onClick = { viewModel.onIntent(SettingsIntent.ShowTimePicker(TimePickerTarget.CLOSE)) },
                showBorder = false,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        SettingsGroup(title = "关于") {
            SettingRow(
                label = "APP 版本",
                value = "v${BuildConfig.APP_VERSION}",
                showArrow = false,
                showBorder = false,
                onClick = {},
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (uiState.showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                when (uiState.timePickerTarget) {
                    TimePickerTarget.OPEN -> viewModel.onIntent(SettingsIntent.SetOpenTime(hour, minute))
                    TimePickerTarget.CLOSE -> viewModel.onIntent(SettingsIntent.SetCloseTime(hour, minute))
                }
                viewModel.onIntent(SettingsIntent.DismissTimePicker)
            },
            onDismiss = { viewModel.onIntent(SettingsIntent.DismissTimePicker) },
        )
    }

    if (uiState.showFreqPicker) {
        FrequencyPickerDialog(
            selected = uiState.refreshMinutes,
            onConfirm = { viewModel.onIntent(SettingsIntent.SetRefreshMinutes(it)) },
            onDismiss = { viewModel.onIntent(SettingsIntent.ShowFreqPicker(false)) },
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(10.dp)),
        ) {
            content()
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = TextSecondary,
    showArrow: Boolean = true,
    showBorder: Boolean = true,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = valueColor,
                    fontFamily = if (valueColor == Gold) FontFamily.Monospace else FontFamily.Default,
                )
                if (showArrow) {
                    Text(
                        text = "\u25BE",
                        fontSize = 12.sp,
                        color = TextMuted,
                    )
                }
            }
        }
        if (showBorder) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Border),
            )
        }
    }
}

@Composable
private fun FrequencyPickerDialog(
    selected: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var workingSelection by remember { mutableIntStateOf(selected) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(14.dp))
                .padding(20.dp),
        ) {
            Text(
                text = "刷新频率",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            listOf(1, 3, 10).forEach { minutes ->
                val isSelected = minutes == workingSelection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) GoldDim else Surface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Gold else Border,
                            shape = RoundedCornerShape(6.dp),
                        )
                        .clickable { workingSelection = minutes }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$minutes 分钟",
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Gold else TextPrimary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(50))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "取消",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Gold)
                        .clickable { onConfirm(workingSelection); onDismiss() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "确认",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Background,
                    )
                }
            }
        }
    }
}
