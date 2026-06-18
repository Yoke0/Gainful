package com.yoke.gainful.feature.holdings.overview

sealed interface HoldingsIntent {
    data object LoadHoldings : HoldingsIntent
    data object Refresh : HoldingsIntent
}
