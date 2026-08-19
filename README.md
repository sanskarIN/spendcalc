<p align="center">
  <img src="docs/assets/spendcalc-logo.svg" alt="SpendCalc logo" width="128" height="128" />
</p>

<h1 align="center">SpendCalc</h1>

<p align="center"><strong>A precision-safe, private, offline-first expense calculator for Android.</strong></p>

<p align="center">
  <a href="https://github.com/sanskarIN/spendcalc/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/sanskarIN/spendcalc/actions/workflows/ci.yml/badge.svg" /></a>
  <a href="https://github.com/sanskarIN/spendcalc/actions/workflows/codeql.yml"><img alt="CodeQL" src="https://github.com/sanskarIN/spendcalc/actions/workflows/codeql.yml/badge.svg" /></a>
  <img alt="Android API 26+" src="https://img.shields.io/badge/Android-API%2026%2B-3DDC84?logo=android&logoColor=white" />
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?logo=kotlin&logoColor=white" />
  <a href="LICENSE"><img alt="MIT License" src="https://img.shields.io/badge/License-MIT-blue.svg" /></a>
</p>

<p align="center">
  <a href="https://buymeacoffee.com/sanskarIN"><img alt="Buy Me a Coffee" src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000" /></a>
</p>

> **Made by the Sanskar**

SpendCalc is built for everyday expense calculations rather than demo arithmetic. It keeps finance rules separate from Android UI/infrastructure, uses `BigDecimal` for money, works without a required network connection or account, and provides local history, templates, explicit backup/restore, accessibility settings, and offline export options.

## Screenshots

Verified screenshots are intentionally captured from real release-candidate builds rather than presented as fake production images. The capture checklist is in [`docs/assets/screenshots/README.md`](docs/assets/screenshots/README.md). Until those captures are added, the editable brand artwork at [`docs/assets/spendcalc-logo.svg`](docs/assets/spendcalc-logo.svg) is the repository's visual placeholder.

## Features

### Expense calculation

- Itemized expense lines with live totals.
- Up to 100 editable expense items per calculation, with an explicit limit state to keep the eager Compose editor bounded.
- Discount, tax, tip, and service-charge percentages.
- Discount capped at 100% so valid input cannot produce a negative discounted base.
- Split-bill calculation for 1 through 1,000,000 people.
- Manual exchange-rate conversion with three-letter currency codes.
- Precision-safe `BigDecimal` arithmetic and centralized rounding policy.
- Bounded decimal precision/scale and input lengths to prevent pathological numeric expansion.
- Receipt-style result view.

### Save, find, and reuse

- Room-backed calculation history.
- Optional user-provided history labels, bounded to 120 characters, with a safe default when left blank.
- UTF-16-safe saved-name truncation avoids splitting valid surrogate pairs such as emoji at the boundary.
- Local history search by labels, currencies, totals, and per-person values, with a bounded 120-character query.
- Individual history deletion with Snackbar Undo.
- Clear-all confirmation.
- Optional history auto-delete after 30 or 90 days.
- Saved templates for common discount/tax/tip/service/split/currency settings.
- New saved history labels/template names are normalized at the persistence boundary, while already-valid restore records are preserved exactly.
- Individual template deletion with Snackbar Undo.

### Backup and restore

- Explicit user-driven local backup for history, templates, and preferences.
- Android Storage Access Framework document creation/selection; no broad storage permission.
- Visible busy/progress state while backup data is being read, written, or restored.
- Duplicate backup actions are disabled while a backup operation is active.
- Versioned, bounded backup format with URL-safe Base64 text fields.
- SHA-256 accidental-corruption detection.
- Strict schema, record, identifier, timestamp, currency, split, checksum, decimal, saved-name, and Unicode validation.
- Restore confirmation before replacing current data.
- Valid decoded history labels/template names are restored without silent trimming or rewriting.
- Transactional Room replacement plus compensating rollback for the separate DataStore preference write when a multi-store restore fails.

See [`docs/backup-restore.md`](docs/backup-restore.md) and [`docs/security-backup.md`](docs/security-backup.md).

### Export

- Plain-text receipt sharing.
- CSV export with quote escaping and spreadsheet-formula neutralization for text cells.
- Offline PDF receipt creation using Android `PdfDocument`.
- Cache-file sharing through a non-exported Android `FileProvider` with temporary read permission.
- Canonical-path containment prevents sharing files outside the private export cache directory.
- Backup/CSV/PDF file I/O runs off the main thread.

