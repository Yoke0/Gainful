package com.yoke.gainful.feature.dashboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.common.extensions.formatSigned
import com.yoke.gainful.designsystem.components.GainfulScaffold
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.components.bottomBarPadding
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.GainGreen
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.PnlCell
import com.yoke.gainful.model.PnlPeriod
import com.yoke.gainful.model.PnlPeriodType
import com.yoke.gainful.model.StockPnlDetail
import com.yoke.gainful.ui.AutoSizeText
import com.yoke.gainful.ui.LineChart
import com.yoke.gainful.ui.gainColor
import com.yoke.gainful.ui.gainDimColor
import com.yoke.gainful.ui.lossColor
import com.yoke.gainful.ui.lossDimColor
import gainful.core.designsystem.generated.resources.ic_chevron_left
import gainful.core.designsystem.generated.resources.ic_chevron_right
import gainful.feature.dashboard.generated.resources.Res
import gainful.feature.dashboard.generated.resources.daily_pnl
import gainful.feature.dashboard.generated.resources.dashboard_title
import gainful.feature.dashboard.generated.resources.holdings_count
import gainful.feature.dashboard.generated.resources.holdings_overflow
import gainful.feature.dashboard.generated.resources.holdings_overview
import gainful.feature.dashboard.generated.resources.holdings_quantity_label
import gainful.feature.dashboard.generated.resources.holdings_trend
import gainful.feature.dashboard.generated.resources.key_metrics
import gainful.feature.dashboard.generated.resources.live_badge
import gainful.feature.dashboard.generated.resources.no_transactions
import gainful.feature.dashboard.generated.resources.no_trend_data
import gainful.feature.dashboard.generated.resources.pnl_day_label
import gainful.feature.dashboard.generated.resources.pnl_detail_buy_fee
import gainful.feature.dashboard.generated.resources.pnl_detail_buy_fee_total
import gainful.feature.dashboard.generated.resources.pnl_detail_daily_gain
import gainful.feature.dashboard.generated.resources.pnl_detail_daily_gain_total
import gainful.feature.dashboard.generated.resources.pnl_detail_dividend
import gainful.feature.dashboard.generated.resources.pnl_detail_dividend_total
import gainful.feature.dashboard.generated.resources.pnl_detail_no_records
import gainful.feature.dashboard.generated.resources.pnl_detail_sell_fee
import gainful.feature.dashboard.generated.resources.pnl_detail_sell_fee_total
import gainful.feature.dashboard.generated.resources.pnl_detail_total_pnl
import gainful.feature.dashboard.generated.resources.pnl_month_label
import gainful.feature.dashboard.generated.resources.pnl_overview
import gainful.feature.dashboard.generated.resources.pnl_period_day
import gainful.feature.dashboard.generated.resources.pnl_period_month
import gainful.feature.dashboard.generated.resources.pnl_period_week
import gainful.feature.dashboard.generated.resources.pnl_period_year
import gainful.feature.dashboard.generated.resources.pnl_week_cell_label
import gainful.feature.dashboard.generated.resources.pnl_year_cell_first_label
import gainful.feature.dashboard.generated.resources.pnl_year_cell_label
import gainful.feature.dashboard.generated.resources.pnl_year_label
import gainful.feature.dashboard.generated.resources.pnl_year_month_label
import gainful.feature.dashboard.generated.resources.profit_rate
import gainful.feature.dashboard.generated.resources.today_badge
import gainful.feature.dashboard.generated.resources.total_assets
import gainful.feature.dashboard.generated.resources.total_cost
import gainful.feature.dashboard.generated.resources.total_cost_label
import gainful.feature.dashboard.generated.resources.total_market_value_label
import gainful.feature.dashboard.generated.resources.total_pnl
import gainful.feature.dashboard.generated.resources.total_pnl_label
import gainful.feature.dashboard.generated.resources.total_profit
import gainful.feature.dashboard.generated.resources.weekday_fri
import gainful.feature.dashboard.generated.resources.weekday_mon
import gainful.feature.dashboard.generated.resources.weekday_sat
import gainful.feature.dashboard.generated.resources.weekday_sun
import gainful.feature.dashboard.generated.resources.weekday_thu
import gainful.feature.dashboard.generated.resources.weekday_tue
import gainful.feature.dashboard.generated.resources.weekday_wed
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import gainful.core.designsystem.generated.resources.Res as DesignRes

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardScreen(uiState = uiState, onIntent = viewModel::onIntent)
}

