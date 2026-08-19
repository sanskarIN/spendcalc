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
- Optional user-provided labels when saving calculations so history search can find meaningful names; blank labels retain the safe `Calculation` fallback.
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
- Visible modal backup progress state while backup reads, writes, serialization, or restore work is active.
- Versioned backup format with SHA-256 corruption detection, strict record validation, bounded decoding, duplicate-ID rejection, and compensating rollback if a multi-store restore fails.
- Responsive phone/tablet calculator layout.
- About/support/funding UI with `Made by the Sanskar` credit.
- Sequenced user-feedback events so repeated saves, deletes, backup results, and errors are not collapsed by `StateFlow` equality.
- Unit tests for calculation arithmetic, validation, repositories, backup codec, exports, path containment, safe logging, and UI-state feedback sequencing.
- Deterministic seeded fuzz/regression coverage for finance arithmetic and backup serialization/corruption handling.
- Android integration tests for Room persistence and backup replacement, plus Compose and real-activity journey smoke tests.
- Compose regression coverage for the named-history save dialog and callback wiring.
- Settings UI coverage for the backup busy state.
- CI compilation of instrumentation tests in addition to JVM tests, full Android lint, debug build, and release compilation.
- Android manifest/FileProvider local-first policy guard in CI.
- Repository metadata/link audit, secret-pattern scan, CodeQL, and dependency-review workflows.
- Project policies for privacy, security, support, contribution, and community conduct.

### Changed

- Discount validation now caps discounts at 100% so a valid discount cannot make the taxable base negative.
- Monetary/exchange-rate inputs are bounded by supported precision, scale, text length, and integer-digit limits.
- Split counts are bounded to 1 through 1,000,000.
- Editable expense items are capped at 100 with a visible UI limit state to bound eager Compose work.
- Calculator item/name/input counts are bounded before expensive conversion or rendering work.
- Saved history labels and template names now share one 120-character domain limit; persistence normalizes the value before storage so valid local data cannot later violate backup validation.
- Saved-name normalization now occurs at the repository boundary instead of being partially duplicated in the ViewModel.
- Backup document I/O runs on `Dispatchers.IO`, while bounded backup encoding/decoding runs on `Dispatchers.Default` instead of the UI thread.
- Room history/templates are captured in one transaction for backups and restored with batch DAO inserts.
- Backup result decimals accept the full bounded magnitude that `CalculatorEngine` can legitimately produce, including converted totals up to 34 integer digits.
- Returning users remain on the splash screen until stored preferences load, avoiding a false onboarding flash.
- Corrupted Preferences DataStore files recover to safe default preferences without deleting Room history/templates.
- Bottom-navigation icon graphics are decorative when a visible text label already provides the accessible name, avoiding duplicate screen-reader announcements.
- GitHub Actions use maintained major action versions and concurrency cancellation for superseded pull-request runs.
- CI runs Android lint across configured variants rather than only the debug variant.

### Security

- Core application requires no Android Internet permission, and CI now fails if that manifest invariant regresses.
- Export sharing uses app-private `cache/exports` files and temporary URI read permission; CI verifies the FileProvider remains non-exported and exposes only `cache/exports/`.
- Export path containment uses canonical path semantics rather than vulnerable string-prefix matching.
- CSV text values are protected from common spreadsheet formula injection prefixes.
- Backup parser rejects oversized payloads, excessive line counts, malformed checksum fields, exponent-expansion decimal shapes, duplicate identifiers, invalid timestamps, invalid currencies, unsupported schema versions, oversized saved names, malformed UTF-8 input, and out-of-contract result magnitudes.
- Backup export rejects malformed Unicode instead of silently replacing invalid surrogate data.
- Structured logging redacts sensitive keys and performs key normalization with `Locale.ROOT` so redaction is locale independent.
- Production signing material is intentionally not stored in the repository.

## [1.0.0] - Planned

First production release after the current pull-request automation and remaining manual device/accessibility/signing/screenshot release gates are completed.
