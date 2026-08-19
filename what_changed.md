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
- Requested Git email observed in GitHub commit metadata: `Sanskar <sanskarin@outlook.in>`.
- Pre-handoff head for this update: `2fa5e41fcb1dcae2b87f5181deebe871e772ce84`.
- Pull request state at the start of this final source-audit continuation: open, non-draft, mergeable.
- Source state: the planned application implementation, persistence/export hardening, regression suite, repository automation, deep permanent documentation, strict backup byte/currency validation, and Unicode-safe PDF truncation are complete at source level on `complete/v1-finalization`. Every tracked file is covered by the exhaustive file reference whose coverage is enforced by CI.
- Release state: do not tag or describe `v1.0.0` as verified until the exact final commit has successful CI, CodeQL, Dependency Review, and Repository Audit results plus the manual Android/accessibility/export/backup/signing/screenshot gates in `docs/verification.md`.

## Continuation starting point

The previous handoff already included:

- precision-safe `BigDecimal` finance arithmetic and bounded input validation;
- Room history and saved templates;
- DataStore preferences;
- named history saves;
- history search/filter;
- individual history/template delete Undo behavior;
- history retention;
- text/CSV/PDF export;
- explicit local backup/restore with checksum, bounded parsing, transactional Room replacement, and compensating DataStore rollback;
- UTF-16-safe saved-name policy and 120-character history/template name contract;
- exact valid restored-name preservation;
- Unicode-boundary history save tests;
- bounded History search;
- Android string-resource reference/duplicate-name audit;
- Android local-first/FileProvider security audit;
- branded splash/navigation assets and accessibility semantics;
- CI, CodeQL, Dependency Review, Repository Audit, Dependabot, and release-candidate workflows;
- release documentation and manual verification gates.

The active branch head at that previous handoff was `2470b124c19d628009428436788dd400aed05d2a`.

## Exact workflow status observed during the persistence continuation

At head `eaad544f3ed4e5bd9f8658b7a70dac181ee0ccaa`, GitHub reported:

- CI — `pending`;
- Dependency Review — `pending`;
- CodeQL — `queued`;
- Repository Audit — `queued`.

Earlier exact heads in the same continuation also repeatedly registered the same four workflow families as queued/pending. These states are not failures, but they are also not successful release verification. Workflow concurrency cancels superseded pull-request revisions, so no result from an older head should be used as proof for a newer one.

Immediately before this final handoff commit, exact head `2fa5e41fcb1dcae2b87f5181deebe871e772ce84` registered:

- CI — `pending`;
- Dependency Review — `pending`;
- CodeQL — `queued`;
- Repository Audit — `queued`.

Those runs are expected to be superseded by the handoff commit itself. Re-fetch all four workflow families for the new exact head before making any release decision.

The connected execution container still cannot resolve `github.com`, so a clean local Gradle dependency resolution/build cannot be used as release proof in this environment. Do not claim local `gradle test`, lint, debug, release, or instrumentation results unless they are actually run in a network-capable environment. GitHub Actions remains the authoritative automated Android/Gradle verification source here.

## Work completed in the persistence continuation

### 1. Template repository finance validation

Before this continuation, `TemplateRepository.save(...)` assumed callers had already passed through ViewModel validation. A direct repository caller could provide invalid persisted template settings and create local data that `BackupCodec` would later reject.

This is now closed:

- `TemplateRepository` accepts a `CalculatorEngine` validator dependency.
- `save(...)` validates `input.copy(items = emptyList())` before persistence.
- Only settings actually persisted by templates are validated; discarded expense line items do not block template saving.
- `restore(...)` and `replaceAll(...)` revalidate template finance settings before DAO writes.
- Invalid discount/tax/tip/service-charge/split/currency/exchange-rate settings fail closed with `IllegalArgumentException` before persistence.
- `AppContainer` passes its shared `calculatorEngine` instance into `TemplateRepository`, so the production app does not create a separate rule implementation for template persistence.

Regression tests cover:

- invalid tax rejected during direct repository save;
- invalid exchange rate rejected during restore;
- invalid split rejected during replacement;
- line items intentionally ignored for template-setting validation;
- invalid candidate replacement preserving existing template data.

