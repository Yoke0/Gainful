package com.yoke.gainful.common.extensions

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun Double.formatLocalized(decimals: Int): String {
    val formatter = NSNumberFormatter()
    formatter.numberStyle = NSNumberFormatterDecimalStyle
    formatter.minimumFractionDigits = decimals.toULong()
    formatter.maximumFractionDigits = decimals.toULong()
    return formatter.stringFromNumber(NSNumber(this)) ?: this.toString()
}

actual fun Double.formatCompact(decimals: Int): String {
    val absVal = kotlin.math.abs(this)
    val sign = if (this < 0) "-" else ""
    val isChinese = NSLocale.currentLocale.languageCode.startsWith("zh")

    return if (isChinese) {
        when {
            absVal >= 100_000_000 -> "$sign${(absVal / 100_000_000).formatLocalized(decimals)}亿"
            absVal >= 10_000 -> "$sign${(absVal / 10_000).formatLocalized(decimals)}万"
            absVal >= 1_000 -> "$sign${(absVal / 1_000).formatLocalized(decimals)}千"
            else -> formatLocalized(decimals)
        }
    } else {
        when {
            absVal >= 1_000_000_000_000 -> "$sign${(absVal / 1_000_000_000_000).formatLocalized(decimals)}T"
            absVal >= 1_000_000_000 -> "$sign${(absVal / 1_000_000_000).formatLocalized(decimals)}B"
            absVal >= 1_000_000 -> "$sign${(absVal / 1_000_000).formatLocalized(decimals)}M"
            absVal >= 1_000 -> "$sign${(absVal / 1_000).formatLocalized(decimals)}K"
            else -> formatLocalized(decimals)
        }
    }
}
