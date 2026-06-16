package com.yoke.gainful.feature.holdings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary

private data class Position(
    val name: String,
    val tag: String,
    val profit: Int,
    val returnPercent: Double,
)

@Composable
fun HoldingsScreen() {
    val positions = remember {
        listOf(
            Position("NVDA 英伟达", "科技", 21892, 18.2),
            Position("META 元宇宙", "科技", 29705, 15.6),
            Position("AMD 超威", "半导体", 11440, 12.4),
            Position("AAPL 苹果", "消费电子", 9360, 11.8),
            Position("VTI 全市场ETF", "指数", 9425, 10.5),
            Position("JPM 摩根大通", "金融", 3744, 9.2),
            Position("LLY 礼来", "医疗", 2496, 8.0),
            Position("XOM 埃克森美孚", "能源", 1248, 6.8),
            Position("INTC 英特尔", "芯片", -3224, -4.8),
        )
    }

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
                text = "持仓",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
            )
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
                    text = "已同步",
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Allocation Card
        AllocationCard()

        Spacer(modifier = Modifier.height(14.dp))

        // Holdings Detail Card
        HoldingsDetailCard(positions)

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun AllocationCard() {
    val segments = listOf(
        "科技" to 38f,
        "金融" to 18f,
        "消费" to 20f,
        "医疗" to 10f,
        "能源" to 8f,
        "其他" to 6f,
    )
    val segmentColors = listOf(
        Gold,
        GainGreen,
        Color(0xFF4285F4),
        Color(0xFFAB47BC),
        Color(0xFFF57C00),
        Color(0xFF546E7A),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(20.dp),
    ) {
        Text(
            text = "资产配置",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        // Allocation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(RoundedCornerShape(6.dp)),
        ) {
            segments.forEachIndexed { index, (label, percentage) ->
                Box(
                    modifier = Modifier
                        .weight(percentage)
                        .height(32.dp)
                        .background(segmentColors[index]),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$label ${percentage.toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            segments.forEachIndexed { index, (label, percentage) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(segmentColors[index]),
                    )
                    Text(
                        text = "$label $percentage%",
                        fontSize = 12.sp,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun HoldingsDetailCard(positions: List<Position>) {
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
                text = "持仓明细",
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
                    text = "${positions.size} 个标的",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gold,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        positions.forEach { position ->
            PositionRow(position)
            if (position != positions.last()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Border.copy(alpha = 0.03f)),
                )
            }
        }
    }
}

@Composable
private fun PositionRow(position: Position) {
    val isPositive = position.profit >= 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = position.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldDim)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = position.tag,
                    fontSize = 10.sp,
                    color = Gold,
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isPositive) "+${position.profit}" else "${position.profit}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPositive) GainGreen else GainRed,
            )
            Text(
                text = "${if (isPositive) "+" else ""}${position.returnPercent}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPositive) GainGreen else GainRed,
            )
        }
    }
}


