package com.yoke.gainful.feature.settings

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute() {
    val viewModel = koinViewModel<SettingsViewModel>()
    SettingsScreen(
        viewModel = viewModel,
    )
}
