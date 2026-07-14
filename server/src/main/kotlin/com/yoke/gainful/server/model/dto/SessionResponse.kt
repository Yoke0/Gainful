package com.yoke.gainful.server.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    val id: String,
    val deviceInfo: String? = null,
    val ipAddress: String? = null,
    val createdAt: String,
    val expiresAt: String,
    val isRevoked: Boolean = false,
)
