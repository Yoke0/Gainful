package com.yoke.gainful.data.di

import com.yoke.gainful.data.repository.AppSettingsRepository
import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.AuthRepository
import com.yoke.gainful.data.repository.AuthRepositoryImpl
import com.yoke.gainful.data.repository.EastMoneyMarketRepository
import com.yoke.gainful.data.repository.KLineCacheRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.OfflineAppSettingsRepository
import com.yoke.gainful.data.repository.OfflineAssetRepository
import com.yoke.gainful.data.repository.OfflineKLineCacheRepository
import com.yoke.gainful.data.repository.OfflinePnlCacheRepository
import com.yoke.gainful.data.repository.OfflineQuoteCacheRepository
import com.yoke.gainful.data.repository.OfflineSyncQueueRepository
import com.yoke.gainful.data.repository.OfflineTransactionRepository
import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.data.repository.QuoteCacheRepository
import com.yoke.gainful.data.repository.ServerTransactionRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.data.repository.TransactionSyncRepository
import com.yoke.gainful.datastore.SettingsDataSource
import com.yoke.gainful.datastore.UserDataSource
import org.koin.dsl.module

val dataModule =
    module {
        single<MarketRepository> { EastMoneyMarketRepository(get()) }
        single<AssetRepository> { OfflineAssetRepository(get()) }
        single<TransactionRepository> { OfflineTransactionRepository(get()) }
        single<SyncQueueRepository> { OfflineSyncQueueRepository(get()) }
        single<AppSettingsRepository> { OfflineAppSettingsRepository(get<SettingsDataSource>()) }
        single<QuoteCacheRepository> { OfflineQuoteCacheRepository(get()) }
        single<KLineCacheRepository> { OfflineKLineCacheRepository(get()) }
        single<PnlCacheRepository> { OfflinePnlCacheRepository(get()) }
        single<TransactionSyncRepository> { ServerTransactionRepository(get()) }
        single<AuthRepository> {
            AuthRepositoryImpl(
                publicApi = get(),
                authenticatedApi = get(),
                userDataSource = get<UserDataSource>(),
                transactionDao = get(),
                syncQueueDao = get(),
                syncDataSource = get(),
            )
        }
    }
