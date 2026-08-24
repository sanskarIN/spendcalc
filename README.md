<p align="center">
  <img src="docs/assets/spendcalc-logo.svg" alt="SpendCalc logo" width="128" height="128" />
</p>

<h1 align="center">SpendCalc</h1>

<p align="center"><strong>A precision-safe, private, offline-first expense calculator for Android.</strong></p>

<p align="center">
  <a href="https://github.com/sanskarIN/spendcalc/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/sanskarIN/spendcalc/actions/workflows/ci.yml/badge.svg" /></a>
  <a href="https://github.com/sanskarIN/spendcalc/actions/workflows/codeql.yml"><img alt="CodeQL" src="https://github.com/sanskarIN/spendcalc/actions/workflows/codeql.yml/badge.svg" /></a>
  <img alt="Android API 26+" src="https://img.shields.io/badge/Android-API%2026%2B-3DDC84?logo=android&logoColor=white" />
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?logo=kotlin" />
  <a href="LICENSE"><img alt="MIT License" src="https://img.shields.io/badge/License-MIT-blue.svg" /></a>
</p>

<p align="center">
  <a href="https://buymeacoffee.com/sanskarIN"><img alt="Buy Me a Coffee" src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000" /></a>
</p>

> **Made by the Sanskar**

SpendCalc is an Android-first expense calculator designed for real finance workflows rather than demo arithmetic. It uses `BigDecimal` for monetary calculations, keeps core behavior local/offline, stores history/templates/preferences on-device, supports explicit backup/restore, and can export receipts as text, CSV, and PDF.

> **Current release candidate:** `2.15.4` (`versionCode 21504`). Room database version and explicit backup schema version remain `1` because application releases and persistence compatibility versions are intentionally independent.

> **Release status:** 2.15.4 is a release candidate, not a verified public release until every exact-head automated and manual gate in [`docs/verification.md`](docs/verification.md) is complete.

## Documentation entry points

- [Documentation index](docs/README.md)
- [Android APK/AAB/build/install/signing guide](docs/android-build-guide.md)
- [Complete command reference](docs/command-reference.md)
- [Architecture](docs/architecture.md)
- [Features](docs/features.md)
- [Testing](docs/testing.md)
- [Accessibility](docs/accessibility.md)
- [Backup/restore](docs/backup-restore.md)
- [Persistence invariants](docs/persistence-invariants.md)
- [Release guide](docs/release.md)
- [Blocking release verification checklist](docs/verification.md)
- [Troubleshooting](docs/troubleshooting.md)
- [Tracked-file codebase reference](docs/codebase-reference.md)
- [Documentation source-of-truth map](docs/documentation-map.md)

## Platform support

| Platform | Status |
| --- | --- |
| Android API 26+ | Primary supported target |
| Android phone | Supported |
| Android tablet / wide layout | Supported/responsive |
| iOS | Not part of 2.15.4 |
| Desktop | Not part of 2.15.4 |
| Web/browser extension | Future work; deliberately separate from Android 2.15.4 stabilization |

## Current release metadata

```text
Application ID: in.sanskar.spendcalc
versionName: 2.15.4
versionCode: 21504
minSdk: 26
targetSdk: 35
compileSdk: 35
Room schema: 1
Explicit backup schema: 1
Java/JVM target: 17
```

Application version bumps do not automatically change Room or backup schemas.

## Features

### Precision-safe expense calculation

- Itemized expense lines.
- Up to 100 editable items per calculation.
- Discount percentage.
- Tax percentage.
- Tip percentage.
- Service-charge percentage.
- Split bill from 1 through the documented bounded maximum.
- Manual currency conversion using three-letter currency codes.
- `BigDecimal` finance arithmetic.
- Centralized validation/rounding policy.
- Receipt-style results.
- Bounded numeric precision/scale/input lengths to prevent pathological expansion.

### Deterministic calculation order

1. Sum items.
2. Calculate/subtract discount.
3. Calculate tax/tip/service charge from the discounted base.
4. Sum the discounted base and charges.
5. Apply manual exchange rate.
6. Round using the project policy.
7. Divide by split count.

Any change to this order should include exact regression tests and changelog/release documentation.

### History

- Room-backed local calculation history.
- Optional user-provided labels.
- Safe default label when left blank.
- Saved-name length boundary and Unicode-safe truncation.
- Search by meaningful stored data.
- Bounded search query.
- Individual delete.
- Snackbar Undo.
- Clear-all confirmation.
- Optional 30/90-day retention behavior.

### Templates