### 2. Template naming UX and accessibility completion

The template dialog previously enforced the same 120-character Unicode-safe name contract as history labels but did not explain it to the user.

The dialog now includes:

- `Template name` visible label;
- supporting text: `Give this template a short name. Up to 120 characters.`;
- surrogate-safe boundary truncation through `truncateUtf16Safely`;
- a concise dialog confirmation labeled `Save`;
- a separate `Cancel` action.

The underlying calculator action remains `Save template`. Using `Save` for the dialog confirmation removes an ambiguity where the title, underlying button, and confirmation all used the same phrase.

Compose tests now cover:

- template naming guidance is visible;
- entered template name reaches the callback;
- an emoji crossing the 120-character boundary is not split;
- the distinct `Save` dialog action is used.

Accessibility documentation and release verification explicitly require TalkBack/large-font review of both history and template save dialogs.

### 3. Shared persisted-record structural policy

A new domain file, `SavedRecordPolicy.kt`, defines the structural contract for persisted history/template records.

Current constants:

- `MAX_SAVED_ID_CHARS = 128`;
- `MAX_SAVED_RESULT_INTEGER_DIGITS = 34`;
- `MAX_SAVED_RESULT_SCALE = 12`;
- `MAX_SAVED_SPLIT_COUNT = 1_000_000`.

Current shared helpers include:

- `isValidSavedId(...)`;
- `requireValidSavedId(...)`;
- `hasUniqueSavedIds(...)`;
- `requireUniqueSavedIds(...)`;
- `isCanonicalSavedCurrencyCode(...)`;
- `isValidSavedResultDecimal(...)`;
- `isValidHistoryRecord(...)`;
- `requireValidHistoryRecord(...)`;
- `isValidTemplateEnvelope(...)`;
- `requireValidTemplateEnvelope(...)`.

Persisted IDs must be:

- nonblank;
- at most 128 UTF-16 code units;
- well-formed UTF-16.

Persisted timestamps must be nonnegative.

Persisted currencies are canonical uppercase three-letter values such as `INR` and `USD`.

History records additionally require:

- valid saved label;
- split count from 1 through 1,000,000;
- nonnegative subtotal, discount, tax, tip, service charge, total, converted total, per-person, and converted-per-person values;
- saved result scale from 0 through 12;
- at most 34 integer digits for saved result fields.

Template envelope validation covers:

- ID;
- timestamp;
- saved name;
- canonical base currency;
- canonical converted currency.

Template finance settings are then validated through `CalculatorEngine`.

### 4. History repository persistence boundary

`HistoryRepository` now validates records before DAO writes.

Behavior:

- new history labels use `normalizeSavedName(...)`;
- currencies are normalized with `trim().uppercase(Locale.ROOT)` before persistence;
- the resulting domain record must pass `requireValidHistoryRecord(...)`;
- invalid direct `CalculationResult` input cannot be persisted;
- restore/replace records cannot bypass ID/timestamp/name/currency/split/result-shape bounds.

Tests now cover:

- negative history result rejected before persistence;
- valid mixed-case/whitespace currency input canonicalized to `INR`/`USD`;
- negative timestamp rejected;
- oversized ID rejected;
- invalid split rejected;
- unsupported stored result magnitude rejected;
- exact valid restored history-label whitespace preserved;
- oversized restored label rejected rather than rewritten;
- invalid replace-all candidate leaves existing history intact.

### 5. Template repository structural persistence boundary

`TemplateRepository` now performs two validation layers before DAO writes:

1. normalize intended canonical currencies and validate the persisted template envelope;
2. validate persisted finance settings through `CalculatorEngine`.

Tests now cover:

- negative injected save timestamp rejected;
- valid mixed-case/whitespace currencies canonicalized before persistence;
- negative restored timestamp rejected;
- oversized restored ID rejected;
- oversized name rejected rather than rewritten;
- invalid finance settings rejected before persistence;
- invalid replacement candidate leaves existing templates intact.

### 6. Duplicate-ID replacement protection