@Composable
private fun DashboardScreen(
    uiState: DashboardUiState,
    onIntent: (DashboardIntent) -> Unit = {},
) {
    GainfulScaffold(
        appTopBar = {
            GainfulTopAppBar(
                title = stringResource(Res.string.dashboard_title),
                actions = {
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Card)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(GainGreen),
                            )
                            Text(
                                text = stringResource(Res.string.live_badge),
                                fontSize = 12.sp,
                                color = TextSecondary,
                            )
                        }
                    }
                },
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
            DailyPnlCard(uiState.holdings)

            SummaryCard(uiState)

            ChartCard(uiState.holdings)

            MetricsSection(uiState)

            PnlOverviewCard(
                uiState = uiState,
                onIntent = onIntent,
            )

            HoldingsOverviewCard(holdings = uiState.holdings)

            Spacer(modifier = Modifier.bottomBarPadding())
        }
    }

    if (uiState.selectedPnlDate != null) {
        StockPnlDetailDialog(
            details = uiState.stockPnlDetails,
            onDismiss = { onIntent(DashboardIntent.DismissPnlDetail) },
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .padding(20.dp),
    ) {
        if (trailing != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                trailing()
            }
        } else {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun PnlOverviewCard(
    uiState: DashboardUiState,
    onIntent: (DashboardIntent) -> Unit,
) {
    val pnlData = uiState.pnlData
    val period = pnlData?.period

    SectionCard(title = stringResource(Res.string.pnl_overview)) {
        PnlPeriodTabs(
            selectedPeriod = uiState.selectedPnlPeriod,
            onPeriodSelected = { period ->
                onIntent(DashboardIntent.SelectPnlPeriod(period))
            },
        )

        Spacer(modifier = Modifier.height(12.dp))
        if (uiState.selectedPnlPeriod == PnlPeriodType.YEAR) {
            PnlPeriodNavigation(
                periodLabel = "${uiState.firstTransactionYear}.${uiState.firstTransactionMonth.toString().padStart(2, '0')}至今",
                showNavigation = false,
                onNavigate = { },
            )
        } else {
            val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val isCurrentPeriod =
                when (uiState.selectedPnlPeriod) {
                    PnlPeriodType.DAY, PnlPeriodType.WEEK -> {
                        uiState.pnlYear == today.year && uiState.pnlMonth == today.month.ordinal + 1
                    }

                    PnlPeriodType.MONTH -> {
                        uiState.pnlYear == today.year
                    }
                }
            PnlPeriodNavigation(
                periodLabel = periodNavLabel(period, uiState.selectedPnlPeriod),
                showNavigation = true,
                showNext = !isCurrentPeriod,
                onNavigate = { direction ->
                    onIntent(DashboardIntent.NavigatePnlPeriod(direction))
                },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.holdings.isEmpty() && allTransactionsEmpty(uiState)) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.no_transactions),
                    fontSize = 14.sp,
                    color = TextMuted,
                )
            }
        } else if (pnlData != null) {
            val columns =
                when (uiState.selectedPnlPeriod) {
                    PnlPeriodType.DAY -> 7
                    PnlPeriodType.WEEK -> 3
                    PnlPeriodType.MONTH -> 3
                    PnlPeriodType.YEAR -> 3
                }

            if (uiState.selectedPnlPeriod == PnlPeriodType.DAY) {
                WeekdayHeader()
                Spacer(modifier = Modifier.height(6.dp))
            }

            PnlGrid(
                cells = pnlData.cells,
                columns = columns,
                periodType = uiState.selectedPnlPeriod,
                firstYear = uiState.firstTransactionYear,
                firstMonth = uiState.firstTransactionMonth,
                onCellClick = { year, month, day ->
                    onIntent(DashboardIntent.SelectPnlCell(year, month, day))
                },
            )
        }
    }
}

@Composable
private fun periodNavLabel(period: PnlPeriod?, type: PnlPeriodType): String {
    if (period == null) return ""
    return when (type) {
        PnlPeriodType.WEEK -> {
            stringResource(Res.string.pnl_year_month_label, period.year, period.month)
        }

        PnlPeriodType.DAY -> {
            stringResource(Res.string.pnl_year_month_label, period.year, period.month)
        }

        PnlPeriodType.MONTH -> {
            stringResource(Res.string.pnl_year_label, period.year)
        }

        PnlPeriodType.YEAR -> {
            ""
        }
    }
}

