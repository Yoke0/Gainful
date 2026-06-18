package com.yoke.gainful.data.model

import com.yoke.gainful.database.model.TransactionEntity
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    assetId = assetId,
    type = TransactionType.fromCode(type),
    quantity = quantity,
    price = price,
    amount = amount,
    tradeDate = tradeDate,
    timestamp = timestamp,
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    assetId = assetId,
    type = type.value,
    quantity = quantity,
    price = price,
    amount = amount,
    tradeDate = tradeDate,
    timestamp = timestamp,
)
