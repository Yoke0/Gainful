package com.yoke.gainful.model

data class QuoteSnapshot(
    val quoteId: String,
    val code: String,
    val name: String,
    val quote: StockQuote,
    val trends: List<StockTrend>,
    val lastUpdated: Long,
)
