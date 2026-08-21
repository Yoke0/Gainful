package com.yoke.gainful.feature.settings.model

import com.yoke.gainful.feature.settings.util.CsvParseResult
import com.yoke.gainful.feature.settings.util.CsvRow
import com.yoke.gainful.ui.TransactionDisplayItem

data class CsvPreviewData(
    val fileName: String,
    val parseResult: CsvParseResult,
    val deletedIndices: MutableSet<Int> = mutableSetOf(),
) {
    val totalCount: Int get() = parseResult.totalCount
    val validCount: Int get() = parseResult.validCount

    /** Duplicates still present in the preview (deleted duplicates are excluded). */
    val duplicateCount: Int get() = (parseResult.duplicateIndices - deletedIndices).size

    val invalidCount: Int get() = parseResult.invalidCount
    val duplicateIndices: Set<Int> get() = parseResult.duplicateIndices
    val invalidIndices: Set<Int> get() = parseResult.invalidIndices
    val rows: List<CsvRow> get() = parseResult.rows
}

/**
 * Maps every row (row-index aligned) to a display item.
 *
 * Deleted rows are NOT filtered here — the UI skips them via [CsvPreviewData.deletedIndices],
 * keeping display indices identical to CSV row indices so enrichment survives deletions.
 */
fun CsvPreviewData.toDisplayItems(): List<TransactionDisplayItem> =
    rows.map { it.toDisplayItem() }

fun CsvRow.toDisplayItem(): TransactionDisplayItem =
    TransactionDisplayItem(
        name = name,
        code = code,
        pinYin = "",
        type = type,
        quantity = quantity,
        price = price,
        amount = amount,
        tradeDate = tradeDateMs,
    )
