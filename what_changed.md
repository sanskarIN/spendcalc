# SpendCalc — Work Continuity

## Current release candidate

- Date: 2026-08-19
- Repository: `sanskarIN/spendcalc`
- Default branch: `main`
- Active branch: `complete/v1-finalization`
- Active pull request: `#12`
- Master specification: `15_spendcalc_master_prompt.md`
- Target application release: `2.0.12`
- Android `versionName`: `2.0.12`
- Android `versionCode`: `20012`
- Room database version: `1`
- Explicit backup schema version: `1`
- Platform: Android API 26+, Kotlin + Jetpack Compose, local/offline-first
- License: MIT
- Product credit: `Made by the Sanskar`
- Requested commit email observed in repository history: `Sanskar <sanskarin@outlook.in>`
- Exact pre-handoff branch head: `84a875132f76bcbb58be2f4fae73d809bba454d1`
- PR state before this handoff commit: open, non-draft, mergeable
- Release status: source/documentation finalization is complete; exact-final-head automation and manual release/distribution evidence remain required before `v2.0.12`.

## Exact workflow state before this handoff commit

At exact head `84a875132f76bcbb58be2f4fae73d809bba454d1`, GitHub registered:

- CI — `pending`
- CodeQL — `queued`
- Dependency Review — `pending`
- Repository Audit — `queued`

This handoff commit creates a newer exact head. Re-fetch all four workflow families for that new SHA before any merge/tag/release decision. Pending, queued, cancelled, skipped, and superseded runs are never treated as successful release evidence.

## Version 2.0.12 retarget

The requested application release is now `2.0.12`.

`app/build.gradle.kts` uses:

```text
versionName = 2.0.12
versionCode = 20012
```

The application release number is intentionally independent from persistence compatibility versions:

```text
Room database version = 1
Explicit backup schema version = 1
```

No fake Room `1 -> 2` migration or fake backup schema `1 -> 2` migration was added. A future compatibility version changes only when the actual stored/serialized contract changes and must then receive focused migration/compatibility tests and documentation.

## 2.0.12 work completed

### Release metadata

- Android application version retargeted to 2.0.12.
- Android monotonic `versionCode` set to 20012.
- Package/application ID, min/target SDK, Room schema, backup schema, finance behavior, and signing model were not changed merely to mirror the release number.

### README

`README.md` now exposes:

- current `2.0.12` release candidate;
- `versionCode 20012`;
- independent Room/backup compatibility versions;
- exact-head verification boundary;
- strict malformed UTF-8 backup rejection;
- exact canonical persisted-currency backup validation;
- Unicode-safe PDF line truncation;
- `v2.0.12` as the tag target only after blocking gates pass.

### Changelog

`CHANGELOG.md` now:

- uses `[2.0.12] - Planned`;
- records the version retarget;
- records final persistence, backup, Unicode/PDF, regression, security, CI, and documentation hardening;
- keeps Room/backup compatibility versions independent;
- does not falsely mark pending runtime/release gates complete.

### Roadmap

`ROADMAP.md` now:

- targets 2.0.12;
- marks application metadata complete;
- keeps exact-head automation, connected Android runtime, accessibility, export/restore, screenshots, signing, and artifact verification open;
- requires `v2.0.12` only after all blockers pass;
- moves non-blocking future enhancements to post-2.0.12 planning.

### Release guide

`docs/release.md` now:

- identifies 2.0.12/versionCode 20012;
- separates source completeness, automated exact-head evidence, manual Android evidence, and distribution evidence;
- uses `v2.0.12` tag instructions;
- requires the final signed artifact to report 2.0.12;
- keeps protected signing material outside Git;
- documents independent app/database/backup versions.

### Release verification

`docs/verification.md` is the authoritative blocking checklist and includes:

