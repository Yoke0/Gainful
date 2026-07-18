package com.yoke.gainful.network.server

import com.yoke.gainful.api.AuthResponse
import com.yoke.gainful.api.LoginRequest
import com.yoke.gainful.api.RefreshTokenResponse
import com.yoke.gainful.api.RegisterRequest
import com.yoke.gainful.api.UpdateProfileRequest
import com.yoke.gainful.api.UserResponse

interface PublicApi {
    suspend fun register(request: RegisterRequest): AuthResponse

    suspend fun login(request: LoginRequest): AuthResponse

    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse

    suspend fun logout()
}

interface AuthenticatedApi {
    suspend fun getProfile(): UserResponse

    suspend fun updateProfile(request: UpdateProfileRequest): UserResponse

    suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): String
}
