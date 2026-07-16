package com.yoke.gainful.network.eastmoney

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    @SerialName("QuotationCodeTable") val codeTable: SearchCodeTable? = null,
)

@Serializable
data class SearchCodeTable(
    @SerialName("Data") val data: List<SearchItemDto>? = null,
)

@Serializable
data class SearchItemDto(
    @SerialName("Code") val code: String? = null,
    @SerialName("Name") val name: String? = null,
    @SerialName("PinYin") val pinYin: String? = null,
    @SerialName("ID") val id: String? = null,
    @SerialName("JYS") val jys: String? = null,
    @SerialName("Classify") val classify: String? = null,
    @SerialName("MarketType") val marketType: String? = null,
    @SerialName("SecurityTypeName") val typeName: String? = null,
    @SerialName("SecurityType") val securityType: String? = null,
    @SerialName("MktNum") val market: String? = null,
    @SerialName("TypeUS") val typeUS: String? = null,
    @SerialName("QuoteID") val quoteId: String? = null,
    @SerialName("UnifiedCode") val unifiedCode: String? = null,
    @SerialName("InnerCode") val innerCode: String? = null,
)
