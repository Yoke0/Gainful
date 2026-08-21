package com.yoke.gainful.network.server

import com.yoke.gainful.api.AUTH_LOGIN
import com.yoke.gainful.api.AUTH_REFRESH
import com.yoke.gainful.api.AUTH_REGISTER
import com.yoke.gainful.api.AuthResponse
import com.yoke.gainful.api.LoginRequest
import com.yoke.gainful.api.RefreshTokenRequest
import com.yoke.gainful.api.RefreshTokenResponse
import com.yoke.gainful.api.RegisterRequest
import com.yoke.gainful.ksafe.SecureTokenStorage
import com.yoke.gainful.network.exception.RefreshTokenExpiredException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
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

    override suspend fun refreshToken(refreshToken: String): RefreshTokenResponse {
        val resp =
            client.post(AUTH_REFRESH) {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken))
            }
        if (resp.status == HttpStatusCode.Unauthorized) {
            // Refresh token is expired/revoked/invalid — session cannot be renewed.
            secureTokenStorage.clearTokens()
            throw RefreshTokenExpiredException()
        }
        val body: RefreshTokenResponse = resp.body()
        secureTokenStorage.saveTokens(body.accessToken, refreshToken)
        return body
    }

    override suspend fun logout() {
        // Local logout only — server-side session revocation happens via the authenticated client.
        secureTokenStorage.clearTokens()
    }
}
