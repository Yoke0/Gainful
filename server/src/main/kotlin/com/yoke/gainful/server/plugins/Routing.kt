package com.yoke.gainful.server.plugins

import com.yoke.gainful.server.config.UploadConfig
import com.yoke.gainful.server.routes.authRoutes
import com.yoke.gainful.server.routes.transactionRoutes
import com.yoke.gainful.server.routes.userRoutes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {
    val uploadConfig by inject<UploadConfig>()

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.ContentDisposition)
        allowHeader(HttpHeaders.Authorization)
    }

    routing {
        route("/api") {
            authRoutes()
            userRoutes()
            transactionRoutes()
        }
        staticFiles("/avatars", File(uploadConfig.dir))

        get("/openapi/documentation.yaml") {
            call.respondText(
                this::class.java.classLoader.getResource("openapi/documentation.yaml")!!.readText(),
                ContentType("text", "yaml"),
            )
        }

        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}
