package com.yoke.gainful.data.di

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.AuthRepository
import com.yoke.gainful.data.repository.AuthRepositoryImpl
import com.yoke.gainful.data.repository.EastMoneyMarketRepository
import com.yoke.gainful.data.repository.KLineCacheRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.OfflineAssetRepository
import com.yoke.gainful.data.repository.OfflineKLineCacheRepository
import com.yoke.gainful.data.repository.OfflinePnlCacheRepository
import com.yoke.gainful.data.repository.OfflineQuoteCacheRepository
import com.yoke.gainful.data.repository.OfflineSyncQueueRepository
import com.yoke.gainful.data.repository.OfflineTransactionRepository
import com.yoke.gainful.data.repository.OfflineUserPreferencesRepository
import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.data.repository.QuoteCacheRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.data.repository.UserPreferencesRepository
import com.yoke.gainful.datastore.AuthDataSource
import com.yoke.gainful.datastore.UserPreferencesDataSource
import org.koin.dsl.module

val dataModule =
    module {
        single<MarketRepository> { EastMoneyMarketRepository(get()) }
        single<AssetRepository> { OfflineAssetRepository(get()) }
        single<TransactionRepository> { OfflineTransactionRepository(get()) }
        single<SyncQueueRepository> { OfflineSyncQueueRepository(get()) }
        single<UserPreferencesRepository> { OfflineUserPreferencesRepository(get<UserPreferencesDataSource>()) }
        single<QuoteCacheRepository> { OfflineQuoteCacheRepository(get()) }
        single<KLineCacheRepository> { OfflineKLineCacheRepository(get()) }
        single<PnlCacheRepository> { OfflinePnlCacheRepository(get()) }
        single<AuthRepository> { AuthRepositoryImpl(get(), get<AuthDataSource>(), get(), get()) }
    }
