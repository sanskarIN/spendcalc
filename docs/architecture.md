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
- export formatting contracts and platform-independent CSV/text implementations.

The domain layer uses `java.math.BigDecimal` for money and percentage arithmetic. UI code never owns calculation formulas.

### Data

Packages: `in.sanskar.spendcalc.data` and `in.sanskar.spendcalc.data.local`

Responsibilities:

- Room entities and DAOs;
- mapping persisted decimal strings back to `BigDecimal` domain values;
- calculation history repository;
- reusable template repository;
- DataStore preferences.

Decimals are persisted as plain decimal strings rather than binary floating-point numbers.

### Presentation

Package: `in.sanskar.spendcalc.ui`

Responsibilities:

- `SpendCalcViewModel` state and user actions;
- calculator form-state parsing;
- Compose navigation;
- responsive screens and reusable UI components;
- theme and design tokens.

The ViewModel coordinates repositories but does not depend on Android `Context`.

### Platform adapters

Package: `in.sanskar.spendcalc.platform`

Responsibilities:

- Android share intents;
- cache export files and `FileProvider` URIs;
- PDF generation with Android `PdfDocument`;
- opening external URLs/email apps safely.

These adapters isolate Android-specific behavior from finance logic.

## Dependency direction

```text
Compose UI -> ViewModel -> Domain + Repositories
Repositories -> Room/DataStore
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

## Export architecture

`ExportFormatter` defines platform-independent string export. Implementations currently include:

- CSV (`text/csv`), with common spreadsheet-formula prefixes neutralized in text fields;
- plain-text receipt (`text/plain`).

Android's `PdfReceiptExporter` creates an offline PDF using `PdfDocument`. Files are written under the app cache `exports/` directory and are shared via a non-exported `FileProvider` using temporary read permission.

## Error handling

Input is parsed at the presentation boundary and validated in the domain engine. Invalid input produces typed domain errors mapped to form issues. The calculation result is not produced until all required fields are valid.

Platform export/open-link failures are handled without crashing the calculation workflow and produce user-safe feedback.

## Accessibility and responsive behavior

- user-visible strings are Android resources;
- labels are attached to form controls;
- Material components preserve semantic roles and minimum touch targets;
- settings include larger text and reduced-motion preferences;
- calculator content switches to a two-column arrangement on wider screens;
- destructive clear-history action requires confirmation.

## Dependency wiring

`AppContainer` explicitly constructs Room, repositories, settings, and the calculation engine. `SpendCalcApplication` owns one lazy container. This keeps dependency setup simple and testable without introducing a dependency-injection framework.

## Architectural decisions

See `docs/adr/` for decisions that should remain understandable independently of source history.
