package com.yoke.gainful.feature.transactions

import org.koin.dsl.module

val transactionsViewModelModule = module {
    factory { TransactionsViewModel(get()) }
    factory { AddTransactionViewModel(get(), get()) }
}
