package com.yoke.gainful.server.security.token

interface TokenService {
    fun generate(config: TokenConfig, vararg claims: TokenClaim): String
}
