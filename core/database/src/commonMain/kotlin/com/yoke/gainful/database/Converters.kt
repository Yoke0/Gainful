package com.yoke.gainful.database

import androidx.room.TypeConverter
import com.yoke.gainful.model.StockTrend
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class StockTrendDto(
    val time: String,
    val price: Double,
)

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStockTrendList(trends: List<StockTrend>): String {
        val dtos =
            trends.map {
                StockTrendDto(
                    time = it.time,
                    price = it.price,
                )
            }
        return json.encodeToString(dtos)
    }

    @TypeConverter
    fun toStockTrendList(jsonStr: String): List<StockTrend> =
        runCatching {
            val dtos = json.decodeFromString<List<StockTrendDto>>(jsonStr)
            dtos.map { dto ->
                StockTrend(
                    time = dto.time,
                    price = dto.price,
                )
            }
        }.getOrDefault(emptyList())
}
