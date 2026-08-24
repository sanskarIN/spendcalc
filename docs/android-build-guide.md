# SpendCalc Android Build, APK, AAB, Signing, and Installation Guide

This guide explains how to build, test, install, inspect, sign, and verify SpendCalc Android artifacts from source.

> Repository: `https://github.com/sanskarIN/spendcalc`
>
> Application ID: `in.sanskar.spendcalc`
>
> Current release candidate: `2.15.4`
>
> Android `versionCode = 21504`
>
> Minimum Android API: 26
>
> Compile/target API: 35
>
> Java/JVM target: 17
>
> Primary language/UI: Kotlin + Jetpack Compose + Material 3

The application version is independent from the Room database schema and explicit SpendCalc backup schema. Both compatibility schemas remain at version `1` unless their stored formats actually change.

---

## 1. Android artifact types

### APK

An APK is an installable Android application package.

SpendCalc commonly produces:

- a **debug APK** for development and local testing;
- a **release APK** for optimized distribution preparation.

Debug APKs are automatically signed with a development key. Production release artifacts must use a controlled production signing identity that is never committed to Git.

### AAB

An Android App Bundle (`.aab`) is the preferred publishing format for Google Play. An AAB is not normally installed directly with `adb install`; the store or `bundletool` generates device-specific APKs from it.

Use:

- APK for local/device testing and direct distribution;
- AAB for app-store publishing;
- signed production artifacts only after the exact source commit passes the release gates.

---

## 2. Current Android build metadata

`app/build.gradle.kts` is authoritative for application release metadata.

```kotlin
android {
    namespace = "in.sanskar.spendcalc"
    compileSdk = 35

    defaultConfig {
        applicationId = "in.sanskar.spendcalc"
        minSdk = 26
        targetSdk = 35
        versionCode = 21504
        versionName = "2.15.4"
    }
}
```

Meaning:

| Setting | Meaning |
| --- | --- |
| `namespace` | Android/Kotlin generated-code namespace. |
| `applicationId` | Installed/store package identity. |
| `minSdk = 26` | Oldest declared supported Android API. |
| `targetSdk = 35` | Android behavior level the app targets. |
| `compileSdk = 35` | SDK level used to compile the app. |
| `versionCode = 21504` | Monotonically increasing Android/store upgrade number for 2.15.4. |
| `versionName = "2.15.4"` | Human-readable application version. |

Release builds enable code/resource shrinking:

```kotlin
release {
    isMinifyEnabled = true
    isShrinkResources = true
}
```

The project targets Java/JVM 17.

---

## 3. Required tools

Install and verify:

1. Git.
2. JDK 17.
3. Android Studio.
4. Android SDK Platform 35.
5. Android SDK Build-Tools.
6. Android SDK Platform-Tools (`adb`).
7. Gradle 8.9 for the repository's documented command-line workflow.

The repository does not commit a Gradle wrapper JAR, so examples use the global `gradle` command.

Check the environment:

```bash
git --version
java -version
javac -version
gradle --version
adb version
```

For this repository, `java`/`javac` should resolve to JDK 17 and Gradle should be compatible with Android Gradle Plugin 8.7.3.

---

## 4. Clone and enter the repository

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
git status
```

Optional repository-local Git identity:

```bash
git config user.name "Sanskar"
git config user.email "sanskarin@outlook.in"
```

Do not commit machine-specific Android SDK paths, keystores, passwords, tokens, or private configuration.

---

## 5. Configure the Android SDK

Android Studio normally generates `local.properties` automatically.

Typical examples:

### Windows

```properties
sdk.dir=C:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
```

### macOS

```properties
sdk.dir=/Users/YOUR_USER/Library/Android/sdk
```

### Linux

```properties
sdk.dir=/home/YOUR_USER/Android/Sdk
```

`local.properties` is machine-specific and must not be committed.

---

## 6. Verify the project before building

Run repository guards first:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

On Windows, use `python` instead of `python3` if that is how Python is installed.

These checks cover text hygiene, namespaces, tracked-file documentation, Android resource references, local-first Android security policy, required repository metadata/links/version alignment, and common secret patterns.

---

## 7. Clean and run JVM tests

```bash
gradle --no-daemon clean
gradle --no-daemon testDebugUnitTest
```

`clean` removes generated build output.

`testDebugUnitTest` runs local JVM tests, including finance arithmetic, persistence/repository behavior, backup encoding/validation, exports, path safety, safe logging, and deterministic regression/fuzz coverage.

---

## 8. Compile Android instrumentation tests

Before running an emulator/device, confirm Android test sources compile:

```bash
gradle --no-daemon assembleDebugAndroidTest
```

This does not execute connected tests. It proves the instrumentation test APK can be built.

---

## 9. Run Android lint

Run the full lint task used by CI:

```bash
gradle --no-daemon lint
```

For a debug-focused local pass:

```bash
gradle --no-daemon lintDebug
```

Review generated reports under `app/build/reports/` if lint fails.

---

## 10. Build the debug APK

```bash
gradle --no-daemon assembleDebug
```

Expected artifact:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Inspect it:

### Windows PowerShell

```powershell
Get-Item .\app\build\outputs\apk\debug\app-debug.apk
```

### Windows Command Prompt

```cmd
dir app\build\outputs\apk\debug\app-debug.apk
```

### macOS/Linux

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

---

## 11. Install the debug APK

With one compatible device/emulator connected:

```bash
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Or let Gradle build/install it:

