package com.yoke.gainful.feature.transactions.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yoke.gainful.feature.transactions.add.AddTransactionScreen
import com.yoke.gainful.feature.transactions.add.AddTransactionViewModel
import com.yoke.gainful.feature.transactions.overview.TransactionsScreen
import com.yoke.gainful.feature.transactions.overview.TransactionsViewModel
import com.yoke.gainful.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel

fun EntryProviderScope<NavKey>.transactionsEntry(navigator: Navigator) {
    entry<TransactionsNavKey> {
        val viewModel = koinViewModel<TransactionsViewModel>()
        TransactionsScreen(
            viewModel = viewModel,
            onAddTransaction = { navigator.navigate(AddTransactionNavKey) },
        )
    }
    entry<AddTransactionNavKey> {
        val viewModel = koinViewModel<AddTransactionViewModel>()
        AddTransactionScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
        )
    }
}
