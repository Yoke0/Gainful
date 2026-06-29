package com.yoke.gainful.network

import com.yoke.gainful.network.model.KLineData
import com.yoke.gainful.network.model.QuoteData
import com.yoke.gainful.network.model.SearchItemDto
import com.yoke.gainful.network.model.TrendData

interface EastMoneyApi {
    suspend fun getQuote(secId: String): QuoteData?

    suspend fun getBatchQuotes(secIds: List<String>): List<QuoteData>

    suspend fun getTrends(secId: String, ndays: Int = 1): TrendData

    suspend fun getKLines(secId: String, klt: Int = 101, fqt: Int = 1, limit: Int = 120): KLineData?

    suspend fun search(query: String, count: Int = 10): List<SearchItemDto>
}
