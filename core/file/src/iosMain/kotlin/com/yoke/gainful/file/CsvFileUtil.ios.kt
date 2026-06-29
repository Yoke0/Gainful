@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.yoke.gainful.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeCommaSeparatedText
import platform.UniformTypeIdentifiers.UTTypeUTF8PlainText
import platform.darwin.NSObject

@Composable
actual fun rememberCsvFileUtil(): CsvFileUtil {
    val viewController = LocalUIViewController.current
    return remember(viewController) { IosFileUtil(viewController) }
}

private class IosFileUtil(
    private val viewController: UIViewController,
) : CsvFileUtil {
    override fun saveFile(fileName: String, content: String, onResult: (Boolean) -> Unit) {
        val tempDir = NSTemporaryDirectory()
        val filePath = "$tempDir/$fileName"
        val nsString = NSString.create(string = content)
        val success =
            nsString.writeToFile(
                path = filePath,
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = null,
            )
        if (!success) {
            onResult(false)
            return
        }

        val fileUrl = NSURL.fileURLWithPath(filePath)
        val activityVC =
            UIActivityViewController(
                activityItems = listOf(fileUrl),
                applicationActivities = null,
            )
        activityVC.completionWithItemsHandler = { _, completed, _, _ ->
            onResult(completed)
        }
        viewController.presentViewController(activityVC, animated = true, completion = null)
    }

    override fun pickFile(onResult: (String?, String?) -> Unit) {
        val contentTypes = listOf(UTTypeCommaSeparatedText, UTTypeUTF8PlainText)
        val picker = UIDocumentPickerViewController(forOpeningContentTypes = contentTypes, asCopy = true)
        val delegate =
            object : NSObject(), UIDocumentPickerDelegateProtocol {
                override fun documentPicker(
                    controller: UIDocumentPickerViewController,
                    didPickDocumentsAtURLs: List<*>,
                ) {
                    val url =
                        didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: run {
                            onResult(null, null)
                            return
                        }
                    val filePath =
                        url.path ?: run {
                            onResult(null, null)
                            return
                        }
                    val fileManager = NSFileManager.defaultManager
                    val data = fileManager.contentsAtPath(filePath)
                    val content =
                        if (data != null) {
                            NSString.create(data = data, encoding = NSUTF8StringEncoding).toString()
                        } else {
                            null
                        }
                    val fileName = url.lastPathComponent ?: "trades.csv"
                    onResult(content, fileName)
                }

                override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                    onResult(null, null)
                }
            }
        picker.delegate = delegate
        viewController.presentViewController(picker, animated = true, completion = null)
    }
}
