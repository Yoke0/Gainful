package com.yoke.gainful.network.server

import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.api.TransactionResponse

interface TransactionApi {
    suspend fun getTransactions(token: String, since: Long? = null): List<TransactionResponse>

    suspend fun createTransaction(token: String, request: CreateTransactionRequest): TransactionResponse

    suspend fun deleteTransaction(token: String, id: String)
}
