# SpendCalc — Work Continuity

## 2026-08-24 — 2.15.4 release preparation

### Current repository state

- Repository: `sanskarIN/spendcalc`
- Default branch: `main`
- Active release branch: `complete/v1-finalization`
- Active pull request: `#12`
- Pull-request title: `release: prepare SpendCalc 2.15.4 release candidate`
- Application ID: `in.sanskar.spendcalc`
- Target application release: `2.15.4`
- Android `versionName`: `2.15.4`
- Android `versionCode`: `21504`
- Android minimum API: `26`
- Android target/compile API: `35`
- Java/JVM target: `17`
- Room database version: `1`
- Explicit backup schema version: `1`
- License: MIT
- Core runtime model: Android-first, Kotlin + Jetpack Compose, local/offline-first
- Product credit: `Made by the Sanskar`

Application release versioning remains intentionally independent from Room/backup compatibility versions. The move to 2.15.4 does **not** create fake database or backup migrations.

---

## Release target change

The active release candidate has been retargeted from the superseded `2.0.12` candidate to **2.15.4**.

`app/build.gradle.kts` now contains:

```kotlin
versionCode = 21504
versionName = "2.15.4"
```

The `21504` versionCode follows the repository's existing semantic-component encoding convention and is greater than the earlier 20012 candidate code.

The following current release-facing documents have been retargeted to 2.15.4:

- `README.md`
- `docs/README.md`
- `docs/android-build-guide.md`
- `docs/command-reference.md`
- `docs/release.md`
- `docs/verification.md`
- `CHANGELOG.md`
- `ROADMAP.md`
- `what_changed.md`
- `what_changed_latest.md`
- `what_changed_final.md`
- PR `#12` title/body

Current signed-APK documentation examples use:

```text
SpendCalc-2.15.4-release.apk
```

The repository audit still derives application `versionName` and `versionCode` directly from `app/build.gradle.kts`, requires the documentation index/build guide/command reference to match those values, and rejects stale semantic-versioned signed-APK examples.

---

## Concrete Android instrumentation blocker fixed

The most recent executed Android instrumentation evidence before this continuation had one remaining failure in:

```text
MainActivityJourneyTest.calculateSaveAndFindHistoryJourney
```

The failure was:

```text
Expected exactly 1 node but found 3 nodes matching INR 25.00
```

The application had successfully compiled, the API 35 emulator had booted, and 11 of 12 instrumentation tests passed. The failure was caused by the test assuming that the correctly formatted amount could only appear once in the Compose semantics tree.

That uniqueness assumption was invalid because the same amount may be represented in multiple legitimate UI semantics nodes.

The journey test now:

1. waits until at least one node contains the expected formatted amount;
2. asserts the first matching collection interaction exists;
3. keeps the exact saved-history-name assertion;
4. does not weaken production UI behavior or add fake accessibility descriptions for testing.

This preserves the intent of the end-to-end journey while removing a false semantics-uniqueness requirement.

Fix commit:

- `7c859340f8c4739507ed8047e689249e908a2299` — `test: allow repeated amount semantics in activity journey`

Because later release-preparation commits advanced the branch, workflow runs on the isolated test-fix commit were intentionally superseded/cancelled by concurrency rules and are not final release evidence.

---

## Focused commits created in this continuation

- `7c859340f8c4739507ed8047e689249e908a2299` — `test: allow repeated amount semantics in activity journey`
- `941bfa67a69bf35091a19cceac749490fbc440ee` — `release: bump application version to 2.15.4`
- `458243050c4ade8f3c051b4def6c5b5cb4fc0afc` — `docs: retarget documentation index to 2.15.4`
- `c3e0f864bfb9652dbd892389d0dd9f6acb6f096b` — `docs: retarget Android build guide to 2.15.4`
- `3c40baf53acc36dfde4cdb2a9807236adb3844b2` — `docs: retarget command reference to 2.15.4`
- `757d937ca0a2d210b80d711e48cb74bb5f26b6a7` — `docs: retarget release verification to 2.15.4`
- `276474ebc148bed3f37f9a1d98817d518adb9173` — `docs: prepare 2.15.4 release workflow`
- `e7977feb450fa45b693bd681eae46995e66fefba` — `docs: retarget project README to 2.15.4`
- `139f5c56b70a83adac9d98bfc3493f59c1a08fc3` — `docs: prepare changelog for 2.15.4`
- `ac3095ee71cbbd30a1ea2cb3983d1ac3294817c1` — `docs: retarget roadmap to 2.15.4`
- `676a722fd91c91dc9d82084d26d84f0d59af42bf` — `docs: retarget latest continuity pointer to 2.15.4`
- `afb5f69e76cd22d33e753eb17347405cb2936dcc` — `docs: retarget final handoff pointer to 2.15.4`

