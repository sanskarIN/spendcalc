# Testing Strategy

SpendCalc treats financial correctness, restore integrity, privacy boundaries, and user-controlled data handling as high-risk behavior. Tests stay as close as practical to the layer they verify.

## JVM unit tests

Run:

```bash
gradle testDebugUnitTest
```

Coverage includes:

- subtotal, discount, tax, tip, service charge, conversion, and split arithmetic;
- decimal precision such as `0.10 + 0.20` and half-up money rounding;
- discount <= 100%, bounded charge percentages, non-negative amounts, three-letter currency codes, positive exchange rates, bounded decimal precision/scale, and split counts from 1 through 1,000,000;
- rejection of scientific-notation shapes that could expand dramatically when converted to plain decimal text;
- locale-independent currency normalization;
- history repository save/delete/restore/clear/retention behavior;
- history-label trimming, blank fallback, shared 120-character persistence bound, exact valid restore behavior, and over-limit restore rejection;
- history persistence-envelope rejection for invalid IDs, timestamps, split counts, stored result magnitudes, negative stored values, and noncanonical result inputs after normalization;
- history replace-all prevalidation that leaves existing records untouched when any candidate record is invalid;
- template repository save/delete/exact-restore/replace behavior;
- template-name trimming, blank fallback, shared 120-character persistence bound, exact valid restore behavior, and over-limit restore rejection;
- template repository finance validation for discount/tax/tip/service, split, exchange-rate, and currency settings even when the caller bypasses the ViewModel;
- template persistence-envelope validation for IDs/timestamps/canonical currencies plus replace-all prevalidation;
- confirmation that template save validates only settings that are actually persisted, not line items that templates intentionally discard;
- UTF-16-safe saved-name truncation, malformed-surrogate rejection, and recovery from a previously split trailing surrogate boundary;
- shared persisted-record policy coverage for IDs, timestamps, canonical currencies, split bounds, saved names, and stored history decimal shapes;
- backup round-trip coverage for a saved label whose emoji crosses the saved-name limit boundary;
- backup encode rejection for invalid persisted-record envelopes, including noncanonical template currency and invalid history identifiers;
- CSV quoting, embedded quotes, and spreadsheet-formula neutralization;
- text receipt output;
- versioned backup round trips, Unicode text, corruption detection, unsupported schemas, duplicate identifiers, structural limits, checksum validation, and exponent-expansion rejection;
- safe log redaction, including Turkish-locale regression coverage;
- canonical export path containment, including sibling-prefix bypass prevention.

## Deterministic fuzz/regression tests

The repository includes seed-based JVM fuzz loops so failures are reproducible.

Current invariants include:

- valid generated finance input remains deterministic and produces non-negative rounded totals;
- generated negative item amounts fail validation rather than throwing;
- generated Unicode backup labels/names round trip exactly;
- deterministic mutations of a checksummed backup body fail integrity verification.

These are regression-fuzz tests rather than an external randomized test service, so CI receives the same cases on every run.

## Android integration and UI tests

Compile the instrumentation suite without an emulator:

```bash
gradle assembleDebugAndroidTest
```

Run it on a connected emulator/device:

```bash
gradle connectedDebugAndroidTest
```

Android coverage includes:

- Room history round trip;
- Room template round trip;
- backup replacement across persisted Room records;
- Compose calculator/receipt smoke rendering;
- Compose named-history save dialog rendering, text entry, and callback wiring;
- Compose history-label Unicode-boundary behavior;
- Compose template dialog naming guidance, callback wiring, distinct confirm semantics, and Unicode-boundary behavior;
- Compose History filtering that verifies a saved label is found while non-matching entries disappear;
- Settings backup busy/progress state and disabled duplicate backup actions;
- a real-activity journey that completes onboarding when needed, enters an amount, verifies the calculated result, saves it with a meaningful label, navigates to History, and verifies both the saved label and amount.

CI compiles the instrumentation suite on every pull request so Android tests cannot silently stop compiling. Actual emulator/device execution remains a documented release gate.

