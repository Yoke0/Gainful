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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.common.extensions.formatLocalizedDate
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.components.BottomBarHeight
import com.yoke.gainful.ui.components.ConfirmDialog
import com.yoke.gainful.ui.components.GainfulTopAppBar
import com.yoke.gainful.ui.components.PrimaryButton
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.Surface2
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary
import com.yoke.gainful.ui.theme.gainColor
import com.yoke.gainful.ui.theme.gainDimColor
import com.yoke.gainful.ui.theme.lossColor
import com.yoke.gainful.ui.theme.lossDimColor
import gainful.feature.transactions.generated.resources.Res
import gainful.feature.transactions.generated.resources.add_button
import gainful.feature.transactions.generated.resources.all
import gainful.feature.transactions.generated.resources.buy
import gainful.feature.transactions.generated.resources.cancel
import gainful.feature.transactions.generated.resources.confirm_delete
import gainful.feature.transactions.generated.resources.delete
import gainful.feature.transactions.generated.resources.delete_confirm_suffix
import gainful.feature.transactions.generated.resources.delete_confirm_text
import gainful.feature.transactions.generated.resources.dividend
import gainful.feature.transactions.generated.resources.no_trade_records_empty
import gainful.feature.transactions.generated.resources.no_trade_records_hint
import gainful.feature.transactions.generated.resources.quantity_format
import gainful.feature.transactions.generated.resources.sell
import gainful.feature.transactions.generated.resources.summary_buy
import gainful.feature.transactions.generated.resources.summary_dividend
import gainful.feature.transactions.generated.resources.summary_fee
import gainful.feature.transactions.generated.resources.summary_sell
import gainful.feature.transactions.generated.resources.summary_total
import gainful.feature.transactions.generated.resources.time_groups
import gainful.feature.transactions.generated.resources.trade_count_unit
import gainful.feature.transactions.generated.resources.trade_price_label
import gainful.feature.transactions.generated.resources.trade_quantity_label
import gainful.feature.transactions.generated.resources.transactions_title
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAddTransaction: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredTrades = when (uiState.filterType) {
        TransactionType.BUY -> uiState.transactions.filter { it.type == TransactionType.BUY }
        TransactionType.SELL -> uiState.transactions.filter { it.type == TransactionType.SELL }
        TransactionType.DIVIDEND -> uiState.transactions.filter { it.type == TransactionType.DIVIDEND }
        null -> uiState.transactions
    }

    val buyCount = uiState.transactions.count { it.type == TransactionType.BUY }
    val sellCount = uiState.transactions.count { it.type == TransactionType.SELL }
    val divCount = uiState.transactions.count { it.type == TransactionType.DIVIDEND }

    val groups = remember(filteredTrades) {
        filteredTrades.groupBy { getTimeGroupIndex(it.tradeDate) }
    }
    val groupOrder = stringArrayResource(Res.array.time_groups)

    val showDeleteDialog = remember { mutableStateOf(false) }
    val deleteTarget = remember { mutableStateOf<TransactionItem?>(null) }

    if (showDeleteDialog.value && deleteTarget.value != null) {
        val target = deleteTarget.value!!
        val typeLabel = when (target.type) {
            TransactionType.BUY -> stringResource(Res.string.buy)
            TransactionType.SELL -> stringResource(Res.string.sell)
            TransactionType.DIVIDEND -> stringResource(Res.string.dividend)
        }
        val typeColor = when (target.type) {
            TransactionType.BUY -> gainColor
            TransactionType.SELL -> lossColor
            else -> Gold
        }
        val typeBgColor = when (target.type) {
            TransactionType.BUY -> gainDimColor
            TransactionType.SELL -> lossDimColor
            else -> GoldDim
        }

        ConfirmDialog(
            title = stringResource(Res.string.confirm_delete),
            confirmText = stringResource(Res.string.delete),
            dismissText = stringResource(Res.string.cancel),
            onConfirm = {
                viewModel.onIntent(TransactionsIntent.DeleteTransaction(target.id))
                deleteTarget.value = null
                showDeleteDialog.value = false
            },
            onDismiss = {
                showDeleteDialog.value = false
                deleteTarget.value = null
            },
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.delete_confirm_text),
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
                                .background(typeBgColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = typeLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = typeColor,
                            )
                        }
                        Text(
                            text = target.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary,
                        )
                    }
                    Text(
                        text = stringResource(Res.string.delete_confirm_suffix),
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        GainfulTopAppBar(
            title = stringResource(Res.string.transactions_title),
            actions = {
                PrimaryButton(
                    label = stringResource(Res.string.add_button),
                    onClick = onAddTransaction,
                )
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            FilterTab(stringResource(Res.string.all), uiState.filterType == null, null) {
                viewModel.onIntent(TransactionsIntent.SetFilter(null))
            }
            FilterTab(stringResource(Res.string.buy), uiState.filterType == TransactionType.BUY, gainColor) {
                viewModel.onIntent(TransactionsIntent.SetFilter(TransactionType.BUY))
            }
            FilterTab(stringResource(Res.string.sell), uiState.filterType == TransactionType.SELL, lossColor) {
                viewModel.onIntent(TransactionsIntent.SetFilter(TransactionType.SELL))
            }
            FilterTab(stringResource(Res.string.dividend), uiState.filterType == TransactionType.DIVIDEND, Gold) {
                viewModel.onIntent(TransactionsIntent.SetFilter(TransactionType.DIVIDEND))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SummaryItem(stringResource(Res.string.summary_total), stringResource(Res.string.trade_count_unit, uiState.transactions.size))
            SummaryItem(stringResource(Res.string.summary_buy), "$buyCount", gainColor)
            SummaryItem(stringResource(Res.string.summary_sell), "$sellCount", lossColor)
            SummaryItem(stringResource(Res.string.summary_dividend), "$divCount", Gold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredTrades.isEmpty()) {
            EmptyState()
        } else {
            groupOrder.forEachIndexed { index, groupName ->
                val items = groups[index]
                if (!items.isNullOrEmpty()) {
                    TimeGroupHeader(groupName, items.size)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items.forEach { trade ->
                            TradeCard(
                                trade = trade,
                                onLongPress = {
                                    deleteTarget.value = trade
                                    showDeleteDialog.value = true
                                },
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(BottomBarHeight))
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
                text = stringResource(Res.string.trade_count_unit, count),
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
        isBuy -> gainColor
        isSell -> lossColor
        else -> Gold
    }
    val typeBgColor = when {
        isBuy -> gainDimColor
        isSell -> lossDimColor
        else -> GoldDim
    }
    val typeLabel = when {
        isBuy -> stringResource(Res.string.buy)
        isSell -> stringResource(Res.string.sell)
        else -> stringResource(Res.string.dividend)
    }
    val amountPrefix = if (isBuy) "-" else "+"
    val dateStr = trade.tradeDate.formatLocalizedDate()

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
                    text = "$amountPrefix${trade.displayAmount.formatLocalized()}",
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
                        .background(Border),
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
                            text = stringResource(Res.string.trade_quantity_label),
                            fontSize = 11.sp,
                            color = TextMuted,
                        )
                        Text(
                            text = stringResource(Res.string.quantity_format, trade.quantity.toInt()),
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
                            text = stringResource(Res.string.trade_price_label),
                            fontSize = 11.sp,
                            color = TextMuted,
                        )
                        Text(
                            text = trade.price.formatLocalized(),
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
                            text = stringResource(Res.string.summary_fee),
                            fontSize = 11.sp,
                            color = TextMuted,
                        )
                        Text(
                            text = trade.fee.formatLocalized(),
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
    val tradeDate = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    val now = Clock.System.now()
    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return (today.toEpochDays() - tradeDate.toEpochDays()).toInt()
}