@Composable
private fun PnlPeriodTabs(
    selectedPeriod: PnlPeriodType,
    onPeriodSelected: (PnlPeriodType) -> Unit,
) {
    val periods =
        listOf(
            PnlPeriodType.DAY to stringResource(Res.string.pnl_period_day),
            PnlPeriodType.WEEK to stringResource(Res.string.pnl_period_week),
            PnlPeriodType.MONTH to stringResource(Res.string.pnl_period_month),
            PnlPeriodType.YEAR to stringResource(Res.string.pnl_period_year),
        )
    val selectedIndex = periods.indexOfFirst { it.first == selectedPeriod }

    var rowSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val tabWidthDp = if (rowSize.width > 0) with(density) { (rowSize.width / periods.size).toDp() } else 0.dp
    val animatedOffset by animateDpAsState(
        targetValue = tabWidthDp * selectedIndex,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(9999.dp))
                .background(Surface)
                .padding(3.dp),
    ) {
        if (rowSize.width > 0) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .offset(x = animatedOffset)
                        .width(tabWidthDp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Gold),
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { rowSize = it.size },
        ) {
            periods.forEach { (period, label) ->
                val isSelected = period == selectedPeriod
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Background else TextMuted,
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable { onPeriodSelected(period) }
                            .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun PnlPeriodNavigation(
    periodLabel: String,
    showNavigation: Boolean = true,
    showNext: Boolean = true,
    onNavigate: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(DesignRes.drawable.ic_chevron_left),
            contentDescription = null,
            modifier =
                Modifier
                    .size(20.dp)
                    .then(if (showNavigation) Modifier.clickable { onNavigate(-1) } else Modifier)
                    .then(if (!showNavigation) Modifier.alpha(0f) else Modifier),
            tint = TextMuted,
        )
        Text(
            text = periodLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Icon(
            painter = painterResource(DesignRes.drawable.ic_chevron_right),
            contentDescription = null,
            modifier =
                Modifier
                    .size(20.dp)
                    .then(if (showNavigation && showNext) Modifier.clickable { onNavigate(1) } else Modifier)
                    .then(if (!showNavigation || !showNext) Modifier.alpha(0f) else Modifier),
            tint = TextMuted,
        )
    }
}

@Composable
private fun WeekdayHeader() {
    val weekdays =
        listOf(
            stringResource(Res.string.weekday_sun),
            stringResource(Res.string.weekday_mon),
            stringResource(Res.string.weekday_tue),
            stringResource(Res.string.weekday_wed),
            stringResource(Res.string.weekday_thu),
            stringResource(Res.string.weekday_fri),
            stringResource(Res.string.weekday_sat),
        )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        weekdays.forEach { day ->
            Text(
                text = day,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PnlGrid(
    cells: List<PnlCell>,
    columns: Int,
    periodType: PnlPeriodType,
    firstYear: Int = 2023,
    firstMonth: Int = 8,
    onCellClick: (year: Int, month: Int, day: Int) -> Unit = { _, _, _ -> },
) {
    val rows = cells.chunked(columns)

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { cell ->
                    if (cell.isPadding) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        PnlCellItem(
                            cell = cell,
                            periodType = periodType,
                            firstYear = firstYear,
                            firstMonth = firstMonth,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (!cell.isFuture && !cell.isPadding && cell.day > 0) {
                                    onCellClick(cell.year, cell.month, cell.day)
                                }
                            },
                        )
                    }
                }
                repeat(columns - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PnlCellItem(
    cell: PnlCell,
    periodType: PnlPeriodType,
    firstYear: Int = 2023,
    firstMonth: Int = 8,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val label = cellLabel(cell, periodType, firstYear, firstMonth)

    if (cell.isFuture) {
        Column(
            modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-2).dp, Alignment.CenterVertically),
        ) {
            AutoSizeText(
                text = label,
                maxFontSize = 10.sp,
                minFontSize = 7.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium,
            )
            AutoSizeText(
                text = "",
                maxFontSize = 11.sp,
                minFontSize = 6.sp,
                color = Color.Transparent,
            )
        }
        return
    }

    val backgroundColor =
        when {
            cell.isPadding -> Color.Transparent
            cell.isEmpty -> Surface
            (cell.value ?: 0.0) >= 0 -> gainDimColor
            else -> lossDimColor
        }

    val valueColor =
        when {
            cell.isPadding || cell.isEmpty -> TextMuted
            (cell.value ?: 0.0) >= 0 -> gainColor
            else -> lossColor
        }

    val borderColor = if (cell.isCurrent) Gold else Color.Transparent

    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(6.dp))
                .background(backgroundColor)
                .then(
                    Modifier.drawBehind {
                        if (cell.isCurrent) {
                            drawRoundRect(
                                color = borderColor,
                                style = Stroke(width = 2.dp.toPx()),
                                cornerRadius = CornerRadius(6.dp.toPx()),
                            )
                        }
                    },
                )
                .clickable { onClick() }
                .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-2).dp, Alignment.CenterVertically),
    ) {
        AutoSizeText(
            text = label,
            maxFontSize = 10.sp,
            minFontSize = 7.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium,
        )
        AutoSizeText(
            text =
                if (cell.isPadding) {
                    ""
                } else if (cell.isEmpty) {
                    "0.00"
                } else {
                    cell.value?.formatSigned(decimals = 2) ?: "0.00"
                },
            maxFontSize = 11.sp,
            minFontSize = 6.sp,
            color = valueColor,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun cellLabel(cell: PnlCell, periodType: PnlPeriodType, firstYear: Int = 2023, firstMonth: Int = 8): String {
    return when (periodType) {
        PnlPeriodType.DAY -> {
            stringResource(Res.string.pnl_day_label, cell.day)
        }

        PnlPeriodType.WEEK -> {
            stringResource(Res.string.pnl_week_cell_label, cell.week, cell.weekStartDay, cell.weekEndDay)
        }

        PnlPeriodType.MONTH -> {
            stringResource(Res.string.pnl_month_label, cell.month)
        }

        PnlPeriodType.YEAR -> {
            if (cell.year == firstYear) {
                stringResource(Res.string.pnl_year_cell_first_label, cell.year, firstMonth)
            } else {
                stringResource(Res.string.pnl_year_cell_label, cell.year)
            }
        }
    }
}

@Composable
private fun SummaryCard(state: DashboardUiState) {
    val totalGain = state.totalGain
    val totalGainPercent = state.totalGainPercent
    val totalValue = state.totalMarketValue
    val totalCost = state.totalCost

    SectionCard(title = stringResource(Res.string.total_pnl)) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = totalGain.formatSigned(),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (totalGain >= 0) Gold else lossColor,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = totalGainPercent.formatSigned() + "%",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (totalGainPercent >= 0) gainColor else lossColor,
                modifier = Modifier.alignByBaseline(),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            DetailText(
                stringResource(Res.string.total_market_value_label),
                totalValue.formatLocalized(),
                Modifier.alignByBaseline(),
            )
            DetailText(
                stringResource(Res.string.total_cost_label),
                totalCost.formatLocalized(),
                Modifier.alignByBaseline(),
            )
        }
    }
}

