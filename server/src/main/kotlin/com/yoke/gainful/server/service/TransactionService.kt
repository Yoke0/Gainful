package com.yoke.gainful.server.service

import com.yoke.gainful.server.db.Transactions
import com.yoke.gainful.server.model.dto.CreateTransactionRequest
import com.yoke.gainful.server.model.dto.TransactionResponse
import com.yoke.gainful.server.model.dto.UpdateTransactionRequest
import com.yoke.gainful.server.plugins.NotFoundException
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Clock
import kotlin.uuid.Uuid

class TransactionService {
    fun createTransaction(userId: Uuid, request: CreateTransactionRequest): TransactionResponse {
        val tradeDate = LocalDate.parse(request.tradeDate)

        // Check for duplicate by business key
        val existing =
            transaction {
                Transactions.selectAll().where {
                    (Transactions.userId eq userId) and
                        (Transactions.assetCode eq request.assetCode) and
                        (Transactions.type eq request.type) and
                        (Transactions.quantity eq request.quantity) and
                        (Transactions.price eq request.price) and
                        (Transactions.amount eq request.amount) and
                        (Transactions.tradeDate eq tradeDate)
                }.firstOrNull()
            }
        if (existing != null) {
            return getTransactionById(userId, existing[Transactions.id])!!
        }

        val id = Uuid.random()
        transaction {
            Transactions.insert {
                it[Transactions.id] = id
                it[Transactions.userId] = userId
                it[Transactions.assetCode] = request.assetCode
                it[Transactions.assetName] = request.assetName
                it[Transactions.type] = request.type
                it[Transactions.quantity] = request.quantity
                it[Transactions.price] = request.price
                it[Transactions.amount] = request.amount
                it[Transactions.tradeDate] = tradeDate
            }
        }

        return getTransactionById(userId, id)!!
    }

    fun getTransactions(userId: Uuid): List<TransactionResponse> {
        return transaction {
            Transactions.selectAll().where {
                (Transactions.userId eq userId) and Transactions.deletedAt.isNull()
            }
                .orderBy(Transactions.tradeDate, SortOrder.DESC)
                .map { it.toResponse() }
        }
    }

    fun getTransactionsSince(userId: Uuid, sinceMillis: Long): List<TransactionResponse> {
        val sinceDateTime =
            Instant.fromEpochMilliseconds(sinceMillis)
                .toLocalDateTime(TimeZone.currentSystemDefault())
        return transaction {
            Transactions.selectAll().where {
                (Transactions.userId eq userId) and
                    (Transactions.updatedAt greaterEq sinceDateTime) and
                    Transactions.deletedAt.isNull()
            }.orderBy(Transactions.tradeDate, SortOrder.DESC)
                .map { it.toResponse() }
        }
    }

    fun getTransactionById(userId: Uuid, transactionId: Uuid): TransactionResponse? {
        return transaction {
            Transactions.selectAll().where {
                (Transactions.id eq transactionId) and (Transactions.userId eq userId) and Transactions.deletedAt.isNull()
            }.singleOrNull()?.toResponse()
        }
    }

    fun updateTransaction(userId: Uuid, transactionId: Uuid, request: UpdateTransactionRequest): TransactionResponse {
        val existing: ResultRow? =
            transaction {
                Transactions.selectAll().where {
                    (Transactions.id eq transactionId) and (Transactions.userId eq userId)
                }.singleOrNull()
            }
        if (existing == null) throw NotFoundException("Transaction not found")

        transaction {
            Transactions.update({ Transactions.id eq transactionId }) {
                if (request.assetCode != null) it[assetCode] = request.assetCode
                if (request.assetName != null) it[assetName] = request.assetName
                if (request.type != null) it[type] = request.type
                if (request.quantity != null) it[quantity] = request.quantity
                if (request.price != null) it[price] = request.price
                if (request.amount != null) it[amount] = request.amount
                if (request.tradeDate != null) it[tradeDate] = LocalDate.parse(request.tradeDate)
                it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }

        return getTransactionById(userId, transactionId)!!
    }

    fun deleteTransaction(userId: Uuid, transactionId: Uuid) {
        val existing: ResultRow? =
            transaction {
                Transactions.selectAll().where {
                    (Transactions.id eq transactionId) and (Transactions.userId eq userId)
                }.singleOrNull()
            }
        if (existing == null) throw NotFoundException("Transaction not found")

        transaction {
            Transactions.update({ Transactions.id eq transactionId }) {
                it[deletedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }
    }

    private fun ResultRow.toResponse(): TransactionResponse =
        TransactionResponse(
            id = this[Transactions.id].toString(),
            assetCode = this[Transactions.assetCode],
            assetName = this[Transactions.assetName],
            type = this[Transactions.type],
            quantity = this[Transactions.quantity],
            price = this[Transactions.price],
            amount = this[Transactions.amount],
            tradeDate = this[Transactions.tradeDate].toString(),
            createdAt = this[Transactions.createdAt].toString(),
            updatedAt = this[Transactions.updatedAt].toString(),
            deletedAt = this[Transactions.deletedAt]?.toString(),
        )
}