The backup codec already treated duplicate IDs as invalid. Direct repository `replaceAll(...)` calls did not previously enforce the same collection invariant.

Now:

- `HistoryRepository.replaceAll(...)` calls `requireUniqueSavedIds(...)` before mapping/replacement;
- `TemplateRepository.replaceAll(...)` does the same;
- every candidate record is mapped and validated before the DAO replacement call;
- single-record `restore(...)` intentionally remains an upsert and may reuse an existing ID because Undo restores the exact deleted record.

`RepositoryDuplicateIdTest.kt` proves:

- duplicate history replacement IDs fail before existing data is cleared;
- duplicate template replacement IDs fail before existing data is cleared.

### 7. Backup codec and persistence policy alignment

`BackupCodec` now reuses persisted-record policy rules rather than duplicating structural history/template envelope logic.

Changes include:

- history validation delegates to `isValidHistoryRecord(...)`;
- template validation first requires `isValidTemplateEnvelope(...)`, then applies `CalculatorEngine` finance validation;
- backup decimal-shape checks use the shared saved-result scale/integer-digit constants;
- structurally invalid/noncanonical in-memory records are rejected during encode rather than silently transformed;
- decoded history/template currency strings are validated in their exact decoded form instead of being uppercased before validation.

This closes two round-trip semantic issues: a programmatically constructed persisted record with noncanonical currency cannot be encoded, and a checksum-valid edited backup containing lowercase/noncanonical currency cannot be silently repaired during decode. The persistence/export contract now fails closed in both directions.

`BackupCodecPersistedPolicyTest.kt` covers:

- noncanonical template currency rejected during encode;
- checksum-valid noncanonical history currency rejected during decode;
- checksum-valid noncanonical template currency rejected during decode;
- oversized history identifier rejected during encode;
- negative template timestamp rejected during encode.

Backup decode continues to reject duplicate IDs, unsupported schemas, malformed records, excessive sizes/lines, invalid decimal shapes, malformed UTF-8/Unicode, invalid saved names, invalid timestamps/currencies/splits, and corrupted checksums.

### 8. Persistence documentation

A dedicated `docs/persistence-invariants.md` defines:

- the repository/backup compatibility invariant;
- ID rules;
- timestamp rules;
- saved-name rules;
- canonical currency rules, including strict decoded forms;
- history result/split rules;
- template setting rules;
- replacement ordering;
- duplicate-ID handling;
- backup relationship;
- strict UTF-8 document input behavior;
- future schema/regression expectations.

The repository audit requires this file.

`docs/architecture.md` describes repositories as validation boundaries and links to the persistence contract.

`docs/security-backup.md` documents local repository validation and strict document-byte decoding in the backup threat model rather than treating only decoded backup fields as untrusted.

`docs/testing.md` includes the complete persistence-invariant and final platform regression strategy.

`docs/verification.md` includes automated/manual persistence, template-dialog, duplicate-ID, canonical-currency, and backup-alignment gates.

`docs/release-candidate-final-audit.md` records the persistence-invariant source and regression coverage as complete.

`docs/accessibility.md` includes template dialog guidance/confirmation/Unicode checks.

`README.md`, `ROADMAP.md`, and `CHANGELOG.md` describe the hardened persistence model.

## Current complete implementation state

### Finance

- `BigDecimal` finance arithmetic.
- Deterministic calculation order.
- Explicit rounding policy.
- Itemized expenses.
- Discount/tax/tip/service charge.
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

## Regression suite state

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

## Current CI/repository automation

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
- tag-triggered unsigned Release Candidate artifact build, now repeating the fast guards, JVM tests, instrumentation compilation, full lint, and release compilation before artifact upload.

Superseded PR revisions use concurrency cancellation.

## Commits from the persistence continuation

