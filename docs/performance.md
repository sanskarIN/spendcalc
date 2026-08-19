# Performance

## Performance goals

SpendCalc is a local calculator, so common interactions should feel immediate without network dependencies or artificial delays.

Initial qualitative budgets:

- calculation recomputation should be effectively instantaneous for ordinary bills;
- typing in calculator fields should not trigger blocking disk I/O;
- database work must remain off the main thread through Room suspend/Flow APIs;
- launch should avoid network initialization;
- exports should be generated only after explicit user action;
- lists should remain responsive for typical local history/template sizes.

## Current hot paths

### Calculation

`CalculatorEngine` performs a small number of `BigDecimal` operations per item and charge. Correct decimal behavior is more important than replacing these operations with floating-point math.

### Compose recomposition

Calculator state is centralized in a `StateFlow`. Input changes recompute the result in memory. The calculation is small enough for normal bills; if profiling shows unusually large item lists cause frame pressure, move heavy parsing/calculation to a dedicated dispatcher only after measurement.

### Room

History and template reads use `Flow`; writes use suspend DAO methods. Current queries are simple full-list reads ordered by timestamp/name. If history grows enough to create measurable load, introduce paging/search and indexes based on measured queries.

### Export

CSV/text export is linear in item count. PDF generation is also linear and paginates vertically. Export work is user-triggered and currently synchronous at the call site; profiling should determine whether large receipts require background dispatch before adding complexity.

## Measurement plan

Before optimizing a release:

1. profile launch with Android Studio;
2. test calculator typing with 1, 20, 100, and 1,000 synthetic items;
3. profile history rendering with a large fictional dataset;
4. measure CSV/PDF generation at large item counts;
5. inspect allocations around repeated decimal parsing;
6. verify no database calls execute on the UI thread.

## Future benchmark triggers

Add a benchmark/macrobenchmark module when any of these become true:

- startup regresses noticeably after a dependency/feature change;
- calculation workloads expand beyond small local bills;
- history grows to thousands of records in typical use;
- performance work needs a regression guard in CI.

## Optimization constraints

Do not trade away correctness, privacy, or readability for unmeasured micro-optimizations. In particular, do not replace `BigDecimal` money math with `Double` or cache sensitive user content without a clear invalidation/privacy model.
