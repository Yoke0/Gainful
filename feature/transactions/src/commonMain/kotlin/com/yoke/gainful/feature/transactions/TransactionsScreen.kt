package com.yoke.gainful.feature.transactions

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.CardHover
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary

private data class TradeItem(
    val code: String,
    val name: String,
    val type: String,
    val price: Double,
    val volume: Int,
    val amount: Double,
    val commission: Double,
)

@Composable
fun TransactionsScreen() {
    var selectedFilter by remember { mutableStateOf("all") }
    var expandedIndex by remember { mutableStateOf(-1) }

    val trades = remember {
        listOf(
            TradeItem("NVDA", "英伟达 (NVIDIA)", "买入", 128.50, 100, 12900.0, 50.0),
            TradeItem("AAPL", "苹果 (Apple)", "卖出", 175.30, 50, 8700.0, 25.0),
            TradeItem("600519", "贵州茅台", "买入", 155.00, 40, 6250.0, 10.0),
        )
    }

    val filteredTrades = when (selectedFilter) {
        "profit" -> trades.filter { it.type == "卖出" }
        "loss" -> trades.filter { it.type == "买入" }
        else -> trades
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
                text = "交易记录",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterButton("全部", selectedFilter == "all") { selectedFilter = "all" }
                FilterButton("盈利", selectedFilter == "profit") { selectedFilter = "profit" }
                FilterButton("亏损", selectedFilter == "loss") { selectedFilter = "loss" }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Trade count
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "最近交易",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GoldDim)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "${filteredTrades.size} 笔",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gold,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Trade list
        filteredTrades.forEachIndexed { index, trade ->
            TradeCard(
                trade = trade,
                isExpanded = expandedIndex == index,
                onClick = {
                    expandedIndex = if (expandedIndex == index) -1 else index
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun FilterButton(text: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isActive) GoldDim else Card)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) Gold else TextSecondary,
        )
    }
}

@Composable
private fun TradeCard(
    trade: TradeItem,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val profit = if (trade.type == "买入") -trade.amount else trade.amount
    val isPositive = profit >= 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Card)
            .clickable(onClick = onClick),
    ) {
        // Main row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = trade.code,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = trade.name,
                    fontSize = 12.sp,
                    color = TextMuted,
                    maxLines = 1,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = if (isPositive) "+${trade.amount.toInt()}" else "-${trade.amount.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) GainGreen else GainRed,
                )
                Text(
                    text = "\u00A5${trade.amount.toInt()} | \u00A5${trade.commission.toInt()}",
                    fontSize = 11.sp,
                    color = TextMuted,
                )
            }
        }

        // Expanded details
        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background.copy(alpha = 0.4f))
                    .padding(16.dp),
            ) {
                DetailRow("类型", trade.type)
                DetailRow("成交价", "\u00A5${trade.price}")
                DetailRow("成交量", "${trade.volume} 股")
                DetailRow("金额", "\u00A5${trade.amount.toInt()}")
                DetailRow("手续费", "\u00A5${trade.commission.toInt()}")
                DetailRow("盈亏", if (isPositive) "+${trade.amount.toInt()}" else "-${trade.amount.toInt()}")

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagChip(
                        text = trade.type,
                        isGreen = trade.type == "买入",
                    )
                    TagChip(
                        text = trade.code,
                        isGreen = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextMuted,
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
        )
    }
}

@Composable
private fun TagChip(text: String, isGreen: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isGreen) com.yoke.gainful.ui.theme.GreenDim else GoldDim,
            )
            .padding(horizontal = 12.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isGreen) GainGreen else Gold,
        )
    }
}
