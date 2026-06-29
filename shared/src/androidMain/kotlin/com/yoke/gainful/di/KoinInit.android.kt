package com.yoke.gainful.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

actual fun initKoin() {
    // no-op: Android initializes Koin from MainActivity with context
}

fun initKoin(context: Context) {
    if (GlobalContext.getOrNull() != null) return
    startKoin {
        androidContext(context)
        modules(allModules())
    }
}
