package com.yoke.gainful.model

data class Asset(
    val innerCode: String,
    val code: String,
    val name: String,
    val pinYin: String,
    val id: String,
    val jys: String,
    val classify: String,
    val marketType: String,
    val typeName: String,
    val securityType: String,
    val market: Int,
    val typeUS: String,
    val quoteId: String,
    val unifiedCode: String,
)
