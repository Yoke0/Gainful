package com.yoke.gainful.feature.transactions.di

import com.yoke.gainful.feature.transactions.overview.TransactionsViewModel
import com.yoke.gainful.feature.transactions.add.AddTransactionViewModel
import org.koin.dsl.module

val transactionsViewModelModule = module {
    factory { TransactionsViewModel(get(), get()) }
    factory { AddTransactionViewModel(get(), get(), get(), get()) }
}
