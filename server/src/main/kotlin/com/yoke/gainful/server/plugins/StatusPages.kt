package com.yoke.gainful.server.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.exception
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String, val message: String)

class NotFoundException(message: String) : Exception(message)

class ConflictException(message: String) : Exception(message)

class UnauthorizedException(message: String) : Exception(message)

class ForbiddenException(message: String) : Exception(message)

class ValidationException(message: String) : Exception(message)

class UnsupportedMediaTypeException(message: String) : Exception(message)

class PayloadTooLargeException(message: String) : Exception(message)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse("NOT_FOUND", cause.message ?: "Resource not found"))
        }
        exception<ConflictException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, ErrorResponse("CONFLICT", cause.message ?: "Resource already exists"))
        }
        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("UNAUTHORIZED", cause.message ?: "Authentication required"))
        }
        exception<ForbiddenException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, ErrorResponse("FORBIDDEN", cause.message ?: "Access denied"))
        }
        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("VALIDATION_ERROR", cause.message ?: "Invalid input"))
        }
        exception<UnsupportedMediaTypeException> { call, cause ->
            call.respond(
                HttpStatusCode.UnsupportedMediaType,
                ErrorResponse("UNSUPPORTED_MEDIA_TYPE", cause.message ?: "Unsupported file type"),
            )
        }
        exception<PayloadTooLargeException> { call, cause ->
            call.respond(HttpStatusCode.PayloadTooLarge, ErrorResponse("PAYLOAD_TOO_LARGE", cause.message ?: "File too large"))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("INTERNAL_ERROR", cause.message ?: "Internal server error"))
        }
    }
}
