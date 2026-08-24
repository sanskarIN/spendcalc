# SpendCalc Release Guide

This document defines how SpendCalc release candidates are verified, signed, tagged, and published.

Current target:

```text
versionName = 2.15.4
versionCode = 21504
applicationId = in.sanskar.spendcalc
Room schema = 1
explicit backup schema = 1
```

The application release number is intentionally independent from persistence compatibility versions. Do not introduce a fake Room or backup migration merely to mirror `2.15.4`.

---

## 1. Release principles

SpendCalc releases must be reproducible from source without committing private signing material.

A release is not proven by:

- a mergeable pull request;
- a configured workflow file;
- a queued/in-progress workflow;
- an older successful commit;
- source review alone;
- a debug build alone.

A release requires evidence from the exact final source commit.

Evidence classes are distinct:

1. repository/source guards;
2. JVM/build/static verification;
3. connected Android runtime verification;
4. manual Android/accessibility/privacy/export/backup/offline verification;
5. production signing/artifact verification;
6. release metadata/tag/publication verification.

---

## 2. Exact-source rule

Before release:

```bash
git rev-parse HEAD
git status --short
```

Record the exact SHA and require a clean intended source state.

Every new commit invalidates older workflow runs as final release proof. If documentation, version metadata, tests, or workflow files change, verify the new head again.

Do not build/sign a production artifact from a different commit than the one whose gates were approved.

---

## 3. Current version metadata

Authoritative application metadata lives in `app/build.gradle.kts`:

```kotlin
versionCode = 21504
versionName = "2.15.4"
```

Android rules:

- `versionCode` must be greater than previously published upgrade codes;
- `versionName` is user-facing;
- Room/backup schema versions change only for real compatibility changes;
- release documentation must remain aligned with the build file.

The repository audit checks current version alignment in the documentation index, Android build guide, and command reference.

---

## 4. Keep dependency upgrades isolated

Do not automatically mix unrelated major dependency upgrades into the 2.15.4 release candidate.

Evaluate Dependabot PRs independently for:

- breaking API changes;
- Android Gradle Plugin/Gradle compatibility;
- Kotlin/Compose/KSP compatibility;
- Room compiler/runtime behavior;
- AndroidX test behavior;
- GitHub Actions Node/runner requirements;
- licensing/terms changes;
- CI/cache behavior;
- runtime regressions.

A dependency upgrade should enter the release only when it is required or independently verified.

---

## 5. Repository guards

Run:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

All must pass.

These protect formatting/text hygiene, namespaces, tracked-file documentation coverage, resource references, local-first Android security assumptions, required metadata/local links/release-version alignment, and common secret patterns.

---

## 6. JVM/build verification

Run:

```bash
gradle --no-daemon clean testDebugUnitTest
gradle --no-daemon assembleDebugAndroidTest
gradle --no-daemon lint
gradle --no-daemon assembleDebug
gradle --no-daemon assembleRelease
gradle --no-daemon bundleRelease
```

Required outcomes:

- unit/regression/fuzz tests pass;
- instrumentation tests compile;
- Android lint passes;
- debug APK compiles;
- release APK compiles with shrinking/minification;
- release AAB compiles.

A successful compile does not replace connected runtime testing.

---

## 7. GitHub Actions exact-head gates

For the exact candidate SHA require successful conclusions from:

- `CI`;
- `CodeQL`;
- `Dependency Review`;
- `Repository Audit`;
- `Android Instrumentation`.

The Android instrumentation workflow must execute `connectedDebugAndroidTest` on the configured API 35 emulator.

If a run is cancelled because a newer commit superseded it, that is not a failure of the product, but it is also not release evidence. Verify the newest exact head.

---

## 8. Connected Android verification

Automated connected tests are required, then a representative local emulator/physical-device pass should be performed.

Verify at minimum:

- Room history/template/backup integration;
- Calculator Compose behavior;
- named-history save flow;
- template-name flow;
- History search/filter;
- Settings busy/progress state;
- Unicode boundary cases;
- real-activity calculate → save → History journey.

The activity journey may legitimately expose the same formatted amount in multiple semantics nodes; tests should verify that at least one correct amount representation exists rather than incorrectly requiring a unique semantics match.

---

## 9. Manual product verification

Use `docs/verification.md` as the authoritative blocking checklist.

Required manual areas include:

- fresh install/splash/onboarding;
- returning-install state;
- Calculator/History/Templates/Settings/About navigation;
- item limits and finance validation;
- saved history labels/search/delete/undo/retention;
- template save/load/delete/undo;
- text/CSV/PDF export/share;
- FileProvider containment;
- backup/restore/system picker flows;
- malformed backup rejection;
- offline operation;
- themes/font scale/reduced motion/TalkBack;
- phone/tablet layouts;
- privacy/security state.

Do not mark these complete from source inspection alone.

---

## 10. Build release artifacts

From the exact approved source SHA:

```bash
gradle --no-daemon assembleRelease
gradle --no-daemon bundleRelease
```

Expected directories:

```text
app/build/outputs/apk/release/
app/build/outputs/bundle/release/
```

Inspect actual filenames rather than assuming them.

Production signing material is not stored in the repository.

---

## 11. Production signing security

Never commit:

- `.jks`/`.keystore` files;
- signing passwords;
- private keys;
- CI tokens;
- local signing configuration containing secrets.

Use a controlled offline/secure signing environment or protected CI secret store.

If creating a new signing identity intentionally:

```bash
keytool -genkeypair -v -keystore spendcalc-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias spendcalc
```

