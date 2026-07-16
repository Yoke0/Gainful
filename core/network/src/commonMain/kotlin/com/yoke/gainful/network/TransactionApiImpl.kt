package com.yoke.gainful.network

import com.yoke.gainful.common.BuildConfig
import com.yoke.gainful.network.model.CreateTransactionRequestDto
import com.yoke.gainful.network.model.TransactionDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class TransactionApiImpl(
    private val client: HttpClient,
) : TransactionApi {
    override suspend fun getTransactions(token: String, since: Long?): List<TransactionDto> {
        val url =
            if (since != null) {
                "${BuildConfig.SERVER_BASE_URL}/api/transactions?since=$since"
            } else {
                "${BuildConfig.SERVER_BASE_URL}/api/transactions"
            }
        return client.get(url) {
            header("Authorization", "Bearer $token")
        }.body()
    }

    override suspend fun createTransaction(token: String, request: CreateTransactionRequestDto): TransactionDto =
        client.post("${BuildConfig.SERVER_BASE_URL}/api/transactions") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun deleteTransaction(token: String, id: String) {
        client.delete("${BuildConfig.SERVER_BASE_URL}/api/transactions/$id") {
            header("Authorization", "Bearer $token")
        }
    }
}
