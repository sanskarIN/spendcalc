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
- Requested Git email observed in repository commit metadata: `Sanskar <sanskarin@outlook.in>`.
- Exact pre-handoff branch head: `70904861e4c1e80080f4969ba52d3a5e66b798a6`.
- PR state before this handoff commit: open, non-draft, mergeable.
- Source state: application implementation, persistence/export hardening, strict backup validation, Unicode-safe PDF handling, regression coverage, repository automation, exhaustive file documentation, and 2.0.12 release documentation are complete at source level.
- Release state: **not yet verified/taggable** because the exact-final-head automated and manual distribution gates below remain authoritative.

## Version 2.0.12 decision

This continuation retargets the application release to `2.0.12` without inventing unrelated persistence migrations.

Application/distribution metadata:

```text
versionName = 2.0.12
versionCode = 20012
```

Compatibility metadata intentionally remains independent:

```text
Room database version = 1
Explicit backup schema version = 1
```

Rules applied:

- The application release number does not dictate Room schema version.
- The application release number does not dictate explicit backup schema version.
- No fake Room `1 -> 2` migration was added.
- No fake backup-format `1 -> 2` migration was added.
- Future Room migrations are added only when the Room schema actually changes.
- Future backup schema revisions are added only when the serialized compatibility contract actually changes.
- Each real persistence/backup compatibility change must include migration/compatibility tests and permanent documentation updates.

## Exact workflow state before this final handoff commit

At exact pre-handoff head `70904861e4c1e80080f4969ba52d3a5e66b798a6`, GitHub registered:

- CI — `pending`;
- CodeQL — `pending`;
- Dependency Review — `pending`;
- Repository Audit — `queued`.

These states are neither failures nor successes. This handoff commit creates a newer head, so all four workflow families must be re-fetched for the new exact SHA. Workflow concurrency may cancel the older runs as superseded.

Never use a successful check from an older head as release evidence for a newer commit.

## 2.0.12 continuation changes

### Android metadata

`app/build.gradle.kts` now uses:

- `versionCode = 20012`;
- `versionName = "2.0.12"`.

The package/application ID, minimum/target SDK, Room schema, backup schema, finance behavior, and signing model were not modified simply to match the release number.

### Public README

`README.md` now visibly identifies:

- current release candidate `2.0.12`;
- Android `versionCode 20012`;
- independent Room/backup compatibility versions;
- exact-head verification as the release boundary;
- strict malformed UTF-8 backup rejection;
- exact canonical persisted-currency backup validation;
- Unicode-safe PDF line truncation;
- `v2.0.12` as the tag target only after all blocking gates pass.

The README still directs detailed release truth to `docs/verification.md` instead of claiming pending checks are green.

### Changelog

`CHANGELOG.md` now:

- uses `[2.0.12] - Planned`;
- records the application versionCode/versionName retarget;
- documents independent Room/backup compatibility versions;
- records strict backup byte handling, canonical-currency validation, PDF Unicode hardening, persistence safeguards, tests, CI, and exhaustive documentation work;
- does not claim pending automated/manual release gates have completed.

### Roadmap

`ROADMAP.md` now:

- treats 2.0.12 as the active release target;
- marks application metadata work complete;
- keeps exact-final-head automation, Android runtime, accessibility, export/backup, screenshots, external signing, and artifact verification open;
- requires `v2.0.12` only after all release blockers pass;
- treats possible future receipt notes/categories as post-2.0.12 enhancement work, not fabricated blockers.

### Release procedure

`docs/release.md` now:

- identifies 2.0.12/versionCode 20012;
- keeps source completeness, automated exact-head verification, manual Android verification, and distribution evidence separate;
- verifies the version metadata before release;
- requires external signing material to stay outside Git;
- uses the `v2.0.12` tag procedure;
- requires the final signed artifact to report 2.0.12 in About;
- documents that app release version changes alone do not require Room/backup compatibility revisions.

### Release verification

`docs/verification.md` is the authoritative blocking checklist and now explicitly verifies:

- versionName `2.0.12`;
- versionCode `20012`;
- independent Room/backup compatibility versions;
- formatting/namespace/documentation/resource/security/repository/secret guards;
- JVM tests and deterministic fuzz regressions;
- instrumentation compilation;
- full Android lint;
- debug/release compilation;
- CodeQL;
- Dependency Review;
- Repository Audit;
- connected Android tests;
- phone/tablet layouts;
- light/dark/system themes;
- app/system large text;
- reduced motion;
- TalkBack;
- history/template naming/search/undo/retention;
- 100-item bound;
- text/CSV/PDF sharing;
- long-Unicode PDF truncation behavior;
- malformed UTF-8 backup rejection;
- checksum-valid noncanonical persisted-currency backup rejection;
- backup export/progress/confirmation/restore;
- offline/airplane-mode operation;
- splash behavior;
- real screenshots using fictional data;
- protected signing;
- final artifact version/checksum/source SHA.