### UI, branding, and accessibility

- Jetpack Compose + Material 3.
- Responsive phone/tablet layout.
- Light, dark, and system theme modes.
- Large-text preference.
- Reduced-motion preference that removes navigation transitions when enabled.
- Repository-owned vector icons for primary navigation with visible text labels.
- Branded AndroidX launch splash treatment using the SpendCalc icon.
- Externalized user-facing strings for localization readiness.
- Clear validation text in addition to color/state styling.
- First-run onboarding.
- About screen with version, license, support, repository, funding, and credit.

### Privacy

- Core use requires no account.
- Core calculation, history, templates, backup encoding, and receipt generation require no network.
- Current manifest does not request Android Internet permission.
- History/templates/preferences live in app-local storage.
- No analytics or advertising SDK is required by the current implementation.
- Android system-managed backup/device transfer is documented separately from user-created backup files.

See [`PRIVACY.md`](PRIVACY.md) and [`SECURITY.md`](SECURITY.md).

## Supported platform

| Platform | Status |
| --- | --- |
| Android API 26+ | Primary supported target |
| Android phone | Supported |
| Android tablet / wide screen | Responsive layout supported |
| iOS / desktop / web | Not part of this repository |

## Tech stack

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX SplashScreen
- AndroidX Navigation Compose
- AndroidX Lifecycle/ViewModel
- Room + KSP
- Preferences DataStore
- Kotlin coroutines/Flow
- Android `PdfDocument`
- JUnit + Android/Compose UI tests
- GitHub Actions + CodeQL + Dependabot

## Architecture

SpendCalc follows a small layered modular-monolith approach inside one Android module:

```text
Compose UI
   ↓
SpendCalcViewModel
   ↓
Domain calculation + repositories
   ↓
Room / DataStore

Platform adapters: document picker, FileProvider, PDF, share intents, external links
```

The finance domain layer does not depend on Compose, Room, Activity, or Android resources. Backup orchestration coordinates Room and DataStore through explicit repositories rather than bypassing persistence boundaries.

Full details: [`docs/architecture.md`](docs/architecture.md)

Architecture decisions:

- [`ADR 0001 — BigDecimal finance arithmetic`](docs/adr/0001-use-bigdecimal-for-finance.md)
- [`ADR 0002 — Local-first core`](docs/adr/0002-local-first-core.md)
- [`ADR 0003 — Room and DataStore`](docs/adr/0003-room-and-datastore.md)
- [`ADR 0004 — Versioned bounded backup format`](docs/adr/0004-versioned-local-backup.md)

## Calculation rule

The calculation order is deterministic:

1. Sum item amounts.
2. Calculate discount from subtotal.
3. Subtract discount.
4. Calculate tax, tip, and service charge from the discounted base.
5. Sum the discounted base and charges.
6. Apply the manual exchange rate.
7. Round monetary outputs using the configured policy.
8. Divide by split count.

Any behavior change to this order should include exact regression tests and changelog notes.

## Quick start

### Requirements

- Git
- JDK 17
- Android SDK Platform 35
- Gradle 8.9 if a local Gradle installation is used

Clone:

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
```

Build the debug APK:

```bash
gradle assembleDebug
```

For Android Studio, open the repository, use JDK 17 for Gradle, allow sync to complete, select an API 26+ device/emulator, and run the `app` configuration.

Detailed platform-specific setup: [`docs/setup.md`](docs/setup.md)

## Development checks

```bash
gradle testDebugUnitTest
gradle assembleDebugAndroidTest
gradle lint
gradle assembleDebug
gradle assembleRelease
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

With an emulator/device:

```bash
gradle connectedDebugAndroidTest
```

See [`docs/development.md`](docs/development.md).

## Testing

The repository includes:

- finance arithmetic, rounding, bounds, and validation unit tests;
- deterministic seeded finance fuzz/regression coverage;
- history and template repository tests covering new-input normalization, exact valid restore behavior, and over-limit rejection;
- UTF-16-safe saved-name policy tests, including emoji-boundary and malformed-surrogate cases;
- backup round-trip coverage for Unicode saved names at the saved-name boundary;
- backup corruption, schema, structural-bound, and semantic-validation tests;
- deterministic seeded backup serialization/corruption fuzz coverage;
- CSV security/escaping and receipt formatter tests;
- export path-containment and structured-log redaction tests;
- Room history/template/backup integration tests;
- Compose calculator, named-history-save/Unicode-boundary dialog, History label-filter, and Settings busy-state tests;
- a real-activity calculate/named-save/history journey test;
- fast repository guards for formatting, namespace, string resources, Android local-first security, links/required files, and common secret patterns.

