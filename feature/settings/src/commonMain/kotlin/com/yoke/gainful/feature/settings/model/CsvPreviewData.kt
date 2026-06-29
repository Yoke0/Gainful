package com.yoke.gainful.feature.settings.model

import com.yoke.gainful.common.extensions.parseLocalizedDateTimeToEpochMillis
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.ui.TransactionDisplayItem

data class CsvPreviewData(
    val fileName: String,
    val headers: List<String>,
    val rows: List<List<String>>,
    val totalCount: Int,
    val validCount: Int,
    val duplicateCount: Int,
    val rawCsv: String,
    val duplicateIndices: Set<Int> = emptySet(),
    val deletedIndices: MutableSet<Int> = mutableSetOf(),
)

fun CsvPreviewData.toDisplayItems(csvConfig: CsvConfig): List<TransactionDisplayItem> {
    val codeIndex = headers.indexOf(csvConfig.assetCodeHeader)
    val nameIndex = headers.indexOf(csvConfig.assetNameHeader)
    val typeIndex = headers.indexOf(csvConfig.typeHeader)
    val quantityIndex = headers.indexOf(csvConfig.quantityHeader)
    val priceIndex = headers.indexOf(csvConfig.priceHeader)
    val amountIndex = headers.indexOf(csvConfig.amountHeader)
    val dateIndex = headers.indexOf(csvConfig.dateHeader)

    return rows.mapIndexed { rowIndex, row ->
        if (rowIndex in deletedIndices) return@mapIndexed null

        val typeStr = if (typeIndex >= 0) row[typeIndex] else csvConfig.buyType
        val type =
            when (typeStr) {
                csvConfig.buyType -> TransactionType.BUY
                csvConfig.sellType -> TransactionType.SELL
                else -> TransactionType.DIVIDEND
            }
        val dateStr = if (dateIndex >= 0) row[dateIndex] else ""
        val tradeDate = dateStr.parseLocalizedDateTimeToEpochMillis()

        TransactionDisplayItem(
            name = if (nameIndex >= 0) row[nameIndex] else "",
            code = if (codeIndex >= 0) row[codeIndex] else "",
            pinYin = "",
            type = type,
            quantity = if (quantityIndex >= 0) row[quantityIndex].toDoubleOrNull() ?: 0.0 else 0.0,
            price = if (priceIndex >= 0) row[priceIndex].toDoubleOrNull() ?: 0.0 else 0.0,
            amount = if (amountIndex >= 0) row[amountIndex].toDoubleOrNull() ?: 0.0 else 0.0,
            tradeDate = tradeDate,
        )
    }.filterNotNull()
}
