# SpendCalc Roadmap

The roadmap prioritizes correctness, privacy, accessibility, maintainability, and verifiable documentation over feature count.

Current verified Android release candidate: **2.15.4** (`versionCode 21504`). Room database and explicit backup schema compatibility versions remain `1` unless their actual contracts change. The next maintenance line is being prepared on `develop/v2.15.5`; application metadata remains at 2.15.4 until the 2.15.4 release is merged/published and the next release cut is intentionally performed.

## Phase 0 — Repository foundation

- [x] Android/Kotlin/Compose build configuration.
- [x] Repository standards and policy files.
- [x] Architecture, privacy, security, and support direction.
- [x] Pull-request CI, CodeQL, dependency review, repository audit, namespace check, formatting check, Android resource/security checks, documentation coverage, and secret-pattern scan are configured.
- [x] Exhaustive tracked-file reference and documentation source-of-truth map are maintained as required repository artifacts.
- [x] 2.15.4 release-candidate workflow results are green on exact head `4b4f0ae520cbbaf4c7cee9adb4c5dd7a813bbe80`.

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
- [x] Export filename sanitization rejects blank/dot-only directory-like names and bounds filenames.
- [x] Export containment rejects the export directory itself as a shareable file.
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
- [x] Android instrumentation suite succeeded on exact 2.15.4 candidate head `4b4f0ae520cbbaf4c7cee9adb4c5dd7a813bbe80`.
- [x] Export path/filename regression tests cover root rejection, separator neutralization, dot-only fallback, and filename length bounds.
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
- [x] CI is green on exact 2.15.4 head `4b4f0ae520cbbaf4c7cee9adb4c5dd7a813bbe80`.
- [x] CodeQL is green on exact 2.15.4 head `4b4f0ae520cbbaf4c7cee9adb4c5dd7a813bbe80`.
- [x] Dependency Review is green on exact 2.15.4 head `4b4f0ae520cbbaf4c7cee9adb4c5dd7a813bbe80`.
- [x] Repository Audit is green on exact 2.15.4 head `4b4f0ae520cbbaf4c7cee9adb4c5dd7a813bbe80`.
- [x] Android Instrumentation is green on exact 2.15.4 head `4b4f0ae520cbbaf4c7cee9adb4c5dd7a813bbe80`.
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

Major dependency updates are deliberately isolated from the verified 2.15.4 release candidate unless required to fix a blocker.

- [ ] Evaluate Android Gradle Plugin major update independently; do not couple it to the first 2.15.5 maintenance batch.
- [ ] Evaluate Kotlin major/minor update together with Compose/KSP compatibility.
- [x] Evaluate Room 2.8.4 on the 2.15.5 maintenance branch with compiler/runtime/instrumentation verification pending on the exact final maintenance head.
- [x] Evaluate AndroidX Core compatibility: 1.19.0 was rejected because it requires compileSdk 37 / AGP 9.1+; 1.16.0 is the selected compatible update for compileSdk 35 / AGP 8.7.3.
- [x] Evaluate AndroidX Test JUnit 1.3.0 for the current minSdk baseline; exact-head instrumentation verification remains required.
- [x] Evaluate Dependency Review Action v5 on the maintenance branch; exact-head workflow verification remains required.
- [ ] Evaluate remaining GitHub Actions major updates independently for runner/Node/licensing/cache behavior.
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

## Phase 9 — 2.15.5 maintenance preparation

The first 2.15.5 batch is intentionally stacked on the exact green 2.15.4 candidate and is tracked in draft PR #13. It must not be merged ahead of PR #12.

- [x] Create `develop/v2.15.5` from exact green 2.15.4 head `4b4f0ae520cbbaf4c7cee9adb4c5dd7a813bbe80`.
- [x] Open draft stacked PR #13 for next-version validation.
- [x] Harden export filename sanitization for blank and dot-only names.
- [x] Reject the export directory root itself from the shareable-file containment predicate.
- [x] Add JVM regression tests for the new export path policy.
- [x] Update AndroidX Core from 1.15.0 to compatible 1.16.0; explicitly reject incompatible 1.19.0 for the current SDK/AGP baseline.
- [x] Update AndroidX Test JUnit from 1.2.1 to 1.3.0.
- [x] Update Room runtime/ktx/compiler from 2.6.1 to 2.8.4 without changing database schema version 1.
- [x] Update Dependency Review Action from v4 to v5.
- [ ] Confirm Repository Audit on the exact final 2.15.5 maintenance head.
- [ ] Confirm CI/unit/fuzz/lint/debug/release compilation on the exact final 2.15.5 maintenance head.
- [ ] Confirm CodeQL on the exact final 2.15.5 maintenance head.
- [ ] Confirm Dependency Review on the exact final 2.15.5 maintenance head.
- [ ] Confirm Android Instrumentation on the exact final 2.15.5 maintenance head.
- [ ] Evaluate the next dependency batch only after this one is completely green.
- [ ] After 2.15.4 is actually merged/released, retarget application metadata and release-facing documentation to `2.15.5` / `21505` as a separate intentional release-cut change.

Future work should enter a tagged release only after the exact commit being released passes both automated checks and the documented manual Android/accessibility/export/backup/signing/screenshot gates.
