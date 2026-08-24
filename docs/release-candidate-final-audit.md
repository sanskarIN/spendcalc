# SpendCalc 2.0.12 Release-Candidate Final Audit

Date: 2026-08-19

This file is the final **source-level** audit checklist for the current `2.0.12` production release candidate. It complements [`verification.md`](verification.md), [`codebase-reference.md`](codebase-reference.md), and the root work-continuity document. Checked items below mean the implementation/documentation exists and was source-audited; they do not imply pending CI/device/signing/screenshot gates have run successfully.

## Source completeness

- [x] Android app module and release/debug build configuration exist.
- [x] Application metadata targets `versionName = "2.0.12"` and monotonic Android `versionCode = 20012`.
- [x] Room database and explicit backup schema compatibility versions remain independent from the app release number; no artificial migration was introduced for the version bump.
- [x] Kotlin/Compose application entry point exists.
- [x] Domain finance engine uses `BigDecimal` rather than floating-point arithmetic.
- [x] Itemized expenses, discount, tax, tip, service charge, manual currency conversion, and split calculation are implemented.
- [x] Editable calculator work is bounded to 100 line items with a visible limit state.
- [x] Receipt-style output is implemented.
- [x] Local Room history and saved templates are implemented.
- [x] Users can optionally name saved history records; labels are bounded consistently across UI, persistence, and backup validation.
- [x] History retention controls and local search, including label search, are implemented.
- [x] History search input is bounded to 120 characters and uses surrogate-safe truncation.
- [x] Template save dialog exposes the same 120-character naming guidance and surrogate-safe truncation.
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
- [x] New saved history/template names are trimmed, bounded, defaulted, and truncated with a shared UTF-16-safe policy at the repository boundary rather than trusting only UI callers.
- [x] The shared 120-character saved-name contract is reused by backup validation so normal persisted data cannot later violate backup name limits.
- [x] Valid accepted history/template names entering restore/replace paths are validated and preserved exactly instead of being silently normalized again.
- [x] Saved-name truncation never leaves a valid surrogate pair split at the boundary, and malformed surrogate input fails closed.
- [x] PDF receipt line truncation reuses Unicode-safe truncation so a supplementary code point crossing the ellipsis boundary cannot leave a dangling surrogate.
- [x] A shared persisted-record policy defines ID, timestamp, canonical-currency, saved-name, history split, saved-result-shape, and batch-identifier rules.
- [x] History persistence validates records before DAO writes, including direct save/restore callers that bypass ViewModel/UI code.
- [x] Template persistence validates both structural envelope fields and the exact finance settings it stores through `CalculatorEngine`.
- [x] `AppContainer` supplies the same `CalculatorEngine` validator instance to template persistence used by the app domain layer.
- [x] History/template batch replacement rejects duplicate IDs before DAO replacement.
- [x] History/template batch replacement maps and validates every candidate before invoking the DAO, so an invalid record cannot clear valid existing data first.
- [x] Backup validation reuses shared persisted-record predicates rather than maintaining a separate structural policy.
- [x] Backup encoding rejects noncanonical in-memory persisted records instead of silently changing their semantics during serialization.
- [x] Backup decode validates persisted history/template currencies in their exact decoded form and rejects noncanonical forms instead of uppercasing/repairing them.
- [x] Backup document input uses a strict UTF-8 decoder that reports malformed and unmappable byte sequences rather than silently replacing them.
- [x] CSV text cells neutralize common spreadsheet-formula prefixes.
- [x] Export files use app cache + FileProvider rather than broad storage access.
- [x] Export path containment uses canonical path semantics.
- [x] Blocking backup/CSV/PDF file work runs away from the main thread.
- [x] Core app does not require Android Internet permission.
- [x] Backup parser is versioned, bounded, checksum-validated, and avoids arbitrary object deserialization.
- [x] Backup restore requires confirmation and coordinates rollback across Room/DataStore boundaries when needed.
- [x] Room history/template replacement is transactional.
- [x] Android system-backup/data-extraction policy files are documented separately from SpendCalc's explicit user-created backup format.
- [x] Common secret-pattern scanning exists.
- [x] CodeQL and dependency-review workflows exist.
- [x] Structured logging boundary redacts common sensitive field categories using locale-stable key normalization.

