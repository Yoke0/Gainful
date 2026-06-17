package com.yoke.gainful.common.extensions

import kotlin.math.abs
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

fun Double.formatDecimal(decimals: Int = 2): String {
    val factor = listOf(1.0, 10.0, 100.0, 1000.0, 10000.0).getOrElse(decimals) { 10.0.pow(decimals) }
    val rounded = round(this * factor) / factor
    val str = rounded.toString()
    val dotIndex = str.indexOf('.')
    return if (dotIndex < 0) {
        str + "." + "0".repeat(decimals)
    } else {
        val currentDecimals = str.length - dotIndex - 1
        when {
            currentDecimals > decimals -> str.substring(0, dotIndex + decimals + 1)
            currentDecimals < decimals -> str + "0".repeat(decimals - currentDecimals)
            else -> str
        }
    }
}

fun Double.formatCompact(): String {
    val absVal = abs(this)
    val sign = if (this < 0) "-" else ""
    return when {
        absVal >= 1_000_000 -> "${sign}${(absVal / 1_000_000).formatDecimal(1)}M"
        absVal >= 1_000 -> "${sign}${(absVal / 1_000).formatDecimal(1)}K"
        else -> "${sign}${absVal.formatDecimal(0)}"
    }
}

fun Double.formatSigned(decimals: Int = 2): String {
    val sign = if (this >= 0) "+" else ""
    return "$sign${formatDecimal(decimals)}"
}

private fun Double.pow(n: Int): Double {
    var result = 1.0
    repeat(n) { result *= this }
    return result
}
