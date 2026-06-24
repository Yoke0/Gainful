package com.yoke.gainful.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberCsvFileUtil(): CsvFileUtil {
    return remember { IosFileUtil() }
}

private class IosFileUtil : CsvFileUtil {

    override fun saveFile(fileName: String, content: String, onResult: (Boolean) -> Unit) {
        onResult(false)
    }

    override fun pickFile(onResult: (String?, String?) -> Unit) {
        onResult(null, null)
    }
}
