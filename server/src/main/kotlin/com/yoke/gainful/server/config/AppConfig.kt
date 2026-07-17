package com.yoke.gainful.server.config

import com.typesafe.config.ConfigFactory
import com.yoke.gainful.server.security.token.TokenConfig
import io.ktor.server.application.Application
import io.ktor.server.config.HoconApplicationConfig

data class DatabaseConfig(
    val driver: String,
    val url: String,
    val user: String,
    val password: String,
)

data class UploadConfig(
    val dir: String,
    val maxFileSizeBytes: Long,
)

data class AppConfig(
    val database: DatabaseConfig,
    val jwt: TokenConfig,
    val upload: UploadConfig,
)

fun Application.loadConfig(): AppConfig {
    val config = HoconApplicationConfig(ConfigFactory.load())

    val database =
        DatabaseConfig(
            driver = config.property("database.driver").getString(),
            url = config.property("database.url").getString(),
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString(),
        )

    val jwt =
        TokenConfig(
            secret = config.property("jwt.secret").getString(),
            issuer = config.property("jwt.issuer").getString(),
            audience = config.property("jwt.audience").getString(),
            realm = config.property("jwt.realm").getString(),
            expiresIn = config.property("jwt.expiresIn").getString().toLong(),
            refreshExpiresIn = config.property("jwt.refreshExpiresIn").getString().toLong(),
        )

    val upload =
        UploadConfig(
            dir = config.property("upload.dir").getString(),
            maxFileSizeBytes = config.property("upload.maxFileSizeBytes").getString().toLong(),
        )

    return AppConfig(database, jwt, upload)
}
