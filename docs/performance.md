# Performance and Resource Use

SpendCalc is intentionally local-first and small. The current architecture favors deterministic correctness and bounded work over speculative optimization.

## Current design

- Financial calculation runs in memory with `BigDecimal` and a bounded item count.
- Calculator names, numeric text, currency codes, split counts, precision, and scale are bounded before expensive rendering/persistence paths.
- History and templates are observed through Room `Flow`s.
- History search is local and currently filters the already-observed history list in memory.
- Settings are small DataStore preferences.
- Backup decoding has hard payload, line, record, field, decimal, and split limits.
- Backup document reads/writes, CSV file creation, and PDF generation run on `Dispatchers.IO` rather than the UI thread.
- FileProvider shares only app-private cache exports.
- Reduced-motion mode removes navigation transitions; otherwise navigation uses short fades.

## Deliberate limits

The calculator accepts at most 500 line items through the current UI state. Split counts are limited to 1,000,000. Numeric inputs and persisted backup decimal shapes are bounded so scientific notation cannot create unexpectedly huge plain strings or pathological arithmetic/rendering work.

The explicit backup format accepts at most 10,000 combined history/template records and approximately 5 million characters, with an additional pre-split newline bound.

These limits are defensive implementation boundaries rather than financial advice or business rules.

## History search

The current History screen performs case-insensitive substring matching against label, currency codes, subtotal/total, converted total, and per-person values. This is simple and responsive for ordinary local histories. If real profiling shows large histories causing UI latency or memory pressure, move search/filtering into Room queries and expose paged results rather than increasing in-memory work blindly.

## Export behavior

Text receipt sharing is generated in memory and handed to Android's share intent. CSV/PDF file creation occurs in the private cache export directory. Disk/PDF work is dispatched away from the main thread, then the already-created file is shared on the UI thread.

Android owns cache eviction. A future measured cache-growth problem may justify explicit stale-export cleanup, but it should be added from profiling evidence rather than by deleting files aggressively during active share flows.

## Benchmark policy

A macrobenchmark/profile module is not required for the first release unless measurements identify a performance regression. Before adding one, collect representative measurements for:

- first screen render after process start;
- calculator recomposition while editing amounts;
- 100/500-item calculation updates;
- History screen with realistic and stress-test record counts;
- backup encode/decode near supported limits;
- PDF generation for large valid item lists.

Performance changes should preserve finance correctness, validation bounds, accessibility semantics, and local-first privacy.
