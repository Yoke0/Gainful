# Gainful

> Track every growth, trace every gain.

[中文版](README.md)

Gainful is a revenue tracking and financial analysis tool for personal users, built with Kotlin Multiplatform + Compose Multiplatform, targeting Android, iOS, and Desktop (JVM).

## Architecture

**MVI (Model-View-Intent)** pattern with unidirectional data flow.

```
User Action → Intent → ViewModel → State → UI
     ↑                                    │
     └────────────────────────────────────┘
```

### Project Structure

**Clean Architecture** following [Now in Android](https://github.com/android/nowinandroid) patterns:

```
Gainful/
├── shared/                    # Shared KMP module
│   └── src/
│       ├── commonMain/        # Platform-agnostic code
│       ├── androidMain/       # Android platform implementation
│       ├── iosMain/           # iOS platform implementation
│       └── jvmMain/           # Desktop platform implementation
├── core/                      # Core modules
│   ├── common/                # Utilities, extensions, constants
│   ├── data/                  # Data layer interfaces (Repository contracts)
│   ├── database/              # Local data source (Room)
│   ├── network/               # Remote data source (Ktor)
│   ├── domain/                # Domain layer (UseCases)
│   ├── model/                 # Data models (DTOs, Entities)
│   ├── ui/                    # Common UI components, theme
│   ├── navigation/            # Navigation config (Navigation3)
│   └── testing/               # Test utilities, Fake implementations
├── feature/                   # Feature modules (per business)
│   ├── dashboard/             # Dashboard
│   ├── holdings/              # Holdings
│   ├── transactions/          # Transactions
│   └── settings/              # Settings
├── androidApp/                # Android application entry
├── desktopApp/                # Desktop application entry
└── iosApp/                    # iOS application (Xcode project)
```

### Tech Stack

| Category | Technology | Purpose |
|----------|------------|---------|
| Network | Ktor | HTTP client, multi-platform support |
| Storage | Room | Cross-platform database, type-safe ORM |
| DI | Koin | Lightweight dependency injection |
| Navigation | Navigation3 | Compose cross-platform navigation |
| Image | Coil | Native image loading for Compose |
| DateTime | kotlinx-datetime | Cross-platform date/time handling |
| UI | Compose Multiplatform | Declarative cross-platform UI |

### Key Versions

| Component | Version |
|-----------|---------|
| Kotlin | 2.4.0 |
| Compose Multiplatform | 1.11.1 |
| Material3 | 1.11.0-alpha07 |
| Gradle | 9.1.0 |
| AGP | 9.0.1 |

### Platform Support

- **Android**: minSdk 24, targetSdk 36, compileSdk 36
- **iOS**: iOS 18.2+, arm64 (device + Apple Silicon simulator)
- **Desktop**: JVM 11+, supports macOS/Windows/Linux

## Quick Start

### Android

```bash
./gradlew :androidApp:assembleDebug
```

### Desktop

```bash
# Standard run
./gradlew :desktopApp:run

# Hot reload (recommended for development)
./gradlew :desktopApp:hotRun --auto

# Build macOS installer (DMG)
./gradlew :desktopApp:packageDmg
```

> macOS DMG output path: `desktopApp/build/compose/binaries/main/dmg/`

### iOS

1. Open `iosApp/iosApp.xcodeproj`
2. Select simulator or device in Xcode
3. Click Run (first build triggers Gradle to compile shared module automatically)

> ⚠️ iOS build requires setting `TEAM_ID` in `iosApp/Configuration/Config.xcconfig`

## Development Guide

### Project Conventions

- **Package**: `com.yoke.gainful`
- **Dependency management**: Gradle Version Catalog (`gradle/libs.versions.toml`)
- **Project references**: Use `projects.shared` instead of `:shared` (`TYPESAFE_PROJECT_ACCESSORS` enabled)
- **iOS framework**: Static framework named `Shared`
- **Compose resources**: Place in `shared/src/commonMain/composeResources/`
- **Generated resource IDs**: `gainful.shared.generated.resources.*`

### Code Standards

1. **Platform-specific code**: Place in corresponding `androidMain/`, `iosMain/`, `jvmMain/` directories
2. **Shared logic**: Prefer `commonMain`, use `expect`/`actual` for platform differences
3. **UI components**: Use Compose Multiplatform, place in `commonMain`
4. **Testing**: Each platform has independent test source sets

### Testing

```bash
# Android host tests
./gradlew :shared:testAndroidHostTest

# Desktop tests
./gradlew :shared:jvmTest

# iOS tests
./gradlew :shared:iosSimulatorArm64Test
```

> ℹ️ Android tests use `androidHostTest` source set (not `androidTest`)

## License

This project is licensed under the [Apache License 2.0](LICENSE).
