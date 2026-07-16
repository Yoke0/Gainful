package com.yoke.gainful.database.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yoke.gainful.database.GainfulDatabase
import com.yoke.gainful.database.createDatabase
import com.yoke.gainful.database.getDatabaseBuilder
import org.koin.dsl.module

val databaseModule =
    module {
        single {
            createDatabase(
                getDatabaseBuilder()
                    .setDriver(BundledSQLiteDriver()),
            )
        }
        single { get<GainfulDatabase>().assetDao() }
        single { get<GainfulDatabase>().transactionDao() }
        single { get<GainfulDatabase>().quoteSnapshotDao() }
        single { get<GainfulDatabase>().kLineCacheDao() }
        single { get<GainfulDatabase>().pnlCacheDao() }
        single { get<GainfulDatabase>().syncQueueDao() }
    }
