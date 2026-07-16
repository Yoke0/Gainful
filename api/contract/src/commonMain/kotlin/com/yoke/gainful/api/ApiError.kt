package com.yoke.gainful.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val error: String,
    val message: String? = null,
)
