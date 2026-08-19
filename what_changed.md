# SpendCalc — Work Continuity

## Current milestone

- Date: 2026-08-19
- Repository: `sanskarIN/spendcalc`
- Default branch: `main`
- Active branch: `complete/v1-finalization`
- Active pull request: `#12` — `fix: complete SpendCalc release implementation`
- Master specification: `15_spendcalc_master_prompt.md`
- Target application release: `2.0.12`
- Android `versionName`: `2.0.12`
- Android `versionCode`: `20012`
- Room database version: `1`
- Explicit backup schema version: `1`
- Platform: Android API 26+, Kotlin + Jetpack Compose, local/offline-first.
- License: MIT.
- Required product credit: `Made by the Sanskar`.
- Requested Git email observed in GitHub commit metadata: `Sanskar <sanskarin@outlook.in>`.
- Pre-handoff head for this 2.0.12 continuation: `c7c376ce98888531447f4b2498f609bb7a976313`.
- Pull request state immediately before this handoff commit: open, non-draft, mergeable.
- Source state: planned product implementation, persistence/export hardening, platform-security fixes, regression suite, repository automation, exhaustive file documentation, release documentation, and 2.0.12 application metadata are complete at source level on `complete/v1-finalization`.
- Release state: do **not** tag or describe `v2.0.12` as verified until exact-final-head CI, CodeQL, Dependency Review, and Repository Audit have acceptable successful conclusions and the manual Android/accessibility/export/backup/signing/screenshot gates in `docs/verification.md` are complete.

## 2.0.12 versioning decision

The user requested this continuation to be prepared as version `2.0.12`.

Application/distribution metadata now uses:

```text
versionName = 2.0.12
versionCode = 20012
```

This release-number change intentionally does **not** manufacture a persistence migration:

- Room database version remains `1` because the database schema did not change merely because the application version changed.
- Explicit backup schema version remains `1` because the serialized backup contract did not require an incompatible schema revision merely because the application version changed.
- No fake Room `1 -> 2` migration was added.
- No fake backup schema `1 -> 2` transformation was added.
- Future Room or backup schema changes must be driven by actual compatibility requirements and receive their own migrations/tests/documentation.

`versionCode = 20012` provides a monotonic Android distribution code corresponding to this requested application release target.

## Exact workflow status before this handoff

Immediately before the final `what_changed.md` commit, exact branch head `c7c376ce98888531447f4b2498f609bb7a976313` registered all four required workflow families:

- CI — `pending`;
- CodeQL — `pending`;
- Dependency Review — `pending`;
- Repository Audit — `pending`.

Those runs are expected to be superseded by this handoff commit because workflow concurrency cancels older PR revisions. Pending/queued/cancelled/superseded states are not represented as successful verification.

After this file is committed, re-fetch PR #12 and all four workflow families for the new exact head. Never use an older green result as proof for a newer commit.

## 2.0.12 continuation completed

### Android release metadata

`app/build.gradle.kts` now sets:

- `versionName = "2.0.12"`;
- `versionCode = 20012`.

No finance, Room schema, backup schema, package name, API-level, signing, or application-ID behavior was changed solely to perform the release-number retarget.

### Changelog

`CHANGELOG.md` now:

- identifies `[2.0.12] - Planned` as the target release;
- records the 2.0.12/20012 application metadata;
- explicitly states that Room database and backup schema versions remain independent compatibility dimensions;
- retains the completed persistence, backup, UTF-8, canonical-currency, Unicode-safe PDF, CI, documentation, and security changes;
- does not falsely claim the release has passed exact-head automation or manual device/signing gates.

### Roadmap

`ROADMAP.md` now:

- targets `2.0.12` for current release engineering;
- marks the application version metadata work complete;
- keeps exact-head workflow/device/accessibility/export/backup/signing/screenshot gates open;
- requires `v2.0.12` only after all blockers pass;
- keeps possible receipt notes/categories as post-2.0.12 enhancement work rather than inventing release blockers;
- explicitly keeps Room/backup compatibility versions independent from the app release number.

### Release guide

`docs/release.md` now:

- defines the active release as 2.0.12 / Android versionCode 20012;
- requires exact metadata verification before release;
- keeps source completeness, automated exact-head verification, manual Android verification, and distribution evidence as separate proof classes;
- uses the `v2.0.12` tag procedure;
- requires the signed artifact to report 2.0.12 in About;
- explains that application versioning does not automatically require Room/backup schema changes.

