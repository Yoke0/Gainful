package com.yoke.gainful.feature.settings.`import`

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.yoke.gainful.file.rememberCsvFileUtil
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.ui.components.ConfirmDialog
import com.yoke.gainful.ui.theme.TextSecondary
import gainful.feature.settings.generated.resources.Res
import gainful.feature.settings.generated.resources.cancel
import gainful.feature.settings.generated.resources.confirm
import gainful.feature.settings.generated.resources.csv_headers
import gainful.feature.settings.generated.resources.csv_type_values
import gainful.feature.settings.generated.resources.import_confirm_title
import gainful.feature.settings.generated.resources.import_confirm_duplicate_message
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImportEntry(
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<ImportViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val fileUtil = rememberCsvFileUtil()
    var showDuplicateConfirm by remember { mutableStateOf(false) }

    val csvHeaders = stringArrayResource(Res.array.csv_headers)
    val csvTypeValues = stringArrayResource(Res.array.csv_type_values)
    val csvConfig = remember(csvHeaders, csvTypeValues) {
        CsvConfig(headers = csvHeaders, typeValues = csvTypeValues)
    }

    val confirmTitle = stringResource(Res.string.import_confirm_title)
    val confirmMessage = stringResource(
        Res.string.import_confirm_duplicate_message,
        uiState.preview?.duplicateCount ?: 0
    )
    val cancelText = stringResource(Res.string.cancel)
    val confirmText = stringResource(Res.string.confirm)

    ImportScreen(
        viewModel = viewModel,
        fileUtil = fileUtil,
        csvConfig = csvConfig,
        onConfirm = {
            if ((uiState.preview?.duplicateCount ?: 0) > 0) {
                showDuplicateConfirm = true
            } else {
                viewModel.onIntent(ImportIntent.ConfirmImport(csvConfig))
                onBack()
            }
        },
    )

    if (showDuplicateConfirm) {
        ConfirmDialog(
            title = confirmTitle,
            confirmText = confirmText,
            dismissText = cancelText,
            onConfirm = {
                showDuplicateConfirm = false
                viewModel.onIntent(ImportIntent.ConfirmImport(csvConfig))
                onBack()
            },
            onDismiss = { showDuplicateConfirm = false },
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
