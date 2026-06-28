package com.yoke.gainful.feature.holdings.detail

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatCompact
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.common.extensions.formatLocalizedDate
import com.yoke.gainful.common.extensions.formatSigned
import com.yoke.gainful.designsystem.components.BackNavigationIcon
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.components.LoadingDots
import com.yoke.gainful.designsystem.components.LoadingSpinner
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.ChartPeriod
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.GainfulScaffold
import com.yoke.gainful.ui.gainColor
import com.yoke.gainful.ui.lossColor
import gainful.feature.holdings.generated.resources.Res
import gainful.feature.holdings.generated.resources.amplitude
import gainful.feature.holdings.generated.resources.back_to_holdings
import gainful.feature.holdings.generated.resources.buy
import gainful.feature.holdings.generated.resources.chart_daily
import gainful.feature.holdings.generated.resources.chart_min_1
import gainful.feature.holdings.generated.resources.chart_min_15
import gainful.feature.holdings.generated.resources.chart_min_30
import gainful.feature.holdings.generated.resources.chart_min_5
import gainful.feature.holdings.generated.resources.chart_min_60
import gainful.feature.holdings.generated.resources.chart_monthly
import gainful.feature.holdings.generated.resources.chart_trends
import gainful.feature.holdings.generated.resources.chart_trends_5d
import gainful.feature.holdings.generated.resources.chart_weekly
import gainful.feature.holdings.generated.resources.cost
import gainful.feature.holdings.generated.resources.dividend
import gainful.feature.holdings.generated.resources.error_desc
import gainful.feature.holdings.generated.resources.error_title
import gainful.feature.holdings.generated.resources.high_price
import gainful.feature.holdings.generated.resources.holding_badge
import gainful.feature.holdings.generated.resources.holding_quantity
import gainful.feature.holdings.generated.resources.loading_hint
import gainful.feature.holdings.generated.resources.loading_quote
import gainful.feature.holdings.generated.resources.low_price
import gainful.feature.holdings.generated.resources.market_value
import gainful.feature.holdings.generated.resources.minutes_suffix
import gainful.feature.holdings.generated.resources.no_data
import gainful.feature.holdings.generated.resources.no_trade_records
import gainful.feature.holdings.generated.resources.open_price
import gainful.feature.holdings.generated.resources.profit_loss
import gainful.feature.holdings.generated.resources.recent_trades
import gainful.feature.holdings.generated.resources.retry
import gainful.feature.holdings.generated.resources.sell
import gainful.feature.holdings.generated.resources.trade_count
import gainful.feature.holdings.generated.resources.trade_detail
import gainful.feature.holdings.generated.resources.trend
import gainful.feature.holdings.generated.resources.turnover
import gainful.feature.holdings.generated.resources.turnover_rate
import gainful.feature.holdings.generated.resources.volume
import org.jetbrains.compose.resources.stringResource

@Composable
private fun ChartPeriod.localizedLabel(): String = when (this) {
    ChartPeriod.TRENDS -> stringResource(Res.string.chart_trends)
    ChartPeriod.TRENDS_5D -> stringResource(Res.string.chart_trends_5d)
    ChartPeriod.DAILY -> stringResource(Res.string.chart_daily)
    ChartPeriod.WEEKLY -> stringResource(Res.string.chart_weekly)
    ChartPeriod.MONTHLY -> stringResource(Res.string.chart_monthly)
    ChartPeriod.MIN_1 -> stringResource(Res.string.chart_min_1)
    ChartPeriod.MIN_5 -> stringResource(Res.string.chart_min_5)
    ChartPeriod.MIN_15 -> stringResource(Res.string.chart_min_15)
    ChartPeriod.MIN_30 -> stringResource(Res.string.chart_min_30)
    ChartPeriod.MIN_60 -> stringResource(Res.string.chart_min_60)
}

@Composable
fun StockDetailScreen(
    viewModel: StockDetailViewModel,
    onBack: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value

    StockDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onPeriodSelected = { viewModel.onIntent(StockDetailIntent.SelectPeriod(it)) },
        onRetry = { viewModel.onIntent(StockDetailIntent.Retry) },
    )
}

@Composable
private fun StockDetailScreen(
    uiState: StockDetailUiState,
    onBack: () -> Unit,
    onPeriodSelected: (ChartPeriod) -> Unit,
    onRetry: () -> Unit,
) {
    when (uiState) {
        is StockDetailUiState.Loading -> LoadingCenterArea(uiState.pinYin, uiState.name)
        is StockDetailUiState.Error -> ErrorCenterArea(
            pinYin = uiState.pinYin,
            name = uiState.name,
            onRetry = onRetry,
            onBack = onBack,
        )
        is StockDetailUiState.Success -> GainfulScaffold(
            appTopBar = {
                GainfulTopAppBar(
                    title = uiState.name,
                    subtitle = "${uiState.pinYin} ${uiState.code}",
                    navigationIcon = { BackNavigationIcon(onClick = onBack) },
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                PriceHeroCard(uiState)
                ChartCard(uiState.selectedPeriod, uiState.kLines.map { it.close }, onPeriodSelected)
                MetricsGrid(uiState)
                TradesCard(uiState)
            }
        }
    }
}

@Composable
private fun LoadingCenterArea(pinYin: String, name: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = pinYin,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary,
                    letterSpacing = 0.4.sp,
                )
                Text(
                    text = name,
                    fontSize = 13.sp,
                    color = TextMuted,
                )
            }

            LoadingSpinner()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(Res.string.loading_quote),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                )
                LoadingDots()
                Text(
                    text = stringResource(Res.string.loading_hint),
                    fontSize = 11.sp,
                    color = TextMuted,
                )
            }
        }
    }
}

