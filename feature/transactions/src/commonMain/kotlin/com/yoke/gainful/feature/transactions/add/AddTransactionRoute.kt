package com.yoke.gainful.feature.transactions.add

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddTransactionRoute(
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<AddTransactionViewModel>()
    AddTransactionScreen(
        viewModel = viewModel,
        onBack = onBack,
    )
}
