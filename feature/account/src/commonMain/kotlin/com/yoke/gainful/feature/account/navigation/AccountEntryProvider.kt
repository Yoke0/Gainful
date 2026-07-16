package com.yoke.gainful.feature.account.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.yoke.gainful.feature.account.avatar.AvatarScreen
import com.yoke.gainful.feature.account.avatar.AvatarViewModel
import com.yoke.gainful.feature.account.login.LoginScreen
import com.yoke.gainful.feature.account.login.LoginViewModel
import com.yoke.gainful.feature.account.register.RegisterScreen
import com.yoke.gainful.feature.account.register.RegisterViewModel
import com.yoke.gainful.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel

fun EntryProviderScope<NavKey>.accountEntry(navigator: Navigator) {
    entry<LoginNavKey> {
        val viewModel = koinViewModel<LoginViewModel>()
        LoginScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
            onNavigateToRegister = { navigator.navigate(RegisterNavKey) },
            onLoginSuccess = { navigator.goBack() },
        )
    }
    entry<RegisterNavKey> {
        val viewModel = koinViewModel<RegisterViewModel>()
        RegisterScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
            onNavigateToLogin = { navigator.goBack() },
            onRegisterSuccess = {
                navigator.goBack()
                navigator.goBack()
            },
        )
    }
    entry<AvatarNavKey> {
        val viewModel = koinViewModel<AvatarViewModel>()
        AvatarScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
        )
    }
}
