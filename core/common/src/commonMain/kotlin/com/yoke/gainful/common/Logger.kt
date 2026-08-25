package com.yoke.gainful.common

/**
 * Lightweight KMP logger that wraps [println] with log levels.
 * Output goes to platform default: logcat (Android), NSLog (iOS), stdout (JVM).
 */
object Logger {
    fun debug(tag: String, message: String) {
        println("[DEBUG] $tag: $message")
    }

    fun info(tag: String, message: String) {
        println("[INFO] $tag: $message")
    }

    fun warn(tag: String, message: String) {
        println("[WARN] $tag: $message")
    }

    fun error(tag: String, message: String, throwable: Throwable? = null) {
        val suffix = throwable?.let { " | ${it::class.simpleName}: ${it.message}" } ?: ""
        println("[ERROR] $tag: $message$suffix")
    }
}
