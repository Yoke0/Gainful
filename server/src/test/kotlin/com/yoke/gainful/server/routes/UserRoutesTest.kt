package com.yoke.gainful.server.routes

import com.yoke.gainful.server.config.UploadConfig
import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.plugins.configureRouting
import com.yoke.gainful.server.plugins.configureSecurity
import com.yoke.gainful.server.plugins.configureStatusPages
import com.yoke.gainful.server.security.token.JwtTokenService
import com.yoke.gainful.server.security.token.TokenClaim
import com.yoke.gainful.server.security.token.TokenConfig
import com.yoke.gainful.server.security.token.TokenService
import com.yoke.gainful.server.service.AuthService
import com.yoke.gainful.server.service.AvatarService
import com.yoke.gainful.server.service.SessionService
import com.yoke.gainful.server.service.TransactionService
import com.yoke.gainful.server.service.UserService
import com.yoke.gainful.server.util.PasswordUtils
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.uuid.Uuid

class UserRoutesTest {
    private lateinit var database: Database
    private lateinit var tokenService: TokenService
    private lateinit var tokenConfig: TokenConfig
    private lateinit var testUserId: Uuid
    private lateinit var testSessionId: Uuid
    private lateinit var testToken: String

    @BeforeTest
    fun setup() {
        database = Database.connect("jdbc:h2:mem:test_${Uuid.random()};DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(Users, Transactions, UserSessions)
        }

        tokenConfig = TokenConfig("test-issuer", "test-audience", "test-realm", "test-secret", 86400000)
        tokenService = JwtTokenService()

        testUserId = Uuid.random()
        testSessionId = Uuid.random()

        transaction(database) {
            Users.insert {
                it[id] = testUserId
                it[username] = "testuser"
                it[email] = "test@test.com"
                it[passwordHash] = PasswordUtils.hashPassword("123456")
            }
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            UserSessions.insert {
                it[id] = testSessionId
                it[userId] = testUserId
                it[expiresAt] =
                    kotlinx.datetime.LocalDateTime(
                        year = now.year,
                        monthNumber = now.monthNumber,
                        dayOfMonth = now.dayOfMonth + 1,
                        hour = now.hour,
                        minute = now.minute,
                        second = now.second,
                        nanosecond = now.nanosecond,
                    )
            }
        }

        testToken =
            tokenService.generate(
                tokenConfig,
                TokenClaim("sub", testUserId.toString()),
                TokenClaim("sessionId", testSessionId.toString()),
                TokenClaim("username", "testuser"),
            )
    }

    @AfterTest
    fun teardown() {
        transaction(database) {
            SchemaUtils.drop(Users, Transactions, UserSessions)
        }
    }

    private fun Application.testModule() {
        val testTokenConfig = TokenConfig("test-issuer", "test-audience", "test-realm", "test-secret", 86400000)
        val sessionService = SessionService()

        install(ContentNegotiation) { json() }
        configureStatusPages()
        install(Koin) {
            modules(
                module {
                    single { UploadConfig("test-uploads", 2 * 1024 * 1024) }
                    single<TokenService> { JwtTokenService() }
                    single { testTokenConfig }
                    single { AuthService(get(), get(), get()) }
                    single { UserService() }
                    single { sessionService }
                    single { TransactionService() }
                    single { AvatarService(get()) }
                },
            )
        }
        configureSecurity(testTokenConfig, sessionService)
        configureRouting()
    }

    @Test
    fun `GET users me returns user info`() =
        testApplication {
            application { testModule() }

            val response =
                client.get("/api/users/me") {
                    header(HttpHeaders.Authorization, "Bearer $testToken")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.bodyAsText()
            assertTrue(body.contains("testuser"))
            assertTrue(body.contains("test@test.com"))
        }

    @Test
    fun `GET users me without token returns 401`() =
        testApplication {
            application { testModule() }

            val response = client.get("/api/users/me")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `PUT users me updates profile`() =
        testApplication {
            application { testModule() }

            val response =
                client.put("/api/users/me") {
                    header(HttpHeaders.Authorization, "Bearer $testToken")
                    contentType(ContentType.Application.Json)
                    setBody("""{"nickname":"New Nickname"}""")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.bodyAsText()
            assertTrue(body.contains("New Nickname"))
        }

    @Test
    fun `GET users sessions returns session list`() =
        testApplication {
            application { testModule() }

            val response =
                client.get("/api/users/sessions") {
                    header(HttpHeaders.Authorization, "Bearer $testToken")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.bodyAsText()
            assertTrue(body.contains("id"))
        }

    @Test
    fun `DELETE users sessions revokes other sessions`() =
        testApplication {
            application { testModule() }

            val response =
                client.delete("/api/users/sessions") {
                    header(HttpHeaders.Authorization, "Bearer $testToken")
                }

            assertEquals(HttpStatusCode.OK, response.status)
        }
}
