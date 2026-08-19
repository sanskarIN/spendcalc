# Architecture

## Goals

SpendCalc uses a small modular-monolith style inside a single Android application module. The architecture optimizes for finance correctness, offline operation, straightforward testing, and a codebase that can grow without making simple calculations difficult to understand.

## Layers

### Domain

Package: `in.sanskar.spendcalc.domain`

Responsibilities:

- expense models;
- precision-safe calculation rules;
- validation;
- rounding policy;
- persisted-record structural policy;
- export formatting contracts and platform-independent CSV/text implementations.

The domain layer uses `java.math.BigDecimal` for money and percentage arithmetic. UI code never owns calculation formulas. Shared saved-name and persisted-record policies live in the domain model package so repositories and backup validation consume the same structural rules without depending on Android or Room.

### Data

Packages: `in.sanskar.spendcalc.data` and `in.sanskar.spendcalc.data.local`

Responsibilities:

- Room entities and DAOs;
- mapping persisted decimal strings back to `BigDecimal` domain values;
- calculation history repository;
- reusable template repository;
- repository-boundary persistence validation;
- DataStore preferences.

Decimals are persisted as plain decimal strings rather than binary floating-point numbers.

Repositories are validation boundaries, not passive DAO wrappers. They normalize intended canonical fields, validate records before writes, reject duplicate IDs before batch replacement, and map every candidate replacement record before invoking DAO replacement. This keeps normal local persistence compatible with explicit backup rules.

Full persistence contract: [`persistence-invariants.md`](persistence-invariants.md)

### Presentation

Package: `in.sanskar.spendcalc.ui`

Responsibilities:

- `SpendCalcViewModel` state and user actions;
- calculator form-state parsing;
- Compose navigation;
- responsive screens and reusable UI components;
- theme and design tokens.

The ViewModel coordinates repositories but does not depend on Android `Context`. Repository validation remains authoritative even if a caller bypasses ViewModel/UI code.

### Platform adapters

Package: `in.sanskar.spendcalc.platform`

Responsibilities:

- Android share intents;
- cache export files and `FileProvider` URIs;
- PDF generation with Android `PdfDocument`;
- bounded backup file I/O;
- opening external URLs/email apps safely.

These adapters isolate Android-specific behavior from finance logic.

## Dependency direction

```text
Compose UI -> ViewModel -> Domain + Repositories
Repositories -> shared domain validation -> Room/DataStore
Backup codec -> shared domain validation
Platform export adapters -> Android framework
Domain -> Kotlin/JDK only
```

The domain layer does not depend on Room, Compose, Activity, Context, or Android resources.

## Calculation order

The current documented rule is:

1. Sum item amounts into subtotal.
2. Calculate discount from subtotal.
3. Subtract discount to create the discounted/taxable base.
4. Calculate tax, tip, and service charge from that discounted base.
5. Sum the base and charges into total.
6. Apply the manual exchange rate.
7. Round monetary outputs with the configured `RoundingPolicy`.
8. Split totals by the requested number of people.

Changing charge order is a behavior change and requires tests plus release notes.

## Persistence

Room database: `spendcalc.db`

Tables:

- `calculation_history`
- `calculation_templates`

Database schema version starts at `1`. Future schema changes must use explicit Room migrations; destructive fallback is intentionally not configured.

DataStore: `spendcalc_settings`

Preferences include theme mode, large text, reduced motion, history retention policy, and onboarding completion.

### Persistence invariants

The shared persisted-record policy defines structural limits for record IDs, timestamps, saved names, canonical currencies, history split counts, and stored history result shapes. Template finance settings are additionally validated through `CalculatorEngine`.

Batch `replaceAll` operations reject duplicate IDs and validate/map the complete candidate set before calling the DAO, ensuring an invalid candidate cannot cause valid existing data to be cleared first.

The explicit backup codec independently enforces format/security rules while reusing persisted-record predicates. A normally persisted record should therefore be exportable without a cleanup pass.

See [`persistence-invariants.md`](persistence-invariants.md) and [`security-backup.md`](security-backup.md).

## Backup architecture

`BackupRepository` coordinates history, templates, and preferences. History/templates are snapshot/replaced under Room transactions. DataStore cannot participate in that database transaction, so restore snapshots the previous complete state and performs compensating rollback if the cross-store operation fails after Room replacement.

`BackupCodec` is versioned, bounded, checksum-protected for accidental corruption detection, and fail-closed for malformed/unsupported records. It does not deserialize arbitrary objects or execute backup content.

Backup file reading/writing occurs through Android's Storage Access Framework and runs away from the UI thread.

## Export architecture

`ExportFormatter` defines platform-independent string export. Implementations currently include:

- CSV (`text/csv`), with common spreadsheet-formula prefixes neutralized in text fields;
- plain-text receipt (`text/plain`).

Android's `PdfReceiptExporter` creates an offline PDF using `PdfDocument`. Files are written under the app cache `exports/` directory and are shared via a non-exported `FileProvider` using temporary read permission.

## Error handling

Input is parsed at the presentation boundary and validated in the domain engine. Invalid input produces typed domain errors mapped to form issues. The calculation result is not produced until all required fields are valid.

Repository persistence also validates domain records before DAO writes. Backup decoding validates untrusted backup records before repository replacement begins.

Platform export/open-link failures are handled without crashing the calculation workflow and produce user-safe feedback.

## Accessibility and responsive behavior

- user-visible strings are Android resources;
- labels are attached to form controls;
- Material components preserve semantic roles and minimum touch targets;
- named-history and template dialogs expose visible length guidance and distinct confirm/cancel actions;
- settings include larger text and reduced-motion preferences;
- calculator content switches to a two-column arrangement on wider screens;
- destructive clear-history action requires confirmation.

## Dependency wiring

`AppContainer` explicitly constructs Room, repositories, settings, and the calculation engine. `SpendCalcApplication` owns one lazy container. The same `CalculatorEngine` instance is supplied to template persistence so calculator, repository, and backup template validation use one finance rule implementation. This keeps dependency setup simple and testable without introducing a dependency-injection framework.

## Architectural decisions

See `docs/adr/` for decisions that should remain understandable independently of source history.
