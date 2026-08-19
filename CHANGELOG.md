# Changelog

All notable changes to SpendCalc are documented here. The project follows a semantic-versioning-oriented release process.

## [Unreleased]

### Added

- Android application bootstrap with Kotlin and Jetpack Compose.
- Precision-safe `BigDecimal` calculation engine with explicit rounding policy.
- Itemized expense totals.
- Discount, tax, tip, service-charge, and split-bill calculations.
- Manual exchange-rate conversion with configurable three-letter currency codes.
- Receipt-style result presentation.
- Room-backed calculation history with individual deletion, clear-all confirmation, search/filter, and undo after individual deletion.
- Optional 30-day and 90-day automatic history retention.
- Room-backed reusable calculation templates with undo after individual deletion.
- DataStore preferences for theme, large text, reduced motion, retention, and onboarding.
- Light, dark, and system appearance modes.
- Reduced-motion-aware navigation transitions.
- First-run onboarding.
- Branded AndroidX launch splash screen using repository-owned SpendCalc artwork.
- Repository-owned vector icons for Calculator, History, Templates, and Settings navigation.
- CSV export with spreadsheet-formula neutralization for text cells.
- Plain-text receipt sharing.
- Offline Android PDF receipt generation.
- Secure cache-file sharing through a non-exported `FileProvider`.
- User-driven local backup/export and restore for history, templates, and preferences through Android's document picker.
- Visible backup progress state with duplicate backup actions disabled while work is active.
- Versioned backup format with SHA-256 corruption detection, strict record validation, bounded decoding, duplicate-ID rejection, and compensating rollback if a multi-store restore fails.
- Responsive phone/tablet calculator layout.
- About/support/funding UI with `Made by the Sanskar` credit.
- Unit tests for calculation arithmetic, validation, repositories, backup codec, exports, path containment, and safe logging.
- Deterministic seeded fuzz/regression coverage for finance arithmetic and backup serialization/corruption handling.
- Android integration tests for Room persistence and backup replacement, plus Compose and real-activity journey smoke tests.
- Settings UI coverage for the backup busy state.
- CI compilation of instrumentation tests in addition to JVM tests, full Android lint, debug build, and release compilation.
- Repository metadata/link audit, secret-pattern scan, CodeQL, and dependency-review workflows.
- Project policies for privacy, security, support, contribution, and community conduct.

### Changed

- Discount validation now caps discounts at 100% so a valid discount cannot make the taxable base negative.
- Monetary/exchange-rate inputs are bounded by supported precision, scale, text length, and integer-digit limits.
- Split counts are bounded to 1 through 1,000,000.
- Editable expense items are capped at 100 with a visible UI limit state to bound eager Compose work.
- Calculator item/name/input counts are bounded before expensive conversion or rendering work.
- Backup and export file I/O is performed off the main thread.
- Bottom-navigation icon graphics are decorative when a visible text label already provides the accessible name, avoiding duplicate screen-reader announcements.
- GitHub Actions use maintained major action versions and concurrency cancellation for superseded pull-request runs.
- CI runs Android lint across configured variants rather than only the debug variant.

### Security

- Core application requires no Android Internet permission.
- Export sharing uses app-private `cache/exports` files and temporary URI read permission.
- Export path containment uses canonical path semantics rather than vulnerable string-prefix matching.
- CSV text values are protected from common spreadsheet formula injection prefixes.
- Backup parser rejects oversized payloads, excessive line counts, malformed checksum fields, exponent-expansion decimal shapes, duplicate identifiers, invalid timestamps, invalid currencies, and unsupported schema versions.
- Structured logging redacts sensitive keys and performs key normalization with `Locale.ROOT` so redaction is locale independent.
- Production signing material is intentionally not stored in the repository.

## [1.0.0] - Planned

First production release after the current pull-request automation and remaining manual device/accessibility/signing/screenshot release gates are completed.
