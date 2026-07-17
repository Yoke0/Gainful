package com.yoke.gainful.sync.di

import com.yoke.gainful.data.repository.AppSettingsRepository
import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.QuoteCacheRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.sync.KLineFetchService
import com.yoke.gainful.sync.StockPriceFetchService
import com.yoke.gainful.sync.TransactionSyncService
import org.koin.dsl.module

val syncModule =
    module {
        single {
            StockPriceFetchService(
                assetRepository = get<AssetRepository>(),
                marketRepository = get<MarketRepository>(),
                quoteCacheRepository = get<QuoteCacheRepository>(),
                appSettingsRepository = get<AppSettingsRepository>(),
                transactionRepository = get<TransactionRepository>(),
            )
        }
        single { KLineFetchService(get(), get(), get(), get(), get()) }
        single { TransactionSyncService(get(), get(), get(), get(), get(), get()) }
    }
