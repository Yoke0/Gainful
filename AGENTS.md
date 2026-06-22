# Gainful — Agent Guide

## Project

Kotlin Multiplatform + Compose Multiplatform app targeting Android, iOS, and Desktop (JVM).
Architecture: **MVI** (Model-View-Intent), Clean Architecture layering.
UI is in Chinese. Dark-only theme (`GainfulTheme` uses `darkColorScheme` only).

## Modules

- `shared/` — KMP library. Shared UI and logic live here.
  - `commonMain/` — platform-agnostic code (all targets)
  - `androidMain/` — Android platform implementation
  - `iosMain/` — iOS platform implementation
  - `jvmMain/` — Desktop platform implementation
- `core/` — Core modules (Clean Architecture)
  - `common/` — Utilities, extensions, constants
  - `model/` — Data models (DTOs, Entities)
  - `ui/` — Common UI components, theme colors
  - `data/` — Repository interfaces + offline implementations
  - `database/` — Room with BundledSQLiteDriver (cross-platform)
  - `network/` — Ktor HTTP client (expect/actual per platform)
  - `domain/` — UseCases
  - `navigation/` — Navigation config (Navigation3)

- `feature/` — Feature modules (per business)
  - `dashboard/`, `holdings/`, `transactions/`, `settings/`
  - Sub-packages: `overview/` (main list), `add/` or `detail/` (secondary screens), `di/` (Koin modules)
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

# iOS — open in Xcode (requires TEAM_ID in iosApp/Configuration/Config.xcconfig)
open iosApp/iosApp.xcodeproj
```

## Tests

```bash
./gradlew allTests                         # All platform tests
```

Run tests after making changes. CI runs `./gradlew allTests`.

## Key Versions

- Kotlin 2.4.0
- Gradle 9.4.1
- AGP 9.2.1
- Compose Multiplatform 1.11.1
- Material3 1.12.0-alpha02
- JVM target: 11
- compileSdk/targetSdk: 37, minSdk: 24
- Ktor 3.5.0
- Room 2.8.4
- Koin 4.2.2

## Conventions

- Package: `com.yoke.gainful`
- Dependencies managed via Gradle version catalog (`gradle/libs.versions.toml`)
- `TYPESAFE_PROJECT_ACCESSORS` enabled — use `projects.shared` (not `:shared`)
- iOS framework name: `Shared` (static)
- Compose resources in `shared/src/commonMain/composeResources/`
- Generated resource IDs: `gainful.shared.generated.resources.*`

## Dependency Rules

- `feature/` depends only on `core/domain/`, `core/model/`, and `core/ui/`
- `core/domain/` depends on `core/data/` and `core/model/`
- `core/data/` defines interfaces; `core/database/` and `core/network/` provide implementations

- `shared/` is the aggregator — depends on all `core/` and `feature/` modules

## Git Commit Convention

Strictly adhere to the **Conventional Commits** format for all code diffs or task descriptions.

### 1. Format
`<type>(<scope>): <description>`

### 2. Allowed Types & Scopes
- **Types**: `feat` (feature), `fix` (bug fix), `refactor` (code rewrite), `chore` (build/deps), `test`, `docs`, `perf`, `style`.
- **Scopes**: `ui`, `compose`, `auth`, `network`, `database`, `viewmodel`, `gradle`, `deps`, `navigation`, `di`
  - `dashboard`, `holdings`, `transactions`, `settings`, `shared`, `core`, `feature`

### 3. Rules
- **Imperative Mood**: Use present tense (e.g., "add", not "added").
- **Casing**: Lowercase `<type>`, `<scope>`, and the first letter of `<description>`.
- **No Period**: Do not end the description line with a period.
- **Output Only**: Return **only** the raw commit message text. No explanations, no markdown blocks.

### 4. Quick Examples
- `feat(dashboard): add portfolio summary card`
- `fix(network): handle timeout on East Money API`
- `chore(deps): bump AGP version to 9.2.1`

## Localization (i18n)

Each module has its own `composeResources/` with localized strings:
- `values/strings.xml` — Chinese (default)
- `values-en/strings.xml` — English

### Adding new strings
1. Add `<string name="key">文本</string>` to the module's `values/strings.xml`
2. Add English translation to `values-en/strings.xml`
3. Use positional format args: `%1$d`, `%2$s` (not `%d`, `%s`)
4. Import: `import gainful.<module>.generated.resources.<key>`
5. Usage: `stringResource(Res.string.key)` in @Composable functions

### Gotchas
- `import xxx.generated.resources.string` does NOT work — use individual imports
- ViewModels can't use `stringResource()` — resolve strings in UI layer
- Enums with display labels: use `@Composable` extension function to resolve labels

## Android Verification

After making changes, verify on Android device using this workflow:

> **Prerequisites**: `android` CLI installed (`curl -fsSL https://dl.google.com/android/cli/latest/darwin_arm64/install.sh | bash`), `adb` at `~/Library/Android/sdk/platform-tools/adb`, wireless ADB connected.

```bash
# 1. Delete old APK, build and install
rm -f androidApp/build/outputs/apk/debug/*.apk
./gradlew :androidApp:installDebug

# 2. Launch app
adb shell am force-stop com.yoke.gainful
adb shell am start -n com.yoke.gainful/.MainActivity

# 3. Inspect UI (structured JSON with center coords)
android layout

# 4. Tap element (use center from layout output)
adb shell input tap <x> <y>

# 5. Check what changed after an action
android layout --diff

# 6. Screenshot (only when visual inspection needed)
android screen capture -o /tmp/s.png
```

- `android layout` returns JSON with `text`, `center`, `bounds`, `interactions` — no grep needed.
- `adb shell input tap` may fail with SecurityException on first wireless ADB attempt; retry works.
- Clean up screenshots after verification.

## Gotchas

- **NEVER use `java.time.*` in commonMain** — use `kotlin.time.Instant`/`Clock` + `kotlinx.datetime.toLocalDateTime()` + `kotlinx.datetime.TimeZone` instead. Add `libs.kotlinx.datetime` dependency if needed.
- `local.properties` is gitignored — SDK path must be set per machine
- Android tests use `androidHostTest` source set (not the usual `androidTest`)
- Configuration cache is enabled — Gradle will re-run if scripts change
- Room schemas stored in `core/database/schemas/` — checked in for migration tracking
- Ktor HTTP client uses `expect`/`actual` — platform engines: OkHttp (Android), Darwin (iOS), Java (JVM)
- `initKoin()` is called in both `App.kt` (Compose) and `MainActivity.kt` (Android) — don't double-init
