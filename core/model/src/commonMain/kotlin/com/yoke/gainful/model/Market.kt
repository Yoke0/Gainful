package com.yoke.gainful.model

enum class Market(val code: Int) {
    SHENZHEN(0),
    SHANGHAI(1),
    BEIJING(2),
    ;

    companion object {
        fun fromCode(code: Int): Market = entries.first { it.code == code }
    }
}

fun secId(market: Market, stockCode: String): String = "${market.code}.$stockCode"
