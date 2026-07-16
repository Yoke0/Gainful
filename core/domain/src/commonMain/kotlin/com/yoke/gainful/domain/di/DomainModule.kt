package com.yoke.gainful.domain.di

import com.yoke.gainful.domain.usecase.asset.InsertAssetUseCase
import com.yoke.gainful.domain.usecase.asset.SearchAssetsUseCase
import com.yoke.gainful.domain.usecase.dashboard.ComputePnlUseCase
import com.yoke.gainful.domain.usecase.dashboard.GetPnlDataUseCase
import com.yoke.gainful.domain.usecase.holding.GetClosedPositionsUseCase
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.domain.usecase.holding.GetHoldingsUseCase
import com.yoke.gainful.domain.usecase.holding.GetStockDetailUseCase
import com.yoke.gainful.domain.usecase.transaction.AddTransactionUseCase
import com.yoke.gainful.domain.usecase.transaction.DeleteTransactionUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsWithAssetsOnceUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsWithAssetsUseCase
import org.koin.dsl.module

val domainModule =
    module {
        factory { ComputePnlUseCase(get(), get(), get()) }
        factory { GetPnlDataUseCase(get(), get()) }
        factory { GetHoldingsUseCase(get()) }
        factory { GetHoldingsDisplayUseCase(get(), get(), get(), get()) }
        factory { GetClosedPositionsUseCase(get(), get()) }
        factory { GetStockDetailUseCase(get(), get(), get()) }
        factory { GetTransactionsUseCase(get()) }
        factory { GetTransactionsWithAssetsUseCase(get(), get()) }
        factory { GetTransactionsWithAssetsOnceUseCase(get(), get()) }
        factory { SearchAssetsUseCase(get()) }
        factory { InsertAssetUseCase(get()) }
        factory { AddTransactionUseCase(get(), get(), get(), get(), get()) }
        factory { DeleteTransactionUseCase(get(), get(), get(), get(), get()) }
    }
