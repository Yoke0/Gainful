# Gainful 盈迹

> 让每一次增长，都有迹可循。

[English](README_EN.md)

Gainful 是一款面向个人用户的收益追踪与财务分析工具，基于 Kotlin Multiplatform + Compose Multiplatform 构建，支持 Android、iOS 和 Desktop (JVM) 三端。

## 架构设计

采用 **MVI (Model-View-Intent)** 架构，单向数据流驱动 UI 更新。

```
User Action → Intent → Reducer → State → UI
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
│   ├── data/                  # 数据层接口定义（Repository 接口）
│   ├── database/              # 本地数据源（SQLDelight）
│   ├── network/               # 远程数据源（Ktor）
│   ├── domain/                # 领域层（UseCase）
│   ├── model/                 # 数据模型（DTO、Entity）
│   ├── ui/                    # 通用 UI 组件、主题
│   ├── navigation/            # 导航配置（Navigation3）
│   └── testing/               # 测试工具、Fake 实现
├── feature/                   # 功能模块（按业务拆分）
│   ├── dashboard/             # 仪表盘
│   ├── holdings/              # 持仓
│   ├── transactions/          # 交易记录
│   └── settings/              # 设置
├── androidApp/                # Android 应用入口
├── desktopApp/                # Desktop 应用入口
└── iosApp/                    # iOS 应用（Xcode 项目）
```

### 技术栈

| 类别 | 技术 | 用途 |
|------|------|------|
| 网络请求 | Ktor | HTTP 客户端，支持多平台 |
| 本地存储 | SQLDelight | 跨平台数据库，类型安全 SQL |
| 依赖注入 | Koin | 轻量级 DI 框架 |
| 导航 | Navigation3 | Compose 跨平台导航 |
| 图片加载 | Coil | Compose 原生图片加载 |
| UI 框架 | Compose Multiplatform | 跨平台声明式 UI |

### 关键版本

| 组件 | 版本 |
|------|------|
| Kotlin | 2.4.0 |
| Compose Multiplatform | 1.11.1 |
| Material3 | 1.11.0-alpha07 |
| Gradle | 9.1.0 |
| AGP | 9.0.1 |

### 平台支持

- **Android**: minSdk 24, targetSdk 36, compileSdk 36
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

# 构建 macOS 安装包（DMG）
./gradlew :desktopApp:packageDmg
```

> macOS DMG 输出路径: `desktopApp/build/compose/binaries/main/dmg/`

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
- **Compose 资源**: 放在 `shared/src/commonMain/composeResources/`
- **生成的资源 ID**: `gainful.shared.generated.resources.*`

### 代码规范

1. **平台特定代码**: 放在对应的 `androidMain/`、`iosMain/`、`jvmMain/` 目录
2. **共享逻辑**: 优先放在 `commonMain`，使用 `expect`/`actual` 处理平台差异
3. **UI 组件**: 使用 Compose Multiplatform，放在 `commonMain`
4. **测试**: 每个平台有独立的测试源集

### 测试

```bash
# Android 主机测试
./gradlew :shared:testAndroidHostTest

# Desktop 测试
./gradlew :shared:jvmTest

# iOS 测试
./gradlew :shared:iosSimulatorArm64Test
```

> ℹ️ Android 测试使用 `androidHostTest` 源集（非 `androidTest`）

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源。
