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
- Requested Git email confirmed in GitHub commit metadata: `Sanskar <sanskarin@outlook.in>`.
- Pre-handoff head for this update: `24f0396e00b4fc1d1a30450ff21094b82aa786ef`.
- Current state: planned application features and the release-candidate source hardening are implemented on `complete/v1-finalization`. This continuation completed named history saves, direct label-search coverage, persistence/backup saved-name invariants, exact restore semantics, UTF-16-safe boundary handling, a fast Android resource-reference audit, additional CI/repository guards, and documentation reconciliation. Production merge/tagging still requires successful automated checks on the exact final commit and the documented manual Android/accessibility/export/backup/signing/screenshot gates.

## Continuation audit performed

This continuation re-inspected current GitHub source and PR state instead of treating an older handoff as proof of completion.

Findings and decisions:

- PR `#12` is the active release-candidate path.
- Connected GitHub inspection previously reported the PR open, non-draft, and mergeable; this must be rechecked again after this handoff commit before merge decisions.
- No open repository issues were returned by the connected issue search used during this continuation.
- No core `TODO`/`FIXME`/placeholder implementation result was found in the repository search used during the audit.
- History search already indexed labels, but the calculator previously saved every result under the generic `Calculation` label. That gap is now closed with optional named saves.
- Persistence previously had a mismatch with backup validation: direct repository calls could create names longer than the 120-character backup contract. The shared saved-name policy now closes that mismatch.
- A later audit found that restore/replace paths were re-normalizing accepted saved names. They now validate and preserve already-valid names exactly.
- A later Unicode audit found that plain `String.take(120)` can split a UTF-16 surrogate pair, such as an emoji at the length boundary, creating malformed text that the strict backup codec correctly rejects. The application now uses a shared surrogate-safe truncation policy for saved names and History search.
- The Android resources are intentionally split across multiple default `values/*.xml` files. An early suspicion that resource keys had been lost was disproven by inspecting those files; no unnecessary restoration commit was made.
- A later suspicion that the formatter imposed a 120-column Markdown/XML rule was also disproven. The existing formatter only checks UTF-8, final newline, trailing whitespace, and tabs. The attempted edit was rejected by stale-SHA protection before any commit, so no formatter-policy change exists or is needed.
- The connected execution container cannot resolve `github.com`, so a clean local Gradle dependency resolution/build cannot be used as release proof in this environment. GitHub Actions remains the authoritative automated Android/Gradle verification source.

## Work completed in this continuation

### Named history saves

- `Save result` now opens a dedicated Compose dialog instead of immediately writing a generic history entry.
- The dialog accepts an optional user-provided history label.
- Blank/whitespace-only user input still saves as the stable `Calculation` fallback.
- The dialog has a visible title, labeled input, explicit supporting guidance, a concise `Save` action, and a separate Cancel path.
- Temporary dialog input is cleared when the dialog is canceled/dismissed or successfully saved.
- `SpendCalcApp` wires the entered label through `SpendCalcViewModel.saveHistory(label)`.
- The template-name dialog follows the same saved-name boundary behavior.
- A distinct history-dialog `Save` resource avoids ambiguous UI-test targeting against the underlying `Save result` button.
- History-specific strings are grouped in `strings_history.xml` rather than leaving the new labels in the generic root string file.

### History search completion and bounded input

- Existing History filtering now has direct Compose regression coverage proving a saved label can be found and a non-matching record disappears.
- History search continues to match label, currency codes, subtotal/total, converted total, and per-person values.
- Interactive History search input is capped at 120 UTF-16 code units to keep repeated in-memory filtering bounded.
- The History UI tells users that the query is limited to 120 characters.
- History search uses the same surrogate-safe truncation helper as saved-name input so a pasted emoji cannot be cut in half at the boundary.
- Performance and testing documentation now records the History search budget and future Room/paging direction if profiling shows large-history pressure.

### Shared saved-name contract

