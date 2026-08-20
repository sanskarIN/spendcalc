# SpendCalc Release Guide

This guide defines how SpendCalc release candidates and production Android artifacts should be prepared, checked, signed, verified, and published.

For command-by-command APK/AAB instructions, see [`android-build-guide.md`](android-build-guide.md). For individual command meanings and flags, see [`command-reference.md`](command-reference.md).

## 1. Release principles

SpendCalc releases should be reproducible from reviewed public source without committing private signing material.

Core rules:

- release source must be identifiable by commit/tag;
- `versionCode` must increase for distributed Android upgrades;
- production keystores/passwords must never be committed;
- tests/lint/build verification must happen before distribution;
- release APK/AAB signatures must be verified;
- the final signed APK should be installed/tested on Android hardware or a representative emulator;
- user-visible/privacy/security documentation must match actual behavior;
- existing release tags/artifacts should not be silently rewritten.

## 2. Current Android release configuration

Defined in `app/build.gradle.kts`:

```kotlin
applicationId = "in.sanskar.spendcalc"
minSdk = 26
targetSdk = 35
versionCode = 1
versionName = "1.0.0"
```

Release builds currently enable:

```kotlin
isMinifyEnabled = true
isShrinkResources = true
```

This enables R8 code optimization/shrinking and Android resource shrinking for the release variant.

## 3. APK versus AAB

### APK

An `.apk` is directly installable on compatible Android devices.

Build debug APK:

```bash
gradle assembleDebug
```

Build release APK:

```bash
gradle assembleRelease
```

### AAB

An `.aab` is an Android App Bundle used primarily for app-store publishing. It is not normally installed directly with `adb install`.

Build:

```bash
gradle bundleRelease
```

## 4. Pre-release documentation checks

Before building the release candidate, confirm these are current:

- `README.md`;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `what_changed.md`;
- `PRIVACY.md`;
- `SECURITY.md`;
- `docs/setup.md`;
- `docs/android-build-guide.md`;
- `docs/release.md`;
- user-facing version/about information.

## 5. Versioning

Use semantic versioning for public versions:

- **MAJOR** — incompatible behavior/data-contract changes;
- **MINOR** — backwards-compatible features;
- **PATCH** — backwards-compatible fixes.

Android `versionCode` is separate from semantic `versionName` and must monotonically increase for store upgrades.

Example update:

```kotlin
versionCode = 2
versionName = "1.0.1"
```

Inspect before committing:

```bash
git diff -- app/build.gradle.kts
```

## 6. Record the exact release source

Check repository state:

```bash
git status
```

Record commit:

```bash
git rev-parse --short HEAD
```

A production artifact should be traceable to this exact reviewed revision.

## 7. Clean local verification

Delete old generated build output:

```bash
gradle clean
```

Run JVM unit tests:

```bash
gradle testDebugUnitTest
```

Run Android lint:

```bash
gradle lintDebug
```

Build debug APK:

```bash
gradle assembleDebug
```

Build release APK:

```bash
gradle assembleRelease
```

Build release AAB:

```bash
gradle bundleRelease
```

Equivalent grouped command:

```bash
gradle clean testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease
```

## 8. Connected Android verification

With a physical device/emulator:

```bash
adb devices
gradle connectedDebugAndroidTest
```

The device must be API 26 or newer to match the supported minimum.

Do not consider unit tests alone a complete Android release verification because Room/framework/UI behavior also requires Android runtime coverage.

## 9. Repository utility checks

Run formatting/source guard:

```bash
python3 scripts/check_format.py
```

Run conservative secret-pattern scan:

```bash
python3 scripts/scan_secrets.py
```

Review the actual Git diff too; automated scanners are not a substitute for human secret/privacy review.

## 10. Expected build outputs

Debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Release APK directory:

```text
app/build/outputs/apk/release/
```

Release AAB directory:

```text
app/build/outputs/bundle/release/
```

Inspect actual filenames before signing because generated names may change with build configuration.

## 11. Production signing policy

Never place these in Git:

- `.jks`/`.keystore` files;
- keystore passwords;
- key passwords;
- signing environment dumps;
- base64-encoded private signing material;
- production CI signing credentials.

Signing material belongs in secure local storage or protected CI/store secret infrastructure.

The repository default release build intentionally does not embed a production signing configuration.

## 12. Create a signing key when establishing a new signing identity

Example:

```bash
keytool -genkeypair -v -keystore spendcalc-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias spendcalc
```

This is explained in detail in [`android-build-guide.md`](android-build-guide.md).

Back up the production signing identity securely. Do not lose it and do not commit it.

## 13. Manual release APK signing

Assuming Gradle generated:

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

First align it:

```bash
zipalign -v -p 4 app/build/outputs/apk/release/app-release-unsigned.apk SpendCalc-release-aligned.apk
```

Check alignment:

```bash
zipalign -c -v 4 SpendCalc-release-aligned.apk
```

Sign:

```bash
apksigner sign --ks spendcalc-release.jks --ks-key-alias spendcalc --out SpendCalc-1.0.0-release.apk SpendCalc-release-aligned.apk
```

Verify:

```bash
apksigner verify --verbose --print-certs SpendCalc-1.0.0-release.apk
```

Do not put passwords directly into public scripts/logs. Prefer secure prompts or protected secret injection.

## 14. Manual AAB signing

Build:

```bash
gradle bundleRelease
```

Assuming output:

```text
app/build/outputs/bundle/release/app-release.aab
```

