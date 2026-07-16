package com.yoke.gainful.server.routes

import com.yoke.gainful.server.model.dto.CreateTransactionRequest
import com.yoke.gainful.server.model.dto.UpdateTransactionRequest
import com.yoke.gainful.server.plugins.UserPrincipal
import com.yoke.gainful.server.plugins.ValidationException
import com.yoke.gainful.server.service.TransactionService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.transactionRoutes() {
    val transactionService by inject<TransactionService>()

    authenticate("auth-jwt") {
        route("/transactions") {
            get {
                val principal = call.principal<UserPrincipal>()!!
                val since = call.request.queryParameters["since"]?.toLongOrNull()
                val transactions =
                    if (since != null) {
                        transactionService.getTransactionsSince(principal.userId, since)
                    } else {
                        transactionService.getTransactions(principal.userId)
                    }
                call.respond(transactions)
            }

            get("/{id}") {
                val principal = call.principal<UserPrincipal>()!!
                val id = Uuid.parse(call.parameters["id"]!!)
                val transaction = transactionService.getTransactionById(principal.userId, id)
                if (transaction != null) {
                    call.respond(transaction)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Transaction not found"))
                }
            }

            post {
                val principal = call.principal<UserPrincipal>()!!
                val request = call.receive<CreateTransactionRequest>()

                if (request.assetCode.isBlank()) throw ValidationException("Asset code is required")
                if (request.amount <= 0) throw ValidationException("Amount must be positive")

                val transaction = transactionService.createTransaction(principal.userId, request)
                call.respond(HttpStatusCode.Created, transaction)
            }

            put("/{id}") {
                val principal = call.principal<UserPrincipal>()!!
                val id = Uuid.parse(call.parameters["id"]!!)
                val request = call.receive<UpdateTransactionRequest>()
                val transaction = transactionService.updateTransaction(principal.userId, id, request)
                call.respond(transaction)
            }

            delete("/{id}") {
                val principal = call.principal<UserPrincipal>()!!
                val id = Uuid.parse(call.parameters["id"]!!)
                transactionService.deleteTransaction(principal.userId, id)
                call.respond(mapOf("message" to "Transaction deleted"))
            }
        }
    }
}
