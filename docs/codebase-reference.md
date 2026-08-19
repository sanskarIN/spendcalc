# SpendCalc Codebase Reference

This is the exhaustive tracked-file reference for the SpendCalc repository. It is intentionally file-oriented: every tracked file must appear exactly once in the file index below, together with its role in the product, engineering workflow, test strategy, security model, or documentation set.

The companion [`documentation-map.md`](documentation-map.md) explains which documents are authoritative for each audience and change type. `scripts/check_documentation_coverage.py` compares this index with `git ls-files` so adding, deleting, or renaming a tracked file requires this reference to be updated in the same change.

## How to use this reference

- Start with `README.md` for the public product overview.
- Use `docs/architecture.md` for dependency direction and system boundaries.
- Use this file when locating ownership for a concrete source/resource/test/configuration file.
- Use `docs/testing.md` to understand the verification layer paired with a source change.
- Use `docs/persistence-invariants.md` and `docs/security-backup.md` before modifying stored-record or backup behavior.
- Use `docs/verification.md` before calling any commit release-ready.
- Use `what_changed.md` only for the current multi-session engineering handoff; it does not replace permanent design documentation.

## File index

<!-- FILE-INDEX:START -->

### Root project, policy, and build files

- `.editorconfig` — Repository-wide editor defaults for encoding, indentation, final newlines, and whitespace consistency; complements the lightweight format guard.
- `.env.example` — Explicitly documents that core SpendCalc requires no remote API keys or environment secrets and reserves non-secret placeholders for possible future optional integrations.
- `.gitattributes` — Git text/binary and line-ending normalization rules used to keep repository behavior consistent across development platforms.
- `.gitignore` — Excludes Android/Gradle/IDE build state, local configuration, signing material, and other machine-specific artifacts from source control.
- `CHANGELOG.md` — User-visible and security-relevant change history for the current release candidate and future semantic-versioned releases.
- `CODE_OF_CONDUCT.md` — Community participation and conduct expectations for the open-source project.
- `CONTRIBUTING.md` — Contributor workflow, quality expectations, privacy/security boundaries, commit guidance, and pull-request requirements.
- `LICENSE` — MIT license governing use, modification, and redistribution of the project.
- `PRIVACY.md` — Public privacy statement covering local data, user-created backups, Android system backup/device transfer, external actions, and the no-analytics/no-advertising baseline.
- `README.md` — Primary public entry point: product positioning, capabilities, architecture summary, build/test commands, release boundary, support, and project links.
- `ROADMAP.md` — Release-oriented implementation and verification roadmap; distinguishes completed source work from still-open automated/manual release gates.
- `SECURITY.md` — Vulnerability-reporting policy, security assumptions, supported release posture, and guidance against exposing sensitive reports publicly.
- `SUPPORT.md` — User/contributor support channels and the distinction between support questions, bugs, feature requests, and security reports.
- `build.gradle.kts` — Root Gradle plugin declarations and pinned Android/Kotlin/Compose/KSP plugin versions; plugins are applied by the app module.
- `gradle.properties` — Repository Gradle/JVM, AndroidX, Kotlin style, and Android resource-ID build settings.
- `settings.gradle.kts` — Defines plugin/dependency repositories, repository mode, root project name, and the single `:app` module.
- `what_changed.md` — Canonical current engineering handoff containing the active branch/PR state, recent implementation decisions, verification truth, and exact continuation instructions.
- `what_changed_final.md` — Compatibility summary that points readers back to `what_changed.md` and the release verification checklist instead of acting as an independent current-state source.
- `what_changed_latest.md` — Compatibility pointer retained for older handoffs; explicitly warns that it is not newer or more authoritative than `what_changed.md`.

### GitHub repository automation and contribution UX

