package com.yoke.gainful.domain.di

import com.yoke.gainful.domain.usecase.asset.SearchAssetsUseCase
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.domain.usecase.holding.GetHoldingsUseCase
import com.yoke.gainful.domain.usecase.holding.GetStockDetailUseCase
import com.yoke.gainful.domain.usecase.portfolio.GetPortfolioSummaryUseCase
import com.yoke.gainful.domain.usecase.transaction.AddTransactionUseCase
import com.yoke.gainful.domain.usecase.transaction.DeleteTransactionUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsWithAssetsUseCase
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
    factory { DeleteTransactionUseCase(get()) }
}
