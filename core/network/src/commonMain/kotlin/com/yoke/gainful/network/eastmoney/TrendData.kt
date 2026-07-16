package com.yoke.gainful.network.eastmoney

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendData(
    val code: String? = null,
    val market: Int? = null,
    val name: String? = null,
    @SerialName("preClose") val preClose: Double? = null,
    val data: List<TrendItem> = emptyList(),
)

@Serializable
data class TrendItem(
    @SerialName("f1") val market: Int? = null,
    @SerialName("f2") val timestamp: Long? = null,
    @SerialName("f3") val price: Int? = null,
)
