package com.yoke.gainful.file

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

actual fun cropImageToSquare(bytes: ByteArray, targetSize: Int): ByteArray {
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return bytes
    val size = minOf(bitmap.width, bitmap.height)
    val x = (bitmap.width - size) / 2
    val y = (bitmap.height - size) / 2
    val cropped = Bitmap.createBitmap(bitmap, x, y, size, size)
    val scaled = Bitmap.createScaledBitmap(cropped, targetSize, targetSize, true)
    if (scaled !== cropped) cropped.recycle()
    val output = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 90, output)
    scaled.recycle()
    bitmap.recycle()
    return output.toByteArray()
}