## Regression coverage

- [x] Finance arithmetic and rounding tests.
- [x] Invalid/bounded finance input tests.
- [x] Locale normalization regression test.
- [x] CSV escaping/formula-prefix tests and deterministic fuzz coverage.
- [x] Receipt formatter test.
- [x] History repository deletion/restore plus saved-label normalization, exact-restore, over-limit rejection, persistence-envelope, canonical-currency, and replace-all prevalidation coverage.
- [x] Template repository deletion/exact-restore plus saved-name normalization, envelope, finance-validation, canonical-currency, and replace-all prevalidation coverage.
- [x] Repository duplicate-ID replacement tests prove existing history/templates survive rejected duplicate candidate sets.
- [x] Shared saved-name policy tests cover surrogate-safe truncation, malformed UTF-16 rejection, fallback behavior, and repair of a previously split trailing surrogate boundary.
- [x] Shared persisted-record policy tests cover IDs, timestamps, currencies, history split/result bounds, and template envelopes.
- [x] Backup codec coverage proves a saved label whose emoji crosses the 120-character boundary remains exportable and round-trips exactly after safe normalization.
- [x] Backup codec persisted-policy tests reject noncanonical template currencies, invalid history identifiers, and negative template timestamps during encode.
- [x] Backup codec persisted-policy tests reject checksum-valid history/template records whose decoded currency text is noncanonical.
- [x] Backup codec round-trip, corruption, version, malformed/oversized input, and deterministic fuzz coverage.
- [x] Strict backup UTF-8 decoder regression rejects malformed byte sequences.
- [x] PDF line-truncation regression covers a surrogate pair crossing the truncation boundary, normal ASCII budget behavior, and short Unicode preservation.
- [x] Room round-trip and transactional replace-all instrumentation tests.
- [x] Compose calculator smoke test.
- [x] Named-history save dialog Compose regression coverage, including an emoji at the saved-name boundary.
- [x] Template save dialog Compose regression coverage for guidance, distinct confirm action, callback, and Unicode boundary behavior.
- [x] History label-filter Compose regression coverage.
- [x] Settings backup-busy Compose regression test.
- [x] Primary calculate/named-save/history instrumentation journey coverage verifies both label and amount.
- [x] Kotlin reserved-namespace regression guard.
- [x] Android default string-resource reference/duplicate-name guard.
- [x] Android manifest/FileProvider local-first security guard.
- [x] Repository required-file/local-link audit.
- [x] Tracked-file documentation coverage guard rejects missing, stale, and duplicate file-reference entries.

## Accessibility, UX, and performance source audit

- [x] Numeric inputs request appropriate Android number/decimal keyboards.
- [x] Validation includes explanatory text rather than color-only state.
- [x] Named-history save uses a titled dialog, labeled optional text field, visible 120-character guidance, concise Save action, and Cancel path.
- [x] Template save uses a titled dialog, labeled text field, visible 120-character guidance, concise Save action, and Cancel path.
- [x] Template dialog confirm text is distinct from the underlying `Save template` calculator action, avoiding ambiguous screen-reader/test targeting.
- [x] History search explains its 120-character limit and bounds repeated in-memory filtering work.
- [x] Large-text and reduced-motion preferences affect the actual UI.
- [x] Primary navigation keeps visible labels and avoids duplicate icon announcements.
- [x] Destructive individual deletions provide Undo; destructive bulk/restore operations require confirmation.
- [x] Backup progress is represented by both progress UI and explanatory text.
- [x] Phone and wide/tablet Compose layouts exist.
- [x] Calculator eager-composition work is explicitly bounded.
- [x] Expensive document/PDF/file work is dispatched off the UI thread.
- [x] Performance budgets and future profiling thresholds are documented.

