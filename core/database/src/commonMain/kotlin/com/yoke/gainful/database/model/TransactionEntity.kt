package com.yoke.gainful.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "asset_id") val assetId: String,
    val type: Int,
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val amount: Double = 0.0,
    @ColumnInfo(name = "trade_date") val tradeDate: Long,
    val timestamp: Long,
)
