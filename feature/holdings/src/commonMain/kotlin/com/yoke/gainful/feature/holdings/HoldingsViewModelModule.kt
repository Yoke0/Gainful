package com.yoke.gainful.feature.holdings

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.core.parameter.parametersOf

val holdingsViewModelModule = module {
    factory { HoldingsViewModel(get()) }
    factory { params -> StockDetailViewModel(get(), params.get()) }
}
