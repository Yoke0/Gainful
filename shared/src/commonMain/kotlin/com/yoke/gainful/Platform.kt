package com.yoke.gainful

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