### Verification checklist

`docs/verification.md` now:

- names 2.0.12/20012 as the current target;
- checks app version metadata explicitly;
- checks that Room/backup schema versions changed only for real compatibility reasons;
- preserves all exact-head CI/CodeQL/Dependency Review/Repository Audit gates;
- preserves connected-device instrumentation gates;
- preserves TalkBack, large-font, responsive-layout, theme, reduced-motion, export/share, backup/restore, offline, screenshot, signing, and artifact checks;
- explicitly includes malformed UTF-8 backup rejection, checksum-valid noncanonical currency rejection, and long-Unicode PDF truncation review.

### Final release-candidate source audit

`docs/release-candidate-final-audit.md` now:

- is explicitly the `SpendCalc 2.0.12 Release-Candidate Final Audit`;
- records 2.0.12/20012 source metadata as complete;
- records the deliberate non-change of Room database/backup schema versions;
- includes the previously completed strict UTF-8, canonical persisted-currency, persistence, Unicode-safe PDF, export-path, accessibility, and regression hardening;
- continues to distinguish source audit completion from pending runtime/release evidence.

### Documentation source-of-truth map

`docs/documentation-map.md` now:

- refers to the current 2.0.12 candidate;
- adds explicit release-status/version-metadata documentation rules;
- requires changelog, roadmap, release guide, verification, release-candidate audit, and handoff reconciliation for release-version changes;
- requires compatibility versions to remain independent unless an actual schema contract changes.

### Exhaustive codebase reference

`docs/codebase-reference.md` remains the exhaustive tracked-file ownership/reference document enforced by CI.

Its descriptions were reconciled for 2.0.12 and the final hardening state, including:

- `app/build.gradle.kts` — 2.0.12/versionCode 20012 metadata;
- `BackupCodec.kt` — exact canonical persisted-currency validation;
- `BackupFileIo.kt` — strict UTF-8 malformed/unmappable byte rejection;
- `PdfReceiptExporter.kt` — Unicode-safe ellipsis-boundary truncation;
- `BackupCodecPersistedPolicyTest.kt` — checksum-valid noncanonical decode rejection;
- `PathSafetyTest.kt` — consolidated platform regressions for path containment, strict backup decoding, and PDF Unicode truncation;
- `docs/release-candidate-final-audit.md` — current 2.0.12 source-level audit.

No tracked file was added merely for the version retarget, so the file-index set remains structurally unchanged. The exact tracked-file equality result must still come from the final-head documentation coverage workflow.

### GitHub maintenance documentation

`docs/github-maintenance.md` now uses:

- `2.0.12` — current verified-production-release target milestone;
- `2.1.0` — first post-2.0.12 feature milestone;
- `maintenance` — optional recurring maintenance milestone.

It explicitly warns that app release milestones are not Room/backup schema numbers.

### Compatibility handoff

`what_changed_final.md` now points readers to this canonical handoff and identifies 2.0.12/versionCode 20012 as the active application target.

`what_changed_latest.md` remains a compatibility pointer only and already contains no active 1.0.0 release target.

### README reconciliation

`README.md` was re-read during this continuation. It does not contain an active `v1.0.0` release target requiring replacement. Its feature/build/release-boundary content remains consistent with the current implementation and deeper 2.0.12 release documentation.

Historical version references in old commits or explicitly historical handoff descriptions are not rewritten merely to erase history. Only current authoritative release-target language is changed.

## Complete implementation state retained for 2.0.12

### Finance

- `BigDecimal` finance arithmetic.
- Deterministic calculation order.
- Explicit rounding policy.
- Itemized expenses.
- Discount, tax, tip, and service charge.
- Manual currency conversion.
- Split bill.
- Discount limited to 0–100%.
- Bounded percentages, amounts, exchange rates, precision, scale, integer digits, split count, and editable item count.
- `MAX_EXPENSE_ITEMS = 100` eager-editor budget.

### History

- Room-backed local history.
- Optional named saves.
- Stable `Calculation` fallback.
- UTF-16-safe 120-character label policy.
- Label/currency/total/per-person search.
- Bounded 120-character Unicode-safe search query.
- Individual delete + Undo.
- Clear-all confirmation.
- Optional 30-day/90-day retention.
- Repository structural validation before persistence.
- Duplicate-ID replacement rejection.

