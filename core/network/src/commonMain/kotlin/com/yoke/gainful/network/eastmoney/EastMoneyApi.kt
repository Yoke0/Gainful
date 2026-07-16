package com.yoke.gainful.network.eastmoney

interface EastMoneyApi {
    suspend fun getQuote(secId: String): QuoteData?

    suspend fun getBatchQuotes(secIds: List<String>): List<QuoteData>

    suspend fun getTrends(secId: String): TrendData

    suspend fun getKLines(
        secId: String,
        klt: Int,
        fqt: Int,
        beg: String,
        end: String,
    ): KLineData?

    suspend fun search(query: String, count: Int): List<SearchItemDto>
}
