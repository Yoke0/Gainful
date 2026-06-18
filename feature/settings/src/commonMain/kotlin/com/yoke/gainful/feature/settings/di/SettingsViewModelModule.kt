package com.yoke.gainful.feature.settings.di

import com.yoke.gainful.feature.settings.SettingsViewModel
import org.koin.dsl.module

val settingsViewModelModule = module {
    factory { SettingsViewModel() }
}
