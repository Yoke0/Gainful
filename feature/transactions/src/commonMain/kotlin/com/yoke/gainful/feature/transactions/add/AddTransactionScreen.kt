package com.yoke.gainful.feature.transactions.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yoke.gainful.common.extensions.formatLocalized
import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.components.BackNavigationIcon
import com.yoke.gainful.ui.components.DateTimePickerDialog
import com.yoke.gainful.ui.components.DateTimePickerField
import com.yoke.gainful.ui.components.GainfulTopAppBar
import com.yoke.gainful.ui.components.SelectChip
import com.yoke.gainful.ui.components.SquareIconButton
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
import gainful.feature.transactions.generated.resources.add_transaction_title
import gainful.feature.transactions.generated.resources.asset_section
import gainful.feature.transactions.generated.resources.buy
import gainful.feature.transactions.generated.resources.dividend
import gainful.feature.transactions.generated.resources.dividend_amount
import gainful.feature.transactions.generated.resources.dividend_amount_hint
import gainful.feature.transactions.generated.resources.dividend_amount_placeholder
import gainful.feature.transactions.generated.resources.dividend_date
import gainful.feature.transactions.generated.resources.dividend_details_section
import gainful.feature.transactions.generated.resources.existing_holdings
import gainful.feature.transactions.generated.resources.fee_label
import gainful.feature.transactions.generated.resources.holding_badge
import gainful.feature.transactions.generated.resources.no_match_stock
import gainful.feature.transactions.generated.resources.no_selection
import gainful.feature.transactions.generated.resources.quantity_unit
import gainful.feature.transactions.generated.resources.save
import gainful.feature.transactions.generated.resources.search_stock_placeholder
import gainful.feature.transactions.generated.resources.sell
import gainful.feature.transactions.generated.resources.summary_amount
import gainful.feature.transactions.generated.resources.summary_asset
import gainful.feature.transactions.generated.resources.summary_date
import gainful.feature.transactions.generated.resources.summary_fee
import gainful.feature.transactions.generated.resources.summary_pnl
import gainful.feature.transactions.generated.resources.summary_type
import gainful.feature.transactions.generated.resources.trade_amount
import gainful.feature.transactions.generated.resources.trade_amount_placeholder
import gainful.feature.transactions.generated.resources.trade_date
import gainful.feature.transactions.generated.resources.trade_details_section
import gainful.feature.transactions.generated.resources.trade_price
import gainful.feature.transactions.generated.resources.trade_quantity
import gainful.feature.transactions.generated.resources.transaction_summary
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        AddTransactionHeader(
            onBack = onBack,
            isEnabled = uiState.canSave,
            onSave = {
                viewModel.onIntent(AddTransactionIntent.SaveTransaction)
            },
        )

        AnimatedVisibility(visible = uiState.showCalendar) {
            DateTimePickerDialog(
                initialSelectedDateTimeMillis = uiState.dateTimeMillis,
                selectableToTodayOnly = true,
                onDateTimeSelected = {
                    viewModel.onIntent(AddTransactionIntent.DateTimeChanged(it))
                    viewModel.onIntent(AddTransactionIntent.HideCalendar)
                },
                onDismiss = { viewModel.onIntent(AddTransactionIntent.HideCalendar) },
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            TypeSelector(
                selectedType = uiState.type,
                onTypeSelected = { viewModel.onIntent(AddTransactionIntent.SelectType(it)) },
            )

            Spacer(modifier = Modifier.height(24.dp))

            AssetSelectorSection(
                selectedAsset = uiState.selectedAsset,
                showSearch = uiState.showSearch,
                searchQuery = uiState.searchQuery,
                suggestions = uiState.suggestions,
                showSuggestions = uiState.showSuggestions,
                holdings = uiState.holdings,
                type = uiState.type,
                onToggleSearch = { viewModel.onIntent(AddTransactionIntent.ToggleSearch) },
                onQueryChanged = { viewModel.onIntent(AddTransactionIntent.SearchQueryChanged(it)) },
                onAssetSelected = { viewModel.onIntent(AddTransactionIntent.SelectAsset(it)) },
                onAssetSelectedFromHolding = { viewModel.onIntent(AddTransactionIntent.SelectAssetFromHolding(it)) },
                onAssetCleared = { viewModel.onIntent(AddTransactionIntent.ClearAsset) },
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.type == TransactionType.DIVIDEND) {
                DividendFields(
                    amount = uiState.amount,
                    dateTimeMillis = uiState.dateTimeMillis,
                    onAmountChanged = { viewModel.onIntent(AddTransactionIntent.AmountChanged(it)) },
                    onDateClicked = { viewModel.onIntent(AddTransactionIntent.ShowCalendar) },
                    amountError = uiState.amountError,
                )
            } else {
                TradeFields(
                    type = uiState.type,
                    amount = uiState.amount,
                    price = uiState.price,
                    quantity = uiState.quantity,
                    fee = viewModel.computeFee(),
                    dateTimeMillis = uiState.dateTimeMillis,
                    onAmountChanged = { viewModel.onIntent(AddTransactionIntent.AmountChanged(it)) },
                    onPriceChanged = { viewModel.onIntent(AddTransactionIntent.PriceChanged(it)) },
                    onQuantityChanged = { viewModel.onIntent(AddTransactionIntent.QuantityChanged(it)) },
                    onDateClicked = { viewModel.onIntent(AddTransactionIntent.ShowCalendar) },
                    amountError = uiState.amountError,
                    priceError = uiState.priceError,
                    quantityError = uiState.quantityError,
                    feeError = uiState.feeError,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TransactionSummary(
                type = uiState.type,
                asset = uiState.selectedAsset,
                amount = uiState.amount,
                fee = if (uiState.type != TransactionType.DIVIDEND) viewModel.computeFee() else "",
                dateTimeMillis = uiState.dateTimeMillis,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AddTransactionHeader(
    onBack: () -> Unit,
    isEnabled: Boolean = true,
    onSave: () -> Unit,
) {
    val saveAlpha = if (isEnabled) 1f else 0.4f
    GainfulTopAppBar(
        title = stringResource(Res.string.add_transaction_title),
        navigationIcon = { BackNavigationIcon(onClick = onBack) },
        actions = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Gold.copy(alpha = saveAlpha))
                    .clickable(enabled = isEnabled, onClick = onSave)
                    .padding(horizontal = 18.dp, vertical = 6.dp),
            ) {
                Text(
                    text = stringResource(Res.string.save),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Background,
                )
            }
        },
    )
}

@Composable
private fun TypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectChip(
            label = stringResource(Res.string.buy),
            icon = "📈",
            isSelected = selectedType == TransactionType.BUY,
            activeColor = gainColor,
            activeBackground = gainDimColor,
            modifier = Modifier.weight(1f),
            onClick = { onTypeSelected(TransactionType.BUY) },
        )
        SelectChip(
            label = stringResource(Res.string.sell),
            icon = "📉",
            isSelected = selectedType == TransactionType.SELL,
            activeColor = lossColor,
            activeBackground = lossDimColor,
            modifier = Modifier.weight(1f),
            onClick = { onTypeSelected(TransactionType.SELL) },
        )
        SelectChip(
            label = stringResource(Res.string.dividend),
            icon = "💵",
            isSelected = selectedType == TransactionType.DIVIDEND,
            activeColor = Gold,
            activeBackground = GoldDim,
            modifier = Modifier.weight(1f),
            onClick = { onTypeSelected(TransactionType.DIVIDEND) },
        )
    }
}

