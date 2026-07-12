package com.yoke.gainful.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatCompact
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.common.extensions.formatSigned
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Blue
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.Purple
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.Surface2
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.KLine
import gainful.core.ui.generated.resources.Res
import gainful.core.ui.generated.resources.kline_change_amount
import gainful.core.ui.generated.resources.kline_close
import gainful.core.ui.generated.resources.kline_high
import gainful.core.ui.generated.resources.kline_low
import gainful.core.ui.generated.resources.kline_open
import gainful.core.ui.generated.resources.kline_turnover
import gainful.core.ui.generated.resources.kline_turnover_rate
import gainful.core.ui.generated.resources.kline_volume
import gainful.core.ui.generated.resources.kline_volume_format
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import kotlin.random.Random

private data class KLineMaConfig(
    val period: Int,
    val color: Color,
    val strokeWidth: Float = 1.2f,
)

private val maConfigs =
    listOf(
        KLineMaConfig(5, Gold, 1.4f),
        KLineMaConfig(10, Blue, 1.2f),
        KLineMaConfig(20, Purple, 1.2f),
    )

@Composable
fun KLineChart(
    kLines: List<KLine>,
    modifier: Modifier = Modifier,
) {
    if (kLines.isEmpty()) return

    val density = LocalDensity.current
    val candleWidth = with(density) { 8.dp.toPx() }
    val yAxisWidth = with(density) { 44.dp.toPx() }
    val chartPadding = with(density) { 6.dp.toPx() }
    val maLegendHeight = with(density) { 16.dp.toPx() }
    val dateAreaHeight = with(density) { 18.dp.toPx() }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var touchX by remember { mutableFloatStateOf(0f) }
    var touchY by remember { mutableFloatStateOf(0f) }
    var scrollOffset by remember { mutableFloatStateOf(0f) }
    var viewportWidth by remember { mutableFloatStateOf(0f) }

    val textMeasurer = rememberTextMeasurer()
    val labelTextStyle = TextStyle(fontSize = 10.sp, color = TextMuted)

    val totalCandleCount = kLines.size
    val bodyWidth = (candleWidth * 0.58f).coerceAtLeast(3f)

    val currentGainColor = gainColor
    val currentLossColor = lossColor

    val maDataList =
        maConfigs.map { config ->
            calculateMA(kLines, config.period)
        }

    val totalContentWidth = candleWidth * totalCandleCount + yAxisWidth + chartPadding

    LaunchedEffect(kLines) {
        selectedIndex = null
        if (viewportWidth > 0f) {
            scrollOffset = (candleWidth * totalCandleCount + yAxisWidth - viewportWidth).coerceAtLeast(0f)
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        viewportWidth = it.width.toFloat()
                        val maxOffset = (candleWidth * totalCandleCount + yAxisWidth - viewportWidth).coerceAtLeast(0f)
                        if (scrollOffset > maxOffset) {
                            scrollOffset = maxOffset
                        } else if (scrollOffset == 0f && totalContentWidth > viewportWidth) {
                            scrollOffset = maxOffset
                        }
                    }
                    .pointerInput(kLines) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val chartX = offset.x + scrollOffset - yAxisWidth
                                val idx = (chartX / candleWidth).toInt()
                                if (idx in 0 until totalCandleCount) {
                                    selectedIndex = idx
                                    touchX = offset.x
                                    touchY = offset.y
                                } else {
                                    selectedIndex = null
                                }
                            },
                            onDrag = { change, dragAmount ->
                                scrollOffset =
                                    (scrollOffset - dragAmount.x).coerceIn(
                                        0f,
                                        (candleWidth * totalCandleCount + yAxisWidth - viewportWidth).coerceAtLeast(0f),
                                    )
                                change.consume()

                                val chartX = change.position.x + scrollOffset - yAxisWidth
                                val idx = (chartX / candleWidth).toInt()
                                if (idx in 0 until totalCandleCount) {
                                    selectedIndex = idx
                                    touchX = change.position.x
                                    touchY = change.position.y
                                }
                            },
                            onDragEnd = {
                                selectedIndex = null
                            },
                        )
                    }
                    .pointerInput(kLines) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                when (event.type) {
                                    PointerEventType.Move -> {
                                        val change = event.changes.first()
                                        val chartX = change.position.x + scrollOffset - yAxisWidth
                                        val idx = (chartX / candleWidth).toInt()
                                        if (idx in 0 until totalCandleCount) {
                                            selectedIndex = idx
                                            touchX = change.position.x
                                            touchY = change.position.y
                                        }
                                    }

                                    PointerEventType.Exit -> {
                                        selectedIndex = null
                                    }
                                }
                            }
                        }
                    },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val chartH = h - maLegendHeight
                val volRatio = 0.22f
                val volH = chartH * volRatio
                val klineH = chartH - volH - dateAreaHeight
                val dateAreaTop = maLegendHeight + klineH
                val dateAreaBot = dateAreaTop + dateAreaHeight

                val xMid = { i: Int -> yAxisWidth + i * candleWidth + candleWidth / 2f - scrollOffset }

                val visStart = ((scrollOffset - yAxisWidth) / candleWidth).toInt().coerceIn(0, totalCandleCount - 1)
                val visEnd = ((scrollOffset + viewportWidth - yAxisWidth) / candleWidth).toInt().coerceIn(0, totalCandleCount - 1)

                var maxVolume = kLines[visStart].volume
                for (i in visStart..visEnd) {
                    maxVolume = maxOf(maxVolume, kLines[i].volume)
                }

                var pMin = Float.MAX_VALUE
                var pMax = Float.MIN_VALUE
                for (i in visStart..visEnd) {
                    val c = kLines[i]
                    pMin = minOf(pMin, c.low.toFloat())
                    pMax = maxOf(pMax, c.high.toFloat())
                }
                maDataList.forEach { ma ->
                    for (i in visStart..visEnd) {
                        val v = ma[i]
                        if (v != null) {
                            pMin = minOf(pMin, v.toFloat())
                            pMax = maxOf(pMax, v.toFloat())
                        }
                    }
                }
                val pm = (pMax - pMin) * 0.05f
                pMin -= pm
                pMax += pm
                if (pMin == pMax) pMax = pMin + 1f
                val pr = pMax - pMin
                val toY = { v: Double -> maLegendHeight + klineH * (1f - (v.toFloat() - pMin) / pr) }
                val toP = { y: Float -> pMax - (y - maLegendHeight) / klineH * pr }

                val visMin =
                    kLines[visStart].low.toFloat().let { low ->
                        (visStart..visEnd).fold(low) { min, i -> minOf(min, kLines[i].low.toFloat()) }
                    }
                val visMax =
                    kLines[visStart].high.toFloat().let { high ->
                        (visStart..visEnd).fold(high) { max, i -> maxOf(max, kLines[i].high.toFloat()) }
                    }
                val visMaxText = visMax.toDouble().formatLocalized(2)
                val visMinText = visMin.toDouble().formatLocalized(2)
                val visMaxMeasured = textMeasurer.measure(visMaxText, labelTextStyle)
                val visMinMeasured = textMeasurer.measure(visMinText, labelTextStyle)
                drawText(
                    visMaxMeasured,
                    topLeft = Offset(6f, toY(visMax.toDouble()) - visMaxMeasured.size.height / 2f),
                )
                drawText(
                    visMinMeasured,
                    topLeft = Offset(6f, toY(visMin.toDouble()) - visMinMeasured.size.height / 2f),
                )

                val lastClose = kLines.last().close
                if (lastClose.toFloat() in pMin..pMax) {
                    val lcY = toY(lastClose)
                    drawLine(
                        color = Gold.copy(alpha = 0.4f),
                        start = Offset(0f, lcY),
                        end = Offset(w - chartPadding, lcY),
                        strokeWidth = 0.8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f)),
                    )
                }

                kLines.forEachIndexed { i, d ->
                    val cx = xMid(i)
                    if (cx > -candleWidth && cx < w + candleWidth) {
                        drawCandle(cx, d, bodyWidth, toY, currentGainColor, currentLossColor)
                    }
                }

                maConfigs.forEachIndexed { idx, config ->
                    drawMALine(maDataList[idx], config.color, config.strokeWidth, xMid, toY)
                }

                val legendIdx = selectedIndex?.coerceIn(0, totalCandleCount - 1) ?: visEnd
                var legendX = w - chartPadding - 4f
                val legendY = 6f
                for (idx in maConfigs.lastIndex downTo 0) {
                    val config = maConfigs[idx]
                    val value = maDataList[idx].getOrNull(legendIdx) ?: continue
                    val text = "MA${config.period}:${value.formatLocalized(2)}"
                    val style = TextStyle(fontSize = 10.sp, color = config.color)
                    val measured = textMeasurer.measure(text, style)
                    legendX -= measured.size.width
                    drawText(measured, topLeft = Offset(legendX, legendY))
                    legendX -= 10f
                }

                drawXLabels(kLines, xMid, dateAreaTop, w, textMeasurer, labelTextStyle)

                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(yAxisWidth, dateAreaBot),
                    end = Offset(w - chartPadding, dateAreaBot),
                    strokeWidth = 0.5f,
                )

                val vBarW = (candleWidth * 0.5f).coerceAtLeast(2f)
                kLines.forEachIndexed { i, d ->
                    val xc = xMid(i)
                    if (xc > -candleWidth && xc < w + candleWidth) {
                        val isGain = d.close >= d.open
                        val barH = (d.volume.toFloat() / maxVolume) * volH
                        val fillColor = if (isGain) currentGainColor.copy(alpha = 0.4f) else currentLossColor.copy(alpha = 0.4f)
                        drawRect(
                            color = fillColor,
                            topLeft = Offset(xc - vBarW / 2f, dateAreaBot + volH - barH),
                            size = Size(vBarW, barH),
                        )
                    }
                }

                val idx = selectedIndex
                if (idx != null && idx in 0 until totalCandleCount) {
                    val d = kLines[idx]
                    val cx = xMid(idx)

                    drawCandle(cx, d, bodyWidth, toY, currentGainColor, currentLossColor, strokeWidth = 1.6f)

                    val volBottom = dateAreaBot + volH
                    drawLine(
                        color = Gold.copy(alpha = 0.35f),
                        start = Offset(cx, maLegendHeight),
                        end = Offset(cx, volBottom),
                        strokeWidth = 0.8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 3f)),
                    )

                    val my = touchY.coerceIn(maLegendHeight, maLegendHeight + klineH)
                    drawLine(
                        color = Gold.copy(alpha = 0.35f),
                        start = Offset(yAxisWidth, my),
                        end = Offset(w - chartPadding, my),
                        strokeWidth = 0.8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 3f)),
                    )

                    val tagText = toP(my).toDouble().formatLocalized(2)
                    val tagTextStyle = TextStyle(fontSize = 10.sp, color = Background)
                    val tagMeasured = textMeasurer.measure(tagText, tagTextStyle)
                    val tw = tagMeasured.size.width + 20f
                    val th = 24f
                    val tagX = yAxisWidth - 6f - tw
                    val tagY = my - th / 2f
                    drawRoundRect(
                        color = Gold,
                        topLeft = Offset(tagX, tagY),
                        size = Size(tw, th),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                    )
                    drawText(
                        tagMeasured,
                        topLeft = Offset(tagX + (tw - tagMeasured.size.width) / 2f, tagY + (th - tagMeasured.size.height) / 2f),
                    )
                }
            }

            val idx = selectedIndex
            if (idx != null && idx in 0 until totalCandleCount) {
                val candle = kLines[idx]
                KLineTooltip(candle = candle, x = touchX, y = touchY, viewportWidth = viewportWidth)
            }
        }
    }
}

