package com.yoke.gainful.feature.dashboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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
import com.yoke.gainful.designsystem.theme.GoldLight
import com.yoke.gainful.designsystem.theme.GridLine
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.PnlCell
import com.yoke.gainful.model.PnlPeriod
import com.yoke.gainful.model.PnlPeriodType
import com.yoke.gainful.ui.AutoSizeText
import com.yoke.gainful.ui.gainColor
import com.yoke.gainful.ui.gainDimColor
import com.yoke.gainful.ui.lossColor
import com.yoke.gainful.ui.lossDimColor
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
import gainful.feature.dashboard.generated.resources.no_trend_data
import gainful.feature.dashboard.generated.resources.pnl_current_period
import gainful.feature.dashboard.generated.resources.pnl_day_label
import gainful.feature.dashboard.generated.resources.pnl_details
import gainful.feature.dashboard.generated.resources.pnl_month_label
import gainful.feature.dashboard.generated.resources.pnl_next_period
import gainful.feature.dashboard.generated.resources.pnl_period_day
import gainful.feature.dashboard.generated.resources.pnl_period_month
import gainful.feature.dashboard.generated.resources.pnl_period_week
import gainful.feature.dashboard.generated.resources.pnl_period_year
import gainful.feature.dashboard.generated.resources.pnl_previous_period
import gainful.feature.dashboard.generated.resources.pnl_total_label
import gainful.feature.dashboard.generated.resources.pnl_total_period_label
import gainful.feature.dashboard.generated.resources.pnl_week_cell_label
import gainful.feature.dashboard.generated.resources.pnl_week_range_label
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
import org.jetbrains.compose.resources.stringResource

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
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PnlOverviewCard(
                uiState = uiState,
                onIntent = onIntent,
            )

            DailyPnlCard(uiState.holdings)

            SummaryCard(uiState)

            ChartCard(uiState.holdings)

            MetricsSection(uiState)

            HoldingsOverviewCard(holdings = uiState.holdings)

            Spacer(modifier = Modifier.bottomBarPadding())
        }
    }
}

@Composable
private fun PnlOverviewCard(
    uiState: DashboardUiState,
    onIntent: (DashboardIntent) -> Unit,
) {
    val pnlData = uiState.pnlData
    val period = pnlData?.period

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .padding(20.dp),
    ) {
        Text(
            text = periodTitle(period, uiState.selectedPnlPeriod),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(14.dp))

        PnlPeriodTabs(
            selectedPeriod = uiState.selectedPnlPeriod,
            onPeriodSelected = { period ->
                onIntent(DashboardIntent.SelectPnlPeriod(period))
            },
        )

        if (uiState.selectedPnlPeriod != PnlPeriodType.YEAR) {
            Spacer(modifier = Modifier.height(12.dp))
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
                showNext = !isCurrentPeriod,
                onNavigate = { direction ->
                    onIntent(DashboardIntent.NavigatePnlPeriod(direction))
                },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val totalPnl = pnlData?.totalPnl ?: 0.0
        val periodInfoLabel = periodInfoLabel(period, uiState.selectedPnlPeriod)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = stringResource(Res.string.pnl_current_period),
                fontSize = 12.sp,
                color = TextMuted,
            )
            Text(
                text = periodInfoLabel,
                fontSize = 12.sp,
                color = TextMuted,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = totalPnl.formatSigned(),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (totalPnl >= 0) Gold else lossColor,
            letterSpacing = (-0.5).sp,
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (pnlData != null) {
            val columns =
                when (uiState.selectedPnlPeriod) {
                    PnlPeriodType.DAY -> 7
                    PnlPeriodType.WEEK -> 4
                    PnlPeriodType.MONTH -> 4
                    PnlPeriodType.YEAR -> 3
                }

            Text(
                text = stringResource(Res.string.pnl_details),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 0.5.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.selectedPnlPeriod == PnlPeriodType.DAY) {
                WeekdayHeader()
                Spacer(modifier = Modifier.height(6.dp))
            }

            PnlGrid(
                cells = pnlData.cells,
                columns = columns,
                periodType = uiState.selectedPnlPeriod,
            )
        }
    }
}

@Composable
private fun periodTitle(period: PnlPeriod?, type: PnlPeriodType): String {
    if (period == null) return stringResource(Res.string.total_pnl)
    return when (type) {
        PnlPeriodType.DAY, PnlPeriodType.WEEK -> {
            stringResource(Res.string.pnl_year_month_label, period.year, period.month)
        }

        PnlPeriodType.MONTH -> {
            stringResource(Res.string.pnl_year_label, period.year)
        }

        PnlPeriodType.YEAR -> {
            stringResource(Res.string.pnl_total_label)
        }
    }
}

