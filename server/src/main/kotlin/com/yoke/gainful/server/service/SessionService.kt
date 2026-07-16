package com.yoke.gainful.server.service

import com.yoke.gainful.api.SessionResponse
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.plugins.ForbiddenException
import com.yoke.gainful.server.plugins.NotFoundException
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Clock
import kotlin.uuid.Uuid

class SessionService {
    data class SessionInfo(
        val id: Uuid,
        val userId: Uuid,
    )

    fun createSession(userId: Uuid, deviceInfo: String?, ipAddress: String?): SessionInfo {
        val sessionId = Uuid.random()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val expiresAt =
            kotlinx.datetime.LocalDateTime(
                year = now.year,
                monthNumber = now.monthNumber,
                dayOfMonth = now.dayOfMonth + 1,
                hour = now.hour,
                minute = now.minute,
                second = now.second,
                nanosecond = now.nanosecond,
            )

        transaction {
            UserSessions.insert {
                it[UserSessions.id] = sessionId
                it[UserSessions.userId] = userId
                it[UserSessions.deviceInfo] = deviceInfo
                it[UserSessions.ipAddress] = ipAddress
                it[UserSessions.expiresAt] = expiresAt
            }
        }

        return SessionInfo(
            id = sessionId,
            userId = userId,
        )
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
                    expiresAt = row[UserSessions.expiresAt].toString(),
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

    fun revokeAllOtherSessions(userId: Uuid, currentSessionId: Uuid) {
        transaction {
            UserSessions.update({
                UserSessions.userId eq userId and (UserSessions.id neq currentSessionId)
            }) {
                it[isRevoked] = true
            }
        }
    }

    fun isSessionValid(sessionId: Uuid): Boolean {
        return transaction {
            val session: ResultRow? = UserSessions.selectAll().where { UserSessions.id eq sessionId }.singleOrNull()
            session?.let { row ->
                !row[UserSessions.isRevoked] && row[UserSessions.expiresAt] >
                    Clock.System.now().toLocalDateTime(
                        TimeZone.currentSystemDefault(),
                    )
            } ?: false
        }
    }
}
