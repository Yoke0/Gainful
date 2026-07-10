package com.yoke.gainful.data.repository

import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.FqType
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.KLinePeriod
import com.yoke.gainful.model.StockQuote
import com.yoke.gainful.model.StockTrend

interface MarketRepository {
    suspend fun search(query: String, count: Int = 10): List<Asset>

    suspend fun getQuote(secId: String): StockQuote?

    suspend fun getBatchQuotes(secIds: List<String>): List<StockQuote>

    suspend fun getTrends(secId: String): List<StockTrend>

    suspend fun getKLines(
        secId: String,
        period: KLinePeriod = KLinePeriod.DAILY,
        fqType: FqType = FqType.QFQ,
        startDate: String = "0",
        endDate: String = "20500101",
    ): List<KLine>
}
