package com.yoke.gainful.server.security.token

interface TokenService {
    fun generateAccessToken(config: TokenConfig, vararg claims: TokenClaim): String

    fun generateRefreshToken(config: TokenConfig, vararg claims: TokenClaim): String
}
