package com.yoke.gainful.server.routes

import com.yoke.gainful.api.UpdateProfileRequest
import com.yoke.gainful.server.plugins.UserPrincipal
import com.yoke.gainful.server.service.AvatarService
import com.yoke.gainful.server.service.SessionService
import com.yoke.gainful.server.service.UserService
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.userRoutes() {
    val userService by inject<UserService>()
    val sessionService by inject<SessionService>()
    val avatarService by inject<AvatarService>()

    authenticate("auth-jwt") {
        route("/users") {
            get("/me") {
                val principal = call.principal<UserPrincipal>()!!
                val user = userService.getProfile(principal.userId)
                call.respond(user)
            }

            put("/me") {
                val principal = call.principal<UserPrincipal>()!!
                val request = call.receive<UpdateProfileRequest>()
                val user = userService.updateProfile(principal.userId, request)
                call.respond(user)
            }

            post("/avatar") {
                val principal = call.principal<UserPrincipal>()!!
                val multipart = call.receiveMultipart()
                avatarService.uploadAvatar(principal.userId, multipart)
                val user = userService.getProfile(principal.userId)
                call.respond(user)
            }

            get("/sessions") {
                val principal = call.principal<UserPrincipal>()!!
                val sessions = sessionService.getSessions(principal.userId)
                call.respond(sessions)
            }

            delete("/sessions/{sessionId}") {
                val principal = call.principal<UserPrincipal>()!!
                val sessionId = Uuid.parse(call.parameters["sessionId"]!!)
                sessionService.revokeSession(principal.userId, sessionId)
                call.respond(mapOf("message" to "Session revoked"))
            }

            delete("/sessions") {
                val principal = call.principal<UserPrincipal>()!!
                sessionService.revokeAllOtherSessions(principal.userId, principal.sessionId)
                call.respond(mapOf("message" to "All other sessions revoked"))
            }
        }
    }
}