- `.github/FUNDING.yml` — Configures the repository funding link used by GitHub's sponsor/funding UI.
- `.github/ISSUE_TEMPLATE/bug_report.yml` — Structured bug-report form requesting reproducible, privacy-conscious diagnostic information.
- `.github/ISSUE_TEMPLATE/config.yml` — Issue-template chooser configuration and contact links/blank-issue policy.
- `.github/ISSUE_TEMPLATE/feature_request.yml` — Structured feature-request form designed to capture value, scope, privacy, and accessibility considerations.
- `.github/dependabot.yml` — Scheduled dependency-update configuration for Gradle and GitHub Actions ecosystems.
- `.github/pull_request_template.md` — Pull-request checklist for behavior, tests, documentation, privacy/security, accessibility, and release-impact review.
- `.github/workflows/ci.yml` — Main pull-request CI: formatting, namespace, documentation/resource/security/repository/secret guards, JVM tests, instrumentation compilation, lint, debug build, and release build.
- `.github/workflows/codeql.yml` — Java/Kotlin CodeQL static-analysis workflow used as a release-blocking security signal when applicable.
- `.github/workflows/dependency-review.yml` — Pull-request dependency change review to surface risky newly introduced packages/licenses/vulnerabilities.
- `.github/workflows/release.yml` — Tag-triggered release-candidate workflow that verifies/builds an unsigned artifact; production signing remains outside source control.
- `.github/workflows/repository-audit.yml` — Lightweight fast audit workflow for repository/documentation/resource invariants before expensive Android work.

### Android module build and schema metadata

- `app/build.gradle.kts` — Android application configuration: namespace/application ID, API 26+ support, compile/target SDK, `1.0.0` version metadata, Java 17, Compose, release shrinking, dependencies, tests, packaging, and Room schema export arguments.
- `app/proguard-rules.pro` — App-specific R8/ProGuard rule location; intentionally minimal because current dependencies primarily provide their own consumer rules.
- `app/schemas/README.md` — Explains the Room exported-schema directory and the requirement to preserve/version schema history when migrations begin.

### Android instrumentation and Compose integration tests

- `app/src/androidTest/java/in/sanskar/spendcalc/data/local/BackupRestoreDatabaseTest.kt` — Android Room integration coverage for transactional replacement/restore behavior across persisted history and templates.
- `app/src/androidTest/java/in/sanskar/spendcalc/data/local/SpendCalcDatabaseTest.kt` — On-device/instrumented Room round-trip coverage for the database entities and DAO mappings.
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/CalculatorScreenTest.kt` — Compose regression tests for calculator rendering plus named-history/template dialogs, callback wiring, guidance, and surrogate-safe saved-name boundaries.
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/HistoryScreenTest.kt` — Compose regression coverage proving local History filtering finds matching saved labels and excludes non-matching records.
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/MainActivityJourneyTest.kt` — Real-activity journey covering onboarding as needed, calculation, named save, navigation to History, and verification of the saved label/value.
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/SettingsScreenTest.kt` — Compose coverage for backup busy/progress presentation and prevention of duplicate backup actions while work is active.

### Android manifest and application bootstrap

- `app/src/main/AndroidManifest.xml` — Declares the application/activity, AndroidX splash theme, backup/data-extraction rules, and non-exported cache `FileProvider`; intentionally does not request Android Internet permission.
- `app/src/main/java/in/sanskar/spendcalc/AppContainer.kt` — Explicit dependency composition root for Room, repositories, settings, `CalculatorEngine`, and backup codec/repository; shares the finance validator with template persistence.
- `app/src/main/java/in/sanskar/spendcalc/MainActivity.kt` — Android activity entry point; installs the splash screen, waits for preferences to load, configures edge-to-edge content, creates the ViewModel, and hosts Compose.
- `app/src/main/java/in/sanskar/spendcalc/SpendCalcApplication.kt` — Application-level owner for the lazily created `AppContainer`.

### Data repositories and Room persistence

