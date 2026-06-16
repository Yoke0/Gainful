package com.yoke.gainful.model

data class Transaction(
    val id: String,
    val assetId: String,
    val type: TransactionType,
    val quantity: Double,
    val price: Double,
    val fee: Double,
    val currency: String,
    val timestamp: Long,
    val note: String = "",
)

enum class TransactionType {
    BUY,
    SELL,
    DIVIDEND,
    INTEREST,
    DEPOSIT,
    WITHDRAWAL
}
