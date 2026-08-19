# SpendCalc Roadmap

The roadmap prioritizes correctness, privacy, accessibility, and maintainability over feature count.

## Phase 0 — Repository foundation

- [x] Android/Kotlin/Compose build configuration.
- [x] Repository standards and policy files.
- [x] Architecture, privacy, security, and support direction.
- [x] Pull-request CI, CodeQL, dependency review, repository audit, namespace check, formatting check, Android resource/security checks, and secret-pattern scan are configured.
- [ ] Current release-candidate workflow results are green on the final PR head.

## Phase 1 — Core calculator MVP

- [x] Precision-safe decimal arithmetic.
- [x] Itemized expenses.
- [x] Discount, tax, tip, service charge, split bill.
- [x] Manual currency exchange rate.
- [x] Receipt-style result view.
- [x] Bounded finance-input validation and non-negative-result invariants.
- [x] Unit and deterministic fuzz coverage for finance arithmetic and rounding.

## Phase 2 — Persistence and reusable workflows

- [x] Room history.
- [x] Optional named history saves with a shared persistence/backup name bound.
- [x] Saved templates.
- [x] DataStore settings.
- [x] Optional history auto-delete.
- [x] History search/filter, including user-provided labels.
- [x] Undo for individual history deletion.
- [x] Undo for individual template deletion.
- [x] CSV/text/PDF export paths.
- [x] User-driven versioned backup/restore for history, templates, and preferences.
- [x] Shared persisted-record policy for IDs, timestamps, canonical currencies, names, history splits/results, and replacement-ID uniqueness.
- [x] Repository-boundary history validation prevents locally persisted records from violating backup structural rules.
- [x] Repository-boundary template finance validation reuses `CalculatorEngine` even for callers that bypass the ViewModel.
- [x] Batch history/template replacement validates all candidates and duplicate IDs before DAO replacement.
- [x] Backup codec reuses persisted-record structural predicates.
- [x] Onboarding, appearance, accessibility, and About screens.

## Phase 3 — UX, reliability, and platform polish

- [x] Responsive phone/tablet calculator composition.
- [x] Local-first/no-account core experience.
- [x] FileProvider-based export sharing with canonical-path containment.
- [x] Reduced-motion-aware navigation transitions.
- [x] Repository-owned primary-navigation icons with non-duplicated accessibility semantics.
- [x] Branded AndroidX launch splash treatment.
- [x] Backup/CSV/PDF file I/O moved off the main thread.
- [x] Visible backup busy state prevents duplicate backup operations.
- [x] Destructive restore and clear-all confirmation flows.
- [x] Calculator eager-composition budget capped at 100 editable expense items with visible feedback.
- [x] Named-history and template save dialogs expose the 120-character/Unicode-safe naming contract and unambiguous Save/Cancel actions.
- [ ] Add real release screenshots from verified builds using fictional data.
- [ ] Profile very large history/template collections if real-device measurements identify a need.
- [ ] Optional receipt notes/categories remain a post-1.0 enhancement, not a release blocker.

## Phase 4 — Verification depth

- [x] Domain unit tests.
- [x] Repository tests for saved names, persisted-record envelopes, template finance settings, duplicate IDs, and fail-before-replace semantics.
- [x] Shared persisted-record policy tests.
- [x] Backup codec validation, persisted-policy, and corruption tests.
- [x] Deterministic finance and backup fuzz/regression tests.
- [x] Room integration and backup replacement tests.
- [x] Compose smoke tests.
- [x] Named-history save dialog and Unicode-boundary regression coverage.
- [x] Template save dialog guidance/confirm/Unicode-boundary regression coverage.
- [x] History label-filter regression coverage.
- [x] Settings backup-busy UI regression coverage.
- [x] Real-activity calculate/named-save/history journey smoke test.
- [x] Instrumentation-test compilation in CI.
- [ ] Execute the Android instrumentation suite on a connected emulator/device for the final release candidate.
- [ ] Add database migration tests when schema version 2 exists.
- [ ] Add a macrobenchmark/profile module only if measured performance warrants it.

## Phase 5 — Release engineering

- [x] Debug/release build, full Android lint, unit-test, static-security, dependency-review, and repository-audit workflows are defined.
- [x] Tag-triggered unsigned release-artifact workflow is defined.
- [x] Production signing material is kept outside source control.
- [ ] Confirm all current pull-request checks are green on the exact final commit.
- [ ] Produce the signed production artifact with external signing credentials.
- [ ] Capture final screenshots.
- [ ] Finalize the published 1.0.0 release entry.
- [ ] Tag `v1.0.0` only after automated and manual release gates pass.

## Phase 6 — Final audit

- [x] Source-level architecture, persistence, privacy, backup, export, input-boundary, logging, accessibility-semantics, and performance-budget audits completed.
- [x] Dedicated persistence-invariant documentation is required by the repository audit.
- [x] Repository documentation reconciled with implemented behavior for the release-candidate branch.
- [x] Secret-pattern, Android resource/security, and repository-link checks are part of CI.
- [ ] Clean setup using `docs/setup.md` is confirmed by the final CI run.
- [ ] Final PR unit tests, instrumentation compilation, lint, debug build, release build, CodeQL, dependency review, and repository audit are green.
- [ ] Accessibility manual pass with TalkBack and large system font scale is completed on a device/emulator.
- [ ] Phone and tablet/wide layouts are manually reviewed on the final build.
- [ ] Text, CSV, PDF, backup export, and backup restore are manually exercised through Android system pickers/share sheets.
- [ ] `README.md`, `CHANGELOG.md`, `ROADMAP.md`, `docs/`, and `what_changed.md` match the merged release candidate.

Future work should move into a tagged release only after the exact commit being released passes both automated checks and the documented manual device gates.
