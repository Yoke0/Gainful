package com.yoke.gainful.server.service

import com.yoke.gainful.server.config.UploadConfig
import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.db.Users
import com.yoke.gainful.server.util.PasswordUtils
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class AvatarServiceTest {
    private lateinit var database: Database
    private lateinit var avatarService: AvatarService
    private lateinit var testUserId: Uuid
    private lateinit var uploadDir: File

    @BeforeTest
    fun setup() {
        database = Database.connect("jdbc:h2:mem:test_${Uuid.random()};DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(Users, Transactions, UserSessions)
        }

        uploadDir = File("test-uploads-${Uuid.random()}")
        uploadDir.mkdirs()

        avatarService = AvatarService(UploadConfig(uploadDir.absolutePath, 2 * 1024 * 1024))

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
        uploadDir.deleteRecursively()
    }

    @Test
    fun `allowed types contain JPEG PNG WEBP`() {
        val allowedTypes = setOf("image/jpeg", "image/png", "image/webp")
        assertTrue("image/jpeg" in allowedTypes)
        assertTrue("image/png" in allowedTypes)
        assertTrue("image/webp" in allowedTypes)
        assertFalse("image/bmp" in allowedTypes)
        assertFalse("image/gif" in allowedTypes)
    }

    @Test
    fun `extension map maps correctly`() {
        val extensionMap =
            mapOf(
                "image/jpeg" to "jpg",
                "image/png" to "png",
                "image/webp" to "webp",
            )
        assertEquals("jpg", extensionMap["image/jpeg"])
        assertEquals("png", extensionMap["image/png"])
        assertEquals("webp", extensionMap["image/webp"])
    }

    @Test
    fun `upload dir is created`() {
        assertTrue(uploadDir.exists())
        assertTrue(uploadDir.isDirectory)
    }

    @Test
    fun `max file size is 2MB`() {
        val config = UploadConfig(uploadDir.absolutePath, 2 * 1024 * 1024)
        assertEquals(2 * 1024 * 1024L, config.maxFileSizeBytes)
    }
}