## Repository guard tests

Fast Python guards run before the Android build work:

- formatting/whitespace and line-length checks;
- Kotlin namespace/package checks;
- Android default string-resource reference and duplicate-name audit;
- Android local-first manifest/FileProvider security checks;
- repository required-file/local Markdown-link audit;
- common secret-pattern scanning.

The Android string-resource audit scans Kotlin sources plus manifest/resource XML references against default strings under `app/src/main/res/values/`. This catches missing `R.string.*`/`@string/*` references and duplicate default string names before resource compilation.

## Persistence invariant policy

History and template repositories are treated as trust boundaries, not passive DAO wrappers. A caller that bypasses Compose/ViewModel code must still be unable to store data that the explicit backup codec later rejects.

The persisted-record policy therefore checks:

- nonblank, bounded, well-formed identifiers;
- nonnegative creation timestamps;
- valid saved names;
- canonical three-letter stored currency codes;
- supported history split counts;
- nonnegative bounded stored history result decimals;
- template finance settings through `CalculatorEngine`.

Repositories normalize only fields that are intentionally canonicalized, such as currency codes and newly entered names. Valid restored names remain exact. `replaceAll` maps and validates every candidate before invoking the DAO replacement operation, so one invalid record cannot clear existing data first.

The backup codec reuses the same persisted-record policy for history and template envelopes. This prevents repository and backup rules from drifting apart.

## Regression policy

Every confirmed bug should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- finance formula or validation defect -> pure JVM test/fuzz invariant;
- backup parser or integrity defect -> codec unit test;
- repository/backup persistence-contract drift -> persisted-record policy plus repository/codec tests;
- saved-name normalization/Unicode-boundary defect -> saved-name policy + repository test, plus UI coverage when the user flow changes;
- history search/filter defect -> focused Compose History test;
- Room replacement/migration defect -> Android database test;
- missing Android string-resource reference -> fast Python resource audit;
- path containment/logging defect -> pure JVM platform helper test where possible;
- rendering/semantics/busy-state defect -> Compose or activity test.

## Database migrations

Database version 1 has no prior production schema to migrate from. When version 2 is introduced, the schema version must be incremented deliberately and a migration test must create the prior schema, execute the migration, validate preserved data, and verify Room's schema expectations. Destructive fallback is not the default migration strategy.

## CI expectations

The main CI workflow checks formatting, Kotlin package syntax, Android string resources, Android local-first security policy, repository metadata/Markdown links, common secret patterns, JVM unit tests, instrumentation-test compilation, full Android lint, debug compilation, and release compilation. Separate workflows run CodeQL, dependency review, and a lightweight repository audit. The lightweight audit also executes the Android string-resource guard.

A release candidate should not proceed unless the checks associated with the exact commit being released complete successfully or a documented exception has been explicitly reviewed.

## Manual release checks

Before a production release:

1. run `connectedDebugAndroidTest` on a representative emulator/device;
2. review phone and tablet/wide layouts;
3. review light, dark, and system theme behavior;
4. enable large system font scale and the app large-text preference;
5. enable reduced motion and verify navigation motion is removed;
6. use TalkBack for major controls, dialogs, lists, primary navigation, and progress messaging;
7. save a calculation with a meaningful history label, verify it is searchable by that label, confirm the search query stops at 120 characters, then create/search/delete/undo/clear/auto-delete history with controlled test data;
8. paste a Unicode-heavy saved name near the 120-character boundary and verify the UI remains valid and the resulting backup exports/restores successfully;
9. create/load/delete/undo templates, verify the template dialog explains its 120-character limit, and verify its confirm action is announced distinctly from the underlying `Save template` control;
10. verify the 100-item calculator limit is visible and the Add item action becomes disabled at the limit;
11. share text, CSV, and PDF exports;
12. export a backup, confirm the busy state, restore it after confirmation, and verify history/templates/preferences;
13. open GitHub/BMC/email actions from About;
14. verify the AndroidX branded launch splash on supported OS versions;
15. verify core workflows in airplane/offline mode.