private fun DrawScope.drawCandle(
    xc: Float,
    candle: KLine,
    bodyWidth: Float,
    toY: (Double) -> Float,
    gainColor: Color,
    lossColor: Color,
    strokeWidth: Float = 1.2f,
) {
    val isGain = candle.close >= candle.open
    val color = if (isGain) gainColor else lossColor

    drawLine(
        color = color,
        start = Offset(xc, toY(candle.high)),
        end = Offset(xc, toY(candle.low)),
        strokeWidth = strokeWidth,
    )

    val bTop = toY(maxOf(candle.open, candle.close))
    val bBot = toY(minOf(candle.open, candle.close))
    val bodyH = (bBot - bTop).coerceAtLeast(1f)
    drawRect(
        color = color,
        topLeft = Offset(xc - bodyWidth / 2f, bTop),
        size = Size(bodyWidth, bodyH),
    )
}

private fun DrawScope.drawMALine(
    maData: List<Double?>,
    color: Color,
    strokeWidth: Float,
    xMid: (Int) -> Float,
    toY: (Double) -> Float,
) {
    var prevX = 0f
    var prevY = 0f
    var hasPrev = false
    maData.forEachIndexed { i, v ->
        if (v == null) {
            hasPrev = false
            return@forEachIndexed
        }
        val x = xMid(i)
        val y = toY(v)
        if (hasPrev) {
            drawLine(
                color = color,
                start = Offset(prevX, prevY),
                end = Offset(x, y),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
        prevX = x
        prevY = y
        hasPrev = true
    }
}

private fun DrawScope.drawXLabels(
    kLines: List<KLine>,
    xMid: (Int) -> Float,
    bandTop: Float,
    viewportWidth: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
) {
    val count = kLines.size
    val isMinute = kLines.firstOrNull()?.date?.contains(' ') == true

    if (isMinute) {
        val step = maxOf(1, count / 6)
        val labels = mutableListOf<Int>()
        var i = 0
        while (i < count) {
            labels.add(i)
            i += step
        }
        if (labels.lastOrNull() != count - 1) {
            labels.add(count - 1)
        }
        var lastRightEdge = -Float.MAX_VALUE
        labels.forEach { idx ->
            val dateStr = kLines[idx].date
            val cx = xMid(idx)
            val measured = textMeasurer.measure(dateStr, textStyle)
            val left = cx - measured.size.width / 2f
            val right = cx + measured.size.width / 2f
            if (left > 0f && right < viewportWidth && left - lastRightEdge >= 4f) {
                drawText(
                    measured,
                    topLeft = Offset(left, bandTop + 3f),
                )
                lastRightEdge = right
            }
        }
    } else {
        val monthFirstCandles = mutableListOf<Int>()
        for (i in 0 until count) {
            val cur = kLines[i].date.substring(0, 7)
            val prev = if (i > 0) kLines[i - 1].date.substring(0, 7) else null
            if (cur != prev) {
                monthFirstCandles.add(i)
            }
        }

        val lineStyle = TextStyle(fontSize = 10.sp, color = TextMuted)
        var lastRightEdge = -Float.MAX_VALUE

        monthFirstCandles.forEach { idx ->
            val cx = xMid(idx)
            val label = kLines[idx].date.substring(0, 7)
            val measured = textMeasurer.measure(label, lineStyle)
            val left = cx - measured.size.width / 2f
            val right = cx + measured.size.width / 2f
            if (left > 0f && right < viewportWidth && left - lastRightEdge >= 4f) {
                drawText(
                    measured,
                    topLeft = Offset(left, bandTop + 3f),
                )
                lastRightEdge = right
            }
        }
    }
}

private fun calculateMA(
    kLines: List<KLine>,
    period: Int,
): List<Double?> {
    return List(kLines.size) { i ->
        if (i < period - 1) return@List null
        var sum = 0.0
        for (j in (i - period + 1)..i) {
            sum += kLines[j].close
        }
        sum / period
    }
}

@Composable
private fun KLineTooltip(
    candle: KLine,
    x: Float,
    y: Float,
    viewportWidth: Float,
) {
    val isGain = candle.close >= candle.open
    val chgColor = if (isGain) gainColor else lossColor
    val gap = with(LocalDensity.current) { 12.dp.toPx() }
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier =
            Modifier
                .onSizeChanged { tooltipSize = it }
                .offset {
                    val w = tooltipSize.width.toFloat()
                    val h = tooltipSize.height.toFloat()
                    val tx =
                        if (x + gap + w < viewportWidth) {
                            x + gap
                        } else {
                            (x - gap - w).coerceAtLeast(4f)
                        }
                    val ty =
                        if (y + gap + h < 320f) {
                            y + gap
                        } else {
                            (y - gap - h).coerceAtLeast(4f)
                        }
                    IntOffset(tx.toInt(), ty.toInt())
                }
                .wrapContentWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Surface2)
                .border(1.dp, Border, RoundedCornerShape(12.dp))
                .padding(12.dp),
    ) {
        Column(modifier = Modifier.width(240.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = candle.date,
                    style = TextStyle(fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold),
                )
                val badgeBg = if (isGain) gainColor.copy(alpha = 0.08f) else lossColor.copy(alpha = 0.08f)
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "${candle.changePercent.formatSigned(2)}%",
                        style = TextStyle(fontSize = 11.sp, color = chgColor, fontWeight = FontWeight.Bold),
                    )
                }
            }
            TooltipSeparator()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TooltipCellItem(Modifier.weight(1f), stringResource(Res.string.kline_open), candle.open.formatLocalized(2), chgColor)
                TooltipCellItem(Modifier.weight(1f), stringResource(Res.string.kline_close), candle.close.formatLocalized(2), chgColor)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TooltipCellItem(Modifier.weight(1f), stringResource(Res.string.kline_high), candle.high.formatLocalized(2), gainColor)
                TooltipCellItem(Modifier.weight(1f), stringResource(Res.string.kline_low), candle.low.formatLocalized(2), lossColor)
            }
            TooltipSeparator()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TooltipCellItem(
                    Modifier.weight(1f),
                    stringResource(Res.string.kline_change_amount),
                    candle.changeAmount.formatSigned(2),
                    chgColor,
                )
                TooltipCellItem(
                    Modifier.weight(1f),
                    stringResource(Res.string.kline_volume),
                    stringResource(Res.string.kline_volume_format, candle.volume.toDouble().formatCompact(2)),
                    TextMuted,
                )
            }
            TooltipSeparator()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TooltipCellItem(Modifier.weight(1f), stringResource(Res.string.kline_turnover), candle.turnover.formatCompact(2), TextMuted)
                TooltipCellItem(
                    Modifier.weight(1f),
                    stringResource(Res.string.kline_turnover_rate),
                    "${candle.turnoverRate.formatLocalized(2)}%",
                    TextMuted,
                )
            }
        }
    }
}

