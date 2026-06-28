package com.yoke.gainful.feature.settings.overview

import com.yoke.gainful.ui.components.GainfulTopAppBar
import gainful.feature.settings.generated.resources.Res
import gainful.feature.settings.generated.resources.about_group
import gainful.feature.settings.generated.resources.app_version
import gainful.feature.settings.generated.resources.cancel
import gainful.feature.settings.generated.resources.close_time
import gainful.feature.settings.generated.resources.color_demo_down
import gainful.feature.settings.generated.resources.color_demo_up
import gainful.feature.settings.generated.resources.color_green_up
import gainful.feature.settings.generated.resources.color_red_up
import gainful.feature.settings.generated.resources.confirm
import gainful.feature.settings.generated.resources.data_management_group
import gainful.feature.settings.generated.resources.export_confirm
import gainful.feature.settings.generated.resources.export_dialog_title
import gainful.feature.settings.generated.resources.export_done
import gainful.feature.settings.generated.resources.export_record_count
import gainful.feature.settings.generated.resources.export_success
import gainful.feature.settings.generated.resources.export_success_msg
import gainful.feature.settings.generated.resources.export_transactions
import gainful.feature.settings.generated.resources.file_name_label
import gainful.feature.settings.generated.resources.gain_loss_color
import gainful.feature.settings.generated.resources.gain_loss_color_title
import gainful.feature.settings.generated.resources.gain_label
import gainful.feature.settings.generated.resources.loss_label
import gainful.feature.settings.generated.resources.import_transactions
import gainful.feature.settings.generated.resources.minutes_format
import gainful.feature.settings.generated.resources.open_time
import gainful.feature.settings.generated.resources.refresh_frequency
import gainful.feature.settings.generated.resources.refresh_frequency_title
import gainful.feature.settings.generated.resources.settings_data_group
import gainful.feature.settings.generated.resources.settings_title
import gainful.feature.settings.generated.resources.trading_hours_group
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.yoke.gainful.file.rememberCsvFileUtil
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.model.GainLossColorScheme
import com.yoke.gainful.ui.components.BottomBarHeight
import com.yoke.gainful.ui.components.GainfulDialog
import com.yoke.gainful.ui.components.TimePickerDialog
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.GreenDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.stringArrayResource
import gainful.feature.settings.generated.resources.csv_headers
import gainful.feature.settings.generated.resources.csv_type_values

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToImport: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val fileUtil = rememberCsvFileUtil()
    val csvHeaders = stringArrayResource(Res.array.csv_headers)
    val csvTypeValues = stringArrayResource(Res.array.csv_type_values)
    val csvConfig = remember(csvHeaders, csvTypeValues) {
        CsvConfig(headers = csvHeaders, typeValues = csvTypeValues)
    }

    LaunchedEffect(uiState.exportCsvContent) {
        uiState.exportCsvContent?.let { csv ->
            fileUtil.saveFile(
                fileName = "${uiState.exportFileName}.csv",
                content = csv,
            ) { success ->
                viewModel.onExportFileSaved(success)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        GainfulTopAppBar(
            title = stringResource(Res.string.settings_title),
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsGroup(title = stringResource(Res.string.settings_data_group)) {
            SettingRow(
                label = stringResource(Res.string.refresh_frequency),
                value = stringResource(Res.string.minutes_format, uiState.refreshMinutes),
                onClick = { viewModel.onIntent(SettingsIntent.ShowFreqPicker(true)) },
            )
            ColorSettingRow(
                label = stringResource(Res.string.gain_loss_color),
                scheme = uiState.gainLossColorScheme,
                onClick = { viewModel.onIntent(SettingsIntent.ShowColorPicker(true)) },
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        SettingsGroup(title = stringResource(Res.string.trading_hours_group)) {
            SettingRow(
                label = stringResource(Res.string.open_time),
                value = uiState.openTimeDisplay,
                valueColor = Gold,
                onClick = { viewModel.onIntent(SettingsIntent.ShowTimePicker(TimePickerTarget.OPEN)) },
            )
            SettingRow(
                label = stringResource(Res.string.close_time),
                value = uiState.closeTimeDisplay,
                valueColor = Gold,
                onClick = { viewModel.onIntent(SettingsIntent.ShowTimePicker(TimePickerTarget.CLOSE)) },
                showBorder = false,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        SettingsGroup(title = stringResource(Res.string.data_management_group)) {
            SettingRow(
                label = stringResource(Res.string.export_transactions),
                value = "CSV",
                onClick = { viewModel.onIntent(SettingsIntent.ShowExportDialog(true)) },
            )
            SettingRow(
                label = stringResource(Res.string.import_transactions),
                value = "CSV",
                showBorder = false,
                onClick = onNavigateToImport,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        SettingsGroup(title = stringResource(Res.string.about_group)) {
            SettingRow(
                label = stringResource(Res.string.app_version),
                value = "v${BuildConfig.APP_VERSION}",
                showArrow = false,
                showBorder = false,
                onClick = {},
            )
        }

        Spacer(modifier = Modifier.height(BottomBarHeight))
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

    if (uiState.showColorPicker) {
        ColorPickerDialog(
            selected = uiState.gainLossColorScheme,
            onConfirm = { viewModel.onIntent(SettingsIntent.SetGainLossColorScheme(it)) },
            onDismiss = { viewModel.onIntent(SettingsIntent.ShowColorPicker(false)) },
        )
    }

    if (uiState.showExportDialog) {
        ExportDialog(
            recordCount = uiState.exportRecordCount,
            fileName = uiState.exportFileName,
            onConfirm = { viewModel.onIntent(SettingsIntent.ConfirmExport(csvConfig)) },
            onDismiss = { viewModel.onIntent(SettingsIntent.ShowExportDialog(false)) },
        )
    }

    if (uiState.showExportResult && uiState.exportResult != null) {
        ExportResultDialog(
            fileName = uiState.exportResult!!.fileName,
            onDone = { viewModel.onIntent(SettingsIntent.DismissExportResult) },
        )
    }
}

@Composable
private fun ExportDialog(
    recordCount: Int,
    fileName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    GainfulDialog(
        onDismiss = onDismiss,
        title = stringResource(Res.string.export_dialog_title),
        content = {
            Text(
                text = stringResource(Res.string.export_record_count, recordCount),
                fontSize = 15.sp,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.file_name_label, fileName),
                fontSize = 14.sp,
                color = TextMuted,
                fontFamily = FontFamily.Monospace,
            )
        },
        buttons = {
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
                        text = stringResource(Res.string.cancel),
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
                        .clickable(onClick = onConfirm),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.export_confirm),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Background,
                    )
                }
            }
        },
    )
}

@Composable
private fun ExportResultDialog(
    fileName: String,
    onDone: () -> Unit,
) {
    GainfulDialog(
        onDismiss = onDone,
        title = "",
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GreenDim),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "\u2713",
                        fontSize = 24.sp,
                        color = GainGreen,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.export_success),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.export_success_msg, fileName),
                    fontSize = 14.sp,
                    color = TextSecondary,
                )
            }
        },
        buttons = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Gold)
                    .clickable(onClick = onDone),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.export_done),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Background,
                )
            }
        },
    )
}