The commit containing this canonical handoff becomes a newer exact head than every SHA above and therefore requires its own final workflow evidence.

---

## PR #12 retargeted

PR `#12` remains the active release candidate on `complete/v1-finalization` targeting `main`.

The PR metadata now describes 2.15.4, including:

- `versionName 2.15.4`;
- `versionCode 21504`;
- Room/backup schema independence;
- the activity-journey semantics fix;
- current release documentation;
- exact-head automated/manual gates;
- isolation of major dependency upgrades;
- browser-extension work as post-Android-release work.

Do not merge/tag/publish the pull request as a verified release until every blocking gate below is actually complete.

---

## Current implemented product baseline

The release branch contains the Android application and hardening work accumulated through the earlier stabilization phases.

### Finance/domain

- precision-safe `BigDecimal` arithmetic;
- itemized expense lines;
- discount, tax, tip, service charge;
- split bill;
- manual currency conversion;
- centralized rounding/validation;
- bounded decimal precision/scale/input lengths;
- bounded split count;
- bounded 100-item calculator editor;
- deterministic calculation order.

### Persistence

- Room history;
- saved history labels;
- History search/filter;
- delete/Undo/clear confirmation;
- retention options;
- Room templates;
- template save/load/delete/Undo;
- Preferences DataStore settings;
- shared persisted-record policy;
- ID/timestamp/currency/name/result/split validation;
- duplicate replacement-ID rejection;
- repository-boundary validation before DAO replacement.

### Backup/restore

- explicit user-driven local backup;
- Android Storage Access Framework document flows;
- versioned bounded backup format;
- SHA-256 accidental-corruption detection;
- strict malformed/unmappable UTF-8 rejection;
- canonical persisted-currency validation;
- Unicode-safe saved names;
- duplicate-ID and structural validation;
- confirmation before replacement;
- visible busy/progress state;
- multi-store restore compensation behavior.

### Export/share

- text receipts;
- CSV export;
- spreadsheet-formula neutralization for text cells;
- offline PDF receipts;
- Unicode-safe PDF truncation;
- non-exported FileProvider;
- canonical-path export containment.

### UI/accessibility

- Kotlin + Jetpack Compose + Material 3;
- responsive phone/tablet layout;
- light/dark/system themes;
- large-text preference;
- reduced-motion preference;
- branded splash screen;
- repository-owned navigation icons;
- user-facing string resources;
- first-run onboarding;
- About/support/funding/version UI;
- stable non-user-facing Compose test tags for exact editable-field targeting.

### Privacy/security

- local-first core;
- no account required;
- no remote API key required;
- no Android Internet permission in current manifest;
- no production signing material in Git;
- conservative secret-pattern guard;
- safe logging/redaction coverage;
- privacy/security/support/contribution policies.

---

## Testing/automation baseline

The repository includes:

- finance unit tests;
- deterministic finance fuzz/regression tests;
- history/template repository tests;
- persistence invariant tests;
- saved-name Unicode-boundary tests;
- backup codec/validation/corruption/fuzz tests;
- strict backup-byte UTF-8 tests;
- CSV security tests;
- PDF Unicode truncation tests;
- path containment tests;
- SafeLogger redaction tests;
- Room integration tests;
- Compose calculator/history/settings/dialog tests;
- real-activity calculate → named save → History journey;
- instrumentation-test compilation in CI.

GitHub workflow families:

- CI;
- CodeQL;
- Dependency Review;
- Repository Audit;
- Android Instrumentation;
- tag-triggered release-candidate build.

Workflow concurrency intentionally cancels superseded PR runs so CI resources focus on the newest head.

---

## Documentation/repository integrity baseline

Current documentation includes:

- task-oriented `docs/README.md`;
- complete 2.15.4 Android build/sign/install guide;
- 2.15.4 command reference;
- architecture/development/testing/accessibility/performance docs;
- backup/security/privacy/persistence docs;
- documentation source-of-truth map;
- exhaustive tracked-file codebase reference;
- release guide;
- blocking 2.15.4 verification checklist;
- troubleshooting;
- screenshot-capture policy;
- ADRs.

Repository guards cover:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

The repository intentionally does not commit a Gradle wrapper JAR. Documentation uses compatible local Gradle 8.9 and CI pins Gradle 8.9 through `gradle/actions/setup-gradle`.

---

## Dependency-upgrade policy for 2.15.4

Open Dependabot major/minor updates are not blindly folded into the current release candidate.

They require isolated compatibility review for:

- Android Gradle Plugin/Gradle compatibility;
- Kotlin/Compose/KSP compatibility;
- Room compiler/runtime behavior;
- AndroidX test/runtime behavior;
- GitHub Actions Node/runner requirements;
- action/cache behavior;
- licensing/terms changes.

