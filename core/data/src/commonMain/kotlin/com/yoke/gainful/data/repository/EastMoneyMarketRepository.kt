package com.yoke.gainful.data.repository

import com.yoke.gainful.data.model.toKLineList
import com.yoke.gainful.data.model.toSearchResult
import com.yoke.gainful.data.model.toStockQuote
import com.yoke.gainful.data.model.toStockTrendList
import com.yoke.gainful.model.FqType
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.KLinePeriod
import com.yoke.gainful.model.SearchResult
import com.yoke.gainful.model.StockQuote
import com.yoke.gainful.model.StockTrend
import com.yoke.gainful.network.EastMoneyApi

class EastMoneyMarketRepository(
    private val api: EastMoneyApi,
) : MarketRepository {

    override suspend fun search(query: String, count: Int): List<SearchResult> {
        return api.search(query, count).mapNotNull { it.toSearchResult() }
    }

    override suspend fun getQuote(secId: String): StockQuote? {
        return api.getQuote(secId)?.toStockQuote()
    }

    override suspend fun getBatchQuotes(secIds: List<String>): List<StockQuote> {
        return api.getBatchQuotes(secIds).mapNotNull { it.toStockQuote() }
    }

    override suspend fun getTrends(secId: String, ndays: Int): List<StockTrend> {
        return api.getTrends(secId, ndays)?.toStockTrendList() ?: emptyList()
    }

    override suspend fun getKLines(
        secId: String,
        period: KLinePeriod,
        fqType: FqType,
        limit: Int,
    ): List<KLine> {
        return api.getKLines(secId, klt = period.value, fqt = fqType.value, limit = limit)
            ?.toKLineList() ?: emptyList()
    }
}
