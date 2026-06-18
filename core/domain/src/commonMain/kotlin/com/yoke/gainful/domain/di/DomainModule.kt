package com.yoke.gainful.domain.di

import com.yoke.gainful.domain.usecase.AddTransactionUseCase
import com.yoke.gainful.domain.usecase.GetHoldingsDisplayUseCase
import com.yoke.gainful.domain.usecase.GetHoldingsUseCase
import com.yoke.gainful.domain.usecase.GetPortfolioSummaryUseCase
import com.yoke.gainful.domain.usecase.GetStockDetailUseCase
import com.yoke.gainful.domain.usecase.GetTransactionsUseCase
import com.yoke.gainful.domain.usecase.GetTransactionsWithAssetsUseCase
import com.yoke.gainful.domain.usecase.SearchAssetsUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetPortfolioSummaryUseCase(get()) }
    factory { GetHoldingsUseCase(get()) }
    factory { GetHoldingsDisplayUseCase(get(), get(), get()) }
    factory { GetStockDetailUseCase(get(), get(), get()) }
    factory { GetTransactionsUseCase(get()) }
    factory { GetTransactionsWithAssetsUseCase(get(), get()) }
    factory { SearchAssetsUseCase(get()) }
    factory { AddTransactionUseCase(get(), get()) }
}
