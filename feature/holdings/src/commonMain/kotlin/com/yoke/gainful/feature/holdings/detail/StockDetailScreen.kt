package com.yoke.gainful.feature.holdings.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.common.extensions.formatLocalizedDateTime
import com.yoke.gainful.common.extensions.formatSigned
import com.yoke.gainful.designsystem.components.BackNavigationIcon
import com.yoke.gainful.designsystem.components.GainfulScaffold
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.components.LoadingDots
import com.yoke.gainful.designsystem.components.LoadingSpinner
import com.yoke.gainful.designsystem.components.bottomBarPadding
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.Surface2
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.ChartPeriod
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.GainfulErrorPage
import com.yoke.gainful.ui.KLineChart
import com.yoke.gainful.ui.LineChart
import com.yoke.gainful.ui.gainColor
import com.yoke.gainful.ui.gainDimColor
import com.yoke.gainful.ui.lossColor
import com.yoke.gainful.ui.lossDimColor
import gainful.core.designsystem.generated.resources.ic_check
import gainful.feature.holdings.generated.resources.Res
import gainful.feature.holdings.generated.resources.amplitude
import gainful.feature.holdings.generated.resources.avoided_loss_per_share
import gainful.feature.holdings.generated.resources.back_to_holdings
import gainful.feature.holdings.generated.resources.buy_badge
import gainful.feature.holdings.generated.resources.buy_quantity
import gainful.feature.holdings.generated.resources.cash_dividend
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
import gainful.feature.holdings.generated.resources.current_price
import gainful.feature.holdings.generated.resources.current_price_pnl
import gainful.feature.holdings.generated.resources.dividend_badge
import gainful.feature.holdings.generated.resources.dividend_income
import gainful.feature.holdings.generated.resources.error_desc
import gainful.feature.holdings.generated.resources.error_title
import gainful.feature.holdings.generated.resources.execution_price
import gainful.feature.holdings.generated.resources.high_price
import gainful.feature.holdings.generated.resources.holding_badge
import gainful.feature.holdings.generated.resources.holding_quantity
import gainful.feature.holdings.generated.resources.income_total
import gainful.feature.holdings.generated.resources.loading_hint
import gainful.feature.holdings.generated.resources.loading_quote
import gainful.feature.holdings.generated.resources.low_price
import gainful.feature.holdings.generated.resources.market_value
import gainful.feature.holdings.generated.resources.minutes_suffix
import gainful.feature.holdings.generated.resources.no_data
import gainful.feature.holdings.generated.resources.no_trade_records
import gainful.feature.holdings.generated.resources.open_price
import gainful.feature.holdings.generated.resources.price_difference_per_share
import gainful.feature.holdings.generated.resources.profit_loss
import gainful.feature.holdings.generated.resources.recent_trades
import gainful.feature.holdings.generated.resources.retry
import gainful.feature.holdings.generated.resources.sell_badge
import gainful.feature.holdings.generated.resources.sell_quantity
import gainful.feature.holdings.generated.resources.trade_count
import gainful.feature.holdings.generated.resources.trend
import gainful.feature.holdings.generated.resources.turnover
import gainful.feature.holdings.generated.resources.turnover_rate
import gainful.feature.holdings.generated.resources.volume
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import gainful.core.designsystem.generated.resources.Res as DsRes

