package com.yoke.gainful.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.common.extensions.formatLocalizedDate
import com.yoke.gainful.designsystem.components.ConfirmDialog
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.TransactionType
import gainful.core.ui.generated.resources.Res
import gainful.core.ui.generated.resources.cancel
import gainful.core.ui.generated.resources.confirm_delete
import gainful.core.ui.generated.resources.delete
import gainful.core.ui.generated.resources.delete_confirm_suffix
import gainful.core.ui.generated.resources.delete_confirm_text
import gainful.core.ui.generated.resources.trade_fee_label
import gainful.core.ui.generated.resources.trade_price_label
import gainful.core.ui.generated.resources.trade_quantity_label
import gainful.core.ui.generated.resources.trade_shares_format
import gainful.core.ui.generated.resources.trade_type_buy
import gainful.core.ui.generated.resources.trade_type_dividend
import gainful.core.ui.generated.resources.trade_type_sell
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.stringResource

data class TransactionDisplayItem(
    val name: String,
    val code: String,
    val pinYin: String,
    val type: TransactionType,
    val quantity: Double,
    val price: Double,
    val amount: Double,
    val tradeDate: Long,
)

@Composable
fun TransactionCard(
    item: TransactionDisplayItem,
    onDelete: (() -> Unit)? = null,
    isDuplicate: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        TransactionDeleteDialog(
            item = item,
            onConfirm = {
                showDeleteDialog = false
                onDelete?.invoke()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }

    val isPressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed.value) 0.97f else 1f, label = "scale")

    val borderColor =
        when {
            isDuplicate -> GainRed
            isPressed.value -> GainRed.copy(alpha = 0.5f)
            else -> Border
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .scale(scale)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isPressed.value) Card.copy(alpha = 0.8f) else Card)
                .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                .pointerInput(Unit) {
                    if (onDelete != null) {
                        detectTapGestures(
                            onPress = {
                                isPressed.value = true
                                tryAwaitRelease()
                                isPressed.value = false
                            },
                            onLongPress = {
                                isPressed.value = false
                                showDeleteDialog = true
                            },
                        )
                    }
                },
    ) {
        TransactionCardHeader(item = item)
        if (item.type != TransactionType.DIVIDEND) {
            TransactionCardDetails(item = item)
        }
    }
}

@Composable
private fun TransactionCardHeader(item: TransactionDisplayItem) {
    val typeColor =
        when (item.type) {
            TransactionType.BUY -> gainColor
            TransactionType.SELL -> lossColor
            TransactionType.DIVIDEND -> Gold
        }
    val typeBgColor =
        when (item.type) {
            TransactionType.BUY -> gainDimColor
            TransactionType.SELL -> lossDimColor
            TransactionType.DIVIDEND -> GoldDim
        }
    val typeLabel =
        when (item.type) {
            TransactionType.BUY -> stringResource(Res.string.trade_type_buy)
            TransactionType.SELL -> stringResource(Res.string.trade_type_sell)
            TransactionType.DIVIDEND -> stringResource(Res.string.trade_type_dividend)
        }
    val amountPrefix = if (item.type == TransactionType.BUY) "-" else "+"

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
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
                    modifier =
                        Modifier
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
                    text = item.pinYin.ifBlank { item.code },
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
                text = "$amountPrefix${item.amount.formatLocalized()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = typeColor,
            )
            Text(
                text = item.tradeDate.formatLocalizedDate(),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun TransactionCardDetails(item: TransactionDisplayItem) {
    val fee =
        when (item.type) {
            TransactionType.BUY -> item.amount - item.price * item.quantity
            TransactionType.SELL -> item.price * item.quantity - item.amount
            else -> 0.0
        }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Border),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DetailItem(
                label = stringResource(Res.string.trade_quantity_label),
                value = stringResource(Res.string.trade_shares_format, item.quantity.toInt()),
            )
            DetailItem(
                label = stringResource(Res.string.trade_price_label),
                value = item.price.formatLocalized(),
            )
            DetailItem(
                label = stringResource(Res.string.trade_fee_label),
                value = fee.formatLocalized(),
                valueColor = TextSecondary,
            )
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
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
private fun TransactionDeleteDialog(
    item: TransactionDisplayItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val typeLabel =
        when (item.type) {
            TransactionType.BUY -> stringResource(Res.string.trade_type_buy)
            TransactionType.SELL -> stringResource(Res.string.trade_type_sell)
            TransactionType.DIVIDEND -> stringResource(Res.string.trade_type_dividend)
        }
    val typeColor =
        when (item.type) {
            TransactionType.BUY -> gainColor
            TransactionType.SELL -> lossColor
            TransactionType.DIVIDEND -> Gold
        }
    val typeBgColor =
        when (item.type) {
            TransactionType.BUY -> gainDimColor
            TransactionType.SELL -> lossDimColor
            TransactionType.DIVIDEND -> GoldDim
        }

    ConfirmDialog(
        title = stringResource(Res.string.confirm_delete),
        confirmText = stringResource(Res.string.delete),
        dismissText = stringResource(Res.string.cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
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
                        modifier =
                            Modifier
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
                        text = item.name,
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

@Preview
@Composable
private fun TransactionCardPreview() {
    val tradeDate =
        LocalDateTime(2026, 1, 1, 10, 0, 0)
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .background(Background),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TransactionCard(
            item =
                TransactionDisplayItem(
                    name = "Kweichow Moutai",
                    code = "SH600519",
                    pinYin = "GZMT",
                    type = TransactionType.BUY,
                    quantity = 100.0,
                    price = 1800.00,
                    amount = 180500.00,
                    tradeDate = tradeDate,
                ),
        )
        TransactionCard(
            item =
                TransactionDisplayItem(
                    name = "Kweichow Moutai",
                    code = "SH600519",
                    pinYin = "GZMT",
                    type = TransactionType.SELL,
                    quantity = 100.0,
                    price = 1850.00,
                    amount = 184500.00,
                    tradeDate = tradeDate,
                ),
        )
        TransactionCard(
            item =
                TransactionDisplayItem(
                    name = "Kweichow Moutai",
                    code = "SH600519",
                    pinYin = "GZMT",
                    type = TransactionType.DIVIDEND,
                    quantity = 0.0,
                    price = 0.0,
                    amount = 200.00,
                    tradeDate = tradeDate,
                ),
        )
    }
}
