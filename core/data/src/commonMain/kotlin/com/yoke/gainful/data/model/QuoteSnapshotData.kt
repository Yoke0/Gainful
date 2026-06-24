package com.yoke.gainful.data.model

import com.yoke.gainful.database.model.QuoteSnapshotEntity
import com.yoke.gainful.model.QuoteSnapshot
import com.yoke.gainful.model.StockQuote

fun QuoteSnapshotEntity.toDomain(): QuoteSnapshot {
    return QuoteSnapshot(
        quoteId = quoteId,
        code = code,
        name = name,
        quote = StockQuote(
            code = code,
            market = 0,
            name = name,
            latestPrice = latestPrice,
            changePercent = changePercent,
            changeAmount = changeAmount,
            volume = volume,
            turnover = turnover,
            amplitude = amplitude,
            turnoverRate = turnoverRate,
            peDynamic = peDynamic,
            pb = pb,
            high = high,
            low = low,
            open = open,
            preClose = preClose,
            totalMarketCap = totalMarketCap,
            circulatingMarketCap = circulatingMarketCap,
            industry = industry,
        ),
        trends = trendData,
        lastUpdated = lastUpdated,
    )
}

fun QuoteSnapshot.toEntity(): QuoteSnapshotEntity {
    return QuoteSnapshotEntity(
        quoteId = quoteId,
        code = code,
        name = name,
        latestPrice = quote.latestPrice,
        changePercent = quote.changePercent,
        changeAmount = quote.changeAmount,
        high = quote.high,
        low = quote.low,
        open = quote.open,
        preClose = quote.preClose,
        volume = quote.volume,
        turnover = quote.turnover,
        amplitude = quote.amplitude,
        turnoverRate = quote.turnoverRate,
        peDynamic = quote.peDynamic,
        totalMarketCap = quote.totalMarketCap,
        circulatingMarketCap = quote.circulatingMarketCap,
        pb = quote.pb,
        industry = quote.industry,
        trendData = trends,
        lastUpdated = lastUpdated,
    )
}
