package com.yoke.gainful.feature.transactions.add

import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.TransactionType

sealed interface AddTransactionIntent {
    data class SelectType(val type: TransactionType) : AddTransactionIntent

    data object ToggleSearch : AddTransactionIntent

    data class SearchQueryChanged(val query: String) : AddTransactionIntent

    data class SelectAssetFromHolding(val holding: HoldingDisplay) : AddTransactionIntent

    data class SelectAsset(val asset: Asset) : AddTransactionIntent

    data object ClearAsset : AddTransactionIntent

    data class AmountChanged(val amount: String) : AddTransactionIntent

    data class PriceChanged(val price: String) : AddTransactionIntent

    data class QuantityChanged(val quantity: String) : AddTransactionIntent

    data class DateTimeChanged(val dateTimeMillis: Long) : AddTransactionIntent

    data object ShowCalendar : AddTransactionIntent

    data object HideCalendar : AddTransactionIntent

    data object SaveTransaction : AddTransactionIntent
}
