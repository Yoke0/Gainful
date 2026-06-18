package com.yoke.gainful.feature.holdings.detail

import com.yoke.gainful.model.ChartPeriod

sealed interface StockDetailIntent {
    data object LoadData : StockDetailIntent
    data class LoadChart(val period: ChartPeriod) : StockDetailIntent
}
