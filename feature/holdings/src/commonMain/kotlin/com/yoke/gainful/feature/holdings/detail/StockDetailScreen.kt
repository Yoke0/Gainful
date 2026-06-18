package com.yoke.gainful.feature.holdings.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatCompact
import com.yoke.gainful.common.extensions.formatDecimal
import com.yoke.gainful.common.extensions.formatTurnover
import com.yoke.gainful.model.ChartPeriod
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun StockDetailScreen(
    viewModel: StockDetailViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPeriod by remember { mutableStateOf(ChartPeriod.DAILY) }

    val change = uiState.changeAmount
    val changePct = uiState.changePercent
    val isPositive = change >= 0
    val changeColor = if (isPositive) GainGreen else GainRed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(50))
                .background(Card)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\u2039",
                fontSize = 20.sp,
                color = TextSecondary,
            )
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = uiState.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = uiState.code,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
                modifier = Modifier.alignByBaseline(),
            )
        }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PriceHeroCard(uiState, change, changePct, changeColor)

        Spacer(modifier = Modifier.height(14.dp))

        ChartCard(selectedPeriod, uiState.kLines.map { it.close }) { selectedPeriod = it; viewModel.loadChart(selectedPeriod) }

        Spacer(modifier = Modifier.height(14.dp))

        MetricsGrid(uiState)

        Spacer(modifier = Modifier.height(14.dp))

        TradesCard(uiState)

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun PriceHeroCard(
    uiState: StockDetailUiState,
    change: Double,
    changePct: Double,
    changeColor: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        if (uiState.industry.isNotEmpty()) {
            Text(
                text = uiState.industry,
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Surface)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = uiState.price.formatDecimal(2),
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = "${if (change >= 0) "+" else ""}${change.formatDecimal(2)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = changeColor,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = "${if (changePct >= 0) "+" else ""}${changePct.formatDecimal(2)}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = changeColor,
                modifier = Modifier.alignByBaseline(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ExtraItem("开盘", uiState.open.formatDecimal(2))
            ExtraItem("最高", uiState.high.formatDecimal(2))
            ExtraItem("最低", uiState.low.formatDecimal(2))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ExtraItem("成交量", formatVolume(uiState.volume))
            ExtraItem("成交额", uiState.turnover.formatTurnover())
        }
    }
}

@Composable
private fun ExtraItem(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            fontSize = 11.sp,
            color = TextMuted,
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = TextSecondary,
        )
    }
}

@Composable
private fun ChartCard(
    selectedPeriod: ChartPeriod,
    chartData: List<Double>,
    onPeriodSelected: (ChartPeriod) -> Unit,
) {
    var minutesExpanded by remember { mutableStateOf(false) }
    val isMinuteSelected = selectedPeriod.klt != null && selectedPeriod.klt in 1..60

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "走势",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f, fill = false),
            ) {
                listOf(ChartPeriod.TRENDS, ChartPeriod.TRENDS_5D, ChartPeriod.DAILY, ChartPeriod.WEEKLY, ChartPeriod.MONTHLY).forEach { period ->
                    val isActive = period == selectedPeriod && !isMinuteSelected
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = if (isActive) Gold else Border,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .background(if (isActive) GoldDim else Surface)
                            .clickable {
                                minutesExpanded = false
                                onPeriodSelected(period)
                            }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = period.label,
                            fontSize = 10.sp,
                            color = if (isActive) Gold else TextMuted,
                        )
                    }
                }
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = if (isMinuteSelected || minutesExpanded) Gold else Border,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .background(if (isMinuteSelected || minutesExpanded) GoldDim else Surface)
                            .clickable { minutesExpanded = !minutesExpanded }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isMinuteSelected && !minutesExpanded) selectedPeriod.label else "分钟",
                                fontSize = 10.sp,
                                color = if (isMinuteSelected || minutesExpanded) Gold else TextMuted,
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Canvas(
                                modifier = Modifier
                                    .size(5.dp)
                                    .rotate(if (minutesExpanded) 180f else 0f),
                            ) {
                                val stroke = Stroke(width = 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                val color = if (isMinuteSelected || minutesExpanded) Gold else TextMuted
                                drawPath(
                                    path = Path().apply {
                                        moveTo(0f, 0f)
                                        lineTo(size.width / 2, size.height)
                                        lineTo(size.width, 0f)
                                    },
                                    color = color,
                                    style = stroke,
                                )
                            }
                        }
                    }
                    DropdownMenu(
                        expanded = minutesExpanded,
                        onDismissRequest = { minutesExpanded = false },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Card)
                            .border(1.dp, Border, RoundedCornerShape(10.dp)),
                    ) {
                        listOf(ChartPeriod.MIN_1, ChartPeriod.MIN_5, ChartPeriod.MIN_15, ChartPeriod.MIN_30, ChartPeriod.MIN_60).forEach { period ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = period.label,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (selectedPeriod == period) Gold else TextSecondary,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                },
                                onClick = {
                                    minutesExpanded = false
                                    onPeriodSelected(period)
                                },
                                trailingIcon = if (selectedPeriod == period) {
                                    {
                                        Text(
                                            text = "\u2713",
                                            fontSize = 11.sp,
                                            color = Gold,
                                        )
                                    }
                                } else null,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (chartData.isNotEmpty()) {
            ChartArea(chartData)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Surface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "暂无数据",
                    fontSize = 14.sp,
                    color = TextMuted,
                )
            }
        }
    }
}

