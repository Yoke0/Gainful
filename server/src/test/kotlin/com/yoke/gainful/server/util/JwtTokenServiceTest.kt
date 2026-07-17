package com.yoke.gainful.server.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.yoke.gainful.server.security.token.JwtTokenService
import com.yoke.gainful.server.security.token.TokenClaim
import com.yoke.gainful.server.security.token.TokenConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JwtTokenServiceTest {
    private val config =
        TokenConfig(
            secret = "test-secret-key-for-testing",
            issuer = "test-issuer",
            audience = "test-audience",
            realm = "test-realm",
            expiresIn = 86400000,
            refreshExpiresIn = 2592000000,
        )

    private val tokenService = JwtTokenService()

    @Test
    fun `generateAccessToken produces valid JWT`() {
        val token =
            tokenService.generateAccessToken(
                config,
                TokenClaim("sub", "user-id"),
                TokenClaim("username", "testuser"),
            )

        assertTrue(token.isNotEmpty())
        assertTrue(token.split(".").size == 3)
    }

    @Test
    fun `generated token contains correct claims`() {
        val token =
            tokenService.generateAccessToken(
                config,
                TokenClaim("sub", "user-id"),
                TokenClaim("username", "testuser"),
            )

        val verifier =
            JWT.require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()

        val decoded = verifier.verify(token)
        assertEquals("user-id", decoded.subject)
        assertEquals("testuser", decoded.getClaim("username").asString())
        assertEquals("test-audience", decoded.audience.first())
    }

    @Test
    fun `generateRefreshToken produces valid JWT`() {
        val token =
            tokenService.generateRefreshToken(
                config,
                TokenClaim("sub", "user-id"),
                TokenClaim("type", "refresh"),
            )

        assertTrue(token.isNotEmpty())
        assertTrue(token.split(".").size == 3)
    }

    @Test
    fun `invalid token throws verification exception`() {
        val verifier =
            JWT.require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()

        try {
            verifier.verify("invalid.token.here")
            throw AssertionError("Should have thrown")
        } catch (e: Exception) {
            // Expected
        }
    }
}
