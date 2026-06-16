package com.yoke.gainful.model

data class Asset(
    val id: String,
    val symbol: String,
    val name: String,
    val type: AssetType,
    val currency: String,
)

enum class AssetType {
    STOCK,
    CRYPTO,
    ETF,
    BOND,
    COMMODITY,
    OTHER
}