- `app/src/main/java/in/sanskar/spendcalc/data/BackupRepository.kt` — Coordinates stable history/template/preference snapshots and transactional Room replacement for explicit backup/restore orchestration.
- `app/src/main/java/in/sanskar/spendcalc/data/HistoryRepository.kt` — History persistence boundary: normalizes new labels, validates persisted-record envelopes, maps `BigDecimal` values as plain strings, supports snapshot/delete/clear/retention/restore, and rejects invalid/duplicate batch input before replacement.
- `app/src/main/java/in/sanskar/spendcalc/data/SettingsRepository.kt` — Preferences DataStore adapter for theme, large text, reduced motion, retention, and onboarding state, including safe fallback behavior for corrupted preference storage.
- `app/src/main/java/in/sanskar/spendcalc/data/TemplateRepository.kt` — Template persistence boundary: validates persisted settings through `CalculatorEngine`, enforces the shared record/name envelope, canonicalizes currencies for new data, and protects restore/replace operations from invalid/duplicate records.
- `app/src/main/java/in/sanskar/spendcalc/data/local/HistoryDao.kt` — Room DAO for observing, snapshotting, upserting, deleting, clearing, aging, and batch-replacing calculation history.
- `app/src/main/java/in/sanskar/spendcalc/data/local/HistoryEntity.kt` — Room history table entity storing bounded/canonical metadata and decimal values as exact strings.
- `app/src/main/java/in/sanskar/spendcalc/data/local/SpendCalcDatabase.kt` — Room database definition/version and DAO exposure for history/templates.
- `app/src/main/java/in/sanskar/spendcalc/data/local/TemplateDao.kt` — Room DAO for observing, snapshotting, upserting, deleting, clearing, and batch-replacing reusable templates.
- `app/src/main/java/in/sanskar/spendcalc/data/local/TemplateEntity.kt` — Room template table entity storing reusable calculator settings and decimal values as exact strings.

### Finance domain, export contracts, and persisted models

- `app/src/main/java/in/sanskar/spendcalc/domain/CalculatorEngine.kt` — Pure precision-safe finance engine and validator: item totals, discount, charges, manual conversion, split, rounding, numeric bounds, currency rules, and typed calculation errors.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/BackupCodec.kt` — Deterministic versioned backup serializer/parser with URL-safe Base64 text fields, SHA-256 corruption detection, bounded parsing, record validation, duplicate-ID rejection, canonical persisted-record policy reuse, and fail-closed decoding.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/CsvExportFormatter.kt` — Platform-independent CSV receipt serializer with quoting and spreadsheet-formula neutralization for text cells.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/ExportFormatter.kt` — Small domain interface for platform-independent text-based export implementations.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/ReceiptTextFormatter.kt` — Human-readable plain-text receipt serializer used by Android sharing.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/ExpenseModels.kt` — Core calculation input/result, expense-item, rounding-policy, error, and outcome domain models.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedModels.kt` — History/template/preferences/backup domain models and shared saved-name size constant.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedNamePolicy.kt` — Shared saved-label/template-name normalization and validation, including UTF-16 well-formedness and surrogate-safe truncation at the 120-character boundary.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedRecordPolicy.kt` — Shared persisted-record envelope rules for IDs, timestamps, names, canonical currencies, split counts, result decimal magnitude/scale, and unique batch identifiers.

### Android platform adapters

- `app/src/main/java/in/sanskar/spendcalc/platform/BackupFileIo.kt` — Bounded UTF-8 document read/write adapter for Android Storage Access Framework backup URIs.
- `app/src/main/java/in/sanskar/spendcalc/platform/ExportManager.kt` — Android export/share coordinator that writes app-private cache exports and launches user-selected share flows with temporary URI access.
- `app/src/main/java/in/sanskar/spendcalc/platform/ExternalLinks.kt` — Safe explicit-user-action helpers for opening repository/funding URLs and email applications without making the core app network-dependent.
- `app/src/main/java/in/sanskar/spendcalc/platform/PathSafety.kt` — Canonical-path containment helper preventing cache-export path prefix/sibling escape mistakes.
- `app/src/main/java/in/sanskar/spendcalc/platform/PdfReceiptExporter.kt` — Offline Android `PdfDocument` renderer for receipt export.
- `app/src/main/java/in/sanskar/spendcalc/platform/SafeLogger.kt` — Structured logging boundary that normalizes keys with `Locale.ROOT` and redacts sensitive categories instead of recording raw private values.

### Compose presentation, navigation, screens, and theme

