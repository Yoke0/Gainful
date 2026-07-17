package com.yoke.gainful.network.server

import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.api.TRANSACTIONS
import com.yoke.gainful.api.TransactionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class TransactionApiImpl(
    private val client: HttpClient,
) : TransactionApi {
    override suspend fun getTransactions(since: Long?): List<TransactionResponse> {
        val url =
            if (since != null) {
                "$TRANSACTIONS?since=$since"
            } else {
                TRANSACTIONS
            }
        return client.get(url).body()
    }

    override suspend fun createTransaction(request: CreateTransactionRequest): TransactionResponse =
        client.post(TRANSACTIONS) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun deleteTransaction(id: String) {
        client.delete("$TRANSACTIONS/$id")
    }
}
