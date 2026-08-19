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
- Pre-handoff head for this update: `873b8ac2dd4c690ccf531b3143232920cc068f9f`.
- Pull request state at the start of this continuation: open, non-draft, mergeable.
- Source state: the planned application implementation and release-candidate source hardening are complete on `complete/v1-finalization`. This continuation closed additional repository/backup persistence-invariant gaps, expanded template naming/accessibility behavior, added duplicate-ID protection, deepened regression tests, and reconciled architecture/security/testing/release documentation.
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

## Exact workflow status observed during this continuation

At head `eaad544f3ed4e5bd9f8658b7a70dac181ee0ccaa`, GitHub reported:

- CI — `pending`;
- Dependency Review — `pending`;
- CodeQL — `queued`;
- Repository Audit — `queued`.

Earlier exact heads in the same continuation also repeatedly registered the same four workflow families as queued/pending. These states are not failures, but they are also not successful release verification. Workflow concurrency cancels superseded pull-request revisions, so no result from an older head should be used as proof for a newer one.

The connected execution container still cannot resolve `github.com`, so a clean local Gradle dependency resolution/build cannot be used as release proof in this environment. Do not claim local `gradle test`, lint, debug, release, or instrumentation results unless they are actually run in a network-capable environment. GitHub Actions remains the authoritative automated Android/Gradle verification source here.

## Work completed in this continuation

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
- structurally invalid/noncanonical in-memory records are rejected during encode rather than silently transformed.

This closes a subtle round-trip semantic issue: a programmatically constructed template with lowercase persisted currency could previously be encoded and decoded into a different uppercase object. Encoding now requires canonical persisted currency form.

`BackupCodecPersistedPolicyTest.kt` covers:

- noncanonical template currency rejected during encode;
- oversized history identifier rejected during encode;
- negative template timestamp rejected during encode.

Backup decode continues to reject duplicate IDs, unsupported schemas, malformed records, excessive sizes/lines, invalid decimal shapes, malformed UTF-8/Unicode, invalid saved names, invalid timestamps/currencies/splits, and corrupted checksums.

### 8. Persistence documentation

A dedicated `docs/persistence-invariants.md` now defines:

- the repository/backup compatibility invariant;
- ID rules;
- timestamp rules;
- saved-name rules;
- canonical currency rules;
- history result/split rules;
- template setting rules;
- replacement ordering;
- duplicate-ID handling;
- backup relationship;
- future schema/regression expectations.

The repository audit now requires this file.

`docs/architecture.md` now describes repositories as validation boundaries and links to the persistence contract.

`docs/security-backup.md` documents local repository validation in the backup threat model rather than treating only decoded backup files as untrusted.

`docs/testing.md` now includes the complete persistence-invariant regression strategy.

`docs/verification.md` includes automated/manual persistence, template-dialog, duplicate-ID, canonical-currency, and backup-alignment gates.

`docs/release-candidate-final-audit.md` records the persistence-invariant source and regression coverage as complete.

`docs/accessibility.md` includes template dialog guidance/confirmation/Unicode checks.

`README.md`, `ROADMAP.md`, and `CHANGELOG.md` now describe the hardened persistence model.

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
- Valid UTF-8/Unicode requirement.
- SHA-256 accidental-corruption detection.
- Strict record/schema/checksum/size/line/field/decimal/name/ID/timestamp/currency/split validation.
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
- Deterministic backup fuzz/regression tests.
- CSV safety.
- Receipt formatter.
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
- Android string-resource reference/duplicate-name audit.
- Android local-first manifest/FileProvider audit.
- Required-file/local Markdown-link repository audit.
- Common secret-pattern scan.

## Current CI/repository automation

Main `CI` performs:

1. formatting guard;
2. Kotlin namespace guard;
3. Android string-resource audit;
4. Android local-first security audit;
5. repository metadata/required-file/local-link audit;
6. secret-pattern scan;
7. JVM tests;
8. instrumentation-test compilation;
9. full Android lint;
10. debug build;
11. release build.

Separate workflows:

- CodeQL;
- Dependency Review;
- Repository Audit;
- Dependabot;
- tag-triggered unsigned Release Candidate artifact build.

Superseded PR revisions use concurrency cancellation.

## Commits added in this continuation

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

This handoff update creates one additional commit after the list above. Use GitHub to resolve its exact SHA before making release decisions.

## Known verification limitation

The connected execution environment used for this continuation cannot resolve `github.com` for a clean clone/dependency download. Therefore:

- no local Gradle success is claimed;
- no local lint success is claimed;
- no connected Android test success is claimed;
- queued/pending GitHub workflows are not treated as green.

This is a verification-environment limitation, not evidence of a source failure.

## Remaining release gates

### Automated

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
- text/CSV/PDF share-flow review;
- backup export/progress/restore/replaced-data review;
- offline/airplane-mode core workflow review;
- branded launch-splash review.

### Distribution

- capture real screenshots from the exact verified build using fictional data;
- provide production signing material outside source control;
- produce signed artifact from the exact verified commit;
- verify signed artifact;
- merge/tag/release only after all blocking gates pass.

## Database/migration state

- Room database version: 1.
- No previous public production schema exists yet.
- No fake v1→v2 migration has been added.
- Destructive migration fallback is intentionally not the default.
- The first future schema change must add an explicit Room migration and migration test.

## Next exact actions

1. Treat the commit containing this handoff as the new candidate head.
2. Stop speculative feature/source churn unless a concrete release-blocking defect is found.
3. Re-fetch PR `#12` exact head.
4. Re-fetch CI, CodeQL, Dependency Review, and Repository Audit for that exact head.
5. If a workflow fails, inspect the exact failing job/log and add the smallest regression-tested fix.
6. Any fix creates a new head and requires fresh exact-head automated verification.
7. When automated checks are green, execute `docs/verification.md` manual Android/accessibility/export/backup gates.
8. Merge PR `#12` only when branch protection/repository policy and release gates allow it.
9. Capture real screenshots and sign outside source control.
10. Tag `v1.0.0` only after the exact signed release candidate satisfies every blocking gate.

## Continuation rule

While PR `#12` remains open, continue from `complete/v1-finalization`. After it is merged, continue from `main`. Do not use an older handoff, older branch head, or older successful run as proof for the newest revision. Inspect current source and exact-head workflow state first. Keep fixes small, meaningful, regression-tested, documented, and reflected here.
