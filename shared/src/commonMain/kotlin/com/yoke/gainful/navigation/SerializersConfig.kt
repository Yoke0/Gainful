package com.yoke.gainful.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.yoke.gainful.feature.account.navigation.AvatarNavKey
import com.yoke.gainful.feature.account.navigation.LoginNavKey
import com.yoke.gainful.feature.account.navigation.RegisterNavKey
import com.yoke.gainful.feature.dashboard.navigation.DashboardNavKey
import com.yoke.gainful.feature.holdings.navigation.HoldingsNavKey
import com.yoke.gainful.feature.holdings.navigation.StockDetailNavKey
import com.yoke.gainful.feature.settings.navigation.ImportTransactionsNavKey
import com.yoke.gainful.feature.settings.navigation.SettingsNavKey
import com.yoke.gainful.feature.transactions.navigation.AddTransactionNavKey
import com.yoke.gainful.feature.transactions.navigation.TransactionsNavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val serializersConfig =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(DashboardNavKey::class, DashboardNavKey.serializer())
                    subclass(TransactionsNavKey::class, TransactionsNavKey.serializer())
                    subclass(HoldingsNavKey::class, HoldingsNavKey.serializer())
                    subclass(SettingsNavKey::class, SettingsNavKey.serializer())
                    subclass(AddTransactionNavKey::class, AddTransactionNavKey.serializer())
                    subclass(ImportTransactionsNavKey::class, ImportTransactionsNavKey.serializer())
                    subclass(StockDetailNavKey::class, StockDetailNavKey.serializer())
                    subclass(LoginNavKey::class, LoginNavKey.serializer())
                    subclass(RegisterNavKey::class, RegisterNavKey.serializer())
                    subclass(AvatarNavKey::class, AvatarNavKey.serializer())
                }
            }
    }
