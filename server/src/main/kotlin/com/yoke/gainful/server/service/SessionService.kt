package com.yoke.gainful.server.service

import com.auth0.jwt.JWT
import com.yoke.gainful.api.SessionResponse
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.plugins.ForbiddenException
import com.yoke.gainful.server.plugins.NotFoundException
import com.yoke.gainful.server.plugins.UnauthorizedException
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

class SessionService {
    data class SessionInfo(
        val id: Uuid,
        val userId: Uuid,
    )

    fun createSession(
        sessionId: Uuid,
        userId: Uuid,
        expiresInMs: Long,
        deviceInfo: String?,
        ipAddress: String?,
    ): SessionInfo {
        val now = Clock.System.now()
        val expiresAt =
            Instant.fromEpochMilliseconds(now.toEpochMilliseconds() + expiresInMs)
                .toLocalDateTime(TimeZone.currentSystemDefault())

        transaction {
            UserSessions.insert {
                it[UserSessions.id] = sessionId
                it[UserSessions.userId] = userId
                it[UserSessions.deviceInfo] = deviceInfo
                it[UserSessions.ipAddress] = ipAddress
                it[UserSessions.expiresAt] = expiresAt
            }
        }

        return SessionInfo(id = sessionId, userId = userId)
    }

    fun getSessions(userId: Uuid): List<SessionResponse> {
        return transaction {
            UserSessions.selectAll().where {
                UserSessions.userId eq userId and (UserSessions.isRevoked eq false)
            }.orderBy(UserSessions.createdAt, SortOrder.DESC).map { row ->
                SessionResponse(
                    id = row[UserSessions.id].toString(),
                    deviceInfo = row[UserSessions.deviceInfo],
                    ipAddress = row[UserSessions.ipAddress],
                    createdAt = row[UserSessions.createdAt].toString(),
                    isRevoked = row[UserSessions.isRevoked],
                )
            }
        }
    }

    fun revokeSession(userId: Uuid, sessionId: Uuid) {
        val session: ResultRow? =
            transaction {
                UserSessions.selectAll().where { UserSessions.id eq sessionId }.singleOrNull()
            }
        if (session == null) throw NotFoundException("Session not found")

        if (session[UserSessions.userId] != userId) {
            throw ForbiddenException("Cannot revoke another user's session")
        }

        transaction {
            UserSessions.update({ UserSessions.id eq sessionId }) {
                it[isRevoked] = true
            }
        }
    }

    fun validateRefreshToken(refreshToken: String): SessionInfo {
        val jti =
            try {
                JWT.decode(refreshToken).getClaim("jti").asString()
            } catch (_: Exception) {
                throw UnauthorizedException("Invalid refresh token")
            }

        if (jti.isNullOrBlank()) throw UnauthorizedException("Invalid refresh token")

        val sessionId =
            try {
                Uuid.parse(jti)
            } catch (_: Exception) {
                throw UnauthorizedException("Invalid refresh token")
            }

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val session: ResultRow? =
            transaction {
                UserSessions.selectAll().where {
                    UserSessions.id eq sessionId
                }.singleOrNull()
            }

        if (session == null) throw UnauthorizedException("Invalid refresh token")
        if (session[UserSessions.isRevoked]) throw UnauthorizedException("Refresh token has been revoked")

        val sessionExpiresAt = session[UserSessions.expiresAt]
        if (sessionExpiresAt == null || sessionExpiresAt < now) {
            throw UnauthorizedException("Refresh token has expired")
        }

        return SessionInfo(
            id = session[UserSessions.id],
            userId = session[UserSessions.userId],
        )
    }
}