## Exhaustive documentation audit

- [x] Every current tracked root policy/build/handoff file is described individually in `docs/codebase-reference.md`.
- [x] Every `.github` funding/template/Dependabot/workflow file is described individually.
- [x] Every app build/schema metadata file is described individually.
- [x] Every production Kotlin bootstrap/data/domain/platform/UI/theme/screen/component file is described individually.
- [x] Every Android manifest/drawable/values/xml resource file is described individually.
- [x] Every JVM unit/fuzz/regression test file is described individually.
- [x] Every Android instrumentation/Compose/activity test file is described individually.
- [x] Every repository guard script is described individually.
- [x] Every permanent documentation/ADR/asset/compatibility handoff file is described individually.
- [x] `docs/documentation-map.md` defines authority and update relationships among public, architecture, persistence/security/privacy, testing, setup/maintenance, release, ADR, planning, and handoff docs.
- [x] `scripts/check_documentation_coverage.py` makes the exhaustive file index self-maintaining by comparing it to `git ls-files`.
- [x] Main CI runs the documentation-coverage guard before Android build work.
- [x] Lightweight Repository Audit also runs documentation coverage.
- [x] `scripts/check_repository.py` requires the exhaustive codebase reference, documentation map, and coverage guard.
- [x] Contributor/development/setup/testing/release docs explain how to maintain the tracked-file documentation invariant.
- [x] The intentional absence of a committed Gradle wrapper is explicitly documented rather than treated as an omitted file.
- [x] Future tracked Room schema files are explicitly called out as documentation-covered migration/release evidence.
- [x] Brand artwork is distinguished from real release screenshots; fabricated screenshots are prohibited as release evidence.

## Repository engineering

- [x] MIT license.
- [x] Contributor guide and code of conduct.
- [x] Security and support policies.
- [x] Privacy documentation, including local history-label/backup handling.
- [x] Changelog, roadmap, and canonical work-continuity document.
- [x] Architecture/setup/development/testing/release/troubleshooting/accessibility/performance/design-system documentation.
- [x] Dedicated `docs/persistence-invariants.md` defines repository/backup structural contracts and replacement ordering.
- [x] Exhaustive `docs/codebase-reference.md` documents the complete tracked repository.
- [x] `docs/documentation-map.md` defines documentation source-of-truth/anti-drift rules.
- [x] Repository audit requires persistence and exhaustive documentation artifacts.
- [x] ADR set includes precision arithmetic, local-first behavior, persistence, and backup-format decisions.
- [x] Issue and pull-request templates.
- [x] Dependabot configuration.
- [x] CI formatting/namespace/documentation/string-resource/security/repository/secret checks, unit tests, instrumentation compilation, full Android lint, debug build, and release build.
- [x] Lightweight Repository Audit runs repository metadata/link, tracked-file documentation, and Android string-resource guards.
- [x] CodeQL workflow.
- [x] Dependency review workflow.
- [x] Release-candidate artifact workflow.
- [x] Workflow concurrency prevents superseded PR revisions from consuming runner capacity indefinitely.
- [x] Funding configuration and Buy Me a Coffee documentation.
- [x] Editable logo source and real-screenshot policy.

## Verification boundary

Automated Android build/lint/test/security/repository checks are authoritative only after the configured GitHub-hosted jobs finish for the **exact final commit**. Queued, pending, cancelled, skipped, or superseded jobs are not treated as success.

This audit can mark source/documentation existence and structural review complete, but it cannot execute a connected Android runtime, perform TalkBack review, externally sign an artifact, capture real screenshots, or verify store distribution. Those remain explicit unchecked gates in `docs/verification.md` until actually performed.

The source branch may remain open while runners are unavailable. `v2.0.12` must not be represented as a fully verified release until the exact-head automated checks and manual Android accessibility/export/backup/device/signing/screenshot checks complete.

Production signing material, store credentials, real personal test data, and fabricated screenshots must remain outside source control.
