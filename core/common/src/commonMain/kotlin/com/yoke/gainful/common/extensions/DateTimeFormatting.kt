package com.yoke.gainful.common.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Int.pad2(): String = this.toString().padStart(2, '0')

fun Long.formatLocalizedDate(): String {
    val tz = TimeZone.currentSystemDefault()
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(tz).date.toString()
}

fun Long.formatLocalizedDateTime(): String {
    val tz = TimeZone.currentSystemDefault()
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(tz).toString().replace("T", " ")
}

fun String.parseLocalizedDateTimeToEpochMillis(): Long {
    val tz = TimeZone.currentSystemDefault()
    val dt = LocalDateTime.parse(this.replace(" ", "T"))
    return dt.toInstant(tz).toEpochMilliseconds()
}
