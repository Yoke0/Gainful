package com.yoke.gainful.feature.transactions.di

import com.yoke.gainful.feature.transactions.add.AddTransactionViewModel
import com.yoke.gainful.feature.transactions.overview.TransactionsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val transactionsViewModelModule =
    module {
        viewModel { TransactionsViewModel(get(), get()) }
        viewModel { AddTransactionViewModel(get(), get(), get(), get()) }
    }
