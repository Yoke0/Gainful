package com.yoke.gainful.feature.settings.util

import com.yoke.gainful.domain.usecase.transaction.TransactionWithAsset
import com.yoke.gainful.feature.settings.model.CsvConfig
import com.yoke.gainful.feature.settings.model.CsvPreviewData
import com.yoke.gainful.feature.settings.model.toDisplayItems
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CsvUtilTest {
    private val config =
        CsvConfig(
            headers = listOf("日期", "资产代码", "股票名称", "交易类型", "股数", "成交价格", "成交金额"),
            typeValues = listOf("买入", "卖出", "股息"),
        )

    private val headers = "日期,资产代码,股票名称,交易类型,股数,成交价格,成交金额"

    private fun at(dateTime: String): Long =
        LocalDateTime.parse(dateTime.replace(" ", "T"))
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

    private fun tx(
        assetId: String = "SH600000",
        tradeDateMs: Long = at("2024-01-15 09:30:00"),
        type: TransactionType = TransactionType.BUY,
        quantity: Double = 100.0,
        price: Double = 10.5,
        amount: Double = 1060.0,
    ) = Transaction(
        id = Uuid.random().toString(),
        assetId = assetId,
        type = type,
        quantity = quantity,
        price = price,
        amount = amount,
        tradeDate = tradeDateMs,
        timestamp = 0L,
    )

    private fun txWithAsset(transaction: Transaction): TransactionWithAsset =
        TransactionWithAsset(
            transaction = transaction,
            code = transaction.assetId,
            name = "Test",
            pinYin = "TEST",
        )

    private fun csv(vararg rows: String): String =
        buildString {
            appendLine(headers)
            rows.forEach { appendLine(it) }
        }

    // ---------- basic parsing ----------

    @Test
    fun parsesValidRowsAndCounts() {
        val result =
            CsvUtil.parseCsv(
                csv(
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00",
                    "2024-01-16 10:00,SH600519,贵州茅台,卖出,10,1800.00,17950.00",
                ),
                config,
            )
        assertNotNull(result)
        assertEquals(2, result.totalCount)
        assertEquals(2, result.validCount)
        assertEquals(0, result.duplicateCount)
        assertEquals(0, result.invalidCount)
        assertTrue(result.missingColumns.isEmpty())
        assertTrue(result.duplicateIndices.isEmpty())
        assertTrue(result.invalidIndices.isEmpty())
        // total = valid + duplicate + invalid
        assertEquals(result.totalCount, result.validCount + result.duplicateCount + result.invalidCount)
    }

    @Test
    fun returnsNullForEmptyOrHeaderOnlyFile() {
        assertNull(CsvUtil.parseCsv("", config))
        assertNull(CsvUtil.parseCsv(headers, config))
        assertNull(CsvUtil.parseCsv("   \n$headers\n  ", config))
    }

    @Test
    fun stripsUtf8BomFromHeader() {
        val result = CsvUtil.parseCsv("\uFEFF$headers\n2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00", config)
        assertNotNull(result)
        assertEquals(1, result.validCount)
    }

    // ---------- duplicate detection ----------

    @Test
    fun detectsDuplicateWithinFile() {
        val row = "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00"
        val result = CsvUtil.parseCsv(csv(row, row), config)
        assertNotNull(result)
        assertEquals(1, result.validCount)
        assertEquals(1, result.duplicateCount)
        assertEquals(setOf(1), result.duplicateIndices)
        assertEquals(RowStatus.DUPLICATE, result.rows[1].status)
    }

    @Test
    fun detectsDuplicateAgainstExistingTransactions() {
        val existing = listOf(txWithAsset(tx()))
        val result =
            CsvUtil.parseCsv(
                csv("2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00"),
                config,
                existing = existing,
            )
        assertNotNull(result)
        assertEquals(0, result.validCount)
        assertEquals(1, result.duplicateCount)
        assertEquals(setOf(0), result.duplicateIndices)
    }

    @Test
    fun numericNormalizationMatchesDifferentTextualForms() {
        // CSV "100" must match existing transaction with quantity 100.0
        val existing = listOf(txWithAsset(tx(quantity = 100.0, price = 10.5, amount = 1060.0)))
        val result =
            CsvUtil.parseCsv(
                csv("2024-01-15 09:30,SH600000,浦发银行,买入,100,10.5,1060"),
                config,
                existing = existing,
            )
        assertNotNull(result)
        assertEquals(1, result.duplicateCount)
    }

    @Test
    fun sameDaySameStockDifferentQuantityIsNotDuplicate() {
        val result =
            CsvUtil.parseCsv(
                csv(
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00",
                    "2024-01-15 14:00,SH600000,浦发银行,买入,200,10.60,2130.00",
                ),
                config,
            )
        assertNotNull(result)
        assertEquals(2, result.validCount)
        assertEquals(0, result.duplicateCount)
    }

    @Test
    fun dividendRowsAreNotFlaggedAsDuplicateWhenValuesDiffer() {
        val result =
            CsvUtil.parseCsv(
                csv(
                    "2024-06-30 00:00,SH600000,浦发银行,股息,0,0,200.00",
                    "2025-06-30 00:00,SH600000,浦发银行,股息,0,0,210.00",
                ),
                config,
            )
        assertNotNull(result)
        assertEquals(2, result.validCount)
        assertEquals(0, result.duplicateCount)
    }

    @Test
    fun roundTripGeneratedCsvIsFullyDuplicate() {
        val transactions =
            listOf(
                tx(tradeDateMs = at("2024-01-15 09:30:00")),
                tx(
                    assetId = "SH600519",
                    tradeDateMs = at("2024-02-20 10:00:00"),
                    type = TransactionType.SELL,
                    quantity = 10.0,
                    price = 1800.0,
                    amount = 17950.0,
                ),
            )
        val existing = transactions.map(::txWithAsset)
        val generated = CsvUtil.generateCsv(existing, config)

        val result = CsvUtil.parseCsv(generated, config, existing = existing)
        assertNotNull(result)
        assertEquals(transactions.size, result.totalCount)
        assertEquals(0, result.validCount)
        assertEquals(transactions.size, result.duplicateCount)
    }

    // ---------- invalid rows ----------

    @Test
    fun marksInvalidDateAsInvalidWithoutThrowing() {
        val result =
            CsvUtil.parseCsv(
                csv(
                    "2024-13-45 99:99,SH600000,浦发银行,买入,100,10.50,1060.00",
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00",
                ),
                config,
            )
        assertNotNull(result)
        assertEquals(1, result.validCount)
        assertEquals(1, result.invalidCount)
        assertEquals(setOf(0), result.invalidIndices)
        assertEquals(RowStatus.INVALID, result.rows[0].status)
    }

    @Test
    fun marksNonNumericFieldsAsInvalid() {
        val result =
            CsvUtil.parseCsv(
                csv(
                    "2024-01-15 09:30,SH600000,浦发银行,买入,abc,10.50,1060.00",
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,xyz,1060.00",
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,abc",
                ),
                config,
            )
        assertNotNull(result)
        assertEquals(0, result.validCount)
        assertEquals(3, result.invalidCount)
        assertEquals(setOf(0, 1, 2), result.invalidIndices)
    }

    @Test
    fun marksUnknownTypeAndBlankCodeAsInvalid() {
        val result =
            CsvUtil.parseCsv(
                csv(
                    "2024-01-15 09:30,SH600000,浦发银行,申购,100,10.50,1060.00",
                    "2024-01-15 09:30,,浦发银行,买入,100,10.50,1060.00",
                ),
                config,
            )
        assertNotNull(result)
        assertEquals(0, result.validCount)
        assertEquals(2, result.invalidCount)
        assertEquals(setOf(0, 1), result.invalidIndices)
    }

    @Test
    fun marksWrongColumnCountRowAsInvalid() {
        val result =
            CsvUtil.parseCsv(
                csv(
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50", // 6 columns
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00",
                ),
                config,
            )
        assertNotNull(result)
        assertEquals(1, result.validCount)
        assertEquals(1, result.invalidCount)
        assertEquals(setOf(0), result.invalidIndices)
        // row index 0 is the malformed line — indices refer to row positions, not line numbers
        assertEquals(RowStatus.INVALID, result.rows[0].status)
    }

    @Test
    fun blankNumericCellIsInvalid() {
        val result =
            CsvUtil.parseCsv(
                csv("2024-01-15 09:30,SH600000,浦发银行,买入,100,,1060.00"),
                config,
            )
        assertNotNull(result)
        assertEquals(1, result.invalidCount)
        assertEquals(RowStatus.INVALID, result.rows[0].status)
    }

    // ---------- lenient date parsing ----------

    @Test
    fun acceptsMinuteSecondAndDateOnlyFormats() {
        val minute = at("2024-01-15 09:30:00")
        val withSeconds =
            CsvUtil.parseCsv(csv("2024-01-15 09:30:00,SH600000,浦发银行,买入,100,10.50,1060.00"), config)
        val withoutSeconds =
            CsvUtil.parseCsv(csv("2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00"), config)
        val dateOnly =
            CsvUtil.parseCsv(csv("2024-01-15,SH600000,浦发银行,买入,100,10.50,1060.00"), config)

        assertNotNull(withSeconds)
        assertNotNull(withoutSeconds)
        assertNotNull(dateOnly)
        assertEquals(minute, withSeconds.rows[0].tradeDateMs)
        // minute precision without seconds resolves to the same instant
        assertEquals(minute, withoutSeconds.rows[0].tradeDateMs)
        // date only resolves to midnight
        assertEquals(at("2024-01-15 00:00:00"), dateOnly.rows[0].tradeDateMs)
        assertTrue(withoutSeconds.invalidIndices.isEmpty())
        assertTrue(dateOnly.invalidIndices.isEmpty())
    }

    @Test
    fun acceptsTemporalSeparator() {
        val result =
            CsvUtil.parseCsv(csv("2024-01-15T09:30:00,SH600000,浦发银行,买入,100,10.50,1060.00"), config)
        assertNotNull(result)
        assertEquals(at("2024-01-15 09:30:00"), result.rows[0].tradeDateMs)
    }

    // ---------- missing columns ----------

    @Test
    fun reportsMissingRequiredColumns() {
        val csvWithoutQuantity = "日期,资产代码,股票名称,交易类型,成交价格,成交金额\n2024-01-15 09:30,SH600000,浦发银行,买入,10.50,1060.00"
        val result = CsvUtil.parseCsv(csvWithoutQuantity, config)
        assertNotNull(result)
        assertEquals(listOf(config.quantityHeader), result.missingColumns)
    }

    @Test
    fun reportsMultipleMissingColumns() {
        val csvWithoutCodeAndAmount = "日期,股票名称,交易类型,股数,成交价格\n2024-01-15 09:30,浦发银行,买入,100,10.50"
        val result = CsvUtil.parseCsv(csvWithoutCodeAndAmount, config)
        assertNotNull(result)
        assertTrue(config.assetCodeHeader in result.missingColumns)
        assertTrue(config.amountHeader in result.missingColumns)
    }

    // ---------- toTransactions ----------

    @Test
    fun toTransactionsSkipsInvalidAndDeletedButKeepsDuplicates() {
        val row = "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00"
        val result = CsvUtil.parseCsv(csv(row, row, "bad-date,SH600000,浦发银行,买入,100,10.50,1060.00"), config)
        assertNotNull(result)

        // no deletions: duplicates kept, invalid skipped
        val all = CsvUtil.toTransactions(result)
        assertEquals(2, all.size)
        assertTrue(all.all { it.assetId == "SH600000" })

        // delete the first duplicate (row index 0): only the second duplicate remains
        val afterDelete = CsvUtil.toTransactions(result, deletedIndices = setOf(0))
        assertEquals(1, afterDelete.size)
    }

    @Test
    fun toTransactionsRespectsRowIndicesWithMalformedLines() {
        val result =
            CsvUtil.parseCsv(
                csv(
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50", // malformed → row 0
                    "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00", // row 1
                    "2024-01-16 09:30,SH600000,浦发银行,卖出,100,11.00,1090.00", // row 2
                ),
                config,
            )
        assertNotNull(result)

        // Deleting row index 1 must skip exactly the second row, keeping rows 2
        val transactions = CsvUtil.toTransactions(result, deletedIndices = setOf(1))
        assertEquals(1, transactions.size)
        assertEquals(TransactionType.SELL, transactions[0].type)
    }

    // ---------- preview model ----------

    @Test
    fun previewDuplicateCountTracksDeletedDuplicates() {
        val row = "2024-01-15 09:30,SH600000,浦发银行,买入,100,10.50,1060.00"
        val result = CsvUtil.parseCsv(csv(row, row), config)
        assertNotNull(result)

        val preview = CsvPreviewData(fileName = "test.csv", parseResult = result)
        assertEquals(1, preview.duplicateCount)
        assertEquals(2, preview.totalCount)
        assertEquals(1, preview.validCount)
        assertEquals(0, preview.invalidCount)

        preview.deletedIndices.add(1)
        assertEquals(0, preview.duplicateCount)
        // display items stay row-index aligned (all rows); the UI hides deleted rows
        assertEquals(2, preview.toDisplayItems().size)
    }

    @Test
    fun previewMarksInvalidRowsInDisplayItems() {
        val result =
            CsvUtil.parseCsv(
                csv("bad-date,SH600000,浦发银行,买入,100,10.50,1060.00"),
                config,
            )
        assertNotNull(result)
        val preview = CsvPreviewData(fileName = "test.csv", parseResult = result)
        assertEquals(1, preview.invalidCount)
        assertEquals(setOf(0), preview.invalidIndices)
        assertEquals(1, preview.toDisplayItems().size)
    }

    @Test
    fun fingerprintNormalizesPrecision() {
        assertEquals(
            TransactionFingerprint.normalize(10.5000001),
            TransactionFingerprint.normalize(10.5),
        )
        assertEquals(
            TransactionFingerprint.from(tx(quantity = 100.0, price = 10.5, amount = 1060.0)),
            TransactionFingerprint.from(tx(quantity = 100.0, price = 10.5, amount = 1060.0)),
        )
    }
}
