# SpendCalc Codebase Reference

This is the exhaustive tracked-file reference for the SpendCalc repository. It is intentionally file-oriented: every tracked file must appear exactly once in the file index below, together with its role in the product, engineering workflow, test strategy, security model, or documentation set.

The companion [`documentation-map.md`](documentation-map.md) explains which documents are authoritative for each audience and change type. `scripts/check_documentation_coverage.py` compares this index with `git ls-files` so adding, deleting, or renaming a tracked file requires this reference to be updated in the same change.

## How to use this reference

- Start with `README.md` for the public product overview.
- Use `docs/README.md` for the documentation index and recommended reading paths.
- Use `docs/android-build-guide.md` for end-to-end Android APK/AAB build, installation, packaging, and signing guidance.
- Use `docs/command-reference.md` when you need the meaning, purpose, and expected behavior of project/tooling commands.
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
- `.github/workflows/android-instrumentation.yml` — API 35 hardware-accelerated emulator workflow that executes `connectedDebugAndroidTest` on pull requests/main and uploads Android instrumentation reports when the connected test gate fails.
- `.github/workflows/ci.yml` — Main pull-request CI: formatting, namespace, documentation/resource/security/repository/secret guards, JVM tests, instrumentation compilation, lint, debug build, and release build.
- `.github/workflows/codeql.yml` — Java/Kotlin CodeQL static-analysis workflow used as a release-blocking security signal when applicable.
- `.github/workflows/dependency-review.yml` — Pull-request dependency change review to surface risky newly introduced packages/licenses/vulnerabilities.
- `.github/workflows/release.yml` — Tag-triggered release-candidate workflow that verifies/builds an unsigned artifact; production signing remains outside source control.
- `.github/workflows/repository-audit.yml` — Lightweight fast audit workflow for repository/documentation/resource invariants before expensive Android work.

### Android module build and schema metadata

- `app/build.gradle.kts` — Android application configuration: namespace/application ID, API 26+ support, compile/target SDK, `2.0.12`/`versionCode 20012` application metadata, Java 17, Compose, release shrinking, dependencies, tests, packaging, and Room schema export arguments.
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
- `app/src/main/java/in/sanskar/spendcalc/data/local/TemplateDao.kt` — Room DAO for observing, snapshotting, upserting, deleting, clearing, and batch-replacing calculation templates.
- `app/src/main/java/in/sanskar/spendcalc/data/local/TemplateEntity.kt` — Room template table entity storing reusable calculator settings with decimal values as exact strings.

### Domain model and finance/backup logic

- `app/src/main/java/in/sanskar/spendcalc/domain/CalculatorEngine.kt` — Pure `BigDecimal` finance engine and validation boundary for bounded items, percentages, split counts, currencies, exchange rates, and decimal shapes.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/BackupCodec.kt` — Versioned deterministic local backup encoder/decoder with strict structural/text/decimal bounds, SHA-256 integrity checking, explicit persisted-record validation, strict UTF-8 byte decoding, and exact canonical stored-currency rejection.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/CsvReceiptFormatter.kt` — CSV receipt serializer with RFC-style quoting plus spreadsheet-formula neutralization for user-controlled text.
- `app/src/main/java/in/sanskar/spendcalc/domain/export/ReceiptFormatter.kt` — Human-readable text receipt serializer using deterministic money formatting.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/CalculationModels.kt` — Finance input/output data model, decimal/percentage/split/item bounds, validation-error types, and default calculator state.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedModels.kt` — Persisted-domain models for history, templates, preferences, and explicit backups, including release-independent backup schema metadata.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedNamePolicy.kt` — Shared saved-name policy for trimming user input, preserving exact accepted restored names, enforcing the 120-character UTF-16 bound, and avoiding split surrogate pairs.
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedRecordPolicy.kt` — Shared persisted-record envelope policy for bounded IDs/timestamps/canonical currencies/split/result values/names and duplicate-ID rejection.

### Platform/export/logging helpers

