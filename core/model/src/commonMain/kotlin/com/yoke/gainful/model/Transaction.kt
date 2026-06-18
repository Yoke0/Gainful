package com.yoke.gainful.model

data class Transaction(
    val id: String,
    val assetId: String,
    val type: TransactionType,
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val amount: Double = 0.0,
    val tradeDate: Long,
    val timestamp: Long,
) {
    val fee: Double get() = when (type) {
        TransactionType.BUY -> amount - price * quantity
        TransactionType.SELL -> price * quantity - amount
        TransactionType.DIVIDEND -> 0.0
    }
}
