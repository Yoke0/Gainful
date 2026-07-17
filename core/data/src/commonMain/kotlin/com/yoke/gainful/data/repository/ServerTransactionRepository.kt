package com.yoke.gainful.data.repository

import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.network.server.TransactionApi

class ServerTransactionRepository(
    private val transactionApi: TransactionApi,
) : TransactionSyncRepository {
    override suspend fun createTransaction(request: CreateTransactionRequest) {
        transactionApi.createTransaction(request)
    }

    override suspend fun deleteTransaction(id: String) {
        transactionApi.deleteTransaction(id)
    }
}
