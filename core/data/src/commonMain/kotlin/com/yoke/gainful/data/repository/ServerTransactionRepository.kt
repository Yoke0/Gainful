package com.yoke.gainful.data.repository

import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.network.server.TransactionApi

class ServerTransactionRepository(
    private val transactionApi: TransactionApi,
) : TransactionSyncRepository {
    override suspend fun createTransaction(token: String, request: CreateTransactionRequest) {
        transactionApi.createTransaction(token, request)
    }

    override suspend fun deleteTransaction(token: String, id: String) {
        transactionApi.deleteTransaction(token, id)
    }
}
