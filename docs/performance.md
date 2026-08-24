# Performance and Resource Use

SpendCalc is intentionally local-first and small. The current architecture favors deterministic correctness and bounded work over speculative optimization.

## Current design

- Financial calculation runs in memory with `BigDecimal` and a bounded item count.
- Calculator names, numeric text, currency codes, split counts, precision, and scale are bounded before expensive rendering/persistence paths.
- The calculator UI accepts at most 100 expense items and exposes the limit instead of silently creating an unbounded eager Compose tree.
- History and templates are observed through Room `Flow`s.
- History search is local, filters the already-observed history list in memory, and caps the interactive query at 120 characters.
- Settings are small DataStore preferences.
- Backup decoding has hard payload, line, record, field, decimal, and split limits.
- Backup document reads/writes, CSV file creation, and PDF generation run on `Dispatchers.IO` rather than the UI thread.
- Bounded backup encoding/decoding runs on `Dispatchers.Default`, so checksum/Base64/parser CPU work does not block Compose.
- History/templates are captured under one Room transaction for a consistent backup snapshot.
- Restore uses batch Room inserts inside the database transaction instead of issuing one DAO call per record.
- Backup operations are app-modal while work is active, preventing conflicting history/template mutations during replacement.
- FileProvider shares only app-private cache exports.
- Reduced-motion mode removes navigation transitions; otherwise navigation uses short fades.

## Deliberate limits

The calculator accepts at most 100 line items through the current UI state. This is a UI/performance budget for an eagerly composed editable bill, not a financial or accounting rule. For substantially larger datasets, a future design should use a virtualized editor rather than increasing this limit without measurement.

Split counts are limited to 1,000,000. Numeric inputs and persisted backup decimal shapes are bounded so scientific notation cannot create unexpectedly huge plain strings or pathological arithmetic/rendering work. Saved history result fields allow up to 34 integer digits because that is the bounded worst-case magnitude the supported 100-item calculator, charge ranges, and exchange-rate range can legitimately produce.

Saved history labels and template names use a shared 120-character persistence/backup bound. History search queries are also capped at 120 characters so the screen does not retain or repeatedly scan with arbitrarily large user-provided query strings.

The explicit backup format accepts at most 10,000 combined history/template records and approximately 5 million characters, with an additional pre-split newline bound.

These limits are defensive implementation boundaries rather than financial advice or business rules.

## History search

The current History screen performs case-insensitive substring matching against label, currency codes, subtotal/total, converted total, and per-person values. The interactive search query is capped at 120 characters and the UI explains that limit. This is simple and responsive for ordinary local histories. If real profiling shows large histories causing UI latency or memory pressure, move search/filtering into Room queries and expose paged results rather than increasing in-memory work blindly.

## Export and backup behavior

Text receipt sharing is generated in memory and handed to Android's share intent. CSV/PDF file creation occurs in the private cache export directory. Disk/PDF work is dispatched away from the main thread, then the already-created file is shared on the UI thread.

Explicit backup file reads/writes run on `Dispatchers.IO`; backup serialization, checksum generation, Base64 work, and decoding run on `Dispatchers.Default`. The app displays a modal indeterminate progress state while active backup work is applying, which also prevents saved-data mutations from racing a restore.

Android owns cache eviction. A future measured cache-growth problem may justify explicit stale-export cleanup, but it should be added from profiling evidence rather than by deleting files aggressively during active share flows.

## Benchmark policy

A macrobenchmark/profile module is not required for the first release unless measurements identify a performance regression. Before adding one, collect representative measurements for:

- first screen render after process start;
- calculator recomposition while editing amounts;
- 25/50/100-item calculation updates;
- History screen with realistic and stress-test record counts;
- backup snapshot/encode/decode/restore near supported limits;
- PDF generation for large valid item lists.

Performance changes should preserve finance correctness, validation bounds, accessibility semantics, and local-first privacy.
