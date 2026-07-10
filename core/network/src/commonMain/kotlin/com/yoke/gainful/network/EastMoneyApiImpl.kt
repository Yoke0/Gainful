package com.yoke.gainful.network

import com.yoke.gainful.network.model.ApiResponse
import com.yoke.gainful.network.model.BatchQuoteData
import com.yoke.gainful.network.model.KLineData
import com.yoke.gainful.network.model.QuoteData
import com.yoke.gainful.network.model.SearchItemDto
import com.yoke.gainful.network.model.SearchResponse
import com.yoke.gainful.network.model.TrendData
import com.yoke.gainful.network.model.TrendItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

internal class EastMoneyApiImpl(
    private val client: HttpClient,
    private val json: Json,
) : EastMoneyApi {
    override suspend fun getQuote(secId: String): QuoteData? {
        val rawBody =
            client.prepareGet(QUOTE_URL) {
                parameter("secid", secId)
                parameter("fltt", 2)
                parameter("fields", QUOTE_FIELDS)
            }.execute { it.bodyAsText() }

        return try {
            val resp = json.decodeFromString<ApiResponse<QuoteData>>(rawBody)
            println("[EastMoneyApi] getQuote f80=${resp.data?.tradingHours}")
            resp.data
        } catch (e: Exception) {
            println("[EastMoneyApi] getQuote deserialization failed: ${e.message}")
            null
        }
    }

    override suspend fun getBatchQuotes(secIds: List<String>): List<QuoteData> {
        val resp: ApiResponse<BatchQuoteData> =
            client.get(BATCH_URL) {
                parameter("secids", secIds.joinToString(","))
                parameter("fltt", 2)
                parameter("fields", BATCH_FIELDS)
            }.body()
        return resp.data?.diff ?: emptyList()
    }

    override suspend fun getTrends(secId: String): TrendData {
        val resp: ApiResponse<List<TrendItem>> =
            client.get(TREND_URL) {
                parameter("secid", secId)
            }.body()
        return TrendData(data = resp.data ?: emptyList())
    }

    override suspend fun getKLines(
        secId: String,
        klt: Int,
        fqt: Int,
        beg: String,
        end: String,
    ): KLineData? {
        val resp: ApiResponse<KLineData> =
            client.get(KLINE_URL) {
                parameter("secid", secId)
                parameter("fields1", COMMON_FIELDS1)
                parameter("fields2", KLINE_FIELDS2)
                parameter("klt", klt)
                parameter("fqt", fqt)
                parameter("beg", beg)
                parameter("end", end)
                parameter("fltt", 2)
            }.body()
        return resp.data
    }

    override suspend fun search(query: String, count: Int): List<SearchItemDto> {
        val resp: SearchResponse =
            client.get(SEARCH_URL) {
                parameter("input", query)
                parameter("type", 14)
                parameter("token", SEARCH_TOKEN)
                parameter("count", count)
            }.body()
        return resp.codeTable?.data ?: emptyList()
    }

    companion object {
        private const val QUOTE_URL = "https://push2.eastmoney.com/api/qt/stock/get"
        private const val BATCH_URL = "https://push2.eastmoney.com/api/qt/ulist.np/get"
        private const val TREND_URL = "https://push2.eastmoney.com/api/qt/stock/trends/get"
        private const val KLINE_URL = "https://push2his.eastmoney.com/api/qt/stock/kline/get"
        private const val SEARCH_URL = "https://searchapi.eastmoney.com/api/suggest/get"
        private const val SEARCH_TOKEN = "D43BF722C8E33BDC906FB84D85E326E8"
        private val QUOTE_FIELDS =
            listOf(
                "f43",
                "f44",
                "f45",
                "f46",
                "f47",
                "f48",
                "f50",
                "f55",
                "f57",
                "f58",
                "f59",
                "f60",
                "f80",
                "f84",
                "f85",
                "f92",
                "f116",
                "f117",
                "f127",
                "f162",
                "f167",
                "f168",
                "f169",
                "f170",
                "f171",
            ).joinToString(",")
        private const val BATCH_FIELDS = "f2,f3,f4,f5,f6,f7,f8,f9,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f100"
        private const val COMMON_FIELDS1 = "f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13"
        private const val KLINE_FIELDS2 = "f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61"
    }
}
