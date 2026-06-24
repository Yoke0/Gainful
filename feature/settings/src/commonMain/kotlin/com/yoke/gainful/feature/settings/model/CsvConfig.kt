package com.yoke.gainful.feature.settings.model

data class CsvConfig(
    val headers: List<String>,
    val typeValues: List<String>,
) {
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
