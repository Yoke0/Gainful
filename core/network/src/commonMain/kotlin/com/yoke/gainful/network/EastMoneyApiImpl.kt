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
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class EastMoneyApiImpl(
    private val client: HttpClient,
) : EastMoneyApi {

    override suspend fun getQuote(secId: String): QuoteData? {
        val resp: ApiResponse<QuoteData> = client.get(QUOTE_URL) {
            quoteParams(secId = secId)
        }.body()
        return resp.data
    }

    override suspend fun getBatchQuotes(secIds: List<String>): List<QuoteData> {
        val resp: ApiResponse<BatchQuoteData> = client.get(BATCH_URL) {
            quoteParams(secIds = secIds)
        }.body()
        return resp.data?.diff ?: emptyList()
    }

    override suspend fun getTrends(secId: String, ndays: Int): TrendData {
        val resp: ApiResponse<List<TrendItem>> = client.get(TREND_URL) {
            parameter("secid", secId)
            parameter("iscr", 0)
            parameter("iscca", 0)
            parameter("ndays", ndays)
            parameter("forcect", 1)
        }.body()
        return TrendData(data = resp.data ?: emptyList())
    }

    override suspend fun getKLines(secId: String, klt: Int, fqt: Int, limit: Int): KLineData? {
        val resp: ApiResponse<KLineData> = client.get(KLINE_URL) {
            parameter("secid", secId)
            parameter("fields1", COMMON_FIELDS1)
            parameter("fields2", KLINE_FIELDS2)
            parameter("klt", klt)
            parameter("fqt", fqt)
            parameter("lmt", limit)
            parameter("end", "20500101")
            parameter("fltt", 2)
        }.body()
        return resp.data
    }

    override suspend fun search(query: String, count: Int): List<SearchItemDto> {
        val resp: SearchResponse = client.get(SEARCH_URL) {
            parameter("input", query)
            parameter("type", 14)
            parameter("token", SEARCH_TOKEN)
            parameter("count", count)
        }.body()
        return resp.codeTable?.data ?: emptyList()
    }

    private fun HttpRequestBuilder.quoteParams(secId: String? = null, secIds: List<String>? = null) {
        secId?.let { parameter("secid", it) }
        secIds?.let { parameter("secids", it.joinToString(",")) }
        parameter("fltt", 2)
        if (secId != null) {
            parameter("fields", SINGLE_QUOTE_FIELDS)
        } else {
            parameter("fields", BATCH_QUOTE_FIELDS)
        }
    }

    companion object {
        private const val QUOTE_URL = "https://push2.eastmoney.com/api/qt/stock/get"
        private const val BATCH_URL = "https://push2.eastmoney.com/api/qt/ulist.np/get"
        private const val TREND_URL = "https://push2.eastmoney.com/api/qt/stock/trends/get"
        private const val KLINE_URL = "https://push2his.eastmoney.com/api/qt/stock/kline/get"
        private const val SEARCH_URL = "https://searchapi.eastmoney.com/api/suggest/get"
        private const val SEARCH_TOKEN = "D43BF722C8E33BDC906FB84D85E326E8"
        private const val SINGLE_QUOTE_FIELDS =
            "f43,f44,f45,f46,f47,f48,f50,f55,f57,f58,f59,f60,f84,f85,f92,f116,f117,f162,f167,f168,f169,f170,f171"
        private const val BATCH_QUOTE_FIELDS =
            "f2,f3,f4,f5,f6,f7,f8,f9,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f100"
        private const val COMMON_FIELDS1 = "f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13"
        private const val KLINE_FIELDS2 = "f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61"
    }
}
