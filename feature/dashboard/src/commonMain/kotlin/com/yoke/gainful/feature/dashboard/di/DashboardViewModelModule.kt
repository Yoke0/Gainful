package com.yoke.gainful.feature.dashboard.di

import com.yoke.gainful.domain.usecase.dashboard.GetPnlDataUseCase
import com.yoke.gainful.feature.dashboard.DashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dashboardViewModelModule =
    module {
        single { GetPnlDataUseCase() }
        viewModel { DashboardViewModel(get(), get(), get()) }
    }