### Release-candidate source audit

`docs/release-candidate-final-audit.md` now identifies the **SpendCalc 2.0.12 Release-Candidate Final Audit** and records source-level completion for:

- version metadata;
- no fake persistence migration;
- finance correctness boundaries;
- history/templates/settings;
- strict backup handling;
- PDF/export security;
- repository persistence invariants;
- regression coverage;
- accessibility source decisions;
- exhaustive documentation;
- GitHub automation.

It intentionally does not convert source-audit completion into fake runtime/signing/screenshot evidence.

### Documentation source-of-truth map

`docs/documentation-map.md` now treats 2.0.12 as the current candidate and requires release-version changes to review:

- `CHANGELOG.md`;
- `ROADMAP.md`;
- `docs/release.md`;
- `docs/verification.md`;
- `docs/release-candidate-final-audit.md`;
- `what_changed.md`.

It also explicitly states that Room database and backup schema versions remain independent unless real compatibility changes occur.

### Exhaustive codebase reference

`docs/codebase-reference.md` remains the machine-enforced file ownership index for every tracked repository path.

Descriptions were reconciled for:

- `app/build.gradle.kts` — 2.0.12/versionCode 20012;
- `BackupCodec.kt` — exact canonical persisted-currency validation;
- `BackupFileIo.kt` — strict malformed/unmappable UTF-8 rejection;
- `PdfReceiptExporter.kt` — Unicode-safe line truncation;
- `BackupCodecPersistedPolicyTest.kt` — checksum-valid noncanonical decode rejection;
- `PathSafetyTest.kt` — path-containment + strict decoder + PDF truncation platform regressions;
- `docs/release-candidate-final-audit.md` — current 2.0.12 source audit.

`scripts/check_documentation_coverage.py` compares that file index to `git ls-files`, so file coverage remains an enforceable CI invariant rather than a prose promise.

### Repository maintenance docs

`docs/github-maintenance.md` now proposes:

- `2.0.12` — current release milestone;
- `2.1.0` — first post-2.0.12 feature milestone;
- `maintenance` — optional recurring maintenance milestone.

It explicitly warns that application milestones are not persistence schema versions.

### Compatibility handoffs

`what_changed_final.md` now identifies 2.0.12/versionCode 20012 and points back here.

`what_changed_latest.md` remains a compatibility pointer only. It must not override this canonical handoff.

## Final source hardening retained

The 2.0.12 retarget preserves all previously completed release-hardening work.

### Unicode-safe PDF truncation

`PdfReceiptExporter` no longer risks splitting a supplementary Unicode character at the ellipsis boundary.

The current helper:

- uses the shared UTF-16-safe truncation policy;
- respects the PDF line budget;
- preserves short Unicode text exactly;
- cannot leave a dangling high surrogate at the truncation edge.

Regression coverage is kept in the existing documented JVM platform regression file rather than introducing an unnecessary duplicate test file.

### Strict UTF-8 backup document input

`BackupFileIo` uses a strict UTF-8 decoder with malformed/unmappable input configured to report failure.

Consequences:

- malformed bytes are not silently replaced;
- invalid document bytes fail before semantic backup restoration;
- the bounded document-read limit remains enforced;
- failure does not authorize replacement of current user data.

### Exact canonical persisted currencies

`BackupCodec` validates decoded history/template currency text exactly rather than uppercasing/repairing it before validation.

A checksum-valid modified backup containing noncanonical persisted currency text therefore fails closed instead of being silently normalized.

### Repository/backup persistence contract

Repositories remain validation boundaries.

`HistoryRepository` and `TemplateRepository` enforce:

- bounded/valid identifiers;
- nonnegative timestamps;
- valid saved names;
- canonical stored currencies;
- supported history result/split shapes;
- valid template finance settings;
- unique identifiers for replacement batches;
- full candidate prevalidation before DAO replacement.

`BackupCodec` independently reapplies compatible structural rules because a backup object can be constructed without passing through repositories.

## Complete application implementation state

### Finance

- `BigDecimal` arithmetic.
- Deterministic operation order.
- Explicit monetary rounding policy.
- Itemized expenses.
- Discount, tax, tip, service charge.
- Manual exchange-rate conversion.
- Split bill.
- Discount bounded to 0–100%.
- Bounded percentages, monetary values, exchange rates, precision, scale, integer digits, split count, and editable item count.
- Maximum editable expense items: 100.

### History

- Room-backed local history.
- Optional user labels.
- Stable `Calculation` fallback.
- UTF-16-safe 120-character naming policy.
- Local label/currency/total/per-person search.
- 120-character Unicode-safe search bound.
- Individual delete + Undo.
- Confirmed clear-all.
- Optional 30-day/90-day retention.
- Persistence-envelope validation.
- Duplicate-ID batch replacement rejection.