- versionName/versionCode verification;
- independent Room/backup compatibility checks;
- formatting/namespace/documentation/resource/security/repository/secret guards;
- JVM/fuzz tests;
- instrumentation compilation;
- full Android lint;
- debug/release compilation;
- CodeQL, Dependency Review, Repository Audit;
- connected Android tests;
- phone/tablet, theme, large text, reduced motion, TalkBack;
- history/template/search/undo/retention behavior;
- 100-item UI bound;
- text/CSV/PDF share flows;
- long-Unicode PDF boundary behavior;
- malformed UTF-8 backup rejection;
- checksum-valid noncanonical persisted-currency rejection;
- backup export/progress/confirmation/restore integrity;
- offline/airplane-mode behavior;
- splash review;
- real screenshots using fictional data;
- external signing;
- exact artifact version/checksum/source-SHA verification.

### Release-candidate audit

`docs/release-candidate-final-audit.md` is now explicitly the **SpendCalc 2.0.12 Release-Candidate Final Audit**. It records source-level implementation/documentation completion without pretending that connected-device, signing, screenshot, or pending GitHub workflow evidence exists.

### Documentation authority

`docs/documentation-map.md` now treats the 2.0.12 release state as a cross-document change that requires reconciliation of changelog, roadmap, release guide, verification, release audit, and this handoff.

### Exhaustive file documentation

`docs/codebase-reference.md` remains the exhaustive tracked-file ownership/invariant index. Its descriptions now match 2.0.12 and the final platform hardening.

`scripts/check_documentation_coverage.py` compares that marked index against `git ls-files`, so every tracked file remains mechanically required to be documented exactly once.

### Repository maintenance

`docs/github-maintenance.md` now uses:

- `2.0.12` — current release milestone;
- `2.1.0` — first post-2.0.12 feature milestone;
- `maintenance` — recurring maintenance where useful.

It explicitly states that application milestones are not Room/backup schema versions.

### Compatibility pointers

- `what_changed_final.md` points to this canonical handoff and identifies 2.0.12.
- `what_changed_latest.md` now explicitly identifies 2.0.12 and warns against old PR #1 / historical 1.0.0 continuation state.

Neither compatibility file overrides this root handoff.

## Final source defects already closed

### Unicode-safe PDF truncation

`PdfReceiptExporter` no longer truncates a long line in a way that can split a supplementary Unicode character into a dangling surrogate. Regression coverage exercises a surrogate pair crossing the line boundary, normal ASCII budget behavior, and exact preservation of short Unicode text.

### Strict UTF-8 backup document input

`BackupFileIo` uses a strict UTF-8 decoder configured to report malformed/unmappable input instead of silently replacing invalid bytes. Invalid document bytes therefore fail before semantic restore processing.

### Exact canonical persisted-currency decode

`BackupCodec` validates decoded history/template currency text exactly. A checksum-valid modified backup containing lowercase/noncanonical persisted currency fails instead of being repaired before validation.

### Repository/backup persistence contract

Repositories remain validation boundaries:

- IDs are bounded/nonblank/valid Unicode.
- Creation timestamps are nonnegative.
- Saved names use the shared 120-code-unit well-formed UTF-16 policy.
- Persisted currencies are canonical uppercase three-letter values.
- History split/result shapes are bounded.
- Template finance settings are revalidated through `CalculatorEngine`.
- Replacement batches reject duplicate IDs.
- All replacement candidates are validated before DAO replacement begins.

`BackupCodec` independently reapplies compatible structural rules because backup objects can bypass repositories.

## Complete product state retained

### Finance

- precision-safe `BigDecimal` arithmetic;
- deterministic calculation order;
- explicit rounding;
- itemized expenses;
- discount/tax/tip/service charge;
- manual exchange-rate conversion;
- split bill;
- bounded input precision/scale/ranges;
- discount 0–100%;
- split count 1–1,000,000;
- maximum 100 editable expense items.

### History

- Room-backed local history;
- optional user labels + `Calculation` fallback;
- UTF-16-safe 120-character labels;
- local search by labels/currencies/values;
- Unicode-safe 120-character search bound;
- individual delete + Undo;
- clear-all confirmation;
- 30/90-day optional retention;
- persistence-envelope validation;
- duplicate-ID replacement rejection.

