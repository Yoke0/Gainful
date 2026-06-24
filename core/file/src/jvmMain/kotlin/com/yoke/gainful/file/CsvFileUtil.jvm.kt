package com.yoke.gainful.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
actual fun rememberCsvFileUtil(): CsvFileUtil {
    return remember { DesktopFileUtil() }
}

private class DesktopFileUtil : CsvFileUtil {

    override fun saveFile(fileName: String, content: String, onResult: (Boolean) -> Unit) {
        val success = runCatching {
            val chooser = JFileChooser().apply {
                dialogTitle = "保存交易记录"
                selectedFile = File(fileName)
                fileFilter = FileNameExtensionFilter("CSV 文件", "csv")
            }
            val result = chooser.showSaveDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                var file = chooser.selectedFile
                if (!file.name.endsWith(".csv")) {
                    file = File(file.absolutePath + ".csv")
                }
                file.writeText(content)
                true
            } else false
        }.getOrDefault(false)
        onResult(success)
    }

    override fun pickFile(onResult: (String?, String?) -> Unit) {
        val result = runCatching {
            val chooser = JFileChooser().apply {
                dialogTitle = "选择交易记录文件"
                fileFilter = FileNameExtensionFilter("CSV 文件", "csv")
            }
            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                file.readText() to file.name
            } else null
        }.getOrNull()
        onResult(result?.first, result?.second)
    }
}
