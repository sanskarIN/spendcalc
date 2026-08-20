# SpendCalc Troubleshooting Guide

This guide covers common setup, Gradle, Android SDK, build, APK/AAB, ADB, test, signing, Room/KSP, export, and release problems.

For commands and their meanings, see [`command-reference.md`](command-reference.md). For the complete executable workflow, see [`android-build-guide.md`](android-build-guide.md).

## 1. Collect basic environment information

Before diagnosing a build problem, run:

```bash
git status
git rev-parse --short HEAD
java -version
gradle --version
adb version
adb devices
```

When sharing logs publicly, remove usernames, private paths, serial numbers when unnecessary, credentials, tokens, signing information, and other private data.

## 2. `gradle` is not recognized / command not found

Symptoms:

```text
gradle: command not found
```

or Windows reports that `gradle` is not recognized.

Cause: Gradle is not installed or the Gradle `bin` directory is not on PATH.

Verify:

```bash
gradle --version
```

SpendCalc currently documents local Gradle 8.9 because the repository does not commit a Gradle wrapper JAR.

After changing PATH, open a new terminal and run the check again.

## 3. Gradle uses the wrong Java version

SpendCalc targets Java/JVM 17 bytecode.

Check:

```bash
java -version
javac -version
gradle --version
```

`gradle --version` is especially important because it shows the JVM Gradle itself is using.

In Android Studio, check the configured Gradle JDK and select JDK 17 for the documented project environment.

If `JAVA_HOME` is used, verify it points to the intended JDK.

### Windows PowerShell

```powershell
$env:JAVA_HOME
```

### Windows Command Prompt

```cmd
echo %JAVA_HOME%
```

### macOS/Linux

```bash
echo "$JAVA_HOME"
```

## 4. Android SDK 35 is missing

SpendCalc uses:

```text
compileSdk = 35
targetSdk = 35
```

Install Android SDK Platform 35 and compatible Build-Tools from Android Studio SDK Manager.

Then retry:

```bash
gradle clean assembleDebug
```

## 5. `SDK location not found`

Create a local `local.properties` in the repository root.

Windows example:

```properties
sdk.dir=C:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
```

macOS example:

```properties
sdk.dir=/Users/YOUR_USER/Library/Android/sdk
```

Linux example:

```properties
sdk.dir=/home/YOUR_USER/Android/Sdk
```

This file is machine-specific and intentionally ignored by Git.

## 6. Gradle cannot download a dependency

A fresh source build can require network access even though SpendCalc's app runtime is offline-first.

Check:

- internet connection;
- VPN/proxy/firewall behavior;
- Maven/Google repository accessibility;
- system clock/certificate problems;
- Gradle cache state.

For a deliberate dependency refresh:

```bash
gradle assembleDebug --refresh-dependencies
```

Do not use dependency refresh for every normal build.

If dependencies are already cached and you want to prove an offline build:

```bash
gradle assembleDebug --offline
```

The command fails if required artifacts are not available locally.

## 7. Gradle daemon appears stale

Stop Gradle daemons:

```bash
gradle --stop
```

Then retry:

```bash
gradle clean assembleDebug --stacktrace
```

## 8. Build error does not show enough information

Add stack trace:

```bash
gradle assembleDebug --stacktrace
```

Add informational logging:

```bash
gradle assembleDebug --info --stacktrace
```

Very verbose logging:

```bash
gradle assembleDebug --debug --stacktrace
```

Review verbose logs before sharing because they can contain local environment details.

## 9. Debug APK cannot be found

Build it:

```bash
gradle assembleDebug
```

Expected path:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Windows PowerShell:

```powershell
Get-Item .\app\build\outputs\apk\debug\app-debug.apk
```

macOS/Linux:

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

If the build command failed, no valid APK should be expected.

## 10. Release APK cannot be found

Run:

```bash
gradle assembleRelease
```

Inspect:

```text
app/build/outputs/apk/release/
```

Do not assume the exact release filename; inspect the generated directory.

## 11. AAB cannot be found

Run:

```bash
gradle bundleRelease
```

Inspect:

```text
app/build/outputs/bundle/release/
```

An AAB is a publishing bundle and is not normally installed directly with `adb install`.

## 12. `adb` is not recognized / command not found

ADB comes from Android SDK Platform-Tools.

Check:

```bash
adb version
```

If missing:

- install Platform-Tools through Android SDK Manager;
- add the SDK `platform-tools` directory to PATH;
- or invoke the `adb` executable using its full path.

## 13. `adb devices` shows no device

Run:

```bash
adb devices
```

For a physical device:

- enable Developer Options;
- enable USB debugging;
- connect a data-capable USB cable;
- accept the debugging authorization prompt;
- install appropriate device drivers on Windows if required.

For an emulator:

- create/start an Android Virtual Device in Android Studio;
- wait for Android to finish booting;
- rerun `adb devices`.

## 14. ADB device is `unauthorized`

Unlock the physical device and accept the USB debugging authorization prompt.

If the prompt was denied, revoke USB debugging authorizations in Android Developer Options and reconnect only on a trusted development machine.

## 15. More than one Android device is connected

List serials:

```bash
adb devices
```

Select a specific target:

```bash
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
```

Replace `emulator-5554` with the actual target serial shown by `adb devices`.

## 16. `INSTALL_FAILED_UPDATE_INCOMPATIBLE`

A common cause is signature mismatch: for example, a debug-signed SpendCalc is already installed and you are trying to install a release signed by a different key.

For a disposable test installation, uninstall the old package:

```bash
adb uninstall in.sanskar.spendcalc
```

Then install the intended APK.

For real upgrades, both versions must use the appropriate compatible signing identity.

