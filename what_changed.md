# SpendCalc — Work Continuity

## Current milestone

- Date: 2026-08-19
- Repository: `sanskarIN/spendcalc`
- Default branch: `main`
- Target: Android, Kotlin, Jetpack Compose, offline-first.
- Current implementation state: Phase 0 through Phase 5 features/documentation are substantially implemented; final Phase 6 verification is in progress.

## Source prompt analyzed

The repository is implemented against `15_spendcalc_master_prompt.md`. The required product is an offline expense calculator with precision-safe decimal arithmetic, itemized expenses, tax/discount/tip/service-charge/split calculations, manual currency conversion, reusable templates, receipt-style results, history with optional auto-delete, export architecture, accessibility, tests, polished settings/about UI, security/privacy documentation, CI, and release engineering.

## Completed work

### Repository/build foundation

- Added Gradle settings/root build configuration and Android app module.
- Configured Android API 26 minimum, API 35 target/compile SDK, Java 17 bytecode, Kotlin, Jetpack Compose, KSP, Room, DataStore, and Android test dependencies.
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

### Documentation

- Added comprehensive `README.md` with logo, feature overview, platform support, tech stack, setup, testing, release, architecture, security/privacy, accessibility, performance, contribution, support, BMC, MIT license, and `Made by the Sanskar`.
- Added `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SECURITY.md`, `SUPPORT.md`, `PRIVACY.md`, `CHANGELOG.md`, and `ROADMAP.md`.
- Added `docs/architecture.md`, `docs/setup.md`, `docs/development.md`, `docs/testing.md`, `docs/release.md`, `docs/troubleshooting.md`, `docs/accessibility.md`, and `docs/performance.md`.
- Added ADRs for BigDecimal finance arithmetic, local-first core behavior, and Room/DataStore persistence.
- Added editable SVG brand artwork and a verified-screenshot capture policy.

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

## Commands/checks run and results

- Confirmed repository exists, is public, default branch is `main`, and connected GitHub account has admin/push access.
- Direct local `git clone` from the execution container was attempted but failed because that container cannot resolve `github.com`; this is an execution-environment network limitation, not a repository result.
- GitHub commit status endpoint returned no legacy commit statuses for the latest commit; GitHub Actions uses check runs rather than those legacy statuses, so this did not verify the build.
- Source-level compile-risk review identified and fixed two issues before final CI verification:
  - changed settings card content receiver from invalid `Column` receiver to `ColumnScope`;
  - safely finished nullable PDF pages;
  - adapted the optional-argument history-save method to the no-argument UI callback;
  - added explicit Android test-core dependency.
- Final clean Android build/test/lint verification is still pending through a pull-request-triggered Actions run so workflow runs can be inspected with the connected GitHub tool.

## Known limitations / not-yet-verified items

- The connected GitHub file/commit API does not expose a commit-author-email argument. Commits therefore use the authenticated GitHub identity. Documentation records `git config user.email "sanskarin@outlook.in"` for local contributions.
- A Gradle wrapper JAR is not committed; documented local setup uses Gradle 8.9, while GitHub Actions explicitly installs Gradle 8.9 through `gradle/actions/setup-gradle`.
- Real Android screenshots are intentionally not fabricated. `docs/assets/screenshots/README.md` defines the release screenshot capture checklist; captures should be added only from a verified real build with fictional data.
- No production signing keys are committed. The release workflow builds an unsigned release candidate by design.
- Database schema version is 1, so there is no historical migration to test yet; future schema version changes must add explicit migrations and migration tests.

## Open issues

- Final CI verification and any resulting compile/lint/test fixes.
- Final repository audit after CI is green.
- Real release screenshots and production signing remain release/distribution tasks rather than source-code secrets.

## Next exact tasks

1. Create a final verification branch from current `main`.
2. Open a pull request to trigger CI/CodeQL/dependency-review workflows in a way inspectable by the connected GitHub workflow-run tools.
3. Inspect failed workflow jobs/logs if any, patch every reproducible code/configuration issue, and rerun failed jobs.
4. When checks are green, merge the verification PR.
5. Update this file with actual verification results and final commit/PR hashes.
6. Perform final repository tree/documentation audit and close any remaining blocker issue.

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
- Comprehensive repository documentation, testing baseline, CI, CodeQL, dependency review, Dependabot, issue templates, and release workflow.

## Recent meaningful commits

- `9dd55fd` — `test: add Android test core dependency`
- `320aa7e` — `docs: add complete SpendCalc README`
- `325660a` — `design: add editable SpendCalc logo artwork`
- `cece0be` — `docs: configure project funding links`
- `2cac309` — `docs: add pull request quality checklist`
- `d5aff753` — `ci: configure Dependabot updates`
- `752bf5d` — `ci: add release candidate build workflow`
- `d06fa42` — `ci: add dependency review workflow`
- `5405b51` — `ci: add CodeQL Java Kotlin analysis`
- `94710c2` — `ci: add Android build test lint security workflow`
- `06deab3` — `fix: align app navigation callbacks and error handling`
- `2be5f21` — `fix: safely finish nullable PDF page`
- `14ecb79` — `fix: use ColumnScope for settings card content`
- `332118d` — `feat: add Compose main activity`
- `729c7dc` — `feat: build responsive calculator and receipt UI`
- `c64a3d1` — `feat: implement app view model and calculator workflow`
- `1489737` — `feat: implement precision-safe calculator engine`
- `a2123ab` — `test: cover finance arithmetic and validation`
- `0ebaa9e` — `docs: add implementation handoff plan`
