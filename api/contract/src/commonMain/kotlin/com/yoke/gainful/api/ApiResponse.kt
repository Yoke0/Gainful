package com.yoke.gainful.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val rc: Int = 0,
    val data: T? = null,
)