### Templates

- Room-backed reusable settings.
- Stable `Template` fallback.
- UTF-16-safe 120-character name policy.
- Visible length guidance.
- Distinct dialog `Save` confirmation.
- Load/delete + Undo.
- `CalculatorEngine` finance validation at repository boundary.
- Persistence-envelope validation.
- Duplicate-ID replacement rejection.

### Settings/preferences

- DataStore theme mode.
- Large text.
- Reduced motion.
- History retention.
- Onboarding completion.
- Corrupted preferences recover to safe defaults without deleting Room history/templates.

### Backup/restore

- User-selected Android document backup/export and restore.
- History/templates/preferences included.
- Named history included.
- Versioned bounded format.
- URL-safe Base64 text fields.
- Strict UTF-8 document decoding.
- Valid UTF-8/Unicode requirement for text.
- SHA-256 accidental-corruption detection.
- Strict schema/record/checksum/size/line/field/decimal/name/ID/timestamp/canonical-currency/split validation.
- Duplicate-ID rejection.
- Shared persistence-envelope rules.
- Template finance revalidation.
- Transactional Room snapshot/replacement.
- Batch DAO inserts.
- Cross-store compensating preference rollback strategy.
- Modal busy/progress UI.
- Duplicate backup actions disabled while active.
- I/O on `Dispatchers.IO`.
- Bounded encode/decode CPU work on `Dispatchers.Default`.

### Export/platform security

- Plain-text receipt sharing.
- CSV quote/formula neutralization.
- Offline PDF receipts.
- Unicode-safe PDF truncation.
- Private cache exports.
- Non-exported FileProvider.
- Temporary read grants.
- Canonical path containment.
- No core Android INTERNET permission.
- Privacy-conscious structured logging/redaction.

### UI/accessibility/branding

- Jetpack Compose + Material 3.
- Responsive phone/tablet layout.
- Light/dark/system themes.
- Large text.
- Reduced motion.
- AndroidX branded splash.
- Repository-owned primary navigation icons.
- Non-duplicated screen-reader navigation semantics.
- Visible validation text.
- Numeric keyboard hints.
- Named-history dialog with guidance/Save/Cancel.
- Template dialog with guidance/Save/Cancel.
- First-run onboarding.
- Settings.
- About/support/funding/license/version UI.
- `Made by the Sanskar` product credit.

## Regression suite state

### JVM/domain

- finance arithmetic and rounding;
- validation bounds;
- locale-stable currency behavior;
- deterministic finance fuzz coverage;
- saved-name Unicode/surrogate boundaries;
- persisted-record policy;
- history persistence/replacement invariants;
- template persistence/finance/replacement invariants;
- duplicate replacement IDs;
- backup round-trip/corruption/schema/size/semantic validation;
- invalid backup encode rejection;
- checksum-valid noncanonical currency decode rejection;
- malformed UTF-8 backup-byte decoder regression;
- deterministic backup fuzz coverage;
- CSV safety;
- receipt formatting;
- Unicode-safe PDF truncation;
- export path containment;
- safe logger redaction.

### Android/Compose/instrumentation

- Room history/template round trips;
- transactional Room backup replacement;
- calculator/receipt Compose smoke;
- named-history dialog callback/Unicode boundary;
- template dialog guidance/callback/distinct confirm/Unicode boundary;
- History label filtering;
- Settings backup busy state;
- real-activity onboarding/calculation/named-save/history journey;
- instrumentation suite compilation in CI.

Connected-device execution remains a real release gate and is not represented as complete merely because tests compile.

### Fast repository guards

- formatting/UTF-8/final-newline/trailing-whitespace/tab guard;
- Kotlin namespace guard;
- tracked-file documentation coverage guard;
- Android string-resource reference/duplicate-name audit;
- Android local-first manifest/FileProvider audit;
- required-file/local Markdown-link repository audit;
- common secret-pattern scan.

## CI/repository automation

Main CI executes:

1. format guard;
2. Kotlin namespace guard;
3. documentation coverage;
4. Android string-resource audit;
5. Android local-first security audit;
6. repository metadata/link audit;
7. secret-pattern scan;
8. JVM tests;
9. instrumentation compilation;
10. full Android lint;
11. debug build;
12. release build.

Separate automation includes:

- CodeQL;
- Dependency Review;
- Repository Audit;
- Dependabot;
- tag-triggered unsigned release-candidate artifact verification/build.

Superseded PR revisions use concurrency cancellation.

## Granular 2.0.12 continuation commits

This continuation intentionally used separate meaningful commits rather than one large release-version rewrite. Commit messages include:

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
- the commit containing this refreshed handoff is intended to be the final branch source/documentation change unless exact-head automation exposes a concrete defect.