```bash
gradle --no-daemon installDebug
```

If multiple devices are attached, select one explicitly:

```bash
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
```

Launch the installed app with a basic launcher smoke command:

```bash
adb shell monkey -p in.sanskar.spendcalc 1
```

---

## 12. Run connected Android instrumentation tests

Start a compatible emulator or attach a physical Android device, then run:

```bash
gradle --no-daemon connectedDebugAndroidTest
```

This builds, installs, and executes Android instrumentation tests.

The GitHub Actions `Android Instrumentation` workflow runs the connected suite on an API 35 Google APIs x86_64 emulator. A release candidate is not considered fully verified merely because the test APK compiles; the connected test workflow must execute successfully for the exact release commit.

---

## 13. Build the release APK

```bash
gradle --no-daemon assembleRelease
```

Expected directory:

```text
app/build/outputs/apk/release/
```

The repository intentionally does not contain production signing material, so treat the default release output as unsigned/untrusted for production until it is securely signed outside source control.

Inspect the directory instead of assuming a generated filename:

```bash
ls -lh app/build/outputs/apk/release/
```

or on PowerShell:

```powershell
Get-ChildItem .\app\build\outputs\apk\release\
```

---

## 14. Build the release AAB

```bash
gradle --no-daemon bundleRelease
```

Expected directory:

```text
app/build/outputs/bundle/release/
```

Typical bundle path:

```text
app/build/outputs/bundle/release/app-release.aab
```

An AAB is primarily a publishing artifact and is not installed directly with `adb install`.

---

## 15. Recommended automated pre-release sequence

