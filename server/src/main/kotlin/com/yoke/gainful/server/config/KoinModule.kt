package com.yoke.gainful.server.config

import com.yoke.gainful.server.security.token.JwtTokenService
import com.yoke.gainful.server.security.token.TokenService
import com.yoke.gainful.server.service.AuthService
import com.yoke.gainful.server.service.AvatarService
import com.yoke.gainful.server.service.SessionService
import com.yoke.gainful.server.service.TransactionService
import com.yoke.gainful.server.service.UserService
import org.koin.dsl.module

fun serverModule(config: AppConfig) =
    module {
        single { config.jwt }
        single { config.upload }

        single<TokenService> { JwtTokenService() }

        single { AuthService(get(), get(), get()) }
        single { UserService() }
        single { SessionService() }
        single { TransactionService() }
        single { AvatarService(get()) }
    }
