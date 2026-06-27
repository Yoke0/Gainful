package com.yoke.gainful.feature.settings.`import`

import com.yoke.gainful.ui.components.GainfulTopAppBar
import com.yoke.gainful.ui.components.BackNavigationIcon
import gainful.feature.settings.generated.resources.Res
import gainful.feature.settings.generated.resources.import_confirm
import gainful.feature.settings.generated.resources.import_confirm_delete_suffix
import gainful.feature.settings.generated.resources.import_confirm_delete_text
import gainful.feature.settings.generated.resources.import_confirm_delete_title
import gainful.feature.settings.generated.resources.import_delete
import gainful.feature.settings.generated.resources.import_dialog_title
import gainful.feature.settings.generated.resources.import_file_types
import gainful.feature.settings.generated.resources.import_loading
import gainful.feature.settings.generated.resources.import_stat_duplicate
import gainful.feature.settings.generated.resources.import_stat_total
import gainful.feature.settings.generated.resources.import_stat_valid
import gainful.feature.settings.generated.resources.import_upload_hint
import gainful.feature.settings.generated.resources.import_error_format
import gainful.feature.settings.generated.resources.cancel
import gainful.feature.settings.generated.resources.confirm
import gainful.feature.settings.generated.resources.csv_headers
import gainful.feature.settings.generated.resources.csv_type_values
import gainful.feature.settings.generated.resources.import_confirm_title
import gainful.feature.settings.generated.resources.import_confirm_duplicate_message
import gainful.feature.settings.generated.resources.import_summary_buy
import gainful.feature.settings.generated.resources.import_summary_sell
import gainful.feature.settings.generated.resources.import_summary_dividend
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.file.rememberCsvFileUtil
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.ui.components.ConfirmDialog
import com.yoke.gainful.ui.components.TransactionCard
import com.yoke.gainful.ui.components.TransactionDisplayItem
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
import com.yoke.gainful.ui.theme.gainColor
import com.yoke.gainful.ui.theme.gainDimColor
import com.yoke.gainful.ui.theme.lossColor
import com.yoke.gainful.ui.theme.lossDimColor
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImportScreen(
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<ImportViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val fileUtil = rememberCsvFileUtil()

    val csvHeaders = stringArrayResource(Res.array.csv_headers)
    val csvTypeValues = stringArrayResource(Res.array.csv_type_values)
    val csvConfig = remember(csvHeaders, csvTypeValues) {
        CsvConfig(headers = csvHeaders, typeValues = csvTypeValues)
    }

    ImportContent(
        uiState = uiState,
        onBack = onBack,
        onPickFile = { content, fileName ->
            viewModel.onIntent(ImportIntent.Reset)
            viewModel.onIntent(ImportIntent.ParseCsv(content, fileName, csvConfig))
        },
        onConfirmImport = {
            if ((uiState.preview?.duplicateCount ?: 0) > 0) {
                viewModel.onIntent(ImportIntent.ShowDuplicateConfirm)
            } else {
                viewModel.onIntent(ImportIntent.ConfirmImport(csvConfig))
                onBack()
            }
        },
        onConfirmDuplicate = {
            viewModel.onIntent(ImportIntent.ConfirmImport(csvConfig))
            onBack()
        },
        onShowDeleteDialog = { index, item ->
            viewModel.onIntent(ImportIntent.ShowDeleteDialog(index, item))
        },
        onDismissDeleteDialog = {
            viewModel.onIntent(ImportIntent.DismissDeleteDialog)
        },
        onDeleteItem = { index ->
            viewModel.onIntent(ImportIntent.DeleteItem(index))
            viewModel.onIntent(ImportIntent.DismissDeleteDialog)
        },
        onDismissDuplicateConfirm = {
            viewModel.onIntent(ImportIntent.DismissDuplicateConfirm)
        },
        fileUtil = fileUtil,
    )
}

