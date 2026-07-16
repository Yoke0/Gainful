package com.yoke.gainful.network

import com.yoke.gainful.network.model.CreateTransactionRequestDto
import com.yoke.gainful.network.model.TransactionDto

interface TransactionApi {
    suspend fun getTransactions(token: String, since: Long? = null): List<TransactionDto>

    suspend fun createTransaction(token: String, request: CreateTransactionRequestDto): TransactionDto

    suspend fun deleteTransaction(token: String, id: String)
}
