package com.yoke.gainful.server.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTransactionRequest(
    val assetCode: String,
    val assetName: String? = null,
    val type: Int,
    val quantity: Double,
    val price: Double,
    val amount: Double,
    val tradeDate: String,
)

@Serializable
data class UpdateTransactionRequest(
    val assetCode: String? = null,
    val assetName: String? = null,
    val type: Int? = null,
    val quantity: Double? = null,
    val price: Double? = null,
    val amount: Double? = null,
    val tradeDate: String? = null,
)

@Serializable
data class TransactionResponse(
    val id: String,
    val assetCode: String,
    val assetName: String? = null,
    val type: Int,
    val quantity: Double,
    val price: Double,
    val amount: Double,
    val tradeDate: String,
    val createdAt: String,
)
