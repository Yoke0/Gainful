package com.yoke.gainful.feature.settings.model

data class CsvConfig(
    val headers: List<String>,
    val typeValues: List<String>,
) {
    init {
        require(headers.size >= 7) { "CsvConfig requires at least 7 headers, got ${headers.size}" }
        require(typeValues.size >= 3) { "CsvConfig requires at least 3 type values, got ${typeValues.size}" }
    }

    val dateHeader: String get() = headers[0]
    val assetCodeHeader: String get() = headers[1]
    val assetNameHeader: String get() = headers[2]
    val typeHeader: String get() = headers[3]
    val quantityHeader: String get() = headers[4]
    val priceHeader: String get() = headers[5]
    val amountHeader: String get() = headers[6]
    val buyType: String get() = typeValues[0]
    val sellType: String get() = typeValues[1]
    val dividendType: String get() = typeValues[2]
}