- `app/src/main/java/in/sanskar/spendcalc/platform/BackupFileIo.kt` — Android document-stream adapter for bounded explicit-backup reads/writes and strict UTF-8 decode delegation.
- `app/src/main/java/in/sanskar/spendcalc/platform/ExportManager.kt` — Android text/CSV/PDF export coordinator that creates files only inside the intended export cache and shares them through the configured `FileProvider`.
- `app/src/main/java/in/sanskar/spendcalc/platform/PathSafety.kt` — Canonical-path containment helper preventing sibling-prefix/path-escape mistakes for export targets.
- `app/src/main/java/in/sanskar/spendcalc/platform/PdfReceiptExporter.kt` — Android PDF renderer with bounded page layout and Unicode-safe line truncation.
- `app/src/main/java/in/sanskar/spendcalc/platform/SafeLogger.kt` — Minimal diagnostic logging facade with deterministic sensitive-key redaction and no analytics/remote transport.

### Compose UI and presentation state

- `app/src/main/java/in/sanskar/spendcalc/ui/AppUiState.kt` — Top-level immutable UI state including calculator, history/templates, preferences, selected navigation destination, backup progress, transient feedback, and loading readiness.
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcApp.kt` — App navigation/scaffold, responsive navigation rail/bar choice, dialog/confirmation orchestration, About entry, backup launchers, transient messaging, and reduced-motion transition behavior.
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcViewModel.kt` — Lifecycle-aware presentation coordinator for calculation, persistence, history/templates, settings, backup/restore, retention, saved-name policy application, and transient feedback.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/AboutScreen.kt` — About/support/funding/version/privacy entry points and product credit surface.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/CalculatorScreen.kt` — Responsive calculator form, item editor, finance adjustments, result receipt, share/save/template actions, bounded saved-name dialogs, and validation/limit messaging.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/HistoryScreen.kt` — Searchable named history list with bounded Unicode-safe search input, delete/Undo/clear behavior, empty states, and formatted persisted results.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/OnboardingScreen.kt` — First-run local-first product introduction and onboarding completion action.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/SettingsScreen.kt` — Theme/accessibility/retention controls plus backup/restore/About/repository actions and backup-busy presentation.
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/TemplatesScreen.kt` — Reusable calculation-template list with load/delete actions and empty-state handling.
- `app/src/main/java/in/sanskar/spendcalc/ui/theme/Theme.kt` — Material 3 light/dark/system theme selection, large-text typography scaling, and app color scheme.

### Android resources

- `app/src/main/res/drawable/ic_launcher_foreground.xml` — Adaptive-launcher foreground artwork.
- `app/src/main/res/drawable/ic_nav_calculator.xml` — Calculator navigation icon vector.
- `app/src/main/res/drawable/ic_nav_history.xml` — History navigation icon vector.
- `app/src/main/res/drawable/ic_nav_settings.xml` — Settings navigation icon vector.
- `app/src/main/res/drawable/ic_nav_templates.xml` — Templates navigation icon vector.
- `app/src/main/res/drawable/ic_spendcalc_logo.xml` — Reusable SpendCalc logo vector used by branding surfaces.
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` — Adaptive launcher icon definition.
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` — Round adaptive launcher icon definition.
- `app/src/main/res/mipmap-hdpi/ic_launcher.webp` — hdpi raster launcher icon fallback.
- `app/src/main/res/mipmap-hdpi/ic_launcher_round.webp` — hdpi round raster launcher icon fallback.
- `app/src/main/res/mipmap-mdpi/ic_launcher.webp` — mdpi raster launcher icon fallback.
- `app/src/main/res/mipmap-mdpi/ic_launcher_round.webp` — mdpi round raster launcher icon fallback.
- `app/src/main/res/mipmap-xhdpi/ic_launcher.webp` — xhdpi raster launcher icon fallback.
- `app/src/main/res/mipmap-xhdpi/ic_launcher_round.webp` — xhdpi round raster launcher icon fallback.
- `app/src/main/res/mipmap-xxhdpi/ic_launcher.webp` — xxhdpi raster launcher icon fallback.
- `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp` — xxhdpi round raster launcher icon fallback.
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp` — xxxhdpi raster launcher icon fallback.
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp` — xxxhdpi round raster launcher icon fallback.
- `app/src/main/res/values/colors.xml` — XML color resources required by splash/launcher theme setup.
- `app/src/main/res/values/strings.xml` — Default product/navigation/calculator/template/general strings and accessibility-facing text.
- `app/src/main/res/values/strings_about.xml` — About/support/funding/version/privacy strings.
- `app/src/main/res/values/strings_feedback.xml` — User-facing result/error/Undo/backup status messages.
- `app/src/main/res/values/strings_history.xml` — History search/list/delete/clear/retention strings.
- `app/src/main/res/values/strings_limits.xml` — User-facing bounded-input/limit explanatory strings.
- `app/src/main/res/values/strings_onboarding.xml` — First-run onboarding copy.
- `app/src/main/res/values/strings_settings.xml` — Settings, theme/accessibility, backup/restore, and retention labels/descriptions.
- `app/src/main/res/values/themes.xml` — Base Material splash/application themes including the AndroidX starting-window splash configuration.
- `app/src/main/res/xml/backup_rules.xml` — Android 12+ data-extraction rules for system-managed cloud backup/device transfer.
- `app/src/main/res/xml/data_extraction_rules.xml` — Android data-extraction configuration matching privacy documentation and local persistence policy.
- `app/src/main/res/xml/file_paths.xml` — FileProvider path restriction exposing only the private `exports/` cache subdirectory.

