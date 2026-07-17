package com.yoke.gainful.network.server

import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.api.TransactionResponse

interface TransactionApi {
    suspend fun getTransactions(since: Long? = null): List<TransactionResponse>

    suspend fun createTransaction(request: CreateTransactionRequest): TransactionResponse

    suspend fun deleteTransaction(id: String)
}
