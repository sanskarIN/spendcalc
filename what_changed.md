# SpendCalc — Work Continuity

## Current milestone

- Date: 2026-08-20
- Repository: `sanskarIN/spendcalc`
- Default branch: `main`
- Runtime target: Android API 26+, Kotlin, Jetpack Compose, offline-first.
- Current implementation state: core product, tests, repository automation, security/privacy docs, release docs, and deep Android APK/AAB command documentation are substantially implemented; final CI/device/release verification remains an execution step.

## Source prompt analyzed

The repository is implemented against `15_spendcalc_master_prompt.md`. The required product is an offline expense calculator with precision-safe decimal arithmetic, itemized expenses, tax/discount/tip/service-charge/split calculations, manual currency conversion, reusable templates, receipt-style results, history with optional auto-delete, export architecture, accessibility, tests, polished settings/about UI, security/privacy documentation, CI, and release engineering.

## Completed work

### Repository/build foundation

- Added Gradle settings/root build configuration and Android app module.
- Configured Android API 26 minimum, API 35 target/compile SDK, Java 17 bytecode, Kotlin, Jetpack Compose, KSP, Room, DataStore, and Android test dependencies.
- Root build currently uses Android Gradle Plugin `8.7.3`, Kotlin `2.0.21`, and KSP `2.0.21-1.0.28`.
- Local/CI documentation standardizes on Gradle `8.9` while the repository does not commit a Gradle wrapper JAR.
- Added manifest, launch theme, vector icon, backup/device-transfer policy, and private `FileProvider` export paths.
- Added `.gitignore`, `.editorconfig`, `.gitattributes`, `.env.example`, and MIT license.

### Domain and finance engine

- Added `ExpenseItem`, `CalculationInput`, `CalculationResult`, typed calculation errors/outcomes, and explicit rounding policy.
- Implemented `CalculatorEngine` using `BigDecimal` only for finance arithmetic.
- Defined charge order: subtotal -> discount -> discounted base -> tax/tip/service -> total -> conversion -> split.
- Added currency-code, percentage, exchange-rate, split-count, and non-negative amount validation.
- Added reusable persisted domain models for history, templates, theme, accessibility, and retention.

### Persistence and settings

- Added Room entities/DAOs/database for calculation history and templates.
- Added history repository with save/delete/clear/age purge.
- Added template repository with save/load/delete mapping.
- Added DataStore settings repository for system/light/dark theme, large text, reduced motion, history retention, and onboarding completion.
- Added explicit application dependency wiring through `AppContainer` and `SpendCalcApplication`.

### UI/UX

- Added design tokens and custom light/dark Compose themes.
- Added first-run onboarding.
- Added responsive calculator form with itemized expenses and validation.
- Added receipt-style result card.
- Added history screen with clear confirmation and per-entry deletion.
- Added templates screen with load/delete actions.
- Added settings screen for appearance, accessibility, privacy/retention, repository updates, and About navigation.
- Added About screen with version, MIT license, GitHub, repository, Buy Me a Coffee, business/support contacts, and `Made by the Sanskar`.
- Added app navigation shell and snackbar feedback.
- Externalized user-facing strings into Android resources.

### Export

- Added platform-independent `ExportFormatter` abstraction.
- Added CSV export with proper quoting and common spreadsheet-formula-prefix neutralization for text cells.
- Added plain-text receipt export.
- Added offline PDF receipt generation using Android `PdfDocument`.
- Added secure app-cache file sharing through non-exported `FileProvider` URIs.
- Added safe URL and email intent helpers.

### Tests

- Added finance-engine tests covering totals, charge order, conversion, split rounding, decimal precision, currency normalization, invalid amounts, invalid exchange rates/splits, and invalid currency codes.
- Added CSV export tests for quote escaping and formula injection defense.
- Added receipt text export test.
- Added history repository tests for persistence mapping, retention purge, and clear.
- Added template repository tests.
- Added Room Android integration tests for history/template round trips.
- Added Compose calculator smoke test.

### CI/security/release automation