- `1c4f891` — `fix: validate template settings at repository boundary`
- `0c1a01d` — `test: enforce template finance persistence invariants`
- `1693f06` — `refactor: share calculator validator with template repository`
- `8e14578` — `ux: explain template name length limit`
- `05829a8` — `ux: show template name length guidance`
- `4d1240e` — `test: cover template naming guidance and Unicode boundary`
- `d1c6869` — `ux: distinguish template dialog confirm action`
- `8099a42` — `ux: use concise template dialog confirm action`
- `46ab765` — `test: target concise template dialog confirm action`
- `ef371d7` — `refactor: centralize persisted record validation policy`
- `4526bb7` — `test: cover persisted record envelope policy`
- `d3fd281` — `fix: validate history records before persistence`
- `cb95c7b` — `fix: validate template record envelope before persistence`
- `830e4c7` — `test: enforce history persistence invariants`
- `6cda520` — `refactor: share persisted record rules with backup codec`
- `a3cb84f` — `test: enforce backup persisted record policy`
- `b4be00b` — `test: cover template envelope persistence rules`
- `e170701` — `docs: record persisted record invariant hardening`
- `8a26cb1` — `docs: document persisted record regression strategy`
- `eaad544` — `docs: document repository and backup invariant policy`
- `2f9263e` — `refactor: centralize duplicate saved ID validation`
- `0df7610` — `fix: reject duplicate history IDs before replacement`
- `3fd7b15` — `fix: reject duplicate template IDs before replacement`
- `06edcf6` — `refactor: expose saved ID uniqueness predicate`
- `1fde1b7` — `test: reject duplicate IDs before repository replacement`
- `282e711` — `docs: define persistence and backup invariants`
- `75ec14e` — `ci: require persistence invariant documentation`
- `d5a4ff8` — `docs: link persistence invariants into architecture`
- `dafa6af` — `docs: expand release checks for persistence invariants`
- `c98ce5b` — `docs: finalize persistence invariant release audit`
- `f7ca1f4` — `docs: add template dialog accessibility verification`
- `0dbb9fb` — `docs: expose persistence invariant hardening in README`
- `873b8ac` — `docs: mark persistence invariant audit complete`

## Exhaustive documentation continuation

This documentation pass started from release-candidate head `4c935e4baaa6d9b7fd75afaf4e0976ea3d611afb`.

At that exact starting head, PR `#12` was still open, non-draft, mergeable, and GitHub registered:

- CI — pending;
- Dependency Review — pending;
- CodeQL — queued;
- Repository Audit — queued.

No queued/pending result was represented as successful.

### Full repository inventory

A recursive inventory covered root files, `.github`, `app`, all source sets, Android resources, JVM tests, instrumentation tests, scripts, schemas, ADRs, assets, and permanent/compatibility documentation.

Before this pass, the repository inventory contained 147 tracked files. Three maintained documentation/coverage files were added:

- `docs/codebase-reference.md`;
- `docs/documentation-map.md`;
- `scripts/check_documentation_coverage.py`.

The exhaustive reference is therefore designed for the resulting 150 tracked paths. This numeric expectation is not treated as proof until the new exact-head documentation guard actually runs successfully in GitHub Actions; the invariant is defined by exact set equality with `git ls-files`, not by a hard-coded count.

The audit also confirmed that the repository intentionally does **not** commit Gradle wrapper files. Setup/development/troubleshooting/README now state this explicitly rather than implying `gradlew` should exist.

### `docs/codebase-reference.md`

A permanent file-by-file repository reference now documents every tracked path individually, grouped by role:

- root build/policy/handoff files;
- GitHub funding/templates/Dependabot/workflows;
- app build/schema metadata;
- all Android instrumentation/Compose/activity tests;
- manifest/application bootstrap;
- every data/Room repository/DAO/entity file;
- every finance/domain/export/model file;
- every Android platform adapter;
- every Compose state/navigation/component/screen/theme file;
- every drawable/value/XML Android resource;
- every JVM repository/domain/export/model/platform/UI test;
- every permanent document/ADR/asset;
- every repository guard script.

Each entry describes ownership plus the behavior/test/security/release invariant the file supports. The marked file-index block is deliberately machine-readable.

### `docs/documentation-map.md`

A documentation information architecture now defines:

- public product documentation authority;
- architecture/implementation authority;
- persistence/backup/privacy/security authority;
- testing/verification authority;
- setup/operations/maintenance authority;
- ADR durability rules;
- changelog/roadmap responsibilities;
- current-work handoff responsibilities;
- screenshot/artwork distinction;
- change-to-document matrix;
- anti-drift rules.

