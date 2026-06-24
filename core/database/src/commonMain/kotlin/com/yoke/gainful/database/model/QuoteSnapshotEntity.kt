package com.yoke.gainful.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yoke.gainful.model.StockTrend

@Entity(tableName = "quote_snapshots")
data class QuoteSnapshotEntity(
    @PrimaryKey
    @ColumnInfo(name = "quote_id") val quoteId: String,
    val code: String,
    val name: String,
    @ColumnInfo(name = "latest_price") val latestPrice: Double,
    @ColumnInfo(name = "change_percent") val changePercent: Double,
    @ColumnInfo(name = "change_amount") val changeAmount: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    @ColumnInfo(name = "pre_close") val preClose: Double,
    val volume: Long,
    val turnover: Double,
    val amplitude: Double,
    @ColumnInfo(name = "turnover_rate") val turnoverRate: Double,
    @ColumnInfo(name = "pe_dynamic") val peDynamic: Double,
    @ColumnInfo(name = "total_market_cap") val totalMarketCap: Double,
    @ColumnInfo(name = "circulating_market_cap") val circulatingMarketCap: Double,
    val pb: Double,
    val industry: String,
    @ColumnInfo(name = "trend_data") val trendData: List<StockTrend>,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long,
)
