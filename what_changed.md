# SpendCalc — Work Continuity

## Current milestone

- Date: 2026-08-19
- Repository: `sanskarIN/spendcalc`
- Default branch: `main`
- Active branch: `complete/v1-finalization`
- Active pull request: `#12` — `fix: complete SpendCalc release implementation`
- Master specification: `15_spendcalc_master_prompt.md`
- Target release: `1.0.0`
- Platform: Android API 26+, Kotlin + Jetpack Compose, local/offline-first.
- License: MIT.
- Required product credit: `Made by the Sanskar`.
- Requested Git email confirmed in GitHub commit metadata: `sanskarin@outlook.in`.
- Current state: the planned source implementation, hardening, tests, automation, and repository documentation are implemented on the release-candidate branch. Production tagging still requires successful automated checks for the exact final commit plus the documented Android device/accessibility/signing/screenshot release gates.

## Continuation audit performed

This continuation did not trust earlier completion claims blindly. It inspected the current branch, PR state, workflows, open issues, TODO/placeholder search, relevant source files, tests, and the uploaded master specification before continuing.

Findings:

- PR `#12` remained open and mergeable.
- No open repository issues were returned by the connected GitHub issue search.
- No core `TODO`/`FIXME`/placeholder implementation result was found in the repository search used during the audit.
- GitHub-hosted CI/CodeQL/dependency-review/repository-audit runs for earlier heads remained queued/pending rather than reporting failures; those superseded runs are not evidence for the final head.
- The connected execution container cannot resolve `github.com`, so full local Android dependency resolution/build is unavailable there. GitHub Actions remains the clean Android/Gradle authority.

## Complete product implementation state

### Finance and validation

- Finance arithmetic uses `BigDecimal`; no monetary `Float`/`Double` calculation path is used.
- Charge order remains explicit and deterministic: subtotal -> discount -> discounted base -> tax/tip/service charge -> total -> conversion -> split.
- Discount is limited to 0–100% so valid input cannot create a negative discounted base.
- Tax/tip/service-charge percentages use bounded supported ranges.
- Amount/exchange values use bounded precision, scale, integer digits, and text length.
- Scientific/exponent shapes that could expand pathologically into huge plain-decimal strings are rejected by supported input/backup boundaries.
- Split count is limited to `1..1,000,000`.
- Currency-code normalization uses `Locale.ROOT` and accepts normalized three-letter codes only.
- Calculator text/name/currency/split inputs are bounded before expensive work.
- The editable calculator now has a shared `MAX_EXPENSE_ITEMS = 100` UI/performance budget. The Add item action disables at the limit and explains why.

### History, templates, preferences, and recovery

- Room-backed history and reusable templates are implemented.
- History supports local search across labels, currencies, totals, converted totals, and per-person values.
- Individual history deletion provides Snackbar Undo using exact record restoration.
- Individual template deletion now also provides Snackbar Undo using exact template restoration.
- Clear-all history remains confirmation-protected.
- Optional 30-day/90-day history retention is implemented.
- DataStore persists theme, large text, reduced motion, retention, and onboarding preferences.
- Reduced-motion preference changes actual navigation behavior by removing transitions.

### Explicit local backup and restore

- `SpendCalcBackup`, `BackupCodec`, `BackupRepository`, bounded `BackupFileIo`, Room replacement APIs, DataStore replacement, Settings UI, and document picker flows are fully connected.
- Backup includes history, templates, and preferences.
- Restore requires explicit replacement confirmation.
- Backup parsing is versioned and fail-closed for unsupported schemas/records.
- Bounds cover payload size, newline/line counts, record count, field sizes, text bytes, decimal text/shape, identifiers, timestamps, currencies, and split counts.
- URL-safe Base64 text fields protect record boundaries.
- SHA-256 detects accidental corruption; documentation explicitly states it is not encryption/authenticity.
- Duplicate history/template identifiers are rejected.
- Template finance values are revalidated through `CalculatorEngine`.
- Room history/template replacement occurs inside one transaction.
- Because Room and DataStore are separate storage engines, restore snapshots the old complete state and performs compensating rollback if a multi-store restore fails.
- Backup read/write runs off the main thread.
- The Settings screen now shows a real progress indicator/text while backup read/write/restore work is active and disables duplicate backup actions until completion.

### Export and platform boundaries

- Plain-text receipt, CSV, and PDF export/share paths are implemented.
- CSV quoting and spreadsheet-formula-prefix neutralization are covered by tests.
- PDF receipts are generated locally with Android `PdfDocument`.
- FileProvider is non-exported and exposes only the private `cache/exports/` path.
- Canonical path containment prevents sibling-prefix bypass such as `exports-private`.
- CSV/PDF file creation runs on `Dispatchers.IO`.
- Backup destination content is generated after the user chooses a document destination rather than being held as fragile pending payload state.
- Coroutine cancellation is preserved through async backup/export error paths.

