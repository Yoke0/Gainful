package com.yoke.gainful.server

import com.yoke.gainful.server.config.DatabaseFactory
import com.yoke.gainful.server.config.loadConfig
import com.yoke.gainful.server.config.serverModule
import com.yoke.gainful.server.plugins.configureRouting
import com.yoke.gainful.server.plugins.configureSecurity
import com.yoke.gainful.server.plugins.configureSerialization
import com.yoke.gainful.server.plugins.configureStatusPages
import com.yoke.gainful.server.service.SessionService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val appConfig = loadConfig()

    DatabaseFactory.init(appConfig.database)

    install(Koin) {
        modules(serverModule(appConfig))
    }

    val sessionService by inject<SessionService>()

    install(CallLogging)
    configureSerialization()
    configureSecurity(appConfig.jwt, sessionService)
    configureStatusPages()
    configureRouting()
}
