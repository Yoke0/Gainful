package com.yoke.gainful.data.model

import com.yoke.gainful.model.KLine
import com.yoke.gainful.network.model.KLineData

fun KLineData.toKLineList(): List<KLine> {
    return klines.map { line ->
        val parts = line.split(",")
        KLine(
            date = parts[0],
            open = parts.getOrElse(1) { "0" }.toDouble(),
            close = parts.getOrElse(2) { "0" }.toDouble(),
            high = parts.getOrElse(3) { "0" }.toDouble(),
            low = parts.getOrElse(4) { "0" }.toDouble(),
            volume = parts.getOrElse(5) { "0" }.toLong(),
            turnover = parts.getOrElse(6) { "0" }.toDouble(),
            amplitude = parts.getOrElse(7) { "0" }.toDouble(),
            changePercent = parts.getOrElse(8) { "0" }.toDouble(),
            changeAmount = parts.getOrElse(9) { "0" }.toDouble(),
            turnoverRate = parts.getOrElse(10) { "0" }.toDouble(),
        )
    }
}
