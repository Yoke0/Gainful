package com.yoke.gainful.sync.di

import com.yoke.gainful.sync.KLineFetchService
import com.yoke.gainful.sync.StockPriceFetchService
import org.koin.dsl.module

val syncModule =
    module {
        single { StockPriceFetchService(get(), get(), get(), get()) }
        single { KLineFetchService(get(), get(), get(), get()) }
    }
