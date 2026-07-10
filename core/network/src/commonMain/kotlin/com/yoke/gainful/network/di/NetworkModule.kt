package com.yoke.gainful.network.di

import com.yoke.gainful.network.EastMoneyApi
import com.yoke.gainful.network.EastMoneyApiImpl
import com.yoke.gainful.network.createPlatformHttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule =
    module {
        single {
            Json {
                ignoreUnknownKeys = true
            }
        }
        single {
            createPlatformHttpClient().config {
                install(ContentNegotiation) {
                    json(get<Json>())
                }
            }
        }
        single<EastMoneyApi> { EastMoneyApiImpl(get(), get()) }
    }
