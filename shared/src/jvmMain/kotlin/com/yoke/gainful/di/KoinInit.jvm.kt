package com.yoke.gainful.di

import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

actual fun initKoin() {
    if (GlobalContext.getOrNull() == null) {
        startKoin { modules(allModules()) }
    }
}
