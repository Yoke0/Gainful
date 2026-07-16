package com.yoke.gainful.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String,
    val assetCode: String,
    val assetName: String? = null,
    val type: Int,
    val quantity: Double,
    val price: Double,
    val amount: Double,
    val tradeDate: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String? = null,
)

@Serializable
data class CreateTransactionRequestDto(
    val assetCode: String,
    val assetName: String? = null,
    val type: Int,
    val quantity: Double,
    val price: Double,
    val amount: Double,
    val tradeDate: String,
)
