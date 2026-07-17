package com.yoke.gainful.server.routes

import com.yoke.gainful.api.AUTH_LOGIN
import com.yoke.gainful.api.AUTH_REFRESH
import com.yoke.gainful.api.AUTH_REGISTER
import com.yoke.gainful.api.LoginRequest
import com.yoke.gainful.api.RefreshTokenRequest
import com.yoke.gainful.api.RegisterRequest
import com.yoke.gainful.server.plugins.ValidationException
import com.yoke.gainful.server.service.AuthService
import io.ktor.server.request.receive
import io.ktor.server.request.userAgent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authService by inject<AuthService>()

    post(AUTH_REGISTER) {
        val request = call.receive<RegisterRequest>()

        if (request.username.isBlank()) throw ValidationException("Username is required")
        if (request.email.isBlank()) throw ValidationException("Email is required")
        if (request.password.isBlank()) throw ValidationException("Password is required")

        val deviceInfo = call.request.userAgent()
        val ipAddress = call.request.local.remoteAddress
        val response = authService.register(request.username, request.email, request.password, deviceInfo, ipAddress)
        call.respond(response)
    }

    post(AUTH_LOGIN) {
        val request = call.receive<LoginRequest>()
        val deviceInfo = call.request.userAgent()
        val ipAddress = call.request.local.remoteAddress

        val response = authService.login(request.username, request.password, deviceInfo, ipAddress)
        call.respond(response)
    }

    post(AUTH_REFRESH) {
        val request = call.receive<RefreshTokenRequest>()
        val response = authService.refreshAccessToken(request.refreshToken)
        call.respond(response)
    }
}
