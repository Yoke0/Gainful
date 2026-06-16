package com.yoke.gainful.database.di

import com.yoke.gainful.database.GainfulDatabase
import com.yoke.gainful.database.createDatabase
import com.yoke.gainful.database.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        createDatabase(getDatabaseBuilder(androidContext()))
    }
    single { get<GainfulDatabase>().assetDao() }
    single { get<GainfulDatabase>().transactionDao() }
}
