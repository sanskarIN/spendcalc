# SpendCalc 1.0.0 Release-Candidate Final Audit

Date: 2026-08-19

This file is the final source-audit checklist for the first public release candidate. It complements `docs/verification.md` and the root work-continuity document.

## Source completeness

- [x] Android app module and release/debug build configuration exist.
- [x] Kotlin/Compose application entry point exists.
- [x] Domain finance engine uses `BigDecimal` rather than floating-point arithmetic.
- [x] Itemized expenses, discount, tax, tip, service charge, manual currency conversion, and split calculation are implemented.
- [x] Editable calculator work is bounded to 100 line items with a visible limit state.
- [x] Receipt-style output is implemented.
- [x] Local Room history and saved templates are implemented.
- [x] History retention controls and local search are implemented.
- [x] Individual history and template deletion provide Undo recovery.
- [x] Text, CSV, and PDF export paths are implemented.
- [x] Explicit local backup/restore is implemented for history, templates, and preferences.
- [x] Backup work exposes a real busy/progress state and blocks duplicate operations while active.
- [x] Settings, onboarding, About, light/dark/system themes, large text, and reduced-motion preference are implemented.
- [x] Repository-owned primary-navigation vector icons and a branded AndroidX launch splash are implemented.
- [x] Required support, GitHub, funding, license, and `Made by the Sanskar` identity are present in product/repository documentation.

## Reliability and security

- [x] Finance inputs are validated centrally.
- [x] Currency normalization is locale-stable.
- [x] CSV text cells neutralize common spreadsheet-formula prefixes.
- [x] Export files use app cache + FileProvider rather than broad storage access.
- [x] Export path containment uses canonical path semantics.
- [x] Blocking backup/CSV/PDF file work runs away from the main thread.
- [x] Core app does not require Android Internet permission.
- [x] Backup parser is versioned, bounded, checksum-validated, and avoids arbitrary object deserialization.
- [x] Backup restore requires confirmation and coordinates rollback across Room/DataStore boundaries when needed.
- [x] Room history/template replacement is transactional.
- [x] Common secret-pattern scanning exists.
- [x] CodeQL and dependency-review workflows exist.
- [x] Structured logging boundary redacts common sensitive field categories using locale-stable key normalization.

## Regression coverage

- [x] Finance arithmetic and rounding tests.
- [x] Invalid/bounded finance input tests.
- [x] Locale normalization regression test.
- [x] CSV escaping/formula-prefix tests and deterministic fuzz coverage.
- [x] Receipt formatter test.
- [x] History repository deletion/restore coverage.
- [x] Template repository deletion/exact-restore coverage.
- [x] Backup codec round-trip, corruption, version, malformed/oversized input, and deterministic fuzz coverage.
- [x] Room round-trip and transactional replace-all instrumentation tests.
- [x] Compose calculator smoke test.
- [x] Settings backup-busy Compose regression test.
- [x] Primary calculate/save/history instrumentation journey coverage.
- [x] Kotlin reserved-namespace regression guard.
- [x] Repository required-file/local-link audit.

## Accessibility, UX, and performance source audit

- [x] Numeric inputs request appropriate Android number/decimal keyboards.
- [x] Validation includes explanatory text rather than color-only state.
- [x] Large-text and reduced-motion preferences affect the actual UI.
- [x] Primary navigation keeps visible labels and avoids duplicate icon announcements.
- [x] Destructive individual deletions provide Undo; destructive bulk/restore operations require confirmation.
- [x] Backup progress is represented by both progress UI and explanatory text.
- [x] Phone and wide/tablet Compose layouts exist.
- [x] Calculator eager-composition work is explicitly bounded.
- [x] Expensive document/PDF/file work is dispatched off the UI thread.
- [x] Performance budgets and future profiling thresholds are documented.

## Repository engineering

- [x] MIT license.
- [x] Contributor guide and code of conduct.
- [x] Security and support policies.
- [x] Privacy documentation.
- [x] Changelog, roadmap, and canonical work-continuity document.
- [x] Architecture/setup/development/testing/release/troubleshooting/accessibility/performance/design-system documentation.
- [x] ADR set includes precision arithmetic, local-first behavior, persistence, and backup-format decisions.
- [x] Issue and pull-request templates.
- [x] Dependabot configuration.
- [x] CI formatting/namespace/repository/secret checks, unit tests, instrumentation compilation, full Android lint, debug build, and release build.
- [x] CodeQL workflow.
- [x] Dependency review workflow.
- [x] Release-candidate artifact workflow.
- [x] Workflow concurrency prevents superseded PR revisions from consuming runner capacity indefinitely.
- [x] Funding configuration and Buy Me a Coffee documentation.
- [x] Editable logo source and real-screenshot policy.

## Verification boundary

Automated Android build/lint/test checks are authoritative only after the configured GitHub-hosted jobs finish for the exact final commit. Queued or pending jobs are not treated as success.

The source branch may remain open while runners are unavailable. `v1.0.0` must not be represented as a fully verified release until the automated checks and the manual Android accessibility/export/backup/device checks in `docs/verification.md` complete.

Production signing material, store credentials, real personal test data, and fabricated screenshots must remain outside source control.
