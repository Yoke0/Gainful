package com.yoke.gainful.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavEntryDecorator

class ViewModelStoreRegistry {
    private val stores = mutableMapOf<Any, ViewModelStore>()

    fun getOrCreate(key: Any): ViewModelStore {
        return stores.getOrPut(key) { ViewModelStore() }
    }

    fun clear(key: Any) {
        stores.remove(key)?.clear()
    }
}

@Composable
fun <T : Any> rememberPersistentViewModelStoreNavEntryDecorator(
    registry: ViewModelStoreRegistry,
): NavEntryDecorator<T> {
    return remember(registry) {
        PersistentViewModelStoreDecorator(registry)
    }
}

private class PersistentViewModelStoreDecorator<T : Any>(
    private val registry: ViewModelStoreRegistry,
) : NavEntryDecorator<T>(
        onPop = { key -> registry.clear(key) },
        decorate = { entry ->
            val store = registry.getOrCreate(entry.contentKey)
            val owner =
                object : ViewModelStoreOwner {
                    override val viewModelStore: ViewModelStore get() = store
                }
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                entry.Content()
            }
        },
    )
