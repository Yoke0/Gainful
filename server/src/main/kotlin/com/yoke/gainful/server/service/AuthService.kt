package com.yoke.gainful.server.service

import com.yoke.gainful.api.AuthResponse
import com.yoke.gainful.api.RefreshTokenResponse
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.plugins.ConflictException
import com.yoke.gainful.server.plugins.UnauthorizedException
import com.yoke.gainful.server.plugins.ValidationException
import com.yoke.gainful.server.security.token.TokenClaim
import com.yoke.gainful.server.security.token.TokenConfig
import com.yoke.gainful.server.security.token.TokenService
import com.yoke.gainful.server.util.PasswordUtils
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.Uuid

class AuthService(
    private val tokenConfig: TokenConfig,
    private val sessionService: SessionService,
    private val tokenService: TokenService,
) {
    fun register(username: String, email: String, password: String, deviceInfo: String? = null, ipAddress: String? = null): AuthResponse {
        if (password.length < 6) throw ValidationException("Password must be at least 6 characters")

        val existing =
            transaction {
                Users.selectAll().where { (Users.username eq username) or (Users.email eq email) }.toList()
            }

        if (existing.any { it[Users.username] == username }) throw ConflictException("Username already exists")
        if (existing.any { it[Users.email] == email }) throw ConflictException("Email already exists")

        val userId = Uuid.random()
        transaction {
            Users.insert {
                it[Users.id] = userId
                it[Users.username] = username
                it[Users.email] = email
                it[Users.passwordHash] = PasswordUtils.hashPassword(password)
            }
        }

        val refreshToken =
            tokenService.generateRefreshToken(
                config = tokenConfig,
                TokenClaim("sub", userId.toString()),
                TokenClaim("type", "refresh"),
                TokenClaim("jti", Uuid.random().toString()),
            )
        val session =
            sessionService.createSession(
                userId = userId,
                refreshToken = refreshToken,
                refreshExpiresInMs = tokenConfig.refreshExpiresIn,
                deviceInfo = deviceInfo,
                ipAddress = ipAddress,
            )
        val accessToken =
            tokenService.generateAccessToken(
                config = tokenConfig,
                TokenClaim("sub", userId.toString()),
                TokenClaim("sessionId", session.id.toString()),
                TokenClaim("username", username),
            )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = session.refreshToken,
            userId = userId.toString(),
            username = username,
        )
    }

    fun login(username: String, password: String, deviceInfo: String?, ipAddress: String?): AuthResponse {
        val user =
            transaction {
                Users.selectAll().where { Users.username eq username }.singleOrNull()
            } ?: throw UnauthorizedException("Invalid username or password")

        if (!PasswordUtils.verifyPassword(password, user[Users.passwordHash])) {
            throw UnauthorizedException("Invalid username or password")
        }

        val refreshToken =
            tokenService.generateRefreshToken(
                config = tokenConfig,
                TokenClaim("sub", user[Users.id].toString()),
                TokenClaim("type", "refresh"),
                TokenClaim("jti", Uuid.random().toString()),
            )
        val session =
            sessionService.createSession(
                userId = user[Users.id],
                refreshToken = refreshToken,
                refreshExpiresInMs = tokenConfig.refreshExpiresIn,
                deviceInfo = deviceInfo,
                ipAddress = ipAddress,
            )

        val accessToken =
            tokenService.generateAccessToken(
                config = tokenConfig,
                TokenClaim("sub", user[Users.id].toString()),
                TokenClaim("sessionId", session.id.toString()),
                TokenClaim("username", user[Users.username]),
            )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = session.refreshToken,
            userId = user[Users.id].toString(),
            username = user[Users.username],
        )
    }

    fun refreshAccessToken(refreshToken: String): RefreshTokenResponse {
        val session = sessionService.validateRefreshToken(refreshToken)

        val user =
            transaction {
                Users.selectAll().where { Users.id eq session.userId }.singleOrNull()
            } ?: throw UnauthorizedException("User not found")

        val accessToken =
            tokenService.generateAccessToken(
                config = tokenConfig,
                TokenClaim("sub", session.userId.toString()),
                TokenClaim("sessionId", session.id.toString()),
                TokenClaim("username", user[Users.username]),
            )

        return RefreshTokenResponse(accessToken = accessToken)
    }
}
