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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import gainful.core.ui.generated.resources.Res
import gainful.core.ui.generated.resources.trade_fee_label
import gainful.core.ui.generated.resources.trade_price_label
import gainful.core.ui.generated.resources.trade_quantity_label
import gainful.core.ui.generated.resources.trade_shares_format
import gainful.core.ui.generated.resources.trade_type_buy
import gainful.core.ui.generated.resources.trade_type_dividend
import gainful.core.ui.generated.resources.trade_type_sell
import org.jetbrains.compose.resources.stringResource

data class TransactionDisplayItem(
    val name: String,
    val code: String,
    val pinYin: String,
    val type: Int,
    val quantity: Double,
    val price: Double,
    val amount: Double,
    val fee: Double,
    val dateStr: String,
    val showDetails: Boolean = true,
)

@Composable
fun TransactionCard(
    item: TransactionDisplayItem,
    onLongPress: (() -> Unit)? = null,
    isDuplicate: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val isBuy = item.type == 0
    val isSell = item.type == 1

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
        isBuy -> stringResource(Res.string.trade_type_buy)
        isSell -> stringResource(Res.string.trade_type_sell)
        else -> stringResource(Res.string.trade_type_dividend)
    }
    val amountPrefix = if (isBuy) "-" else "+"

    val borderColor = when {
        isDuplicate -> GainRed
        isPressed.value -> GainRed.copy(alpha = 0.5f)
        else -> Border
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isPressed.value) Card.copy(alpha = 0.8f) else Card)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .pointerInput(Unit) {
                if (onLongPress != null) {
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
                }
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
                    text = item.dateStr,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted,
                )
            }
        }

        if (item.showDetails && item.type != 2) {
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
                            text = stringResource(Res.string.trade_shares_format, item.quantity.toInt()),
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
                            text = item.price.formatLocalized(),
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
                            text = stringResource(Res.string.trade_fee_label),
                            fontSize = 11.sp,
                            color = TextMuted,
                        )
                        Text(
                            text = item.fee.formatLocalized(),
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
