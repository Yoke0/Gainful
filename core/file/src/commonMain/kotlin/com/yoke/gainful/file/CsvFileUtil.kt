package com.yoke.gainful.file

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCsvFileUtil(): CsvFileUtil

interface CsvFileUtil {
    fun saveFile(fileName: String, content: String, onResult: (Boolean) -> Unit)
    fun pickFile(onResult: (String?, String?) -> Unit)
}
