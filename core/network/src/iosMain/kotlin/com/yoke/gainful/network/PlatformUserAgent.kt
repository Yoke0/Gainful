package com.yoke.gainful.network

import platform.UIKit.UIDevice

internal actual fun platformUserAgent(): String {
    val device = UIDevice.currentDevice
    val model = device.model
    val systemName = device.systemName
    val systemVersion = device.systemVersion
    return "$model ($systemName $systemVersion)"
}