The current 2.15.4 release prep keeps those dependency changes separate unless one becomes necessary to resolve a specific release blocker.

---

## Browser-extension planning boundary

Browser-extension work is planned **after Android 2.15.4 stabilization** and is not a blocking gate for this release.

Current roadmap preparation includes:

- supported browser scope;
- Manifest V3 baseline;
- platform-neutral finance-rule reuse where practical;
- local-only extension storage/privacy model;
- popup/options/history UX;
- import/export compatibility boundaries;
- dedicated extension build/test/security automation;
- separate extension publishing credentials from Android signing credentials.

Do not couple Android production stability to unfinished browser-extension implementation.

---

## Exact-head automated verification still required

For the exact commit containing this handoff, fetch workflow results again and require:

1. CI — success;
2. CodeQL — success;
3. Dependency Review — success;
4. Repository Audit — success;
5. Android Instrumentation — success.

CI must reach and pass:

- repository guards;
- JVM tests;
- instrumentation-test compilation;
- full Android lint;
- debug compilation;
- release compilation.

Android Instrumentation must execute `connectedDebugAndroidTest` successfully, not merely compile test sources.

If any gate fails, inspect the exact workflow/job logs and fix the concrete defect. Do not mark a failed/cancelled/pending run as successful evidence.

---

## Manual release blockers still required

Even after all automated workflows are green, the following remain blocking until real evidence exists:

### Representative Android runtime

- fresh install;
- splash/onboarding;
- returning-user state;
- Calculator/History/Templates/Settings/About navigation;
- named history/save/search/delete/Undo/retention;
- templates save/load/delete/Undo;
- item/input limits;
- Unicode boundary behavior.

### Export/share

- text share;
- CSV share;
- PDF share;
- FileProvider containment;
- Unicode-heavy PDF behavior.

### Backup/restore

- document creator/picker;
- confirmation before replace;
- visible progress state;
- history/template/preferences round-trip;
- malformed UTF-8 rejection;
- checksum-invalid rejection;
- noncanonical persisted-currency rejection;
- invalid/duplicate replacement protection.

### Offline/privacy/security

- core calculation/history/templates/settings/export with network disabled;
- no unintended Internet permission;
- no secrets/private data/signing material in repository/artifacts/screenshots.

### Accessibility/layout

- light/dark/system themes;
- app/system large text;
- reduced motion;
- TalkBack order/labels/dialogs;
- color-independent validation meaning;
- small phone layout;
- tablet/wide layout;
- touch targets/destructive wording.

### Screenshots

- real screenshots from the verified 2.15.4 build;
- fictional data only;
- privacy review before publication.

### Production signing/artifact

- build production candidate from exact verified SHA;
- keep signing material outside Git;
- sign using controlled identity;
- verify certificate/signature;
- inspect application ID/versionCode/versionName/SDK/permissions;
- install exact signed artifact;
- verify About reports 2.15.4;
- record artifact SHA-256 and source SHA relationship.

Only after all blocking automated/manual gates pass should `v2.15.4` be created and published.

---

## Recommended continuation order

1. Fetch exact-head CI/CodeQL/Dependency Review/Repository Audit/Android Instrumentation.
2. If any workflow fails, inspect exact job logs and fix the concrete error.
3. Repeat until the exact current head is green across all automated families.
4. Perform representative Android/manual/accessibility/export/backup/offline checks.
5. Capture real screenshots with fictional data.
6. Build/sign/verify/install the exact production artifact outside Git.
7. Record checksum/source SHA/certificate evidence.
8. Reconcile README/changelog/roadmap/release/verification/continuity docs with actual completed evidence.
9. Merge PR #12 only when release policy permits.
10. Tag/publish `v2.15.4` only after every blocking gate passes.
11. Then evaluate isolated dependency upgrades.
12. Then begin browser-extension implementation as a separate next-version stream.

---

## Safety/secret rules

Never commit or paste into repository history, issues, PR comments, docs, screenshots, or logs:

- production keystore/private key;
- signing passwords;
- API/access tokens;
- private user data;
- real financial records;
- machine-local secret configuration.

Production signing and store credentials intentionally remain outside source control.

---

## Historical continuity

Earlier development/release-candidate details, including the 2.0.12 stabilization work, remain available in repository Git history and earlier revisions of this file. They are historical engineering evidence, not the current release target.

Current continuation authority is:

1. current GitHub PR/workflow state;
2. this canonical `what_changed.md`;
3. `docs/verification.md` for release blockers;
4. `app/build.gradle.kts` for application release metadata;
5. authoritative permanent documentation identified by `docs/documentation-map.md`.

**Made by the Sanskar**
