# SpendCalc — Work Continuity

## Current release candidate

- Date: 2026-08-21
- Repository: `sanskarIN/spendcalc`
- Default branch: `main`
- Active branch: `complete/v1-finalization`
- Active pull request: `#12`
- Target application release: `2.0.12`
- Android `versionName`: `2.0.12`
- Android `versionCode`: `20012`
- Room database version: `1`
- Explicit backup schema version: `1`
- Application ID: `in.sanskar.spendcalc`
- Android support: API 26+, Kotlin + Jetpack Compose, local/offline-first
- License: MIT
- Product credit: `Made by the Sanskar`
- Current `main` incorporated through: `b307830415cf8fb1d0eca14a66f7d70b45d35f90`
- Main-reconciliation merge: `b8bc06578a48334e75fad0c5022a152d6bcc3c11`
- Exact pre-handoff branch head before this documentation commit: `cfe4f425514a0a8df002108b29c7111f07f37696`
- Comparison with `main` during this continuation: `status = ahead`, `behind_by = 0`
- Release status: automated verification is actively being driven from real GitHub Actions results. Concrete unit-test and instrumentation-compilation blockers discovered by CI were corrected. Exact-final-head automation must be re-fetched after this handoff commit, and real Android/accessibility/signing/screenshot/artifact evidence remains blocking before merge/tag/release.

## 2026-08-21 continuation completed

### Used real CI evidence instead of source-only assumptions

The continuation resumed from PR #12 rather than duplicating work on `main`. The branch already contained the stronger 2.0.12 release candidate, so all fixes were applied to `complete/v1-finalization`.

The first inspected release-candidate CI run, `32353151249`, executed repository guards successfully but failed at JVM tests:

- 98 tests executed;
- one test failed: `BackupCodecFuzzTest > deterministic unicode labels round trip through record encoding`;
- the exception was an `IllegalArgumentException` raised while encoding a generated backup fixture;
- instrumentation compilation, lint, debug build, and release compilation were correctly skipped after the failing test gate.

The failure was not a production backup-codec defect. The seeded fuzz generator could create an empty or whitespace-only saved label, while the shared saved-record policy correctly requires persisted history/template names to be nonblank. The fuzz fixture was therefore outside the domain invariant it was supposed to exercise.

Fix commits:

- `0b6ecfcfc1d714d6f072077a203965b199dc748e` — `test: keep backup fuzz labels within saved-name invariant`
- `4375405d25da6187b640d3d1eb5c1f59d2d330c4` — `test: keep checksum fuzz fixtures valid`

The test still exercises spaces, tabs, newlines, INR symbols, accented Latin text, CJK text, commas, quotes, equals signs, URL-safe Base64 record encoding, checksum behavior, and deterministic seeded repetition. It now always keeps the saved name itself valid by prefixing generated text with a nonblank character.

### Confirmed the JVM regression fix and exposed the next Android-test blocker

Fresh CI run `32434604031` on head `4375405d25da6187b640d3d1eb5c1f59d2d330c4` re-passed:

- formatting guard;
- Kotlin namespace guard;
- tracked-file documentation coverage;
- Android string-resource audit;
- local-first Android security policy;
- repository metadata/link/release-version audit;
- secret-pattern scan;
- the complete JVM unit/fuzz suite.

The run then reached `assembleDebugAndroidTest` and failed during Kotlin compilation. The concrete errors were unresolved Compose test imports across the newly expanded instrumentation suite:

- `androidx.compose.ui.test.assertExists`;
- `androidx.compose.ui.test.assertDoesNotExist`;
- `androidx.compose.ui.test.onNode`.

For the Compose test-rule API used by this project, `assertExists`, `assertDoesNotExist`, and `onNode` are available through the rule/interaction types and do not require those invalid top-level imports. The production UI was not changed; only test-source imports were corrected.

Fix commits:

- `812d5ed5d6fb69ff2fb9cde032c109ad226d8ee0` — `test: fix calculator Compose rule API imports`
- `8d6db345ac66a33bf74d0cb4f2ca602ac427fbf2` — `test: fix history Compose rule API imports`
- `c401cbe7b52d0958fcccddd3eb135ae94b9b34fd` — `test: fix activity journey Compose rule API imports`
- `1c91c5c48dc7962ec3d0a8078bb9a799bf109eba` — `test: fix settings Compose assertion import`

Because every commit changes the exact release-candidate head, any workflow runs started for intermediate heads are superseded release evidence even if they later finish successfully.

### Removed a deprecated GitHub Actions runtime warning

The same CI logs reported that `actions/upload-artifact@v4` targets deprecated Node 20 and was being forced by GitHub onto Node 24. The current repository has an automated dependency update path for `actions/upload-artifact@v7`, and the hosted runner used by the project supports the current action runtime.

