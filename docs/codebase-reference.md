# SpendCalc Codebase Reference

This is the exhaustive tracked-file reference for the SpendCalc repository. Every tracked file must appear exactly once in the file index below together with its role in the product, engineering workflow, test strategy, security model, or documentation set.

The companion [`documentation-map.md`](documentation-map.md) defines documentation authority. `scripts/check_documentation_coverage.py` compares the marked index with `git ls-files`, so any tracked-file add, delete, or rename must update this reference in the same change.

## How to use this reference

- Start with `README.md` for the public product overview.
- Use `docs/README.md` for documentation navigation.
- Use `docs/android-build-guide.md` for APK/AAB build, install, signing, and inspection workflows.
- Use `docs/command-reference.md` for command meanings and examples.
- Use `docs/architecture.md` for dependency direction and system boundaries.
- Use `docs/testing.md` for verification strategy.
- Use `docs/persistence-invariants.md` and `docs/security-backup.md` before changing stored-data or backup behavior.
- Use `docs/verification.md` before calling a commit release-ready.
- Use `what_changed.md` for the current multi-session engineering handoff only.

## File index

<!-- FILE-INDEX:START -->

### Root project, policy, and build files

- `.editorconfig` — Repository-wide editor defaults for encoding, indentation, final newlines, and whitespace consistency.
- `.env.example` — Documents the local-first no-secret baseline and reserved non-secret placeholders.
- `.gitattributes` — Git text/binary and line-ending normalization rules.
- `.gitignore` — Excludes Android/Gradle/IDE state, local configuration, signing material, and generated artifacts.
- `CHANGELOG.md` — User-visible and security-relevant release history.
- `CODE_OF_CONDUCT.md` — Community participation expectations.
- `CONTRIBUTING.md` — Contributor workflow, quality, privacy, security, and pull-request requirements.
- `LICENSE` — MIT license.
- `PRIVACY.md` — Public local-data, backup, external-action, analytics, and advertising privacy contract.
- `README.md` — Primary public product and repository entry point.
- `ROADMAP.md` — Release-oriented implementation and verification roadmap.
- `SECURITY.md` — Vulnerability reporting and security-support policy.
- `SUPPORT.md` — Support channels and issue-routing guidance.
- `build.gradle.kts` — Root Gradle plugin declarations.
- `gradle.properties` — Repository Gradle/JVM/Android/Kotlin settings.
- `settings.gradle.kts` — Plugin/dependency repositories and module declaration.
- `what_changed.md` — Canonical current engineering handoff and exact continuation state.
- `what_changed_final.md` — Compatibility handoff pointer.
- `what_changed_latest.md` — Compatibility handoff pointer retained for older sessions.

### GitHub repository automation and contribution UX

- `.github/FUNDING.yml` — Repository funding link configuration.
- `.github/ISSUE_TEMPLATE/bug_report.yml` — Structured privacy-conscious bug-report form.
- `.github/ISSUE_TEMPLATE/config.yml` — Issue-template chooser configuration.
- `.github/ISSUE_TEMPLATE/feature_request.yml` — Structured feature-request form.
- `.github/dependabot.yml` — Scheduled Gradle and GitHub Actions dependency updates.
- `.github/pull_request_template.md` — Pull-request quality/security/accessibility checklist.
- `.github/workflows/android-instrumentation.yml` — API 35 connected Android test workflow and failure artifacts.
- `.github/workflows/ci.yml` — Main format, repository guard, JVM test, lint, and build workflow.
- `.github/workflows/codeql.yml` — Java/Kotlin CodeQL static-analysis workflow.
- `.github/workflows/dependency-review.yml` — Pull-request dependency review workflow.
- `.github/workflows/release.yml` — Tag-triggered release-candidate verification/build workflow.
- `.github/workflows/repository-audit.yml` — Fast repository/documentation/resource invariant workflow.

### Android module build and schema metadata

- `app/build.gradle.kts` — Android application configuration, release metadata, dependencies, Compose, tests, packaging, and Room schema export.
- `app/proguard-rules.pro` — App-specific R8/ProGuard rules.
- `app/schemas/README.md` — Room exported-schema policy and migration-history guidance.

### Android instrumentation and Compose integration tests

