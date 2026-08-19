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
- history-label trimming, blank fallback, and the shared 120-character persistence bound;
- template repository save/delete/exact-restore/replace behavior;
- template-name trimming, blank fallback, and the shared 120-character persistence bound;
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
- Settings backup busy/progress state and disabled duplicate backup actions;
- a real-activity journey that completes onboarding when needed, enters an amount, verifies the calculated result, saves it, navigates to History, and verifies the saved amount.

CI compiles the instrumentation suite on every pull request so Android tests cannot silently stop compiling. Actual emulator/device execution remains a documented release gate.

## Regression policy

Every confirmed bug should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- finance formula or validation defect -> pure JVM test/fuzz invariant;
- backup parser or integrity defect -> codec unit test;
- saved-name normalization defect -> repository unit test plus UI test when the user flow changes;
- Room replacement/migration defect -> Android database test;
- path containment/logging defect -> pure JVM platform helper test where possible;
- rendering/semantics/busy-state defect -> Compose or activity test.

## Database migrations

Database version 1 has no prior production schema to migrate from. When version 2 is introduced, the schema version must be incremented deliberately and a migration test must create the prior schema, execute the migration, validate preserved data, and verify Room's schema expectations. Destructive fallback is not the default migration strategy.

## CI expectations

The main CI workflow checks formatting, Kotlin package syntax, repository metadata/Markdown links, common secret patterns, JVM unit tests, instrumentation-test compilation, full Android lint, debug compilation, and release compilation. Separate workflows run CodeQL, dependency review, and a lightweight repository audit.

A release candidate should not proceed unless the checks associated with the exact commit being released complete successfully or a documented exception has been explicitly reviewed.

## Manual release checks

Before a production release:

1. run `connectedDebugAndroidTest` on a representative emulator/device;
2. review phone and tablet/wide layouts;
3. review light, dark, and system theme behavior;
4. enable large system font scale and the app large-text preference;
5. enable reduced motion and verify navigation motion is removed;
6. use TalkBack for major controls, dialogs, lists, primary navigation, and progress messaging;
7. save a calculation with a meaningful history label, verify it is searchable by that label, then create/search/delete/undo/clear/auto-delete history with controlled test data;
8. create/load/delete/undo templates;
9. verify the 100-item calculator limit is visible and the Add item action becomes disabled at the limit;
10. share text, CSV, and PDF exports;
11. export a backup, confirm the busy state, restore it after confirmation, and verify history/templates/preferences;
12. open GitHub/BMC/email actions from About;
13. verify the AndroidX branded launch splash on supported OS versions;
14. verify core workflows in airplane/offline mode.