## Documentation authority

Permanent detail is intentionally kept in the appropriate file instead of being duplicated indefinitely in this volatile handoff:

- `README.md` — public product/release-candidate overview;
- `docs/features.md` — implemented user-visible behavior;
- `docs/architecture.md` — architecture/layer boundaries;
- `docs/codebase-reference.md` — every tracked file and ownership/invariant role;
- `docs/documentation-map.md` — documentation authority/change matrix;
- `docs/persistence-invariants.md` — stored-record/backup compatibility contract;
- `docs/backup-restore.md` — explicit backup functionality;
- `docs/security-backup.md` — backup threat/parser security model;
- `docs/privacy-backup.md` + `PRIVACY.md` — privacy/data behavior;
- `docs/testing.md` — verification strategy;
- `docs/accessibility.md` — accessibility behavior/manual review;
- `docs/performance.md` — bounded-work/performance policy;
- `docs/logging.md` — logging/redaction contract;
- `docs/setup.md`, `docs/development.md`, `docs/troubleshooting.md` — contributor operations;
- `docs/github-maintenance.md` — repository maintenance/governance;
- `docs/release.md` — 2.0.12 release procedure;
- `docs/verification.md` — authoritative blocking release checklist;
- `docs/release-candidate-final-audit.md` — source-level 2.0.12 audit;
- `CHANGELOG.md` — notable changes;
- `ROADMAP.md` — planning/open gates;
- this file — active exact-head continuation state.

## Known verification boundary

Source/documentation work performed through the connected GitHub environment is not a substitute for real Android runtime, external signing, or final distribution evidence.

Therefore:

- do not claim local Gradle success unless actually run;
- do not claim local lint success unless actually run;
- do not claim connected Android test success unless actually run;
- do not claim documentation coverage success for the newest head until GitHub Actions reports it;
- do not treat pending/queued/cancelled/superseded workflows as green;
- do not fabricate release screenshots;
- do not commit production signing/store credentials.

## Remaining release gates for 2.0.12

### Automated exact-final-head

- documentation coverage success;
- CI success;
- CodeQL success;
- Dependency Review success;
- Repository Audit success.

### Manual Android/runtime

- `connectedDebugAndroidTest` on a representative emulator/device;
- phone layout review;
- tablet/wide layout review;
- light/dark/system theme review;
- large Android font review;
- app large-text review;
- reduced-motion review;
- TalkBack navigation/dialog/list/progress review;
- named-history save/search review;
- template dialog naming/confirm review;
- Unicode-heavy saved-name boundary review;
- 100-item calculator-limit review;
- text/CSV/PDF share-flow review;
- long-Unicode PDF item-name review;
- malformed UTF-8 backup rejection review;
- checksum-valid noncanonical persisted-currency backup rejection review;
- backup export/progress/confirmation/restore/data-integrity review;
- offline/airplane-mode core workflow review;
- branded launch-splash review.

### Distribution

- capture real screenshots from the exact verified build using fictional data only;
- supply production signing material outside source control;
- produce the signed artifact from the exact verified source;
- verify About reports 2.0.12;
- record/verify artifact checksum and exact source SHA;
- merge/tag/release only after all blockers pass.

## Database/backup migration state

- Room database version: `1`.
- Backup schema version: `1`.
- No fake app-version-driven persistence migration was introduced.
- Destructive Room migration fallback is intentionally not the default.
- Room schema export remains configured under `app/schemas/`.
- Future generated schema history must be tracked/documented as migration evidence.
- The first real Room schema change must include an explicit migration and migration test.
- A future incompatible backup format change must deliberately revise compatibility handling and regression coverage.

## Next exact actions

1. Treat the exact commit containing this file as the newest 2.0.12 release-candidate head.
2. Re-fetch PR #12 and confirm its exact head.
3. Re-fetch CI, CodeQL, Dependency Review, and Repository Audit for that exact SHA.
4. If a workflow fails, inspect the exact failed job/log and make only the smallest concrete regression-tested/documented fix.
5. Any source/docs fix creates a new head; discard older release evidence.
6. When exact-head automated checks are green, execute every remaining manual gate in `docs/verification.md`.
7. Merge PR #12 only when repository policy and release gates allow it.
8. Capture genuine screenshots from that exact verified build using fictional data.
9. Sign outside source control and verify source SHA/version/checksum relationship.
10. Tag `v2.0.12` only after every blocking automated/manual/distribution gate is complete.

## Continuation rule

While PR #12 remains open, continue from `complete/v1-finalization`. After it is merged, continue from `main`.

Do not use older branch heads, older successful workflows, previous `v1.0.0` planning language, or earlier handoffs as evidence for the current 2.0.12 candidate. Keep any future changes small, justified by concrete failures or contradictions, regression-tested where applicable, documented in permanent docs, and reflected here.
