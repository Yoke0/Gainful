package com.yoke.gainful.feature.account.di

import com.yoke.gainful.feature.account.avatar.AvatarViewModel
import com.yoke.gainful.feature.account.login.LoginViewModel
import com.yoke.gainful.feature.account.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val accountViewModelModule =
    module {
        viewModel { LoginViewModel(get()) }
        viewModel { RegisterViewModel(get()) }
        viewModel { AvatarViewModel(get()) }
    }
