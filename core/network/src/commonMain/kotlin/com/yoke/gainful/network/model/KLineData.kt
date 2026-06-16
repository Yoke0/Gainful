package com.yoke.gainful.network.model

import kotlinx.serialization.Serializable

@Serializable
data class KLineData(
    val code: String? = null,
    val market: Int? = null,
    val name: String? = null,
    val klines: List<String> = emptyList(),
)
