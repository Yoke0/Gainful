package com.yoke.gainful.feature.settings.util

import com.yoke.gainful.common.extensions.formatLocalizedDateTime
import com.yoke.gainful.common.extensions.parseLocalizedDateTimeToEpochMillis
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.feature.settings.model.CsvPreviewData
import com.yoke.gainful.domain.usecase.transaction.TransactionWithAsset
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlin.time.Clock
import kotlin.uuid.Uuid

object CsvUtil {

    fun generateCsv(
        transactions: List<TransactionWithAsset>,
        config: CsvConfig,
    ): String {
        val sb = StringBuilder()
        sb.appendLine(config.headers.joinToString(","))
        transactions.forEach { tx ->
            val typeStr = when (tx.transaction.type) {
                TransactionType.BUY -> config.buyType
                TransactionType.SELL -> config.sellType
                TransactionType.DIVIDEND -> config.dividendType
            }
            val dateStr = tx.transaction.tradeDate.formatLocalizedDateTime()
            sb.appendLine(
                "$dateStr,${tx.transaction.assetId},${tx.name},${typeStr},${tx.transaction.quantity},${tx.transaction.price},${tx.transaction.amount}"
            )
        }
        return sb.toString()
    }

    fun parseCsv(
        csvContent: String,
        config: CsvConfig,
        existingIds: Set<String> = emptySet(),
    ): CsvPreviewData? {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.size < 2) return null

        val csvHeaders = lines[0].split(",").map { it.trim() }
        val rows = mutableListOf<List<String>>()
        for (i in 1 until lines.size) {
            val values = lines[i].split(",").map { it.trim() }
            if (values.size == csvHeaders.size) {
                rows.add(values)
            }
        }
        if (rows.isEmpty()) return null

        val codeIndex = csvHeaders.indexOf(config.assetCodeHeader)

        var validCount = 0
        var duplicateCount = 0
        val duplicateIndices = mutableSetOf<Int>()
        val seenIds = mutableSetOf<String>()

        rows.forEachIndexed { index, row ->
            val code = if (codeIndex >= 0) row[codeIndex] else ""
            if (code.isBlank()) return@forEachIndexed

            val id = Uuid.random().toString()
            val inDb = existingIds.contains(id)
            val inCsv = !seenIds.add(id)
            if (inDb || inCsv) {
                duplicateCount++
                duplicateIndices.add(index)
            } else {
                validCount++
            }
        }

        return CsvPreviewData(
            fileName = "",
            headers = csvHeaders,
            rows = rows,
            totalCount = rows.size,
            validCount = validCount,
            duplicateCount = duplicateCount,
            rawCsv = csvContent,
            duplicateIndices = duplicateIndices,
        )
    }

    fun parseToTransactions(
        csvContent: String,
        config: CsvConfig,
        deletedIndices: Set<Int> = emptySet(),
    ): List<Transaction> {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.size < 2) return emptyList()

        val csvHeaders = lines[0].split(",").map { it.trim() }

        val codeIndex = csvHeaders.indexOf(config.assetCodeHeader)
        val dateIndex = csvHeaders.indexOf(config.dateHeader)
        val typeIndex = csvHeaders.indexOf(config.typeHeader)
        val quantityIndex = csvHeaders.indexOf(config.quantityHeader)
        val priceIndex = csvHeaders.indexOf(config.priceHeader)
        val amountIndex = csvHeaders.indexOf(config.amountHeader)

        val transactions = mutableListOf<Transaction>()
        for (i in 1 until lines.size) {
            if ((i - 1) in deletedIndices) continue

            val values = lines[i].split(",").map { it.trim() }
            if (values.size != csvHeaders.size) continue

            val code = if (codeIndex >= 0) values[codeIndex] else ""
            if (code.isBlank()) continue

            val typeStr = if (typeIndex >= 0) values[typeIndex] else config.buyType
            val type = when (typeStr) {
                config.buyType -> TransactionType.BUY
                config.sellType -> TransactionType.SELL
                config.dividendType -> TransactionType.DIVIDEND
                else -> TransactionType.BUY
            }

            val quantity = if (quantityIndex >= 0) values[quantityIndex].toDoubleOrNull() ?: 0.0 else 0.0
            val price = if (priceIndex >= 0) values[priceIndex].toDoubleOrNull() ?: 0.0 else 0.0
            val amount = if (amountIndex >= 0) values[amountIndex].toDoubleOrNull() ?: 0.0 else 0.0
            val dateStr = if (dateIndex >= 0) values[dateIndex] else null
            val tradeDateMs = dateStr?.parseLocalizedDateTimeToEpochMillis() ?: Clock.System.now().toEpochMilliseconds()
            val id = Uuid.random().toString()

            transactions.add(
                Transaction(
                    id = id,
                    assetId = code,
                    type = type,
                    quantity = quantity,
                    price = price,
                    amount = amount,
                    tradeDate = tradeDateMs,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                )
            )
        }
        return transactions
    }
}
