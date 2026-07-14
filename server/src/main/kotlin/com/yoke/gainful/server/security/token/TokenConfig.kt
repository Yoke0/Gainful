package com.yoke.gainful.server.security.token

data class TokenConfig(
    val issuer: String,
    val audience: String,
    val realm: String,
    val secret: String,
    val expiresIn: Long,
)
