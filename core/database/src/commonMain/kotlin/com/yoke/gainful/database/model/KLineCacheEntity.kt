package com.yoke.gainful.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "kline_cache",
    primaryKeys = ["asset_id", "date"],
)
data class KLineCacheEntity(
    @ColumnInfo(name = "asset_id") val assetId: String,
    val date: String,
    val open: Double,
    val close: Double,
    val high: Double,
    val low: Double,
    val volume: Long,
    val turnover: Double,
    @ColumnInfo(name = "change_percent") val changePercent: Double,
    @ColumnInfo(name = "change_amount") val changeAmount: Double,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long,
)
