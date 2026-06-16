package com.yoke.gainful.di

import org.koin.core.context.startKoin

actual fun initKoin() {
    startKoin { modules(allModules()) }
}
