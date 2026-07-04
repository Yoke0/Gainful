package com.yoke.gainful.feature.transactions.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
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
import com.yoke.gainful.designsystem.components.GainfulScaffold
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.components.PrimaryButton
import com.yoke.gainful.designsystem.components.bottomBarPadding
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.Surface2
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.TransactionCard
import com.yoke.gainful.ui.TransactionDisplayItem
import com.yoke.gainful.ui.gainColor
import com.yoke.gainful.ui.lossColor
import gainful.core.designsystem.generated.resources.ic_clipboard
import gainful.feature.transactions.generated.resources.Res
import gainful.feature.transactions.generated.resources.add_button
import gainful.feature.transactions.generated.resources.all
import gainful.feature.transactions.generated.resources.buy
import gainful.feature.transactions.generated.resources.dividend
import gainful.feature.transactions.generated.resources.no_trade_records_empty
import gainful.feature.transactions.generated.resources.no_trade_records_hint
import gainful.feature.transactions.generated.resources.sell
import gainful.feature.transactions.generated.resources.summary_buy
import gainful.feature.transactions.generated.resources.summary_dividend
import gainful.feature.transactions.generated.resources.summary_sell
import gainful.feature.transactions.generated.resources.summary_total
import gainful.feature.transactions.generated.resources.time_groups
import gainful.feature.transactions.generated.resources.trade_count_unit
import gainful.feature.transactions.generated.resources.transactions_title
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant
import gainful.core.designsystem.generated.resources.Res as DsRes

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAddTransaction: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredTrades =
        when (uiState.filterType) {
            TransactionType.BUY -> uiState.transactions.filter { it.type == TransactionType.BUY }
            TransactionType.SELL -> uiState.transactions.filter { it.type == TransactionType.SELL }
            TransactionType.DIVIDEND -> uiState.transactions.filter { it.type == TransactionType.DIVIDEND }
            null -> uiState.transactions
        }

    val buyCount = uiState.transactions.count { it.type == TransactionType.BUY }
    val sellCount = uiState.transactions.count { it.type == TransactionType.SELL }
    val divCount = uiState.transactions.count { it.type == TransactionType.DIVIDEND }

    val groups =
        remember(filteredTrades) {
            filteredTrades.groupBy { getTimeGroupIndex(it.tradeDate) }
        }
    val groupOrder = stringArrayResource(Res.array.time_groups)

    TransactionsScreen(
        uiState = uiState,
        filteredTrades = filteredTrades,
        groups = groups,
        groupOrder = groupOrder,
        buyCount = buyCount,
        sellCount = sellCount,
        divCount = divCount,
        onIntent = viewModel::onIntent,
        onAddTransaction = onAddTransaction,
        onDeleteTrade = { trade ->
            viewModel.onIntent(TransactionsIntent.DeleteTransaction(trade.id))
        },
    )
}

@Composable
private fun TransactionsScreen(
    uiState: TransactionsUiState,
    filteredTrades: List<TransactionItem>,
    groups: Map<Int, List<TransactionItem>>,
    groupOrder: List<String>,
    buyCount: Int,
    sellCount: Int,
    divCount: Int,
    onIntent: (TransactionsIntent) -> Unit,
    onAddTransaction: () -> Unit,
    onDeleteTrade: (TransactionItem) -> Unit,
) {
    GainfulScaffold(
        appTopBar = {
            GainfulTopAppBar(
                title = stringResource(Res.string.transactions_title),
                actions = {
                    PrimaryButton(
                        label = stringResource(Res.string.add_button),
                        onClick = onAddTransaction,
                    )
                },
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                FilterTab(stringResource(Res.string.all), uiState.filterType == null, null) {
                    onIntent(TransactionsIntent.SetFilter(null))
                }
                FilterTab(stringResource(Res.string.buy), uiState.filterType == TransactionType.BUY, gainColor) {
                    onIntent(TransactionsIntent.SetFilter(TransactionType.BUY))
                }
                FilterTab(stringResource(Res.string.sell), uiState.filterType == TransactionType.SELL, lossColor) {
                    onIntent(TransactionsIntent.SetFilter(TransactionType.SELL))
                }
                FilterTab(stringResource(Res.string.dividend), uiState.filterType == TransactionType.DIVIDEND, Gold) {
                    onIntent(TransactionsIntent.SetFilter(TransactionType.DIVIDEND))
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SummaryItem(
                    stringResource(Res.string.summary_total),
                    stringResource(Res.string.trade_count_unit, uiState.transactions.size),
                )
                SummaryItem(stringResource(Res.string.summary_buy), "$buyCount", gainColor)
                SummaryItem(stringResource(Res.string.summary_sell), "$sellCount", lossColor)
                SummaryItem(stringResource(Res.string.summary_dividend), "$divCount", Gold)
            }

            if (filteredTrades.isEmpty()) {
                EmptyState()
            } else {
                groupOrder.forEachIndexed { index, groupName ->
                    val items = groups[index]
                    if (!items.isNullOrEmpty()) {
                        TimeGroupHeader(groupName, items.size)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items.forEach { trade ->
                                TransactionCard(
                                    item =
                                        TransactionDisplayItem(
                                            name = trade.name,
                                            code = trade.code,
                                            pinYin = trade.pinYin,
                                            type = trade.type,
                                            quantity = trade.quantity,
                                            price = trade.price,
                                            amount = trade.amount,
                                            tradeDate = trade.tradeDate,
                                        ),
                                    onDelete = { onDeleteTrade(trade) },
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.bottomBarPadding())
        }
    }
}

@Composable
private fun FilterTab(text: String, isActive: Boolean, activeColor: Color?, onClick: () -> Unit) {
    val bgColor =
        when {
            isActive && activeColor != null -> activeColor.copy(alpha = 0.12f)
            isActive -> GoldDim
            else -> Surface
        }
    val textColor =
        when {
            isActive && activeColor != null -> activeColor
            isActive -> Gold
            else -> TextSecondary
        }
    val borderColor =
        when {
            isActive && activeColor != null -> activeColor
            isActive -> Gold
            else -> Border
        }

    Box(
        modifier =
            Modifier
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
            modifier =
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
        ) {
            Text(
                text = stringResource(Res.string.trade_count_unit, count),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(14.dp))
                .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Surface2)
                    .border(1.dp, Border, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(DsRes.drawable.ic_clipboard),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TextSecondary,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.no_trade_records_empty),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.no_trade_records_hint),
            fontSize = 14.sp,
            color = TextMuted,
        )
    }
}

private fun getTimeGroupIndex(timestamp: Long): Int {
    val days = getDaysAgo(timestamp)
    return when {
        days == 0 -> 0
        days <= 7 -> 1
        days <= 30 -> 2
        else -> 3
    }
}

private fun getDaysAgo(timestamp: Long): Int {
    val tradeDate =
        Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    val now = Clock.System.now()
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return (today.toEpochDays() - tradeDate.toEpochDays()).toInt()
}
