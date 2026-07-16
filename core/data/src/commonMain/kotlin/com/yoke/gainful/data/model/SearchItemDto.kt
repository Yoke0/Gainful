package com.yoke.gainful.data.model

import com.yoke.gainful.model.Asset
import com.yoke.gainful.network.eastmoney.SearchItemDto

fun SearchItemDto.toAsset(): Asset? {
    val code = code ?: return null
    val name = name ?: return null
    val market = market?.toIntOrNull() ?: return null
    val quoteId = quoteId ?: return null
    val innerCode = innerCode ?: return null
    return Asset(
        innerCode = innerCode,
        code = code,
        name = name,
        pinYin = pinYin.orEmpty(),
        id = id.orEmpty(),
        jys = jys.orEmpty(),
        classify = classify.orEmpty(),
        marketType = marketType.orEmpty(),
        typeName = typeName.orEmpty(),
        securityType = securityType.orEmpty(),
        market = market,
        typeUS = typeUS.orEmpty(),
        quoteId = quoteId,
        unifiedCode = unifiedCode.orEmpty(),
    )
}