@Composable
private fun ImportContent(
    uiState: ImportUiState,
    onBack: () -> Unit,
    onPickFile: (content: String, fileName: String) -> Unit,
    onConfirmImport: () -> Unit,
    onConfirmDuplicate: () -> Unit,
    onShowDeleteDialog: (index: Int, item: TransactionDisplayItem) -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onDeleteItem: (index: Int) -> Unit,
    onDismissDuplicateConfirm: () -> Unit,
    fileUtil: com.yoke.gainful.file.CsvFileUtil,
) {
    val errorFormatMessage = stringResource(Res.string.import_error_format)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp),
    ) {
        GainfulTopAppBar(
            title = stringResource(Res.string.import_dialog_title),
            navigationIcon = { BackNavigationIcon(onClick = onBack) },
            actions = {
                if (uiState.preview != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Gold)
                            .clickable { onConfirmImport() }
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.import_confirm),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Background,
                        )
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (uiState.preview != null) GreenDim else Surface)
                .border(
                    width = 2.dp,
                    color = if (uiState.preview != null) GainGreen else Border,
                    shape = RoundedCornerShape(10.dp),
                )
                .clickable {
                    fileUtil.pickFile { content, fileName ->
                        if (content != null && fileName != null) {
                            onPickFile(content, fileName)
                        }
                    }
                }
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\u2191",
                    fontSize = 36.sp,
                    color = if (uiState.preview != null) GainGreen else TextMuted,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (uiState.preview != null) {
                    Text(
                        text = uiState.preview.fileName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GainGreen,
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.import_upload_hint),
                        fontSize = 14.sp,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.import_file_types),
                        fontSize = 12.sp,
                        color = TextMuted,
                    )
                }
            }
        }

        if (uiState.hasParseError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorFormatMessage,
                fontSize = 12.sp,
                color = GainRed,
                lineHeight = 16.sp,
            )
        }

        uiState.preview?.let { preview ->
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatBox(
                    value = "${preview.totalCount}",
                    label = stringResource(Res.string.import_stat_total),
                    modifier = Modifier.weight(1f),
                )
                StatBox(
                    value = "${preview.validCount}",
                    label = stringResource(Res.string.import_stat_valid),
                    modifier = Modifier.weight(1f),
                )
                StatBox(
                    value = "${preview.duplicateCount}",
                    label = stringResource(Res.string.import_stat_duplicate),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val displayItems = uiState.displayItems

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (uiState.isLoading) 0.5f else 1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(
                        items = displayItems,
                        key = { index, _ -> index }
                    ) { index, item ->
                        val isDuplicate = index in preview.duplicateIndices
                        TransactionCard(
                            item = item,
                            isDuplicate = isDuplicate,
                            onLongPress = {
                                onShowDeleteDialog(index, item)
                            },
                            modifier = Modifier.padding(horizontal = 0.dp),
                        )
                    }
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CircularProgressIndicator(
                                color = Gold,
                                strokeWidth = 2.dp,
                            )
                            Text(
                                text = stringResource(Res.string.import_loading),
                                fontSize = 12.sp,
                                color = TextMuted,
                            )
                        }
                    }
                }
            }
        } ?: run {
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (uiState.deleteDialog != null && uiState.deleteDialog.item != null) {
        val target = uiState.deleteDialog.item
        val typeLabel = when (target.type) {
            0 -> stringResource(Res.string.import_summary_buy)
            1 -> stringResource(Res.string.import_summary_sell)
            else -> stringResource(Res.string.import_summary_dividend)
        }
        val typeColor = when (target.type) {
            0 -> gainColor
            1 -> lossColor
            else -> Gold
        }
        val typeBgColor = when (target.type) {
            0 -> gainDimColor
            1 -> lossDimColor
            else -> GoldDim
        }

        ConfirmDialog(
            title = stringResource(Res.string.import_confirm_delete_title),
            confirmText = stringResource(Res.string.import_delete),
            dismissText = stringResource(Res.string.cancel),
            onConfirm = {
                onDeleteItem(uiState.deleteDialog.index)
            },
            onDismiss = {
                onDismissDeleteDialog()
            },
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.import_confirm_delete_text),
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(typeBgColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = typeLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = typeColor,
                            )
                        }
                        Text(
                            text = target.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary,
                        )
                    }
                    Text(
                        text = stringResource(Res.string.import_confirm_delete_suffix),
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            },
        )
    }

    if (uiState.showDuplicateConfirm) {
        val confirmTitle = stringResource(Res.string.import_confirm_title)
        val confirmMessage = stringResource(
            Res.string.import_confirm_duplicate_message,
            uiState.preview?.duplicateCount ?: 0
        )
        val cancelText = stringResource(Res.string.cancel)
        val confirmText = stringResource(Res.string.confirm)

        ConfirmDialog(
            title = confirmTitle,
            confirmText = confirmText,
            dismissText = cancelText,
            onConfirm = {
                onConfirmDuplicate()
            },
            onDismiss = {
                onDismissDuplicateConfirm()
            },
            confirmColor = Gold,
            content = {
                Text(
                    text = confirmMessage,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        )
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(6.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted,
        )
    }
}
