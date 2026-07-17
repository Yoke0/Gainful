package com.yoke.gainful.network.server

import com.yoke.gainful.api.AUTH_LOGIN
import com.yoke.gainful.api.AUTH_REFRESH
import com.yoke.gainful.api.AUTH_REGISTER
import com.yoke.gainful.api.AuthResponse
import com.yoke.gainful.api.LoginRequest
import com.yoke.gainful.api.RefreshTokenRequest
import com.yoke.gainful.api.RegisterRequest
import com.yoke.gainful.api.USERS_SESSIONS
import com.yoke.gainful.ksafe.SecureTokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class PublicApiImpl(
    private val client: HttpClient,
    private val secureTokenStorage: SecureTokenStorage,
) : PublicApi {
    override suspend fun register(request: RegisterRequest): AuthResponse {
        val resp: AuthResponse =
            client.post(AUTH_REGISTER) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        secureTokenStorage.saveTokens(resp.accessToken, resp.refreshToken)
        return resp
    }

    override suspend fun login(request: LoginRequest): AuthResponse {
        val resp: AuthResponse =
            client.post(AUTH_LOGIN) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        secureTokenStorage.saveTokens(resp.accessToken, resp.refreshToken)
        return resp
    }

    override suspend fun refreshToken(refreshToken: String): AuthResponse {
        val resp: AuthResponse =
            client.post(AUTH_REFRESH) {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
            }.body()
        secureTokenStorage.saveTokens(resp.accessToken, resp.refreshToken)
        return resp
    }

    override suspend fun logout() {
        runCatching { client.delete(USERS_SESSIONS) }
        secureTokenStorage.clearTokens()
    }
}
