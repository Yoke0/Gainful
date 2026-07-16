package com.yoke.gainful.data.model

import com.yoke.gainful.model.StockTrend
import com.yoke.gainful.network.eastmoney.TrendData

private val TRADING_PERIODS = listOf(930 to 1130, 1300 to 1500)

fun TrendData.toStockTrendList(): List<StockTrend> {
    return data
        .map { item ->
            StockTrend(
                time = item.timestamp?.toString() ?: "",
                price = (item.price ?: 0) / 100.0,
            )
        }
        .filter { trend ->
            val ts = trend.time.toLongOrNull() ?: return@filter true
            val hhmm = (ts % 10000).toInt()
            TRADING_PERIODS.any { (start, end) -> hhmm in start..end }
        }
}
