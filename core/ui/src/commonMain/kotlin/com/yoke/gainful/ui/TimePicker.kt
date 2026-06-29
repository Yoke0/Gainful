package com.yoke.gainful.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yoke.gainful.common.extensions.pad2
import com.yoke.gainful.designsystem.components.PrimaryButton
import com.yoke.gainful.designsystem.components.SecondaryButton
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.GainfulTheme
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import gainful.core.ui.generated.resources.Res
import gainful.core.ui.generated.resources.cancel
import gainful.core.ui.generated.resources.confirm
import gainful.core.ui.generated.resources.now
import gainful.core.ui.generated.resources.select_time
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.Instant

internal val TIME_ITEM_HEIGHT = 44.dp
internal const val VISIBLE_ITEMS = 5
internal const val HALF_VISIBLE = VISIBLE_ITEMS / 2

@Composable
internal fun TimePickerWheel(
    workingHour: Int,
    workingMinute: Int,
    onHourChanged: (Int) -> Unit,
    onMinuteChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WheelColumn(
            valueRange = 0..23,
            initialValue = workingHour,
            onValueChanged = onHourChanged,
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
            valueRange = 0..59,
            initialValue = workingMinute,
            onValueChanged = onMinuteChanged,
        )
    }
}

@Composable
fun TimePickerField(
    label: String,
    dateTimeMillis: Long?,
    onClick: () -> Unit,
) {
    val time =
        dateTimeMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
        }
    val hasValue = time != null
    val displayText = if (hasValue) "${time.hour.pad2()}:${time.minute.pad2()}" else stringResource(Res.string.select_time)

    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier =
                Modifier
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
                text = "\uD83D\uDD50",
                fontSize = 16.sp,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = displayText,
                fontSize = 15.sp,
                color = if (hasValue) Gold else TextMuted,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (hasValue) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "\u25BE",
                fontSize = 14.sp,
                color = TextMuted,
            )
        }
    }
}

@Composable
fun TimePickerDialog(
    initialSelectedTimeMillis: Long? = null,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val initialTime =
        initialSelectedTimeMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
        } ?: now
    var workingHour by remember { mutableIntStateOf(initialTime.hour.coerceIn(0, 23)) }
    var workingMinute by remember { mutableIntStateOf(initialTime.minute.coerceIn(0, 59)) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
                    .padding(20.dp),
        ) {
            Text(
                text = stringResource(Res.string.select_time),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            TimePickerWheel(
                workingHour = workingHour,
                workingMinute = workingMinute,
                onHourChanged = { workingHour = it },
                onMinuteChanged = { workingMinute = it },
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SecondaryButton(
                    label = stringResource(Res.string.now),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val currentNow = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        workingHour = currentNow.hour
                        workingMinute = currentNow.minute
                    },
                )
                SecondaryButton(
                    label = stringResource(Res.string.cancel),
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss,
                )
                PrimaryButton(
                    label = stringResource(Res.string.confirm),
                    modifier = Modifier.weight(1f),
                    onClick = { onTimeSelected(workingHour, workingMinute) },
                )
            }
        }
    }
}

@Composable
internal fun WheelColumn(
    valueRange: IntRange,
    initialValue: Int,
    onValueChanged: (Int) -> Unit,
) {
    val itemCount = valueRange.count()
    val repeatCount = 1000
    val totalItems = itemCount * repeatCount
    val middleOffset = (repeatCount / 2) * itemCount

    val startIndex = middleOffset + (initialValue - valueRange.first)

    val listState =
        rememberLazyListState(
            initialFirstVisibleItemIndex = startIndex - HALF_VISIBLE,
        )

    val snapBehavior = rememberSnapFlingBehavior(listState)

    LaunchedEffect(initialValue) {
        val target = middleOffset + (initialValue - valueRange.first)
        listState.scrollToItem(target - HALF_VISIBLE)
    }

    val currentValue by remember {
        derivedStateOf {
            val centerIndex = listState.firstVisibleItemIndex + HALF_VISIBLE
            val wrapped = ((centerIndex % itemCount) + itemCount) % itemCount
            (wrapped + valueRange.first).coerceIn(valueRange)
        }
    }

    val centerIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex + HALF_VISIBLE }
    }

    LaunchedEffect(currentValue) {
        onValueChanged(currentValue)
    }

    Box(
        modifier =
            Modifier
                .width(96.dp)
                .height(TIME_ITEM_HEIGHT * VISIBLE_ITEMS)
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(10.dp)),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(TIME_ITEM_HEIGHT)
                    .align(Alignment.Center)
                    .background(GoldDim)
                    .drawBehind {
                        val stroke = 1.dp.toPx()
                        drawLine(
                            color = Gold,
                            start = Offset(0f, stroke / 2),
                            end = Offset(size.width, stroke / 2),
                            strokeWidth = stroke,
                        )
                        drawLine(
                            color = Gold,
                            start = Offset(0f, size.height - stroke / 2),
                            end = Offset(size.width, size.height - stroke / 2),
                            strokeWidth = stroke,
                        )
                    },
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(totalItems) { index ->
                val valueIndex = index % itemCount
                val value = valueIndex + valueRange.first

                val distance = abs(index - centerIndex).toFloat()
                val t = (distance / 2f).coerceIn(0f, 1f)

                val fontSize = (24f - (24f - 16f) * t).sp
                val fontWeight = if (distance < 0.5f) FontWeight.Bold else FontWeight.Medium
                val alpha = 1f - 0.6f * t
                val color = if (distance < 0.5f) TextPrimary else TextSecondary

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(TIME_ITEM_HEIGHT),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = value.pad2(),
                        fontSize = fontSize,
                        fontWeight = fontWeight,
                        fontFamily = FontFamily.Monospace,
                        color = color.copy(alpha = alpha),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TimePickerPreview() {
    var showDialog by remember { mutableStateOf(false) }
    var timeMillis by remember { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }

    GainfulTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(20.dp),
        ) {
            TimePickerField(
                label = "交易时间",
                dateTimeMillis = timeMillis,
                onClick = { showDialog = true },
            )

            Spacer(modifier = Modifier.height(16.dp))

            val time = Instant.fromEpochMilliseconds(timeMillis).toLocalDateTime(TimeZone.currentSystemDefault())
            Text(
                text = "已选时间：${time.hour.pad2()}:${time.minute.pad2()}",
                fontSize = 15.sp,
                color = TextSecondary,
            )
        }

        if (showDialog) {
            TimePickerDialog(
                initialSelectedTimeMillis = timeMillis,
                onTimeSelected = { h, m ->
                    val currentDate = Instant.fromEpochMilliseconds(timeMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
                    timeMillis =
                        LocalDateTime(
                            currentDate,
                            LocalTime(h, m),
                        ).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }
    }
}
