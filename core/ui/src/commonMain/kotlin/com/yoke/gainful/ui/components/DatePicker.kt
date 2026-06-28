package com.yoke.gainful.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import gainful.core.ui.generated.resources.Res
import gainful.core.ui.generated.resources.cancel
import gainful.core.ui.generated.resources.confirm
import gainful.core.ui.generated.resources.date_format
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
import gainful.core.ui.generated.resources.select_date
import gainful.core.ui.generated.resources.today
import gainful.core.ui.generated.resources.weekday_friday
import gainful.core.ui.generated.resources.weekday_monday
import gainful.core.ui.generated.resources.weekday_saturday
import gainful.core.ui.generated.resources.weekday_sunday
import gainful.core.ui.generated.resources.weekday_thursday
import gainful.core.ui.generated.resources.weekday_tuesday
import gainful.core.ui.generated.resources.weekday_wednesday
import gainful.core.ui.generated.resources.year_month_format
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import com.yoke.gainful.common.extensions.pad2
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun DatePickerField(
    label: String,
    dateMillis: Long?,
    onClick: () -> Unit,
) {
    val dateStr = dateMillis?.let {
        val d = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
        stringResource(Res.string.date_format, d.year, d.month.number, d.day)
    }
    val displayText = dateStr ?: stringResource(Res.string.select_date)

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
                text = "📅",
                fontSize = 16.sp,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = displayText,
                fontSize = 15.sp,
                color = if (dateStr == null) TextMuted else TextPrimary,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "▾",
                fontSize = 14.sp,
                color = TextMuted,
            )
        }
    }
}

@Composable
fun CalendarDialog(
    initialSelectedDateMillis: Long? = null,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    selectableToTodayOnly: Boolean = false,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val selectedDate = initialSelectedDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val initialYear = selectedDate?.year ?: today.year
    val initialMonth = (selectedDate?.month?.number ?: today.month.number) - 1

    var year by remember { mutableIntStateOf(initialYear) }
    var month by remember { mutableIntStateOf(initialMonth) }
    var tempSelected by remember { mutableStateOf(selectedDate ?: today) }

    val monthNames = listOf(
        stringResource(Res.string.month_1), stringResource(Res.string.month_2),
        stringResource(Res.string.month_3), stringResource(Res.string.month_4),
        stringResource(Res.string.month_5), stringResource(Res.string.month_6),
        stringResource(Res.string.month_7), stringResource(Res.string.month_8),
        stringResource(Res.string.month_9), stringResource(Res.string.month_10),
        stringResource(Res.string.month_11), stringResource(Res.string.month_12),
    )
    val weekdays = listOf(
        stringResource(Res.string.weekday_monday), stringResource(Res.string.weekday_tuesday),
        stringResource(Res.string.weekday_wednesday), stringResource(Res.string.weekday_thursday),
        stringResource(Res.string.weekday_friday), stringResource(Res.string.weekday_saturday),
        stringResource(Res.string.weekday_sunday),
    )

    fun prevMonth() {
        if (month == 0) { month = 11; year-- } else month--
    }

    fun nextMonth() {
        if (month == 11) { month = 0; year++ } else month++
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(14.dp))
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.year_month_format, year, monthNames[month]),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    NavButton("‹") { prevMonth() }
                    NavButton("›") { nextMonth() }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                weekdays.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CalendarGrid(
                year = year,
                month = month,
                today = today,
                tempSelected = tempSelected,
                selectableToTodayOnly = selectableToTodayOnly,
                onDayClick = { tempSelected = it },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SecondaryButton(
                    label = stringResource(Res.string.today),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        year = today.year
                        month = today.month.number - 1
                        tempSelected = today
                    },
                )
                SecondaryButton(label = stringResource(Res.string.cancel), modifier = Modifier.weight(1f), onClick = onDismiss)
                PrimaryButton(
                    label = stringResource(Res.string.confirm),
                    modifier = Modifier.weight(1f),
                    onClick = { onDateSelected(kotlinx.datetime.LocalDateTime(tempSelected, kotlinx.datetime.LocalTime(0, 0)).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()) },
                )
            }
        }
    }
}

@Composable
internal fun CalendarGrid(
    year: Int,
    month: Int,
    today: LocalDate,
    tempSelected: LocalDate,
    selectableToTodayOnly: Boolean,
    onDayClick: (LocalDate) -> Unit,
) {
    val firstDayOfMonth = LocalDate(year, month + 1, 1)
    val firstDayOfNextMonth = firstDayOfMonth.plus(1, DateTimeUnit.MONTH)
    val daysInMonth = (firstDayOfNextMonth.toEpochDays() - firstDayOfMonth.toEpochDays()).toInt()
    val offset = firstDayOfMonth.dayOfWeek.ordinal

    Column(modifier = Modifier.animateContentSize()) {
        val totalCells = offset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - offset + 1
                    if (dayNum in 1..daysInMonth) {
                        val date = LocalDate(year, month + 1, dayNum)
                        val isToday = date == today
                        val isSelected = date == tempSelected
                        val isAfterToday = selectableToTodayOnly && date > today
                        val bgColor = when {
                            isSelected && !isAfterToday -> Gold
                            isToday -> GoldDim
                            else -> Color.Transparent
                        }
                        val textColor = when {
                            isSelected && !isAfterToday -> Background
                            isToday -> TextPrimary
                            isAfterToday -> TextMuted.copy(alpha = 0.4f)
                            else -> TextSecondary
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .then(
                                    if (isAfterToday) Modifier else Modifier.clickable { onDayClick(date) }
                                )
                                .background(bgColor),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$dayNum",
                                fontSize = 15.sp,
                                fontWeight = if (isSelected && !isAfterToday || isToday) FontWeight.Bold else FontWeight.SemiBold,
                                color = textColor,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DatePickerPreview() {
    var showDialog by remember { mutableStateOf(false) }
    var dateMillis by remember { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }

    GainfulTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(20.dp),
        ) {
            DatePickerField(
                label = "交易日期",
                dateMillis = dateMillis,
                onClick = { showDialog = true },
            )

            Spacer(modifier = Modifier.height(16.dp))

            val dateStr = if (dateMillis > 0) {
                val d = Instant.fromEpochMilliseconds(dateMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
                "${d.year}-${d.month.number.pad2()}-${d.day.pad2()}"
            } else "未选择"
            Text(
                text = "已选日期：$dateStr",
                fontSize = 15.sp,
                color = TextSecondary,
            )
        }

        if (showDialog) {
            CalendarDialog(
                initialSelectedDateMillis = if (dateMillis > 0) dateMillis else null,
                onDateSelected = {
                    dateMillis = it
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }
    }
}