### Templates

- Room-backed saved settings.
- Stable `Template` fallback.
- UTF-16-safe 120-character name policy.
- Visible name-length guidance.
- Distinct `Save` dialog confirmation.
- Load/delete + Undo.
- Repository finance validation through `CalculatorEngine`.
- Structural envelope validation before persistence.
- Duplicate-ID replacement rejection.

### Preferences

- DataStore-backed theme mode.
- Large text.
- Reduced motion.
- History retention.
- Onboarding completion.
- Corrupted preferences recover to safe defaults without deleting Room history/templates.

### Backup/restore

- User-driven local backup using Android document APIs.
- History/templates/preferences included.
- History labels included.
- Versioned bounded format.
- URL-safe Base64 text fields.
- Strict UTF-8 document decoding with malformed/unmappable byte rejection.
- Valid UTF-8/Unicode requirement for decoded/exported text.
- SHA-256 accidental-corruption detection.
- Strict record/schema/checksum/size/line/field/decimal/name/ID/timestamp/canonical-currency/split validation.
- Duplicate-ID rejection.
- Shared persisted-record policy with repositories.
- Template finance revalidation.
- Room transactional snapshot/replacement.
- Batch DAO inserts.
- Cross-store compensating DataStore rollback strategy.
- Modal busy/progress state.
- Duplicate backup actions disabled while busy.
- File I/O on `Dispatchers.IO`.
- Bounded encode/decode CPU work on `Dispatchers.Default`.

### Export/platform security

- Plain-text receipt sharing.
- CSV export with quote/formula neutralization.
- Offline PDF receipt generation.
- Unicode-safe PDF line truncation that cannot leave a dangling surrogate at the ellipsis boundary.
- Private cache export directory.
- Non-exported `FileProvider`.
- Temporary URI read permission.
- Canonical path containment.
- No Android `INTERNET` permission for core app.
- Structured logging redaction.

### UI/accessibility/branding

- Jetpack Compose + Material 3.
- Responsive phone/tablet calculator layout.
- Light/dark/system themes.
- Large text.
- Reduced motion.
- Branded AndroidX splash.
- Repository-owned primary navigation vectors.
- Non-duplicated navigation screen-reader semantics.
- Visible validation text.
- Numeric keyboards.
- Named-history save dialog with guidance/Save/Cancel.
- Template save dialog with guidance/Save/Cancel.
- Onboarding.
- Settings.
- About/support/funding/license/version.
- `Made by the Sanskar` credit.

## Persistence and backup invariants retained

The 2.0.12 retarget does not weaken or bypass existing persistence safeguards.

### Saved-record envelope

- IDs must be nonblank, bounded, and valid Unicode.
- Creation timestamps must be nonnegative.
- Saved names must satisfy the shared 120-code-unit well-formed UTF-16 policy.
- Persisted currencies must be canonical uppercase three-letter values.
- History split count/result decimal shapes remain bounded.
- Template finance settings are revalidated through `CalculatorEngine`.
- Batch replacements reject duplicate IDs.

### Repository boundary

`HistoryRepository` and `TemplateRepository` validate records before DAO mutation. Batch replacement validates every candidate before the DAO replacement call, preventing an invalid candidate from clearing existing data first.

### Backup boundary

`BackupCodec` independently validates in-memory/decoded backup objects because they can bypass normal repositories.

The codec:

- reuses persisted-record structural predicates;
- rejects unsupported schemas;
- bounds payload/line/record/field/decimal sizes;
- validates exact canonical persisted currencies;
- rejects duplicate IDs;
- validates saved names and Unicode;
- detects accidental corruption with SHA-256;
- does not treat SHA-256 as authentication/signature/encryption.

`BackupFileIo` rejects malformed/unmappable UTF-8 bytes before backup semantic parsing.

## Final platform defects already closed

### Unicode-safe PDF truncation

Long PDF receipt lines no longer use unsafe ordinary UTF-16 truncation at the ellipsis boundary. A supplementary Unicode character crossing the boundary cannot leave a dangling surrogate.

Regression coverage lives in the documented JVM platform test bundle.

### Strict backup UTF-8

Backup document reads no longer rely on a decoder that may replace malformed byte input. Malformed/unmappable UTF-8 reports failure and therefore cannot silently mutate input before backup validation.

### Strict decoded persisted currencies

