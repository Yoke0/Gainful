package com.yoke.gainful.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey
    @ColumnInfo(name = "inner_code") val innerCode: String,
    val code: String,
    val name: String,
    val pinYin: String,
    val id: String,
    val jys: String,
    val classify: String,
    @ColumnInfo(name = "market_type") val marketType: String,
    @ColumnInfo(name = "type_name") val typeName: String,
    @ColumnInfo(name = "security_type") val securityType: String,
    val market: Int,
    @ColumnInfo(name = "type_us") val typeUS: String,
    @ColumnInfo(name = "quote_id") val quoteId: String,
    @ColumnInfo(name = "unified_code") val unifiedCode: String,
)
