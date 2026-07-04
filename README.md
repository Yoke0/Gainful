# Gainful 盈迹

> 让每一次增长，都有迹可循。

[English](docs/README_EN.md)

Gainful 是一款面向个人用户的收益追踪与财务分析工具，基于 Kotlin Multiplatform + Compose Multiplatform 构建，支持 Android、iOS 和 Desktop (JVM) 三端。

## 架构设计

采用 **MVI (Model-View-Intent)** 架构，单向数据流驱动 UI 更新。

```
User Action → Intent → ViewModel → State → UI
     ↑                                    │
     └────────────────────────────────────┘
```

### 项目结构

采用 **Clean Architecture** 分层架构，参考 [Now in Android](https://github.com/android/nowinandroid) 项目组织：

```
Gainful/
├── shared/                    # 共享 KMP 模块
│   └── src/
│       ├── commonMain/        # 平台无关代码
│       ├── androidMain/       # Android 平台实现
│       ├── iosMain/           # iOS 平台实现
│       └── jvmMain/           # Desktop 平台实现
├── core/                      # 核心模块
│   ├── common/                # 通用工具、扩展函数、常量
│   ├── model/                 # 数据模型（DTO、Entity）
│   ├── ui/                    # 通用 UI 组件、主题
│   ├── designsystem/          # 设计系统 tokens、共享样式
│   ├── data/                  # Repository 接口
│   ├── database/              # 本地数据源（Room + BundledSQLiteDriver）
│   ├── datastore/             # 偏好设置 DataStore
│   ├── network/               # 远程数据源（Ktor）
│   ├── domain/                # UseCase（按业务分包）
│   │   ├── asset/             # 资产搜索
│   │   ├── dashboard/         # 仪表盘（盈亏计算）
│   │   ├── holding/           # 持仓相关
│   │   └── transaction/       # 交易相关
│   ├── sync/                  # 后台数据同步（行情拉取、K线缓存）
│   ├── file/                  # 文件 I/O 工具
│   └── navigation/            # 导航配置（Navigation3）
├── feature/                   # 功能模块（按业务拆分）
│   ├── dashboard/             # 仪表盘
│   ├── holdings/              # 持仓（overview/ + detail/ + di/）
│   ├── transactions/          # 交易记录（overview/ + add/ + di/）
│   └── settings/              # 设置
├── androidApp/                # Android 应用入口
├── desktopApp/                # Desktop 应用入口
└── iosApp/                    # iOS 应用（Xcode 项目）
```

### 技术栈

| 类别 | 技术 | 用途 |
|------|------|------|
| 网络请求 | Ktor | HTTP 客户端，支持多平台 |
| 本地存储 | Room | 跨平台数据库，类型安全 ORM |
| 依赖注入 | Koin | 轻量级 DI 框架 |
| 导航 | Navigation3 | Compose 跨平台导航 |
| 日期时间 | kotlinx-datetime | 跨平台日期时间处理 |
| UI 框架 | Compose Multiplatform | 跨平台声明式 UI |
| 国际化 | Compose Resources | 多模块字符串本地化（中文/英文） |

### 关键版本

| 组件 | 版本 |
|------|------|
| Kotlin | 2.4.0 |
| Compose Multiplatform | 1.11.1 |
| Material3 | 1.12.0-alpha02 |
| Gradle | 9.4.1 |
| AGP | 9.2.1 |
| Ktor | 3.5.0 |
| Room | 2.8.4 |
| Koin | 4.2.2 |

### 平台支持

- **Android**: minSdk 24, targetSdk 37, compileSdk 37
- **iOS**: iOS 18.2+, arm64 (真机 + Apple Silicon 模拟器)
- **Desktop**: JVM 11+, 支持 macOS/Windows/Linux

## 快速开始

### Android

```bash
./gradlew :androidApp:assembleDebug
```

### Desktop

```bash
# 标准运行
./gradlew :desktopApp:run

# 热重载（开发推荐）
./gradlew :desktopApp:hotRun --auto
```

### iOS

1. 打开 `iosApp/iosApp.xcodeproj`
2. 在 Xcode 中选择模拟器或真机
3. 点击运行（首次构建会自动触发 Gradle 编译共享模块）

> ⚠️ iOS 构建需要先设置 `iosApp/Configuration/Config.xcconfig` 中的 `TEAM_ID`

## 开发指南

### 项目约定

- **包名**: `com.yoke.gainful`
- **依赖管理**: Gradle Version Catalog (`gradle/libs.versions.toml`)
- **项目引用**: 使用 `projects.shared` 而非 `:shared`（已启用 `TYPESAFE_PROJECT_ACCESSORS`）
- **iOS 框架**: 静态框架，名称为 `Shared`
- **Compose 资源**: 每个模块独立维护 `src/commonMain/composeResources/values/strings.xml`（中文）和 `values-en/strings.xml`（英文）
- **生成的资源 ID**: `gainful.<module>.generated.resources.*`（如 `gainful.feature.dashboard.generated.resources.*`）
- **格式化字符串**: 使用位置参数 `%1$d`、`%2$s`（CMP 要求）
- **测试命令**: `./gradlew allTests` 运行所有平台测试

### 通用 UI 组件 (`core/ui`)

| 组件 | 文件 | 说明 |
|------|------|------|
| `GainfulScaffold` | `ScreenScaffold.kt` | 通用页面布局容器（可选顶栏） |
| `NavButton` | `Button.kt` | 圆形导航按钮（‹ ›） |
| `PrimaryButton` | `Button.kt` | 填充按钮（支持自定义颜色） |
| `SecondaryButton` | `Button.kt` | 描边按钮 |
| `SelectChip` | `Button.kt` | 可选中 chip，icon + label |
| `SquareIconButton` | `Button.kt` | 方形图标按钮 |
| `GainfulDialog` | `Dialog.kt` | 通用对话框容器（title + content + buttons） |
| `ConfirmDialog` | `Dialog.kt` | 确认对话框（支持自定义确认按钮颜色） |
| `GainfulTopAppBar` | `TopAppBar.kt` | 顶部导航栏 |
| `BottomBar` / `Modifier.bottomBarPadding()` | `BottomBar.kt` | 底部导航栏 / 底部间距修饰符 |
| `DatePickerField` / `CalendarDialog` | `DatePicker.kt` | 日期选择器 |
| `TimePickerField` / `TimePickerDialog` | `TimePicker.kt` | 时间选择器 |
| `DateTimePickerField` / `DateTimePickerDialog` | `DateTimePicker.kt` | 日期时间选择器 |
| `LoadingIndicator` | `Loading.kt` | 加载状态指示器 |
| `TransactionCard` | `TransactionCard.kt` | 交易卡片 |
| `BackNavigationIcon` / `NavIcons` | `NavIcons.kt` | 导航图标 |

### 测试

```bash
./gradlew allTests
```

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源。
