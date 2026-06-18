package com.yoke.gainful.data.model

import com.yoke.gainful.model.StockTrend
import com.yoke.gainful.network.model.TrendData

fun TrendData.toStockTrendList(): List<StockTrend> {
    return data.map { item ->
        StockTrend(
            time = item.timestamp?.toString() ?: "",
            price = (item.price ?: 0) / 100.0,
            volume = 0,
            averagePrice = 0.0,
        )
    }
}
