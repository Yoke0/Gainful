package com.yoke.gainful.data.repository

import com.yoke.gainful.api.CreateTransactionRequest

interface TransactionSyncRepository {
    suspend fun createTransaction(token: String, request: CreateTransactionRequest)

    suspend fun deleteTransaction(token: String, id: String)
}
