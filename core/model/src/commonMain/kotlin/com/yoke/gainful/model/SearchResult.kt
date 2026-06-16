package com.yoke.gainful.model

data class SearchResult(
    val code: String,
    val name: String,
    val pinYin: String,
    val market: Int,
    val type: String,
    val quoteId: String,
    val classify: String,
)
