package com.yoke.gainful.network.di

import com.yoke.gainful.common.BuildConfig
import com.yoke.gainful.ksafe.SecureTokenStorage
import com.yoke.gainful.network.createPlatformHttpClient
import com.yoke.gainful.network.eastmoney.EastMoneyApi
import com.yoke.gainful.network.eastmoney.EastMoneyApiImpl
import com.yoke.gainful.network.server.AuthenticatedApi
import com.yoke.gainful.network.server.AuthenticatedApiImpl
import com.yoke.gainful.network.server.PublicApi
import com.yoke.gainful.network.server.PublicApiImpl
import com.yoke.gainful.network.server.TransactionApi
import com.yoke.gainful.network.server.TransactionApiImpl
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
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

        // Public client — no auth, for login/register/refresh
        single(named("publicHttpClient")) {
            createPlatformHttpClient().config {
                install(ContentNegotiation) {
                    json(get<Json>())
                }
                defaultRequest {
                    url.takeFrom(BuildConfig.SERVER_BASE_URL)
                }
            }
        }

        // Auth client — with Bearer auth, auto token attachment + refresh
        single(named("authHttpClient")) {
            val secureTokenStorage = get<SecureTokenStorage>()
            val publicApi = get<PublicApi>()
            createPlatformHttpClient().config {
                install(ContentNegotiation) {
                    json(get<Json>())
                }
                install(Auth) {
                    bearer {
                        loadTokens {
                            val accessToken = secureTokenStorage.accessToken ?: return@loadTokens null
                            BearerTokens(accessToken, secureTokenStorage.refreshToken ?: "")
                        }
                        refreshTokens {
                            val refreshToken = oldTokens?.refreshToken ?: secureTokenStorage.refreshToken ?: return@refreshTokens null
                            val newAuth = publicApi.refreshToken(refreshToken)
                            BearerTokens(newAuth.accessToken, refreshToken)
                        }
                        sendWithoutRequest { true }
                    }
                }
                defaultRequest {
                    url.takeFrom(BuildConfig.SERVER_BASE_URL)
                }
            }
        }

        single<PublicApi> { PublicApiImpl(get(named("publicHttpClient")), get()) }
        single<AuthenticatedApi> { AuthenticatedApiImpl(get(named("authHttpClient"))) }
        single<TransactionApi> { TransactionApiImpl(get(named("authHttpClient"))) }
    }
