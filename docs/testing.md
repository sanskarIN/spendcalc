# Testing Strategy

SpendCalc treats calculation correctness as the highest-risk behavior and keeps tests close to the layer they verify.

## Unit tests

Run:

```bash
gradle testDebugUnitTest
```

Current unit coverage includes:

- subtotal, discount, tax, tip, service charge, conversion, and split arithmetic;
- decimal precision such as `0.10 + 0.20`;
- half-up money rounding;
- validation for negative amounts, malformed currency codes, exchange rates, and split counts;
- history repository persistence mapping and retention purge logic;
- template repository mapping;
- CSV quote escaping and spreadsheet-formula neutralization;
- text receipt output.

## Android integration tests

Run on an emulator or connected device:

```bash
gradle connectedDebugAndroidTest
```

Current Android tests cover:

- Room history round trip;
- Room template round trip;
- Compose calculator/receipt smoke rendering.

## Regression policy

Every confirmed bug should receive a regression test at the lowest practical layer before or with the fix.

Examples:

- finance formula defect -> pure JVM unit test;
- Room mapping/migration defect -> Android database test;
- state interaction defect -> ViewModel test;
- rendering/semantics defect -> Compose test.

## Rounding cases to preserve

Tests should include:

- amounts ending near half-cent boundaries;
- split counts that produce repeating decimals;
- very small manual exchange rates;
- large but valid percentages;
- zero subtotal and empty item list;
- multiple line items whose decimal representation would be unsafe with binary floating point.

## Future property/fuzz testing

Property-based testing is planned for parser/export edge cases. Useful invariants include:

- non-negative valid input never produces a negative subtotal;
- total equals discounted base plus all configured charges at intermediate precision;
- converted total equals total multiplied by positive exchange rate under the rounding policy;
- CSV output remains structurally quoted for arbitrary Unicode names;
- parser failures never crash calculation flow.

## Database migrations

Database version 1 has no prior production schema to migrate from. When version 2 is introduced, add a migration test that creates the prior schema, migrates it, validates data, and verifies Room's schema expectations.

## CI expectations

A release candidate should not proceed unless:

- unit tests pass;
- lint passes;
- debug and release compilation pass;
- instrumentation tests are executed when an emulator runner is available;
- dependency/static-security checks pass or have a documented, reviewed exception.

## Manual checks

Before a release:

1. test phone and tablet/wide layouts;
2. enable dark mode;
3. enable large text/font scaling;
4. use TalkBack for major controls;
5. create/delete history and templates;
6. test 30/90-day retention behavior with controlled test data;
7. share text, CSV, and PDF exports;
8. open GitHub/BMC/email actions from About;
9. verify airplane/offline use for core workflows.
