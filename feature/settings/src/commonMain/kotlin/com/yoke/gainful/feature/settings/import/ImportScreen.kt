
@file:Suppress("ktlint:standard:package-name")

package com.yoke.gainful.feature.settings.`import`

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.designsystem.components.BackNavigationIcon
import com.yoke.gainful.designsystem.components.ConfirmDialog
import com.yoke.gainful.designsystem.components.GainfulScaffold
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.components.LoadingSpinner
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.GainGreen
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GreenDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.feature.settings.model.CsvPreviewData
import com.yoke.gainful.file.rememberCsvFileUtil
import com.yoke.gainful.ui.TransactionCard
import com.yoke.gainful.ui.TransactionDisplayItem
import gainful.core.designsystem.generated.resources.ic_upload
import gainful.feature.settings.generated.resources.Res
import gainful.feature.settings.generated.resources.cancel
import gainful.feature.settings.generated.resources.confirm
import gainful.feature.settings.generated.resources.csv_headers
import gainful.feature.settings.generated.resources.csv_type_values
import gainful.feature.settings.generated.resources.import_confirm
import gainful.feature.settings.generated.resources.import_confirm_duplicate_message
import gainful.feature.settings.generated.resources.import_confirm_title
import gainful.feature.settings.generated.resources.import_dialog_title
import gainful.feature.settings.generated.resources.import_error_format
import gainful.feature.settings.generated.resources.import_file_types
import gainful.feature.settings.generated.resources.import_loading
import gainful.feature.settings.generated.resources.import_stat_duplicate
import gainful.feature.settings.generated.resources.import_stat_total
import gainful.feature.settings.generated.resources.import_stat_valid
import gainful.feature.settings.generated.resources.import_upload_hint
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import gainful.core.designsystem.generated.resources.Res as DsRes

@Composable
fun ImportScreen(
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<ImportViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val fileUtil = rememberCsvFileUtil()

    val csvHeaders = stringArrayResource(Res.array.csv_headers)
    val csvTypeValues = stringArrayResource(Res.array.csv_type_values)
    val csvConfig =
        remember(csvHeaders, csvTypeValues) {
            CsvConfig(headers = csvHeaders, typeValues = csvTypeValues)
        }

    ImportScreen(
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
        onDeleteItem = { index ->
            viewModel.onIntent(ImportIntent.DeleteItem(index))
        },
        onDismissDuplicateConfirm = {
            viewModel.onIntent(ImportIntent.DismissDuplicateConfirm)
        },
        fileUtil = fileUtil,
    )
}

@Composable
private fun ImportScreen(
    uiState: ImportUiState,
    onBack: () -> Unit,
    onPickFile: (content: String, fileName: String) -> Unit,
    onConfirmImport: () -> Unit,
    onConfirmDuplicate: () -> Unit,
    onDeleteItem: (Int) -> Unit,
    onDismissDuplicateConfirm: () -> Unit,
    fileUtil: com.yoke.gainful.file.CsvFileUtil,
) {
    GainfulScaffold(
        appTopBar = {
            GainfulTopAppBar(
                title = stringResource(Res.string.import_dialog_title),
                navigationIcon = { BackNavigationIcon(onClick = onBack) },
                actions = {
                    if (uiState.preview != null) {
                        Box(
                            modifier =
                                Modifier
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
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            UploadArea(
                hasPreview = uiState.preview != null,
                fileName = uiState.preview?.fileName,
                onPickFile = {
                    fileUtil.pickFile { content, fileName ->
                        if (content != null && fileName != null) {
                            onPickFile(content, fileName)
                        }
                    }
                },
            )

            if (uiState.hasParseError) {
                Text(
                    text = stringResource(Res.string.import_error_format),
                    fontSize = 12.sp,
                    color = GainRed,
                    lineHeight = 16.sp,
                )
            }

            uiState.preview?.let { preview ->
                ImportPreviewContent(
                    preview = preview,
                    displayItems = uiState.displayItems,
                    isLoading = uiState.isLoading,
                    onDeleteItem = onDeleteItem,
                )
            } ?: run {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    if (uiState.showDuplicateConfirm) {
        DuplicateConfirmDialog(
            duplicateCount = uiState.preview?.duplicateCount ?: 0,
            onConfirm = onConfirmDuplicate,
            onDismiss = onDismissDuplicateConfirm,
        )
    }
}

@Composable
private fun ImportPreviewContent(
    preview: CsvPreviewData,
    displayItems: List<TransactionDisplayItem>,
    isLoading: Boolean,
    onDeleteItem: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            LazyColumn(
                modifier =
                    Modifier
                        .weight(1f)
                        .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(
                    items = displayItems,
                    key = { index, _ -> index },
                ) { index, item ->
                    val isDuplicate = index in preview.duplicateIndices
                    TransactionCard(
                        item = item,
                        isDuplicate = isDuplicate,
                        onDelete = { onDeleteItem(index) },
                        modifier = Modifier.padding(horizontal = 0.dp),
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LoadingSpinner()
                        Text(
                            text = stringResource(Res.string.import_loading),
                            fontSize = 12.sp,
                            color = TextMuted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DuplicateConfirmDialog(
    duplicateCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val confirmMessage =
        stringResource(
            Res.string.import_confirm_duplicate_message,
            duplicateCount,
        )

    ConfirmDialog(
        title = stringResource(Res.string.import_confirm_title),
        confirmText = stringResource(Res.string.confirm),
        dismissText = stringResource(Res.string.cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
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

@Composable
private fun UploadArea(
    hasPreview: Boolean,
    fileName: String?,
    onPickFile: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (hasPreview) GreenDim else Surface)
                .border(
                    width = 2.dp,
                    color = if (hasPreview) GainGreen else Border,
                    shape = RoundedCornerShape(10.dp),
                )
                .clickable(onClick = onPickFile)
                .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(DsRes.drawable.ic_upload),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = if (hasPreview) GainGreen else TextMuted,
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (hasPreview) {
                Text(
                    text = fileName ?: "",
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
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
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
