package com.yoke.gainful.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String)

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String, val userId: String, val username: String)

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val nickname: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String,
)

@Serializable
data class UpdateProfileRequest(val nickname: String? = null, val email: String? = null)
