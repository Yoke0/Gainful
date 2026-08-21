package com.yoke.gainful.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.yoke.gainful.server.security.token.TokenConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.Principal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import kotlin.uuid.Uuid

data class UserPrincipal(val userId: Uuid, val sessionId: Uuid, val username: String) : Principal

fun Application.configureSecurity(config: TokenConfig) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = config.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build(),
            )

            validate { credential ->
                if (!credential.payload.audience.contains(config.audience)) {
                    null
                } else {
                    val subject = credential.payload.subject
                    val sessionId = credential.payload.getClaim("sessionId").asString()
                    val username = credential.payload.getClaim("username").asString()
                    if (subject == null || sessionId == null || username == null) {
                        null
                    } else {
                        try {
                            UserPrincipal(Uuid.parse(subject), Uuid.parse(sessionId), username)
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("UNAUTHORIZED", "Invalid or expired token"))
            }
        }
    }
}
