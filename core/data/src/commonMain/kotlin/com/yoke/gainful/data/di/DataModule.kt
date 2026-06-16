package com.yoke.gainful.data.di

import com.yoke.gainful.data.repository.EastMoneyMarketRepository
import com.yoke.gainful.data.repository.MarketRepository
import org.koin.dsl.module

val dataModule = module {
    single<MarketRepository> { EastMoneyMarketRepository(get()) }
}
