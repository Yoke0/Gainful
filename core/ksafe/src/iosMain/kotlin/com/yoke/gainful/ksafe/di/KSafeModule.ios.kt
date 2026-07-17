package com.yoke.gainful.ksafe.di

import com.yoke.gainful.ksafe.SecureTokenStorage
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.dsl.module

actual val ksafeModule =
    module {
        single { KSafe() }
        single { SecureTokenStorage(get()) }
    }