@Composable
private fun periodNavLabel(period: PnlPeriod?, type: PnlPeriodType): String {
    if (period == null) return ""
    return when (type) {
        PnlPeriodType.WEEK -> {
            stringResource(Res.string.pnl_week_range_label, getWeekNumber(period), period.startDay, period.endDay)
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
private fun periodInfoLabel(
    period: PnlPeriod?,
    type: PnlPeriodType,
): String {
    if (period == null) return ""
    return when (type) {
        PnlPeriodType.DAY -> {
            stringResource(Res.string.pnl_year_month_label, period.year, period.month)
        }

        PnlPeriodType.WEEK -> {
            stringResource(Res.string.pnl_week_range_label, getWeekNumber(period), period.startDay, period.endDay)
        }

        PnlPeriodType.MONTH -> {
            stringResource(Res.string.pnl_year_month_label, period.year, period.month)
        }

        PnlPeriodType.YEAR -> {
            stringResource(Res.string.pnl_total_period_label, period.year, 8)
        }
    }
}

private fun getWeekNumber(period: PnlPeriod): Int {
    val firstDayOfYear = kotlinx.datetime.LocalDate(period.year, 1, 1)
    val firstDayOfMonth = kotlinx.datetime.LocalDate(period.year, period.month, 1)
    val daysSinceFirstDay = firstDayOfMonth.dayOfYear - firstDayOfYear.dayOfYear
    return (daysSinceFirstDay / 7) + 1
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
    showNext: Boolean,
    onNavigate: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.pnl_previous_period),
            fontSize = 14.sp,
            color = TextMuted,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigate(-1) }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
        )
        Text(
            text = periodLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        if (showNext) {
            Text(
                text = stringResource(Res.string.pnl_next_period),
                fontSize = 14.sp,
                color = TextMuted,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigate(1) }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
            )
        } else {
            Spacer(modifier = Modifier.width(60.dp))
        }
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
                            modifier = Modifier.weight(1f),
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
    modifier: Modifier = Modifier,
) {
    val label = cellLabel(cell, periodType)

    if (cell.isFuture) {
        Column(
            modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    "—"
                } else {
                    cell.value?.formatSigned(decimals = 2) ?: "—"
                },
            maxFontSize = 11.sp,
            minFontSize = 6.sp,
            color = valueColor,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun cellLabel(cell: PnlCell, periodType: PnlPeriodType): String {
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
            val firstMonth = 8
            if (cell.year == 2023) {
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

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .padding(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.total_pnl),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
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

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .padding(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.daily_pnl),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
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
            holdings
                .filter { it.trends.isNotEmpty() }
                .flatMap { it.trends.map { trend -> trend.price } }
                .takeIf { it.isNotEmpty() }
                ?: emptyList()
        }

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
                text = stringResource(Res.string.holdings_trend),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (chartData.size >= 2) {
            val minVal = chartData.min()
            val maxVal = chartData.max()
            val range = maxVal - minVal

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(342f / 140f)
                        .clip(RoundedCornerShape(8.dp)),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val padTop = 10f
                    val padBottom = 10f
                    val chartH = h - padTop - padBottom

                    val lineColor = Gold
                    val gridColor = GridLine

                    for (i in 0..2) {
                        val y = padTop + chartH * i / 2f
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f,
                        )
                    }

                    val linePath = Path()
                    val fillPath = Path()
                    val stepX = w / (chartData.size - 1).toFloat()

                    chartData.forEachIndexed { index, value ->
                        val x = index * stepX
                        val normalized = if (range > 0) ((value - minVal) / range).toFloat() else 0.5f
                        val y = padTop + chartH * (1f - normalized)

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
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        lineColor.copy(alpha = 0.45f),
                                        lineColor.copy(alpha = 0.12f),
                                        lineColor.copy(alpha = 0.02f),
                                    ),
                                startY = 0f,
                                endY = h,
                            ),
                    )

                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style =
                            Stroke(
                                width = 2.5f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                            ),
                    )

                    val lastX = (chartData.size - 1) * stepX
                    val lastNormalized = if (range > 0) ((chartData.last() - minVal) / range).toFloat() else 0.5f
                    val lastY = padTop + chartH * (1f - lastNormalized)
                    drawCircle(
                        color = GoldLight,
                        radius = 3.5f,
                        center = Offset(lastX, lastY),
                    )
                    drawCircle(
                        color = Background,
                        radius = 1.5f,
                        center = Offset(lastX, lastY),
                    )
                }
            }
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

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.key_metrics),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
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
                .background(Card)
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

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.holdings_overview),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
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
        }
        Spacer(modifier = Modifier.height(12.dp))

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
