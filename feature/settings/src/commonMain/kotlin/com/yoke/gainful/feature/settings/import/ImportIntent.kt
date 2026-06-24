package com.yoke.gainful.feature.settings.`import`

import com.yoke.gainful.feature.settings.model.CsvConfig

sealed interface ImportIntent {
    data class ParseCsv(
        val csvContent: String,
        val fileName: String,
        val csvConfig: CsvConfig,
    ) : ImportIntent

    data class DeleteItem(val index: Int) : ImportIntent
    data class ConfirmImport(val csvConfig: CsvConfig) : ImportIntent
    data object Reset : ImportIntent
}