@Composable
private fun DailyPnlCard(holdings: List<HoldingDisplay>) {
    val totalDailyGain = remember(holdings) { holdings.sumOf { it.changeAmount * it.quantity } }
    val totalMarketValue = remember(holdings) { holdings.sumOf { it.totalMarketValue } }
    val previousDayValue = totalMarketValue - totalDailyGain
    val totalDailyGainPercent = if (previousDayValue > 0) (totalDailyGain / previousDayValue) * 100 else 0.0

    SectionCard(title = stringResource(Res.string.daily_pnl)) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = totalDailyGain.formatSigned(),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (totalDailyGain >= 0) Gold else lossColor,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = totalDailyGainPercent.formatSigned() + "%",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (totalDailyGainPercent >= 0) gainColor else lossColor,
                modifier = Modifier.alignByBaseline(),
            )
        }
    }
}

@Composable
private fun DetailText(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextSecondary,
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
    }
}

@Composable
private fun ChartCard(holdings: List<HoldingDisplay>) {
    val chartData =
        remember(holdings) {
            val activeHoldings = holdings.filter { it.trends.isNotEmpty() }
            if (activeHoldings.isEmpty()) return@remember emptyList()

            // Collect all unique time points across all stocks
            val allTimes =
                activeHoldings
                    .flatMap { holding -> holding.trends.map { it.time } }
                    .toSet()
                    .sorted()

            if (allTimes.isEmpty()) return@remember emptyList()

            // For each time point, compute aggregated PnL:
            // sum of (price_at_time - preClose) * quantity across all stocks
            allTimes.map { time ->
                activeHoldings.sumOf { holding ->
                    val priceAtTime =
                        holding.trends.find { it.time == time }?.price
                            ?: return@sumOf 0.0
                    (priceAtTime - holding.preClose) * holding.quantity
                }
            }
        }

    SectionCard(
        title = stringResource(Res.string.holdings_trend),
        trailing = {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface)
                        .padding(horizontal = 12.dp, vertical = 2.dp),
            ) {
                Text(
                    text = stringResource(Res.string.today_badge),
                    fontSize = 11.sp,
                    color = TextMuted,
                )
            }
        },
    ) {
        if (chartData.size >= 2) {
            val data = chartData.mapIndexed { index, value -> index.toFloat() to value.toFloat() }
            val chartColor = if (chartData.last() >= 0) gainColor else lossColor
            LineChart(
                data = data,
                modifier = Modifier.fillMaxWidth().aspectRatio(2.4f).clip(RoundedCornerShape(8.dp)),
                lineColor = chartColor,
                showBaseline = true,
                showGridLines = true,
                gradientFill = true,
            )
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(342f / 140f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.no_trend_data),
                    fontSize = 13.sp,
                    color = TextMuted,
                )
            }
        }
    }
}

