package com.yoke.gainful.datastore.di

import com.yoke.gainful.datastore.SettingsDataSource
import com.yoke.gainful.datastore.UserDataSource
import com.yoke.gainful.datastore.createDataStore
import com.yoke.gainful.datastore.getDatastorePath
import org.koin.dsl.module

val datastoreModule =
    module {
        single { createDataStore(getDatastorePath()) }
        single { SettingsDataSource(get()) }
        single { UserDataSource(get()) }
    }
