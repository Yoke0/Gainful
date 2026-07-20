package com.yoke.gainful.server

import com.yoke.gainful.server.config.DatabaseFactory
import com.yoke.gainful.server.config.loadConfig
import com.yoke.gainful.server.config.serverModule
import com.yoke.gainful.server.plugins.configureRouting
import com.yoke.gainful.server.plugins.configureSecurity
import com.yoke.gainful.server.plugins.configureSerialization
import com.yoke.gainful.server.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import org.koin.ktor.plugin.Koin

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val host = System.getenv("HOST") ?: "0.0.0.0"
    embeddedServer(Netty, port = port, host = host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val appConfig = loadConfig()

    DatabaseFactory.init(appConfig.database)

    install(Koin) {
        modules(serverModule(appConfig))
    }

    install(CallLogging)
    configureSerialization()
    configureSecurity(appConfig.jwt)
    configureStatusPages()
    configureRouting()
}
