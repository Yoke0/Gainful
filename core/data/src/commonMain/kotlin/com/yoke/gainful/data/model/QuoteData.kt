package com.yoke.gainful.data.model

import com.yoke.gainful.model.StockQuote
import com.yoke.gainful.network.model.QuoteData

fun QuoteData.toStockQuote(): StockQuote? {
    return StockQuote(
        code = resolvedCode.ifEmpty { return null },
        market = resolvedMarket,
        name = resolvedName.ifEmpty { return null },
        latestPrice = resolvedLatestPrice,
        changePercent = resolvedChangePercent,
        changeAmount = resolvedChangeAmount,
        volume = resolvedVolume,
        turnover = resolvedTurnover,
        amplitude = resolvedAmplitude,
        turnoverRate = resolvedTurnoverRate,
        peDynamic = resolvedPeDynamic,
        pb = resolvedPb,
        high = resolvedHigh,
        low = resolvedLow,
        open = resolvedOpen,
        preClose = resolvedPreClose,
        totalMarketCap = resolvedTotalMarketCap,
        circulatingMarketCap = resolvedCirculatingMarketCap,
        industry = resolvedIndustry,
    )
}
