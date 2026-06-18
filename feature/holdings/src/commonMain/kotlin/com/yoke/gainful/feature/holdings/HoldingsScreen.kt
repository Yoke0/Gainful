package com.yoke.gainful.feature.holdings

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
import com.yoke.gainful.common.extensions.formatSigned
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun HoldingsScreen(
    viewModel: HoldingsViewModel,
    onStockClick: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    val totalValue = remember(uiState.holdings) {
        uiState.holdings.sumOf { it.totalMarketValue }
    }
    val totalPnl = remember(uiState.holdings) {
        uiState.holdings.sumOf { it.totalGain }
    }
    val totalPnlPct = remember(uiState.holdings, totalValue) {
        val totalCost = uiState.holdings.sumOf { it.totalCost }
        if (totalCost > 0) (totalPnl / totalCost) * 100 else 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        PortfolioHeader()

        Spacer(modifier = Modifier.height(16.dp))

        TotalCard(totalValue, totalPnl, totalPnlPct)

        Spacer(modifier = Modifier.height(14.dp))

        HeatmapCard(uiState.holdings, totalValue)

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "持仓明细",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        HoldingList(uiState.holdings, onStockClick)

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun PortfolioHeader() {
    Text(
        text = "持仓",
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextPrimary,
    )
}

@Composable
private fun TotalCard(totalValue: Double, totalPnl: Double, totalPnlPct: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        Text(
            text = "总资产",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = totalValue.formatDecimal(2),
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
                text = if (totalPnl >= 0) "+${totalPnl.formatDecimal(2)}" else totalPnl.formatDecimal(2),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = if (totalPnl >= 0) GainGreen else GainRed,
            )
            Text(
                text = totalPnlPct.formatSigned() + "%",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = if (totalPnlPct >= 0) GainGreen else GainRed,
            )
            Text(
                text = "今日",
                fontSize = 14.sp,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun HeatmapCard(holdings: List<HoldingDisplay>, totalValue: Double) {
    val gradientColors = listOf(
        listOf(Color(0xFFC8A34E), Color(0xFFA8862E)),
        listOf(Color(0xFF4285F4), Color(0xFF2A5FC1)),
        listOf(Color(0xFF1DB954), Color(0xFF148A3E)),
        listOf(Color(0xFFAB47BC), Color(0xFF7B2D8B)),
    )

    val sorted = remember(holdings) {
        holdings.sortedByDescending { it.totalMarketValue }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp),
        ) {
            Text(
                text = "投资比重",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 0.6.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Border),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            sorted.chunked(2).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    row.forEachIndexed { idx, holding ->
                        val pct = if (totalValue > 0) (holding.totalMarketValue / totalValue) * 100 else 0.0
                        val colors = gradientColors[(sorted.indexOf(holding)) % gradientColors.size]

                        HeatmapItem(
                            name = holding.name,
                            code = holding.code,
                            pct = pct,
                            amount = holding.totalMarketValue,
                            gradientColors = colors,
                            modifier = Modifier.weight(1f),
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

@Composable
private fun HeatmapItem(
    name: String,
    code: String,
    pct: Double,
    amount: Double,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.linearGradient(gradientColors)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Column {
                Text(
                    text = "${pct.formatDecimal(1)}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                )
                Text(
                    text = amount.formatCompact(),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
        }
        Text(
            text = code,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
        )
    }
}

@Composable
private fun HoldingList(
    holdings: List<HoldingDisplay>,
    onStockClick: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        holdings.forEach { holding ->
            HoldingCard(holding, onStockClick)
        }
    }
}

@Composable
private fun HoldingCard(
    holding: HoldingDisplay,
    onStockClick: (String) -> Unit,
) {
    val isPositive = holding.totalGain >= 0
    val change = holding.currentPrice - holding.averageCost
    val strokeColor = if (holding.changeAmount >= 0) GainGreen else GainRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Card)
            .clickable { onStockClick(holding.code) }
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
                    text = holding.code,
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
                    text = holding.currentPrice.formatDecimal(2),
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
                    color = if (isPositive) GainGreen else GainRed,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MetaText("市值", holding.totalMarketValue.formatCompact(), Modifier.weight(1f))
                MetaText("成本", holding.averageCost.formatDecimal(2), Modifier.weight(1f))
                MetaText("股数", "${holding.quantity.toInt()}", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                MetaText(
                    "盈亏",
                    "${if (isPositive) "+" else ""}${holding.totalGain.formatDecimal(2)}",
                    valueColor = if (isPositive) GainGreen else GainRed,
                )
            }
        }

        Sparkline(
            trendPrices = holding.trendPrices,
            changeAmount = holding.changeAmount,
            modifier = Modifier
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
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = valueColor,
        )
    }
}

@Composable
private fun Sparkline(
    trendPrices: List<Double>,
    changeAmount: Double,
    modifier: Modifier = Modifier,
    strokeColor: Color = GainGreen,
) {
    val points = remember(trendPrices, changeAmount) {
        if (trendPrices.size >= 2) {
            val min = trendPrices.min()
            val max = trendPrices.max()
            val range = max - min
            trendPrices.map { price ->
                if (range > 0) (price - min) / range else 0.5
            }
        } else {
            val direction = if (changeAmount >= 0) "up" else "down"
            generateSparkline(direction)
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stepX = w / (points.size - 1).coerceAtLeast(1).toFloat()

        val linePath = Path()
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val y = h * value.toFloat()
            if (index == 0) {
                linePath.moveTo(x, y)
            } else {
                linePath.lineTo(x, y)
            }
        }

        drawPath(
            path = linePath,
            color = strokeColor,
            style = Stroke(
                width = 2f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

private fun generateSparkline(direction: String): List<Double> {
    val points = mutableListOf<Double>()
    val seed = direction.hashCode().toLong()
    var y = 0.5
    for (i in 0..20) {
        val t = i / 20.0
        val wave = sin(t * PI * 3) * 0.08 + (((seed + i * 7) % 100) / 100.0 - 0.5) * 0.06
        y = if (direction == "up") {
            0.8 - t * 0.5 + wave
        } else {
            0.2 + t * 0.5 + wave
        }
        y = y.coerceIn(0.05, 0.95)
        points.add(y)
    }
    return points
}
