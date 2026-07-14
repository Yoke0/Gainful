package com.yoke.gainful.server.model.dto

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