- `MAX_SAVED_NAME_CHARS = 120` is the shared domain contract for history labels and template names.
- `BackupCodec` consumes the shared domain limit rather than maintaining an independent private saved-name limit.
- New history/template saves normalize at the repository boundary rather than relying on Compose or ViewModel callers.
- New user-entered names are trimmed, safely bounded, and assigned a stable fallback when blank.
- `SpendCalcViewModel` no longer performs premature name truncation before repository normalization.
- This fixes the earlier leading-whitespace edge case where a ViewModel-level `take(120)` could consume the budget before trimming.

### Exact restore semantics

- New-input normalization and accepted-record restoration are now separate behaviors.
- `HistoryRepository.restore/replaceAll` validates an accepted history label and preserves it exactly rather than silently trimming/re-normalizing it.
- `TemplateRepository.restore/replaceAll` does the same for template names.
- Valid intentional surrounding whitespace in an accepted backup record is therefore preserved on restore.
- Over-limit or blank direct restore values fail closed before DAO persistence rather than being silently rewritten.
- Repository tests cover exact whitespace preservation and over-limit rejection for both history and templates.

### UTF-16-safe saved-name policy

A new shared domain helper in `SavedNamePolicy.kt` now owns Unicode-safe saved-name behavior.

It provides:

- `truncateUtf16Safely(value, maxChars)`;
- `normalizeSavedName(value, fallback)`;
- `requireValidSavedName(value)`;
- `isValidSavedName(value)`;
- `isWellFormedUtf16(value)`.

Important behavior:

- A valid high-surrogate/low-surrogate pair is never split at the 120-character truncation boundary.
- A value that already ends with a high surrogate because of an earlier unsafe truncation is healed by dropping that incomplete trailing surrogate before persistence/UI retention.
- New saved-name normalization rejects malformed UTF-16 that is not merely the recoverable trailing-boundary case.
- Exact restore validation rejects malformed Unicode rather than mutating it.
- Compose history-label and template-name fields use this helper rather than plain `.take(120)`.
- History search uses the same helper.
- Backup encoding remains strict: malformed Unicode is not silently normalized into replacement characters.

### Unicode regression coverage

- Domain tests cover a valid emoji exactly crossing the saved-name boundary.
- Domain tests cover repair of a trailing high surrogate created by an earlier unsafe `take(120)` boundary.
- Domain tests cover fallback behavior, surrounding-whitespace preservation during validation, and malformed UTF-16 rejection.
- `CalculatorScreenTest` includes a Compose-level emoji-boundary regression and verifies the save callback receives well-formed truncated text.
- `BackupCodecSavedNamePolicyTest` proves a normalized Unicode-boundary history label remains exportable and round-trips exactly through backup encode/decode.
- Manual verification now explicitly includes a Unicode-heavy name near the boundary and successful backup/restore of the resulting record.

### Android string-resource guard

A new dependency-free `scripts/check_android_resources.py` guard now validates default string resources before the expensive Android build stages.

The guard:

- parses `app/src/main/res/values/*.xml`;
- collects default `<string>` and `<item type="string">` names;
- reports malformed XML and unnamed/default duplicate string resources;
- scans Kotlin under `app/src` for application `R.string.*` references while excluding `android.R.string.*`;
- scans main Android resource XML and `AndroidManifest.xml` for `@string/*` references;
- fails when an application string reference cannot resolve to a default resource.

Integration:

- Main CI runs the string-resource audit before Gradle tests/builds.
- Lightweight `Repository Audit` runs the same guard for faster feedback.
- `scripts/check_repository.py` now requires `scripts/check_android_resources.py` and the existing Android security audit script.
- The repository audit also requires release-critical verification/privacy/security documentation so these files cannot disappear silently.
- `docs/development.md`, `docs/testing.md`, README, changelog, and final audit document the guard and local command.

## Complete product implementation state

### Finance and validation

