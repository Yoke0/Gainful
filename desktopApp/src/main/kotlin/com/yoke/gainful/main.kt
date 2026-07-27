@file:Suppress("ktlint:standard:filename")

package com.yoke.gainful

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() =
    application {
        var title by remember { mutableStateOf("") }
        val state = rememberWindowState(size = DpSize(1024.dp, 768.dp))
        Window(
            onCloseRequest = ::exitApplication,
            title = title,
            state = state,
        ) {
            SideEffect {
                window.minimumSize = java.awt.Dimension(420, 860)

                val awtColor = java.awt.Color(Color.Transparent.toArgb())
                window.background = awtColor // for macOS
                window.contentPane.background = awtColor // for Windows
            }

            App(onTitleReady = { title = it })
        }
    }
