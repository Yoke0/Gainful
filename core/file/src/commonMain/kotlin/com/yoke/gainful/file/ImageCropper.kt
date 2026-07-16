package com.yoke.gainful.file

/**
 * Crop image bytes to a centered square of [targetSize] x [targetSize] pixels.
 */
expect fun cropImageToSquare(bytes: ByteArray, targetSize: Int): ByteArray