- `app/src/main/java/in/sanskar/spendcalc/ui/AppUiState.kt` — Presentation-state and sequenced-feedback models used to keep UI state explicit and prevent repeated events from being collapsed.
- `app/src/main/java/in/sanskar/spendcalc/ui/CalculatorStateMapper.kt` — Converts editable calculator text state into validated domain `CalculationInput` and maps parse/domain failures to form issues.
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcApp.kt` — Top-level Compose application shell: theme, navigation, screens, document-picker/share side effects, backup confirmation/progress, Snackbar feedback, and platform action wiring.
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcViewModel.kt` — Presentation orchestrator for calculator editing, validation/results, history/templates, retention, preferences, backup preparation/restore, undo, and sequenced user feedback.
- `app/src/main/java/in/sanskar/spendcalc/ui/components/Common.kt` — Shared Compose primitives such as screen headers and consistently formatted money rows.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/AboutScreen.kt` — Version/license/project/support/funding/credit presentation and explicit external-action entry points.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/CalculatorScreen.kt` — Responsive calculator form and receipt UI, bounded item editing, validation text, named history/template dialogs, UTF-16-safe name input, and text/CSV/PDF actions.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/HistoryScreen.kt` — Searchable local history list with bounded query input, record details, individual delete/undo integration, and confirmed clear-all behavior.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/OnboardingScreen.kt` — First-run local/offline/privacy-oriented onboarding experience.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/SettingsScreen.kt` — Theme/accessibility/retention/backup-restore/settings UI, including modal busy state and links to About/repository actions.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/TemplatesScreen.kt` — Reusable template list with load/delete behavior and empty-state presentation.
- `app/src/main/java/in/sanskar/spendcalc/ui/theme/DesignTokens.kt` — Shared spacing/radius/layout constants used to keep Compose visuals and touch/layout decisions consistent.
- `app/src/main/java/in/sanskar/spendcalc/ui/theme/Theme.kt` — Material 3 light/dark/system theme configuration, large-text typography adjustments, and system-bar appearance behavior.

### Android drawable resources

- `app/src/main/res/drawable/ic_nav_calculator.xml` — Repository-owned vector icon for the Calculator navigation destination; text label remains the accessible name.
- `app/src/main/res/drawable/ic_nav_history.xml` — Repository-owned vector icon for History navigation.
- `app/src/main/res/drawable/ic_nav_settings.xml` — Repository-owned vector icon for Settings navigation.
- `app/src/main/res/drawable/ic_nav_templates.xml` — Repository-owned vector icon for Templates navigation.
- `app/src/main/res/drawable/ic_spendcalc.xml` — SpendCalc vector mark used by the branded Android launch/splash treatment.

### Android values and string resources

- `app/src/main/res/values/colors.xml` — Small XML color palette used by Android theme/bootstrap resources.
- `app/src/main/res/values/strings.xml` — Core calculator/navigation/history/template/settings/about labels, validation messages, project contacts/URLs, and common actions.
- `app/src/main/res/values/strings_about.xml` — Additional About/support/funding copy kept separate for maintainability.
- `app/src/main/res/values/strings_export.xml` — Text/CSV/PDF/export and sharing strings.
- `app/src/main/res/values/strings_feedback.xml` — Snackbar/status/error feedback strings for saves, deletes, backup operations, and other user events.
- `app/src/main/res/values/strings_history.xml` — History search, empty/no-result, clear-confirmation, and named-history dialog strings including the 120-character guidance.
- `app/src/main/res/values/strings_limits.xml` — User-facing copy for explicit bounded-work limits such as the maximum editable expense-item count.
- `app/src/main/res/values/strings_onboarding.xml` — First-run onboarding title/body/action copy.
- `app/src/main/res/values/strings_settings.xml` — Settings descriptions for appearance, accessibility, retention, backup/restore, privacy, and related controls.
- `app/src/main/res/values/themes.xml` — Android XML theme/splash configuration used before/around Compose rendering.

### Android XML security, provider, and system-backup rules

- `app/src/main/res/xml/backup_rules.xml` — Legacy Android backup include/exclude rules; kept aligned with the documented device-transfer/system-backup privacy model.
- `app/src/main/res/xml/data_extraction_rules.xml` — Modern Android cloud-backup/device-transfer extraction policy paired with `PRIVACY.md`.
- `app/src/main/res/xml/file_paths.xml` — Restricts `FileProvider` sharing to the app-private `cache/exports/` subtree.

### JVM repository tests