Both artifact-upload call sites were modernized:

- `.github/workflows/ci.yml` failure-report upload now uses `actions/upload-artifact@v7`;
- `.github/workflows/release.yml` unsigned release-artifact upload now uses `actions/upload-artifact@v7`.

Commits:

- `6a09135fc3973116ff6af0505eb147cdfbfe2725` — `ci: upgrade artifact upload action to v7`
- `cfe4f425514a0a8df002108b29c7111f07f37696` — `ci: modernize release artifact upload action`

This removes the known Node-20 artifact-upload warning from the final workflow path instead of accepting warning-only technical debt.

### Exact-head verification rule for the next continuation

This handoff commit advances the branch beyond `cfe4f425514a0a8df002108b29c7111f07f37696`, so all older runs are stale for final release evidence.

For the exact commit containing this file:

1. fetch CI, CodeQL, Dependency Review, and Repository Audit;
2. require successful conclusions for all four families;
3. in CI, require the full sequence to pass: guards -> JVM tests -> `assembleDebugAndroidTest` -> full Android lint -> debug APK compilation -> release APK compilation;
4. if instrumentation compilation fails again, inspect the exact compiler error rather than guessing;
5. do not merge/tag/release from an intermediate successful head if a newer documentation or workflow commit exists.

## 2026-08-20 continuation completed

### Reconciled current `main` without reverting 2.0.12

`main` had advanced with Android build/command documentation after the release branch diverged, which made PR #12 non-mergeable. The branches were reconciled deliberately instead of taking one side wholesale.

Merge commit `b8bc06578a48334e75fad0c5022a152d6bcc3c11` has the prior release head and current `main` as parents. It preserves the stronger 2.0.12 implementation/release state while importing the useful documentation additions. After reconciliation, comparison with `main` reports the release branch is not behind and GitHub reports PR #12 mergeable.

Imported/integrated documents:

- `docs/README.md` — task-oriented documentation index;
- `docs/android-build-guide.md` — complete APK/AAB/build/install/signing/artifact workflow;
- `docs/command-reference.md` — detailed Git/Java/Gradle/ADB/Android tooling/repository command reference.

The exhaustive `docs/codebase-reference.md` was updated in the reconciliation itself so those tracked files remained covered by `scripts/check_documentation_coverage.py`.

### Fixed stale release data in imported build documentation

The imported Android executable guide and command reference still contained old `1.0.0` / `versionCode 1` / `SpendCalc-1.0.0-release.apk` examples. Those examples conflicted with the actual 2.0.12 branch and were corrected.

Current documentation now consistently identifies:

```text
versionName = 2.0.12
versionCode = 20012
Room database version = 1
Explicit backup schema version = 1
```

Application release versioning remains intentionally independent from persistence compatibility versions. No fake Room or backup migration was introduced merely to mirror 2.0.12.

### Added a release-document drift guard

`scripts/check_repository.py` now treats release-document consistency as an automated repository invariant.

It:

- requires `docs/README.md`, `docs/android-build-guide.md`, and `docs/command-reference.md` in addition to the established release/documentation set;
- parses the current application `versionName` and `versionCode` directly from `app/build.gradle.kts` rather than maintaining another version constant;
- requires the documentation index, Android build guide, and command reference to expose the current application release metadata;
- rejects semantic-versioned signed-APK examples that do not match the current application version;
- preserves the prior required-file, README identity/contact, and local Markdown-link checks.

This closes the exact failure mode discovered during reconciliation: a release retarget can no longer leave stale copy/paste signing filenames in the deep build documentation without failing the repository audit.

The guard is documented consistently in:

- `README.md`;
- `docs/README.md`;
- `docs/codebase-reference.md`;
- `docs/command-reference.md`;
- `docs/development.md`;
- `docs/documentation-map.md`;
- `docs/github-maintenance.md`;
- `docs/testing.md`;
- `docs/verification.md`;
- `CHANGELOG.md`.

### Completed Android build and command documentation integration

`docs/android-build-guide.md` now covers the current 2.0.12 candidate end to end:

- environment verification;
- Gradle task meanings;
- debug/release APK output;
- AAB output;
- ADB installation and package inspection;
- external production signing flow;
- `zipalign`, `apksigner`, `jarsigner`, and `keytool` roles;
- `SpendCalc-2.0.12-release.apk` examples;
- artifact/version inspection;
- future-version guidance without conflating Room/backup schema versions;
- release verification and troubleshooting links.

`docs/command-reference.md` now documents every repository Python guard, including cross-platform invocation guidance, and uses branch-neutral Git push guidance rather than assuming changes should be pushed directly to `main`.

`docs/README.md`, the root `README.md`, and `docs/documentation-map.md` now expose clear navigation and authority boundaries so setup/build/commands/release/troubleshooting documents can share necessary commands without becoming competing sources of truth.

