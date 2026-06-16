package com.yoke.gainful.feature.transactions

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yoke.gainful.common.extensions.formatTwoDecimals
import com.yoke.gainful.model.Asset
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
import kotlinx.coroutines.launch

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    todayDate: String,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (uiState.date.isBlank()) {
            viewModel.onDateChanged(todayDate)
        }
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        AddTransactionHeader(
            onBack = onBack,
            onSave = {
                scope.launch { viewModel.saveTransaction() }
            },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            TypeSelector(
                selectedType = uiState.type,
                onTypeSelected = viewModel::onTypeSelected,
            )

            Spacer(modifier = Modifier.height(24.dp))

            AssetSelectorSection(
                searchQuery = uiState.searchQuery,
                suggestions = uiState.suggestions,
                showSuggestions = uiState.showSuggestions,
                selectedAsset = uiState.selectedAsset,
                onQueryChanged = viewModel::onSearchQueryChanged,
                onAssetSelected = viewModel::onAssetSelected,
                onAssetCleared = viewModel::onAssetCleared,
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.type == TransactionType.DIVIDEND) {
                DividendFields(
                    amount = uiState.amount,
                    date = uiState.date,
                    onAmountChanged = viewModel::onAmountChanged,
                    onDateChanged = viewModel::onDateChanged,
                )
            } else {
                TradeFields(
                    amount = uiState.amount,
                    price = uiState.price,
                    quantity = uiState.quantity,
                    fee = viewModel.computeFee(),
                    date = uiState.date,
                    onAmountChanged = viewModel::onAmountChanged,
                    onPriceChanged = viewModel::onPriceChanged,
                    onQuantityChanged = viewModel::onQuantityChanged,
                    onDateChanged = viewModel::onDateChanged,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TransactionSummary(
                type = uiState.type,
                asset = uiState.selectedAsset,
                amount = uiState.amount,
                fee = if (uiState.type != TransactionType.DIVIDEND) viewModel.computeFee() else "",
                date = uiState.date,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AddTransactionHeader(
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onBack)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "\u2039",
                fontSize = 22.sp,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "\u8FD4\u56DE",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
            )
        }

        Text(
            text = "\u65B0\u589E\u4EA4\u6613",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Gold)
                .clickable(onClick = onSave)
                .padding(horizontal = 18.dp, vertical = 6.dp),
        ) {
            Text(
                text = "\u4FDD\u5B58",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Background,
            )
        }
    }
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
        TypeButton(
            label = "\u4E70\u5165",
            icon = "\uD83D\uDCC8",
            isSelected = selectedType == TransactionType.BUY,
            activeColor = GainGreen,
            activeBackground = GreenDim,
            modifier = Modifier.weight(1f),
            onClick = { onTypeSelected(TransactionType.BUY) },
        )
        TypeButton(
            label = "\u5356\u51FA",
            icon = "\uD83D\uDCC9",
            isSelected = selectedType == TransactionType.SELL,
            activeColor = GainRed,
            activeBackground = RedDim,
            modifier = Modifier.weight(1f),
            onClick = { onTypeSelected(TransactionType.SELL) },
        )
        TypeButton(
            label = "\u80A1\u606F",
            icon = "\uD83D\uDCB5",
            isSelected = selectedType == TransactionType.DIVIDEND,
            activeColor = Gold,
            activeBackground = GoldDim,
            modifier = Modifier.weight(1f),
            onClick = { onTypeSelected(TransactionType.DIVIDEND) },
        )
    }
}

@Composable
private fun TypeButton(
    label: String,
    icon: String,
    isSelected: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    activeBackground: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) activeBackground else Surface
    val borderColor = if (isSelected) activeColor else Border
    val textColor = if (isSelected) activeColor else TextSecondary

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )
    }
}

@Composable
private fun AssetSelectorSection(
    searchQuery: String,
    suggestions: List<Asset>,
    showSuggestions: Boolean,
    selectedAsset: Asset?,
    onQueryChanged: (String) -> Unit,
    onAssetSelected: (Asset) -> Unit,
    onAssetCleared: () -> Unit,
) {
    SectionLabel("\u6807\u7684\u8D44\u4EA7")

    Box {
        AssetSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
        )

        if (showSuggestions && suggestions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(10.dp)),
            ) {
                suggestions.take(6).forEach { asset ->
                    SuggestionItem(
                        asset = asset,
                        onClick = { onAssetSelected(asset) },
                    )
                }
            }
        }

        if (showSuggestions && suggestions.isEmpty() && searchQuery.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Card)
                    .border(1.dp, Border, RoundedCornerShape(10.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u672A\u627E\u5230\u5339\u914D\u7684\u80A1\u7968",
                    fontSize = 15.sp,
                    color = TextMuted,
                )
            }
        }
    }

    if (selectedAsset != null) {
        Spacer(modifier = Modifier.height(8.dp))
        SelectedAssetTag(
            asset = selectedAsset,
            onRemove = onAssetCleared,
        )
    }
}

