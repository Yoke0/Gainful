package com.yoke.gainful.api

import kotlinx.serialization.Serializable

@Serializable
data class AvatarResponse(
    val avatarUrl: String,
)
