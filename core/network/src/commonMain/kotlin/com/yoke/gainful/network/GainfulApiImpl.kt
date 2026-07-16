package com.yoke.gainful.network

import com.yoke.gainful.network.model.AuthResponse
import com.yoke.gainful.network.model.LoginRequest
import com.yoke.gainful.network.model.RegisterRequest
import com.yoke.gainful.network.model.UpdateProfileRequest
import com.yoke.gainful.network.model.UserResponse
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

private const val BASE_URL = "http://192.168.31.47:8080"

internal class GainfulApiImpl(
    private val client: HttpClient,
) : GainfulApi {
    override suspend fun register(request: RegisterRequest): AuthResponse =
        client.post("$BASE_URL/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun login(request: LoginRequest): AuthResponse =
        client.post("$BASE_URL/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun getProfile(token: String): UserResponse =
        client.get("$BASE_URL/api/users/me") {
            header("Authorization", "Bearer $token")
        }.body()

    override suspend fun updateProfile(token: String, request: UpdateProfileRequest): UserResponse =
        client.put("$BASE_URL/api/users/me") {
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
            url = "$BASE_URL/api/users/avatar",
            formData = formParts,
        ) {
            header("Authorization", "Bearer $token")
        }.body()
    }

    override suspend fun revokeSessions(token: String) {
        client.delete("$BASE_URL/api/users/sessions") {
            header("Authorization", "Bearer $token")
        }
    }
}
