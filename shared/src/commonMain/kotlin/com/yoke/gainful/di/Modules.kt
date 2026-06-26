package com.yoke.gainful.di

import com.yoke.gainful.data.di.dataModule
import com.yoke.gainful.domain.di.domainModule
import com.yoke.gainful.feature.dashboard.di.dashboardViewModelModule
import com.yoke.gainful.feature.holdings.di.holdingsViewModelModule
import com.yoke.gainful.feature.settings.di.settingsViewModelModule
import com.yoke.gainful.feature.transactions.di.transactionsViewModelModule
import com.yoke.gainful.navigation.navigationModule
import com.yoke.gainful.network.di.networkModule
import com.yoke.gainful.sync.di.syncModule
import org.koin.core.module.Module

fun allModules(): List<Module> = platformModules + listOf(
    networkModule,
    dataModule,
    domainModule,
    syncModule,
    navigationModule,
    dashboardViewModelModule,
    transactionsViewModelModule,
    holdingsViewModelModule,
    settingsViewModelModule,
)
