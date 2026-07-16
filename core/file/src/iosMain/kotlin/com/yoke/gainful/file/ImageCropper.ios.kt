package com.yoke.gainful.file

import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface

@OptIn(ExperimentalForeignApi::class)
actual fun cropImageToSquare(bytes: ByteArray, targetSize: Int): ByteArray {
    val image = Image.makeFromEncoded(bytes)
    val size = minOf(image.width, image.height)
    val x = (image.width - size) / 2
    val y = (image.height - size) / 2

    val surface = Surface.makeRasterN32Premul(targetSize, targetSize)
    val canvas = surface.canvas
    canvas.drawImageRect(
        image,
        org.jetbrains.skia.Rect.makeXYWH(x.toFloat(), y.toFloat(), size.toFloat(), size.toFloat()),
        org.jetbrains.skia.Rect.makeWH(targetSize.toFloat(), targetSize.toFloat()),
    )
    val result = surface.makeImageSnapshot()
    val encoded = result.encodeToData(org.jetbrains.skia.EncodedImageFormat.JPEG, 90)
    image.close()
    result.close()
    surface.close()
    return encoded?.bytes ?: bytes
}
