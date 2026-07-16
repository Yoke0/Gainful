package com.yoke.gainful.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yoke.gainful.feature.settings.import.ImportScreen
import com.yoke.gainful.feature.settings.overview.SettingsScreen
import com.yoke.gainful.feature.settings.overview.SettingsViewModel
import com.yoke.gainful.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel

fun EntryProviderScope<NavKey>.settingsEntry(
    navigator: Navigator,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToAvatar: () -> Unit = {},
) {
    entry<SettingsNavKey> {
        val viewModel = koinViewModel<SettingsViewModel>()
        SettingsScreen(
            viewModel = viewModel,
            onNavigateToImport = { navigator.navigate(ImportTransactionsNavKey) },
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToAvatar = onNavigateToAvatar,
        )
    }
    entry<ImportTransactionsNavKey> {
        ImportScreen(onBack = { navigator.goBack() })
    }
}
