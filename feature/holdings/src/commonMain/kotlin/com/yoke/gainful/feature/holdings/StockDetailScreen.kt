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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatCompact
import com.yoke.gainful.common.extensions.formatDecimal
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
import kotlin.math.sin

private data class StockInfo(
    val code: String,
    val name: String,
    val fullName: String,
    val price: Double,
    val avgCost: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val volume: Long,
    val mktCap: Double,
    val pe: Double?,
    val yieldPct: Double?,
    val high52: Double,
    val low52: Double,
    val shares: Int,
)

private data class TradeRecord(
    val type: String,
    val shares: Int,
    val price: Double,
    val date: String,
)

private val stockDB = mapOf(
    "NVDA" to StockInfo("NVDA", "英伟达", "NVIDIA Corp.", 158.20, 128.50, 155.80, 159.40, 154.90, 320_000_000, 2_850_000_000_000.0, 42.5, 0.04, 165.30, 95.20, 100),
    "AAPL" to StockInfo("AAPL", "苹果", "Apple Inc.", 192.40, 175.30, 190.20, 193.10, 189.50, 180_000_000, 3_020_000_000_000.0, 31.2, 0.52, 199.80, 148.50, 50),
    "META" to StockInfo("META", "Meta", "Meta Platforms", 398.50, 352.80, 395.00, 401.20, 393.10, 90_000_000, 1_020_000_000_000.0, 27.8, 0.11, 420.50, 280.30, 35),
    "AMD" to StockInfo("AMD", "超威", "AMD Inc.", 112.80, 95.20, 110.50, 113.90, 109.80, 210_000_000, 182_000_000_000.0, 38.4, null, 125.60, 72.80, 80),
)

private val tradeDB = mapOf(
    "NVDA" to listOf(
        TradeRecord("买入", 50, 120.30, "2025-02-10"),
        TradeRecord("买入", 30, 135.80, "2025-01-22"),
        TradeRecord("卖出", 20, 158.00, "2025-03-05"),
    ),
    "AAPL" to listOf(
        TradeRecord("买入", 30, 170.00, "2025-01-15"),
        TradeRecord("买入", 20, 183.50, "2025-02-28"),
    ),
    "META" to listOf(
        TradeRecord("买入", 20, 340.00, "2025-01-05"),
        TradeRecord("买入", 15, 370.00, "2025-02-18"),
    ),
    "AMD" to listOf(
        TradeRecord("买入", 50, 88.50, "2025-02-01"),
        TradeRecord("买入", 30, 106.20, "2025-03-01"),
    ),
)

private val periodLabels = listOf("1周", "1月", "3月", "1年")

@Composable
fun StockDetailScreen(
    code: String,
    onBack: () -> Unit,
) {
    val stock = remember(code) { stockDB[code] ?: stockDB.values.first() }
    val trades = remember(code) { tradeDB[code] ?: emptyList() }
    var selectedPeriod by remember { mutableIntStateOf(0) }

    val change = stock.price - stock.avgCost
    val changePct = (change / stock.avgCost) * 100
    val isPositive = change >= 0
    val changeColor = if (isPositive) GainGreen else GainRed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // Header
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
            Column {
                Text(
                    text = stock.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = "${stock.fullName} \u00B7 ${stock.code}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Price Hero
        PriceHeroCard(stock, change, changePct, changeColor)

        Spacer(modifier = Modifier.height(14.dp))

        // Chart
        ChartCard(selectedPeriod) { selectedPeriod = it }

        Spacer(modifier = Modifier.height(14.dp))

        // Metrics Grid
        MetricsGrid(stock)

        Spacer(modifier = Modifier.height(14.dp))

        // Recent Trades
        TradesCard(trades)

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun PriceHeroCard(
    stock: StockInfo,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stock.fullName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
            )
            Text(
                text = stock.code,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface)
                    .padding(horizontal = 12.dp, vertical = 2.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stock.price.formatDecimal(2),
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary,
                letterSpacing = (-0.5).sp,
            )
            Text(
                text = "${if (change >= 0) "+" else ""}${change.formatDecimal(2)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = changeColor,
            )
            Text(
                text = "${if (changePct >= 0) "+" else ""}${changePct.formatDecimal(2)}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = changeColor,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ExtraItem("开盘", stock.open.formatDecimal(2))
            ExtraItem("最高", stock.high.formatDecimal(2))
            ExtraItem("最低", stock.low.formatDecimal(2))
            ExtraItem("成交量", "${formatVolume(stock.volume)} 股")
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
    selectedPeriod: Int,
    onPeriodSelected: (Int) -> Unit,
) {
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
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                periodLabels.forEachIndexed { index, label ->
                    val isActive = index == selectedPeriod
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isActive) GoldDim else Surface)
                            .clickable { onPeriodSelected(index) }
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = if (isActive) Gold else TextMuted,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ChartArea()
    }
}

@Composable
private fun ChartArea() {
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

        val points = generateChartPoints()
        val minVal = points.min()
        val maxVal = points.max()
        val range = maxVal - minVal

        val linePath = Path()
        val fillPath = Path()
        val stepX = w / (points.size - 1).toFloat()

        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalized = if (range > 0) (value - minVal) / range else 0.5f
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

        val lastX = (points.size - 1) * stepX
        val lastNormalized = if (range > 0) (points.last() - minVal) / range else 0.5f
        val lastY = padTop + chartH * (1f - lastNormalized)
        drawCircle(color = lineColor, radius = 3.5f, center = Offset(lastX, lastY))
        drawCircle(color = Surface, radius = 1.5f, center = Offset(lastX, lastY))
    }
}

@Composable
private fun MetricsGrid(stock: StockInfo) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricItem("市值", stock.mktCap.formatCompact(), Modifier.weight(1f))
            MetricItem("市盈率", stock.pe?.formatDecimal(1) ?: "\u2014", Modifier.weight(1f))
            MetricItem("收益率", stock.yieldPct?.let { "${it.formatDecimal(2)}%" } ?: "\u2014", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricItem("52周高", stock.high52.formatDecimal(2), Modifier.weight(1f))
            MetricItem("52周低", stock.low52.formatDecimal(2), Modifier.weight(1f))
            MetricItem("持仓", "${stock.shares} 股", Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, modifier: Modifier = Modifier) {
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
            color = TextPrimary,
        )
    }
}

@Composable
private fun TradesCard(trades: List<TradeRecord>) {
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
private fun TradeRow(trade: TradeRecord) {
    val isBuy = trade.type == "买入"
    val typeColor = if (isBuy) GainGreen else GainRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = trade.type,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = typeColor,
            )
            Text(
                text = trade.date,
                fontSize = 11.sp,
                color = TextMuted,
            )
        }
        Text(
            text = "${trade.shares} 股 @ ${trade.price.formatDecimal(2)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = TextPrimary,
        )
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

private fun generateChartPoints(): List<Float> {
    val points = mutableListOf<Float>()
    for (i in 0..30) {
        val t = i / 30.0
        val y = 0.7 - t * 0.6 + sin(t * Math.PI * 3) * 0.08 + ((i * 7) % 10) / 100.0
        points.add(y.coerceIn(0.05, 0.95).toFloat())
    }
    return points
}