- Added no-dependency repository formatting guard.
- Added conservative common-secret-pattern scanner.
- Added GitHub Actions CI for format check, secret-pattern check, unit tests, Android lint, debug build, and release compilation.
- Added CodeQL Java/Kotlin analysis workflow.
- Added pull-request dependency review workflow.
- Added tag-triggered release-candidate build workflow.
- Added Dependabot for Gradle and GitHub Actions dependencies.
- Added issue forms, support/security issue configuration, pull-request checklist, and funding configuration.

### General documentation

- Added comprehensive `README.md` with logo, feature overview, platform support, tech stack, setup, testing, release, architecture, security/privacy, accessibility, performance, contribution, support, BMC, MIT license, and `Made by the Sanskar`.
- Added `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SECURITY.md`, `SUPPORT.md`, `PRIVACY.md`, `CHANGELOG.md`, and `ROADMAP.md`.
- Added architecture, setup, development, testing, release, troubleshooting, accessibility, and performance documentation.
- Added ADRs for BigDecimal finance arithmetic, local-first core behavior, and Room/DataStore persistence.
- Added editable SVG brand artwork and a verified-screenshot capture policy.

### Deep Android build/executable documentation — 2026-08-20

A complete Android build documentation pass was added so contributors can understand not only which commands to run, but what they mean and what files they produce.

New documents:

- `docs/README.md` — central documentation index and learning/build/release paths.
- `docs/android-build-guide.md` — deep source-to-APK/AAB guide.
- `docs/command-reference.md` — command dictionary for Git, Java, Gradle, ADB, Android SDK packaging/signing tools, and repository scripts.

Expanded documents:

- `docs/setup.md` — full environment setup for Windows/macOS/Linux, JDK 17, Android SDK 35, Gradle 8.9, ADB, first build/install, release artifacts, and quality commands.
- `docs/release.md` — APK/AAB distinctions, versioning, release gates, manual signing, signature verification, checksums, fresh-install/upgrade tests, accessibility/privacy/security verification, and rollback.
- `docs/troubleshooting.md` — detailed Gradle/JDK/SDK/dependency/ADB/APK/AAB/signing/Room/KSP/test/release diagnosis.

The Android executable guide now documents:

- what APK and AAB mean;
- debug versus release artifacts;
- current package/application ID and Android SDK levels;
- exact `app/build.gradle.kts` configuration meanings;
- Git/JDK/Android Studio/SDK/Gradle prerequisites;
- Windows/macOS/Linux SDK paths;
- Gradle task syntax and diagnostic flags;
- `gradle assembleDebug` and expected debug APK path;
- `gradle assembleRelease` and release APK directory;
- `gradle bundleRelease` and AAB directory;
- grouped clean/test/lint/build commands;
- `gradle installDebug`;
- `adb devices`, `adb install`, `adb install -r`, `adb uninstall`, package inspection, Logcat, and selected-device syntax;
- connected instrumentation tests;
- `keytool` keystore creation and option meanings;
- `zipalign` APK alignment and verification;
- `apksigner` APK signing and certificate verification;
- `jarsigner` AAB signing/verification;
- release artifact security rules;
- versionCode/versionName rules;
- dependency reports and Gradle diagnostic flags;
- offline Gradle build behavior;
- artifact locations;
- common installation/signature failure causes;
- recommended development and release command sequences.

No production signing secret was added to source control. Documentation explicitly instructs maintainers to keep keystores and passwords outside Git.

## Important files/modules

- `app/src/main/java/in/sanskar/spendcalc/domain/CalculatorEngine.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/model/ExpenseModels.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/HistoryRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/TemplateRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/SettingsRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/local/SpendCalcDatabase.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcViewModel.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcApp.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/CalculatorScreen.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/PdfReceiptExporter.kt`
- `.github/workflows/ci.yml`
- `.github/workflows/codeql.yml`
- `.github/workflows/dependency-review.yml`
- `.github/workflows/release.yml`
- `README.md`
- `docs/README.md`
- `docs/android-build-guide.md`
- `docs/command-reference.md`
- `docs/setup.md`
- `docs/release.md`
- `docs/troubleshooting.md`

## Android executable quick reference

Build debug APK:

```bash
gradle assembleDebug
```

Expected file:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Build release APK:

```bash
gradle assembleRelease
```

Release output directory:

```text
app/build/outputs/apk/release/
```

Build release AAB:

```bash
gradle bundleRelease
```

AAB output directory:

```text
app/build/outputs/bundle/release/
```

Install debug build on a connected device/emulator:

```bash
gradle installDebug
```

or:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Full details: `docs/android-build-guide.md`.

## Commands/checks run and results

- Confirmed repository exists, is public, default branch is `main`, and connected GitHub account has admin/push access.
- Confirmed `app/build.gradle.kts`: application ID `in.sanskar.spendcalc`, `minSdk 26`, `targetSdk/compileSdk 35`, version `1.0.0`/code `1`, Java/Kotlin JVM target 17, release minification/resource shrinking.
- Confirmed root `build.gradle.kts`: Android Gradle Plugin `8.7.3`, Kotlin `2.0.21`, KSP `2.0.21-1.0.28`.
- Confirmed settings contain the single `:app` module and Google/Maven Central/plugin portal repository configuration.
- Confirmed no committed `gradle/wrapper/gradle-wrapper.properties` at the checked path; documentation therefore continues to use local Gradle 8.9 rather than pretending wrapper commands currently exist.
- Direct local `git clone` from the earlier execution container was attempted but failed because that container could not resolve `github.com`; this was an execution-environment network limitation, not a repository result.
- Source-level compile-risk review previously identified/fixed UI/PDF/callback/test dependency issues.
- This documentation session changed documentation through the connected GitHub API; it did not execute an Android SDK build locally.

## Documentation commits from the 2026-08-20 build-guide pass

- `e1f5440` — `docs: add complete Android build and executable guide`
- `c014f32` — `docs: add complete command reference`
- `7d6c878` — `docs: add documentation index and learning path`
- `f0d68aa` — `docs: expand complete development environment setup`
- `5398e26` — `docs: expand release packaging and signing workflow`
- `37d8a97` — `docs: expand Android build and install troubleshooting`

## Known limitations / not-yet-verified items

- The connected GitHub file/commit API does not expose a commit-author-email argument. Commits use the authenticated GitHub identity. Documentation records `git config user.email "sanskarin@outlook.in"` for local contributions.
- A Gradle wrapper JAR is not committed; local docs use Gradle 8.9, while GitHub Actions can explicitly install/configure Gradle.
- Real Android screenshots are intentionally not fabricated. `docs/assets/screenshots/README.md` defines the release screenshot capture checklist.
- No production signing keys are committed. Release production signing remains intentionally external to source control.
- Database schema version is 1, so there is no historical production migration path to test yet.
- Final clean Android build/test/lint/device verification still needs to be executed in an Android SDK environment/CI and recorded when available.

## Open verification work

1. Run/inspect CI for current `main` or a dedicated verification PR.
2. Fix any reproducible compile/lint/test errors discovered by actual Gradle/Android tooling.
3. Run connected Android instrumentation tests on a real device/emulator.
4. Generate actual debug APK and release APK/AAB artifacts from a verified commit.
5. Securely sign a production candidate outside Git and verify its certificate.
6. Perform clean-install and upgrade testing when a previous public release exists.
7. Capture real release screenshots only from a verified build with fictional data.
8. Record final verification commit/run/artifact identities here.

## Migration notes

- Room database version: 1.
- No production migration path exists yet because this is the initial schema.
- Destructive migration fallback is intentionally not enabled.

## Release notes draft

### Unreleased / 1.0.0 release candidate

- Precision-safe itemized expense calculator with discount, tax, tip, service charge, split bill, and manual exchange-rate conversion.
- Offline Room history and saved calculation templates.
- Configurable local history retention.
- Text, CSV, and PDF receipt export/sharing.
- Light/dark/system themes, large-text option, reduced-motion setting, onboarding, and responsive phone/tablet UI.
- Local-first privacy model with no required account or Internet permission for core functionality.
- Comprehensive repository documentation, testing baseline, CI, CodeQL, dependency review, Dependabot, issue templates, release workflow, and deep APK/AAB build/sign/install command documentation.

**Made by the Sanskar**
