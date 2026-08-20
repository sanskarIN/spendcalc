# SpendCalc Setup Guide

This guide explains how to prepare a Windows, macOS, or Linux development machine to build the SpendCalc Android application.

For the full APK/AAB/signing/install workflow, continue to [`android-build-guide.md`](android-build-guide.md) after completing this setup.

## 1. Project requirements

SpendCalc currently uses:

- Kotlin `2.0.21`;
- Android Gradle Plugin `8.7.3`;
- KSP `2.0.21-1.0.28`;
- Jetpack Compose;
- Android `minSdk = 26`;
- Android `targetSdk = 35`;
- Android `compileSdk = 35`;
- Java/JVM target `17`;
- documented local Gradle `8.9`.

The repository does not currently commit a Gradle wrapper JAR, so the documented command-line setup uses an installed Gradle 8.9 executable.

## 2. Required software

### Git

Git is used to clone and manage the source repository.

Verify:

```bash
git --version
```

### JDK 17

Java 17 is used by Gradle/Android build tooling and matches the project's Java/Kotlin bytecode target.

Verify runtime:

```bash
java -version
```

Verify compiler:

```bash
javac -version
```

Both should point to a JDK 17 installation for the documented build environment.

### Android Studio

Android Studio is the recommended IDE because it includes Android-specific project sync, SDK management, emulator management, Logcat, APK analysis, and device deployment.

Install Android Studio, then use its SDK Manager to install at least:

- Android SDK Platform 35;
- compatible Android SDK Build-Tools;
- Android SDK Platform-Tools;
- Android Emulator if you want virtual devices;
- an API 26+ system image if you want to create an emulator.

### Gradle 8.9

Verify:

```bash
gradle --version
```

The output should identify Gradle 8.9 and the JDK being used.

## 3. Clone the repository

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
```

`git clone` downloads the repository. `cd spendcalc` changes the shell's current directory to the project root.

Check source state:

```bash
git status
```

Optional project commit email:

```bash
git config user.email "sanskarin@outlook.in"
```

This applies to the current repository clone rather than globally.

## 4. Android SDK location

Android Studio normally creates `local.properties` automatically when it knows the SDK location.

If using command-line Gradle and the file is missing, create a local-only `local.properties` in the repository root.

### Windows example

```properties
sdk.dir=C:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
```

### macOS example

```properties
sdk.dir=/Users/YOUR_USER/Library/Android/sdk
```

### Linux example

```properties
sdk.dir=/home/YOUR_USER/Android/Sdk
```

Do not commit `local.properties`; the path is specific to one machine.

## 5. Check `JAVA_HOME`

Some command-line environments need `JAVA_HOME` to point to JDK 17.

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

If Gradle reports the wrong Java version, configure `JAVA_HOME` for JDK 17 and make sure that JDK's `bin` directory is correctly resolved by your shell.

## 6. Check Android command-line access

ADB comes from Android SDK Platform-Tools.

Verify:

```bash
adb version
```

List connected devices/emulators:

```bash
adb devices
```

If `adb` is not found, add the Android SDK `platform-tools` directory to PATH or invoke `adb` using its full installation path.

## 7. Open in Android Studio

1. Start Android Studio.
2. Choose to open an existing project.
3. Select the cloned `spendcalc` repository directory.
4. Allow Gradle project sync to complete.
5. Ensure Android SDK 35 is installed if prompted.
6. Ensure Gradle uses JDK 17.
7. Select an API 26+ emulator or connected Android device.
8. Run the `app` configuration.

SpendCalc requires no API key, remote backend, account, or Android Internet permission for core runtime operation.

## 8. First command-line build

From the repository root:

```bash
gradle clean
```

`clean` removes previous generated build outputs.

Run JVM unit tests:

```bash
gradle testDebugUnitTest
```

Run Android lint:

```bash
gradle lintDebug
```

Build the debug APK:

```bash
gradle assembleDebug
```

Expected debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 9. Install the debug APK

With one compatible device/emulator connected:

```bash
gradle installDebug
```

Or build and install manually:

```bash
gradle assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

`adb install -r` requests installation/reinstallation while retaining existing app data when signature/package rules permit.

## 10. Run Android instrumentation/UI tests

Start an emulator or connect/authorize a physical Android device, then run:

```bash
gradle connectedDebugAndroidTest
```

Use:

```bash
adb devices
```

if Gradle reports that no Android target is available.

## 11. Build release artifacts

Release APK:

```bash
gradle assembleRelease
```

Output directory:

```text
app/build/outputs/apk/release/
```

Release Android App Bundle:

```bash
gradle bundleRelease
```

Output directory:

```text
app/build/outputs/bundle/release/
```

The repository intentionally does not contain production signing credentials. Treat default release outputs as non-production artifacts until they have been securely signed and verified.

Detailed signing instructions: [`android-build-guide.md`](android-build-guide.md).

## 12. Build and quality scripts

Repository formatting/source hygiene check:

```bash
python3 scripts/check_format.py
```

Repository conservative secret-pattern scan:

```bash
python3 scripts/scan_secrets.py
```

On Windows installations where Python is exposed as `python` rather than `python3`:

```powershell
python scripts/check_format.py
python scripts/scan_secrets.py
```

## 13. Recommended first full verification

```bash
gradle clean testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease
```

With a connected Android target, additionally run:

```bash
gradle connectedDebugAndroidTest
```

Then run the repository utility checks:

```bash
python3 scripts/check_format.py
python3 scripts/scan_secrets.py
```

## 14. Common setup failures

### `gradle` not recognized / command not found

Gradle is either not installed or its `bin` directory is not on PATH.

Check:

```bash
gradle --version
```

### Wrong Java version

Check:

```bash
java -version
javac -version
```

Use JDK 17 for this project's documented environment.

### Android SDK not found

Confirm Android Studio SDK settings and `local.properties`.

### Android SDK Platform 35 missing

Install API 35 using Android Studio SDK Manager.

### `adb` not found

Install Platform-Tools and add the SDK `platform-tools` directory to PATH.

### No device available

Check:

```bash
adb devices
```

Start an emulator or enable/authorize USB debugging on a physical test device.

### Dependency download problems

Check network/proxy configuration. The app itself is offline-first at runtime, but a fresh source build may need network access to download Gradle plugins and library dependencies.

## 15. Platform notes

### Windows

Android Studio commonly installs the SDK under:

```text
C:\Users\YOUR_USER\AppData\Local\Android\Sdk
```

PowerShell file check example:

```powershell
Get-Item .\app\build\outputs\apk\debug\app-debug.apk
```

### macOS

Typical SDK path:

```text
/Users/YOUR_USER/Library/Android/sdk
```

Artifact check:

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

### Linux

A common SDK path is:

```text
/home/YOUR_USER/Android/Sdk
```

Artifact check:

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

## 16. Next documentation to read

- [`android-build-guide.md`](android-build-guide.md) — complete APK/AAB generation, signing, verification, installation, and release-candidate commands.
- [`command-reference.md`](command-reference.md) — detailed meaning of Git, Gradle, ADB, signing, and diagnostic commands.
- [`development.md`](development.md) — coding/development rules.
- [`testing.md`](testing.md) — test strategy.
- [`release.md`](release.md) — release process.
- [`troubleshooting.md`](troubleshooting.md) — diagnostics.

**Made by the Sanskar**
