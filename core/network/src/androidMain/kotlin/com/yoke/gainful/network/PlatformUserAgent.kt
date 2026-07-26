package com.yoke.gainful.network

import android.os.Build

internal actual fun platformUserAgent(): String {
    val device = Build.MANUFACTURER + " " + Build.MODEL
    val osVersion = "Android ${Build.VERSION.RELEASE}"
    return "$device ($osVersion)"
}