- Save reusable calculation templates.
- Load template settings back into the calculator.
- Delete template.
- Undo template deletion.
- Shared saved-name/Unicode-boundary behavior.
- Repository-level finance validation, even if a caller bypasses the ViewModel.

### Persistence integrity

Repositories are validation boundaries, not passive DAO wrappers.

Shared persisted-record rules cover:

- IDs;
- timestamps;
- canonical uppercase currency codes;
- saved names;
- split/result bounds;
- duplicate IDs in replacement collections;
- template finance settings.

Batch replacement validates the full candidate set before destructive replacement.

See [`docs/persistence-invariants.md`](docs/persistence-invariants.md).

### Explicit local backup/restore

- Android Storage Access Framework document creation/selection.
- No broad storage permission.
- Versioned bounded backup format.
- URL-safe Base64 text fields.
- SHA-256 accidental-corruption detection.
- Strict schema/record/ID/timestamp/currency/split/decimal/name/duplicate validation.
- Strict malformed/unmappable UTF-8 rejection.
- Canonical persisted-currency validation without silent repair.
- Confirmation before replacing local data.
- Visible busy/progress state.
- Duplicate backup actions disabled while work is active.
- Room replacement plus compensating preference rollback across multi-store restore failures.

The backup checksum is not a digital signature/MAC/authorship proof.

See:

- [`docs/backup-restore.md`](docs/backup-restore.md)
- [`docs/security-backup.md`](docs/security-backup.md)
- [`docs/privacy-backup.md`](docs/privacy-backup.md)

### Export/share

- Plain-text receipt sharing.
- CSV export with quoting and spreadsheet-formula neutralization for text cells.
- Offline PDF receipt generation with Android `PdfDocument`.
- Unicode-safe PDF truncation.
- Cache-file sharing through a non-exported `FileProvider`.
- Canonical-path containment preventing export outside the intended private cache path.

### UI/accessibility

- Jetpack Compose + Material 3.
- Responsive phone/tablet layout.
- Light, dark, system themes.
- App large-text preference.
- Reduced-motion preference.
- Branded AndroidX splash screen.
- Repository-owned navigation icons.
- Visible navigation labels.
- User-facing strings in Android resources.
- Validation messages that do not rely only on color.
- First-run onboarding.
- About screen with version/license/support/repository/funding/credit.

### Privacy/local-first design

- No account required for core use.
- No remote API key required for core use.
- No analytics/advertising SDK required by the current implementation.
- Core calculation/history/templates/backup encoding/receipt generation are local.
- Current manifest has no Android `INTERNET` permission.
- History/templates/preferences live in app-local storage.

See [`PRIVACY.md`](PRIVACY.md) and [`SECURITY.md`](SECURITY.md).

## Architecture

SpendCalc is a small layered modular monolith inside one Android app module:

```text
Compose UI
   ↓
SpendCalcViewModel
   ↓
Domain calculation + repository boundaries
   ↓
Room / DataStore

Platform adapters:
Document picker / FileProvider / PDF / share intents / external links
```

The domain finance layer is kept independent from Compose/Room/Activity/Android resources.

Architecture decisions:

- [ADR 0001 — BigDecimal finance arithmetic](docs/adr/0001-use-bigdecimal-for-finance.md)
- [ADR 0002 — Local-first core](docs/adr/0002-local-first-core.md)
- [ADR 0003 — Room and DataStore](docs/adr/0003-room-and-datastore.md)
- [ADR 0004 — Versioned local backup](docs/adr/0004-versioned-local-backup.md)

## Tech stack

- Kotlin 2.0.21
- Jetpack Compose
- Material 3
- AndroidX SplashScreen
- AndroidX Navigation Compose
- AndroidX Lifecycle/ViewModel
- Room + KSP
- Preferences DataStore
- Kotlin Coroutines + Flow
- Android `PdfDocument`
- JUnit
- AndroidX Test / Espresso / Compose UI Test
- GitHub Actions
- CodeQL
- Dependabot

Major dependency upgrades are intentionally reviewed independently rather than automatically mixed into release stabilization.

## Requirements

- Git
- JDK 17
- Android SDK Platform 35
- Android Build-Tools/Platform-Tools
- Gradle 8.9 for the documented command-line workflow

The repository currently does not commit a Gradle wrapper JAR. CI pins Gradle through `gradle/actions/setup-gradle`.