@Composable
private fun MetricsSection(state: DashboardUiState) {
    val totalValue = state.totalMarketValue
    val totalCost = state.totalCost
    val totalGain = state.totalGain
    val totalGainPercent = state.totalGainPercent

    SectionCard(title = stringResource(Res.string.key_metrics)) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                label = stringResource(Res.string.total_assets),
                value = totalValue.formatLocalized(),
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = stringResource(Res.string.total_cost),
                value = totalCost.formatLocalized(),
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = stringResource(Res.string.total_profit),
                value = totalGain.formatSigned(),
                valueColor = if (totalGain >= 0) gainColor else lossColor,
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = stringResource(Res.string.profit_rate),
                value = totalGainPercent.formatSigned() + "%",
                valueColor = if (totalGainPercent >= 0) gainColor else lossColor,
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .padding(16.dp),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
            letterSpacing = 0.4.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
        )
    }
}

@Composable
private fun HoldingsOverviewCard(
    holdings: List<HoldingDisplay>,
    modifier: Modifier = Modifier,
) {
    val sorted = remember(holdings) { holdings.sortedByDescending { it.totalMarketValue } }
    val totalGain = remember(holdings) { holdings.sumOf { it.totalGain } }

    SectionCard(
        title = stringResource(Res.string.holdings_overview),
        trailing = {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GoldDim)
                        .padding(horizontal = 10.dp, vertical = 2.dp),
            ) {
                Text(
                    text = stringResource(Res.string.holdings_count, holdings.size),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gold,
                )
            }
        },
        modifier = modifier,
    ) {
        sorted.take(5).forEach { holding ->
            HoldingRow(
                name = "${holding.name} ${holding.pinYin.ifBlank { holding.code }}",
                gain = holding.totalGain,
            )
        }

        if (sorted.size > 5) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.holdings_overflow, sorted.size - 5),
                fontSize = 12.sp,
                color = TextMuted,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Border),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SummaryBadge(stringResource(Res.string.holdings_quantity_label), "${holdings.size}")
            SummaryBadge(
                stringResource(Res.string.total_pnl_label),
                totalGain.formatSigned(),
                isPositive = totalGain >= 0,
            )
        }
    }
}

@Composable
private fun HoldingRow(name: String, gain: Double) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = gain.formatSigned(),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (gain >= 0) gainColor else lossColor,
        )
    }
}

@Composable
private fun SummaryBadge(label: String, value: String, isPositive: Boolean = false) {
    Row {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted,
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isPositive) gainColor else TextPrimary,
        )
    }
}

private fun allTransactionsEmpty(uiState: DashboardUiState): Boolean {
    return uiState.totalBuys == 0.0 && uiState.totalSells == 0.0 && uiState.totalDividends == 0.0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StockPnlDetailDialog(
    details: List<StockPnlDetail>,
    onDismiss: () -> Unit,
) {
    val sheetState =
        rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
        )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Background,
        contentColor = TextPrimary,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (details.isEmpty()) {
                Text(
                    text = stringResource(Res.string.pnl_detail_no_records),
                    fontSize = 14.sp,
                    color = TextMuted,
                )
            } else {
                details.forEach { detail ->
                    StockPnlDetailItem(detail)
                }

                // Summary card
                StockPnlDetailSummary(details)
            }
        }
    }
}

