package com.yoke.gainful.server.service

import com.yoke.gainful.api.UpdateProfileRequest
import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.plugins.ConflictException
import com.yoke.gainful.server.plugins.NotFoundException
import com.yoke.gainful.server.util.PasswordUtils
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class UserServiceTest {
    private lateinit var database: Database
    private lateinit var userService: UserService
    private lateinit var testUserId: Uuid

    @BeforeTest
    fun setup() {
        database = Database.connect("jdbc:h2:mem:test_${Uuid.random()};DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(Users, Transactions, UserSessions)
        }

        userService = UserService()

        testUserId = Uuid.random()
        transaction(database) {
            Users.insert {
                it[id] = testUserId
                it[username] = "testuser"
                it[email] = "test@test.com"
                it[passwordHash] = PasswordUtils.hashPassword("123456")
            }
        }
    }

    @AfterTest
    fun teardown() {
        transaction(database) {
            SchemaUtils.drop(Users, Transactions, UserSessions)
        }
    }

    @Test
    fun `getProfile returns user info`() {
        val profile = userService.getProfile(testUserId)
        assertEquals("testuser", profile.username)
        assertEquals("test@test.com", profile.email)
    }

    @Test
    fun `getProfile for non-existent user throws NotFoundException`() {
        assertFailsWith<NotFoundException> {
            userService.getProfile(Uuid.random())
        }
    }

    @Test
    fun `updateProfile updates nickname`() {
        val updated = userService.updateProfile(testUserId, UpdateProfileRequest(nickname = "New Nickname"))
        assertEquals("New Nickname", updated.nickname)
    }

    @Test
    fun `updateProfile updates email`() {
        val updated = userService.updateProfile(testUserId, UpdateProfileRequest(email = "new@test.com"))
        assertEquals("new@test.com", updated.email)
    }

    @Test
    fun `updateProfile with duplicate email throws ConflictException`() {
        // Create another user
        val otherId = Uuid.random()
        transaction(database) {
            Users.insert {
                it[id] = otherId
                it[username] = "other"
                it[email] = "other@test.com"
                it[passwordHash] = PasswordUtils.hashPassword("123456")
            }
        }

        assertFailsWith<ConflictException> {
            userService.updateProfile(testUserId, UpdateProfileRequest(email = "other@test.com"))
        }
    }
}