### JVM unit, regression, and deterministic fuzz tests

- `app/src/test/java/in/sanskar/spendcalc/data/HistoryRepositoryTest.kt` — History repository behavior, normalization, retention, restore, invalid persisted-input rejection, and replace-all atomicity preconditions.
- `app/src/test/java/in/sanskar/spendcalc/data/HistoryRestoreRepositoryTest.kt` — Focused restore mapping coverage for preserving exact accepted history names and persisted values.
- `app/src/test/java/in/sanskar/spendcalc/data/RepositoryDuplicateIdTest.kt` — History/template replacement regression proving duplicate IDs are rejected before DAO replacement and existing data remains intact.
- `app/src/test/java/in/sanskar/spendcalc/data/TemplateRepositoryTest.kt` — Template repository save/restore/delete, persisted-record bounds, finance validation, name policy, and replacement prevalidation coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/CalculatorEngineTest.kt` — Deterministic finance and validation regressions for monetary arithmetic, rounding, percentages, split/currency/exchange-rate rules, and decimal bounds.
- `app/src/test/java/in/sanskar/spendcalc/domain/CalculatorFuzzTest.kt` — Seeded property-style finance fuzz coverage for valid deterministic calculations and expected rejection of invalid generated amounts.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecFuzzTest.kt` — Seeded Unicode backup round trips and checksummed-body mutation regressions using only valid saved-record fixtures.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecPersistedPolicyTest.kt` — Backup encode/decode tests for shared persisted-record envelope rules and canonical currency requirements.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecSavedNamePolicyTest.kt` — Backup saved-name boundary, malformed Unicode, and round-trip regressions.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecTest.kt` — Primary versioned backup codec tests for round trips, checksums, record/text/decimal limits, unsupported schema handling, duplicate IDs, and compatibility behavior.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecValidationTest.kt` — Focused backup validation regressions for persisted-record rejection and strict document-byte decoding behavior.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/CsvReceiptFormatterFuzzTest.kt` — Seeded CSV quoting/formula-neutralization fuzz coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/CsvReceiptFormatterTest.kt` — CSV export regression coverage for delimiters, quotes, formatting, and formula-prefix neutralization.
- `app/src/test/java/in/sanskar/spendcalc/domain/export/ReceiptFormatterTest.kt` — Text receipt formatting coverage.
- `app/src/test/java/in/sanskar/spendcalc/domain/model/SavedNamePolicyTest.kt` — Shared saved-name normalization/truncation/malformed-surrogate and exact-restored-name tests.
- `app/src/test/java/in/sanskar/spendcalc/domain/model/SavedRecordPolicyTest.kt` — Persisted-record validation tests for identifiers, timestamps, canonical currencies, split/result bounds, names, and duplicate identifiers.
- `app/src/test/java/in/sanskar/spendcalc/platform/PathSafetyTest.kt` — Canonical export-path containment regressions, including sibling-prefix escape attempts.
- `app/src/test/java/in/sanskar/spendcalc/platform/PdfReceiptExporterTest.kt` — Pure line-truncation regressions proving PDF text helpers never split surrogate pairs at the ellipsis boundary.
- `app/src/test/java/in/sanskar/spendcalc/platform/SafeLoggerTest.kt` — Sensitive-key redaction and locale-independence regressions.
- `app/src/test/java/in/sanskar/spendcalc/ui/AppUiStateTest.kt` — Presentation-state regressions for result/feedback state behavior.

