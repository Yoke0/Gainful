package com.yoke.gainful.file

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberCsvFileUtil(): CsvFileUtil {
    val context = LocalContext.current

    var saveContent by remember { mutableStateOf<String?>(null) }
    var saveCallback by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }
    var pickCallback by remember { mutableStateOf<((String?, String?) -> Unit)?>(null) }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        val success = if (uri != null) {
            runCatching {
                val content = saveContent ?: return@runCatching false
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(content.toByteArray(Charsets.UTF_8))
                }
                true
            }.getOrDefault(false)
        } else false
        saveCallback?.invoke(success)
        saveContent = null
        saveCallback = null
    }

    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        val result = if (uri != null) {
            runCatching {
                val content = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader().readText()
                }
                val fileName = uri.lastPathSegment ?: "trades.csv"
                content to fileName
            }.getOrNull()
        } else null
        pickCallback?.invoke(result?.first, result?.second)
        pickCallback = null
    }

    return remember {
        object : CsvFileUtil {
            override fun saveFile(fileName: String, content: String, onResult: (Boolean) -> Unit) {
                saveContent = content
                saveCallback = onResult
                saveLauncher.launch(fileName)
            }

            override fun pickFile(onResult: (String?, String?) -> Unit) {
                pickCallback = onResult
                pickLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "text/*"))
            }
        }
    }
}
