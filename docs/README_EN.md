# Gainful

> Track every growth, trace every gain.

[中文版](../README.md)

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
│   ├── model/                 # Data models (DTOs, Entities)
│   ├── ui/                    # Common UI components, theme
│   ├── designsystem/          # Design system tokens, shared styling
│   ├── data/                  # Repository interfaces
│   ├── database/              # Local data source (Room + BundledSQLiteDriver)
│   ├── datastore/             # Preferences DataStore
│   ├── network/               # Remote data source (Ktor)
│   ├── domain/                # UseCases (organized by business domain)
│   │   ├── asset/             # Asset search
│   │   ├── dashboard/         # Dashboard (PnL calculation)
│   │   ├── holding/           # Holding-related
│   │   └── transaction/       # Transaction-related
│   ├── sync/                  # Background data sync (price fetching, KLine caching)
│   ├── file/                  # File I/O utilities
│   └── navigation/            # Navigation config (Navigation3)
├── feature/                   # Feature modules (per business)
│   ├── dashboard/             # Dashboard
│   ├── holdings/              # Holdings (overview/ + detail/ + di/)
│   ├── transactions/          # Transactions (overview/ + add/ + di/)
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
| DateTime | kotlinx-datetime | Cross-platform date/time handling |
| UI | Compose Multiplatform | Declarative cross-platform UI |
| i18n | Compose Resources | Multi-module string localization (Chinese/English) |

### Key Versions

| Component | Version |
|-----------|---------|
| Kotlin | 2.4.0 |
| Compose Multiplatform | 1.11.1 |
| Material3 | 1.12.0-alpha02 |
| Gradle | 9.4.1 |
| AGP | 9.2.1 |
| Ktor | 3.5.0 |
| Room | 2.8.4 |
| Koin | 4.2.2 |

### Platform Support

- **Android**: minSdk 24, targetSdk 37, compileSdk 37
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
```

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
- **Compose resources**: Each module maintains its own `src/commonMain/composeResources/values/strings.xml` (Chinese) and `values-en/strings.xml` (English)
- **Generated resource IDs**: `gainful.<module>.generated.resources.*` (e.g., `gainful.feature.dashboard.generated.resources.*`)
- **Format strings**: Use positional args: `%1$d`, `%2$s` (CMP requirement)

### Shared UI Components (`core/ui`)

| Component | File | Description |
|-----------|------|-------------|
| `GainfulScaffold` | `ScreenScaffold.kt` | Common screen layout container (optional top bar) |
| `NavButton` | `Button.kt` | Circular navigation button (‹ ›) |
| `PrimaryButton` | `Button.kt` | Filled button (customizable color) |
| `SecondaryButton` | `Button.kt` | Outlined button |
| `SelectChip` | `Button.kt` | Selectable chip with icon + label |
| `SquareIconButton` | `Button.kt` | Square icon toggle button |
| `GainfulDialog` | `Dialog.kt` | Generic dialog container (title + content + buttons) |
| `ConfirmDialog` | `Dialog.kt` | Confirm dialog (customizable confirm button color) |
| `GainfulTopAppBar` | `TopAppBar.kt` | Top navigation bar |
| `BottomBar` / `Modifier.bottomBarPadding()` | `BottomBar.kt` | Bottom navigation bar / bottom padding modifier |
| `DatePickerField` / `CalendarDialog` | `DatePicker.kt` | Date picker |
| `TimePickerField` / `TimePickerDialog` | `TimePicker.kt` | Time picker |
| `DateTimePickerField` / `DateTimePickerDialog` | `DateTimePicker.kt` | Date-time picker |
| `LoadingIndicator` | `Loading.kt` | Loading state indicator |
| `TransactionCard` | `TransactionCard.kt` | Transaction card |
| `BackNavigationIcon` / `NavIcons` | `NavIcons.kt` | Navigation icons |

### Testing

```bash
./gradlew allTests
```

## License

This project is licensed under the [Apache License 2.0](../LICENSE).
