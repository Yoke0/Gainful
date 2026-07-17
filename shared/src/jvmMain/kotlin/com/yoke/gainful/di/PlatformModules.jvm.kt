package com.yoke.gainful.di

import com.yoke.gainful.database.di.databaseModule
import com.yoke.gainful.datastore.di.datastoreModule
import com.yoke.gainful.ksafe.di.ksafeModule
import org.koin.core.module.Module

actual val platformModules: List<Module> = listOf(ksafeModule, databaseModule, datastoreModule)
