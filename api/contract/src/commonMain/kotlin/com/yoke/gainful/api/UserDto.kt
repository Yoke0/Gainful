package com.yoke.gainful.api

import kotlinx.serialization.Serializable

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
data class UpdateProfileRequest(
    val nickname: String? = null,
    val email: String? = null,
)

@Serializable
data class SessionResponse(
    val id: String,
    val deviceInfo: String? = null,
    val ipAddress: String? = null,
    val createdAt: String,
    val expiresAt: String,
    val isRevoked: Boolean = false,
)
