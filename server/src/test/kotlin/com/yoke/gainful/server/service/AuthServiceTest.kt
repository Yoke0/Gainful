package com.yoke.gainful.server.service

import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.plugins.ConflictException
import com.yoke.gainful.server.plugins.UnauthorizedException
import com.yoke.gainful.server.plugins.ValidationException
import com.yoke.gainful.server.security.token.JwtTokenService
import com.yoke.gainful.server.security.token.TokenConfig
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class AuthServiceTest {
    private lateinit var database: Database
    private lateinit var authService: AuthService
    private lateinit var sessionService: SessionService

    @BeforeTest
    fun setup() {
        database = Database.connect("jdbc:h2:mem:test_${Uuid.random()};DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(Users, Transactions, UserSessions)
        }

        val tokenConfig = TokenConfig("test-issuer", "test-audience", "test-realm", "test-secret", 86400000)
        val tokenService = JwtTokenService()
        sessionService = mockk(relaxed = true)
        every { sessionService.createSession(any(), any(), any()) } returns
            SessionService.SessionInfo(
                id = Uuid.random(),
                userId = Uuid.random(),
            )
        authService = AuthService(tokenConfig, sessionService, tokenService)
    }

    @AfterTest
    fun teardown() {
        transaction(database) {
            SchemaUtils.drop(Users, Transactions, UserSessions)
        }
    }

    @Test
    fun `register creates user successfully`() {
        val response = authService.register("testuser", "test@test.com", "123456")
        assertNotNull(response.token)
        assertTrue(response.token.isNotEmpty())
        assertEquals("testuser", response.username)
    }

    @Test
    fun `register with duplicate username throws ConflictException`() {
        authService.register("testuser", "test@test.com", "123456")
        assertFailsWith<ConflictException> {
            authService.register("testuser", "other@test.com", "123456")
        }
    }

    @Test
    fun `register with duplicate email throws ConflictException`() {
        authService.register("user1", "test@test.com", "123456")
        assertFailsWith<ConflictException> {
            authService.register("user2", "test@test.com", "123456")
        }
    }

    @Test
    fun `register with short password throws ValidationException`() {
        assertFailsWith<ValidationException> {
            authService.register("testuser", "test@test.com", "123")
        }
    }

    @Test
    fun `login with valid credentials returns token`() {
        authService.register("testuser", "test@test.com", "123456")
        val response = authService.login("testuser", "123456", "TestDevice", "127.0.0.1")
        assertTrue(response.token.isNotEmpty())
        assertEquals("testuser", response.username)
    }

    @Test
    fun `login with wrong password throws UnauthorizedException`() {
        authService.register("testuser", "test@test.com", "123456")
        assertFailsWith<UnauthorizedException> {
            authService.login("testuser", "wrong", null, null)
        }
    }

    @Test
    fun `login with non-existent user throws UnauthorizedException`() {
        assertFailsWith<UnauthorizedException> {
            authService.login("nonexistent", "123456", null, null)
        }
    }

    @Test
    fun `login calls session service`() {
        authService.register("testuser", "test@test.com", "123456")
        authService.login("testuser", "123456", null, null)

        io.mockk.verify(exactly = 2) { sessionService.createSession(any(), any(), any()) }
    }
}
