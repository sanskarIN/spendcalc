# SpendCalc Roadmap

The roadmap prioritizes correctness, privacy, accessibility, and maintainability over feature count.

## Phase 0 — Repository foundation

- [x] Android/Kotlin/Compose build configuration.
- [x] Repository standards and policy files.
- [x] Architecture, privacy, security, and support direction.
- [x] Pull-request CI, CodeQL, dependency review, repository audit, namespace check, formatting check, and secret-pattern scan are configured.
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
- [x] Saved templates.
- [x] DataStore settings.
- [x] Optional history auto-delete.
- [x] History search/filter.
- [x] Undo for individual history deletion.
- [x] Undo for individual template deletion.
- [x] CSV/text/PDF export paths.
- [x] User-driven versioned backup/restore for history, templates, and preferences.
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
- [ ] Add real release screenshots from verified builds using fictional data.
- [ ] Profile very large history/template collections if real-device measurements identify a need.
- [ ] Optional receipt notes/categories remain a post-1.0 enhancement, not a release blocker.

## Phase 4 — Verification depth

- [x] Domain unit tests.
- [x] Repository tests.
- [x] Backup codec validation and corruption tests.
- [x] Deterministic finance and backup fuzz/regression tests.
- [x] Room integration and backup replacement tests.
- [x] Compose smoke tests.
- [x] Settings backup-busy UI regression coverage.
- [x] Real-activity calculate/save/history journey smoke test.
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

- [x] Source-level architecture, privacy, backup, export, input-boundary, logging, accessibility-semantics, and performance-budget audits completed.
- [x] Repository documentation reconciled with implemented behavior for the release-candidate branch.
- [x] Secret-pattern and repository-link checks are part of CI.
- [ ] Clean setup using `docs/setup.md` is confirmed by the final CI run.
- [ ] Final PR unit tests, instrumentation compilation, lint, debug build, release build, CodeQL, dependency review, and repository audit are green.
- [ ] Accessibility manual pass with TalkBack and large system font scale is completed on a device/emulator.
- [ ] Phone and tablet/wide layouts are manually reviewed on the final build.
- [ ] Text, CSV, PDF, backup export, and backup restore are manually exercised through Android system pickers/share sheets.
- [ ] `README.md`, `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` match the merged release candidate.

Future work should move into a tagged release only after the exact commit being released passes both automated checks and the documented manual device gates.