## Important continuation commits

### 2026-08-21

- `0b6ecfcfc1d714d6f072077a203965b199dc748e` — `test: keep backup fuzz labels within saved-name invariant`
- `4375405d25da6187b640d3d1eb5c1f59d2d330c4` — `test: keep checksum fuzz fixtures valid`
- `812d5ed5d6fb69ff2fb9cde032c109ad226d8ee0` — `test: fix calculator Compose rule API imports`
- `8d6db345ac66a33bf74d0cb4f2ca602ac427fbf2` — `test: fix history Compose rule API imports`
- `c401cbe7b52d0958fcccddd3eb135ae94b9b34fd` — `test: fix activity journey Compose rule API imports`
- `1c91c5c48dc7962ec3d0a8078bb9a799bf109eba` — `test: fix settings Compose assertion import`
- `6a09135fc3973116ff6af0505eb147cdfbfe2725` — `ci: upgrade artifact upload action to v7`
- `cfe4f425514a0a8df002108b29c7111f07f37696` — `ci: modernize release artifact upload action`
- this handoff commit becomes the newest exact release-candidate head and therefore invalidates all older exact-head workflow evidence.

### Earlier continuation

- `b8bc06578a48334e75fad0c5022a152d6bcc3c11` — `docs: reconcile Android build documentation from main`
- `cc59e1a30c309430ec2580e4ffcad3467a4b5fc3` — `docs: integrate build guides into documentation authority map`
- `7e12c423a5925bafd8540f458888f0e03f0e8f69` — `docs: record reconciled 2.0.12 release head`
- `d50a0246d72498915e40d35294eddda6367b458b` — `docs: align Android build guide with 2.0.12`
- `f2999aa68af3b2c487bac9652c7299a2ba622f74` — `docs: align command reference with release and repository guards`
- `74cdc577b8ecd8c67423662ffa8588201863832b` — `docs: complete documentation index for 2.0.12`
- `4e8cddb42f02d5e002672408081060f390dfe094` — `ci: guard release metadata in build documentation`
- `c01d9028635d2f0947ecad324f7dc69aa903f23e` — `docs: expose complete build documentation from README`
- `84562a0d54c79b51c0d2f4d6bba722f9500ebc09` — `docs: document release metadata repository guard`
- `0ca6fad0d26ea4b67138bcb20ffeb43f9a9f5c8a` — `docs: specify release documentation guard coverage`
- `a0474ad2f9b15d2c9a2e03e710e87f5161406ea6` — `docs: document release metadata maintenance guard`
- `121c2b04a9bd54cc914170887e9022a670c41a8c` — `docs: record build documentation and drift guards`
- `dcb54a1081e8ac91c1066e04d74be94bd03b87b5` — `docs: require build documentation metadata validation`
- `7b95a3a48629613c2a89aac0f66f501d9c9e7c2f` — `docs: verify build documentation release metadata alignment`

## Exact workflow truth from the latest inspected completed runs

For head `4375405d25da6187b640d3d1eb5c1f59d2d330c4`:

- Repository Audit — run `32434604048` — success;
- Dependency Review — run `32434604049` — success;
- CI — run `32434604031` — failed only after JVM tests passed, at instrumentation-test compilation because of the invalid Compose imports documented above;
- CodeQL was still pending/in progress when that head was superseded.

Those results proved the JVM regression fix and exposed the next compiler defect, but they are **not final release evidence** because subsequent commits changed the branch.

For the exact commit containing this file, re-fetch all four workflow families and require successful conclusions before merge/tag/release. Missing, configured, queued, in-progress, skipped, cancelled, superseded, or older-head runs are never treated as passed.

## Product/source state retained

The release branch continues to include the prior completed implementation and hardening work:

- precision-safe `BigDecimal` finance engine with deterministic order and bounded input shapes;
- maximum 100 editable expense items, discount <= 100%, split count 1–1,000,000;
- Room history with optional labels/search/delete/Undo/clear/30-90-day retention;
- Room reusable templates with naming/load/delete/Undo;
- shared 120-character UTF-16-safe saved-name policy;
- repository-level persisted-record validation and duplicate-ID rejection;
- DataStore theme/large-text/reduced-motion/retention/onboarding preferences;
- versioned local backup/restore with bounded parsing, strict UTF-8, exact canonical persisted-currency validation, SHA-256 corruption detection, transactional Room replacement, and compensating preference rollback;
- text/CSV/PDF export, CSV formula neutralization, Unicode-safe PDF truncation, canonical export-path containment, and non-exported restricted `FileProvider`;
- no core Android Internet permission;
- Compose Material 3 responsive phone/tablet UI, onboarding, settings, accessibility behavior, About/support/funding/version information, and branded splash;
- JVM finance/repository/backup/export/platform regressions and deterministic fuzz coverage;
- Android Room/Compose/activity integration tests compiled in CI once the exact-head instrumentation gate is green;
- formatting, namespace, documentation coverage, Android resource, Android security, repository metadata/link/version consistency, and secret-pattern guards.

