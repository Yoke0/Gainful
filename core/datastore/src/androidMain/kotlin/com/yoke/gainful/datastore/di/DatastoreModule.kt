package com.yoke.gainful.datastore.di

import com.yoke.gainful.datastore.SettingsDataSource
import com.yoke.gainful.datastore.UserDataSource
import com.yoke.gainful.datastore.createGainfulDataStore
import com.yoke.gainful.datastore.getDataStorePath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val datastoreModule =
    module {
        single { createGainfulDataStore(getDataStorePath(androidContext())) }
        single { SettingsDataSource(get()) }
        single { UserDataSource(get()) }
    }
