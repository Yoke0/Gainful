package com.yoke.gainful.feature.transactions.overview

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionsRoute(
    onAddTransaction: () -> Unit,
) {
    val viewModel = koinViewModel<TransactionsViewModel>()
    TransactionsScreen(
        viewModel = viewModel,
        onAddTransaction = onAddTransaction,
    )
}
