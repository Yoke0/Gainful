package com.yoke.gainful.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteData(
    @SerialName("f2") val latestPrice: Double? = null,
    @SerialName("f3") val changePercent: Double? = null,
    @SerialName("f4") val changeAmount: Double? = null,
    @SerialName("f5") val volume: Long? = null,
    @SerialName("f6") val turnover: Double? = null,
    @SerialName("f7") val amplitude: Double? = null,
    @SerialName("f8") val turnoverRate: Double? = null,
    @SerialName("f9") val peDynamic: Double? = null,
    @SerialName("f12") val code: String? = null,
    @SerialName("f13") val market: Int? = null,
    @SerialName("f14") val name: String? = null,
    @SerialName("f15") val high: Double? = null,
    @SerialName("f16") val low: Double? = null,
    @SerialName("f17") val open: Double? = null,
    @SerialName("f18") val preClose: Double? = null,
    @SerialName("f20") val totalMarketCap: Double? = null,
    @SerialName("f21") val circulatingMarketCap: Double? = null,
    @SerialName("f23") val pb: Double? = null,
    @SerialName("f100") val industry: String? = null,
)

@Serializable
data class BatchQuoteData(
    val total: Int = 0,
    val diff: List<QuoteData> = emptyList(),
)