Protect and back up the production identity. Losing it may block future upgrades depending on the distribution model.

---

## 12. Align and sign APK

Example manual flow:

```bash
zipalign -v -p 4 app/build/outputs/apk/release/app-release-unsigned.apk SpendCalc-release-aligned.apk
zipalign -c -v 4 SpendCalc-release-aligned.apk
apksigner sign --ks spendcalc-release.jks --ks-key-alias spendcalc --out SpendCalc-2.15.4-release.apk SpendCalc-release-aligned.apk
apksigner verify --verbose --print-certs SpendCalc-2.15.4-release.apk
```

Prefer interactive/secure password input rather than embedding passwords in shell commands.

---

## 13. AAB signing/publishing preparation

Build:

```bash
gradle --no-daemon bundleRelease
```

If the chosen distribution workflow requires JAR-style signing:

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore spendcalc-release.jks app/build/outputs/bundle/release/app-release.aab spendcalc
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

For Google Play, follow the current Play App Signing/upload-key process and keep keys/secrets outside Git.

---

## 14. Inspect the signed candidate

Verify package metadata before distribution.

For 2.15.4 expected values include:

```text
applicationId = in.sanskar.spendcalc
versionName = 2.15.4
versionCode = 21504
minSdk = 26
targetSdk = 35
```

Use Android Studio APK Analyzer or Build-Tools such as:

```bash
aapt dump badging SpendCalc-2.15.4-release.apk
aapt dump permissions SpendCalc-2.15.4-release.apk
```

Confirm no unintended permission, especially Internet access, was introduced.

---

## 15. Install/test the exact signed APK

```bash
adb install SpendCalc-2.15.4-release.apk
```

When upgrading an artifact signed with the same identity:

```bash
adb install -r SpendCalc-2.15.4-release.apk
```

A debug-signed installation may need to be removed before installing a production-signed artifact with the same application ID.

After installation:

- launch the app;
- verify About reports 2.15.4;
- run representative calculations;
- save/search history;
- save/load a template;
- test export/share;
- test backup/restore;
- test offline core behavior.

---

## 16. Upgrade testing

If a previous production-signed public release exists:

1. install that exact previous release;
2. create representative history/templates/preferences;
3. upgrade to the 2.15.4 artifact signed with the same valid production identity;
4. verify local data/preferences survive;
5. verify backup/export behavior;
6. verify About shows 2.15.4.

Do not claim upgrade compatibility if no real prior public artifact/signing path was tested.

---

## 17. Checksums and provenance

Generate SHA-256.

Linux:

```bash
sha256sum SpendCalc-2.15.4-release.apk
```

macOS:

```bash
shasum -a 256 SpendCalc-2.15.4-release.apk
```

Windows PowerShell:

```powershell
Get-FileHash .\SpendCalc-2.15.4-release.apk -Algorithm SHA256
```

Record:

- exact source commit SHA;
- artifact filename;
- artifact SHA-256;
- signing certificate identity/fingerprint as appropriate;
- verification date/environment;
- release tag once created.

---

## 18. Screenshots

Real release screenshots must come from the verified 2.15.4 build.

Use fictional data only. Review screenshots for:

- real financial/user records;
- notification/account identifiers;
- private email addresses beyond intentionally public support/business metadata;
- tokens/secrets;
- local file paths;
- signing material.

Follow `docs/assets/screenshots/README.md`.

---

## 19. Release notes/changelog

Before tagging:

- update `CHANGELOG.md` for 2.15.4;
- update `ROADMAP.md` to separate completed 2.15.4 work from future work;
- update current continuity/handoff state;
- ensure documentation describes actual verified behavior, not planned behavior;
- do not state manual/signing/screenshot gates passed until real evidence exists.

---

## 20. Tagging

Only after every blocking gate in `docs/verification.md` is complete:

```bash
git tag -a v2.15.4 -m "SpendCalc 2.15.4"
git push origin v2.15.4
```

Do not create the verified release tag early and plan to “finish checking later.”

If a defect is found after a candidate tag but before publication, correct the source, create a new appropriate version/tag according to the release policy, and do not silently move an immutable public release tag.

---

## 21. Publication

Publish only artifacts that can be tied to the exact verified/tagged source commit.

For each published artifact retain:

- versionName/versionCode;
- tag/SHA;
- checksum;
- signing identity/certificate verification;
- target distribution channel;
- release notes;
- evidence that required gates passed.

---

## 22. Rollback/withdrawal

If a release-blocking defect is discovered after publication:

1. stop/withdraw distribution where the channel permits;
2. preserve evidence/artifacts for diagnosis;
3. document the defect accurately;
4. fix on a new source commit;
5. choose a new valid Android versionCode/versionName;
6. rerun the full release verification sequence;
7. publish a corrected signed artifact.

Do not reduce Android versionCode for a corrective release.

---

## 23. Final 2.15.4 gate

Do not tag/publish `v2.15.4` until all of the following are complete for the exact final SHA:

- repository guards;
- JVM tests/regressions/fuzz coverage;
- instrumentation test compilation;
- Android lint;
- debug/release/AAB compilation;
- CI;
- CodeQL;
- Dependency Review;
- Repository Audit;
- Android Instrumentation connected suite;
- representative manual Android checks;
- accessibility/layout review;
- export/share verification;
- backup/restore verification;
- offline/privacy/security verification;
- real verified-build screenshots;
- production signing;
- signature/certificate verification;
- signed-artifact installation;
- package/version/permission inspection;
- checksum/source-SHA recording;
- changelog/roadmap/continuity alignment.

**Made by the Sanskar**
