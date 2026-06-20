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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

@Composable
fun DateTimePickerField(
    label: String,
    date: LocalDate?,
    hour: Int,
    minute: Int,
    onClick: () -> Unit,
) {
    val hasDate = date != null
    val hasTime = hour in 0..23 && minute in 0..59

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
            if (hasDate && hasTime) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextPrimary, fontFamily = FontFamily.Monospace)) {
                            append("${date.year}年${date.month.number}月${date.day}日")
                        }
                        withStyle(SpanStyle(color = TextMuted)) {
                            append(" · ")
                        }
                        withStyle(SpanStyle(color = Gold, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)) {
                            append("${hour.pad2()}:${minute.pad2()}")
                        }
                    },
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                )
            } else {
                Text(
                    text = "选择日期和时间",
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
    initialDate: LocalDate?,
    initialHour: Int,
    initialMinute: Int,
    onDateTimeSelected: (date: LocalDate, hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    selectableToTodayOnly: Boolean = false,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val initDate = initialDate ?: today
    var calendarYear by remember { mutableIntStateOf(initDate.year) }
    var calendarMonth by remember { mutableIntStateOf(initDate.month.number - 1) }
    var selectedDate by remember { mutableStateOf(initDate) }
    var workingHour by remember { mutableIntStateOf(initialHour.coerceIn(0, 23)) }
    var workingMinute by remember { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }

    val monthNames = listOf("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月")
    val weekdays = listOf("一", "二", "三", "四", "五", "六", "日")

    fun prevMonth() {
        if (calendarMonth == 0) { calendarMonth = 11; calendarYear-- } else calendarMonth--
    }

    fun nextMonth() {
        if (calendarMonth == 11) { calendarMonth = 0; calendarYear++ } else calendarMonth++
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 360.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
                    .padding(20.dp),
            ) {
                Text(
                    text = "选择日期和时间",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Calendar header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${calendarYear}年${monthNames[calendarMonth]}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        CalendarNavButton("‹") { prevMonth() }
                        CalendarNavButton("›") { nextMonth() }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Weekday headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekdays.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f).padding(vertical = 2.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Calendar grid
                CalendarGrid(
                    year = calendarYear,
                    month = calendarMonth,
                    today = today,
                    tempSelected = selectedDate,
                    selectableToTodayOnly = selectableToTodayOnly,
                    onDayClick = { selectedDate = it },
                )

                // Divider
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Border),
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Time wheels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WheelColumn(
                        label = "时",
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
                        label = "分",
                        valueRange = 0..59,
                        initialValue = workingMinute,
                        onValueChanged = { workingMinute = it },
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Footer buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TimeFooterButton(
                        label = "现在",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedDate = today
                            workingHour = now.hour
                            workingMinute = now.minute
                            calendarYear = today.year
                            calendarMonth = today.month.number - 1
                        },
                    )
                    TimeFooterButton(
                        label = "取消",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                    )
                    TimeFooterButton(
                        label = "确认",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onDateTimeSelected(selectedDate, workingHour, workingMinute)
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DateTimePickerPreview() {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var hour by remember { mutableIntStateOf(14) }
    var minute by remember { mutableIntStateOf(30) }

    GainfulTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(20.dp),
        ) {
            DateTimePickerField(
                label = "日期与时间",
                date = selectedDate,
                hour = hour,
                minute = minute,
                onClick = { showDialog = true },
            )

            Spacer(modifier = Modifier.height(16.dp))

            val dateStr = selectedDate?.let { "${it.year}-${it.month.number.pad2()}-${it.day.pad2()}" } ?: "未选择"
            Text(
                text = "已选：$dateStr ${hour.pad2()}:${minute.pad2()}",
                fontSize = 15.sp,
                color = TextSecondary,
            )
        }

        if (showDialog) {
            DateTimePickerDialog(
                initialDate = selectedDate,
                initialHour = hour,
                initialMinute = minute,
                onDateTimeSelected = { date, h, m ->
                    selectedDate = date
                    hour = h
                    minute = m
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }
    }
}
