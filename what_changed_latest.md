# SpendCalc — Latest Work Continuity

> Current engineering handoff supplement for the release-candidate branch. This file records work completed after the earlier root `what_changed.md` checkpoint and is intended to prevent any loss of context while final verification is completed.

## Current milestone

- Date: 2026-08-19
- Repository: `sanskarIN/spendcalc`
- Working branch: `verify/release-candidate`
- Pull request: `#1` — `chore: verify SpendCalc release candidate`
- Target release: `1.0.0`
- Platform: Android API 26+, Kotlin + Jetpack Compose
- Architecture: local-first modular monolith with domain/data/UI/platform separation
- Source license: MIT
- Required product credit: `Made by the Sanskar`
- Requested Git commit email observed in repository commit metadata: `sanskarin@outlook.in`

## Critical release-candidate corrections

### Kotlin namespace compile blocker

The Android namespace/application ID is `in.sanskar.spendcalc`. In Kotlin source, `in` is a reserved keyword. Initial source used declarations such as `package in.sanskar...`, which are invalid Kotlin syntax.

All production, JVM-test, and Android-test Kotlin package/import declarations were corrected to escaped identifiers, for example:

```kotlin
package `in`.sanskar.spendcalc.domain
import `in`.sanskar.spendcalc.domain.model.CalculationInput
```

Android `namespace` and `applicationId` string values remain unchanged.

A regression guard was added:

- `scripts/check_kotlin_namespace.py`
- CI executes the guard before compilation.

### Other compile/reliability corrections

- Corrected Settings composable content receiver to `ColumnScope`.
- Corrected nullable PDF-page finalization.
- Adapted the optional history label method to the no-argument UI callback.
- Added the Android test-core dependency required by instrumentation tests.
- Replaced unsupported repository mapping callable references with explicit lambdas.
- Changed the Compose receipt smoke assertion so it is not dependent on the initial viewport.
- Added system-bar icon appearance handling for readable light/dark themes.
- Externalized the About screen app name.
- Made currency normalization deterministic with `Locale.ROOT` and added a Turkish-locale regression test.
- Updated Android backup/device-transfer rules so DataStore preferences are included alongside Room data.

## Product completeness additions

### History search

The History screen now includes local search/filtering by:

- history label;
- source currency code;
- converted currency code;
- source total;
- converted total.

The feature includes a dedicated no-match empty state and does not require a network connection.

Changed/added files include:

- `app/src/main/java/in/sanskar/spendcalc/ui/screens/HistoryScreen.kt`
- `app/src/main/res/values/strings_history.xml`

### Explicit local backup and restore

A versioned offline backup/restore path was added for history, templates, and preferences.

Implemented components:

- `SpendCalcBackup` domain model.
- `BackupCodec` with bounded parsing, URL-safe Base64 text-field encoding, schema versioning, and SHA-256 accidental-corruption detection.
- `BackupDecodeResult` and typed decode errors.
- Transactional Room `replaceAll` DAO operations for history and templates.
- Repository reverse mappings and replace-all APIs.
- `SettingsRepository.replace` for one-edit preference restoration.
- `BackupRepository` coordinating Room transaction + DataStore preference restoration.
- `BackupFileIo` with bounded UTF-8 document reads/writes through Android `ContentResolver`.
- Android document-create/document-open activity-result flows.
- Restore confirmation before replacing saved local data.
- Success/failure feedback.
- Settings UI backup/restore controls.
- Dedicated backup/restore documentation.

Important design behavior:

- No cloud service, account, remote API, or network permission is required.
- Backup export occurs only after an explicit user action and destination choice.
- The checksum detects accidental corruption but is not encryption or an authenticity signature.
- History/template replacement is atomic inside Room.
- DataStore is a separate storage engine, so Room + DataStore cannot share one cross-engine transaction; a preference-write failure after the Room transaction is surfaced as restore failure and can be retried from the original backup.

Relevant files:

- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedModels.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/export/BackupCodec.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/BackupRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/HistoryRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/TemplateRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/SettingsRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/local/HistoryDao.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/local/TemplateDao.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/BackupFileIo.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcViewModel.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcApp.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/SettingsScreen.kt`
- `app/src/main/res/values/strings_settings.xml`
- `docs/backup-restore.md`
- `docs/features.md`

## Test additions

Release-candidate work added or strengthened:

- locale-independent currency normalization regression coverage;
- CSV escaping and spreadsheet-formula neutralization coverage;
- text receipt export coverage;
- history repository mapping/retention/clear coverage;
- history backup replace-all coverage;
- template repository save/delete/replace-all coverage;
- backup codec Unicode/tab/newline round-trip coverage;
- backup checksum tampering coverage;
- unsupported backup schema coverage;
- Room history/template round-trip integration coverage;
- Room transactional replace-all backup integration coverage;
- Compose calculator/receipt smoke coverage.

New/changed test files include:

- `app/src/test/java/in/sanskar/spendcalc/domain/CalculatorEngineTest.kt`
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecTest.kt`
- `app/src/test/java/in/sanskar/spendcalc/domain/export/CsvExportFormatterTest.kt`
- `app/src/test/java/in/sanskar/spendcalc/domain/export/ReceiptTextFormatterTest.kt`
- `app/src/test/java/in/sanskar/spendcalc/data/HistoryRepositoryTest.kt`
- `app/src/test/java/in/sanskar/spendcalc/data/HistoryRestoreRepositoryTest.kt`
- `app/src/test/java/in/sanskar/spendcalc/data/TemplateRepositoryTest.kt`
- `app/src/androidTest/java/in/sanskar/spendcalc/data/local/SpendCalcDatabaseTest.kt`
- `app/src/androidTest/java/in/sanskar/spendcalc/data/local/BackupRestoreDatabaseTest.kt`
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/CalculatorScreenTest.kt`

## GitHub automation improvements

Current workflow generations use maintained action majors verified against the official action repositories:

- `actions/checkout@v5`
- `actions/setup-java@v5`
- `gradle/actions/setup-gradle@v5`
- `github/codeql-action/*@v4`
- `actions/upload-artifact@v4`
- `actions/dependency-review-action@v4`

CI now checks:

1. repository formatting;
2. Kotlin namespace declarations;
3. common secret patterns;
4. JVM unit tests;
5. Android lint;
6. debug compilation;
7. release compilation.

Separate workflows cover CodeQL, dependency review, and tag-triggered release-candidate artifacts.

## Verification performed

### Source/static verification

- Repository was inspected through the connected GitHub integration.
- Namespace declarations were audited and corrected across known Kotlin source/test files.
- A CI namespace regression script was added.
- Common TODO search was performed during the release audit; no intentional core-feature TODO placeholder is being used as an implementation substitute.
- Backup parser size/count/field limits, checksum path, schema handling, and template validation were reviewed during implementation.

### Local pure-Kotlin compiler smoke verification

The available Kotlin compiler was used against the pure domain/export subset outside Android framework dependencies.

Smoke verification covered:

- `BigDecimal` calculation compilation/execution;
- expected deterministic total/split/conversion values;
- locale-stable currency normalization;
- CSV formula-prefix defense;
- backup codec compilation;
- backup encode/decode round trip;
- backup checksum tamper rejection.

### Android/Gradle verification state

GitHub Actions verification was triggered through pull request `#1`. During the working session, GitHub-hosted workflow runs remained queued rather than producing completed job results. Repeated source fixes therefore continued without falsely claiming those queued runs were green.

A direct clean clone/static-check attempt was also made from the execution environment. Full Android Gradle verification remains dependent on a usable Android/Gradle runner. The repository CI explicitly installs JDK 17, Gradle 8.9, Android SDK 35, and Build Tools 35.0.0 before running the quality suite.

Do not mark the release tag as verified until the configured GitHub Actions/Android build suite completes successfully.

## Known external/manual release items

These are intentionally not committed as fake or secret artifacts:

- production signing keystore/passwords;
- real release screenshots captured from a verified Android build;
- store-distribution credentials;
- a v1-to-v2 Room migration test (database schema is still version 1, so no prior schema exists to migrate yet).

The repository intentionally does not contain production signing secrets.

## Current next exact release actions

1. Inspect the latest PR-head CI, CodeQL, and dependency-review runs when GitHub-hosted jobs leave the queue.
2. If any job fails, fetch the exact job log, add a regression test where appropriate, fix the defect, and rerun the failed job/workflow.
3. Once automated checks are green, merge PR `#1` without discarding the meaningful atomic commit history.
4. Reconcile root `what_changed.md`, `CHANGELOG.md`, `ROADMAP.md`, and README with the final merged release-candidate state.
5. Capture real screenshots from the verified build using fictional data only.
6. Perform manual TalkBack/large-font/small-phone/tablet/export/backup-restore verification.
7. Tag `v1.0.0` only after the release-candidate checklist is complete.

## Release note draft

### SpendCalc 1.0.0 release candidate

- Precision-safe itemized expense calculations with discount, tax, tip, service charge, split bill, and manual currency conversion.
- Offline Room history with local search and configurable retention.
- Reusable calculation templates.
- Plain-text, CSV, and PDF export paths.
- Explicit versioned local backup/restore for history, templates, and preferences.
- Responsive Compose UI, onboarding, system/light/dark themes, large text, reduced-motion preference, Settings, and About.
- Local-first privacy design with no account or required Internet permission for core functionality.
- Layered automated tests, repository checks, CodeQL, dependency review, Dependabot, issue/PR templates, release workflow, and extensive documentation.

## Continuity rule

On continuation, inspect this file together with root `what_changed.md`, PR `#1`, the latest branch head, workflow runs, and any open review/issues before creating additional work. Do not rewrite completed functionality merely to increase commit count.