Run:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
gradle --no-daemon clean testDebugUnitTest
gradle --no-daemon assembleDebugAndroidTest
gradle --no-daemon lint
gradle --no-daemon assembleDebug
gradle --no-daemon assembleRelease
gradle --no-daemon bundleRelease
```

Then run connected instrumentation tests separately on a device/emulator:

```bash
gradle --no-daemon connectedDebugAndroidTest
```

Passing these commands does not replace manual accessibility, backup/restore, export/share, offline, screenshot, production-signing, and representative-device checks in `verification.md`.

---

## 16. ADB reference for SpendCalc

List devices:

```bash
adb devices -l
```

Reinstall a matching-signature APK while keeping app data when Android permits:

```bash
adb install -r <apk-path>
```

Uninstall SpendCalc:

```bash
adb uninstall in.sanskar.spendcalc
```

Inspect installed package metadata:

```bash
adb shell dumpsys package in.sanskar.spendcalc
```

Force stop:

```bash
adb shell am force-stop in.sanskar.spendcalc
```

Clear app data on a test device:

```bash
adb shell pm clear in.sanskar.spendcalc
```

Stream logs:

```bash
adb logcat
```

Never publish logs containing sensitive user data or local secrets.

---

## 17. Inspect APK metadata

When `aapt` is available:

```bash
aapt dump badging app/build/outputs/apk/debug/app-debug.apk
```

For the 2.15.4 candidate, artifact inspection must agree with:

```text
package/applicationId: in.sanskar.spendcalc
versionName: 2.15.4
versionCode: 21504
minSdk: 26
targetSdk: 35
```

Inspect declared permissions:

```bash
aapt dump permissions app/build/outputs/apk/debug/app-debug.apk
```

SpendCalc's local-first core must not silently gain an `INTERNET` permission without an explicit feature, security/privacy review, and documentation update.

---

## 18. Create a production signing key

A production signing identity must be controlled outside the repository.

Example key creation:

```bash
keytool -genkeypair -v -keystore spendcalc-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias spendcalc
```

Options:

- `-genkeypair` creates a key pair;
- `-keystore` selects the keystore file;
- `-keyalg RSA` selects RSA;
- `-keysize 2048` selects key size;
- `-validity 10000` sets certificate validity days;
- `-alias spendcalc` names the entry.

Inspect a keystore:

```bash
keytool -list -v -keystore spendcalc-release.jks
```

Never commit `.jks`/`.keystore` files or passwords. Keep secure backups of the production signing identity.

---

## 19. Align a release APK

Find the exact unsigned release APK produced by Gradle, then align it with Android Build-Tools:

```bash
zipalign -v -p 4 app/build/outputs/apk/release/app-release-unsigned.apk SpendCalc-release-aligned.apk
```

Verify alignment:

```bash
zipalign -c -v 4 SpendCalc-release-aligned.apk
```

`-c` checks an existing APK instead of creating one.

---

## 20. Sign the 2.15.4 release APK

```bash
apksigner sign --ks spendcalc-release.jks --ks-key-alias spendcalc --out SpendCalc-2.15.4-release.apk SpendCalc-release-aligned.apk
```

Prefer interactive/password-manager/secret-store prompts rather than placing passwords directly in shell history.

Verify the signed APK:

```bash
apksigner verify --verbose --print-certs SpendCalc-2.15.4-release.apk
```

Install it on a test device:

```bash
adb install SpendCalc-2.15.4-release.apk
```

When replacing a build signed with the same production key:

```bash
adb install -r SpendCalc-2.15.4-release.apk
```

A debug build normally uses a different signing key, so Android may require uninstalling the debug-signed package before installing a production-signed artifact with the same application ID.

---

## 21. Sign and verify an AAB

If your release process requires direct JAR-style bundle signing:

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore spendcalc-release.jks app/build/outputs/bundle/release/app-release.aab spendcalc
```

Verify:

```bash
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

For Google Play, follow the store's current Play App Signing workflow and keep upload/production credentials outside source control.

---

## 22. Generate checksums

Checksums help identify the exact artifact that was tested/distributed.

### Linux

```bash
sha256sum SpendCalc-2.15.4-release.apk
```

### macOS

```bash
shasum -a 256 SpendCalc-2.15.4-release.apk
```

### Windows PowerShell

```powershell
Get-FileHash .\SpendCalc-2.15.4-release.apk -Algorithm SHA256
```

Record the SHA-256 together with the exact Git commit SHA that produced the artifact.

---

## 23. Verify source identity

Before release, record the exact commit:

```bash
git rev-parse HEAD
```

Confirm the working tree is clean:

```bash
git status --short
```

A production artifact should come from the exact commit whose CI/runtime/manual gates were verified. Do not test one commit and publish an artifact built from a later unverified commit.

---

## 24. Versioning rules

For the current release:

```kotlin
versionCode = 21504
versionName = "2.15.4"
```

Rules:

1. `versionCode` must monotonically increase for published Android upgrades.
2. `versionName` is the user-facing semantic version.
3. Do not change the Room database schema merely to match an app version.
4. Do not change the explicit backup schema merely to match an app version.
5. A real storage/serialization compatibility change requires its own schema/version/migration decision and tests.
6. Keep release docs and build metadata aligned in the same change.

A future release must choose a new `versionCode` greater than `21504`.

---

## 25. Fresh-install verification

On a clean test device/emulator:

1. uninstall any previous test package when appropriate;
2. install the exact candidate APK;
3. launch the app;
4. confirm branded splash/onboarding behavior;
5. calculate a representative receipt;
6. save and search history;
7. save/load/delete a template;
8. exercise text/CSV/PDF export;
9. exercise explicit backup/restore;
10. confirm themes/accessibility settings;
11. verify About reports `2.15.4`;
12. repeat critical flows with network disabled.

---

## 26. Upgrade verification

When a previous published version exists:

1. install the previous production-signed release;
2. create representative history/templates/preferences;
3. install `SpendCalc-2.15.4-release.apk` with the same production signing identity;
4. verify the upgrade succeeds without clearing data;
5. verify history/templates/preferences still load correctly;
6. verify backup/export behavior;
7. verify About reports `2.15.4`.

Do not claim upgrade compatibility until this is actually executed against a real prior published artifact.

---

## 27. Offline verification

SpendCalc is local-first.

With Wi-Fi/mobile data disabled, verify:

- calculation;
- history save/search/delete;
- template save/load/delete;
- settings;
- text/CSV/PDF generation;
- local explicit backup/restore through Android document-provider flows that do not themselves require a remote provider.

Core calculation/storage must not depend on an account, API key, or remote service.

---

## 28. Export and FileProvider verification

Verify:

- text receipt share flow;
- CSV share flow and spreadsheet-formula neutralization;
- PDF receipt generation;
- long Unicode content near truncation limits;
- generated cache files are exposed only through the intended non-exported FileProvider configuration;
- no broad filesystem path is shared accidentally.

---

## 29. Backup/restore verification

Verify:

- document creator opens for backup;
- document picker opens for restore;
- restore requires confirmation before replacing current data;
- progress/busy state is visible;
- history/templates/preferences round-trip;
- malformed UTF-8 is rejected;
- checksum-invalid backup data is rejected;
- invalid/noncanonical persisted records are rejected;
- failed replacement does not erase valid existing data.

The backup checksum detects accidental corruption. It is not a digital signature, MAC, or proof of authorship.

---

## 30. Accessibility/layout verification

Before release, manually check:

- light/dark/system themes;
- app large-text preference;
- large Android font scale;
- TalkBack order and labels;
- dialog focus/announcements;
- reduced-motion behavior;
- validation meaning without color alone;
- touch targets;
- small phone layout;
- wide/tablet layout.

Automated Compose tests strengthen confidence but do not replace representative accessibility review.

---

## 31. Real screenshots

Release screenshots must come from a verified build.

Use fictional data only. Do not fabricate screenshots or include private user information, tokens, email contents, real financial records, signing material, or local machine secrets.

Follow `docs/assets/screenshots/README.md`.

---

## 32. GitHub Actions release gates

For the exact final candidate commit, require successful conclusions for:

- CI;
- CodeQL;
- Dependency Review;
- Repository Audit;
- Android Instrumentation.

A successful older commit does not verify a newer head.

After any code/documentation/version commit, re-fetch and evaluate the new exact-head workflow results.

---

## 33. Dependency upgrades during release preparation

Major dependency upgrades should normally remain separate from a release-candidate stabilization change unless the upgrade is required to fix a release blocker.

SpendCalc currently has automated dependency update pull requests. Evaluate each independently for:

- build compatibility;
- Kotlin/AGP/Compose/KSP interaction;
- Room schema/compiler behavior;
- Android test/runtime behavior;
- GitHub Actions runner/runtime/licensing changes.

Do not merge a major dependency jump merely because Dependabot opened it.

---

## 34. Troubleshooting quick checks

### Gradle uses the wrong Java version

```bash
gradle --version
java -version
```

Make sure JDK 17 is active.

### Android SDK missing

Verify `local.properties`, Android Studio SDK Manager, and `ANDROID_HOME`/SDK installation paths.

### Device not detected

```bash
adb kill-server
adb start-server
adb devices -l
```

Check USB debugging authorization or emulator state.

### Installation signature mismatch

A debug-signed package cannot normally be upgraded in place by a production-signed package with the same application ID. Use the correct matching signing identity or uninstall the test package when data preservation is not required.

### Connected test failure

Open:

```text
app/build/reports/androidTests/connected/debug/index.html
```

Review the exact failing assertion/log instead of weakening unrelated production behavior.

### Lint failure

Inspect `app/build/reports/` and address the concrete warning/error at the source.

See `troubleshooting.md` for broader diagnostics.

---

## 35. Final 2.15.4 release sequence

1. Freeze the intended source head.
2. Confirm `versionName = "2.15.4"` and `versionCode = 21504`.
3. Run every repository guard.
4. Run JVM unit/regression/fuzz tests.
5. Compile instrumentation tests.
6. Run full Android lint.
7. Build debug and release artifacts.
8. Run connected Android instrumentation tests.
9. Require all exact-head GitHub workflow families to succeed.
10. Perform manual Android/accessibility/export/backup/offline/layout checks.
11. Capture real screenshots from the verified build using fictional data.
12. Build/sign the production APK/AAB outside Git.
13. Verify the signing certificate.
14. Inspect package/version/SDK/permission metadata.
15. Install and test the exact signed artifact.
16. Record artifact SHA-256 and source commit SHA.
17. Tag `v2.15.4` only after all blocking gates are complete.
18. Publish only artifacts derived from that verified/tagged commit.

**Made by the Sanskar**
