package com.yoke.gainful.api

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val username: String,
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
)

@Serializable
data class RefreshTokenResponse(
    val accessToken: String,
)