### Templates

- Room-backed reusable settings;
- optional names + `Template` fallback;
- UTF-16-safe 120-character naming;
- visible name-length guidance;
- distinct `Save` dialog confirmation;
- load/delete + Undo;
- repository-level `CalculatorEngine` validation;
- structural envelope validation;
- duplicate-ID replacement rejection.

### Preferences

- DataStore theme;
- large text;
- reduced motion;
- history retention;
- onboarding state;
- corruption fallback to safe defaults without deleting Room records.

### Backup/restore

- user-selected Android document APIs;
- history/templates/preferences included;
- versioned bounded format;
- URL-safe Base64 text fields;
- strict UTF-8 document decoding;
- well-formed Unicode requirements;
- SHA-256 accidental-corruption detection;
- strict schema/record/checksum/size/line/field/decimal/name/ID/timestamp/canonical-currency/split validation;
- duplicate-ID rejection;
- shared persisted-record rules;
- template finance revalidation;
- transactional Room snapshot/replacement;
- batch DAO inserts;
- compensating preference rollback strategy;
- modal busy/progress state;
- duplicate backup actions disabled while active;
- file I/O on `Dispatchers.IO`;
- bounded encode/decode CPU work on `Dispatchers.Default`.

### Export/platform security

- plain-text sharing;
- CSV quoting/formula neutralization;
- offline PDF receipts;
- Unicode-safe PDF truncation;
- private cache exports;
- non-exported FileProvider;
- temporary read grants;
- canonical path containment;
- no core INTERNET permission;
- privacy-conscious structured logging/redaction.

### UI/accessibility/branding

- Jetpack Compose + Material 3;
- responsive phone/tablet calculator layout;
- light/dark/system themes;
- large text;
- reduced motion;
- branded AndroidX splash;
- repository-owned navigation icons;
- non-duplicated navigation accessibility semantics;
- visible validation text;
- numeric keyboard hints;
- named-history/template dialogs with guidance/Save/Cancel;
- onboarding;
- settings;
- About/support/funding/license/version;
- `Made by the Sanskar` credit.

## Regression/guard state

### JVM/domain/platform

- finance arithmetic/rounding/validation;
- locale-stable currency normalization;
- deterministic finance fuzzing;
- saved-name Unicode/surrogate boundaries;
- persisted-record policy;
- history/template persistence and replacement invariants;
- duplicate replacement IDs;
- backup round-trip/corruption/schema/size/semantic validation;
- invalid backup encode rejection;
- checksum-valid noncanonical currency rejection;
- malformed UTF-8 byte rejection;
- deterministic backup fuzzing;
- CSV safety;
- receipt formatting;
- Unicode-safe PDF truncation;
- export path containment;
- safe logger redaction.

### Android/Compose

- Room history/template round trips;
- transactional backup replacement;
- calculator/receipt Compose smoke;
- history save dialog/Unicode boundary;
- template dialog guidance/callback/distinct confirm/Unicode boundary;
- History label filtering;
- Settings backup busy state;
- real-activity onboarding/calculation/named-save/history journey;
- instrumentation compilation in CI.

Actual connected-device execution remains a manual release gate.

### Fast repository guards

- formatting/UTF-8/newline/whitespace/tab guard;
- Kotlin namespace guard;
- exhaustive tracked-file documentation guard;
- Android string-resource reference/duplicate-name audit;
- Android local-first manifest/FileProvider audit;
- required-file/local Markdown-link audit;
- common secret-pattern scan.

## CI/repository automation

Main CI performs:

1. formatting;
2. Kotlin namespace validation;
3. tracked-file documentation coverage;
4. Android string-resource audit;
5. Android local-first security audit;
6. repository/link audit;
7. secret scan;
8. JVM tests;
9. instrumentation compilation;
10. full Android lint;
11. debug build;
12. release build.

Separate workflows:

