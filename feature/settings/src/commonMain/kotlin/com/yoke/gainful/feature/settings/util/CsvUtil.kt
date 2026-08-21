package com.yoke.gainful.feature.settings.util

import com.yoke.gainful.common.extensions.formatLocalizedDateTime
import com.yoke.gainful.common.extensions.tryParseLocalizedDateTimeToEpochMillis
import com.yoke.gainful.domain.usecase.transaction.TransactionWithAsset
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlin.math.roundToLong
import kotlin.time.Clock
import kotlin.uuid.Uuid

enum class RowStatus {
    VALID,
    INVALID,
    DUPLICATE,
}

/**
 * One parsed data row of the CSV file.
 *
 * For [RowStatus.INVALID] rows only the fields parsed before the failure are populated
 * ([invalidReason] holds a machine-readable code: `column_count`, `blank_code`,
 * `unknown_type`, `bad_quantity`, `bad_price`, `bad_amount`, `bad_date`).
 */
data class CsvRow(
    val raw: List<String>,
    val code: String = "",
    val name: String = "",
    val type: TransactionType = TransactionType.BUY,
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val amount: Double = 0.0,
    val tradeDateMs: Long = 0L,
    val status: RowStatus = RowStatus.VALID,
    val invalidReason: String? = null,
)

data class CsvParseResult(
    val headers: List<String>,
    val rows: List<CsvRow>,
    val totalCount: Int,
    val validCount: Int,
    val duplicateCount: Int,
    val invalidCount: Int,
    val duplicateIndices: Set<Int>,
    val invalidIndices: Set<Int>,
    /** Non-empty when required columns are missing from the header row. */
    val missingColumns: List<String> = emptyList(),
)

/**
 * Business key used to detect duplicate transactions:
 * asset code + trade date + type + quantity + price + amount.
 * Numeric values are normalized to 4 decimals so "100" and "100.0" match.
 */
data class TransactionFingerprint(
    val code: String,
    val tradeDateMs: Long,
    val type: TransactionType,
    val quantity: Double,
    val price: Double,
    val amount: Double,
) {
    companion object {
        private const val PRECISION = 10_000

        fun normalize(value: Double): Double = (value * PRECISION).roundToLong() / PRECISION.toDouble()

        fun from(transaction: Transaction): TransactionFingerprint =
            TransactionFingerprint(
                code = transaction.assetId,
                tradeDateMs = transaction.tradeDate,
                type = transaction.type,
                quantity = normalize(transaction.quantity),
                price = normalize(transaction.price),
                amount = normalize(transaction.amount),
            )

        fun from(row: CsvRow): TransactionFingerprint =
            TransactionFingerprint(
                code = row.code,
                tradeDateMs = row.tradeDateMs,
                type = row.type,
                quantity = normalize(row.quantity),
                price = normalize(row.price),
                amount = normalize(row.amount),
            )
    }
}

/** Resolved column indices for a CSV table against the configured headers. */
class CsvColumnMap(
    val headerCount: Int,
    val dateIndex: Int,
    val codeIndex: Int,
    val nameIndex: Int?,
    val typeIndex: Int?,
    val quantityIndex: Int,
    val priceIndex: Int,
    val amountIndex: Int,
) {
    fun missingRequiredColumns(config: CsvConfig): List<String> =
        buildList {
            if (dateIndex < 0) add(config.dateHeader)
            if (codeIndex < 0) add(config.assetCodeHeader)
            if (typeIndex == null) add(config.typeHeader)
            if (quantityIndex < 0) add(config.quantityHeader)
            if (priceIndex < 0) add(config.priceHeader)
            if (amountIndex < 0) add(config.amountHeader)
        }

    companion object {
        fun from(config: CsvConfig, headers: List<String>): CsvColumnMap {
            fun indexOf(name: String): Int = headers.indexOf(name)
            return CsvColumnMap(
                headerCount = headers.size,
                dateIndex = indexOf(config.dateHeader),
                codeIndex = indexOf(config.assetCodeHeader),
                nameIndex = indexOf(config.assetNameHeader).takeIf { it >= 0 },
                typeIndex = indexOf(config.typeHeader).takeIf { it >= 0 },
                quantityIndex = indexOf(config.quantityHeader),
                priceIndex = indexOf(config.priceHeader),
                amountIndex = indexOf(config.amountHeader),
            )
        }
    }
}

object CsvUtil {
    fun generateCsv(
        transactions: List<TransactionWithAsset>,
        config: CsvConfig,
    ): String {
        val sb = StringBuilder()
        sb.appendLine(config.headers.joinToString(","))
        transactions.forEach { tx ->
            val typeStr =
                when (tx.transaction.type) {
                    TransactionType.BUY -> config.buyType
                    TransactionType.SELL -> config.sellType
                    TransactionType.DIVIDEND -> config.dividendType
                }
            val dateStr = tx.transaction.tradeDate.formatLocalizedDateTime()
            sb.appendLine(
                "$dateStr,${tx.transaction.assetId},${tx.name},$typeStr,${tx.transaction.quantity},${tx.transaction.price},${tx.transaction.amount}",
            )
        }
        return sb.toString()
    }

