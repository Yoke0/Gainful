package com.yoke.gainful.feature.settings.di

import com.yoke.gainful.feature.settings.`import`.ImportViewModel
import com.yoke.gainful.feature.settings.overview.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule =
    module {
        viewModel { SettingsViewModel(get(), get(), get()) }
        viewModel { ImportViewModel(get(), get(), get(), get(), get(), get(), get()) }
    }
