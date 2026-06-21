package com.yoke.gainful.feature.settings.di

import com.yoke.gainful.feature.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {
    viewModel { SettingsViewModel() }
}