### UI, branding, and accessibility

- Jetpack Compose + Material 3 UI is implemented with reusable design tokens/components.
- Phone layout uses a scrolling single-column calculator; wide layouts use calculator/receipt columns.
- Light, dark, and system appearance are supported.
- Large-text preference increases core app typography while system font scaling continues to apply.
- Reduced-motion mode removes navigation transitions.
- Primary navigation no longer uses temporary `= / H / T / S` glyphs. Repository-owned vector drawables now represent Calculator, History, Templates, and Settings.
- Visible navigation text supplies accessibility meaning; decorative vector descriptions are null to prevent duplicate screen-reader announcements.
- AndroidX SplashScreen compatibility is integrated with a branded SpendCalc starting theme, brand background, and app icon before handing off to the Compose theme.
- Backup progress uses both a Material progress indicator and explanatory text rather than visual-only state.
- Validation includes visible explanatory text rather than color-only indication.
- Numeric/decimal text fields request appropriate Android keyboards.
- Onboarding, Settings, About, support links, funding link, license/version info, and `Made by the Sanskar` are implemented.

## Tests implemented or strengthened

### JVM/domain/repository/security tests

- Deterministic finance arithmetic, discount/tax/tip/service, conversion, split, decimal precision, and rounding.
- Discount cap, negative values, malformed currencies, exchange-rate/split bounds, decimal-shape bounds, and locale normalization.
- Deterministic seeded finance fuzz/regression coverage.
- History repository save/delete/restore/clear/retention mapping.
- Template repository save/delete/exact-restore/replace mapping.
- CSV quote/formula-prefix safety.
- Receipt formatter behavior.
- Backup Unicode/tab/newline round-trip, checksum tamper detection, unsupported schema, duplicate IDs, malformed/truncated/oversized payloads, excessive line counts, invalid timestamps/currencies/decimals, exponent rejection, and deterministic backup fuzz coverage.
- Export canonical-path containment regression tests.
- SafeLogger redaction, newline sanitization, and Turkish-locale key-normalization regression tests.

### Android/instrumentation tests

- Room history/template round trips.
- Transactional backup replacement for Room data.
- Compose calculator/receipt smoke coverage.
- Settings backup busy/progress state and disabled duplicate backup actions.
- Real-activity onboarding/calculation/save/history journey.
- CI compiles the Android instrumentation suite using `assembleDebugAndroidTest`; actual connected-device execution remains a manual release gate.

## CI, security, and repository automation

The main CI workflow now runs:

1. formatting guard;
2. Kotlin namespace/package guard;
3. repository metadata/local Markdown-link audit;
4. common secret-pattern scan;
5. JVM unit/fuzz/regression tests;
6. Android instrumentation-test compilation;
7. full Android lint across configured variants;
8. debug APK compilation;
9. release APK compilation.

Separate workflows cover:

- CodeQL Java/Kotlin analysis;
- pull-request Dependency Review;
- lightweight Repository Audit;
- tag-triggered unsigned release-candidate artifact build.

Maintained major versions are used for checkout/setup-java/Gradle/CodeQL/artifact actions. CI/security/audit PR workflows use concurrency cancellation so superseded branch revisions do not consume runner capacity indefinitely.

## Documentation reconciled

The repository documentation now reflects the actual branch implementation, including:

- `README.md`
- `CHANGELOG.md`
- `ROADMAP.md`
- `docs/development.md`
- `docs/testing.md`
- `docs/release.md`
- `docs/accessibility.md`
- `docs/design-system.md`
- `docs/performance.md`
- `docs/backup-restore.md`
- `docs/security-backup.md`
- `docs/privacy-backup.md`
- `docs/verification.md`
- `docs/release-candidate-final-audit.md`
- `what_changed.md`

The README links to the actual backup ADR filename `docs/adr/0004-versioned-local-backup.md`. Real screenshots are intentionally not fabricated; repository guidance requires captures from a verified build using fictional data.

## Important implementation files

- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/in/sanskar/spendcalc/MainActivity.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/CalculatorEngine.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/export/BackupCodec.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedModels.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/BackupRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/HistoryRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/TemplateRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/SettingsRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/AppUiState.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcViewModel.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcApp.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/CalculatorScreen.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/HistoryScreen.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/SettingsScreen.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/BackupFileIo.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/ExportManager.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/PathSafety.kt`
- `app/src/main/java/in/sanskar/spendcalc/platform/SafeLogger.kt`
- `app/src/main/res/drawable/ic_nav_calculator.xml`
- `app/src/main/res/drawable/ic_nav_history.xml`
- `app/src/main/res/drawable/ic_nav_templates.xml`
- `app/src/main/res/drawable/ic_nav_settings.xml`
- `app/src/main/res/values/themes.xml`

## Verification truth

- PR `#12` is the active release-candidate verification path and remained mergeable during this continuation.
- Commit-graph checks confirmed critical workflow, backup, input-boundary, instrumentation-compilation, path-containment, background-I/O, logger, UX, and documentation changes are on the active branch rather than detached work.
- Git commit metadata observed through GitHub uses `Sanskar <sanskarin@outlook.in>`.
- No successful GitHub Actions result is inferred from `queued` or `pending` state.
- The final source branch should be frozen while the exact final head is checked; any later fix creates a new head and requires fresh verification.
- Do not create or describe `v1.0.0` as verified until the exact final commit meets `docs/verification.md`.