Permanent behavioral detail belongs in the specialized documentation and tests rather than being duplicated further into this handoff.

## Documentation authority

- `README.md` — public product/release/build entry point.
- `docs/README.md` — task-oriented documentation index.
- `docs/android-build-guide.md` — complete Android executable/APK/AAB/install/signing workflow.
- `docs/command-reference.md` — detailed command meanings and repository guard invocations.
- `docs/features.md` — implemented user behavior.
- `docs/architecture.md` — architecture boundaries.
- `docs/codebase-reference.md` — exhaustive tracked-file ownership.
- `docs/documentation-map.md` — documentation authority and update matrix.
- `docs/development.md` — contributor change rules.
- `docs/testing.md` — verification strategy and guard/test ownership.
- `docs/persistence-invariants.md` — persisted record/backup compatibility contract.
- `docs/backup-restore.md` — explicit backup behavior.
- `docs/security-backup.md` — backup threat/parser model.
- `docs/privacy-backup.md` and `PRIVACY.md` — backup/runtime privacy behavior.
- `docs/accessibility.md` — accessibility behavior/manual checks.
- `docs/performance.md` — bounded-work/performance policy.
- `docs/logging.md` — logging/redaction contract.
- `docs/github-maintenance.md` — repository maintenance and release-retarget process.
- `docs/release.md` — release procedure.
- `docs/verification.md` — authoritative blocking automated/manual/distribution checklist.
- `docs/release-candidate-final-audit.md` — source-level 2.0.12 audit only.
- `CHANGELOG.md` — notable release-candidate changes.
- `ROADMAP.md` — planning and still-open gates.
- this file — volatile active branch/PR/exact-head continuation truth.

## Remaining blocking gates

### Exact-final-head automation

All must be successful for the exact final commit:

- CI;
- CodeQL;
- Dependency Review;
- Repository Audit.

CI specifically must complete, not merely start, all of these stages:

- repository/script guards;
- JVM unit and deterministic fuzz tests;
- instrumentation-test compilation;
- full Android lint;
- debug APK compilation;
- release APK compilation.

### Android/runtime/accessibility

Still require a real connected Android runtime and human/device review as specified in `docs/verification.md`, including:

- `connectedDebugAndroidTest`;
- Room/Compose/activity test execution;
- phone/tablet layouts;
- light/dark/system themes;
- app and Android large-text behavior;
- reduced motion;
- TalkBack traversal/labels/dialog/list/progress behavior;
- history/template workflows and Unicode name boundaries;
- 100-item limit;
- text/CSV/PDF share flows and long-Unicode PDF behavior;
- backup create/restore/progress/confirmation/data-integrity cases;
- malformed UTF-8 and checksum-valid noncanonical-currency restore rejection;
- offline/airplane-mode core workflow;
- branded launch splash.

### Distribution

Still require:

- genuine screenshots from the exact verified build using fictional data;
- production signing material kept outside source control;
- signed artifact produced from exact verified source;
- artifact inspection confirming application ID, `versionName 2.0.12`, `versionCode 20012`, SDK/permission expectations;
- About screen reporting 2.0.12;
- artifact checksum/source-SHA relationship recorded and verified;
- tag `v2.0.12` only after every blocking automated/manual/distribution gate is complete.

## Continuation instructions

1. Treat the exact commit containing this file as the newest 2.0.12 release-candidate head.
2. Re-fetch PR #12; require it to remain open/non-draft and confirm mergeability before the intended merge step.
3. Compare `main` to `complete/v1-finalization`; require `behind_by = 0`. If `main` advances, reconcile deliberately instead of overwriting the release branch.
4. Fetch CI, CodeQL, Dependency Review, and Repository Audit for this exact SHA.
5. In CI, require the unit-test, instrumentation-compile, lint, debug-build, and release-build steps all to succeed.
6. If any exact-head workflow fails, inspect the failed job/log and fix only the concrete defect with appropriate regression/documentation coverage.
7. Any further source/documentation commit invalidates older workflow release evidence and should be reflected here if work continues across sessions.
8. Once exact-head automation is green, execute every remaining manual gate in `docs/verification.md` on real Android hardware/emulator as appropriate.
9. Do not merge PR #12, claim production readiness, capture release screenshots, sign/tag, or publish merely because the PR is mergeable.
10. Capture screenshots only from the exact verified build using fictional data.
11. Keep signing credentials outside Git; verify the final artifact version, signature, checksum, and source relationship before `v2.0.12`.

While PR #12 remains open, continue from `complete/v1-finalization`. After a verified merge, continue from `main`.