@Composable
private fun TooltipSeparator() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.06f)),
    )
}

@Composable
private fun TooltipCellItem(modifier: Modifier, label: String, value: String, valueColor: Color) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = TextStyle(fontSize = 12.sp, color = TextMuted),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = TextStyle(fontSize = 12.sp, color = valueColor, fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
    }
}

private fun generateKlineData(n: Int): List<KLine> {
    val data = mutableListOf<KLine>()
    var price = 18.48
    for (i in 0 until n) {
        val drift = (Random.nextDouble() - 0.48) * 0.6
        val vol = 0.2 + Random.nextDouble() * 0.4
        val openRaw = price + (Random.nextDouble() - 0.5) * 0.15
        val open = (openRaw * 100).roundToInt() / 100.0
        val closeRaw = open + drift
        val close = (closeRaw * 100).roundToInt() / 100.0
        val highRaw = maxOf(open, close) + Random.nextDouble() * vol
        val high = (highRaw * 100).roundToInt() / 100.0
        val lowRaw = minOf(open, close) - Random.nextDouble() * vol
        val low = (lowRaw * 100).roundToInt() / 100.0
        val volume = (100000 + Random.nextDouble() * 250000).toLong()
        val month = (5 + i / 30).coerceIn(1, 12)
        val day = (1 + i % 28).coerceIn(1, 28)
        val date = "2026-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        val changeAmt = close - open
        val changePct = if (open != 0.0) (close - open) / open * 100.0 else 0.0
        data.add(
            KLine(
                date = date,
                open = open,
                close = close,
                high = high,
                low = low,
                volume = volume,
                turnover = volume * close,
                amplitude = 0.0,
                changePercent = changePct,
                changeAmount = changeAmt,
                turnoverRate = Random.nextDouble() * 0.8 + 0.1,
            ),
        )
        price = close
    }
    return data
}

