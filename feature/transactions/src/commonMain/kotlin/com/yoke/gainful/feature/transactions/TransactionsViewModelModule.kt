package com.yoke.gainful.feature.transactions

import org.koin.dsl.module

val transactionsViewModelModule = module {
    factory { TransactionsViewModel(get(), get()) }
    factory { AddTransactionViewModel(get(), get(), get()) }
}
