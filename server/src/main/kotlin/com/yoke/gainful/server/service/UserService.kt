package com.yoke.gainful.server.service

import com.yoke.gainful.api.UpdateProfileRequest
import com.yoke.gainful.api.UserResponse
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.plugins.ConflictException
import com.yoke.gainful.server.plugins.NotFoundException
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Clock
import kotlin.uuid.Uuid

class UserService {
    fun getProfile(userId: Uuid): UserResponse {
        val user: ResultRow? =
            transaction {
                Users.selectAll().where { Users.id eq userId }.singleOrNull()
            }
        if (user == null) throw NotFoundException("User not found")

        return UserResponse(
            id = user[Users.id].toString(),
            username = user[Users.username],
            email = user[Users.email],
            nickname = user[Users.nickname],
            avatarUrl = user[Users.avatarUrl],
            createdAt = user[Users.createdAt].toString(),
        )
    }

    fun updateProfile(userId: Uuid, request: UpdateProfileRequest): UserResponse {
        transaction {
            val email = request.email
            if (email != null) {
                val existing: ResultRow? =
                    Users.selectAll().where {
                        (Users.email eq email) and (Users.id neq userId)
                    }.firstOrNull()
                if (existing != null) throw ConflictException("Email already in use")
            }

            Users.update({ Users.id eq userId }) {
                val nickname = request.nickname
                val email = request.email
                if (nickname != null) it[this.nickname] = nickname
                if (email != null) it[this.email] = email
                it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }

        return getProfile(userId)
    }
}
