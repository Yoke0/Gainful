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
    @SerialName("f43") val latestPriceSingle: Double? = null,
    @SerialName("f44") val highSingle: Double? = null,
    @SerialName("f45") val lowSingle: Double? = null,
    @SerialName("f46") val openSingle: Double? = null,
    @SerialName("f47") val volumeSingle: Long? = null,
    @SerialName("f48") val turnoverSingle: Double? = null,
    @SerialName("f50") val volumeRatio: Double? = null,
    @SerialName("f55") val eps: Double? = null,
    @SerialName("f57") val codeSingle: String? = null,
    @SerialName("f58") val nameSingle: String? = null,
    @SerialName("f59") val marketSingle: Int? = null,
    @SerialName("f60") val preCloseSingle: Double? = null,
    @SerialName("f84") val totalShares: Double? = null,
    @SerialName("f85") val circulatingShares: Double? = null,
    @SerialName("f92") val bps: Double? = null,
    @SerialName("f100") val industry: String? = null,
    @SerialName("f116") val totalMarketCapSingle: Double? = null,
    @SerialName("f117") val circulatingMarketCapSingle: Double? = null,
    @SerialName("f127") val industrySingle: String? = null,
    @SerialName("f162") val peStatic: Double? = null,
    @SerialName("f167") val pbSingle: Double? = null,
    @SerialName("f168") val turnoverRateSingle: Double? = null,
    @SerialName("f169") val changeAmountSingle: Double? = null,
    @SerialName("f170") val changePercentSingle: Double? = null,
    @SerialName("f171") val amplitudeSingle: Double? = null,
    @SerialName("f80") val tradingHours: String? = null,
) {
    val resolvedLatestPrice: Double get() = latestPriceSingle?.takeIf { it != 0.0 } ?: preCloseSingle ?: latestPrice ?: 0.0
    val resolvedChangePercent: Double get() = changePercentSingle ?: changePercent ?: 0.0
    val resolvedChangeAmount: Double get() = changeAmountSingle ?: changeAmount ?: 0.0
    val resolvedVolume: Long get() = volumeSingle ?: volume ?: 0L
    val resolvedTurnover: Double get() = turnoverSingle ?: turnover ?: 0.0
    val resolvedAmplitude: Double get() = amplitudeSingle ?: amplitude ?: 0.0
    val resolvedTurnoverRate: Double get() = turnoverRateSingle ?: turnoverRate ?: 0.0
    val resolvedPeDynamic: Double get() = peDynamic ?: 0.0
    val resolvedCode: String get() = codeSingle ?: code ?: ""
    val resolvedMarket: Int get() = marketSingle ?: market ?: 0
    val resolvedName: String get() = nameSingle ?: name ?: ""
    val resolvedHigh: Double get() = highSingle ?: high ?: 0.0
    val resolvedLow: Double get() = lowSingle ?: low ?: 0.0
    val resolvedOpen: Double get() = openSingle ?: open ?: 0.0
    val resolvedPreClose: Double get() = preCloseSingle ?: preClose ?: 0.0
    val resolvedTotalMarketCap: Double get() = totalMarketCapSingle ?: totalMarketCap ?: 0.0
    val resolvedCirculatingMarketCap: Double get() = circulatingMarketCapSingle ?: circulatingMarketCap ?: 0.0
    val resolvedPb: Double get() = pbSingle ?: pb ?: 0.0
    val resolvedIndustry: String get() = industrySingle ?: industry ?: ""
}

@Serializable
data class BatchQuoteData(
    val total: Int = 0,
    val diff: List<QuoteData> = emptyList(),
)
