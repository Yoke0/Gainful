# 修复 ViewModel 在 MIUI 桌面启动时被重建的问题

> Commit: `7c5575b` — `fix(navigation): preserve ViewModel across composition rebuilds`

## 问题现象

在小米 MIUI 系统上，从桌面点击图标启动 App 时，ViewModel 会被重建，导致：
- 页面数据重新加载（闪烁）
- 用户操作状态丢失
- 切换 Tab 时数据不一致

## 根因分析

### Navigation3 的 ViewModelStore 存储机制

Jetpack Navigation3 使用 `rememberViewModelStoreNavEntryDecorator()` 为每个 NavEntry 管理 ViewModelStore。该实现内部使用 Compose 的 `remember` 来持有 `ViewModelStore` 实例：

```kotlin
// Navigation3 源码（简化）
@Composable
fun rememberViewModelStoreNavEntryDecorator(): NavEntryDecorator<*> {
    return remember {
        ViewModelStoreNavEntryDecorator(
            stores = mutableMapOf()  // 存在 remember 中
        )
    }
}
```

### MIUI 的特殊行为

MIUI 在用户从桌面点击 App 图标启动时，会触发 Activity 的重建（类似 `configurationChanged`），导致整个 Composition Tree 被销毁重建。此时 `remember` 中的状态全部丢失，包括 ViewModelStore。

### 因果链

```
MIUI 桌面启动 → Activity 重建 → Composition 重建
→ remember 中的 ViewModelStores 被清空
→ 每个 NavEntry 的 ViewModelStore 被重新创建
→ ViewModel 被重新实例化（数据丢失）
```

## 排查思路

### 排除阶段 1：Splash Screen

**假设**：Splash Screen 可能在启动时触发了重建。

**验证**：移除 Splash Screen 配置，问题依旧。排除。

### 排除阶段 2：Crossfade 导航

**假设**：`App.kt` 中的 `Crossfade` 动画在切换时可能导致导航状态重建。

**验证**：将导航状态创建移到 Crossfade 外部，问题依旧。排除。

### 锁定阶段 3：对比启动方式

**关键线索**：通过 `adb shell am start` 启动 App 时一切正常，ViewModel 不会重建。但通过 MIUI 桌面点击图标启动时，ViewModel 必定重建。

这个差异非常关键：`adb start` 直接复用已有的 Activity 实例（或正常创建），而 MIUI 桌面启动会触发 Activity 重建（类似 `configChanged`），导致整个 Composition Tree 被销毁重建。这直接指向了 Compose 层的状态丢失问题，而不是业务逻辑问题。

### 确认阶段 4：定位 ViewModelStore 丢失

**验证**：在 Compose Inspector 中确认，从桌面启动后所有 ViewModel 的状态都被重置为初始值。说明 ViewModel 被重新 `init` 了。

**推理**：如果 ViewModel 被重建，意味着它的 ViewModelStore 被销毁。而 ViewModelStore 是由 Navigation3 的 decorator 管理的。查看 `rememberViewModelStoreNavEntryDecorator` 的实现，发现它使用 `remember` 存储 ViewModelStore —— 这在 Composition 重建时会丢失。

## 解决方案

### 核心思路

将 ViewModelStore 的存储从 Compose 的 `remember`（生命周期 = Composition）迁移到 Koin 单例（生命周期 = 进程）。

### 实现

#### 1. `ViewModelStoreRegistry` — 持久化存储

```kotlin
class ViewModelStoreRegistry {
    private val stores = mutableMapOf<Any, ViewModelStore>()

    fun getOrCreate(key: Any): ViewModelStore {
        return stores.getOrPut(key) { ViewModelStore() }
    }

    fun clear(key: Any) {
        stores.remove(key)?.clear()
    }
}
```

存储在 Koin 单例中，进程生命周期内持续存在。

#### 2. `PersistentViewModelStoreDecorator` — 替换原生 decorator

```kotlin
private class PersistentViewModelStoreDecorator<T : Any>(
    private val registry: ViewModelStoreRegistry,
) : NavEntryDecorator<T>(
    onPop = { key -> registry.clear(key) },
    decorate = { entry ->
        val store = registry.getOrCreate(entry.contentKey)
        val owner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore get() = store
        }
        CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
            entry.Content()
        }
    },
)
```

通过 `CompositionLocalProvider` 注入 ViewModelStoreOwner，让 Compose 的 `viewModel()` 函数获取到持久化的 Store。

#### 3. 注册 Koin 模块

```kotlin
// NavigationModule.kt
val navigationModule = module {
    singleOf(::ViewModelStoreRegistry)
}
```

### 附带修改

- `isTopLevel` 判断逻辑移入 `BottomBar` 内部，避免 NavDisplay 的 modifier 随导航状态变化
- 顶部页面添加 `navigationBarsPadding()` 和底部 `Spacer(BottomBarHeight)`
- `build_release.sh` 简化为仅构建 Android

## 验证

在 MIUI 设备上测试：
1. 冷启动 App → 数据正常加载
2. 按 Home 键返回桌面 → 再次点击图标启动 → 数据保持不变
3. 快速切换 Tab → ViewModel 不重建，状态保留
4. 多次重复上述操作 → 稳定无闪烁