@Composable
private fun ChartPeriod.localizedLabel(): String =
    when (this) {
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
        is StockDetailUiState.Loading -> {
            LoadingCenterArea(uiState.pinYin, uiState.name)
        }

        is StockDetailUiState.Error -> {
            GainfulErrorPage(
                title = stringResource(Res.string.error_title),
                description = stringResource(Res.string.error_desc),
                onRetry = onRetry,
                retryLabel = stringResource(Res.string.retry),
                onBack = onBack,
                backLabel = stringResource(Res.string.back_to_holdings),
            )
        }

        is StockDetailUiState.Success -> {
            GainfulScaffold(
                appTopBar = {
                    GainfulTopAppBar(
                        title = uiState.name,
                        subtitle = "${uiState.pinYin} ${uiState.code}",
                        navigationIcon = { BackNavigationIcon(onClick = onBack) },
                    )
                },
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    PriceHeroCard(uiState)
                    ChartCard(uiState.selectedPeriod, uiState.kLines, onPeriodSelected)
                    MetricsGrid(uiState)
                    TradesCard(uiState)

                    Spacer(modifier = Modifier.bottomBarPadding())
                }
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
private fun PriceHeroCard(uiState: StockDetailUiState.Success) {
    val change = uiState.changeAmount
    val changePct = uiState.changePercent
    val changeColor = if (change >= 0) gainColor else lossColor

    Column(
        modifier =
            Modifier
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
    kLines: List<KLine>,
    onPeriodSelected: (ChartPeriod) -> Unit,
) {
    var minutesExpanded by remember { mutableStateOf(false) }
    val isMinuteSelected = selectedPeriod.klt != null && selectedPeriod.klt in 1..60

    Column(
        modifier =
            Modifier
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
                listOf(ChartPeriod.TRENDS, ChartPeriod.TRENDS_5D, ChartPeriod.DAILY, ChartPeriod.WEEKLY, ChartPeriod.MONTHLY).forEach {
                    period ->
                    val isActive = period == selectedPeriod && !isMinuteSelected
                    Box(
                        modifier =
                            Modifier
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
                        modifier =
                            Modifier
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
                                text =
                                    if (isMinuteSelected && !minutesExpanded) {
                                        selectedPeriod.localizedLabel()
                                    } else {
                                        stringResource(
                                            Res.string.minutes_suffix,
                                        )
                                    },
                                fontSize = 10.sp,
                                color = if (isMinuteSelected || minutesExpanded) Gold else TextMuted,
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Canvas(
                                modifier =
                                    Modifier
                                        .size(5.dp)
                                        .rotate(if (minutesExpanded) 180f else 0f),
                            ) {
                                val stroke = Stroke(width = 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                val color = if (isMinuteSelected || minutesExpanded) Gold else TextMuted
                                drawPath(
                                    path =
                                        Path().apply {
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
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Card)
                                .border(1.dp, Border, RoundedCornerShape(10.dp)),
                    ) {
                        listOf(ChartPeriod.MIN_1, ChartPeriod.MIN_5, ChartPeriod.MIN_15, ChartPeriod.MIN_30, ChartPeriod.MIN_60).forEach {
                            period ->
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
                                trailingIcon =
                                    if (selectedPeriod == period) {
                                        {
                                            Icon(
                                                painter = painterResource(DsRes.drawable.ic_check),
                                                contentDescription = null,
                                                modifier = Modifier.size(11.dp),
                                                tint = Gold,
                                            )
                                        }
                                    } else {
                                        null
                                    },
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ChartArea(
            period = selectedPeriod,
            kLines = kLines,
            modifier = Modifier.fillMaxWidth().height(320.dp).clip(RoundedCornerShape(6.dp)).background(Surface),
        )
    }
}

@Composable
private fun ChartArea(period: ChartPeriod, kLines: List<KLine>, modifier: Modifier) {
    if (kLines.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(text = stringResource(Res.string.no_data), fontSize = 14.sp, color = TextMuted)
        }
        return
    }

    if (period.isTrends) {
        val chartData = kLines.mapIndexed { index, kLine -> index.toFloat() to kLine.close.toFloat() }
        LineChart(
            data = chartData,
            modifier = modifier,
            lineColor = gainColor,
            showBaseline = true,
            gradientFill = true,
        )
    } else {
        KLineChart(kLines = kLines, modifier = modifier)
    }
}

@Composable
private fun MetricsGrid(uiState: StockDetailUiState.Success) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MetricItem(stringResource(Res.string.market_value), uiState.totalMarketCap.formatCompact(), Modifier.weight(1f))
        MetricItem(stringResource(Res.string.turnover_rate), "${uiState.turnoverRate.formatLocalized()}%", Modifier.weight(1f))
        MetricItem(stringResource(Res.string.amplitude), "${uiState.amplitude.formatLocalized()}%", Modifier.weight(1f))
        MetricItem(
            stringResource(Res.string.holding_badge),
            stringResource(Res.string.holding_quantity, uiState.quantity.toInt()),
            Modifier.weight(1f),
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

@Composable
private fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary,
) {
    Column(
        modifier =
            modifier
                .widthIn(min = 100.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Card)
                .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted,
            maxLines = 1,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = valueColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun TradesCard(uiState: StockDetailUiState.Success) {
    val trades = uiState.transactions

    Column(
        modifier =
            Modifier
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .width(3.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Gold),
                )
                Text(
                    text = stringResource(Res.string.recent_trades),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
            }
            Text(
                text = stringResource(Res.string.trade_count, trades.size),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Gold,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldDim)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (trades.isEmpty()) {
            Text(
                text = stringResource(Res.string.no_trade_records),
                fontSize = 14.sp,
                color = TextMuted,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                trades.forEach { trade ->
                    TradeItem(trade = trade, currentPrice = uiState.price)
                }
            }
        }
    }
}

@Composable
private fun TradeItem(
    trade: Transaction,
    currentPrice: Double,
) {
    val accentColor =
        when (trade.type) {
            TransactionType.BUY -> gainColor
            TransactionType.SELL -> lossColor
            TransactionType.DIVIDEND -> Gold
        }
    val accentBackground =
        when (trade.type) {
            TransactionType.BUY -> gainDimColor
            TransactionType.SELL -> lossDimColor
            TransactionType.DIVIDEND -> GoldDim
        }
    val badge =
        when (trade.type) {
            TransactionType.BUY -> stringResource(Res.string.buy_badge)
            TransactionType.SELL -> stringResource(Res.string.sell_badge)
            TransactionType.DIVIDEND -> stringResource(Res.string.dividend_badge)
        }
    val title =
        when (trade.type) {
            TransactionType.BUY -> stringResource(Res.string.buy_quantity, trade.quantity.toInt())
            TransactionType.SELL -> stringResource(Res.string.sell_quantity, trade.quantity.toInt())
            TransactionType.DIVIDEND -> stringResource(Res.string.cash_dividend)
        }
    val isDividend = trade.type == TransactionType.DIVIDEND
    val pnl =
        when (trade.type) {
            TransactionType.BUY -> (currentPrice - trade.price) * trade.quantity - trade.fee
            TransactionType.SELL -> (trade.price - currentPrice) * trade.quantity - trade.fee
            TransactionType.DIVIDEND -> trade.amount
        }
    val pnlColor =
        when {
            isDividend -> Gold
            pnl >= 0 -> gainColor
            else -> lossColor
        }
    val priceDifference =
        when (trade.type) {
            TransactionType.BUY -> currentPrice - trade.price
            TransactionType.SELL -> trade.price - currentPrice
            TransactionType.DIVIDEND -> 0.0
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(13.dp))
                .background(Surface2)
                .border(1.dp, TextMuted.copy(alpha = 0.55f), RoundedCornerShape(13.dp)),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .padding(top = 13.dp, bottom = 12.dp)
                    .width(3.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(accentColor),
        )
        Column(modifier = Modifier.padding(start = 16.dp, top = 13.dp, end = 13.dp, bottom = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(35.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(accentBackground),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = badge,
                            color = accentColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = trade.tradeDate.formatLocalizedDateTime(),
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text =
                            stringResource(
                                if (isDividend) Res.string.dividend_income else Res.string.current_price_pnl,
                            ),
                        color = TextMuted,
                        fontSize = 9.sp,
                    )
                    Text(
                        text = pnl.formatSigned(),
                        color = pnlColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(TextSecondary.copy(alpha = 0.12f)))
            Spacer(modifier = Modifier.height(10.dp))

            if (isDividend) {
                TradeDatum(
                    label = stringResource(Res.string.income_total),
                    value = trade.amount.formatSigned(),
                    valueColor = Gold,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TradeDatum(
                        label = stringResource(Res.string.execution_price),
                        value = "¥${trade.price.formatLocalized()}",
                        modifier = Modifier.weight(1f),
                    )
                    TradeDatumDivider()
                    TradeDatum(
                        label = stringResource(Res.string.current_price),
                        value = "¥${currentPrice.formatLocalized()}",
                        modifier = Modifier.weight(1f).padding(start = 9.dp),
                    )
                    TradeDatumDivider()
                    TradeDatum(
                        label =
                            stringResource(
                                if (trade.type == TransactionType.SELL) {
                                    Res.string.avoided_loss_per_share
                                } else {
                                    Res.string.price_difference_per_share
                                },
                            ),
                        value = priceDifference.formatSigned(),
                        valueColor = if (priceDifference >= 0) gainColor else lossColor,
                        modifier = Modifier.weight(1.05f).padding(start = 9.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TradeDatum(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextSecondary,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, color = TextMuted, fontSize = 9.sp, maxLines = 1)
        Text(
            text = value,
            color = valueColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
        )
    }
}

@Composable
private fun TradeDatumDivider() {
    Box(
        modifier =
            Modifier
                .width(1.dp)
                .height(28.dp)
                .background(TextSecondary.copy(alpha = 0.1f)),
    )
}
