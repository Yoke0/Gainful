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
import androidx.compose.runtime.collectAsState
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
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAddTransaction: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") }
    var expandedIndex by remember { mutableStateOf(-1) }

    val filteredTrades = when (selectedFilter) {
        "buy" -> uiState.transactions.filter { it.type == TransactionType.BUY }
        "sell" -> uiState.transactions.filter { it.type == TransactionType.SELL }
        else -> uiState.transactions
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
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
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterButton("全部", selectedFilter == "all") { selectedFilter = "all" }
                FilterButton("买入", selectedFilter == "buy") { selectedFilter = "buy" }
                FilterButton("卖出", selectedFilter == "sell") { selectedFilter = "sell" }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Gold)
                        .clickable(onClick = onAddTransaction)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "+",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Background,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
    trade: TransactionItem,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val isSell = trade.type == TransactionType.SELL
    val isDividend = trade.type == TransactionType.DIVIDEND
    val isPositive = isSell || isDividend

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Card)
            .clickable(onClick = onClick),
    ) {
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
                    text = "\u00A5${trade.amount.toInt()} | \u00A5${trade.fee.toInt()}",
                    fontSize = 11.sp,
                    color = TextMuted,
                )
            }
        }

        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background.copy(alpha = 0.4f))
                    .padding(16.dp),
            ) {
                DetailRow("类型", trade.typeLabel)
                DetailRow("成交价", "\u00A5${trade.price}")
                DetailRow("数量", "${trade.quantity.toInt()} 股")
                DetailRow("金额", "\u00A5${trade.amount.toInt()}")
                DetailRow("手续费", "\u00A5${trade.fee.toInt()}")

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagChip(
                        text = trade.typeLabel,
                        isGreen = trade.type == TransactionType.BUY,
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