@Composable
private fun AssetSelectorSection(
    selectedAsset: Asset?,
    showSearch: Boolean,
    searchQuery: String,
    suggestions: List<Asset>,
    showSuggestions: Boolean,
    holdings: List<HoldingDisplay>,
    type: TransactionType,
    onToggleSearch: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onAssetSelected: (Asset) -> Unit,
    onAssetSelectedFromHolding: (HoldingDisplay) -> Unit,
    onAssetCleared: () -> Unit,
) {
    SectionLabel(stringResource(Res.string.asset_section))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (selectedAsset != null) {
            SelectedStockInfo(
                asset = selectedAsset,
                onRemove = onAssetCleared,
                modifier = Modifier.weight(1f),
            )
        } else {
            StockPlaceholder(
                modifier = Modifier.weight(1f),
            )
        }

        if (type == TransactionType.BUY) {
            SquareIconButton(
                icon = if (showSearch) "✕" else "🔍",
                onClick = onToggleSearch,
            )
        }
    }

    if (showSearch) {
        Spacer(modifier = Modifier.height(10.dp))
        AssetSearchExpandable(
            query = searchQuery,
            suggestions = suggestions,
            showSuggestions = showSuggestions,
            holdings = holdings,
            onQueryChanged = onQueryChanged,
            onAssetSelected = onAssetSelected,
        )
    }

    if (!showSearch && holdings.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        HoldingsQuickSelect(
            holdings = holdings,
            onHoldingSelected = onAssetSelectedFromHolding,
        )
    }
}

