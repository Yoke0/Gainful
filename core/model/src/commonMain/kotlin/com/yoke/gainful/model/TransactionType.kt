package com.yoke.gainful.model

enum class TransactionType(val value: Int) {
    BUY(0),
    SELL(1),
    DIVIDEND(2);

    companion object {
        fun fromCode(code: Int): TransactionType =
            entries.firstOrNull { it.value == code } ?: BUY
    }
}
