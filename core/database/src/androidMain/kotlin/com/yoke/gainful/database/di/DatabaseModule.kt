package com.yoke.gainful.database.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yoke.gainful.database.GainfulDatabase
import com.yoke.gainful.database.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        getDatabaseBuilder(androidContext())
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<GainfulDatabase>().assetDao() }
    single { get<GainfulDatabase>().transactionDao() }
}
