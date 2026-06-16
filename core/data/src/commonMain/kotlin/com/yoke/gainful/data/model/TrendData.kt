package com.yoke.gainful.data.model

import com.yoke.gainful.model.StockTrend
import com.yoke.gainful.network.model.TrendData

fun TrendData.toStockTrendList(): List<StockTrend> {
    return trends.map { line ->
        val parts = line.split(",")
        StockTrend(
            time = parts[0],
            price = parts.getOrElse(1) { "0" }.toDouble(),
            volume = parts.getOrElse(2) { "0" }.toInt(),
            averagePrice = parts.getOrElse(3) { "0" }.toDouble(),
        )
    }
}
