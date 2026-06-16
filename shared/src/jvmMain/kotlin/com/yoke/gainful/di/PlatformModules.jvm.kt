package com.yoke.gainful.di

import com.yoke.gainful.database.di.databaseModule
import org.koin.core.module.Module

actual val platformModules: List<Module> = listOf(databaseModule)
