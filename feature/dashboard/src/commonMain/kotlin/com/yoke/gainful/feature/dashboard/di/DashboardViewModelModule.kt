package com.yoke.gainful.feature.dashboard.di

import com.yoke.gainful.feature.dashboard.DashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dashboardViewModelModule =
    module {
        viewModel { DashboardViewModel(get(), get(), get(), get(), get(), get(), get()) }
    }