- `app/src/androidTest/java/in/sanskar/spendcalc/data/local/BackupRestoreDatabaseTest.kt` — Room backup replacement/restore integration coverage.
- `app/src/androidTest/java/in/sanskar/spendcalc/data/local/SpendCalcDatabaseTest.kt` — Room entity/DAO round-trip integration coverage.
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/CalculatorScreenTest.kt` — Compose calculator and saved-name dialog regressions.
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/HistoryScreenTest.kt` — Compose saved-label history filtering regression.
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/MainActivityJourneyTest.kt` — Real-activity calculate/save/history journey.
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/SettingsScreenTest.kt` — Backup busy-state and duplicate-action prevention coverage.

### Android manifest and application bootstrap

- `app/src/main/AndroidManifest.xml` — Application/activity, splash, backup rules, and restricted FileProvider declarations.
- `app/src/main/java/in/sanskar/spendcalc/AppContainer.kt` — Explicit dependency composition root.
- `app/src/main/java/in/sanskar/spendcalc/MainActivity.kt` — Android activity, splash, edge-to-edge, ViewModel, and Compose entry point.
- `app/src/main/java/in/sanskar/spendcalc/SpendCalcApplication.kt` — Application-level `AppContainer` owner.

### Data repositories and Room persistence

- `app/src/main/java/in/sanskar/spendcalc/data/BackupRepository.kt` — Stable local snapshots and restore orchestration.
- `app/src/main/java/in/sanskar/spendcalc/data/HistoryRepository.kt` — History validation, mapping, retention, deletion, snapshots, and replacement.
- `app/src/main/java/in/sanskar/spendcalc/data/SettingsRepository.kt` — DataStore settings adapter and corruption fallback behavior.
- `app/src/main/java/in/sanskar/spendcalc/data/TemplateRepository.kt` — Reusable template validation, persistence, and replacement.
- `app/src/main/java/in/sanskar/spendcalc/data/local/HistoryDao.kt` — Room history DAO.
- `app/src/main/java/in/sanskar/spendcalc/data/local/HistoryEntity.kt` — Room history entity.
- `app/src/main/java/in/sanskar/spendcalc/data/local/SpendCalcDatabase.kt` — Room database definition and DAO exposure.
- `app/src/main/java/in/sanskar/spendcalc/data/local/TemplateDao.kt` — Room template DAO.
- `app/src/main/java/in/sanskar/spendcalc/data/local/TemplateEntity.kt` — Room template entity.

### Domain model and finance/export/backup logic

- `app/src/main/java/in/sanskar/spendcalc/domain/CalculatorEngine.kt` — Precision-safe bounded `BigDecimal` finance engine and validation boundary.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/BackupCodec.kt` — Versioned deterministic backup codec with integrity and strict persisted-record validation.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/CsvExportFormatter.kt` — CSV export serializer with quoting and spreadsheet-formula neutralization.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/ExportFormatter.kt` — Shared export formatting contract/helpers used by receipt serializers.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/ReceiptTextFormatter.kt` — Human-readable receipt text serializer.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/ExpenseModels.kt` — Finance input/output models, limits, validation errors, and defaults.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedModels.kt` — Persisted history/template/preferences/backup domain models.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedNamePolicy.kt` — Unicode-safe saved-name normalization and bounds.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedRecordPolicy.kt` — Shared persisted-record envelope and duplicate-ID validation.

### Platform/export/logging helpers

- `app/src/main/java/in/sanskar/spendcalc/platform/BackupFileIo.kt` — Bounded document-stream backup reads/writes and strict UTF-8 delegation.
- `app/src/main/java/in/sanskar/spendcalc/platform/ExportManager.kt` — Android text/CSV/PDF export and secure FileProvider sharing coordinator.
- `app/src/main/java/in/sanskar/spendcalc/platform/ExternalLinks.kt` — Centralized validated external project/support/funding link definitions and launching boundary.
- `app/src/main/java/in/sanskar/spendcalc/platform/PathSafety.kt` — Canonical export-path containment guard.
- `app/src/main/java/in/sanskar/spendcalc/platform/PdfReceiptExporter.kt` — Bounded Android PDF receipt renderer.
- `app/src/main/java/in/sanskar/spendcalc/platform/SafeLogger.kt` — Local diagnostic logging with deterministic sensitive-key redaction.

### Compose UI and presentation state

