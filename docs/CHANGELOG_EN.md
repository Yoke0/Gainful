# Changelog

All notable changes to the Gainful application are documented in this file. Format based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [1.1.0] - 2026-07-12

### Added

#### UI
- Added KLineChart composable for candlestick visualization with MA lines, volume bars, crosshair, and tooltip
- Added reusable LineChart component, replacing all inline chart implementations
- Overhauled PnL overview section with key metrics grid layout

#### Dashboard
- Optimized chart display using aggregated intraday PnL data

### Fixed

- Fixed formatMinimumFractionDigits precision issue

### Refactored

- Optimized LineChart and HoldingCard for desktop display

### Build & Dependencies

- Upgraded Gradle to 9.6.1 and fixed deprecation warnings

## [1.0.2] - 2026-07-07

### Added

#### Dashboard
- PnL overview card
- Real PnL calculation based on K-line data, replacing simulated data
- Stock detail bottom sheet showing holdings and transaction info
- PnL detail now displayed via ModalBottomSheet

#### Holdings
- Replaced heatmap layout with squarify treemap for better readability

#### Widgets
- Android and iOS app widgets displaying today's P&L
- Optimized Android widget size to 2x2/2x4
- Widget support for immediate refresh and background sync

#### UI
- Replaced all text/emoji icons with vector XML drawables

### Fixed

- Unified bottom padding and screen spacing consistency across pages
- Disabled R8 full mode to fix Glance widget rendering in release builds

### Refactored

- Added PnL cache with compute/query separation

### Build & CI

- Added spotlessCheck to GitHub Actions for code formatting

### Documentation

- Added stock calculation rules documentation
- Updated project documentation

## [1.0.1] - 2026-06-30

Gainful v1.0.1 is the first official release. It is a Kotlin Multiplatform + Compose Multiplatform portfolio management application supporting Android, iOS, and Desktop.

### Added

#### Core Architecture
- Clean Architecture-based modular architecture with core, feature, and shared layers
- Navigation3 navigation framework for shared navigation across platforms
- DataStore user preferences storage module
- Koin dependency injection framework integration
- GitHub Actions CI/CD workflow for unit tests and build verification

#### Dashboard
- Real-time portfolio data display connected to database and network sources
- Daily PnL (Profit and Loss) card for intuitive daily returns visualization

#### Holdings
- Heatmap-style holdings display with compact card design
- Stock detail page with navigation support
- Closed positions list display
- Customizable gain/loss color scheme settings

#### Transactions
- Transaction history page with asset search
- Add transaction screen with quick holdings selection
- Calendar dialog date picker replacing text input
- Trade date field and UUID unique identifiers
- Sorting by trade date and holdings by quantity

#### Settings
- Frequency picker and time picker
- CSV import with duplicate check, delete dialog, and asset enrichment
- Gain/loss color scheme settings

#### UI Components
- TimePicker wheel picker component
- DateTimePicker combining calendar and wheel time picker
- GainfulTopAppBar component
- GainfulDialog component
- Reusable button components (NavButton, PrimaryButton, SecondaryButton, SelectChip, SquareIconButton)
- BottomBar component
- ScreenScaffold component
- Design system module (core/designsystem) for unified UI component management

#### Data Layer
- EastMoney API client for stock data fetching
- Room database with BundledSQLiteDriver for cross-platform support
- Stock price caching and background refresh
- iOS CsvFileUtil native UIKit implementation

#### Internationalization
- Multi-module string localization with Chinese and English support
- App name localized to "盈迹" across all platforms

#### Platform
- Android icon design
- Desktop build configuration
- Android core/common module with built-in android.icu

### Fixed

- Navigation ViewModel leak and TopLevelBackStack removeLast bug
- ViewModel loss during composition rebuild
- DatePicker month navigation crash across year boundary
- CalendarDialog height animation when switching months
- formatDecimal floating point precision error using integer arithmetic
- Transaction sorting and holdings chips sorting
- Holdings sparkline X-axis direction
- iOS RoomDatabaseConstructor issue
- Disabled predictive back gesture on Android
- Disabled global ripple effect (NoOpIndication)

### Refactored

- Reorganized project structure with feature sub-packages and Route abstraction
- Extracted GainfulScaffold to core/designsystem module
- Extracted BottomBar to core/ui module
- Extracted reusable button components to core/ui module
- Unified dialog patterns with GainfulDialog extraction
- Centralized number and date formatting into core/common
- Simplified TransactionCard component
- Redesigned settings page layout
- Refactored navigation to nowinandroid pattern with entry providers

### Build & Dependencies

- Added release build script with R8 minification and ProGuard obfuscation
- Simplified release signing configuration
- Added spotless and ktlint code formatting
- Replaced deprecated androidLibrary block
- Updated GitHub Actions to v7/v5/v6 for Node.js 24 compatibility

### Documentation

- Updated README and AGENTS.md
- Added UI components reference documentation
- Added ViewModel recreation fix writeup for MIUI home launch
- Added Screen Composition rules

## [1.0.0] - Unreleased

Initial version (internal development).