## 17. `INSTALL_FAILED_VERSION_DOWNGRADE`

The installed app may have a higher `versionCode` than the APK being installed.

Check the intended source version. For development-only reset, uninstall the existing test package and reinstall. Do not use a lower `versionCode` for a store production upgrade.

## 18. Release APK is unsigned

The repository does not include production signing credentials.

Build:

```bash
gradle assembleRelease
```

Then use the secure signing flow documented in [`android-build-guide.md`](android-build-guide.md):

1. align with `zipalign`;
2. sign with `apksigner`;
3. verify with `apksigner verify`;
4. install/test the signed artifact.

## 19. `apksigner` or `zipalign` not found

These tools are supplied by Android SDK Build-Tools.

Install compatible Build-Tools through Android Studio SDK Manager and add the selected Build-Tools directory to PATH, or invoke the utilities using their full SDK paths.

Do not copy signing tools from untrusted third-party downloads.

## 20. APK signature verification fails

Run:

```bash
apksigner verify --verbose --print-certs SpendCalc-1.0.0-release.apk
```

If verification fails:

- make sure you signed the final aligned APK;
- do not modify an APK after signing;
- repeat the build → alignment → signing process from a clean artifact;
- confirm the correct keystore/key alias was used.

Never expose the keystore/password to troubleshoot publicly.

## 21. `zipalign` verification fails

Check:

```bash
zipalign -c -v 4 SpendCalc-release-aligned.apk
```

If it fails, regenerate the aligned APK from the unsigned release file, then sign the newly aligned output.

Manual APK order is important: **align before signing**.

## 22. Instrumentation tests fail to find a device

Verify:

```bash
adb devices
```

Then rerun:

```bash
gradle connectedDebugAndroidTest
```

The test target must be compatible with the project's minimum SDK.

## 23. JVM unit tests fail but app builds

Run only unit tests with details:

```bash
gradle testDebugUnitTest --stacktrace
```

Do not ignore failing tests simply because `assembleDebug` succeeds. A build artifact can compile while behavior is incorrect.

## 24. Android lint fails

Run:

```bash
gradle lintDebug --stacktrace
```

Read the generated lint report under Gradle build reports. Fix the actual issue rather than globally suppressing lint without justification.

## 25. KSP/Room schema errors

Try a clean build:

```bash
gradle clean assembleDebug
```

Room database version currently begins at 1. Once released schema history exists, keep historical schema snapshots and add explicit migrations for version changes.

Do not delete released schemas or enable destructive migration simply to silence a migration failure.

## 26. Unit test failures around decimal values

Use decimal strings when constructing `BigDecimal` values:

```kotlin
BigDecimal("0.10")
```

Do not create monetary values from binary floating-point literals such as:

```kotlin
BigDecimal(0.1)
```

Binary floating-point representation can introduce unexpected decimal values.

## 27. App shows validation instead of a result

Check input requirements:

- item amounts are valid non-negative decimals;
- percentages are within the app's accepted range;
- split/people count is at least 1;
- currency codes are exactly three letters;
- exchange rate is greater than 0.

Validation is intentional and should not be bypassed in the finance engine.

## 28. History does not contain a calculation

History is opt-in. Save a valid result explicitly.

Also check:

- auto-delete/retention preference;
- whether app data was cleared;
- whether the package was uninstalled/reinstalled;
- whether you are testing a different device/emulator profile.

## 29. Templates disappeared

Templates live in app-local Room storage. Uninstalling the app or clearing package data can remove local application data.

Check whether this command was run:

```bash
adb shell pm clear in.sanskar.spendcalc
```

That command intentionally clears app data on the selected test device.

## 30. CSV/PDF share target is missing

Android's share sheet lists installed apps that advertise support for the exported MIME type.

The export file can be created correctly even if no third-party app is installed that accepts it.

Core SpendCalc calculation functionality does not depend on a share target.

## 31. External GitHub/BMC/email action does nothing

The Android device needs an installed application capable of handling the selected URL/email intent.

Core SpendCalc behavior remains available without those external handlers.

## 32. Release-only crash/shrinking issue

Reproduce the release variant:

```bash
gradle assembleRelease --stacktrace
```

Do not solve a release shrinker problem with a broad rule such as:

```text
-keep class ** { *; }
```

Instead identify the specific reflection/serialization/generated-code requirement and add the narrowest justified ProGuard/R8 rule with a regression test or documented verification.

## 33. Debug works but release behaves differently

Release builds enable minification and resource shrinking, so inspect:

- reflection-based libraries;
- generated classes;
- resource lookups by name;
- ProGuard/R8 warnings;
- release-only code paths.

Build both variants during release verification:

```bash
gradle assembleDebug assembleRelease
```

## 34. Build output appears stale

Run:

```bash
gradle clean assembleDebug
```

If dependency state is also suspect:

```bash
gradle clean assembleDebug --refresh-dependencies
```

Do not delete arbitrary Gradle/Android SDK directories as a first troubleshooting step.

## 35. Need to prove which source produced an artifact

Before building, record:

```bash
git status
git rev-parse --short HEAD
```

Release artifacts should be traceable to a reviewed commit/tag.

## 36. Need more help

Read:

- [`setup.md`](setup.md)
- [`android-build-guide.md`](android-build-guide.md)
- [`command-reference.md`](command-reference.md)
- [`release.md`](release.md)
- [`../SUPPORT.md`](../SUPPORT.md)

When reporting a reproducible build problem, include the exact command, sanitized error output, Android/Java/Gradle environment, and commit SHA. Never include passwords, signing keys, access tokens, or private user data.

**Made by the Sanskar**
