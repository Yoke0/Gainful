package com.yoke.gainful.data.repository

import com.yoke.gainful.api.CreateTransactionRequest

interface TransactionSyncRepository {
    suspend fun createTransaction(request: CreateTransactionRequest)

    suspend fun deleteTransaction(id: String)
}