- `app/src/main/java/in/sanskar/spendcalc/ui/AppUiState.kt` — Top-level immutable presentation state and transient feedback sequencing.
- `app/src/main/java/in/sanskar/spendcalc/ui/CalculatorStateMapper.kt` — Calculator draft/domain mapping and presentation conversion helpers.
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcApp.kt` — App scaffold/navigation/dialog/backup launcher orchestration.
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcViewModel.kt` — Lifecycle-aware calculation, persistence, backup, settings, and feedback coordinator.
- `app/src/main/java/in/sanskar/spendcalc/ui/components/Common.kt` — Shared reusable Compose components and formatting surfaces.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/AboutScreen.kt` — About/support/funding/version/privacy surface.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/CalculatorScreen.kt` — Responsive calculator form, receipt, save/template, and export actions.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/HistoryScreen.kt` — Searchable local history, delete/clear, and empty states.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/OnboardingScreen.kt` — First-run local-first introduction.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/SettingsScreen.kt` — Theme/accessibility/retention/backup/About settings.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/TemplatesScreen.kt` — Reusable calculation-template list and actions.
- `app/src/main/java/in/sanskar/spendcalc/ui/theme/DesignTokens.kt` — Shared design-system spacing and UI tokens.
- `app/src/main/java/in/sanskar/spendcalc/ui/theme/Theme.kt` — Material 3 light/dark/system theme and large-text typography scaling.

### Android resources

- `app/src/main/res/drawable/ic_nav_calculator.xml` — Calculator navigation vector.
- `app/src/main/res/drawable/ic_nav_history.xml` — History navigation vector.
- `app/src/main/res/drawable/ic_nav_settings.xml` — Settings navigation vector.
- `app/src/main/res/drawable/ic_nav_templates.xml` — Templates navigation vector.
- `app/src/main/res/drawable/ic_spendcalc.xml` — Repository-owned SpendCalc brand/icon vector.
- `app/src/main/res/values/colors.xml` — XML colors used by launch/application theming.
- `app/src/main/res/values/strings.xml` — Core product/navigation/calculator/template/general strings.
- `app/src/main/res/values/strings_about.xml` — About/support/funding/version/privacy strings.
- `app/src/main/res/values/strings_export.xml` — Receipt/export/share/PDF/CSV user-facing strings.
- `app/src/main/res/values/strings_feedback.xml` — Result/error/undo/backup feedback strings.
- `app/src/main/res/values/strings_history.xml` — History search/list/delete/clear/retention strings.
- `app/src/main/res/values/strings_limits.xml` — Bounded-input explanatory strings.
- `app/src/main/res/values/strings_onboarding.xml` — First-run onboarding strings.
- `app/src/main/res/values/strings_settings.xml` — Settings/accessibility/backup/retention strings.
- `app/src/main/res/values/themes.xml` — Base Material launch/application themes.
- `app/src/main/res/xml/backup_rules.xml` — Android system backup rules.
- `app/src/main/res/xml/data_extraction_rules.xml` — Android data extraction/device-transfer rules.
- `app/src/main/res/xml/file_paths.xml` — Restricted FileProvider export cache path.

### JVM unit, regression, and deterministic fuzz tests

