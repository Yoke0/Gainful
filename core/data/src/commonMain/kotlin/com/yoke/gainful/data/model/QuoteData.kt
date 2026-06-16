package com.yoke.gainful.data.model

import com.yoke.gainful.model.StockQuote
import com.yoke.gainful.network.model.QuoteData

fun QuoteData.toStockQuote(): StockQuote? {
    return StockQuote(
        code = code ?: return null,
        market = market ?: return null,
        name = name ?: return null,
        latestPrice = latestPrice ?: 0.0,
        changePercent = changePercent ?: 0.0,
        changeAmount = changeAmount ?: 0.0,
        volume = volume ?: 0L,
        turnover = turnover ?: 0.0,
        amplitude = amplitude ?: 0.0,
        turnoverRate = turnoverRate ?: 0.0,
        peDynamic = peDynamic ?: 0.0,
        pb = pb ?: 0.0,
        high = high ?: 0.0,
        low = low ?: 0.0,
        open = open ?: 0.0,
        preClose = preClose ?: 0.0,
        totalMarketCap = totalMarketCap ?: 0.0,
        circulatingMarketCap = circulatingMarketCap ?: 0.0,
        industry = industry ?: "",
    )
}