Checksum-valid edited backups containing lowercase/noncanonical persisted currencies are rejected. Decode does not uppercase/repair them before the shared persisted-record policy sees them.

## Regression suite retained

### JVM/domain

- Calculation arithmetic and rounding.
- Validation bounds and invalid finance input.
- Locale-independent currency behavior.
- Deterministic finance fuzz/regression tests.
- Saved-name policy including Unicode/surrogate boundary cases.
- Persisted-record policy for IDs/timestamps/currencies/splits/history result shapes/template envelopes.
- History repository persistence and replacement invariants.
- Template repository persistence, finance, and replacement invariants.
- Duplicate repository replacement IDs.
- Backup codec round-trip/corruption/schema/size/semantic validation.
- Backup persisted-policy encode rejection.
- Checksum-valid noncanonical history/template currency decode rejection.
- Strict malformed UTF-8 backup-byte decoder regression.
- Deterministic backup fuzz/regression tests.
- CSV safety.
- Receipt formatter.
- Unicode-safe PDF truncation including a surrogate crossing the line boundary.
- Export path containment.
- Safe logger redaction.

### Android/Compose/instrumentation

- Room history/template round trips.
- Transactional Room backup replacement.
- Calculator/receipt Compose smoke.
- Named-history dialog callback and Unicode boundary.
- Template dialog guidance/callback/distinct confirm/Unicode boundary.
- History label filtering.
- Settings backup busy state.
- Real-activity onboarding/calculation/named-save/history journey.
- CI compiles instrumentation tests with `assembleDebugAndroidTest`.
- Actual connected-device execution remains a manual release gate.

### Fast repository guards

- Formatting/UTF-8/final-newline/trailing-whitespace/tab guard.
- Kotlin namespace guard.
- Tracked-file documentation coverage guard.
- Android string-resource reference/duplicate-name audit.
- Android local-first manifest/FileProvider audit.
- Complete required-file/local Markdown-link repository audit.
- Common secret-pattern scan.

## CI and repository automation retained

Main `CI` performs:

1. formatting guard;
2. Kotlin namespace guard;
3. tracked-file documentation coverage guard;
4. Android string-resource audit;
5. Android local-first security audit;
6. repository metadata/required-file/local-link audit;
7. secret-pattern scan;
8. JVM tests;
9. instrumentation-test compilation;
10. full Android lint;
11. debug build;
12. release build.

Separate workflows:

- CodeQL;
- Dependency Review;
- Repository Audit, including documentation coverage and Android string-resource audit;
- Dependabot;
- tag-triggered unsigned Release Candidate artifact build that repeats fast guards, JVM tests, instrumentation compilation, full lint, and release compilation before artifact upload.

Superseded PR revisions use concurrency cancellation.

## 2.0.12 continuation commit sequence

This continuation deliberately used granular release/documentation commits instead of one large undifferentiated commit. The branch now contains commits with these messages:

- `release: set app version 2.0.12`
- `docs: retarget changelog to 2.0.12`
- `docs: retarget roadmap to 2.0.12`
- `docs: update release guide for 2.0.12`
- `docs: update 2.0.12 verification gates`
- `docs: map 2.0.12 release documentation`
- `docs: audit 2.0.12 release candidate`
- `docs: point final handoff to 2.0.12`
- `docs: align codebase reference with 2.0.12`
- `docs: update 2.0.12 maintenance milestone`
- the commit containing this file is the final 2.0.12 handoff commit unless an exact-head workflow failure exposes another concrete defect.

## Exhaustive documentation state

The permanent documentation set remains intentionally split by authority rather than duplicating everything in this handoff:

- `README.md` — public overview and entry points;
- `docs/features.md` — implemented user-visible behavior;
- `docs/architecture.md` — layer/dependency architecture;
- `docs/codebase-reference.md` — **every tracked file** and its ownership/invariant role;
- `docs/documentation-map.md` — documentation authority/update matrix;
- `docs/persistence-invariants.md` — repository/backup stored-data contract;
- `docs/backup-restore.md` — functional explicit backup/restore behavior;
- `docs/security-backup.md` — parser/threat/security details;
- `docs/privacy-backup.md` and `PRIVACY.md` — privacy/data behavior;
- `docs/testing.md` — automated/manual test strategy;
- `docs/accessibility.md` — accessibility implementation/manual review;
- `docs/performance.md` — bounded-work/performance policy;
- `docs/logging.md` — privacy-conscious logging contract;
- `docs/setup.md` / `docs/development.md` / `docs/troubleshooting.md` — contributor operations;
- `docs/github-maintenance.md` — repository governance/maintenance;
- `docs/release.md` — 2.0.12 release procedure;
- `docs/verification.md` — authoritative blocking checklist;
- `docs/release-candidate-final-audit.md` — 2.0.12 source-level completion audit;
- `CHANGELOG.md` — notable changes;
- `ROADMAP.md` — open/completed release work;
- this file — volatile current continuation state.

