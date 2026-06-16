package com.yoke.gainful.common.extensions

import kotlin.math.round

fun String.Companion.empty(): String = ""

fun String?.orDefault(default: String = ""): String = this ?: default

fun Double.formatTwoDecimals(): String {
    val rounded = round(this * 100) / 100
    val str = rounded.toString()
    val dotIndex = str.indexOf('.')
    return when {
        dotIndex < 0 -> "$str.00"
        str.length - dotIndex == 2 -> "${str}0"
        else -> str
    }
}
