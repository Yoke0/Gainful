# Gainful — Agent Guide

## Project

Kotlin Multiplatform + Compose Multiplatform app targeting Android, iOS, and Desktop (JVM).

## Modules

- `shared/` — KMP library. Shared UI and logic live here.
  - `commonMain/` — platform-agnostic code (all targets)
  - `androidMain/`, `iosMain/`, `jvmMain/` — platform-specific `expect`/`actual` implementations
- `core/` — Core modules (Clean Architecture)
  - `common/` — Utilities, extensions, constants
  - `data/` — Repository interfaces (contracts)
  - `data-local/` — Local data source (SQLDelight)
  - `data-remote/` — Remote data source (Ktor)
  - `domain/` — UseCases
  - `ui/` — Common UI components, theme
  - `navigation/` — Navigation config (Navigation3)
  - `testing/` — Test utilities, Fake implementations
- `feature/` — Feature modules (per business)
  - `dashboard/`, `holdings/`, `transactions/`, `settings/`
- `build-logic/` — Convention plugins
- `androidApp/` — Android entry point (`MainActivity`)
- `desktopApp/` — Desktop entry point (`main.kt`, main class `com.yoke.gainful.MainKt`)
- `iosApp/` — iOS project (Xcode, Swift). Uses `MainViewController.kt` from `shared/iosMain`

## Build & Run

```bash
# Android
./gradlew :androidApp:assembleDebug

# Desktop
./gradlew :desktopApp:run                  # standard
./gradlew :desktopApp:hotRun --auto        # hot reload

# iOS — open in Xcode
open iosApp/iosApp.xcodeproj
```

## Tests

```bash
./gradlew :shared:testAndroidHostTest      # Android host tests
./gradlew :shared:jvmTest                  # Desktop tests
./gradlew :shared:iosSimulatorArm64Test    # iOS tests
```

Run tests after making changes to `shared/`. There is no CI; local verification is required.

## Key Versions

- Kotlin 2.4.0, Gradle 9.1.0, AGP 9.0.1
- Compose Multiplatform 1.11.1, Material3 1.11.0-alpha07
- JVM target: 11

## Conventions

- Package: `com.yoke.gainful`
- Dependencies managed via Gradle version catalog (`gradle/libs.versions.toml`)
- `TYPESAFE_PROJECT_ACCESSORS` enabled — use `projects.shared` (not `:shared`)
- iOS framework name: `Shared` (static)
- Compose resources in `shared/src/commonMain/composeResources/`
- Generated resource IDs: `gainful.shared.generated.resources.*`

## Dependency Rules

- `feature/` depends only on `core/domain/` and `core/ui/`, never on `core/data/`
- `core/data/` defines interfaces; `core/data-local/` and `core/data-remote/` provide implementations
- `core/testing/` provides Fake implementations for testing only

## Git Commit Convention

Strictly adhere to the **Conventional Commits** format for all code diffs or task descriptions.

### 1. Format
`<type>(<scope>): <description>`

### 2. Allowed Types & Scopes
- **Types**: `feat` (feature), `fix` (bug fix), `refactor` (code rewrite), `chore` (build/deps), `test`, `docs`, `perf`, `style`.
- **Android Scopes**: `ui`, `compose`, `auth`, `network`, `database`, `viewmodel`, `gradle`, `deps`, `navigation`, `di`.

### 3. Rules
- **Imperative Mood**: Use present tense (e.g., "add", not "added").
- **Casing**: Lowercase `<type>`, `<scope>`, and the first letter of `<description>`.
- **No Period**: Do not end the description line with a period.
- **Output Only**: Return **only** the raw commit message text. No explanations, no markdown blocks.

### 4. Quick Examples
- `feat(auth): add Google Sign-In button to LoginActivity`
- `fix(network): resolve NullPointerException in OkHttpClient callback`
- `chore(deps): bump AGP version to 8.2.0`

## Gotchas

- `local.properties` is gitignored — SDK path must be set per machine
- Android tests use `androidHostTest` source set (not the usual `androidTest`)
- Configuration cache is enabled — Gradle will re-run if scripts change