Important rule: `what_changed.md` owns volatile continuation state but does not replace permanent architecture/security/testing documentation.

### Tracked-file documentation coverage guard

`scripts/check_documentation_coverage.py` now:

- invokes `git ls-files -z`;
- parses only the marked file index in `docs/codebase-reference.md`;
- requires exactly one marker pair;
- rejects tracked files missing from the index;
- rejects documented paths that are no longer tracked;
- rejects duplicate documented paths;
- reports the exact tracked-file count only on successful equality.

This turns “document every file” from a one-time promise into a continuing repository invariant.

### CI/repository/release enforcement

The documentation guard is now run by:

- `.github/workflows/ci.yml`;
- `.github/workflows/repository-audit.yml`;
- tag-triggered `.github/workflows/release.yml`.

The tagged Release Candidate workflow was also strengthened to run:

- format guard;
- Kotlin namespace guard;
- documentation coverage;
- Android string-resource guard;
- Android local-first security guard;
- repository required-file/link audit;
- secret scan;
- JVM unit tests;
- instrumentation-test compilation;
- full Android lint;
- release compilation;

before uploading the unsigned APK artifact.

`scripts/check_repository.py` now requires the complete permanent documentation/ADR/brand-screenshot-policy set plus the Android/documentation guard scripts, rather than only a smaller subset.

### Permanent documentation reconciled

This pass deeply updated:

- `README.md` — public exhaustive documentation entry points, no-wrapper setup, documentation guard, CI/release boundaries;
- `CONTRIBUTING.md` — every tracked-file change must update the codebase reference; complete quality commands and persistence/UI rules;
- `docs/features.md` — calculator/history/template/persistence/backup/export/accessibility limits and actual behavior;
- `docs/development.md` — file ownership/documentation maintenance, persistence/manifest/resources/build rules, complete guard commands;
- `docs/testing.md` — documentation-coverage guard behavior and the full regression/CI/manual strategy;
- `docs/github-maintenance.md` — workflow responsibilities, documentation maintenance, dependency/template/release/handoff practices;
- `docs/setup.md` — explicit local Gradle/no-wrapper model, fast guards, build/test/release setup;
- `docs/architecture.md` — application composition, persistence/backup/resource/repository-documentation architecture;
- `docs/release.md` — separate source/automated/manual/distribution evidence classes and complete release procedure;
- `docs/verification.md` — exhaustive documentation consistency gates alongside source/runtime/security/signing gates;
- `docs/release-candidate-final-audit.md` — explicit exhaustive documentation source audit without faking pending runtime evidence;
- `CHANGELOG.md` — documentation engineering, release workflow hardening, required-doc expansion;
- `ROADMAP.md` — complete tracked-file documentation phase and still-open runtime/release gates;
- `docs/troubleshooting.md` — no-wrapper, documentation/resource/security/repository guard diagnostics plus persistence/Actions troubleshooting;
- `docs/design-system.md` — named-history/template dialog/search design contracts and screenshot/artwork distinction;
- `docs/logging.md` — `Locale.ROOT` redaction semantics, URI/backup/exception privacy rules, testing/documentation links.

The following existing deep documents were re-audited and remained consistent with the implementation without speculative rewrites:

- `docs/backup-restore.md`;
- `docs/persistence-invariants.md`;
- `docs/privacy-backup.md`;
- `docs/performance.md`.

Earlier continuation work had already reconciled `docs/security-backup.md` and `docs/accessibility.md` with current persistence/Unicode/dialog behavior.

### Documentation-pass commits