- `app/src/test/java/in/sanskar/spendcalc/data/HistoryRepositoryTest.kt` — History normalization, retention, restore, invalid-input, and replacement coverage.
- `app/src/test/java/in/sanskar/spendcalc/data/HistoryRestoreRepositoryTest.kt` — Focused history restore mapping coverage.
- `app/src/test/java/in/sanskar/spendcalc/data/RepositoryDuplicateIdTest.kt` — Duplicate-ID prevalidation and atomicity regressions.
- `app/src/test/java/in/sanskar/spendcalc/data/TemplateRepositoryTest.kt` — Template persistence, validation, restore, and replacement coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/CalculatorEngineTest.kt` — Deterministic finance arithmetic, rounding, and validation regressions.
- `app/src/test/java/in/sanskar/spendcalc/domain/CalculatorFuzzTest.kt` — Seeded finance property-style regression coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecFuzzTest.kt` — Seeded Unicode backup and corruption regressions.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecPersistedPolicyTest.kt` — Backup persisted-record envelope/currency policy coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecSavedNamePolicyTest.kt` — Backup saved-name Unicode boundary coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecTest.kt` — Main backup format, checksum, limit, schema, duplicate, and compatibility coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecValidationTest.kt` — Focused strict decode/persisted-record rejection regressions.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/CsvExportFormatterTest.kt` — CSV export quoting, formatting, and formula-neutralization regressions.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/CsvExportFuzzTest.kt` — Seeded CSV export escaping/neutralization fuzz coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/ReceiptTextFormatterTest.kt` — Human-readable receipt formatting coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/model/SavedNamePolicyTest.kt` — Saved-name normalization, truncation, and malformed-surrogate coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/model/SavedRecordPolicyTest.kt` — Persisted-record identifier/timestamp/currency/result/name/duplicate coverage.
- `app/src/test/java/in/sanskar/spendcalc/platform/PathSafetyTest.kt` — Export path-containment escape regressions.
- `app/src/test/java/in/sanskar/spendcalc/platform/SafeLoggerTest.kt` — Sensitive-key redaction and locale-independence regressions.
- `app/src/test/java/in/sanskar/spendcalc/ui/AppUiStateTest.kt` — Presentation result/feedback state regressions.

### Repository verification scripts

- `scripts/check_android_resources.py` — Validates Android string references and duplicate resource names.
- `scripts/check_android_security.py` — Enforces no-Internet and restricted/non-exported FileProvider policy.
- `scripts/check_documentation_coverage.py` — Compares this marked file index with `git ls-files`.
- `scripts/check_format.py` — Enforces UTF-8/final-newline/trailing-whitespace/tab hygiene.
- `scripts/check_kotlin_namespace.py` — Validates Kotlin package declarations and namespace policy.
- `scripts/check_repository.py` — Enforces required files, identity metadata, local links, and release-document drift checks.
- `scripts/scan_secrets.py` — Scans tracked text for common token/private-key/signing-material patterns.

### Documentation and design records

- `docs/README.md` — Documentation index, reading paths, release metadata, and authority boundaries.
- `docs/accessibility.md` — Accessibility decisions and manual review checklist.
- `docs/android-build-guide.md` — Complete APK/AAB build, install, sign, checksum, and troubleshooting guide.
- `docs/architecture.md` — Architecture, dependency direction, trust boundaries, and responsibilities.
- `docs/backup-restore.md` — Explicit backup format, restore behavior, limits, validation, and compatibility.
- `docs/codebase-reference.md` — This machine-enforced exhaustive tracked-file reference.
- `docs/command-reference.md` — Git/Java/Gradle/ADB/SDK/repository-guard command reference.
- `docs/design-system.md` — Material 3 visual, navigation, typography, and accessibility conventions.
- `docs/development.md` — Contributor setup, change boundaries, tests, guards, and release-safe workflow.
- `docs/documentation-map.md` — Documentation authority and anti-drift update requirements.
- `docs/features.md` — Current features, limits, local-first behavior, export/backup, and accessibility capabilities.
- `docs/github-maintenance.md` — Dependency/security/repository/release administration guidance.
- `docs/logging.md` — Local diagnostic logging and redaction contract.
- `docs/performance.md` — Bounded-work and performance review policy.
- `docs/persistence-invariants.md` — Canonical persisted history/template validation contract.
- `docs/privacy-backup.md` — Explicit-backup privacy model and Android system-backup relationship.
- `docs/release-candidate-final-audit.md` — Source-level 2.0.12 release-candidate audit.
- `docs/release.md` — Exact-source verification, signing, artifact, and versioning procedure.
- `docs/security-backup.md` — Backup parser threat model, limits, checksum, decoding, and persistence validation.
- `docs/setup.md` — Minimal contributor environment/setup path.
- `docs/testing.md` — JVM/fuzz/Android/repository-guard test strategy.
- `docs/troubleshooting.md` — Setup/build/test/export/backup/release troubleshooting.
- `docs/verification.md` — Authoritative blocking automated/manual release checklist.
- `docs/adr/0001-use-bigdecimal-for-finance.md` — Decision record for `BigDecimal` financial arithmetic.
- `docs/adr/0002-local-first-core.md` — Decision record for the local-first core architecture.
- `docs/adr/0003-room-and-datastore.md` — Decision record for Room and DataStore persistence roles.
- `docs/adr/0004-versioned-local-backup.md` — Decision record for the versioned explicit local backup format.
- `docs/assets/screenshots/README.md` — Screenshot capture/maintenance guidance and asset placeholder documentation.
- `docs/assets/spendcalc-logo.svg` — Repository-owned scalable SpendCalc documentation/brand logo.

<!-- FILE-INDEX:END -->