@Composable
private fun AssetSearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
) {
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
            text = "\uD83D\uDD0D",
            fontSize = 16.sp,
            color = TextMuted,
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.weight(1f).padding(vertical = 8.dp),
            textStyle = TextStyle(
                fontSize = 17.sp,
                color = TextPrimary,
            ),
            singleLine = true,
            cursorBrush = SolidColor(Gold),
            decorationBox = { innerTextField ->
                if (query.isBlank()) {
                    Text(
                        text = "\u641C\u7D22\u80A1\u7968\u540D\u79F0\u6216\u4EE3\u7801\u2026",
                        fontSize = 17.sp,
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
                    text = "\u2715",
                    fontSize = 12.sp,
                    color = TextMuted,
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    asset: Asset,
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
        Column {
            Text(
                text = asset.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Text(
                text = asset.jys,
                fontSize = 13.sp,
                color = TextMuted,
            )
        }
        Text(
            text = asset.code,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            color = TextSecondary,
        )
    }
}

@Composable
private fun SelectedAssetTag(
    asset: Asset,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(GoldDim)
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "\uD83D\uDCC8", fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = asset.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Text(
                text = "${asset.code} \u00B7 ${asset.jys}",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted,
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = onRemove)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\u2715",
                fontSize = 12.sp,
                color = TextMuted,
            )
        }
    }
}

@Composable
private fun TradeFields(
    amount: String,
    price: String,
    quantity: String,
    fee: String,
    date: String,
    onAmountChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onQuantityChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
) {
    SectionLabel("\u4EA4\u6613\u8BE6\u60C5")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormField(
            label = "\u6210\u4EA4\u91D1\u989D",
            value = amount,
            onValueChange = onAmountChanged,
            prefix = "\u00A5",
            placeholder = "\u8F93\u5165\u5B9E\u9645\u6210\u4EA4\u603B\u91D1\u989D",
            modifier = Modifier.weight(1f),
            hint = "\u5B9E\u9645\u6210\u4EA4\u603B\u91D1\u989D\uFF08\u5DF2\u542B\u624B\u7EED\u8D39\uFF09",
        )
        FormField(
            label = "\u624B\u7EED\u8D39\uFF08\u81EA\u52A8\u8BA1\u7B97\uFF09",
            value = fee,
            onValueChange = {},
            prefix = "\u00A5",
            placeholder = "= \u91D1\u989D \u2212 \u4EF7\u00D7\u91CF",
            readOnly = true,
            modifier = Modifier.weight(1f),
            hint = "\u624B\u7EED\u8D39 = \u6210\u4EA4\u91D1\u989D \u2212 \u6210\u4EA4\u4EF7 \u00D7 \u6210\u4EA4\u91CF",
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormField(
            label = "\u6210\u4EA4\u4EF7",
            value = price,
            onValueChange = onPriceChanged,
            prefix = "\u00A5",
            placeholder = "0.00",
            modifier = Modifier.weight(1f),
        )
        FormField(
            label = "\u6210\u4EA4\u91CF",
            value = quantity,
            onValueChange = onQuantityChanged,
            suffix = "\u80A1",
            placeholder = "0",
            modifier = Modifier.weight(1f),
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    FormField(
        label = "\u4EA4\u6613\u65E5\u671F",
        value = date,
        onValueChange = onDateChanged,
        placeholder = "YYYY-MM-DD",
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DividendFields(
    amount: String,
    date: String,
    onAmountChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
) {
    SectionLabel("\u80A1\u606F\u8BE6\u60C5")

    FormField(
        label = "\u80A1\u606F\u91D1\u989D",
        value = amount,
        onValueChange = onAmountChanged,
        prefix = "\u00A5",
        placeholder = "\u8F93\u5165\u80A1\u606F\u91D1\u989D",
        modifier = Modifier.fillMaxWidth(),
        hint = "\u5B9E\u9645\u5230\u8D26\u91D1\u989D",
    )

    Spacer(modifier = Modifier.height(12.dp))

    FormField(
        label = "\u5230\u8D26\u65E5\u671F",
        value = date,
        onValueChange = onDateChanged,
        placeholder = "YYYY-MM-DD",
        modifier = Modifier.fillMaxWidth(),
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
) {
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
                .border(1.dp, Border, RoundedCornerShape(6.dp))
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
    date: String,
) {
    val hasContent = asset != null || amount.isNotBlank()
    if (!hasContent) return

    val typeLabel = when (type) {
        TransactionType.BUY -> "\u4E70\u5165"
        TransactionType.SELL -> "\u5356\u51FA"
        TransactionType.DIVIDEND -> "\u80A1\u606F"
    }

    val amountVal = amount.toDoubleOrNull() ?: 0.0
    val pnlText = when {
        amountVal <= 0 -> "\u2014"
        type == TransactionType.SELL -> "+\u00A5${amountVal.formatTwoDecimals()}"
        type == TransactionType.DIVIDEND -> "+\u00A5${amountVal.formatTwoDecimals()}"
        else -> "-\u00A5${amountVal.formatTwoDecimals()}"
    }
    val pnlColor = when {
        amountVal <= 0 -> TextPrimary
        type == TransactionType.SELL -> GainGreen
        type == TransactionType.DIVIDEND -> GainGreen
        else -> GainRed
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
            text = "\u4EA4\u6613\u6982\u89C8",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            letterSpacing = 0.06.em,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryItem("\u7C7B\u578B", typeLabel, Modifier.weight(1f))
                SummaryItem(
                    "\u6807\u7684",
                    asset?.let { "${it.code} ${it.name}" } ?: "\u2014",
                    Modifier.weight(1f),
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryItem(
                    "\u91D1\u989D",
                    if (amountVal > 0) "\u00A5${amountVal.formatTwoDecimals()}" else "\u2014",
                    Modifier.weight(1f),
                )
                SummaryItem(
                    "\u624B\u7EED\u8D39",
                    if (fee.isNotBlank() && fee != "0.00") "\u00A5$fee" else "\u2014",
                    Modifier.weight(1f),
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryItem("\u76C8\u4E8F", pnlText, Modifier.weight(1f), valueColor = pnlColor)
                SummaryItem(
                    "\u65E5\u671F",
                    date.ifBlank { "\u2014" },
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