- `ececde2` — `docs: add exhaustive codebase file reference`
- `e07f7f1` — `docs: define documentation source-of-truth map`
- `a35ec62` — `ci: add tracked-file documentation coverage guard`
- `d3cac74` — `ci: enforce tracked-file documentation coverage`
- `5a52efe` — `ci: add documentation coverage to repository audit`
- `4a36c55` — `ci: require exhaustive documentation artifacts`
- `3a2f92b` — `docs: deepen implemented feature contract`
- `2f0b3ba` — `docs: add exhaustive documentation maintenance rules`
- `fc1f34c` — `docs: document tracked-file coverage testing`
- `9454ef5` — `docs: deepen repository maintenance guidance`
- `1a1aced` — `docs: clarify reproducible local build setup`
- `6084e4c` — `docs: connect architecture to exhaustive code reference`
- `a83bade` — `docs: require complete documentation in contributions`
- `bbd9a78` — `docs: deepen exact-commit release procedure`
- `728221b` — `docs: add complete documentation release gates`
- `7cb936f` — `docs: complete exhaustive repository documentation audit`
- `63f7bbf` — `docs: record exhaustive documentation engineering`
- `bdf07b8` — `docs: mark exhaustive documentation engineering complete`
- `9e9021d` — `docs: add documentation and build guard troubleshooting`
- `f3036e8` — `docs: expose exhaustive repository documentation`
- `775adf3` — `ci: harden tagged release verification gates`
- `93f34d8` — `ci: require complete permanent documentation set`
- `33de194` — `docs: document saved-name dialog design contract`
- `cf54f48` — `docs: deepen safe logging contract`
- `7a06ab3` — `docs: record hardened tagged release workflow`

## Final source audit continuation

A final release-focused static audit was performed against the active PR branch rather than the older `main` checkpoint. It rechecked the master specification, PR state, finance/persistence/backup/export/platform/UI source, workflows, repository guards, permanent documentation, and TODO/FIXME state.

Three concrete release defects were found and closed.

### Unicode-safe PDF export truncation

`PdfReceiptExporter` previously truncated a long rendered line with ordinary UTF-16 `take(...)`. A valid emoji or other supplementary Unicode code point crossing the line boundary could therefore be split into a dangling surrogate before drawing the PDF text.

The exporter now:

- routes line shortening through `ellipsizePdfLine(...)`;
- reuses `truncateUtf16Safely(...)` from the shared saved-name Unicode policy;
- preserves the configured 78-code-unit line budget including the ellipsis;
- rejects impossible line budgets below one character.

Existing `PathSafetyTest.kt` now also covers:

- an emoji crossing the truncation boundary;
- normal ASCII line-budget behavior;
- exact preservation of short Unicode text.

No extra tracked test file remains; the temporary focused file created during the audit was removed after its tests were consolidated into the already-documented JVM platform regression file.

### Strict backup document UTF-8 decoding

`BackupFileIo.read(...)` previously created `InputStreamReader` directly with `StandardCharsets.UTF_8`. Java decoder defaults may replace malformed input, which contradicted SpendCalc's fail-closed backup contract.

Backup document input now:

- uses `strictUtf8Decoder()`;
- configures malformed input with `CodingErrorAction.REPORT`;
- configures unmappable input with `CodingErrorAction.REPORT`;
- continues enforcing the 5,000,000-character read bound;
- fails before codec restore parsing when document bytes are not valid UTF-8.

`PathSafetyTest.kt` includes a direct malformed-byte regression against the strict decoder.

### Exact canonical currency validation during backup decode

`BackupCodec` already required canonical uppercase persisted currency forms during encode, but decoded history/template currency text was uppercased before shared-policy validation. A checksum-valid edited backup containing lowercase currency could therefore be silently normalized instead of rejected.

The codec now:

- validates decoded history currency text exactly as encoded;
- validates decoded converted-history currency text exactly as encoded;
- validates decoded template currency text exactly as encoded;
- validates decoded converted-template currency text exactly as encoded;
- relies on the shared canonical persisted-record policy to fail closed.

`BackupCodecPersistedPolicyTest.kt` now constructs checksum-valid modified backups and proves that noncanonical history and template currencies are rejected for semantic reasons rather than only because of checksum mismatch.

### Final documentation reconciliation

The final audit reconciled:

- `CHANGELOG.md` — records strict backup byte decoding, strict decoded canonical currencies, Unicode-safe PDF truncation, and regression coverage;
- `docs/security-backup.md` — documents the strict document-byte decoder and no-repair currency policy;
- `docs/testing.md` — records the direct regressions and adds the long-Unicode PDF manual release check;
- `docs/persistence-invariants.md` — defines strict decoded currency and document UTF-8 behavior as persistence/backup invariants;
- `what_changed.md` — this complete source/release handoff.

