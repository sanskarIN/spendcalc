# SpendCalc — Work Continuity

## Current milestone

- Date: 2026-08-19
- Repository: `sanskarIN/spendcalc`
- Default branch: `main`
- Active branch: `complete/v1-finalization`
- Active pull request: `#12` — `fix: complete SpendCalc release implementation`
- Master specification: `15_spendcalc_master_prompt.md`
- Target: Android, Kotlin, Jetpack Compose, local/offline-first.
- Current state: implementation and source hardening are substantially complete. The exact final PR revision must still finish automated verification and the documented manual Android/accessibility/release gates before a production tag is justified.

## Why this branch exists

The prior handoff overstated completion of a few later-stage features. This continuation audited the actual source tree, found disconnected backup/search pieces, and repaired the missing repository, DAO, ViewModel, UI, test, and workflow integration instead of relying on documentation claims.

## Finance and validation work

- Kept finance arithmetic entirely on `BigDecimal` with explicit half-up money rounding.
- Preserved charge order: subtotal -> discount -> discounted base -> tax/tip/service charge -> total -> conversion -> split.
- Fixed discount validation so discounts cannot exceed 100% and create a negative taxable base.
- Kept bounded extended ranges for tax, tip, and service charge.
- Added supported decimal shape limits for item amounts and exchange rates.
- Rejects exponent forms that could expand into extremely large plain-decimal strings.
- Bounded split count to `1..1,000,000`.
- Added deterministic `Locale.ROOT` currency normalization.
- Bounded ViewModel item count, names, numeric text, split input, and currency-code input before expensive work.

## History, templates, and preferences

- Added transactional history/template replacement for restore flows.
- Added history restore support and Undo after individual deletion.
- Added offline history search/filter across labels, currencies, totals, converted totals, and per-person values.
- Preserved clear-all confirmation and 30/90-day retention behavior.
- Added full DataStore preference replacement for restore.
- Reduced-motion preference now actually removes navigation transitions instead of only being persisted.

## Explicit backup and restore

- Added versioned `SpendCalcBackup` model and complete `BackupCodec`/`BackupRepository` wiring.
- Settings now exposes Export backup and Restore backup through Android's document picker APIs.
- Backup includes history, templates, and user preferences.
- Restore requires a destructive replacement confirmation.
- Decoder rejects unknown schemas and malformed records.
- Added bounded payload, line, record, field, timestamp, split, and decimal validation.
- Added pre-split newline counting so line-dense input is rejected before creating a large split list.
- Added unique identifier checks for history/templates.
- Added strict checksum shape and SHA-256 corruption detection.
- Added plain-decimal-only validation so exponent expansion is rejected.
- Template values are revalidated through `CalculatorEngine`.
- Room history/templates replace inside one database transaction.
- Because preferences use DataStore, restore snapshots the old full state first and performs compensating rollback if the multi-store operation fails.
- Backup read/write is dispatched off the UI thread.

## Export and platform hardening

- Preserved text, CSV, and PDF receipt exports.
- CSV formula-prefix neutralization remains covered by tests.
- FileProvider remains non-exported and limited to the private `cache/exports/` path.
- Added canonical path containment instead of simple string-prefix containment.
- Added regression coverage proving sibling paths such as `exports-private` are rejected.
- Refactored export file creation from file sharing.
- CSV/PDF file creation now runs on `Dispatchers.IO`.
- Backup payload is generated after the destination picker returns instead of relying on fragile pending in-memory content.
- Cancellation is preserved through coroutine error paths.

## Logging and privacy hardening

- Structured logging continues to sanitize line breaks, truncate values, and redact protected field names.
- Field-key normalization now uses `Locale.ROOT` so redaction is stable across device locales.
- Added locale regression coverage.
- The current manifest still has no Android `INTERNET` permission.
- Android system-managed backup/device transfer remains separately documented from explicit user-created backup files.

## Tests added or expanded

### JVM

- Finance discount cap, decimal shape bounds, split bounds, locale normalization, and exponent-shape rejection.
- Seeded deterministic finance fuzz/regression tests.
- History restore/Undo repository path.
- Template replacement path.
- Backup round trip, Unicode, checksum corruption, schema rejection, duplicate IDs, structural limits, exponent rejection, timestamp and numeric validation.
- Seeded deterministic backup fuzz/regression tests.
- Canonical export path containment.
- Locale-independent structured-log redaction.

### Android

- Existing Room and Compose tests retained.
- Backup replacement database integration retained.
- Existing real-activity calculate -> save -> History journey retained as the canonical end-to-end smoke path.
- A duplicate stateful journey test created during the audit was removed after comparison to avoid shared DataStore interference.
- CI now compiles instrumentation tests with `assembleDebugAndroidTest`; connected-device execution remains a separate release gate.

## CI and automation

Main CI now checks formatting, Kotlin package syntax, repository metadata/local Markdown links, repository scanning rules, JVM tests, instrumentation-test compilation, Android lint, debug build, and release build.

Separate workflows cover CodeQL, dependency review, repository audit, and tag-triggered unsigned release-candidate builds. Superseded CI/security/audit runs use concurrency cancellation so the newest PR revision receives runner priority.

GitHub workflow actions were updated to maintained major versions while keeping the prompt-defined Android/Gradle/Kotlin project baseline stable.

## Documentation reconciled

This continuation updated:

- `CHANGELOG.md`
- `ROADMAP.md`
- `docs/testing.md`
- `docs/verification.md`
- `docs/security-backup.md`
- `docs/backup-restore.md`
- `docs/privacy-backup.md`
- `docs/performance.md`
- `what_changed.md`

The documents now distinguish implemented source/automation from checks that still require a device, signing environment, real screenshots, or completed GitHub workflow results.

## Important implementation files

- `app/src/main/java/in/sanskar/spendcalc/domain/CalculatorEngine.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedModels.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/export/BackupCodec.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/BackupRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/HistoryRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/TemplateRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/SettingsRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcViewModel.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcApp.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/HistoryScreen.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/SettingsScreen.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/BackupFileIo.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/ExportManager.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/PathSafety.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/SafeLogger.kt`

## Verification truth

- PR `#12` is the active release-candidate verification path.
- Commit-graph checks confirmed the critical workflow, instrumentation-compilation, path-containment, background-I/O, and logging fixes are ancestors of the PR head rather than detached work.
- Git commit metadata produced during this work uses the requested `sanskarin@outlook.in` address.
- A configured or queued workflow is not recorded as a successful build.
- No `v1.0.0` tag should be created until the exact final PR revision passes the required release gates.

## Remaining release gates

These cannot be truthfully fabricated from source code alone:

- final CI, CodeQL, Dependency Review, and Repository Audit success on the exact final PR revision;
- `connectedDebugAndroidTest` on an emulator/device;
- manual TalkBack and large-system-font review;
- manual phone/tablet/wide-layout review;
- manual Android share/document-picker checks for receipt/CSV/PDF/backup paths;
- real screenshots captured from the verified build with fictional data;
- production signing outside source control;
- creation of the production tag/release after all blockers pass.

## Database/migration state

- Room database version: 1.
- No historical production schema exists yet.
- Destructive migration fallback is intentionally not the default strategy.
- Schema version 2 must include an explicit migration and migration test.

## Continuation rule

While PR `#12` is open, continue from `complete/v1-finalization`. After it is merged, continue from `main`. Do not use an older handoff as proof that a feature or verification gate is complete; inspect the current source and check results first. Keep future changes small, meaningful, and reflected in this file.
