package com.yoke.gainful.network

import com.yoke.gainful.network.model.AuthResponse
import com.yoke.gainful.network.model.LoginRequest
import com.yoke.gainful.network.model.RegisterRequest
import com.yoke.gainful.network.model.UpdateProfileRequest
import com.yoke.gainful.network.model.UserResponse

interface GainfulApi {
    suspend fun register(request: RegisterRequest): AuthResponse

    suspend fun login(request: LoginRequest): AuthResponse

    suspend fun getProfile(token: String): UserResponse

    suspend fun updateProfile(token: String, request: UpdateProfileRequest): UserResponse

    suspend fun uploadAvatar(token: String, imageBytes: ByteArray, fileName: String): UserResponse

    suspend fun revokeSessions(token: String)
}
