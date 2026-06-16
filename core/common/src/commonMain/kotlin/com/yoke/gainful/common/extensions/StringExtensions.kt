package com.yoke.gainful.common.extensions

fun String.Companion.empty(): String = ""

fun String?.orDefault(default: String = ""): String = this ?: default
