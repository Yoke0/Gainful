package com.yoke.gainful.data.di

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.EastMoneyMarketRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.OfflineAssetRepository
import com.yoke.gainful.data.repository.OfflineTransactionRepository
import com.yoke.gainful.data.repository.OfflineUserPreferencesRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.data.repository.UserPreferencesRepository
import com.yoke.gainful.datastore.UserPreferencesDataSource
import org.koin.dsl.module

val dataModule = module {
    single<MarketRepository> { EastMoneyMarketRepository(get()) }
    single<AssetRepository> { OfflineAssetRepository(get()) }
    single<TransactionRepository> { OfflineTransactionRepository(get()) }
    single<UserPreferencesRepository> { OfflineUserPreferencesRepository(get<UserPreferencesDataSource>()) }
}
