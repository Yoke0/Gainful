package com.yoke.gainful.network.di

import com.yoke.gainful.common.BuildConfig
import com.yoke.gainful.network.createPlatformHttpClient
import com.yoke.gainful.network.eastmoney.EastMoneyApi
import com.yoke.gainful.network.eastmoney.EastMoneyApiImpl
import com.yoke.gainful.network.server.GainfulApi
import com.yoke.gainful.network.server.GainfulApiImpl
import com.yoke.gainful.network.server.TransactionApi
import com.yoke.gainful.network.server.TransactionApiImpl
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.takeFrom
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
                defaultRequest {
                    url.takeFrom(BuildConfig.SERVER_BASE_URL)
                }
            }
        }
        single<GainfulApi> { GainfulApiImpl(get(named("serverHttpClient"))) }
        single<TransactionApi> { TransactionApiImpl(get(named("serverHttpClient"))) }
    }
