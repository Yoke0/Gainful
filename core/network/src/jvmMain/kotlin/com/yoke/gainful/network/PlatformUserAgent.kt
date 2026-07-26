package com.yoke.gainful.network

internal actual fun platformUserAgent(): String {
    val os = System.getProperty("os.name") ?: "Unknown"
    val arch = System.getProperty("os.arch") ?: ""
    return "Desktop ($os $arch)"
}
