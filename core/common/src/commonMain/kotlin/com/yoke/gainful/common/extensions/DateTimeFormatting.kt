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

/**
 * Lenient local date-time parsing for CSV import.
 *
 * Accepts (space or `T` separated):
 * - `yyyy-MM-dd HH:mm` (minute precision, no seconds)
 * - `yyyy-MM-dd HH:mm:ss` (optionally with fractional seconds)
 * - `yyyy-MM-dd` (date only, treated as midnight)
 *
 * Returns `null` instead of throwing on malformed input.
 */
fun String.tryParseLocalizedDateTimeToEpochMillis(): Long? {
    val tz = TimeZone.currentSystemDefault()
    val normalized = this.trim().replace(" ", "T")
    if (normalized.isBlank()) return null

    val candidates =
        buildList {
            add(normalized)
            if (':' in normalized && normalized.substringAfterLast('T').count { it == ':' } == 1) {
                // java.time requires seconds — pad "HH:mm" to "HH:mm:00"
                add("$normalized:00")
            } else if (':' !in normalized) {
                // date only — treat as midnight
                add("${normalized}T00:00:00")
            }
        }

    for (candidate in candidates) {
        val millis = runCatching { LocalDateTime.parse(candidate).toInstant(tz).toEpochMilliseconds() }.getOrNull()
        if (millis != null) return millis
    }
    return null
}
