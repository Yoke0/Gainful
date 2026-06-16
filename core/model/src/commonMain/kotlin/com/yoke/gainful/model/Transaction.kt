package com.yoke.gainful.model

data class Transaction(
    val id: String,
    val assetId: String,
    val type: TransactionType,
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val fee: Double = 0.0,
    val timestamp: Long,
)