CI compiles the instrumentation suite; final release verification still requires executing it on a connected emulator/device.

Testing strategy: [`docs/testing.md`](docs/testing.md)

## Build and release

Debug build:

```bash
gradle assembleDebug
```

Release compilation:

```bash
gradle assembleRelease
```

Production signing material is **not** committed to the repository. Tagged release-candidate workflow runs produce an unsigned release artifact after repository/test/lint/release-build verification. Signing and store distribution use protected credentials outside source control.

A configured or queued workflow is not treated as a successful release check. The exact commit being released must satisfy [`docs/verification.md`](docs/verification.md).

Release guide: [`docs/release.md`](docs/release.md)

## CI and repository automation

- `CI`: format, Kotlin namespace, Android string-resource audit, Android local-first security policy, repository/link audit, secret scan, JVM tests, instrumentation-test compilation, full Android lint, debug build, release build.
- `CodeQL`: Java/Kotlin static analysis.
- `Dependency Review`: pull-request dependency change review.
- `Repository Audit`: required-file/local-link audit plus Android string-resource reference/duplicate-name guard.
- `Dependabot`: weekly Gradle and GitHub Actions updates.
- `Release Candidate`: tag-triggered unsigned release build.
- Superseded PR workflow runs use concurrency cancellation so the newest revision receives runner priority.

Repository workflow files live under [`.github/workflows/`](.github/workflows/).

## Security

SpendCalc minimizes permissions and remote dependencies. The export provider is non-exported, path-restricted, and grants temporary read access only during a user-selected share action. CSV text cells are escaped and common formula-leading characters are neutralized. Explicit backup parsing is bounded and fail-closed for unsupported/malformed records, including malformed Unicode saved text.

Do not report exploitable vulnerability details in a public issue. Follow [`SECURITY.md`](SECURITY.md).

## Privacy and data

History/templates use Room while preferences use DataStore. Users can name saved calculations, search/clear history, undo an individual history deletion, delete/undo templates, and configure history expiry. Explicit backup is user-selected and local; Android system backup/device transfer may separately include the documented private database/preferences according to OS/device settings.

Read [`PRIVACY.md`](PRIVACY.md) and [`docs/privacy-backup.md`](docs/privacy-backup.md).

## Accessibility

Release checks include TalkBack traversal, large system font scale, light/dark/system themes, app large-text behavior, reduced-motion behavior, navigation-label semantics, named-history dialog semantics, backup-progress messaging, touch-target review, small/wide screen behavior, and non-color-only validation.

Read [`docs/accessibility.md`](docs/accessibility.md).

## Performance

The app avoids network initialization, keeps ordinary calculation work in memory, caps the editable calculator at 100 line items, bounds history search and user/backup inputs, and moves document/PDF/CSV file I/O off the main thread. Optimization should remain evidence-driven rather than replacing correct decimal math with unsafe primitives.

Read [`docs/performance.md`](docs/performance.md).

## Troubleshooting

JDK, Android SDK, KSP/Room, emulator, export, and release troubleshooting is documented in [`docs/troubleshooting.md`](docs/troubleshooting.md).

## Contributing

Contributions are welcome. Start with [`CONTRIBUTING.md`](CONTRIBUTING.md) and the pull-request checklist.

For local commits, the requested project commit email is:

```bash
git config user.email "sanskarin@outlook.in"
```

## Roadmap and changes

- [`ROADMAP.md`](ROADMAP.md)
- [`CHANGELOG.md`](CHANGELOG.md)
- [`what_changed.md`](what_changed.md) — canonical multi-session engineering handoff/current verification state

## Support and contact

- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`
- GitHub: https://github.com/sanskarIN
- Repository: https://github.com/sanskarIN/spendcalc

Funding is optional and never required to use SpendCalc:

[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

## License

SpendCalc is open source under the [`MIT License`](LICENSE).

**Made by the Sanskar**