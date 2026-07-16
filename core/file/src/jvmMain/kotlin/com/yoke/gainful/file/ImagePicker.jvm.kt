package com.yoke.gainful.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import gainful.core.file.generated.resources.Res
import gainful.core.file.generated.resources.image_picker_filter
import gainful.core.file.generated.resources.image_picker_title
import org.jetbrains.compose.resources.stringResource
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray?, String?) -> Unit): ImagePickerLauncher {
    val title = stringResource(Res.string.image_picker_title)
    val filter = stringResource(Res.string.image_picker_filter)
    return remember(title, filter) {
        object : ImagePickerLauncher {
            override fun launch() {
                val dialog = FileDialog(null as Frame?, title, FileDialog.LOAD)
                dialog.isMultipleMode = false
                dialog.file = "*.jpg;*.jpeg;*.png;*.webp"
                dialog.isVisible = true
                val fileName = dialog.file
                if (fileName != null) {
                    val file = File(dialog.directory, fileName)
                    val bytes = file.readBytes()
                    onResult(bytes, fileName)
                } else {
                    onResult(null, null)
                }
            }
        }
    }
}