Sign with JDK `jarsigner` when using a manual signing flow:

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore spendcalc-release.jks app/build/outputs/bundle/release/app-release.aab spendcalc
```

Verify:

```bash
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

For app-store publishing, follow the current store's app-signing/upload-key requirements.

## 15. Install the final signed APK

List devices:

```bash
adb devices
```

Install:

```bash
adb install SpendCalc-1.0.0-release.apk
```

For an upgrade signed with the same identity:

```bash
adb install -r SpendCalc-1.0.0-release.apk
```

If a debug-signed package with the same application ID is installed, a production-signed release may require uninstalling the debug version because Android rejects incompatible signatures.

## 16. Functional release checks

Before publishing, verify at least:

- app launches successfully;
- onboarding works on a clean-data install;
- itemized expense addition/removal;
- subtotal calculation;
- discount;
- tax;
- tip;
- service charge;
- split-bill calculation;
- manual exchange-rate conversion;
- decimal precision and rounding behavior;
- calculation validation/error messages;
- history save/delete/clear;
- history retention settings;
- template save/load/delete;
- text receipt export/share;
- CSV export/share;
- PDF receipt export/share;
- light theme;
- dark theme;
- system theme;
- large-text preference;
- reduced-motion preference;
- phone layout;
- tablet/wide-screen layout;
- About screen version/license/support/repository/funding data;
- no required account/network for core operation.

## 17. Accessibility release checks

Follow [`accessibility.md`](accessibility.md), including:

- TalkBack traversal;
- meaningful labels/semantics;
- large font scale;
- touch target review;
- non-color-only validation;
- light/dark theme contrast;
- small and wide screen behavior.

## 18. Privacy/security release checks

Confirm:

- no unintended Internet permission was introduced;
- no analytics/ads SDK was accidentally added without documentation/review;
- app-local history/templates/preferences behavior still matches `PRIVACY.md`;
- FileProvider remains non-exported;
- temporary read permissions are used for shared export files;
- CSV formula-prefix protections remain covered by tests;
- no secrets/credentials are present in source/artifacts;
- logging does not expose sensitive user data.

## 19. Upgrade testing

When a previous public release exists:

1. install previous signed release;
2. create representative history/templates/settings;
3. install the new release signed with the same identity;
4. verify upgrade succeeds;
5. verify user data remains correct;
6. verify Room migrations when schema versions change;
7. verify app launches and core calculations after upgrade.

Never use destructive database migration as a shortcut for normal production upgrades.

## 20. Fresh-install testing

On a test device:

```bash
adb uninstall in.sanskar.spendcalc
```

Then install the final signed release APK and verify first-run behavior from a genuinely clean app-data state.

## 21. Tagging a verified release

After verification and merge to the intended release commit:

```bash
git tag -s v1.0.0 -m "SpendCalc 1.0.0"
git push origin v1.0.0
```

- `git tag -s` creates a cryptographically signed Git tag when Git signing is configured.
- `-m` supplies the tag message.
- `git push origin v1.0.0` publishes that tag to GitHub.

Use an unsigned tag only when signed tagging is unavailable and document that limitation.

## 22. Release artifacts

A release may contain:

- signed production APK where direct APK distribution is intended;
- signed AAB for store upload;
- checksums;
- release notes/changelog reference;
- commit/tag identity.

Do not publish an unsigned release APK as though it were a production-installable artifact.

## 23. Artifact verification

Before publishing:

- verify signature/certificate;
- verify APK alignment if manually signed;
- install it on a test target;
- check the displayed app version;
- run representative calculations;
- verify export functions;
- inspect permission expectations;
- confirm no debug credentials/endpoints/logging are present;
- confirm artifact came from approved release source.

## 24. Checksums

A release process may publish cryptographic hashes so downloaded artifacts can be verified.

### Windows PowerShell

```powershell
Get-FileHash .\SpendCalc-1.0.0-release.apk -Algorithm SHA256
```

### macOS/Linux

```bash
shasum -a 256 SpendCalc-1.0.0-release.apk
```

or on many Linux distributions:

```bash
sha256sum SpendCalc-1.0.0-release.apk
```

Publish only the checksum for the exact final artifact being distributed.

## 25. Release failure/rollback

If a release contains a blocker defect:

1. stop promotion/distribution where possible;
2. document the regression in a tracked issue;
3. branch/fix from the correct source point;
4. add a regression test;
5. increase the version appropriately;
6. build/sign/verify a new patch release;
7. do not silently replace an already published tag/artifact with different bytes.

## 26. Complete release command checklist

Typical local command sequence:

```bash
git status
git rev-parse --short HEAD
gradle clean
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
gradle assembleRelease
gradle bundleRelease
python3 scripts/check_format.py
python3 scripts/scan_secrets.py
adb devices
gradle connectedDebugAndroidTest
```

Then perform secure release signing and verification as documented above.

## 27. Related documentation

- [`android-build-guide.md`](android-build-guide.md) — complete Android executable/build/sign/install guide.
- [`command-reference.md`](command-reference.md) — meanings of every major command/flag.
- [`setup.md`](setup.md) — workstation setup.
- [`testing.md`](testing.md) — testing strategy.
- [`accessibility.md`](accessibility.md) — accessibility release gates.
- [`troubleshooting.md`](troubleshooting.md) — build/install diagnosis.
- [`../SECURITY.md`](../SECURITY.md) — security policy.
- [`../PRIVACY.md`](../PRIVACY.md) — privacy model.
- [`../CHANGELOG.md`](../CHANGELOG.md) — release history.

**Made by the Sanskar**