@Composable
private fun ChartArea(data: List<Double>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Surface),
    ) {
        val w = size.width
        val h = size.height
        val padTop = 10f
        val padBottom = 10f
        val chartH = h - padTop - padBottom

        val minVal = data.min()
        val maxVal = data.max()
        val range = maxVal - minVal

        val linePath = Path()
        val fillPath = Path()
        val stepX = w / (data.size - 1).coerceAtLeast(1).toFloat()

        data.forEachIndexed { index, value ->
            val x = index * stepX
            val normalized = if (range > 0) (value - minVal) / range else 0.5
            val y = padTop + chartH * (1f - normalized.toFloat())
            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(w, h)
        fillPath.close()

        val lineColor = GainGreen
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.3f),
                    lineColor.copy(alpha = 0.02f),
                ),
                startY = 0f,
                endY = h,
            ),
        )
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        val lastX = (data.size - 1) * stepX
        val lastNormalized = if (range > 0) (data.last() - minVal) / range else 0.5
        val lastY = padTop + chartH * (1f - lastNormalized.toFloat())
        drawCircle(color = lineColor, radius = 3.5f, center = Offset(lastX, lastY))
        drawCircle(color = Surface, radius = 1.5f, center = Offset(lastX, lastY))
    }
}

@Composable
private fun MetricsGrid(uiState: StockDetailUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricItem("市值", uiState.totalMarketCap.formatCompact(), Modifier.weight(1f))
            MetricItem("市盈率", if (uiState.peDynamic > 0) uiState.peDynamic.formatDecimal(1) else "\u2014", Modifier.weight(1f))
            MetricItem("市净率", if (uiState.pb > 0) uiState.pb.formatDecimal(1) else "\u2014", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricItem("换手率", "${uiState.turnoverRate.formatDecimal(2)}%", Modifier.weight(1f))
            MetricItem("振幅", "${uiState.amplitude.formatDecimal(2)}%", Modifier.weight(1f))
            MetricItem("行业", uiState.industry.ifEmpty { "\u2014" }, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricItem("持仓", "${uiState.quantity.toInt()} 股", Modifier.weight(1f))
            MetricItem(
                "成本",
                uiState.averageCost.formatDecimal(2),
                Modifier.weight(1f),
                valueColor = if (uiState.averageCost > uiState.price) GainRed else GainGreen,
            )
            MetricItem(
                "盈亏",
                "${if (uiState.totalGain >= 0) "+" else ""}${uiState.totalGain.formatCompact()}",
                Modifier.weight(1f),
                valueColor = if (uiState.totalGain >= 0) GainGreen else GainRed,
            )
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Card)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = valueColor,
        )
    }
}

@Composable
private fun TradesCard(uiState: StockDetailUiState) {
    val trades = uiState.transactions

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "最近交易",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Text(
                text = "${trades.size} 笔",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Gold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldDim)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (trades.isEmpty()) {
            Text(
                text = "暂无交易记录",
                fontSize = 14.sp,
                color = TextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        } else {
            trades.forEach { trade ->
                TradeRow(trade)
                if (trade != trades.last()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Border.copy(alpha = 0.06f)),
                    )
                }
            }
        }
    }
}

@Composable
private fun TradeRow(trade: com.yoke.gainful.model.Transaction) {
    val typeLabel = when (trade.type) {
        TransactionType.BUY -> "买入"
        TransactionType.SELL -> "卖出"
        TransactionType.DIVIDEND -> "股息"
    }
    val typeColor = when (trade.type) {
        TransactionType.BUY -> GainGreen
        TransactionType.SELL -> GainRed
        TransactionType.DIVIDEND -> Gold
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = typeLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = typeColor,
            )
            Text(
                text = Instant.fromEpochMilliseconds(trade.timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                    .toString(),
                fontSize = 11.sp,
                color = TextMuted,
            )
        }
        if (trade.type == TransactionType.DIVIDEND) {
            Text(
                text = "+${trade.amount.formatDecimal(2)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = Gold,
            )
        } else {
            Text(
                text = "${trade.quantity.toInt()} 股 @ ${trade.price.formatDecimal(2)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary,
            )
        }
    }
}

private fun formatVolume(volume: Long): String {
    return when {
        volume >= 1_000_000_000 -> "%.1fB".format(volume / 1_000_000_000.0)
        volume >= 1_000_000 -> "%.1fM".format(volume / 1_000_000.0)
        volume >= 1_000 -> "%.1fK".format(volume / 1_000.0)
        else -> volume.toString()
    }
}
