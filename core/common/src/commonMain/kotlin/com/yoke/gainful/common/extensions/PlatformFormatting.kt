package com.yoke.gainful.common.extensions

expect fun Double.formatLocalized(decimals: Int = 2): String

expect fun Double.formatCompact(decimals: Int = 2): String

fun Double.formatSigned(decimals: Int = 2): String {
    val sign = if (this >= 0) "+" else ""
    return "$sign${formatLocalized(decimals)}"
}
