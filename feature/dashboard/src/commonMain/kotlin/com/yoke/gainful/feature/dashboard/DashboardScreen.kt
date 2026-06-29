package com.yoke.gainful.feature.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
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
import com.yoke.gainful.ui.gainColor
import com.yoke.gainful.ui.lossColor
import gainful.feature.dashboard.generated.resources.Res
import gainful.feature.dashboard.generated.resources.dashboard_title
import gainful.feature.dashboard.generated.resources.holdings_count
import gainful.feature.dashboard.generated.resources.holdings_overflow
import gainful.feature.dashboard.generated.resources.holdings_overview
import gainful.feature.dashboard.generated.resources.holdings_quantity_label
import gainful.feature.dashboard.generated.resources.holdings_trend
import gainful.feature.dashboard.generated.resources.key_metrics
import gainful.feature.dashboard.generated.resources.live_badge
import gainful.feature.dashboard.generated.resources.no_trend_data
import gainful.feature.dashboard.generated.resources.profit_rate
import gainful.feature.dashboard.generated.resources.today_badge
import gainful.feature.dashboard.generated.resources.total_assets
import gainful.feature.dashboard.generated.resources.total_cost
import gainful.feature.dashboard.generated.resources.total_cost_label
import gainful.feature.dashboard.generated.resources.total_market_value_label
import gainful.feature.dashboard.generated.resources.total_pnl
import gainful.feature.dashboard.generated.resources.total_pnl_label
import gainful.feature.dashboard.generated.resources.total_profit
import org.jetbrains.compose.resources.stringResource

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardScreen(uiState = uiState)
}

@Composable
private fun DashboardScreen(
    uiState: DashboardUiState,
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
            SummaryCard(uiState)

            ChartCard(uiState.holdings)

            MetricsSection(uiState)

            HoldingsOverviewCard(holdings = uiState.holdings)

            Spacer(modifier = Modifier.bottomBarPadding())
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