    /**
     * Parses CSV content into a [CsvParseResult].
     *
     * Returns `null` when the file has no data rows. When required columns are missing,
     * the result is returned with a non-empty [CsvParseResult.missingColumns].
     *
     * Duplicates are detected against [existing] transactions (DB) and within the file itself
     * using [TransactionFingerprint].
     */
    fun parseCsv(
        csvContent: String,
        config: CsvConfig,
        existing: List<TransactionWithAsset> = emptyList(),
    ): CsvParseResult? {
        val table = parseCsvTable(csvContent) ?: return null
        val columnMap = CsvColumnMap.from(config, table.headers)
        val missingColumns = columnMap.missingRequiredColumns(config)
        if (missingColumns.isNotEmpty()) {
            return CsvParseResult(
                headers = table.headers,
                rows = emptyList(),
                totalCount = table.rows.size,
                validCount = 0,
                duplicateCount = 0,
                invalidCount = 0,
                duplicateIndices = emptySet(),
                invalidIndices = emptySet(),
                missingColumns = missingColumns,
            )
        }

        val dbFingerprints = existing.map { TransactionFingerprint.from(it.transaction) }.toMutableSet()
        val seenFingerprints = mutableSetOf<TransactionFingerprint>()

        var validCount = 0
        var duplicateCount = 0
        var invalidCount = 0
        val duplicateIndices = mutableSetOf<Int>()
        val invalidIndices = mutableSetOf<Int>()
        val rows = mutableListOf<CsvRow>()

        table.rows.forEachIndexed { index, raw ->
            val row = parseRow(raw, columnMap, config)
            if (row.status == RowStatus.VALID) {
                val fingerprint = TransactionFingerprint.from(row)
                val isDuplicate = fingerprint in dbFingerprints || !seenFingerprints.add(fingerprint)
                if (isDuplicate) {
                    duplicateCount++
                    duplicateIndices.add(index)
                    rows.add(row.copy(status = RowStatus.DUPLICATE))
                } else {
                    validCount++
                    rows.add(row)
                }
            } else {
                invalidCount++
                invalidIndices.add(index)
                rows.add(row)
            }
        }

        return CsvParseResult(
            headers = table.headers,
            rows = rows,
            totalCount = table.rows.size,
            validCount = validCount,
            duplicateCount = duplicateCount,
            invalidCount = invalidCount,
            duplicateIndices = duplicateIndices,
            invalidIndices = invalidIndices,
        )
    }

    /**
     * Converts a [CsvParseResult] into [Transaction]s.
     *
     * Rows marked as deleted or invalid are skipped; duplicate rows are kept
     * (the user has already confirmed importing them).
     */
    fun toTransactions(
        result: CsvParseResult,
        deletedIndices: Set<Int> = emptySet(),
    ): List<Transaction> {
        val now = Clock.System.now().toEpochMilliseconds()
        return result.rows.mapIndexedNotNull { index, row ->
            if (index in deletedIndices || row.status == RowStatus.INVALID) return@mapIndexedNotNull null
            Transaction(
                id = Uuid.random().toString(),
                assetId = row.code,
                type = row.type,
                quantity = row.quantity,
                price = row.price,
                amount = row.amount,
                tradeDate = row.tradeDateMs,
                timestamp = now,
            )
        }
    }

    // ---------- Internals ----------

    private data class CsvTable(
        val headers: List<String>,
        val rows: List<List<String>>,
    )

    private fun parseCsvTable(csvContent: String): CsvTable? {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.size < 2) return null

        val headers = lines[0].removePrefix("\uFEFF").split(",").map { it.trim() }
        val rows = mutableListOf<List<String>>()
        for (i in 1 until lines.size) {
            rows.add(lines[i].split(",").map { it.trim() })
        }
        if (rows.isEmpty()) return null
        return CsvTable(headers, rows)
    }

    private fun parseRow(
        raw: List<String>,
        columnMap: CsvColumnMap,
        config: CsvConfig,
    ): CsvRow {
        if (raw.size != columnMap.headerCount) {
            return CsvRow(raw = raw, status = RowStatus.INVALID, invalidReason = "column_count")
        }
        val code = raw[columnMap.codeIndex].trim()
        if (code.isBlank()) {
            return CsvRow(raw = raw, status = RowStatus.INVALID, invalidReason = "blank_code")
        }
        val typeStr = columnMap.typeIndex?.let { raw[it] } ?: config.buyType
        val type =
            when (typeStr) {
                config.buyType -> TransactionType.BUY
                config.sellType -> TransactionType.SELL
                config.dividendType -> TransactionType.DIVIDEND
                else -> return CsvRow(raw = raw, status = RowStatus.INVALID, invalidReason = "unknown_type")
            }
        val quantity =
            raw[columnMap.quantityIndex].toDoubleOrNull()
                ?: return CsvRow(raw = raw, status = RowStatus.INVALID, invalidReason = "bad_quantity")
        val price =
            raw[columnMap.priceIndex].toDoubleOrNull()
                ?: return CsvRow(raw = raw, status = RowStatus.INVALID, invalidReason = "bad_price")
        val amount =
            raw[columnMap.amountIndex].toDoubleOrNull()
                ?: return CsvRow(raw = raw, status = RowStatus.INVALID, invalidReason = "bad_amount")
        val dateStr = raw[columnMap.dateIndex].trim()
        val tradeDateMs =
            dateStr.tryParseLocalizedDateTimeToEpochMillis()
                ?: return CsvRow(raw = raw, status = RowStatus.INVALID, invalidReason = "bad_date")
        val name = columnMap.nameIndex?.let { raw[it] } ?: ""

        return CsvRow(
            raw = raw,
            code = code,
            name = name,
            type = type,
            quantity = quantity,
            price = price,
            amount = amount,
            tradeDateMs = tradeDateMs,
            status = RowStatus.VALID,
        )
    }
}