`scripts/check_documentation_coverage.py` mechanically compares the codebase-reference file index with `git ls-files`. This is the authoritative mechanism preventing new/deleted/renamed tracked paths from silently escaping documentation.

## Known verification limitation

The connected engineering environment used for these continuations cannot be treated as a substitute for a clean network-capable Android build/device environment. Therefore:

- no local Gradle success is claimed unless actually executed;
- no local lint success is claimed unless actually executed;
- no connected Android test success is claimed unless actually executed;
- the documentation coverage guard is not claimed successful for the final head until its workflow runs;
- queued/pending/cancelled/superseded GitHub workflows are not treated as green.

This is a verification-environment limitation, not evidence of a source failure.

## Remaining 2.0.12 release gates

### Automated exact-final-head gates

- documentation coverage success;
- CI success;
- CodeQL success;
- Dependency Review success;
- Repository Audit success.

### Manual Android/runtime gates

- `connectedDebugAndroidTest` on a representative emulator/device;
- phone layout review;
- tablet/wide layout review;
- light/dark/system theme review;
- large system font review;
- app large-text review;
- reduced-motion review;
- TalkBack navigation/dialog/list/progress review;
- named-history save/search review;
- template dialog naming/confirm review;
- Unicode-heavy history/template name boundary review;
- 100-item calculator limit review;
- text/CSV/PDF share-flow review, including a long Unicode item name near the PDF truncation boundary;
- malformed UTF-8 backup rejection review;
- checksum-valid noncanonical persisted-currency backup rejection review;
- backup export/progress/restore/replaced-data review;
- offline/airplane-mode core workflow review;
- branded launch-splash review.

### Distribution gates

- capture real screenshots from the exact verified 2.0.12 build using fictional data;
- provide production signing material outside source control;
- produce the signed artifact from the exact verified commit;
- verify signed artifact reports version 2.0.12;
- verify artifact checksum/source SHA;
- merge/tag/release only after all blocking gates pass.

## Database/migration state

- Room database version: `1`.
- Explicit backup schema version: `1`.
- No app-version-driven fake Room migration exists.
- No app-version-driven fake backup migration exists.
- Destructive Room migration fallback is intentionally not the default.
- Room schema export is configured under `app/schemas/`.
- Future tracked generated schema files must be preserved as migration/release evidence and individually added to `docs/codebase-reference.md`.
- The first real future Room schema change must add an explicit migration and migration test.
- The first incompatible future backup-format change must deliberately change/supersede its schema compatibility handling and tests.

## Next exact actions

1. Treat the exact commit containing this handoff as the new `2.0.12` candidate head.
2. Re-fetch PR `#12` and confirm its exact head did not move unexpectedly.
3. Re-fetch CI, CodeQL, Dependency Review, and Repository Audit for that exact head.
4. If any workflow fails, inspect the exact failed job/log and make only the smallest concrete regression-tested/documented fix.
5. Any fix creates a new head and invalidates older workflow evidence.
6. When exact-head automation is green, execute all remaining `docs/verification.md` Android/accessibility/export/backup gates.
7. Merge PR `#12` only when repository/branch rules and release gates allow it.
8. Capture real screenshots from that verified build using fictional data.
9. Sign outside source control and verify the exact signed artifact/source SHA/checksum/version relationship.
10. Tag `v2.0.12` only after every blocking automated/manual/distribution gate is satisfied.

## Continuation rule

While PR `#12` remains open, continue from `complete/v1-finalization`. After it is merged, continue from `main`.

Do not use an older handoff, older branch head, older successful workflow, or historical `v1.0.0` target as proof for the current 2.0.12 release candidate. Inspect current source and exact-head workflow state first. Keep any future fixes small, meaningful, regression-tested, permanently documented, and reflected here.
