package com.yoke.gainful.feature.holdings.di

import com.yoke.gainful.feature.holdings.overview.HoldingsViewModel
import com.yoke.gainful.feature.holdings.detail.StockDetailViewModel
import org.koin.dsl.module

val holdingsViewModelModule = module {
    factory { HoldingsViewModel(get()) }
    factory { params -> StockDetailViewModel(get(), params.get()) }
}