### Final-audit commits before this handoff

- `0652203` — `fix: preserve Unicode boundaries in PDF export`
- `292396d` — `fix: reject malformed UTF-8 backup files`
- `c59c988` — `fix: reject noncanonical backup currencies`
- `0bead1a` — `test: reject noncanonical currencies on backup decode`
- `e1b0900` — `refactor: make PDF truncation directly testable`
- `296616f` — `test: cover Unicode-safe PDF line truncation`
- `2297e2e` — `test: cover Unicode-safe PDF line truncation`
- `98a5534` — `chore: keep platform regressions in documented test file`
- `41bd195` — `refactor: expose strict backup decoder for regression tests`
- `be279fb` — `test: reject malformed UTF-8 backup bytes`
- `4ccd857` — `docs: record final backup and PDF hardening`
- `a4b7755` — `docs: clarify strict backup byte and currency validation`
- `3233301` — `docs: add final backup and PDF regression coverage`
- `2fa5e41` — `docs: align persistence contract with strict backup decode`

The commit containing this handoff is intentionally the final source/documentation change of this audit unless a concrete exact-head workflow failure exposes another defect. Its exact SHA must be read from GitHub after the commit and used for all subsequent automated/manual release evidence.

## Known verification limitation

The connected execution environment used for these continuations cannot resolve `github.com` for a clean clone/dependency download. Therefore:

- no local Gradle success is claimed;
- no local lint success is claimed;
- no connected Android test success is claimed;
- the documentation coverage guard is not claimed successful until the exact-head GitHub workflow runs it;
- queued/pending GitHub workflows are not treated as green.

This is a verification-environment limitation, not evidence of a source failure.

## Remaining release gates

### Automated

- exact-final-head documentation coverage success;
- exact-final-head CI success;
- exact-final-head CodeQL success;
- exact-final-head Dependency Review success;
- exact-final-head Repository Audit success.

### Manual Android/runtime

- `connectedDebugAndroidTest` on representative emulator/device;
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
- backup export/progress/restore/replaced-data review;
- offline/airplane-mode core workflow review;
- branded launch-splash review.

### Distribution

- capture real screenshots from the exact verified build using fictional data;
- provide production signing material outside source control;
- produce signed artifact from the exact verified commit;
- verify signed artifact version/checksum/source SHA;
- merge/tag/release only after all blocking gates pass.

## Database/migration state

- Room database version: 1.
- No previous public production schema exists yet.
- No fake v1→v2 migration has been added.
- Destructive migration fallback is intentionally not the default.
- Room schema export is configured under `app/schemas/`.
- Future tracked generated schema files must be preserved as migration/release evidence and individually added to `docs/codebase-reference.md`.
- The first future schema change must add an explicit Room migration and migration test.

## Next exact actions

1. Treat the exact commit containing this handoff as the new candidate head.
2. Stop speculative feature/source/documentation churn unless a concrete release-blocking defect or verified documentation contradiction is found.
3. Re-fetch PR `#12` exact head.
4. Re-fetch CI, CodeQL, Dependency Review, and Repository Audit for that exact head.
5. If a workflow fails, inspect the exact failing job/log and add the smallest regression-tested/documented fix.
6. Any fix creates a new head and requires fresh exact-head automated verification.
7. When automated checks are green, execute `docs/verification.md` manual Android/accessibility/export/backup gates.
8. Merge PR `#12` only when branch protection/repository policy and release gates allow it.
9. Capture real screenshots and sign outside source control.
10. Tag `v1.0.0` only after the exact signed release candidate satisfies every blocking gate.

## Continuation rule

While PR `#12` remains open, continue from `complete/v1-finalization`. After it is merged, continue from `main`. Do not use an older handoff, older branch head, or older successful run as proof for the newest revision. Inspect current source and exact-head workflow state first. Keep fixes small, meaningful, regression-tested, permanently documented, and reflected here.
