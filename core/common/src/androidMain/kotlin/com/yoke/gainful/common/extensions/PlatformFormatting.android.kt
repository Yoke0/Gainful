package com.yoke.gainful.common.extensions

import android.icu.text.CompactDecimalFormat
import android.icu.text.CompactDecimalFormat.CompactStyle
import android.icu.text.DecimalFormat
import android.icu.util.ULocale

actual fun Double.formatLocalized(decimals: Int): String {
    val nf = DecimalFormat.getInstance(ULocale.getDefault())
    nf.maximumFractionDigits = decimals
    nf.minimumFractionDigits = decimals
    return nf.format(this)
}

actual fun Double.formatCompact(decimals: Int): String {
    val nf = CompactDecimalFormat.getInstance(ULocale.getDefault(), CompactStyle.SHORT)
    nf.maximumFractionDigits = decimals
    nf.minimumFractionDigits = decimals
    return nf.format(this)
}