## Remaining external/manual release gates

These are release/distribution verification tasks and must not be faked in source control:

- successful CI, CodeQL, Dependency Review, and Repository Audit on the exact final PR revision;
- `connectedDebugAndroidTest` on a representative emulator/device;
- manual TalkBack and large-system-font review;
- manual light/dark/system/reduced-motion review;
- manual phone and tablet/wide-layout review;
- manual launch-splash review on representative supported Android versions;
- manual text/CSV/PDF Android share flow checks;
- manual backup create/read/progress/restore confirmation and restored-data checks;
- real release screenshots captured from the verified build with fictional data;
- production signing with secrets kept outside source control;
- signed artifact verification and production tag/release after every blocking gate passes.

## Database/migration state

- Room database version: 1.
- No historical public production schema exists yet, so there is no legitimate v1->v2 migration to test yet.
- Destructive migration fallback is intentionally not the default strategy.
- Any future schema version 2 change must add an explicit migration and migration test.

## Recent meaningful commits from this continuation

- `e3a8b21` — `feat: support restoring deleted templates`
- `f4ba51a` — `test: cover template delete undo restore`
- `82fe405` — `refactor: distinguish template deletion feedback`
- `66bb95c` — `feat: add undo state for template deletion`
- `aaebb9d` — `feat: add template deletion undo feedback`
- `8f940a1` — `feat: expose template deletion undo action`
- `7f11588` — `feat: add backup progress messaging`
- `12332e9` — `feat: show and gate backup operation progress`
- `ceb8b86` — `feat: surface backup busy state across app shell`
- `b6412b7` — `test: cover backup busy state in settings`
- `e094c4c` — `fix: provide theme parameters in settings UI test`
- `9a16c55` — `perf: define bounded calculator item budget`
- `1fbe915` — `perf: enforce shared calculator item budget`
- `58fd7c2` — `feat: add calculator item limit guidance`
- `f9c1588` — `perf: expose calculator item budget in UI`
- `01d9bce` — `design: add calculator navigation icon`
- `5161e8c` — `design: add history navigation icon`
- `efef003` — `design: add templates navigation icon`
- `c60a2d1` — `design: add settings navigation icon`
- `6b515f6` — `design: use vector icons in primary navigation`
- `ee043c8` — `build: add AndroidX splash screen support`
- `f4129a1` — `design: add splash brand color resource`
- `76349d6` — `design: add branded starting splash theme`
- `d373d75` — `design: apply starting theme to launcher activity`
- `1157143` — `feat: install AndroidX launch splash screen`
- `4f59c37` — `ci: lint all Android variants`
- `efd1faa` — `a11y: avoid duplicate navigation icon announcements`
- `501fdac` — `docs: document calculator and backup performance budgets`
- `803a1ad` — `docs: record final UX and release hardening`
- `ce2582c` — `docs: reconcile roadmap with finalization work`
- `1722dc1` — `docs: refresh README for final UX hardening`
- `2515cdd` — `docs: expand final test coverage guidance`
- `b7747a0` — `docs: document navigation and backup accessibility`
- `c0cbcf4` — `docs: document final navigation and launch design`
- `d4a4946` — `docs: expand final release verification checklist`
- `67d506a` — `docs: align development checks with final CI`
- `eb576b9` — `docs: align release checklist with final quality gates`
- `27c5a17` — `docs: complete final source audit checklist`

## Next exact actions

1. Treat the commit containing this handoff update as the new candidate head and do not add speculative feature churn.
2. Inspect PR `#12` and fetch CI/CodeQL/Dependency Review/Repository Audit for that exact head.
3. If a workflow fails, fetch the exact job log, add a regression test where appropriate, fix the concrete defect, update this handoff, and verify the new head.
4. If automated checks succeed, perform the manual Android/accessibility/export/backup checks in `docs/verification.md`.
5. Merge PR `#12` to `main` with normal merge semantics so the meaningful atomic commit history is preserved when repository policy allows it.
6. Capture real screenshots, sign the production artifact outside source control, and tag `v1.0.0` only after all documented release-blocking gates pass.

## Continuation rule

While PR `#12` is open, continue from `complete/v1-finalization`. After it is merged, continue from `main`. Do not use an older handoff as proof that a feature or verification gate is complete; inspect the current source and current check results first. Keep future changes small, meaningful, tested, and reflected here.