@Preview(name = "Basic")
@Composable
private fun KLineChartBasicPreview() {
    val data = remember { generateKlineData(30) }
    Column(
        modifier = Modifier.padding(16.dp).background(Background).fillMaxWidth(),
    ) {
        KLineChart(
            kLines = data,
            modifier = Modifier.fillMaxWidth().aspectRatio(4 / 3f).clip(RoundedCornerShape(6.dp)).background(Surface),
        )
    }
}

@Preview(name = "With MA Lines")
@Composable
private fun KLineChartWithMAPreview() {
    val data = remember { generateKlineData(50) }
    Column(
        modifier = Modifier.padding(16.dp).background(Background).fillMaxWidth(),
    ) {
        KLineChart(
            kLines = data,
            modifier = Modifier.fillMaxWidth().aspectRatio(4 / 3f).clip(RoundedCornerShape(6.dp)).background(Surface),
        )
    }
}

@Preview(name = "Full")
@Composable
private fun KLineChartFullPreview() {
    val data = remember { generateKlineData(100) }
    Column(
        modifier = Modifier.padding(16.dp).background(Background).fillMaxWidth(),
    ) {
        KLineChart(
            kLines = data,
            modifier = Modifier.fillMaxWidth().aspectRatio(4 / 3f).clip(RoundedCornerShape(6.dp)).background(Surface),
        )
    }
}

@Preview(name = "Tooltip Content")
@Composable
private fun KLineTooltipPreview() {
    val sampleCandle =
        KLine(
            date = "2026-05-19",
            open = 18.48,
            close = 18.45,
            high = 18.85,
            low = 18.42,
            volume = 18317073,
            turnover = 337849997.85,
            amplitude = 0.0,
            changePercent = 0.05,
            changeAmount = 0.01,
            turnoverRate = 0.29,
        )
    Column(
        modifier = Modifier.padding(16.dp).background(Background),
    ) {
        KLineTooltip(candle = sampleCandle, x = 20f, y = 100f, viewportWidth = 400f)
    }
}