## Clone

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
```

Optional local Git identity:

```bash
git config user.name "Sanskar"
git config user.email "sanskarin@outlook.in"
```

## Quick debug build

```bash
gradle --no-daemon assembleDebug
```

Expected APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install with ADB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Or:

```bash
gradle --no-daemon installDebug
```

See [`docs/android-build-guide.md`](docs/android-build-guide.md) for the complete build/sign/install flow.

## Repository guards

Run:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

These cover formatting, Kotlin namespaces, exhaustive tracked-file documentation, Android resources, local-first security policy, repository metadata/version/doc/link consistency, and common secret patterns.

## JVM/build checks

```bash
gradle --no-daemon clean testDebugUnitTest
gradle --no-daemon assembleDebugAndroidTest
gradle --no-daemon lint
gradle --no-daemon assembleDebug
gradle --no-daemon assembleRelease
gradle --no-daemon bundleRelease
```

## Connected Android tests

With a device/emulator:

```bash
gradle --no-daemon connectedDebugAndroidTest
```

The repository also includes an `Android Instrumentation` GitHub Actions workflow that runs the connected test suite on an API 35 emulator.

Release verification requires connected execution, not only instrumentation compilation.

## Test coverage areas

The repository includes coverage for:

- finance arithmetic/rounding/validation;
- deterministic finance fuzz/regression cases;
- history/template repositories;
- persistence envelopes and duplicate IDs;
- backup encode/decode/corruption/strict UTF-8 behavior;
- Unicode saved-name boundaries;
- CSV security/escaping;
- PDF Unicode truncation;
- path containment;
- safe logging/redaction;
- Room integration;
- Compose calculator/history/settings/dialog behavior;
- real-activity calculate → named save → History journey.

The activity journey intentionally asserts that at least one correct formatted amount exists because the UI may expose the same amount in multiple legitimate semantics nodes.

## Release build

```bash
gradle --no-daemon assembleRelease
gradle --no-daemon bundleRelease
```

Production signing material is not committed.

Current production APK example after external signing:

```text
SpendCalc-2.15.4-release.apk
```

Verify signature:

```bash
apksigner verify --verbose --print-certs SpendCalc-2.15.4-release.apk
```

Install exact signed candidate:

```bash
adb install SpendCalc-2.15.4-release.apk
```

Generate/record a SHA-256 checksum and exact source commit SHA before distribution.

See [`docs/release.md`](docs/release.md).

## Release gates

For the exact final 2.15.4 commit require successful:

- CI;
- CodeQL;
- Dependency Review;
- Repository Audit;
- Android Instrumentation.

Then complete the manual gates in [`docs/verification.md`](docs/verification.md), including:

- representative Android device/emulator checks;
- accessibility/font-scale/TalkBack/reduced-motion review;
- small/wide layout review;
- export/share/FileProvider checks;
- backup/restore/malformed-data checks;
- offline operation;
- privacy/security review;
- real screenshots with fictional data;
- production signing;
- signed artifact install/version/permission inspection;
- checksum/source-SHA recording.

Only after those gates are complete should `v2.15.4` be tagged/published.

## Screenshots

Real release screenshots are intentionally captured from verified builds rather than fabricated.

Use fictional data only and follow [`docs/assets/screenshots/README.md`](docs/assets/screenshots/README.md).

## Documentation invariants

Documentation is treated as maintained engineering state:

- `docs/README.md` is the task-oriented index.
- `docs/codebase-reference.md` documents every tracked file exactly once.
- `scripts/check_documentation_coverage.py` compares that inventory with `git ls-files`.
- `docs/documentation-map.md` assigns authoritative documents by topic.
- `scripts/check_repository.py` checks required files, local Markdown links, repository identity markers, and current application version alignment in release/build command documentation.

When adding/renaming/deleting tracked files, update the codebase reference in the same change.

## Security

Please report vulnerabilities according to [`SECURITY.md`](SECURITY.md).

Do not commit or paste into issues/PRs/logs:

- production keystores/private keys;
- passwords;
- API tokens;
- private user data;
- real financial records;
- machine-local secret configuration.

## Contribution

See:

- [`CONTRIBUTING.md`](CONTRIBUTING.md)
- [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md)
- [`docs/development.md`](docs/development.md)
- [`docs/testing.md`](docs/testing.md)
- [`docs/command-reference.md`](docs/command-reference.md)

## Support and project links

- GitHub profile: https://github.com/sanskarIN
- Repository: https://github.com/sanskarIN/spendcalc
- Buy Me a Coffee: https://buymeacoffee.com/sanskarIN
- Business: sanskarin@outlook.in
- Business: sanskarin.business@gmail.com
- Support: supportramsandesh@gmail.com

## License

SpendCalc is released under the **MIT License**. See [`LICENSE`](LICENSE).

**Made by the Sanskar**