- Finance arithmetic uses `BigDecimal`; monetary calculations do not use `Float`/`Double`.
- Charge order remains explicit and deterministic: subtotal -> discount -> discounted base -> tax/tip/service charge -> total -> conversion -> split.
- Discount is limited to 0–100% so valid input cannot create a negative discounted base.
- Tax/tip/service-charge percentages use bounded supported ranges.
- Amount/exchange values use bounded precision, scale, integer digits, and text length.
- Scientific/exponent shapes that could expand pathologically into huge plain-decimal strings are rejected at supported boundaries.
- Split count is limited to `1..1,000,000`.
- Currency-code normalization uses `Locale.ROOT` and accepts normalized three-letter codes only.
- Calculator text/name/currency/split inputs are bounded before expensive work.
- Editable calculator work is capped at `MAX_EXPENSE_ITEMS = 100`; the Add action disables at the limit and the UI explains why.

### History, templates, preferences, and recovery

- Room-backed history and reusable templates are implemented.
- Users can assign optional bounded labels when saving calculations; blank input uses `Calculation`.
- History search covers labels, currencies, totals, converted totals, and per-person values.
- History search query length is bounded and Unicode-safe.
- Individual history deletion provides Snackbar Undo using exact record restoration.
- Individual template deletion provides Snackbar Undo using exact template restoration.
- Clear-all history is confirmation-protected.
- Optional 30-day/90-day history retention is implemented.
- New saved history/template names normalize at the repository boundary; accepted restore records validate and preserve exact valid names.
- DataStore persists theme, large text, reduced motion, retention, and onboarding preferences.
- Reduced-motion preference changes actual navigation behavior by removing transitions.

### Explicit local backup and restore

- `SpendCalcBackup`, `BackupCodec`, `BackupRepository`, bounded `BackupFileIo`, Room replacement APIs, DataStore replacement, Settings UI, and document-picker flows are connected.
- Backup includes history labels, templates, and preferences.
- Restore requires explicit replacement confirmation.
- Backup parsing is versioned and fail-closed for unsupported schemas/records.
- Bounds cover payload size, line/newline counts, record count, field sizes, text bytes, decimal text/shape, identifiers, saved names, timestamps, currencies, and split counts.
- The backup codec and persistence repositories share the same 120-character saved-name contract.
- Text fields use URL-safe Base64 so embedded tabs/newlines cannot change record boundaries.
- Text must round-trip through valid UTF-8; malformed Unicode/UTF-8 fails closed.
- SHA-256 provides accidental-corruption detection; it is explicitly documented as not encryption, authentication, or proof of authorship.
- Duplicate history/template identifiers are rejected.
- Template finance values are revalidated through `CalculatorEngine`.
- Room history/template replacement runs inside a transaction.
- Because Room and DataStore are separate stores, restore snapshots the old complete state and performs best-effort compensating rollback if the cross-store restore fails.
- Valid accepted saved names are preserved exactly during restore rather than silently normalized.
- Backup read/write runs on `Dispatchers.IO`; bounded encoding/decoding CPU work runs on `Dispatchers.Default`.
- The Settings screen shows real backup progress/busy state and disables duplicate backup actions until completion.

### Export and platform boundaries

- Plain-text receipt, CSV, and PDF export/share paths are implemented.
- CSV quoting and spreadsheet-formula-prefix neutralization are covered by tests.
- PDF receipts are generated locally with Android `PdfDocument`.
- FileProvider is non-exported and exposes only the private `cache/exports/` path.
- Canonical path containment prevents sibling-prefix bypass such as `exports-private`.
- CSV/PDF file creation runs on `Dispatchers.IO`.
- Backup destination content is generated after the user chooses a document destination rather than being held as fragile pending payload state.
- Coroutine cancellation is preserved through asynchronous backup/export error paths.

### UI, branding, accessibility, and privacy

- Jetpack Compose + Material 3 UI is implemented with reusable design tokens/components.
- Phone layout uses a scrolling single-column calculator; wide layouts use calculator/receipt columns.
- Light, dark, and system appearance modes are supported.
- Large-text preference increases core app typography while system font scaling continues to apply.
- Reduced-motion mode removes navigation transitions.
- Repository-owned vector drawables represent Calculator, History, Templates, and Settings.
- Visible navigation text supplies accessibility meaning; decorative vector descriptions are null to prevent duplicate screen-reader announcements.
- AndroidX SplashScreen is integrated with branded SpendCalc starting theme/artwork.
- Backup progress uses both a Material progress indicator and explanatory text.
- Validation includes visible explanatory text rather than color-only indication.
- Numeric/decimal fields request appropriate Android keyboards.
- Named-history dialog exposes title, input label, supporting length guidance, Save, and Cancel semantics.
- Onboarding, Settings, About, support links, funding link, license/version info, and `Made by the Sanskar` are implemented.
- Core workflows require no account and no Android Internet permission.
- Privacy documentation explicitly states history labels are local stored data and are included in explicit user-created backups.

