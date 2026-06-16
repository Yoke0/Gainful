package com.yoke.gainful.data.model

import com.yoke.gainful.database.model.AssetEntity
import com.yoke.gainful.model.Asset

fun AssetEntity.toDomain(): Asset = Asset(
    innerCode = innerCode,
    code = code,
    name = name,
    pinYin = pinYin,
    id = id,
    jys = jys,
    classify = classify,
    marketType = marketType,
    typeName = typeName,
    securityType = securityType,
    market = market,
    typeUS = typeUS,
    quoteId = quoteId,
    unifiedCode = unifiedCode,
)

fun Asset.toEntity(): AssetEntity = AssetEntity(
    innerCode = innerCode,
    code = code,
    name = name,
    pinYin = pinYin,
    id = id,
    jys = jys,
    classify = classify,
    marketType = marketType,
    typeName = typeName,
    securityType = securityType,
    market = market,
    typeUS = typeUS,
    quoteId = quoteId,
    unifiedCode = unifiedCode,
)
