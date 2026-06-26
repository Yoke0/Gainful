package com.yoke.gainful.feature.holdings.detail

import com.yoke.gainful.model.ChartPeriod

sealed interface StockDetailIntent {
    data object LoadData : StockDetailIntent
    data object Retry : StockDetailIntent
    data class SelectPeriod(val period: ChartPeriod) : StockDetailIntent
}
