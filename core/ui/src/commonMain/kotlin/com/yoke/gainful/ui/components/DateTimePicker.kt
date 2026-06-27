package com.yoke.gainful.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import gainful.core.ui.generated.resources.Res
import gainful.core.ui.generated.resources.cancel
import gainful.core.ui.generated.resources.confirm
import gainful.core.ui.generated.resources.date_format
import gainful.core.ui.generated.resources.hour_label
import gainful.core.ui.generated.resources.minute_label
import gainful.core.ui.generated.resources.month_1
import gainful.core.ui.generated.resources.month_10
import gainful.core.ui.generated.resources.month_11
import gainful.core.ui.generated.resources.month_12
import gainful.core.ui.generated.resources.month_2
import gainful.core.ui.generated.resources.month_3
import gainful.core.ui.generated.resources.month_4
import gainful.core.ui.generated.resources.month_5
import gainful.core.ui.generated.resources.month_6
import gainful.core.ui.generated.resources.month_7
import gainful.core.ui.generated.resources.month_8
import gainful.core.ui.generated.resources.month_9
import gainful.core.ui.generated.resources.now
import gainful.core.ui.generated.resources.select_datetime
import gainful.core.ui.generated.resources.weekday_friday
import gainful.core.ui.generated.resources.weekday_monday
import gainful.core.ui.generated.resources.weekday_saturday
import gainful.core.ui.generated.resources.weekday_sunday
import gainful.core.ui.generated.resources.weekday_thursday
import gainful.core.ui.generated.resources.weekday_tuesday
import gainful.core.ui.generated.resources.weekday_wednesday
import gainful.core.ui.generated.resources.year_month_format
import org.jetbrains.compose.resources.stringResource
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun DateTimePickerField(
    label: String,
    dateTimeMillis: Long?,
    onClick: () -> Unit,
) {
    val dateTime = dateTimeMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    }
    val hasValue = dateTime != null

    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(10.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "\uD83D\uDCC5",
                fontSize = 16.sp,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (hasValue) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextPrimary, fontFamily = FontFamily.Monospace)) {
                            append(stringResource(Res.string.date_format, dateTime.date.year, dateTime.date.month.number, dateTime.date.day))
                        }
                        withStyle(SpanStyle(color = TextMuted)) {
                            append(" · ")
                        }
                        withStyle(SpanStyle(color = Gold, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)) {
                            append("${dateTime.hour.pad2()}:${dateTime.minute.pad2()}")
                        }
                    },
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                )
            } else {
                Text(
                    text = stringResource(Res.string.select_datetime),
                    fontSize = 15.sp,
                    color = TextMuted,
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                text = "\u25BE",
                fontSize = 14.sp,
                color = TextMuted,
            )
        }
    }
}

@Composable
fun DateTimePickerDialog(
    initialSelectedDateTimeMillis: Long? = null,
    onDateTimeSelected: (dateTimeMillis: Long) -> Unit,
    onDismiss: () -> Unit,
    selectableToTodayOnly: Boolean = false,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val initialDateTime = initialSelectedDateTimeMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    } ?: now
    val initDate = initialDateTime.date
    var calendarYear by remember { mutableIntStateOf(initDate.year) }
    var calendarMonth by remember { mutableIntStateOf(initDate.month.number - 1) }
    var selectedDate by remember { mutableStateOf(initDate) }
    var workingHour by remember { mutableIntStateOf(initialDateTime.hour.coerceIn(0, 23)) }
    var workingMinute by remember { mutableIntStateOf(initialDateTime.minute.coerceIn(0, 59)) }

    val monthNames = listOf(
        stringResource(Res.string.month_1),
        stringResource(Res.string.month_2),
        stringResource(Res.string.month_3),
        stringResource(Res.string.month_4),
        stringResource(Res.string.month_5),
        stringResource(Res.string.month_6),
        stringResource(Res.string.month_7),
        stringResource(Res.string.month_8),
        stringResource(Res.string.month_9),
        stringResource(Res.string.month_10),
        stringResource(Res.string.month_11),
        stringResource(Res.string.month_12),
    )
    val weekdays = listOf(
        stringResource(Res.string.weekday_monday),
        stringResource(Res.string.weekday_tuesday),
        stringResource(Res.string.weekday_wednesday),
        stringResource(Res.string.weekday_thursday),
        stringResource(Res.string.weekday_friday),
        stringResource(Res.string.weekday_saturday),
        stringResource(Res.string.weekday_sunday),
    )

    fun prevMonth() {
        if (calendarMonth == 0) { calendarMonth = 11; calendarYear-- } else calendarMonth--
    }

    fun nextMonth() {
        if (calendarMonth == 11) { calendarMonth = 0; calendarYear++ } else calendarMonth++
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(14.dp))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text(
                text = stringResource(Res.string.select_datetime),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.year_month_format, calendarYear, monthNames[calendarMonth]),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    NavButton("‹") { prevMonth() }
                    NavButton("›") { nextMonth() }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                weekdays.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(vertical = 2.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            CalendarGrid(
                year = calendarYear,
                month = calendarMonth,
                today = today,
                tempSelected = selectedDate,
                selectableToTodayOnly = selectableToTodayOnly,
                onDayClick = { selectedDate = it },
            )

            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Border),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WheelColumn(
                    label = stringResource(Res.string.hour_label),
                    valueRange = 0..23,
                    initialValue = workingHour,
                    onValueChanged = { workingHour = it },
                )

                Text(
                    text = ":",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )

                WheelColumn(
                    label = stringResource(Res.string.minute_label),
                    valueRange = 0..59,
                    initialValue = workingMinute,
                    onValueChanged = { workingMinute = it },
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SecondaryButton(
                    label = stringResource(Res.string.cancel),
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss,
                )
                PrimaryButton(
                    label = stringResource(Res.string.confirm),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val ldt = kotlinx.datetime.LocalDateTime(
                            selectedDate,
                            kotlinx.datetime.LocalTime(workingHour, workingMinute),
                        )
                        onDateTimeSelected(ldt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds())
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun DateTimePickerPreview() {
    var showDialog by remember { mutableStateOf(false) }
    var dateTimeMillis by remember { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }

    GainfulTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(20.dp),
        ) {
            DateTimePickerField(
                label = "日期与时间",
                dateTimeMillis = dateTimeMillis,
                onClick = { showDialog = true },
            )

            Spacer(modifier = Modifier.height(16.dp))

            val dt = Instant.fromEpochMilliseconds(dateTimeMillis).toLocalDateTime(TimeZone.currentSystemDefault())
            Text(
                text = "已选：${dt.date.year}-${dt.date.month.number.pad2()}-${dt.date.day.pad2()} ${dt.hour.pad2()}:${dt.minute.pad2()}",
                fontSize = 15.sp,
                color = TextSecondary,
            )
        }

        if (showDialog) {
            DateTimePickerDialog(
                initialSelectedDateTimeMillis = dateTimeMillis,
                onDateTimeSelected = {
                    dateTimeMillis = it
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }
    }
}
