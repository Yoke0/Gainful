package com.yoke.gainful.file

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Decode image bytes into an [ImageBitmap].
 */
expect fun decodeImageBitmap(bytes: ByteArray): ImageBitmap
