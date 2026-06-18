package com.yoke.gainful.common.extensions

import kotlin.math.abs
import kotlin.math.round

fun String.Companion.empty(): String = ""

fun String?.orDefault(default: String = ""): String = this ?: default

fun Double.formatTwoDecimals(): String = formatDecimal(2)

fun Double.formatDecimal(decimals: Int = 2): String {
    val factor = listOf(1.0, 10.0, 100.0, 1000.0, 10000.0).getOrElse(decimals) { 10.0.pow(decimals) }
    val rounded = round(this * factor) / factor
    val isNegative = rounded < 0
    val absVal = if (isNegative) -rounded else rounded
    val intPart = absVal.toLong()
    val fracPart = ((absVal - intPart) * factor).toLong()
    val intStr = intPart.toString()
    val fracStr = fracPart.toString().padStart(decimals, '0').take(decimals)
    return if (isNegative) "-$intStr.$fracStr" else "$intStr.$fracStr"
}

fun Double.formatCompact(): String {
    val absVal = abs(this)
    val sign = if (this < 0) "-" else ""
    return when {
        absVal >= 1_000_000 -> "${sign}${(absVal / 1_000_000).formatDecimal(1)}M"
        absVal >= 1_000 -> "${sign}${(absVal / 1_000).formatDecimal(1)}K"
        else -> "${sign}${absVal.formatDecimal(2)}"
    }
}

fun Double.formatSigned(decimals: Int = 2): String {
    val sign = if (this >= 0) "+" else ""
    return "$sign${formatDecimal(decimals)}"
}

fun Double.formatTurnover(): String {
    val absVal = abs(this)
    return when {
        absVal >= 1_000_000_000 -> "%.1f亿".format(absVal / 1_000_000_000.0)
        absVal >= 1_000_000 -> "%.1fM".format(absVal / 1_000_000.0)
        absVal >= 1_000 -> "%.1fK".format(absVal / 1_000.0)
        else -> "%.0f".format(absVal)
    }
}

private fun Double.pow(n: Int): Double {
    var result = 1.0
    repeat(n) { result *= this }
    return result
}
