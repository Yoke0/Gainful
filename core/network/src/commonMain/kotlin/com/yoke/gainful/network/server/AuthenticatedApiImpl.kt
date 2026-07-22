package com.yoke.gainful.network.server

import com.yoke.gainful.api.AvatarResponse
import com.yoke.gainful.api.USERS_AVATAR
import com.yoke.gainful.api.USERS_ME
import com.yoke.gainful.api.UpdateProfileRequest
import com.yoke.gainful.api.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal class AuthenticatedApiImpl(
    private val client: HttpClient,
) : AuthenticatedApi {
    override suspend fun getProfile(): UserResponse =
        client.get(USERS_ME).body()

    override suspend fun updateProfile(request: UpdateProfileRequest): UserResponse =
        client.put(USERS_ME) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): String {
        val formParts =
            formData {
                append(
                    FormPart(
                        key = "file",
                        value = imageBytes,
                        headers =
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                            },
                    ),
                )
            }
        val resp: AvatarResponse =
            client.submitFormWithBinaryData(
                url = USERS_AVATAR,
                formData = formParts,
            ).body()
        return resp.avatarUrl
    }
}
