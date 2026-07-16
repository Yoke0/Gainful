package com.yoke.gainful.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import gainful.core.file.generated.resources.Res
import gainful.core.file.generated.resources.csv_pick_title
import gainful.core.file.generated.resources.csv_save_title
import org.jetbrains.compose.resources.stringResource
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun rememberCsvFileUtil(): CsvFileUtil {
    val saveTitle = stringResource(Res.string.csv_save_title)
    val pickTitle = stringResource(Res.string.csv_pick_title)
    return remember(saveTitle, pickTitle) {
        DesktopFileUtil(saveTitle, pickTitle)
    }
}

private class DesktopFileUtil(
    private val saveTitle: String,
    private val pickTitle: String,
) : CsvFileUtil {
    override fun saveFile(fileName: String, content: String, onResult: (Boolean) -> Unit) {
        val dialog = FileDialog(null as Frame?, saveTitle, FileDialog.SAVE)
        dialog.file = fileName
        dialog.isVisible = true
        val selectedFile = dialog.file
        if (selectedFile != null) {
            val success =
                runCatching {
                    var file = File(dialog.directory, selectedFile)
                    if (!file.name.endsWith(".csv")) {
                        file = File(file.absolutePath + ".csv")
                    }
                    file.writeText(content)
                }.isSuccess
            onResult(success)
        } else {
            onResult(false)
        }
    }

    override fun pickFile(onResult: (String?, String?) -> Unit) {
        val dialog = FileDialog(null as Frame?, pickTitle, FileDialog.LOAD)
        dialog.file = "*.csv"
        dialog.isVisible = true
        val selectedFile = dialog.file
        if (selectedFile != null) {
            val file = File(dialog.directory, selectedFile)
            runCatching {
                onResult(file.readText(), file.name)
            }.getOrNull() ?: onResult(null, null)
        } else {
            onResult(null, null)
        }
    }
}
