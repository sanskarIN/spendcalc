# Changelog

All notable changes to SpendCalc are documented here. The project follows a semantic-versioning-oriented release process.

## [Unreleased]

### Added

- Android application bootstrap with Kotlin and Jetpack Compose.
- Precision-safe `BigDecimal` calculation engine.
- Itemized expense totals.
- Discount, tax, tip, service-charge, and split-bill calculations.
- Manual exchange-rate conversion with configurable currency codes.
- Receipt-style result presentation.
- Room-backed calculation history with individual deletion and clear-all controls.
- Optional 30-day and 90-day automatic history retention.
- Room-backed reusable calculation templates.
- DataStore preferences for theme, accessibility, retention, and onboarding.
- Light, dark, and system appearance modes.
- Large-text and reduced-motion preferences.
- First-run onboarding.
- CSV export with spreadsheet-formula neutralization for text cells.
- Plain-text receipt sharing.
- Offline Android PDF receipt generation.
- Secure cache-file sharing through a non-exported `FileProvider`.
- Responsive phone/tablet calculator layout.
- About/support/funding UI with `Made by the Sanskar` credit.
- Unit tests for calculation arithmetic, rounding, validation, repositories, and exports.
- Android integration tests for Room persistence and a Compose calculator smoke test.
- Project policies for privacy, security, support, contribution, and community conduct.

### Security

- Core application currently requires no Android Internet permission.
- Export sharing uses app-private cache files and temporary URI read permission.
- CSV text values are protected from common spreadsheet formula injection prefixes.

## [1.0.0] - Planned

First production release after clean-build, lint, test, security, accessibility, documentation, and release-candidate verification are complete.