- CodeQL;
- Dependency Review;
- Repository Audit;
- Dependabot;
- tag-triggered unsigned release-candidate verification/build.

Superseded PR revisions use concurrency cancellation.

## Granular 2.0.12 commit messages

The release retarget/finalization was intentionally split into meaningful commits:

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
- `docs: finalize SpendCalc 2.0.12 handoff`
- `docs: expose SpendCalc 2.0.12 release target`
- `docs: refresh final SpendCalc 2.0.12 handoff`
- `docs: align latest pointer with 2.0.12`
- this commit is the final refreshed canonical handoff unless exact-head automation exposes a concrete defect.

## Documentation authority

- `README.md` — public product/release overview.
- `docs/features.md` — implemented user behavior.
- `docs/architecture.md` — architecture boundaries.
- `docs/codebase-reference.md` — every tracked file and its role.
- `docs/documentation-map.md` — documentation authority/update matrix.
- `docs/persistence-invariants.md` — stored-record/backup contract.
- `docs/backup-restore.md` — backup functionality.
- `docs/security-backup.md` — backup threat/parser model.
- `docs/privacy-backup.md` + `PRIVACY.md` — privacy behavior.
- `docs/testing.md` — verification strategy.
- `docs/accessibility.md` — accessibility review.
- `docs/performance.md` — bounded-work/performance policy.
- `docs/logging.md` — redaction/logging contract.
- `docs/setup.md`, `docs/development.md`, `docs/troubleshooting.md` — contributor operations.
- `docs/github-maintenance.md` — repository maintenance.
- `docs/release.md` — 2.0.12 release procedure.
- `docs/verification.md` — authoritative blocking checklist.
- `docs/release-candidate-final-audit.md` — source-level 2.0.12 audit.
- `CHANGELOG.md` — notable changes.
- `ROADMAP.md` — planning/open gates.
- this file — active exact-head continuation truth.

## Remaining 2.0.12 release gates

### Automated exact-final-head

- documentation coverage success;
- CI success;
- CodeQL success;
- Dependency Review success;
- Repository Audit success.

### Manual Android/runtime

- `connectedDebugAndroidTest`;
- phone/tablet layouts;
- light/dark/system themes;
- large Android font + app large-text;
- reduced motion;
- TalkBack navigation/dialog/list/progress;
- history save/search/retention/delete/undo/clear;
- template save/load/delete/undo;
- Unicode-heavy saved-name boundaries;
- 100-item calculator limit;
- text/CSV/PDF sharing;
- long-Unicode PDF truncation;
- malformed UTF-8 backup rejection;
- checksum-valid noncanonical persisted-currency backup rejection;
- backup export/progress/confirmation/restore/data integrity;
- offline/airplane-mode core workflow;
- branded launch splash.

### Distribution

- real screenshots from the exact verified build using fictional data;
- production signing material outside source control;
- signed artifact from exact verified source;
- About version = 2.0.12;
- artifact checksum/source-SHA relationship recorded and verified;
- merge/tag/release only after all blockers pass.

## Continuation instructions

1. Treat the exact commit containing this file as the newest 2.0.12 candidate head.
2. Re-fetch PR #12 and confirm its exact head.
3. Re-fetch CI, CodeQL, Dependency Review, and Repository Audit for that exact SHA.
4. If a workflow fails, inspect the exact failed job/log and make only the smallest concrete regression-tested/documented fix.
5. Any new source/docs commit invalidates older release evidence.
6. When exact-head automation is green, execute every remaining manual gate in `docs/verification.md`.
7. Merge PR #12 only when repository policy/release gates allow it.
8. Capture genuine screenshots from the exact verified build using fictional data.
9. Sign outside source control and verify version/source/checksum relationship.
10. Tag `v2.0.12` only after every blocking automated/manual/distribution gate is complete.

While PR #12 remains open, continue from `complete/v1-finalization`; after merge, continue from `main`.

Do not use older branch heads, historical `v1.0.0` planning language, older successful workflows, or older handoffs as evidence for the current 2.0.12 candidate.
