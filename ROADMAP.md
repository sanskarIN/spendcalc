# SpendCalc Roadmap

The roadmap prioritizes correctness, privacy, accessibility, maintainability, and verifiable documentation over feature count.

Current Android release target: **2.15.4** (`versionCode 21504`). Room database and explicit backup schema compatibility versions remain `1` unless their actual contracts change.

## Phase 0 — Repository foundation

- [x] Android/Kotlin/Compose build configuration.
- [x] Repository standards and policy files.
- [x] Architecture, privacy, security, and support direction.
- [x] Pull-request CI, CodeQL, dependency review, repository audit, namespace check, formatting check, Android resource/security checks, documentation coverage, and secret-pattern scan are configured.
- [x] Exhaustive tracked-file reference and documentation source-of-truth map are maintained as required repository artifacts.
- [ ] Current 2.15.4 release-candidate workflow results are green on the exact final PR head.

## Phase 1 — Core calculator

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
- [x] Real-activity instrumentation journey updated so repeated formatted amount semantics do not create a false uniqueness failure.
- [ ] Add real release screenshots from a verified 2.15.4 build using fictional data.
- [ ] Profile very large history/template collections if real-device measurements identify a need.
- [ ] Optional receipt notes/categories remain a post-2.15.4 enhancement, not a release blocker.

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
- [x] Fast guard rejects any tracked file omitted/stale/duplicated in exhaustive codebase documentation.
- [ ] Execute the Android instrumentation suite successfully on the exact final 2.15.4 candidate.
- [ ] Re-run connected tests on a representative physical device/local emulator.
- [ ] Add database migration tests when schema version 2 exists.
- [ ] Add a macrobenchmark/profile module only if measured performance warrants it.

## Phase 5 — 2.15.4 release engineering

- [x] Debug/release build, full Android lint, unit-test, static-security, documentation-coverage, dependency-review, and repository-audit workflows are defined.
- [x] Tag-triggered unsigned release-artifact workflow is defined.
- [x] Production signing material is kept outside source control.
- [x] Release procedure separates source completeness, exact-head automation, manual Android verification, and distribution/signing/screenshot evidence.
- [x] Android application metadata is set to `2.15.4` with monotonic `versionCode` `21504`.
- [x] Room database and explicit backup schema compatibility versions remain independent from the app release number.
- [x] Documentation index, root README, Android build guide, command reference, release guide, verification checklist, changelog, and roadmap are retargeted to 2.15.4.
- [ ] Confirm CI is green on the exact final 2.15.4 commit.
- [ ] Confirm CodeQL is green on the exact final 2.15.4 commit.
- [ ] Confirm Dependency Review is green on the exact final 2.15.4 commit.
- [ ] Confirm Repository Audit is green on the exact final 2.15.4 commit.
- [ ] Confirm Android Instrumentation is green on the exact final 2.15.4 commit.
- [ ] Complete representative manual Android checks.
- [ ] Complete TalkBack/large-font/reduced-motion accessibility review.
- [ ] Complete phone and tablet/wide layout review.
- [ ] Manually exercise text/CSV/PDF export/share.
- [ ] Manually exercise backup export/restore/system picker flows.
- [ ] Verify offline core behavior.
- [ ] Capture final screenshots from the verified build using fictional data.
- [ ] Produce the signed production artifact using protected external signing credentials.
- [ ] Verify signing certificate and install the exact signed artifact.
- [ ] Inspect application ID/version/SDK/permissions.
- [ ] Record artifact SHA-256 and exact source SHA.
- [ ] Finalize the published 2.15.4 release entry.
- [ ] Tag `v2.15.4` only after every blocking automated/manual gate passes.

## Phase 6 — Documentation and source integrity

- [x] Source-level architecture, persistence, privacy, backup, export, input-boundary, logging, accessibility-semantics, and performance-budget audits completed.
- [x] Dedicated persistence-invariant documentation is required by the repository audit.
- [x] Every tracked root/configuration/GitHub/build/source/test/resource/script/policy/documentation file is described individually in `docs/codebase-reference.md`.
- [x] `docs/documentation-map.md` defines documentation authority, update triggers, and anti-drift rules.
- [x] `scripts/check_documentation_coverage.py` mechanically compares exhaustive documentation to `git ls-files`.
- [x] Main CI and Repository Audit enforce tracked-file documentation coverage.
- [x] Repository required-file audit requires the codebase reference, documentation map, and coverage guard.
- [x] Contributor/development/setup/testing/release/verification documentation explains how to maintain complete file coverage.
- [x] Intentional absence of a committed Gradle wrapper is documented rather than mistaken for an omitted project file.
- [x] Future tracked Room schema files are identified as migration/release artifacts that must be individually documented.
- [x] Secret-pattern, documentation, Android resource/security, and repository-link checks are part of CI.
- [ ] Final `README.md`, `CHANGELOG.md`, `ROADMAP.md`, permanent `docs/`, and continuity records match the exact merged/tagged release evidence.

## Phase 7 — Dependency modernization after 2.15.4 stabilization

Major dependency updates are deliberately isolated from the current release candidate unless required to fix a blocker.

- [ ] Evaluate Android Gradle Plugin major update independently.
- [ ] Evaluate Kotlin major/minor update together with Compose/KSP compatibility.
- [ ] Evaluate Room update with compiler/runtime/instrumentation compatibility checks.
- [ ] Evaluate AndroidX Core/Test updates independently.
- [ ] Evaluate GitHub Actions major updates for runner/Node/licensing/cache behavior.
- [ ] Close/supersede Dependabot PRs that become obsolete after controlled upgrades.
- [ ] Re-run full CI and Android instrumentation for each accepted dependency batch.

## Phase 8 — Browser-extension preparation after Android release stability

Browser-extension work is not part of the Android 2.15.4 release gate. Keep it architecturally separate so Android stability is not weakened.

Planned preparation:

- [ ] Define extension product scope and supported browsers.
- [ ] Decide Manifest V3 baseline and browser compatibility strategy.
- [ ] Extract/reuse finance-domain rules in a platform-neutral form where practical without coupling Android storage/UI to web code.
- [ ] Define local-only extension storage/privacy model.
- [ ] Design extension popup/options/history UX.
- [ ] Define import/export compatibility boundaries with Android without weakening validation.
- [ ] Add dedicated extension build/test/lint/security workflow when implementation begins.
- [ ] Keep Android production signing and browser-extension publishing credentials separate.

Future work should enter a tagged release only after the exact commit being released passes both automated checks and the documented manual Android/accessibility/export/backup/signing/screenshot gates.
