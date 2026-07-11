package com.yoke.gainful.common.extensions

import com.ibm.icu.text.CompactDecimalFormat
import com.ibm.icu.text.CompactDecimalFormat.CompactStyle
import java.text.DecimalFormat
import java.util.Locale

actual fun Double.formatLocalized(decimals: Int): String {
    val nf = DecimalFormat.getInstance(Locale.getDefault())
    nf.maximumFractionDigits = decimals
    nf.minimumFractionDigits = decimals
    return nf.format(this)
}

actual fun Double.formatCompact(decimals: Int): String {
    val nf = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactStyle.SHORT)
    nf.maximumFractionDigits = decimals
    nf.minimumFractionDigits = decimals
    return nf.format(this)
}
