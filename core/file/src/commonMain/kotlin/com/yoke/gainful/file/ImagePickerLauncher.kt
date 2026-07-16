package com.yoke.gainful.file

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(onResult: (ByteArray?, String?) -> Unit): ImagePickerLauncher

interface ImagePickerLauncher {
    fun launch()
}