@Composable
private fun StockPlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = stringResource(Res.string.no_selection),
            fontSize = 15.sp,
            color = TextMuted,
        )
    }
}

@Composable
private fun SelectedStockInfo(
    asset: Asset,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(GoldDim)
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = asset.pinYin.ifBlank { asset.code },
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = asset.name,
            fontSize = 13.sp,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = onRemove)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "✕",
                fontSize = 12.sp,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun AssetSearchExpandable(
    query: String,
    suggestions: List<Asset>,
    showSuggestions: Boolean,
    holdings: List<HoldingDisplay>,
    onQueryChanged: (String) -> Unit,
    onAssetSelected: (Asset) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "🔍",
                fontSize = 18.sp,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = TextPrimary,
                ),
                singleLine = true,
                cursorBrush = SolidColor(Gold),
                decorationBox = { innerTextField ->
                    if (query.isBlank()) {
                        Text(
                            text = stringResource(Res.string.search_stock_placeholder),
                            fontSize = 15.sp,
                            color = TextMuted,
                        )
                    }
                    innerTextField()
                },
            )
            if (query.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { onQueryChanged("") }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "✕",
                        fontSize = 12.sp,
                        color = TextMuted,
                    )
                }
            }
        }

        if (showSuggestions && suggestions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(10.dp)),
            ) {
                suggestions.take(6).forEach { asset ->
                    SuggestionItem(
                        asset = asset,
                        isHolding = holdings.any { it.code == asset.code },
                        onClick = { onAssetSelected(asset) },
                    )
                }
            }
        }

        if (showSuggestions && suggestions.isEmpty() && query.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(10.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.no_match_stock),
                    fontSize = 15.sp,
                    color = TextMuted,
                )
            }
        }

        if (!showSuggestions && holdings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            HoldingsQuickSelect(
                holdings = holdings,
                onHoldingSelected = { holding ->
                    val asset = Asset(
                        innerCode = holding.code,
                        code = holding.code,
                        name = holding.name,
                        pinYin = holding.pinYin,
                        id = holding.assetId,
                        jys = "",
                        classify = "",
                        marketType = "",
                        typeName = "",
                        securityType = "",
                        market = 0,
                        typeUS = "",
                        quoteId = "",
                        unifiedCode = holding.assetId,
                    )
                    onAssetSelected(asset)
                },
            )
        }
    }
}

@Composable
private fun SuggestionItem(
    asset: Asset,
    isHolding: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = asset.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    if (isHolding) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.holding_badge),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gold,
                            modifier = Modifier
                                .background(GoldDim, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 1.dp),
                        )
                    }
                }
                Text(
                    text = asset.typeName,
                    fontSize = 13.sp,
                    color = TextMuted,
                )
            }
        }
        Text(
            text = asset.pinYin.ifBlank { asset.code },
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            color = TextSecondary,
        )
    }
}

