package com.yoke.gainful.network.di

import com.yoke.gainful.network.EastMoneyApi
import com.yoke.gainful.network.EastMoneyApiImpl
import com.yoke.gainful.network.GainfulApi
import com.yoke.gainful.network.GainfulApiImpl
import com.yoke.gainful.network.TransactionApi
import com.yoke.gainful.network.TransactionApiImpl
import com.yoke.gainful.network.createPlatformHttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
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

        single(named("serverHttpClient")) {
            createPlatformHttpClient().config {
                install(ContentNegotiation) {
                    json(get<Json>())
                }
            }
        }
        single<GainfulApi> { GainfulApiImpl(get(named("serverHttpClient"))) }
        single<TransactionApi> { TransactionApiImpl(get(named("serverHttpClient"))) }
    }