@Composable
private fun ErrorCenterArea(
    pinYin: String,
    name: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = pinYin,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary,
                    letterSpacing = 0.4.sp,
                )
                Text(
                    text = name,
                    fontSize = 13.sp,
                    color = TextMuted,
                )
            }

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(GainRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(40.dp)) {
                    val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    drawCircle(color = GainRed, style = stroke)
                    drawLine(
                        color = GainRed,
                        start = Offset(size.width / 2, size.height * 0.33f),
                        end = Offset(size.width / 2, size.height * 0.58f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawCircle(
                        color = GainRed,
                        radius = 1.2.dp.toPx(),
                        center = Offset(size.width / 2, size.height * 0.73f),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.error_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = stringResource(Res.string.error_desc),
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.widthIn(max = 260.dp),
                )
            }

            Text(
                text = "ERR_TIMEOUT · NET_ERR",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
                letterSpacing = 0.3.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Surface)
                    .border(1.dp, Border, RoundedCornerShape(9999.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            )

            Column(
                modifier = Modifier.widthIn(max = 240.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Gold)
                        .clickable(onClick = onRetry)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.retry),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Background,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9999.dp))
                        .border(1.dp, Border, RoundedCornerShape(9999.dp))
                        .clickable(onClick = onBack)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.back_to_holdings),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceHeroCard(uiState: StockDetailUiState.Success) {
    val change = uiState.changeAmount
    val changePct = uiState.changePercent
    val changeColor = if (change >= 0) gainColor else lossColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = uiState.price.formatLocalized(),
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = change.formatSigned(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = changeColor,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = changePct.formatSigned() + "%",
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
            ExtraItem(stringResource(Res.string.open_price), uiState.open.formatLocalized())
            ExtraItem(stringResource(Res.string.high_price), uiState.high.formatLocalized())
            ExtraItem(stringResource(Res.string.low_price), uiState.low.formatLocalized())
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ExtraItem(stringResource(Res.string.volume), uiState.volume.toDouble().formatCompact())
            ExtraItem(stringResource(Res.string.turnover), uiState.turnover.formatCompact())
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
                text = stringResource(Res.string.trend),
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
                            text = period.localizedLabel(),
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
                                text = if (isMinuteSelected && !minutesExpanded) selectedPeriod.localizedLabel() else stringResource(Res.string.minutes_suffix),
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
                                        text = period.localizedLabel(),
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
                    text = stringResource(Res.string.no_data),
                    fontSize = 14.sp,
                    color = TextMuted,
                )
            }
        }
    }
}

@Composable
private fun ChartArea(data: List<Double>) {
    val lineColor = gainColor
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
private fun MetricsGrid(uiState: StockDetailUiState.Success) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricItem(stringResource(Res.string.market_value), uiState.totalMarketCap.formatCompact(), Modifier.weight(1f))
            MetricItem(stringResource(Res.string.turnover_rate), "${uiState.turnoverRate.formatLocalized()}%", Modifier.weight(1f))
            MetricItem(stringResource(Res.string.amplitude), "${uiState.amplitude.formatLocalized()}%", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricItem(
                stringResource(Res.string.holding_badge),
                stringResource(Res.string.holding_quantity, uiState.quantity.toInt()),
                Modifier.weight(1f)
            )
            MetricItem(
                stringResource(Res.string.cost),
                uiState.averageCost.formatLocalized(),
                Modifier.weight(1f),
                valueColor = if (uiState.averageCost > uiState.price) lossColor else gainColor,
            )
            MetricItem(
                stringResource(Res.string.profit_loss),
                uiState.totalGain.formatSigned(),
                Modifier.weight(1f),
                valueColor = if (uiState.totalGain >= 0) gainColor else lossColor,
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
private fun TradesCard(uiState: StockDetailUiState.Success) {
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
                text = stringResource(Res.string.recent_trades),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Text(
                text = stringResource(Res.string.trade_count, trades.size),
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
                text = stringResource(Res.string.no_trade_records),
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
        TransactionType.BUY -> stringResource(Res.string.buy)
        TransactionType.SELL -> stringResource(Res.string.sell)
        TransactionType.DIVIDEND -> stringResource(Res.string.dividend)
    }
    val typeColor = when (trade.type) {
        TransactionType.BUY -> gainColor
        TransactionType.SELL -> lossColor
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
                text = trade.tradeDate.formatLocalizedDate(),
                fontSize = 11.sp,
                color = TextMuted,
            )
        }
        if (trade.type == TransactionType.DIVIDEND) {
            Text(
                text = "+${trade.amount.formatLocalized()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = Gold,
            )
        } else {
            Text(
                text = stringResource(Res.string.trade_detail, trade.quantity.toInt(), trade.price.formatLocalized()),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary,
            )
        }
    }
}
