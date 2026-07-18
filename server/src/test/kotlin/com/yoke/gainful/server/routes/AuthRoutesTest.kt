package com.yoke.gainful.server.routes

import com.yoke.gainful.api.AUTH_LOGIN
import com.yoke.gainful.api.AUTH_REGISTER
import com.yoke.gainful.server.config.UploadConfig
import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.plugins.configureRouting
import com.yoke.gainful.server.plugins.configureSecurity
import com.yoke.gainful.server.plugins.configureStatusPages
import com.yoke.gainful.server.security.token.JwtTokenService
import com.yoke.gainful.server.security.token.TokenConfig
import com.yoke.gainful.server.security.token.TokenService
import com.yoke.gainful.server.service.AuthService
import com.yoke.gainful.server.service.AvatarService
import com.yoke.gainful.server.service.SessionService
import com.yoke.gainful.server.service.TransactionService
import com.yoke.gainful.server.service.UserService
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class AuthRoutesTest {
    private lateinit var database: Database

    @BeforeTest
    fun setup() {
        database = Database.connect("jdbc:h2:mem:test_${Uuid.random()};DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(Users, Transactions, UserSessions)
        }
    }

    @AfterTest
    fun teardown() {
        transaction(database) {
            SchemaUtils.drop(Users, Transactions, UserSessions)
        }
    }

    private fun Application.testModule() {
        val testTokenConfig = TokenConfig("test-issuer", "test-audience", "test-realm", "test-secret", 86400000, 2592000000)
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
        configureSecurity(testTokenConfig)
        configureRouting()
    }

    @Test
    fun `POST register creates user`() =
        testApplication {
            application { testModule() }

            val response =
                client.post(AUTH_REGISTER) {
                    contentType(ContentType.Application.Json)
                    setBody("""{"username":"testuser","email":"test@test.com","password":"123456"}""")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.bodyAsText()
            assertTrue(body.contains("userId"))
        }

    @Test
    fun `POST register with duplicate username returns 409`() =
        testApplication {
            application { testModule() }

            client.post(AUTH_REGISTER) {
                contentType(ContentType.Application.Json)
                setBody("""{"username":"testuser","email":"test@test.com","password":"123456"}""")
            }

            val response =
                client.post(AUTH_REGISTER) {
                    contentType(ContentType.Application.Json)
                    setBody("""{"username":"testuser","email":"other@test.com","password":"123456"}""")
                }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

    @Test
    fun `POST login with valid credentials returns token`() =
        testApplication {
            application { testModule() }

            client.post(AUTH_REGISTER) {
                contentType(ContentType.Application.Json)
                setBody("""{"username":"testuser","email":"test@test.com","password":"123456"}""")
            }

            val response =
                client.post(AUTH_LOGIN) {
                    contentType(ContentType.Application.Json)
                    setBody("""{"username":"testuser","password":"123456"}""")
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.bodyAsText()
            assertTrue(body.contains("accessToken"))
        }

    @Test
    fun `POST login with wrong password returns 401`() =
        testApplication {
            application { testModule() }

            client.post(AUTH_REGISTER) {
                contentType(ContentType.Application.Json)
                setBody("""{"username":"testuser","email":"test@test.com","password":"123456"}""")
            }

            val response =
                client.post(AUTH_LOGIN) {
                    contentType(ContentType.Application.Json)
                    setBody("""{"username":"testuser","password":"wrong"}""")
                }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
}
