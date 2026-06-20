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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import androidx.compose.ui.tooling.preview.Preview
import com.yoke.gainful.ui.theme.TextSecondary
import kotlin.math.abs
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val TimeItemHeight = 44.dp
private const val VisibleItems = 5
private const val HalfVisible = VisibleItems / 2

@Composable
fun TimePickerField(
    label: String,
    hour: Int,
    minute: Int,
    onClick: () -> Unit,
) {
    val hasValue = hour in 0..23 && minute in 0..59
    val displayText = if (hasValue) "${hour.pad2()}:${minute.pad2()}" else "选择时间"

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
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    var workingHour by remember { mutableIntStateOf(initialHour.coerceIn(0, 23)) }
    var workingMinute by remember { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 340.dp)
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
                    text = "选择时间",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TimeFooterButton(
                        label = "现在",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            workingHour = now.hour
                            workingMinute = now.minute
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
                        onClick = { onTimeSelected(workingHour, workingMinute) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WheelColumn(
    label: String,
    valueRange: IntRange,
    initialValue: Int,
    onValueChanged: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val itemHeightPx = with(density) { TimeItemHeight.toPx() }

    val itemCount = valueRange.count()
    val repeatCount = 1000
    val totalItems = itemCount * repeatCount
    val middleOffset = (repeatCount / 2) * itemCount

    val startIndex = middleOffset + (initialValue - valueRange.first)

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = startIndex - HalfVisible,
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (!isScrolling) {
                    val offset = listState.firstVisibleItemScrollOffset
                    if (offset != 0) {
                        val target = if (offset > itemHeightPx / 2f) {
                            listState.firstVisibleItemIndex + 1
                        } else {
                            listState.firstVisibleItemIndex
                        }
                        listState.animateScrollToItem(target)
                    }
                }
            }
    }

    val currentValue by remember {
        derivedStateOf {
            val centerIndex = listState.firstVisibleItemIndex + HalfVisible
            val wrapped = ((centerIndex % itemCount) + itemCount) % itemCount
            (wrapped + valueRange.first).coerceIn(valueRange)
        }
    }

    val centerIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex + HalfVisible }
    }

    LaunchedEffect(currentValue) {
        onValueChanged(currentValue)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .width(96.dp)
                .height(TimeItemHeight * VisibleItems)
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(10.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TimeItemHeight)
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(totalItems) { index ->
                    val valueIndex = index % itemCount
                    val value = valueIndex + valueRange.first

                    val distance = abs(index - centerIndex)

                    val isActive = distance == 0
                    val isNear = distance == 1

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(TimeItemHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = value.pad2(),
                            fontSize = if (isActive) 24.sp else if (isNear) 18.sp else 20.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = if (isActive) TextPrimary else if (isNear) TextSecondary else TextMuted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeFooterButton(
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

private fun Int.pad2(): String = if (this < 10) "0$this" else "$this"

@Preview
@Composable
fun TimePickerPreview() {
    var showDialog by remember { mutableStateOf(false) }
    var hour by remember { mutableIntStateOf(14) }
    var minute by remember { mutableIntStateOf(30) }

    GainfulTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(20.dp),
        ) {
            TimePickerField(
                label = "交易时间",
                hour = hour,
                minute = minute,
                onClick = { showDialog = true },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "已选时间：${hour.pad2()}:${minute.pad2()}",
                fontSize = 15.sp,
                color = TextSecondary,
            )
        }

        if (showDialog) {
            TimePickerDialog(
                initialHour = hour,
                initialMinute = minute,
                onTimeSelected = { h, m ->
                    hour = h
                    minute = m
                    showDialog = false
                },
                onDismiss = { showDialog = false },
            )
        }
    }
}
