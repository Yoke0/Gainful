package com.yoke.gainful.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.common.extensions.formatSigned
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.model.StockPnlDetail
import com.yoke.gainful.ui.gainColor
import com.yoke.gainful.ui.lossColor
import gainful.feature.dashboard.generated.resources.Res
import gainful.feature.dashboard.generated.resources.pnl_detail_buy_fee
import gainful.feature.dashboard.generated.resources.pnl_detail_buy_fee_total
import gainful.feature.dashboard.generated.resources.pnl_detail_dividend
import gainful.feature.dashboard.generated.resources.pnl_detail_dividend_total
import gainful.feature.dashboard.generated.resources.pnl_detail_gain
import gainful.feature.dashboard.generated.resources.pnl_detail_gain_total
import gainful.feature.dashboard.generated.resources.pnl_detail_no_records
import gainful.feature.dashboard.generated.resources.pnl_detail_non_trading_day
import gainful.feature.dashboard.generated.resources.pnl_detail_sell_fee
import gainful.feature.dashboard.generated.resources.pnl_detail_sell_fee_total
import gainful.feature.dashboard.generated.resources.pnl_detail_total_pnl
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StockPnlDetailDialog(
    details: List<StockPnlDetail>,
    isNonTradingDay: Boolean = false,
    onDismiss: () -> Unit,
) {
    val sheetState =
        rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
        )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Background,
        contentColor = TextPrimary,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp)
                    .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (details.isEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text =
                            if (isNonTradingDay) {
                                stringResource(Res.string.pnl_detail_non_trading_day)
                            } else {
                                stringResource(Res.string.pnl_detail_no_records)
                            },
                        fontSize = 14.sp,
                        color = TextMuted,
                    )
                }
            } else {
                details.forEach { detail ->
                    StockPnlDetailItem(detail)
                }

                // Summary card
                StockPnlDetailSummary(details)
            }
        }
    }
}

@Composable
internal fun StockPnlDetailItem(detail: StockPnlDetail) {
    val iconColors =
        listOf(
            Color(0xFF3B82F6) to Color(0xFF1E3A5F),
            Color(0xFF8B5CF6) to Color(0xFF3B1F6E),
            Color(0xFFF59E0B) to Color(0xFF5C3D0A),
            Color(0xFF14B8A6) to Color(0xFF0D4F47),
            Color(0xFFEC4899) to Color(0xFF5C1A3D),
            Color(0xFF6366F1) to Color(0xFF2E2D6E),
            Color(0xFFF43F5E) to Color(0xFF5C1428),
        )
    val colorIndex = (detail.assetId.hashCode() and Int.MAX_VALUE) % iconColors.size
    val (iconBg, iconFg) = iconColors[colorIndex]
    val abbreviation = detail.stockName.take(2)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(14.dp))
                .padding(18.dp),
    ) {
        // Stock header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = abbreviation,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconFg,
                )
            }

            // Name + code
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = detail.stockName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = detail.assetId,
                    fontSize = 13.sp,
                    color = TextMuted,
                )
            }

            // Total PnL
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(Res.string.pnl_detail_total_pnl),
                    fontSize = 13.sp,
                    color = TextMuted,
                )
                Text(
                    text = detail.pnl.formatSigned(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (detail.pnl >= 0) gainColor else lossColor,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Detail grid — only show non-zero items
        val gridItems =
            listOf(
                Triple(
                    stringResource(Res.string.pnl_detail_buy_fee),
                    if (detail.buyFee > 0) "-${detail.buyFee.formatLocalized()}" else null,
                    if (detail.buyFee > 0) lossColor else null,
                ),
                Triple(
                    stringResource(Res.string.pnl_detail_sell_fee),
                    if (detail.sellFee > 0) "-${detail.sellFee.formatLocalized()}" else null,
                    if (detail.sellFee > 0) lossColor else null,
                ),
                Triple(
                    stringResource(Res.string.pnl_detail_dividend),
                    if (detail.dividend > 0) "+${detail.dividend.formatLocalized()}" else null,
                    if (detail.dividend > 0) gainColor else null,
                ),
                Triple(
                    stringResource(Res.string.pnl_detail_gain),
                    detail.dailyPnl.formatSigned(),
                    if (detail.dailyPnl > 0) {
                        gainColor
                    } else if (detail.dailyPnl < 0) {
                        lossColor
                    } else {
                        null
                    },
                ),
            ).filter { it.third != null }

        if (gridItems.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                gridItems.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { (label, value, color) ->
                            PnlDetailGridItem(
                                modifier = Modifier.weight(1f),
                                label = label,
                                value = value.orEmpty(),
                                color = color ?: TextSecondary,
                            )
                        }
                        if (row.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun PnlDetailGridItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Surface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

@Composable
internal fun StockPnlDetailSummary(details: List<StockPnlDetail>) {
    val totalBuyFee = details.sumOf { it.buyFee }
    val totalSellFee = details.sumOf { it.sellFee }
    val totalDividend = details.sumOf { it.dividend }
    val totalDailyGain = details.sumOf { it.dailyPnl }
    val totalPnl = details.sumOf { it.pnl }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, Gold, RoundedCornerShape(14.dp))
                .background(GoldDim)
                .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (totalBuyFee > 0) {
            SummaryRow(
                label = stringResource(Res.string.pnl_detail_buy_fee_total),
                value = "-${totalBuyFee.formatLocalized()}",
                color = lossColor,
            )
        }
        if (totalSellFee > 0) {
            SummaryRow(
                label = stringResource(Res.string.pnl_detail_sell_fee_total),
                value = "-${totalSellFee.formatLocalized()}",
                color = lossColor,
            )
        }
        if (totalDividend > 0) {
            SummaryRow(
                label = stringResource(Res.string.pnl_detail_dividend_total),
                value = "+${totalDividend.formatLocalized()}",
                color = gainColor,
            )
        }
        SummaryRow(
            label = stringResource(Res.string.pnl_detail_gain_total),
            value = totalDailyGain.formatSigned(),
            color = if (totalDailyGain >= 0) gainColor else lossColor,
        )

        // Divider
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Gold.copy(alpha = 0.2f)),
        )

        // Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.pnl_detail_total_pnl),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = totalPnl.formatSigned(),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (totalPnl >= 0) gainColor else lossColor,
            )
        }
    }
}

@Composable
internal fun SummaryRow(
    label: String,
    value: String,
    color: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary,
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}
