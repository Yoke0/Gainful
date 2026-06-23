package com.yoke.gainful

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.util.Locale

fun main() = application {
    val appName = if (Locale.getDefault().language == Locale.CHINESE.language) "盈迹" else "Gainful"
    Window(
        onCloseRequest = ::exitApplication,
        title = appName,
    ) {
        App()
    }
}