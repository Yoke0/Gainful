package com.yoke.gainful.feature.transactions.overview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yoke.gainful.common.extensions.formatDecimal
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainGreen
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.GreenDim
import com.yoke.gainful.ui.theme.RedDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.Surface2
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAddTransaction: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") }

    val filteredTrades = when (selectedFilter) {
        "buy" -> uiState.transactions.filter { it.type == TransactionType.BUY }
        "sell" -> uiState.transactions.filter { it.type == TransactionType.SELL }
        "dividend" -> uiState.transactions.filter { it.type == TransactionType.DIVIDEND }
        else -> uiState.transactions
    }

    val buyCount = uiState.transactions.count { it.type == TransactionType.BUY }
    val sellCount = uiState.transactions.count { it.type == TransactionType.SELL }
    val divCount = uiState.transactions.count { it.type == TransactionType.DIVIDEND }

    val groups = remember(filteredTrades) {
        filteredTrades.groupBy { getTimeGroup(it.tradeDate) }
    }
    val groupOrder = listOf("今天", "最近7天", "本月", "更早")

    val showDeleteDialog = remember { mutableStateOf(false) }
    val deleteTarget = remember { mutableStateOf<TransactionItem?>(null) }

    if (showDeleteDialog.value && deleteTarget.value != null) {
        DeleteConfirmDialog(
            transaction = deleteTarget.value!!,
            onConfirm = {
                viewModel.deleteTransaction(deleteTarget.value!!.id)
                deleteTarget.value = null
                showDeleteDialog.value = false
            },
            onDismiss = {
                showDeleteDialog.value = false
                deleteTarget.value = null
            },
        )
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
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Gold)
                    .clickable(onClick = onAddTransaction)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "+ 添加",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Background,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            FilterTab("全部", selectedFilter == "all", null) { selectedFilter = "all" }
            FilterTab("买入", selectedFilter == "buy", GainGreen) { selectedFilter = "buy" }
            FilterTab("卖出", selectedFilter == "sell", GainRed) { selectedFilter = "sell" }
            FilterTab("股息", selectedFilter == "dividend", Gold) { selectedFilter = "dividend" }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SummaryItem("共", "${uiState.transactions.size} 笔")
            SummaryItem("买入", "$buyCount", GainGreen)
            SummaryItem("卖出", "$sellCount", GainRed)
            SummaryItem("股息", "$divCount", Gold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredTrades.isEmpty()) {
            EmptyState()
        } else {
            groupOrder.forEach { groupName ->
                val items = groups[groupName]
                if (!items.isNullOrEmpty()) {
                    TimeGroupHeader(groupName, items.size)
                    items.forEach { trade ->
                        TradeCard(
                            trade = trade,
                            onLongPress = {
                                deleteTarget.value = trade
                                showDeleteDialog.value = true
                            },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun FilterTab(text: String, isActive: Boolean, activeColor: Color?, onClick: () -> Unit) {
    val bgColor = when {
        isActive && activeColor != null -> activeColor.copy(alpha = 0.12f)
        isActive -> GoldDim
        else -> Surface
    }
    val textColor = when {
        isActive && activeColor != null -> activeColor
        isActive -> Gold
        else -> TextSecondary
    }
    val borderColor = when {
        isActive && activeColor != null -> activeColor
        isActive -> Gold
        else -> Border
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )
    }
}

@Composable
private fun SummaryItem(label: String, value: String, valueColor: Color = TextPrimary) {
    Row {
        Text(
            text = "$label ",
            fontSize = 11.sp,
            color = TextMuted,
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = valueColor,
        )
    }
}

@Composable
private fun TimeGroupHeader(title: String, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Surface)
                .padding(horizontal = 10.dp, vertical = 2.dp),
        ) {
            Text(
                text = "$count 笔",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun TradeCard(
    trade: TransactionItem,
    onLongPress: () -> Unit = {},
) {
    val isBuy = trade.type == TransactionType.BUY
    val isSell = trade.type == TransactionType.SELL

    val isPressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed.value) 0.97f else 1f, label = "scale")

    val typeColor = when {
        isBuy -> GainGreen
        isSell -> GainRed
        else -> Gold
    }
    val typeBgColor = when {
        isBuy -> GreenDim
        isSell -> RedDim
        else -> GoldDim
    }
    val typeLabel = when {
        isBuy -> "买入"
        isSell -> "卖出"
        else -> "股息"
    }
    val amountPrefix = if (isBuy) "-" else "+"
    val dateStr = Instant.fromEpochMilliseconds(trade.tradeDate)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .toString()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isPressed.value) Card.copy(alpha = 0.8f) else Card)
            .border(1.dp, if (isPressed.value) GainRed.copy(alpha = 0.5f) else Border, RoundedCornerShape(10.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed.value = true
                        tryAwaitRelease()
                        isPressed.value = false
                    },
                    onLongPress = {
                        isPressed.value = false
                        onLongPress()
                    },
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trade.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(typeBgColor)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = typeLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = typeColor,
                        )
                    }
                    Text(
                        text = trade.pinYin.ifBlank { trade.code },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "$amountPrefix${trade.displayAmount.formatDecimal(2)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = typeColor,
                )
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted,
                )
            }
        }

        if (trade.type != TransactionType.DIVIDEND) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Border.copy(alpha = 0.3f)),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "数量",
                            fontSize = 11.sp,
                            color = TextMuted,
                        )
                        Text(
                            text = "${trade.quantity.toInt()} 股",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "价格",
                            fontSize = 11.sp,
                            color = TextMuted,
                        )
                        Text(
                            text = "\u00A5${trade.price.formatDecimal(2)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "手续费",
                            fontSize = 11.sp,
                            color = TextMuted,
                        )
                        Text(
                            text = "\u00A5${trade.fee.formatDecimal(2)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = TextSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Surface2)
                .border(1.dp, Border, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\uD83D\uDCCB",
                fontSize = 20.sp,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "暂无交易记录",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "点击右上角添加你的第一笔交易",
            fontSize = 14.sp,
            color = TextMuted,
        )
    }
}

private fun getTimeGroup(timestamp: Long): String {
    val days = getDaysAgo(timestamp)
    return when {
        days == 0 -> "今天"
        days <= 7 -> "最近7天"
        days <= 30 -> "本月"
        else -> "更早"
    }
}

private fun getDaysAgo(timestamp: Long): Int {
    val tradeDate = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    val now = Clock.System.now()
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return (today.toEpochDays() - tradeDate.toEpochDays()).toInt()
}

@Composable
private fun DeleteConfirmDialog(
    transaction: TransactionItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val typeLabel = when (transaction.type) {
        TransactionType.BUY -> "买入"
        TransactionType.SELL -> "卖出"
        TransactionType.DIVIDEND -> "股息"
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(14.dp))
                .padding(24.dp),
        ) {
            Text(
                text = "确认删除",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "确定要删除这笔",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (transaction.type) {
                                    TransactionType.BUY -> GreenDim
                                    TransactionType.SELL -> RedDim
                                    else -> GoldDim
                                },
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = typeLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (transaction.type) {
                                TransactionType.BUY -> GainGreen
                                TransactionType.SELL -> GainRed
                                else -> Gold
                            },
                        )
                    }
                    Text(
                        text = transaction.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary,
                    )
                }
                Text(
                    text = "交易吗？此操作不可撤销。",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "取消",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GainRed)
                        .clickable(onClick = onConfirm)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "删除",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
