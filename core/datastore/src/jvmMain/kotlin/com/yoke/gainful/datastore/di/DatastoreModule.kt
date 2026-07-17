package com.yoke.gainful.datastore.di

import com.yoke.gainful.datastore.SettingsDataSource
import com.yoke.gainful.datastore.SyncDataSource
import com.yoke.gainful.datastore.UserDataSource
import com.yoke.gainful.datastore.createGainfulDataStore
import com.yoke.gainful.datastore.getDataStorePath
import org.koin.dsl.module

val datastoreModule =
    module {
        single { createGainfulDataStore(getDataStorePath()) }
        single { SettingsDataSource(get()) }
        single { UserDataSource(get()) }
        single { SyncDataSource(get()) }
    }
