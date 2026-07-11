package com.yoke.gainful.feature.holdings.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.common.extensions.formatSigned
import com.yoke.gainful.designsystem.components.GainfulScaffold
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.components.bottomBarPadding
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.ClosedPosition
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.StockTrend
import com.yoke.gainful.ui.LineChart
import com.yoke.gainful.ui.MarketCapTreemap
import com.yoke.gainful.ui.TreemapItem
import com.yoke.gainful.ui.gainColor
import com.yoke.gainful.ui.lossColor
import gainful.feature.holdings.generated.resources.Res
import gainful.feature.holdings.generated.resources.closed_positions
import gainful.feature.holdings.generated.resources.cost
import gainful.feature.holdings.generated.resources.holdings_detail_header
import gainful.feature.holdings.generated.resources.holdings_title
import gainful.feature.holdings.generated.resources.investment_weight
import gainful.feature.holdings.generated.resources.liquidation_price
import gainful.feature.holdings.generated.resources.market_value
import gainful.feature.holdings.generated.resources.profit_loss
import gainful.feature.holdings.generated.resources.shares
import gainful.feature.holdings.generated.resources.total_assets
import org.jetbrains.compose.resources.stringResource

@Composable
fun HoldingsScreen(
    viewModel: HoldingsViewModel,
    onStockClick: (String, String, String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    HoldingsScreen(
        holdings = uiState.holdings,
        closedPositions = uiState.closedPositions,
        totalValue = uiState.totalValue,
        totalPnl = uiState.totalPnl,
        totalPnlPct = uiState.totalPnlPct,
        onStockClick = onStockClick,
    )
}

@Composable
private fun HoldingsScreen(
    holdings: List<HoldingDisplay>,
    closedPositions: List<ClosedPosition>,
    totalValue: Double,
    totalPnl: Double,
    totalPnlPct: Double,
    onStockClick: (String, String, String) -> Unit,
) {
    GainfulScaffold(
        appTopBar = {
            GainfulTopAppBar(title = stringResource(Res.string.holdings_title))
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TotalCard(totalValue, totalPnl, totalPnlPct)

            if (holdings.isNotEmpty()) {
                val treemapItems =
                    remember(holdings, totalValue) {
                        holdings.map { holding ->
                            val pct = if (totalValue > 0) (holding.totalMarketValue / totalValue) * 100 else 0.0
                            TreemapItem(name = holding.name, percentage = pct.toFloat())
                        }
                    }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Card)
                            .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SectionHeader(title = stringResource(Res.string.investment_weight))
                    MarketCapTreemap(items = treemapItems)
                }
            }

            ListSection(
                title = stringResource(Res.string.holdings_detail_header),
                items = holdings.sortedByDescending { it.quantity },
            ) { holding ->
                HoldingCard(holding, onStockClick)
            }

            if (closedPositions.isNotEmpty()) {
                ListSection(
                    title = stringResource(Res.string.closed_positions),
                    items = closedPositions,
                ) { position ->
                    ClosedPositionItem(position, onStockClick)
                }
            }

            Spacer(modifier = Modifier.bottomBarPadding())
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary,
    )
}

@Composable
private fun <T> ListSection(
    title: String,
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionHeader(title)
        items.forEach { itemContent(it) }
    }
}

@Composable
private fun TotalCard(totalValue: Double, totalPnl: Double, totalPnlPct: Double) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .padding(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.total_assets),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = totalValue.formatLocalized(),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-0.5).sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = totalPnl.formatSigned(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = if (totalPnl >= 0) gainColor else lossColor,
            )
            Text(
                text = totalPnlPct.formatSigned() + "%",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = if (totalPnlPct >= 0) gainColor else lossColor,
            )
        }
    }
}

@Composable
private fun HoldingCard(
    holding: HoldingDisplay,
    onStockClick: (String, String, String) -> Unit,
) {
    val isPositive = holding.changeAmount >= 0
    val change = holding.changeAmount
    val strokeColor = if (isPositive) gainColor else lossColor

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Card)
                .clickable { onStockClick(holding.code, holding.name, holding.pinYin) }
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = holding.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    text = holding.pinYin.ifBlank { holding.code },
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted,
                    modifier = Modifier.alignByBaseline(),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = holding.currentPrice.formatLocalized(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp,
                )
                Text(
                    text = change.formatSigned(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isPositive) gainColor else lossColor,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MetaText(stringResource(Res.string.market_value), holding.totalMarketValue.formatLocalized(), Modifier.weight(1f))
                MetaText(stringResource(Res.string.cost), holding.averageCost.formatLocalized(), Modifier.weight(1f))
                MetaText(stringResource(Res.string.shares), "${holding.quantity.toInt()}", Modifier.weight(1f))
                MetaText(
                    stringResource(Res.string.profit_loss),
                    holding.totalGain.formatLocalized(),
                    Modifier.weight(1f),
                    valueColor = if (holding.totalGain > 0) gainColor else lossColor,
                )
            }
        }

        Sparkline(
            trends = holding.trends,
            preClose = holding.preClose,
            modifier =
                Modifier
                    .width(72.dp)
                    .height(44.dp),
            strokeColor = strokeColor,
        )
    }
}

@Composable
private fun MetaText(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = TextPrimary) {
    Row(modifier = modifier) {
        Text(
            text = "$label ",
            fontSize = 10.sp,
            color = TextMuted,
            maxLines = 1,
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = valueColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun ClosedPositionItem(position: ClosedPosition, onStockClick: (String, String, String) -> Unit) {
    val isPositive = position.realizedGain >= 0

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Card)
                .clickable { onStockClick(position.code, position.name, position.pinYin) }
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = position.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    text = position.pinYin.ifBlank { position.code },
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted,
                    modifier = Modifier.alignByBaseline(),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MetaText(
                    stringResource(Res.string.liquidation_price),
                    position.lastSellPrice.formatLocalized(),
                )
                MetaText(
                    stringResource(Res.string.profit_loss),
                    position.realizedGainPercent.formatLocalized() + "%",
                    valueColor = if (isPositive) gainColor else lossColor,
                )
            }
        }
        Text(
            text = "${if (isPositive) "+" else ""}${position.realizedGain.formatLocalized()}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (isPositive) gainColor else lossColor,
        )
    }
}

@Composable
private fun Sparkline(
    trends: List<StockTrend>,
    preClose: Double,
    modifier: Modifier = Modifier,
    strokeColor: Color = gainColor,
) {
    val data =
        remember(trends, preClose) {
            val prices = trends.map { it.price }
            if (prices.size >= 2 && preClose > 0) {
                prices.mapIndexed { index, price ->
                    index.toFloat() to ((price - preClose) / preClose * 100).toFloat()
                }
            } else {
                emptyList()
            }
        }

    LineChart(
        data = data,
        modifier = modifier,
        lineColor = strokeColor,
        showBaseline = true,
        baselineY = 0f,
    )
}