@Composable
private fun HoldingsQuickSelect(
    holdings: List<HoldingDisplay>,
    onHoldingSelected: (HoldingDisplay) -> Unit,
) {
    if (holdings.isEmpty()) return

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.existing_holdings),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 0.04.em,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${holdings.size}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier
                    .background(Surface, RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 1.dp),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            holdings.sortedByDescending { it.quantity }.forEach { holding ->
                HoldingChip(
                    holding = holding,
                    onClick = { onHoldingSelected(holding) },
                )
            }
        }
    }
}

@Composable
private fun HoldingChip(
    holding: HoldingDisplay,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, Border, RoundedCornerShape(50))
            .background(Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = holding.pinYin.ifBlank { holding.code },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = TextPrimary,
        )
        Text(
            text = holding.name,
            fontSize = 12.sp,
            color = TextSecondary,
        )
        Text(
            text = "${holding.quantity.formatQuantity()} 股",
            fontSize = 10.sp,
            color = TextMuted,
            fontFamily = FontFamily.Monospace,
        )
    }
}

private fun Double.formatQuantity(): String {
    val qty = this.toLong()
    return if (qty.toDouble() == this) qty.toString() else this.formatLocalized()
}

@Composable
private fun TradeFields(
    type: TransactionType,
    amount: String,
    price: String,
    quantity: String,
    fee: String,
    dateTimeMillis: Long,
    onAmountChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onQuantityChanged: (String) -> Unit,
    onDateClicked: () -> Unit,
    amountError: Boolean = false,
    priceError: Boolean = false,
    quantityError: Boolean = false,
    feeError: Boolean = false,
) {
    val isBuy = type == TransactionType.BUY
    val amountHint = if (isBuy) "实际成交总金额（已含手续费）" else "实际到账金额（已扣手续费）"
    val feePlaceholder = if (isBuy) "= 金额 − 价×量" else "= 价×量 − 金额"
    val feeHint = if (isBuy) "手续费 = 成交金额 − 成交价 × 成交量" else "手续费 = 成交价 × 成交量 − 成交金额"
    val decimalKeyboard = androidx.compose.foundation.text.KeyboardOptions(
        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
    )
    val integerKeyboard = androidx.compose.foundation.text.KeyboardOptions(
        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
    )

    SectionLabel(stringResource(Res.string.trade_details_section))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormField(
            label = stringResource(Res.string.trade_amount),
            value = amount,
            onValueChange = onAmountChanged,
            prefix = "¥",
            placeholder = stringResource(Res.string.trade_amount_placeholder),
            modifier = Modifier.weight(1f),
            hint = amountHint,
            isError = amountError,
            keyboardOptions = decimalKeyboard,
        )
        FormField(
            label = stringResource(Res.string.fee_label),
            value = fee,
            onValueChange = {},
            prefix = "¥",
            placeholder = feePlaceholder,
            readOnly = true,
            modifier = Modifier.weight(1f),
            hint = feeHint,
            isError = feeError,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormField(
            label = stringResource(Res.string.trade_price),
            value = price,
            onValueChange = onPriceChanged,
            prefix = "¥",
            placeholder = "0.00",
            modifier = Modifier.weight(1f),
            isError = priceError,
            keyboardOptions = decimalKeyboard,
        )
        FormField(
            label = stringResource(Res.string.trade_quantity),
            value = quantity,
            onValueChange = onQuantityChanged,
            suffix = stringResource(Res.string.quantity_unit),
            placeholder = "0",
            modifier = Modifier.weight(1f),
            isError = quantityError,
            keyboardOptions = integerKeyboard,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    DateTimePickerField(
        label = stringResource(Res.string.trade_date),
        dateTimeMillis = dateTimeMillis,
        onClick = onDateClicked,
    )
}

@Composable
private fun DividendFields(
    amount: String,
    dateTimeMillis: Long,
    onAmountChanged: (String) -> Unit,
    onDateClicked: () -> Unit,
    amountError: Boolean = false,
) {
    val decimalKeyboard = androidx.compose.foundation.text.KeyboardOptions(
        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
    )

    SectionLabel(stringResource(Res.string.dividend_details_section))

    FormField(
        label = stringResource(Res.string.dividend_amount),
        value = amount,
        onValueChange = onAmountChanged,
        prefix = "¥",
        placeholder = stringResource(Res.string.dividend_amount_placeholder),
        modifier = Modifier.fillMaxWidth(),
        hint = stringResource(Res.string.dividend_amount_hint),
        isError = amountError,
        keyboardOptions = decimalKeyboard,
    )

    Spacer(modifier = Modifier.height(12.dp))

    DateTimePickerField(
        label = stringResource(Res.string.dividend_date),
        dateTimeMillis = dateTimeMillis,
        onClick = onDateClicked,
    )
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    suffix: String? = null,
    placeholder: String = "",
    readOnly: Boolean = false,
    hint: String? = null,
    isError: Boolean = false,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
) {
    val borderColor = if (isError) GainRed else Border
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(if (readOnly) Surface2 else Surface)
                .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (prefix != null) {
                    Text(
                        text = prefix,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            fontSize = 15.sp,
                            color = TextMuted,
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = if (readOnly) TextSecondary else TextPrimary,
                        ),
                        singleLine = true,
                        readOnly = readOnly,
                        keyboardOptions = keyboardOptions,
                        cursorBrush = SolidColor(Gold),
                    )
                }
                if (suffix != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = suffix,
                        fontSize = 13.sp,
                        color = TextMuted,
                    )
                }
            }
        }
        if (hint != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = hint,
                fontSize = 13.sp,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun TransactionSummary(
    type: TransactionType,
    asset: Asset?,
    amount: String,
    fee: String,
    dateTimeMillis: Long,
) {
    val hasContent = asset != null || amount.isNotBlank()
    if (!hasContent) return

    val typeLabel = when (type) {
        TransactionType.BUY -> stringResource(Res.string.buy)
        TransactionType.SELL -> stringResource(Res.string.sell)
        TransactionType.DIVIDEND -> stringResource(Res.string.dividend)
    }

    val amountVal = amount.toDoubleOrNull() ?: 0.0
    val pnlText = when {
        amountVal <= 0 -> "—"
        type == TransactionType.SELL -> "+¥${amountVal.formatLocalized()}"
        type == TransactionType.DIVIDEND -> "+¥${amountVal.formatLocalized()}"
        else -> "-¥${amountVal.formatLocalized()}"
    }
    val pnlColor = when {
        amountVal <= 0 -> TextPrimary
        type == TransactionType.SELL -> gainColor
        type == TransactionType.DIVIDEND -> gainColor
        else -> lossColor
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(10.dp))
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.transaction_summary),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.06.em,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryItem(stringResource(Res.string.summary_type), typeLabel, Modifier.weight(1f))
                SummaryItem(
                    stringResource(Res.string.summary_asset),
                    asset?.let { "${it.pinYin.ifBlank { it.code }} ${it.name}" } ?: "—",
                    Modifier.weight(1f),
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryItem(
                    stringResource(Res.string.summary_amount),
                    if (amountVal > 0) "¥${amountVal.formatLocalized()}" else "—",
                    Modifier.weight(1f),
                )
                SummaryItem(
                    stringResource(Res.string.summary_fee),
                    if (fee.isNotBlank() && fee != "0.00") "¥$fee" else "—",
                    Modifier.weight(1f),
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryItem(stringResource(Res.string.summary_pnl), pnlText, Modifier.weight(1f), valueColor = pnlColor)
                SummaryItem(
                    stringResource(Res.string.summary_date),
                    run {
                        val dt = kotlin.time.Instant.fromEpochMilliseconds(dateTimeMillis).toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                        "${dt.date.year}-${dt.date.month.number.toString().padStart(2, '0')}-${dt.date.day.toString().padStart(2, '0')}"
                    },
                    Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextMuted,
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.06.em,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Border),
        )
    }
}
