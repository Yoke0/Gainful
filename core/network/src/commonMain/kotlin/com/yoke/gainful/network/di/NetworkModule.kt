package com.yoke.gainful.network.di

import com.yoke.gainful.network.EastMoneyApi
import com.yoke.gainful.network.EastMoneyApiImpl
import com.yoke.gainful.network.createPlatformHttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        createPlatformHttpClient().config {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("[Ktor] $message")
                    }
                }
                level = LogLevel.BODY
            }
        }
    }
    single<EastMoneyApi> { EastMoneyApiImpl(get()) }
}
