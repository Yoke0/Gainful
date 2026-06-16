package com.yoke.gainful.data.model

import com.yoke.gainful.model.SearchResult
import com.yoke.gainful.network.model.SearchItemDto

fun SearchItemDto.toSearchResult(): SearchResult? {
    val code = code ?: return null
    val name = name ?: return null
    val market = market?.toIntOrNull() ?: return null
    val quoteId = quoteId ?: return null
    return SearchResult(
        code = code,
        name = name,
        pinYin = pinYin.orEmpty(),
        market = market,
        type = typeName.orEmpty(),
        quoteId = quoteId,
        classify = classify.orEmpty(),
    )
}
