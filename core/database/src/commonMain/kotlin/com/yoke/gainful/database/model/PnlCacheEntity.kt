package com.yoke.gainful.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "pnl_cache",
    primaryKeys = ["date"],
)
data class PnlCacheEntity(
    val date: String,
    val pnl: Double,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long,
)