@Composable
private fun ColorSettingRow(
    label: String,
    scheme: GainLossColorScheme,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val upColor = if (scheme == GainLossColorScheme.RED_UP) GainRed else GainGreen
                    val downColor = if (scheme == GainLossColorScheme.RED_UP) GainGreen else GainRed
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(upColor),
                    )
                    Text(
                        text = stringResource(Res.string.gain_label),
                        fontSize = 13.sp,
                        color = TextSecondary,
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(downColor),
                    )
                    Text(
                        text = stringResource(Res.string.loss_label),
                        fontSize = 13.sp,
                        color = TextSecondary,
                    )
                }
                Text(
                    text = "\u25BE",
                    fontSize = 12.sp,
                    color = TextMuted,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Border),
        )
    }
}

@Composable
private fun ColorPickerDialog(
    selected: GainLossColorScheme,
    onConfirm: (GainLossColorScheme) -> Unit,
    onDismiss: () -> Unit,
) {
    var workingSelection by remember { mutableStateOf(selected) }

    GainfulDialog(
        onDismiss = onDismiss,
        title = stringResource(Res.string.gain_loss_color_title),
        content = {
            GainLossColorScheme.entries.forEach { scheme ->
                val isSelected = scheme == workingSelection
                val upColor = if (scheme == GainLossColorScheme.RED_UP) GainRed else GainGreen
                val downColor = if (scheme == GainLossColorScheme.RED_UP) GainGreen else GainRed
                val label = if (scheme == GainLossColorScheme.RED_UP) {
                    stringResource(Res.string.color_red_up)
                } else {
                    stringResource(Res.string.color_green_up)
                }

                Column(
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
                        .clickable { workingSelection = scheme }
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                ) {
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Gold else TextPrimary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    ) {
                        Text(
                            text = stringResource(Res.string.color_demo_up),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = upColor,
                        )
                        Text(
                            text = stringResource(Res.string.color_demo_down),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = downColor,
                        )
                    }
                }
            }
        },
        buttons = {
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
                        text = stringResource(Res.string.cancel),
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
                        text = stringResource(Res.string.confirm),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Background,
                    )
                }
            }
        },
    )
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
    var workingSelection by remember { mutableStateOf(selected) }

    GainfulDialog(
        onDismiss = onDismiss,
        title = stringResource(Res.string.refresh_frequency_title),
        content = {
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
                        text = stringResource(Res.string.minutes_format, minutes),
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Gold else TextPrimary,
                    )
                }
            }
        },
        buttons = {
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
                        text = stringResource(Res.string.cancel),
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
                        text = stringResource(Res.string.confirm),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Background,
                    )
                }
            }
        },
    )
}
