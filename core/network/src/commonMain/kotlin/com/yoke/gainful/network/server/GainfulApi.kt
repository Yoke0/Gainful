package com.yoke.gainful.network.server

import com.yoke.gainful.api.AuthResponse
import com.yoke.gainful.api.LoginRequest
import com.yoke.gainful.api.RegisterRequest
import com.yoke.gainful.api.UpdateProfileRequest
import com.yoke.gainful.api.UserResponse

interface GainfulApi {
    suspend fun register(request: RegisterRequest): AuthResponse

    suspend fun login(request: LoginRequest): AuthResponse

    suspend fun getProfile(token: String): UserResponse

    suspend fun updateProfile(token: String, request: UpdateProfileRequest): UserResponse

    suspend fun uploadAvatar(token: String, imageBytes: ByteArray, fileName: String): UserResponse

    suspend fun revokeSessions(token: String)
}