### Repository verification scripts

- `scripts/check_android_resources.py` — Validates default Android string references and duplicate names across Kotlin/XML sources before Gradle resource compilation.
- `scripts/check_android_security.py` — Enforces the no-Internet manifest baseline plus non-exported/path-restricted FileProvider policy.
- `scripts/check_documentation_coverage.py` — Compares `git ls-files` with this marked file index and rejects missing, stale, or duplicate tracked-file documentation entries.
- `scripts/check_format.py` — Enforces UTF-8 decoding, final newline, trailing-whitespace, and tab hygiene across tracked text files.
- `scripts/check_kotlin_namespace.py` — Validates Kotlin package declarations and rejects forbidden/reserved project namespace usage.
- `scripts/check_repository.py` — Enforces required repository/release/build files, README identity/contact metadata, local Markdown-link integrity, and current application release metadata across build/index/command documentation, including stale signed-APK example detection.
- `scripts/scan_secrets.py` — Scans tracked text for common token/private-key/signing-material patterns while allowing documented placeholder examples.

### Documentation

- `docs/README.md` — Documentation index, recommended reading paths, release metadata, and authority boundaries.
- `docs/accessibility.md` — Accessibility design decisions, semantics expectations, large-text/reduced-motion behavior, and manual TalkBack/layout review checklist.
- `docs/android-build-guide.md` — Complete Android executable guide for environment verification, Gradle builds, APK/AAB outputs, ADB install/inspection, signing tools, checksums, current release filenames, and troubleshooting.
- `docs/architecture.md` — System architecture, dependency direction, local-first boundaries, persistence/export/backup trust boundaries, and ViewModel/UI responsibilities.
- `docs/backup-restore.md` — User/developer-facing explicit backup format, restore replacement behavior, limits, validation, corruption detection, and compatibility rules.
- `docs/codebase-reference.md` — This exhaustive tracked-file index; maintained as a machine-enforced documentation invariant.
- `docs/command-reference.md` — Detailed explanations and examples for Git, Java, Gradle, ADB, Android SDK packaging/signing tools, and every repository guard command.
- `docs/design-system.md` — Material 3 visual/interaction conventions, navigation patterns, typography/accessibility behavior, and UI consistency guidance.
- `docs/development.md` — Contributor setup, architecture/change boundaries, persistence/testing rules, repository guards, and release-safe workflow.
- `docs/documentation-map.md` — Maps documentation ownership/authority by change type and defines anti-drift update requirements.
- `docs/features.md` — Current implemented user features, limits, local-first behavior, export/backup capabilities, and accessibility settings.
- `docs/github-maintenance.md` — Repository administration, dependency/security workflow upkeep, branch/release maintenance, and release-retarget guidance.
- `docs/logging.md` — Local logging/redaction contract and prohibited sensitive-data logging behavior.
- `docs/performance.md` — Bounded-work policy, performance-sensitive paths, and measurement/review guidance.
- `docs/persistence-invariants.md` — Canonical contract for persisted history/template records and their shared repository/backup validation envelope.
- `docs/privacy-backup.md` — Explicit-backup privacy model and its relationship to Android system-managed backup/device transfer.
- `docs/release-candidate-final-audit.md` — Source-level 2.0.12 release-candidate audit; explicitly not a substitute for external/manual release gates.
- `docs/release.md` — Exact-source release procedure, verification/signing/artifact requirements, and semantic-versioning discipline.
- `docs/security-backup.md` — Backup parser/threat model, structural limits, checksum semantics, strict decoding, and persistence-envelope validation.
- `docs/setup.md` — Minimal contributor environment/setup path, delegating detailed command semantics/build steps to the authoritative guides.
- `docs/testing.md` — JVM/fuzz/Android/repository-guard testing strategy and regression-placement policy.
- `docs/troubleshooting.md` — Focused resolution guidance for setup/build/test/export/backup/release failures, with links back to authoritative command/build docs.
- `docs/verification.md` — Authoritative blocking checklist separating automated PR checks, documentation, connected Android runtime checks, accessibility/privacy/security review, and distribution gates.

<!-- FILE-INDEX:END -->
