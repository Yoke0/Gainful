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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary

@Composable
fun DashboardScreen() {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "仪表盘",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Card)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(GainGreen),
                        )
                        Text(
                            text = "实时",
                            fontSize = 12.sp,
                            color = TextSecondary,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Card
        SummaryCard()

        Spacer(modifier = Modifier.height(14.dp))

        // Chart Card
        ChartCard()

        Spacer(modifier = Modifier.height(14.dp))

        // Metrics Grid
        MetricsSection()

        Spacer(modifier = Modifier.height(14.dp))

        // Holdings Overview
        HoldingsOverviewCard()

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SummaryCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        Text(
            text = "今日总盈亏",
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
                text = "+83,506",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Gold,
                letterSpacing = (-0.5).sp,
            )
            Text(
                text = "+8.7%",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GainGreen,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            DetailText("开盘: ", "957,820")
            DetailText("最高: ", "1,038,765")
            DetailText("最低: ", "949,493")
        }
    }
}

@Composable
private fun DetailText(label: String, value: String) {
    Row {
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
private fun ChartCard() {
    val chartData = listOf(
        -115f, -108f, -100f, -92f, -82f, -72f, -62f, -52f,
        -38f, -24f, -28f, -32f, -18f, -4f, -10f,
    )

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
                text = "每日收益趋势",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface)
                    .padding(horizontal = 12.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "近14天",
                    fontSize = 11.sp,
                    color = TextMuted,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
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

                val minVal = chartData.min()
                val maxVal = chartData.max()
                val range = maxVal - minVal

                val lineColor = Color(0xFFC8A34E)
                val gridColor = Color(0x08FFFFFF)

                // Grid lines
                for (i in 0..2) {
                    val y = padTop + chartH * i / 2f
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f,
                    )
                }

                // Build line path
                val linePath = Path()
                val fillPath = Path()
                val stepX = w / (chartData.size - 1).toFloat()

                chartData.forEachIndexed { index, value ->
                    val x = index * stepX
                    val normalized = (value - minVal) / range
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

                // Close fill path
                fillPath.lineTo(w, h)
                fillPath.close()

                // Draw gradient fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.45f),
                            lineColor.copy(alpha = 0.12f),
                            lineColor.copy(alpha = 0.02f),
                        ),
                        startY = 0f,
                        endY = h,
                    ),
                )

                // Draw line
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(
                        width = 2.5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )

                // Draw end dot
                val lastX = (chartData.size - 1) * stepX
                val lastNormalized = (chartData.last() - minVal) / range
                val lastY = padTop + chartH * (1f - lastNormalized)
                drawCircle(
                    color = Color(0xFFE0C06A),
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
    }
}

@Composable
private fun MetricsSection() {
    Column {
        Text(
            text = "关键指标",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "总资产",
                value = "1.03M",
                change = "+2.3%",
                isPositive = true,
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "总成本",
                value = "946.2K",
                change = "基准",
                isPositive = null,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "总收益",
                value = "+83.5K",
                change = "+8.7%",
                isPositive = true,
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "收益率",
                value = "8.83%",
                change = "+0.24%",
                isPositive = true,
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    change: String,
    isPositive: Boolean?,
) {
    Column(
        modifier = modifier
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
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = change,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = when (isPositive) {
                true -> GainGreen
                false -> com.yoke.gainful.ui.theme.GainRed
                null -> TextMuted
            },
        )
    }
}

@Composable
private fun HoldingsOverviewCard() {
    Column(
        modifier = Modifier
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
                text = "持仓概览",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GoldDim)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "8 个标的",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gold,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        HoldingRow("NVDA 英伟达", "+21,892", isPositive = true)
        HoldingRow("AAPL 苹果", "+9,360", isPositive = true)
        HoldingRow("META 元宇宙", "+29,705", isPositive = true)
        HoldingRow("AMD 超威", "+11,440", isPositive = true)

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Border),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SummaryBadge("持仓数量: ", "8")
            SummaryBadge("今日: ", "+5,499", true)
        }
    }
}

@Composable
private fun HoldingRow(name: String, amount: String, isPositive: Boolean) {
    Row(
        modifier = Modifier
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
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isPositive) GainGreen else com.yoke.gainful.ui.theme.GainRed,
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
            color = if (isPositive) GainGreen else TextPrimary,
        )
    }
}
