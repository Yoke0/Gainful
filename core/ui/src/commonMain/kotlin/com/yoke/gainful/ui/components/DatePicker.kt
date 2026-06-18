 package com.yoke.gainful.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun DatePickerField(
    label: String,
    date: String,
    onClick: () -> Unit,
) {
    val displayText = date.ifBlank { "选择日期" }

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
                color = if (date.isBlank()) TextMuted else TextPrimary,
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
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    selectableToTodayOnly: Boolean = false,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val initialYear = selectedDate?.year ?: today.year
    val initialMonth = (selectedDate?.month?.number ?: today.month.number) - 1

    var year by remember { mutableIntStateOf(initialYear) }
    var month by remember { mutableIntStateOf(initialMonth) }
    var tempSelected by remember { mutableStateOf(selectedDate ?: today) }

    val monthNames = listOf("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月")
    val weekdays = listOf("一", "二", "三", "四", "五", "六", "日")

    val firstDayOfMonth = LocalDate(year, month + 1, 1)
    val daysInMonth = (LocalDate(year, month + 2, 1).toEpochDays() - firstDayOfMonth.toEpochDays()).toInt()
    val offset = firstDayOfMonth.dayOfWeek.ordinal

    fun prevMonth() {
        if (month == 0) { month = 11; year-- } else month--
    }

    fun nextMonth() {
        if (month == 11) { month = 0; year++ } else month++
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
                    .padding(20.dp),
            ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${year}年${monthNames[month]}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        CalendarNavButton("‹") { prevMonth() }
                        CalendarNavButton("›") { nextMonth() }
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
                                            if (isAfterToday) Modifier else Modifier.clickable { tempSelected = date }
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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CalendarFooterButton(
                        label = "今天",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            year = today.year
                            month = today.month.number - 1
                            tempSelected = today
                        },
                    )
                    CalendarFooterButton(label = "取消", isPrimary = false, modifier = Modifier.weight(1f), onClick = onDismiss)
                    CalendarFooterButton(
                        label = "确定",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        onClick = { onDateSelected(tempSelected) },
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun CalendarNavButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(Surface)
            .border(1.dp, Border, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
        )
    }
}

@Composable
private fun CalendarFooterButton(
    label: String,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .background(if (isPrimary) Gold else Surface)
            .border(if (isPrimary) 0.dp else 1.dp, Border, RoundedCornerShape(50)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isPrimary) Background else TextSecondary,
        )
    }
}
