package com.yoke.gainful.network.server

import com.yoke.gainful.api.AUTH_LOGIN
import com.yoke.gainful.api.AUTH_REGISTER
import com.yoke.gainful.api.AuthResponse
import com.yoke.gainful.api.LoginRequest
import com.yoke.gainful.api.RegisterRequest
import com.yoke.gainful.api.USERS_AVATAR
import com.yoke.gainful.api.USERS_ME
import com.yoke.gainful.api.USERS_SESSIONS
import com.yoke.gainful.api.UpdateProfileRequest
import com.yoke.gainful.api.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal class GainfulApiImpl(
    private val client: HttpClient,
) : GainfulApi {
    override suspend fun register(request: RegisterRequest): AuthResponse =
        client.post(AUTH_REGISTER) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun login(request: LoginRequest): AuthResponse =
        client.post(AUTH_LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun getProfile(token: String): UserResponse =
        client.get(USERS_ME) {
            header("Authorization", "Bearer $token")
        }.body()

    override suspend fun updateProfile(token: String, request: UpdateProfileRequest): UserResponse =
        client.put(USERS_ME) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun uploadAvatar(token: String, imageBytes: ByteArray, fileName: String): UserResponse {
        val formParts =
            formData {
                append(
                    FormPart(
                        key = "file",
                        value = imageBytes,
                        headers =
                            io.ktor.http.Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                            },
                    ),
                )
            }
        return client.submitFormWithBinaryData(
            url = USERS_AVATAR,
            formData = formParts,
        ) {
            header("Authorization", "Bearer $token")
        }.body()
    }

    override suspend fun revokeSessions(token: String) {
        client.delete(USERS_SESSIONS) {
            header("Authorization", "Bearer $token")
        }
    }
}
