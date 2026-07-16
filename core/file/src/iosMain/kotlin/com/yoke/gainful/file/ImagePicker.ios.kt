package com.yoke.gainful.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlin.time.Clock

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray?, String?) -> Unit): ImagePickerLauncher {
    return remember {
        object : ImagePickerLauncher {
            override fun launch() {
                val config =
                    PHPickerConfiguration().apply {
                        filter = PHPickerFilter.imagesFilter
                        selectionLimit = 1
                    }
                val picker = PHPickerViewController(configuration = config)
                val delegate =
                    object : NSObject(), PHPickerViewControllerDelegateProtocol {
                        override fun picker(
                            picker: PHPickerViewController,
                            didFinishPicking: List<*>,
                        ) {
                            picker.dismissViewControllerAnimated(true, completion = null)
                            val result = didFinishPicking.firstOrNull() as? PHPickerResult
                            val provider = result?.itemProvider
                            if (provider != null && provider.hasItemConformingToTypeIdentifier("public.image")) {
                                provider.loadDataRepresentationForTypeIdentifier(
                                    "public.image",
                                ) { data: NSData?, _: Any? ->
                                    if (data != null) {
                                        val length = data.length.toInt()
                                        val bytes = ByteArray(length)
                                        bytes.usePinned { pinned ->
                                            memcpy(
                                                pinned.addressOf(0),
                                                data.bytes,
                                                data.length,
                                            )
                                        }
                                        val fileName = "avatar_${Clock.System.now().epochSeconds}.jpg"
                                        onResult(bytes, fileName)
                                    } else {
                                        onResult(null, null)
                                    }
                                }
                            } else {
                                onResult(null, null)
                            }
                        }
                    }
                picker.delegate = delegate
                val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootVC?.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}
