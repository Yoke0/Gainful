package com.yoke.gainful.datastore.di

import com.yoke.gainful.datastore.AuthDataSource
import com.yoke.gainful.datastore.UserPreferencesDataSource
import com.yoke.gainful.datastore.createDataStore
import com.yoke.gainful.datastore.getDatastorePath
import org.koin.dsl.module

val datastoreModule =
    module {
        single { createDataStore(getDatastorePath()) }
        single { UserPreferencesDataSource(get()) }
        single { AuthDataSource(get()) }
    }