## Tests implemented or strengthened

### JVM/domain/repository/security tests

- Finance arithmetic, discount/tax/tip/service, conversion, split, decimal precision, rounding, validation, locale normalization, and supported bounds.
- Deterministic seeded finance fuzz/regression coverage.
- History repository save/delete/restore/clear/retention mapping.
- History-label new-input trim/cap/default behavior.
- History exact valid restore and over-limit restore rejection.
- Template repository save/delete/exact-restore/replace mapping.
- Template-name new-input trim/cap/default behavior.
- Template exact valid restore and over-limit restore rejection.
- UTF-16-safe saved-name truncation and malformed-surrogate validation.
- Repair of a previously split trailing surrogate boundary.
- Unicode-boundary saved-label backup round trip.
- CSV quote/formula-prefix safety.
- Receipt formatter behavior.
- Backup Unicode/tab/newline round trip, checksum tamper detection, unsupported schema, duplicate IDs, malformed/truncated/oversized payloads, excessive line counts, invalid timestamps/currencies/decimals, exponent rejection, and deterministic backup fuzz coverage.
- Export canonical-path containment regressions.
- SafeLogger redaction, newline sanitization, and Turkish-locale key-normalization regression tests.

### Android/instrumentation/Compose tests

- Room history/template round trips.
- Transactional backup replacement for Room data.
- Compose calculator/receipt smoke coverage.
- Compose named-history save dialog/callback coverage.
- Compose named-history emoji-boundary coverage.
- Compose History saved-label filter coverage.
- Settings backup busy/progress state and disabled duplicate backup actions.
- Real-activity onboarding/calculation/named-save/history journey verifies both the saved label and amount.
- CI compiles the Android instrumentation suite using `assembleDebugAndroidTest`; actual connected-device execution remains a manual release gate.

### Fast repository guards

- Formatting/UTF-8/final-newline/tab/trailing-whitespace guard.
- Kotlin namespace/package guard.
- Android default string-resource reference/duplicate-name guard.
- Android manifest/FileProvider local-first security guard.
- Required-file and local Markdown-link audit.
- Common secret-pattern scan.

## CI, security, and repository automation

The main CI workflow now runs:

1. formatting guard;
2. Kotlin namespace/package guard;
3. Android string-resource reference/duplicate-name audit;
4. Android local-first manifest/FileProvider security audit;
5. repository metadata/required-file/local Markdown-link audit;
6. common secret-pattern scan;
7. JVM unit/fuzz/regression tests;
8. Android instrumentation-test compilation;
9. full Android lint across configured variants;
10. debug APK compilation;
11. release APK compilation.

Separate workflows cover:

- CodeQL Java/Kotlin analysis;
- pull-request Dependency Review;
- lightweight Repository Audit, including the string-resource guard;
- tag-triggered unsigned release-candidate artifact build.

Maintained major versions are used for checkout/setup-java/Gradle/CodeQL/artifact actions. CI/security/audit PR workflows use concurrency cancellation so superseded branch revisions do not consume runner capacity indefinitely.

## Documentation reconciled

Current documentation now reflects named history, search bounds, Unicode/restore semantics, backup behavior, resource auditing, and release verification:

- `README.md`
- `CHANGELOG.md`
- `ROADMAP.md`
- `PRIVACY.md`
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

Real release screenshots are intentionally not fabricated. Repository guidance requires screenshots captured from the final verified build with fictional data.

## Important implementation and guard files

- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/in/sanskar/spendcalc/domain/CalculatorEngine.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/export/BackupCodec.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedModels.kt`
- `app/src/main/java/in/sanskar/spendcalc/domain/model/SavedNamePolicy.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/BackupRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/HistoryRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/TemplateRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/data/SettingsRepository.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/AppUiState.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcViewModel.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/SpendCalcApp.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/CalculatorScreen.kt`
- `app/src/main/java/in/sanskar/spendcalc/ui/screens/HistoryScreen.kt`
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/CalculatorScreenTest.kt`
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/HistoryScreenTest.kt`
- `app/src/androidTest/java/in/sanskar/spendcalc/ui/MainActivityJourneyTest.kt`
- `app/src/test/java/in/sanskar/spendcalc/domain/model/SavedNamePolicyTest.kt`
- `app/src/test/java/in/sanskar/spendcalc/domain/export/BackupCodecSavedNamePolicyTest.kt`
- `scripts/check_android_resources.py`
- `scripts/check_android_security.py`
- `scripts/check_repository.py`
- `.github/workflows/ci.yml`
- `.github/workflows/repository-audit.yml`

## Meaningful commits added in this continuation

Named-history and persistence work:

- `b0a716f` — `refactor: centralize saved name length limit`
- `9cf8661` — `refactor: share saved name bound with backups`
- `8eb42ed` — `fix: enforce history label storage bound`
- `ae57a3a` — `fix: enforce template name storage bound`
- `8a22baa` — `test: cover bounded history label normalization`
- `fcd1786` — `test: cover bounded template name normalization`
- `393958e` — `feat: add named history save strings`
- `4aa3d72` — `feat: support named history saves`
- `afb73e5` — `test: cover named history save dialog`
- `ba42cdb` — `test: target named save action inside dialog`
- `78f4809` — `refactor: delegate saved name normalization to repositories`
- `a4073c0` — `docs: record named history save hardening`
- `39e48a2` — `docs: document named history save workflow`
- `048c28b` — `docs: expand saved-name regression coverage`
- `e87383f` — `test: update activity journey for named history save`
- `74e74c2` — `test: target named history text input semantically`
- `7ab0e9c` — `refactor: group named history strings`

Note on the earlier string experiment: `f05aa13` temporarily added a distinct history-dialog action string and `7c52961` removed that unused experiment. A proper distinct `save_history_confirm` action was subsequently reintroduced for the real dialog/test flow and is now grouped in `strings_history.xml` by `7ab0e9c`. History was not rewritten or force-pushed.

History-search completion:

- `1f8278d` — `perf: bound history search input`
- `755aa57` — `test: cover history label search filtering`
- `a7a4a5f` — `ux: explain history search input limit`
- `f4d7d87` — `docs: document bounded history search budget`
- `429664c` — `docs: record history search regression coverage`
- `44efcf0` — `docs: record bounded history search hardening`

Resource/CI guard work:

- `4a6fd8e` — `ci: add Android string resource reference audit`
- `3918e2b` — `ci: enforce Android string resource audit`
- `8cb3963` — `docs: add Android resource audit development guidance`
- `59421e7` — `ci: add resource guard to repository audit`
- `4765fed` — `ci: require Android resource audit script`
- `ce699c1` — `ci: require release verification documentation`

Exact-restore and Unicode hardening:

- `9ec075b` — `fix: preserve valid history labels on restore`
- `4582bac` — `fix: preserve valid template names on restore`
- `c52cc61` — `test: preserve exact valid history labels on restore`
- `2880667` — `test: preserve exact valid template names on restore`
- `614bf7b` — `fix: add Unicode-safe saved name policy`
- `07e726f` — `test: cover Unicode-safe saved name policy`
- `13c27d7` — `refactor: use shared saved name policy for history`
- `0187673` — `refactor: use shared saved name policy for templates`
- `5cfaf84` — `fix: heal truncated trailing surrogate in saved names`
- `bb2966d` — `test: cover repaired surrogate boundary truncation`
- `7fc8f60` — `fix: truncate saved names safely in Compose`
- `ba812e1` — `fix: truncate history search safely for Unicode`
- `7a71be4` — `test: round trip Unicode boundary saved label backup`
- `4720224` — `test: cover Unicode boundary in history save dialog`

Documentation/final audit hardening:

- `a35cf92` — `docs: document Unicode-safe saved name security`
- `eb87866` — `docs: clarify exact Unicode-safe backup restoration`
- `3f5a1ab` — `docs: record Unicode and resource audit regressions`
- `2639f2d` — `docs: record Unicode and resource audit hardening`
- `69d8dd6` — `docs: reconcile README with final hardening`
- `e9e6354` — `docs: add Unicode and resource release verification`
- `24f0396` — `docs: finalize Unicode and resource audit checklist`

Earlier finalization commits remain part of the same branch history, including template-delete Undo, backup busy state, calculator item budget, vector navigation icons, AndroidX splash integration, lint expansion, accessibility navigation semantics, and release documentation/verification work. Preserve this history rather than squashing it merely to reduce commit count unless repository policy explicitly requires otherwise.

## Verification truth at this handoff

- Before this final handoff update, the source/documentation head was `24f0396e00b4fc1d1a30450ff21094b82aa786ef`.
- Earlier exact heads in this continuation repeatedly registered all four PR automation families: CI, Dependency Review, CodeQL, and Repository Audit.
- At the most recently observed registered heads, CI and Dependency Review were pending while CodeQL and Repository Audit were queued. These states are neither failures nor successes.
- Workflow concurrency cancels superseded PR revisions, so old runs are not proof for the newest head.
- The handoff commit itself creates a newer branch head. Fetch workflow state for that exact head after this update before any merge/release decision.
- No local Gradle build result is claimed because the connected execution container cannot resolve `github.com` for a clean dependency checkout/resolution.
- Git commit metadata observed through GitHub uses the requested `sanskarin@outlook.in` address.
- No queued/pending workflow is described as green.
- Do not describe `v1.0.0` as verified until the exact final commit satisfies `docs/verification.md`.

## Remaining external/manual release gates

These are real verification/distribution tasks and must not be faked in source control:

- successful CI on the exact final PR revision;
- successful CodeQL on the exact final PR revision;
- successful Dependency Review on the exact final PR revision;
- successful Repository Audit on the exact final PR revision;
- `connectedDebugAndroidTest` on a representative emulator/device;
- manual verification that a named history entry saves and is searchable by that exact label;
- manual verification of a Unicode-heavy saved name at the 120-character boundary and successful backup/restore of that record;
- manual verification that bounded History search does not split a surrogate pair;
- manual TalkBack and large-system-font review, including the named-history dialog;
- manual light/dark/system/reduced-motion review;
- manual phone and tablet/wide-layout review;
- manual launch-splash review on representative supported Android versions;
- manual text/CSV/PDF Android share-flow checks;
- manual backup create/read/progress/restore confirmation and restored-data checks;
- real release screenshots captured from the verified build with fictional data;
- production signing with secrets kept outside source control;
- signed artifact verification and production tag/release only after every blocking gate passes.

## Database/migration state

- Room database version: 1.
- No historical public production schema exists yet, so there is no legitimate v1->v2 migration to test yet.
- Destructive migration fallback is intentionally not the default strategy.
- Any future schema version 2 change must add an explicit migration and migration test.

## Next exact actions

1. Treat the commit containing this handoff update as the new candidate head and stop speculative feature churn.
2. Re-fetch PR `#12` state and CI/CodeQL/Dependency Review/Repository Audit for that exact head.
3. If a workflow fails, inspect the concrete failing job/log and make the smallest regression-tested fix; then this handoff and exact-head verification must be refreshed again.
4. If automated checks succeed, execute the manual Android/accessibility/export/backup/Unicode checks in `docs/verification.md` and `docs/testing.md`.
5. Merge PR `#12` to `main` only after branch protection/repository policy permits and all required automated release gates are green.
6. Capture real screenshots, sign the production artifact outside source control, verify the signed artifact, and tag `v1.0.0` only after every blocking gate passes.

## Continuation rule

While PR `#12` is open, continue from `complete/v1-finalization`. After it is merged, continue from `main`. Do not use an older handoff or an older successful run as proof that the newest head is complete. Inspect current source and exact-head check results first. Keep future fixes small, meaningful, tested, documented, and reflected here.