@Composable
private fun StockPnlDetailItem(detail: StockPnlDetail) {
    val iconColors =
        listOf(
            Color(0xFF3B82F6) to Color(0xFF1E3A5F),
            Color(0xFF8B5CF6) to Color(0xFF3B1F6E),
            Color(0xFFF59E0B) to Color(0xFF5C3D0A),
            Color(0xFF14B8A6) to Color(0xFF0D4F47),
            Color(0xFFEC4899) to Color(0xFF5C1A3D),
            Color(0xFF6366F1) to Color(0xFF2E2D6E),
            Color(0xFFF43F5E) to Color(0xFF5C1428),
        )
    val colorIndex = (detail.assetId.hashCode() and Int.MAX_VALUE) % iconColors.size
    val (iconBg, iconFg) = iconColors[colorIndex]
    val abbreviation = detail.stockName.take(2)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(14.dp))
                .padding(18.dp),
    ) {
        // Stock header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = abbreviation,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconFg,
                )
            }

            // Name + code
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = detail.stockName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = detail.assetId,
                    fontSize = 13.sp,
                    color = TextMuted,
                )
            }

            // Total PnL
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(Res.string.pnl_detail_total_pnl),
                    fontSize = 13.sp,
                    color = TextMuted,
                )
                Text(
                    text = detail.pnl.formatSigned(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (detail.pnl >= 0) gainColor else lossColor,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Detail grid — only show non-zero items
        val gridItems =
            listOf(
                Triple(
                    stringResource(Res.string.pnl_detail_buy_fee),
                    if (detail.buyFee > 0) "-¥${detail.buyFee.formatLocalized()}" else null,
                    if (detail.buyFee > 0) lossColor else null,
                ),
                Triple(
                    stringResource(Res.string.pnl_detail_sell_fee),
                    if (detail.sellFee > 0) "-¥${detail.sellFee.formatLocalized()}" else null,
                    if (detail.sellFee > 0) lossColor else null,
                ),
                Triple(
                    stringResource(Res.string.pnl_detail_dividend),
                    if (detail.dividend > 0) "+¥${detail.dividend.formatLocalized()}" else null,
                    if (detail.dividend > 0) gainColor else null,
                ),
                Triple(
                    stringResource(Res.string.pnl_detail_daily_gain),
                    detail.dailyPnl.formatSigned(),
                    if (detail.dailyPnl > 0) {
                        gainColor
                    } else if (detail.dailyPnl < 0) {
                        lossColor
                    } else {
                        null
                    },
                ),
            ).filter { it.third != null }

        if (gridItems.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                gridItems.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { (label, value, color) ->
                            PnlDetailGridItem(
                                modifier = Modifier.weight(1f),
                                label = label,
                                value = value.orEmpty(),
                                color = color ?: TextSecondary,
                            )
                        }
                        if (row.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PnlDetailGridItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Surface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

@Composable
private fun StockPnlDetailSummary(details: List<StockPnlDetail>) {
    val totalBuyFee = details.sumOf { it.buyFee }
    val totalSellFee = details.sumOf { it.sellFee }
    val totalDividend = details.sumOf { it.dividend }
    val totalDailyGain = details.sumOf { it.dailyPnl }
    val totalPnl = details.sumOf { it.pnl }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, Gold, RoundedCornerShape(14.dp))
                .background(GoldDim)
                .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (totalBuyFee > 0) {
            SummaryRow(
                label = stringResource(Res.string.pnl_detail_buy_fee_total),
                value = "-¥${totalBuyFee.formatLocalized()}",
                color = lossColor,
            )
        }
        if (totalSellFee > 0) {
            SummaryRow(
                label = stringResource(Res.string.pnl_detail_sell_fee_total),
                value = "-¥${totalSellFee.formatLocalized()}",
                color = lossColor,
            )
        }
        if (totalDividend > 0) {
            SummaryRow(
                label = stringResource(Res.string.pnl_detail_dividend_total),
                value = "+¥${totalDividend.formatLocalized()}",
                color = gainColor,
            )
        }
        SummaryRow(
            label = stringResource(Res.string.pnl_detail_daily_gain_total),
            value = totalDailyGain.formatSigned(),
            color = if (totalDailyGain >= 0) gainColor else lossColor,
        )

        // Divider
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Gold.copy(alpha = 0.2f)),
        )

        // Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.pnl_detail_total_pnl),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = totalPnl.formatSigned(),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (totalPnl >= 0) gainColor else lossColor,
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    color: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary,
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}
