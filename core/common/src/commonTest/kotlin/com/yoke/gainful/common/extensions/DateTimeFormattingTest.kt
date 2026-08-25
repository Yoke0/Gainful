package com.yoke.gainful.common.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateTimeFormattingTest {
    @Test
    fun pad2SingleDigit() {
        assertEquals("01", 1.pad2())
        assertEquals("09", 9.pad2())
    }

    @Test
    fun pad2DoubleDigit() {
        assertEquals("10", 10.pad2())
        assertEquals("59", 59.pad2())
    }

    @Test
    fun pad2Zero() {
        assertEquals("00", 0.pad2())
    }

    @Test
    fun formatLocalizedDateConvertsEpochMillisToYyyyMmDd() {
        val epoch = referenceInstant("2025-01-15T12:30:00")
        assertEquals("2025-01-15", epoch.formatLocalizedDate())
    }

    @Test
    fun formatLocalizedDateTimeConvertsEpochMillis() {
        val epoch = referenceInstant("2025-06-20T08:15:30")
        assertEquals("2025-06-20 08:15:30", epoch.formatLocalizedDateTime())
    }

    @Test
    fun parseLocalizedDateTimeToEpochMillisProducesMillis() {
        val millis = "2025-03-10 14:25:00".parseLocalizedDateTimeToEpochMillis()
        assertTrue(millis > 0)
    }

    @Test
    fun tryParseAcceptsFullDatetime() {
        assertNotNull("2025-01-01 12:00:00".tryParseLocalizedDateTimeToEpochMillis())
    }

    @Test
    fun tryParseAcceptsMinuteOnlyDatetime() {
        assertNotNull("2025-01-01 12:00".tryParseLocalizedDateTimeToEpochMillis())
    }

    @Test
    fun tryParseAcceptsDateOnly() {
        assertNotNull("2025-01-01".tryParseLocalizedDateTimeToEpochMillis())
    }

    @Test
    fun tryParseAcceptsTSeparator() {
        assertNotNull("2025-01-01T12:00:00".tryParseLocalizedDateTimeToEpochMillis())
    }

    @Test
    fun tryParseWithFractionalSeconds() {
        assertNotNull("2025-01-01 12:00:00.123".tryParseLocalizedDateTimeToEpochMillis())
    }

    @Test
    fun tryParseRejectsBlank() {
        assertNull("".tryParseLocalizedDateTimeToEpochMillis())
        assertNull("  ".tryParseLocalizedDateTimeToEpochMillis())
    }

    @Test
    fun tryParseRejectsGarbage() {
        assertNull("not a date".tryParseLocalizedDateTimeToEpochMillis())
        assertNull("abc-def-ghi".tryParseLocalizedDateTimeToEpochMillis())
    }

    @Test
    fun tryParseRejectsInvalidMonth() {
        assertNull("2025-13-01 12:00:00".tryParseLocalizedDateTimeToEpochMillis())
    }

    companion object {
        private fun referenceInstant(localDateTimeString: String): Long {
            val tz = TimeZone.currentSystemDefault()
            return LocalDateTime.parse(localDateTimeString).toInstant(tz).toEpochMilliseconds()
        }
    }
}
