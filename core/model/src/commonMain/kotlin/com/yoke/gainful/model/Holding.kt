package com.yoke.gainful.model

data class Holding(
    val id: String,
    val assetId: String,
    val quantity: Double,
    val averageCost: Double,
)
