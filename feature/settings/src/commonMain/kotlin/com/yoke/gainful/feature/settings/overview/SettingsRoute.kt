package com.yoke.gainful.feature.settings.overview

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    onNavigateToImport: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val viewModel = koinViewModel<SettingsViewModel>()
    SettingsScreen(
        viewModel = viewModel,
        onNavigateToImport = onNavigateToImport,
    )
}
