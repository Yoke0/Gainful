package com.yoke.gainful.server.service

import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.plugins.ForbiddenException
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class SessionServiceTest {
    private lateinit var database: Database
    private lateinit var sessionService: SessionService
    private lateinit var testUserId: Uuid

    @BeforeTest
    fun setup() {
        database = Database.connect("jdbc:h2:mem:test_${Uuid.random()};DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(Users, Transactions, UserSessions)
        }

        sessionService = SessionService()

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
    fun `createSession creates session record`() {
        val session = sessionService.createSession(testUserId, "TestDevice", "127.0.0.1")
        assertNotNull(session.id)
        assertEquals(testUserId, session.userId)
    }

    @Test
    fun `getSessions returns user sessions`() {
        sessionService.createSession(testUserId, "Device1", "127.0.0.1")
        sessionService.createSession(testUserId, "Device2", "192.168.1.1")

        val sessions = sessionService.getSessions(testUserId)
        assertEquals(2, sessions.size)
    }

    @Test
    fun `revokeSession marks session as revoked`() {
        val session = sessionService.createSession(testUserId, "Device1", "127.0.0.1")
        sessionService.revokeSession(testUserId, session.id)

        assertFalse(sessionService.isSessionValid(session.id))
    }

    @Test
    fun `revokeSession for non-existent session throws NotFoundException`() {
        assertFailsWith<NotFoundException> {
            sessionService.revokeSession(testUserId, Uuid.random())
        }
    }

    @Test
    fun `revokeSession for other user's session throws ForbiddenException`() {
        val session = sessionService.createSession(testUserId, "Device1", "127.0.0.1")
        val otherUserId = Uuid.random()

        assertFailsWith<ForbiddenException> {
            sessionService.revokeSession(otherUserId, session.id)
        }
    }

    @Test
    fun `revokeAllOtherSessions revokes all except current`() {
        val session1 = sessionService.createSession(testUserId, "Device1", "127.0.0.1")
        val session2 = sessionService.createSession(testUserId, "Device2", "192.168.1.1")

        sessionService.revokeAllOtherSessions(testUserId, session1.id)

        assertTrue(sessionService.isSessionValid(session1.id))
        assertFalse(sessionService.isSessionValid(session2.id))
    }
}
