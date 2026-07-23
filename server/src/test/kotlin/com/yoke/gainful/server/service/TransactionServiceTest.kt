package com.yoke.gainful.server.service

import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.api.UpdateTransactionRequest
import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.db.UserSessions
import com.yoke.gainful.server.db.Users
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class TransactionServiceTest {
    private lateinit var database: Database
    private lateinit var transactionService: TransactionService
    private lateinit var testUserId: Uuid
    private lateinit var otherUserId: Uuid

    @BeforeTest
    fun setup() {
        database = Database.connect("jdbc:h2:mem:test_${Uuid.random()};DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(database) {
            SchemaUtils.create(Users, Transactions, UserSessions)
        }

        transactionService = TransactionService()

        testUserId = Uuid.random()
        otherUserId = Uuid.random()
        transaction(database) {
            Users.insert {
                it[id] = testUserId
                it[username] = "testuser"
                it[email] = "test@test.com"
                it[passwordHash] = PasswordUtils.hashPassword("123456")
            }
            Users.insert {
                it[id] = otherUserId
                it[username] = "otheruser"
                it[email] = "other@test.com"
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
    fun `createTransaction creates record successfully`() {
        val request =
            CreateTransactionRequest(
                assetCode = "600519",
                assetName = "贵州茅台",
                type = 0,
                quantity = 100.0,
                price = 1800.0,
                amount = 180000.0,
                tradeDate = "2025-01-15T10:30:00",
            )

        val response = transactionService.createTransaction(testUserId, request)
        assertNotNull(response.id)
        assertEquals("600519", response.assetCode)
        assertEquals(0, response.type)
    }

    @Test
    fun `getTransactions returns user transactions`() {
        val request =
            CreateTransactionRequest(
                assetCode = "600519",
                type = 0,
                quantity = 100.0,
                price = 1800.0,
                amount = 180000.0,
                tradeDate = "2025-01-15T10:30:00",
            )
        transactionService.createTransaction(testUserId, request)

        val transactions = transactionService.getTransactions(testUserId)
        assertEquals(1, transactions.size)
    }

    @Test
    fun `getTransactionById returns correct record`() {
        val request =
            CreateTransactionRequest(
                assetCode = "600519",
                type = 0,
                quantity = 100.0,
                price = 1800.0,
                amount = 180000.0,
                tradeDate = "2025-01-15T10:30:00",
            )
        val created = transactionService.createTransaction(testUserId, request)

        val found = transactionService.getTransactionById(testUserId, Uuid.parse(created.id))
        assertNotNull(found)
        assertEquals("600519", found.assetCode)
    }

    @Test
    fun `getTransactionById returns null for non-existent`() {
        val result = transactionService.getTransactionById(testUserId, Uuid.random())
        assertNull(result)
    }

    @Test
    fun `getTransactionById returns null for other user's record`() {
        val request =
            CreateTransactionRequest(
                assetCode = "600519",
                type = 0,
                quantity = 100.0,
                price = 1800.0,
                amount = 180000.0,
                tradeDate = "2025-01-15T10:30:00",
            )
        val created = transactionService.createTransaction(testUserId, request)

        val result = transactionService.getTransactionById(otherUserId, Uuid.parse(created.id))
        assertNull(result)
    }

    @Test
    fun `updateTransaction updates fields`() {
        val request =
            CreateTransactionRequest(
                assetCode = "600519",
                type = 0,
                quantity = 100.0,
                price = 1800.0,
                amount = 180000.0,
                tradeDate = "2025-01-15T10:30:00",
            )
        val created = transactionService.createTransaction(testUserId, request)

        val updateRequest = UpdateTransactionRequest(quantity = 200.0, amount = 360000.0)
        val updated = transactionService.updateTransaction(testUserId, Uuid.parse(created.id), updateRequest)

        assertEquals(200.0, updated.quantity)
        assertEquals(360000.0, updated.amount)
    }

    @Test
    fun `updateTransaction for non-existent throws NotFoundException`() {
        assertFailsWith<NotFoundException> {
            transactionService.updateTransaction(testUserId, Uuid.random(), UpdateTransactionRequest(quantity = 200.0))
        }
    }

    @Test
    fun `deleteTransaction removes record`() {
        val request =
            CreateTransactionRequest(
                assetCode = "600519",
                type = 0,
                quantity = 100.0,
                price = 1800.0,
                amount = 180000.0,
                tradeDate = "2025-01-15T10:30:00",
            )
        val created = transactionService.createTransaction(testUserId, request)

        transactionService.deleteTransaction(testUserId, Uuid.parse(created.id))

        val result = transactionService.getTransactionById(testUserId, Uuid.parse(created.id))
        assertNull(result)
    }

    @Test
    fun `deleteTransaction for non-existent throws NotFoundException`() {
        assertFailsWith<NotFoundException> {
            transactionService.deleteTransaction(testUserId, Uuid.random())
        }
    }
}
