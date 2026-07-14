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
  - `ui/` — Common UI components, theme colors (see [UI Components](#ui-components) below)
  - `designsystem/` — Design system tokens, shared styling
  - `data/` — Repository interfaces + offline implementations
  - `database/` — Room with BundledSQLiteDriver (cross-platform)
  - `datastore/` — Preferences DataStore (settings)
  - `network/` — Ktor HTTP client (expect/actual per platform)
  - `domain/` — UseCases (see [Domain UseCases](#domain-usecases) below)
  - `sync/` — Background data sync (stock price fetching, KLine caching)
  - `file/` — File I/O utilities
  - `navigation/` — Navigation config (Navigation3)

- `feature/` — Feature modules (per business)
  - `dashboard/`, `holdings/`, `transactions/`, `settings/`
  - Sub-packages: `overview/` (main list), `add/` or `detail/` (secondary screens), `di/` (Koin modules)
- `androidApp/` — Android entry point (`MainActivity`)
- `desktopApp/` — Desktop entry point (`main.kt`, main class `com.yoke.gainful.MainKt`)
- `iosApp/` — iOS project (Xcode, Swift). Uses `MainViewController.kt` from `shared/iosMain`
- `server/` — Ktor backend server (see [Server](#server) below)

## Build & Run

```bash
# Android
./gradlew :androidApp:assembleDebug

# Desktop
./gradlew :desktopApp:run                  # standard
./gradlew :desktopApp:hotRun --auto        # hot reload

# iOS — open in Xcode (requires TEAM_ID in iosApp/Configuration/Config.xcconfig)
open iosApp/iosApp.xcodeproj

# Server
./gradlew :server:run
```

## Tests

```bash
./gradlew allTests                         # All platform tests
```

Run tests after making changes. CI runs `./gradlew allTests`.

## Key Versions

- Kotlin 2.4.0
- Gradle 9.6.1
- AGP 9.2.1
- Compose Multiplatform 1.11.1
- Material3 1.12.0-alpha02
- JVM target: 11
- compileSdk/targetSdk: 37, minSdk: 24
- Ktor 3.5.1
- Room 2.8.4
- Koin 4.2.2
- Exposed 1.3.1
- kotlinx-datetime 0.8.0

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

## Domain UseCases

Located in `core/domain/src/commonMain/kotlin/com/yoke/gainful/domain/usecase/`:

- **`asset/`** — `SearchAssetsUseCase`, `GetStockDetailUseCase`
- **`dashboard/`** — `ComputePnlUseCase` (daily PnL calculation), `GetPnlDataUseCase` (period PnL aggregation + stock detail)
- **`holding/`** — `GetHoldingsUseCase`, `GetHoldingsDisplayUseCase` (with live quotes), `GetClosedPositionsUseCase`
- **`transaction/`** — `GetTransactionsUseCase`, `AddTransactionUseCase`, `UpdateTransactionUseCase`, `DeleteTransactionUseCase`, `GetTransactionDetailUseCase`

Key calculation formulas are documented in [`docs/stock-calculation-rules.md`](docs/stock-calculation-rules.md).

## Documentation

- `docs/stock-calculation-rules.md` — Stock calculation rules and formulas (PnL, holdings, fees, aggregation)
- `docs/CHANGELOG.md` / `docs/CHANGELOG_EN.md` — Release changelogs
- `README.en.md` — English version of the README
- `docs/private/` — Internal design specs (HTML mockups, API field references)

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

## UI Components

Reusable composables in `core/ui/src/commonMain/kotlin/com/yoke/gainful/ui/`:

- **ScreenScaffold.kt** — `GainfulScaffold` (common screen layout with optional top bar)
- **components/BottomBar.kt** — `BottomBar`, `Modifier.bottomBarPadding()`
- **components/Button.kt** — `NavButton`, `PrimaryButton`, `SecondaryButton`, `SelectChip`, `SquareIconButton`
- **components/Dialog.kt** — `GainfulDialog`, `ConfirmDialog`
- **components/TopAppBar.kt** — `GainfulTopAppBar`
- **components/DatePicker.kt** — `DatePickerField`, `CalendarDialog`, `CalendarGrid`
- **components/TimePicker.kt** — `TimePickerField`, `TimePickerDialog`
- **components/DateTimePicker.kt** — `DateTimePickerField`, `DateTimePickerDialog`
- **components/Loading.kt** — `LoadingIndicator`
- **TransactionCard.kt** — `TransactionCard`
- **LineChart.kt** — `LineChart` (reusable line chart with trend lines, baseline, touch interaction)
- **KLineChart.kt** — `KLineChart` (candlestick chart with MA lines, volume bars, crosshair, tooltip)
- **components/NavIcons.kt** — `BackNavigationIcon`, nav icons

Button naming follows Material3 conventions: `PrimaryButton` (filled), `SecondaryButton` (outlined), `NavButton` (circular icon), `SelectChip` (toggleable chip), `SquareIconButton` (square icon).

## Screen Composition

### Structure: Two Overloaded Methods

Each screen has two composable functions with the same name:

```kotlin
// 1. Data handling — collects state, binds ViewModel
@Composable
fun XxxScreen(viewModel: XxxViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    // LaunchedEffect, callbacks...
    XxxScreen(uiState = uiState, onIntent = viewModel::onIntent, onBack = onBack)
}

// 2. Pure layout — no ViewModel dependency
@Composable
private fun XxxScreen(uiState: XxxUiState, onIntent: (XxxIntent) -> Unit, onBack: () -> Unit) {
    GainfulScaffold(appTopBar = { ... }) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // content blocks
        }
    }
}
```

### Outer Container: GainfulScaffold

```kotlin
GainfulScaffold(
    modifier = Modifier,            // default: Modifier.padding(horizontal = 16.dp)
    appTopBar = { GainfulTopAppBar(title = "...", navigationIcon = { ... }, actions = { ... }) },
    content: @Composable ColumnScope.() -> Unit,
)
```

- `appTopBar` is optional — omit when no top bar needed (e.g., Loading/Error states)
- `modifier` defaults to horizontal padding; callers override for additional modifiers
- Located in `core/ui/ScreenScaffold.kt`

### Inner Column

- Use `verticalArrangement = Arrangement.spacedBy(14.dp)` for spacing between sections
- Do NOT use `Spacer` for inter-section spacing — use `spacedBy` only
- Use `Modifier.verticalScroll(rememberScrollState())` for scrollable content

### Bottom Bar Padding

- Use `Modifier.bottomBarPadding()` on the last content element
- Defined in `core/designsystem/components/BottomBar.kt`
- Do NOT use `Spacer(modifier = Modifier.height(BottomBarHeight))` — use the modifier instead
- Omit when no bottom bar offset needed (e.g., detail screens)

## Gotchas

- **NEVER use `java.time.*` in commonMain** — use `kotlin.time.Instant`/`Clock` + `kotlinx.datetime.toLocalDateTime()` + `kotlinx.datetime.TimeZone` instead. Add `libs.kotlinx.datetime` dependency if needed.
- `local.properties` is gitignored — SDK path must be set per machine
- Android tests use `androidHostTest` source set (not the usual `androidTest`)
- Configuration cache is enabled — Gradle will re-run if scripts change
- Room schemas stored in `core/database/schemas/` — checked in for migration tracking
- Ktor HTTP client uses `expect`/`actual` — platform engines: OkHttp (Android), Darwin (iOS), Java (JVM)
- `initKoin()` is called in both `App.kt` (Compose) and `MainActivity.kt` (Android) — don't double-init

## Server

Ktor backend server for API, authentication, and data management.

### Tech Stack
- Kotlin + Ktor (Netty engine) + Exposed 1.3.1 (DSL) + PostgreSQL
- Koin (DI), JWT (auth), `kotlin.uuid.Uuid` (IDs)
- H2 for testing, PostgreSQL for production
- Swagger UI at `/swagger`

### Security Architecture
- `TokenConfig` — JWT configuration (issuer, audience, secret, expiry)
- `TokenService` / `JwtTokenService` — JWT generation and validation
- `UserPrincipal` — Authenticated user context (userId, sessionId, username)
- `SessionService` — Server-side session management with expiry check
- `Security.kt` — Ktor JWT authentication with audience validation + session validity check

### API Endpoints
- `POST /api/auth/register` — Register
- `POST /api/auth/login` — Login (returns JWT)
- `GET /api/users/me` — Get profile
- `PUT /api/users/me` — Update profile
- `POST /api/users/avatar` — Upload avatar (multipart)
- `GET /api/users/sessions` — List sessions
- `DELETE /api/users/sessions` — Revoke other sessions
- `GET /api/transactions` — List transactions
- `POST /api/transactions` — Create transaction
- `DELETE /api/transactions/{id}` — Delete transaction
- `GET /avatars/{filename}` — Static file serving for avatars

### Project Structure
```
server/
├── src/main/kotlin/com/yoke/gainful/server/
│   ├── Application.kt          # Entry point (module())
│   ├── config/                 # AppConfig, DatabaseFactory, KoinModule
│   ├── db/                     # Exposed table definitions (Users, Transactions, UserSessions)
│   ├── model/dto/              # Request/Response DTOs
│   ├── plugins/                # Ktor plugins (Security, Routing, Serialization, StatusPages)
│   ├── routes/                 # Route handlers (AuthRoutes, UserRoutes, TransactionRoutes)
│   ├── security/token/         # TokenConfig, TokenClaim, TokenService, JwtTokenService
│   ├── service/                # Business logic (Auth, User, Session, Transaction, Avatar)
│   └── util/                   # PasswordUtils
├── src/main/resources/
│   ├── application.conf        # HOCON config (database, jwt, upload)
│   └── openapi/documentation.yaml
└── src/test/                   # Unit tests (service + route tests with H2)
```

### Exposed 1.x Migration Notes
- Package: `org.jetbrains.exposed.v1.core` / `org.jetbrains.exposed.v1.jdbc`
- DateTime: `exposed-kotlin-datetime` (kotlinx.datetime.LocalDateTime)
- UUID columns: `kotlin.uuid.Uuid` (not `java.util.UUID`)
- `eq`/`and`/`or`/`neq` are member functions of `SqlExpressionBuilder` — import from `org.jetbrains.exposed.v1.core.eq` etc.
- `Clock.System` is in `kotlin.time.Clock` (not `kotlinx.datetime.Clock` in 0.8.0)