- `app/src/test/java/in/sanskar/spendcalc/data/HistoryRepositoryTest.kt` — Unit coverage for history save/normalization, snapshot, clear/retention, exact restore, persisted-envelope rejection, and failure-before-write semantics.
- `app/src/test/java/in/sanskar/spendcalc/data/HistoryRestoreRepositoryTest.kt` — Focused regression coverage for exact accepted restore behavior and invalid-history restore rejection.
- `app/src/test/java/in/sanskar/spendcalc/data/RepositoryDuplicateIdTest.kt` — Guards history/template batch replacement against duplicate IDs before any DAO replacement can mutate existing state.
- `app/src/test/java/in/sanskar/spendcalc/data/TemplateRepositoryTest.kt` — Template save/load-persistence coverage including name normalization, shared finance validation, exact restore, invalid settings/envelope rejection, and batch failure-before-write behavior.

### JVM finance/domain/export tests

- `app/src/test/java/in/sanskar/spendcalc/domain/CalculatorEngineTest.kt` — Exact arithmetic, rounding, order-of-operations, validation, currency, split, percentage, and numeric-bound tests for `CalculatorEngine`.
- `app/src/test/java/in/sanskar/spendcalc/domain/CalculatorFuzzTest.kt` — Deterministic seeded finance regression/fuzz invariants for valid and invalid generated inputs.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecFuzzTest.kt` — Deterministic generated backup round-trip/corruption mutations that must remain reproducible in CI.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecPersistedPolicyTest.kt` — Verifies backup encode/decode obeys the same persisted-record envelope and canonicalization expectations as repositories.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecSavedNamePolicyTest.kt` — Exercises saved-name Unicode/surrogate boundary behavior through real backup serialization round trips.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecTest.kt` — Primary backup-format tests for deterministic round trips, checksums, versions, records, Unicode, preferences, identifiers, and structural limits.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecValidationTest.kt` — Focused malformed/invalid backup-record validation regressions and fail-closed outcomes.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/CsvExportFormatterTest.kt` — CSV shape, quoting, embedded-quote, and formula-neutralization regression tests.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/CsvExportFuzzTest.kt` — Deterministic generated CSV text-cell/escaping security regressions.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/ReceiptTextFormatterTest.kt` — Plain-text receipt output regression coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/model/SavedNamePolicyTest.kt` — Shared saved-name tests for trimming/fallback, 120-character bounds, malformed UTF-16 rejection, surrogate-safe truncation, and repair of earlier split boundaries.
- `app/src/test/java/in/sanskar/spendcalc/domain/model/SavedRecordPolicyTest.kt` — Shared persisted-envelope tests for IDs, timestamps, canonical currencies, split bounds, decimal magnitude/scale, and valid history/template envelopes.

### JVM platform/presentation tests

- `app/src/test/java/in/sanskar/spendcalc/platform/PathSafetyTest.kt` — Canonical path-containment tests including sibling-prefix bypass prevention.
- `app/src/test/java/in/sanskar/spendcalc/platform/SafeLoggerTest.kt` — Sensitive-key redaction and locale-independent key-normalization regressions, including Turkish-locale behavior.
- `app/src/test/java/in/sanskar/spendcalc/ui/AppUiStateTest.kt` — Presentation feedback/event sequencing regression coverage.

### Permanent product and engineering documentation

- `docs/accessibility.md` — Accessibility implementation decisions plus TalkBack, font-scale, motion, contrast, touch, dialog, and device-size manual verification guidance.
- `docs/adr/0001-use-bigdecimal-for-finance.md` — ADR establishing `BigDecimal` rather than binary floating point for finance arithmetic.
- `docs/adr/0002-local-first-core.md` — ADR establishing no-account/no-required-network local-first core behavior.
- `docs/adr/0003-room-and-datastore.md` — ADR establishing Room for structured records and DataStore for user preferences.
- `docs/adr/0004-versioned-local-backup.md` — ADR for the explicit versioned bounded backup format, integrity checksum, validation, and restore model.
- `docs/architecture.md` — Layer responsibilities, dependency direction, calculation order, persistence/export architecture, error handling, dependency wiring, and links to detailed invariants.
- `docs/assets/screenshots/README.md` — Policy/checklist for capturing real verified release screenshots using fictional data rather than fabricated imagery.
- `docs/assets/spendcalc-logo.svg` — Editable repository-owned SVG logo source used in project documentation/branding.
- `docs/backup-restore.md` — User/developer behavior of explicit backup/restore, scope, format, SAF flow, confirmation, rollback, and compatibility boundaries.
- `docs/codebase-reference.md` — This exhaustive tracked-file inventory; source of truth for file ownership/purpose and documentation-coverage enforcement.
- `docs/design-system.md` — Compose/Material design tokens, typography/theme, spacing, responsiveness, interaction, and accessibility design conventions.
- `docs/development.md` — Day-to-day contributor guide for source layout, finance/UI/persistence/export/logging changes, dependencies, quality commands, and Git workflow.
- `docs/documentation-map.md` — Documentation information architecture: audience, authority, update triggers, duplication rules, and source-of-truth relationships.
- `docs/features.md` — Implemented product behavior that must remain aligned with tests and release verification.
- `docs/github-maintenance.md` — Repository-maintenance procedures for Actions, Dependabot, issue/PR hygiene, documentation, release checks, and multi-session handoffs.
- `docs/logging.md` — Safe structured-logging policy and sensitive-field redaction boundaries.
- `docs/performance.md` — Performance model, bounded-work decisions, off-main-thread I/O, profiling expectations, and thresholds for future optimization work.
- `docs/persistence-invariants.md` — Permanent contract requiring every repository-accepted saved record to remain backup-exportable, including shared names/IDs/currency/timestamp/split/decimal/duplicate-ID rules.
- `docs/privacy-backup.md` — Privacy analysis for explicit user-created backups versus Android system-managed backup/device transfer.
- `docs/release-candidate-final-audit.md` — Source-level 1.0.0 release-candidate audit; records what is implemented without falsely claiming pending automated/manual gates passed.
- `docs/release.md` — Release workflow, versioning, exact-commit verification, unsigned artifact generation, external signing, screenshots, and tag/publication steps.
- `docs/security-backup.md` — Backup/restore threat model, parser limits, Unicode/text/decimal validation, checksum semantics, rollback, and format-evolution security requirements.
- `docs/setup.md` — Local environment/setup instructions for Git, JDK 17, Android SDK 35, Gradle/Android Studio, build/run, and troubleshooting handoff.
- `docs/testing.md` — Verification taxonomy and commands for JVM tests, deterministic fuzz tests, Android integration/Compose tests, repository guards, regression policy, migrations, and manual release checks.
- `docs/troubleshooting.md` — Known setup/build/KSP/Room/emulator/export/release troubleshooting guidance.
- `docs/verification.md` — Exact release-candidate checklist separating automated PR checks, Android runtime checks, accessibility/responsive checks, security/privacy checks, and final release/signing/screenshots.

### Repository guard scripts

- `scripts/check_android_resources.py` — Parses default Android string resources and scans Kotlin/XML references to reject missing or duplicate default string resources before Android compilation.
- `scripts/check_android_security.py` — Fast manifest/FileProvider/local-first policy audit, including the no-Internet-permission and cache-export-provider invariants.
- `scripts/check_documentation_coverage.py` — Compares `git ls-files` with this file index so every tracked file is documented exactly once and stale/deleted paths cannot remain in the reference.
- `scripts/check_format.py` — Lightweight repository text hygiene guard for UTF-8, final newline, trailing whitespace, and tab characters.
- `scripts/check_kotlin_namespace.py` — Detects invalid/reserved Kotlin package/namespace regressions in source files.
- `scripts/check_repository.py` — Verifies required repository/release documentation and local Markdown links plus required project identity/contact text.
- `scripts/scan_secrets.py` — Fast common secret-pattern scan intended to catch accidental credential/token/signing-material commits; complements, rather than replaces, platform secret scanning.

<!-- FILE-INDEX:END -->

## Ownership and change rules

### When adding or deleting a file

Update this index in the same pull request. The documentation-coverage guard intentionally fails when a tracked path is missing, duplicated, or still documented after deletion/rename.

### When changing production behavior

Update the nearest behavior document (`features.md`, `architecture.md`, persistence/backup/security/privacy/accessibility/performance documentation as applicable), add the lowest-practical regression test, and update `CHANGELOG.md` for user-visible changes.

### When changing CI/repository policy

Update `development.md`, `testing.md`, `github-maintenance.md`, `verification.md`, and this file where the command/workflow/source-of-truth contract changes.

### When changing release status

Update `what_changed.md` and `docs/verification.md` truthfully. Queued, pending, skipped, cancelled, or not-yet-run checks are never represented as successful.
