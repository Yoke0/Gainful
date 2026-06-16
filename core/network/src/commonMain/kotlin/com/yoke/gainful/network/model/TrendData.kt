package com.yoke.gainful.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendData(
    val code: String? = null,
    val market: Int? = null,
    val name: String? = null,
    @SerialName("preClose") val preClose: Double? = null,
    val trends: List<String> = emptyList(),
)
