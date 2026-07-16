package com.yoke.gainful.data.repository

import com.yoke.gainful.data.model.toAsset
import com.yoke.gainful.data.model.toKLineList
import com.yoke.gainful.data.model.toStockQuote
import com.yoke.gainful.data.model.toStockTrendList
import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.FqType
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.KLinePeriod
import com.yoke.gainful.model.StockQuote
import com.yoke.gainful.model.StockTrend
import com.yoke.gainful.network.eastmoney.EastMoneyApi

class EastMoneyMarketRepository(
    private val api: EastMoneyApi,
) : MarketRepository {
    override suspend fun search(query: String, count: Int): List<Asset> {
        return api.search(query, count).mapNotNull { it.toAsset() }
    }

    override suspend fun getQuote(secId: String): StockQuote? {
        return api.getQuote(secId)?.toStockQuote()
    }

    override suspend fun getBatchQuotes(secIds: List<String>): List<StockQuote> {
        return api.getBatchQuotes(secIds).mapNotNull { it.toStockQuote() }
    }

    override suspend fun getTrends(secId: String): List<StockTrend> {
        return api.getTrends(secId).toStockTrendList()
    }

    override suspend fun getKLines(
        secId: String,
        period: KLinePeriod,
        fqType: FqType,
        startDate: String,
        endDate: String,
    ): List<KLine> {
        return api.getKLines(secId, klt = period.value, fqt = fqType.value, beg = startDate, end = endDate)
            ?.toKLineList() ?: emptyList()
    }
}
