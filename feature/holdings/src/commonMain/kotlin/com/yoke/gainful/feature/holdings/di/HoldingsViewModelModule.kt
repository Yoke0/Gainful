package com.yoke.gainful.feature.holdings.di

import com.yoke.gainful.feature.holdings.overview.HoldingsViewModel
import com.yoke.gainful.feature.holdings.detail.StockDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val holdingsViewModelModule = module {
    viewModel { HoldingsViewModel(get(), get()) }
    viewModel { params -> StockDetailViewModel(get(), params.get()) }
}
