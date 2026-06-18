package com.yoke.gainful.feature.dashboard.di

import com.yoke.gainful.feature.dashboard.DashboardViewModel
import org.koin.dsl.module

val dashboardViewModelModule = module {
    factory { DashboardViewModel(get()) }
}
