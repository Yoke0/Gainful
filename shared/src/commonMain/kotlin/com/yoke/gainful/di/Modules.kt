package com.yoke.gainful.di

import com.yoke.gainful.data.di.dataModule
import com.yoke.gainful.domain.di.domainModule
import com.yoke.gainful.feature.holdings.holdingsViewModelModule
import com.yoke.gainful.feature.transactions.transactionsViewModelModule
import com.yoke.gainful.network.di.networkModule
import org.koin.core.module.Module

fun allModules(): List<Module> = platformModules + listOf(
    networkModule,
    dataModule,
    domainModule,
    transactionsViewModelModule,
    holdingsViewModelModule,
)
