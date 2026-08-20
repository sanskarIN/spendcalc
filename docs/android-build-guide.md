# SpendCalc Android Build, APK, AAB, Signing, and Installation Guide

This document explains how to turn the SpendCalc source code into Android executable/distribution files and how to verify, install, test, sign, and prepare those files for distribution.

It is intentionally detailed. A new contributor should be able to start with a clean computer, understand what each major tool does, build SpendCalc, find the generated files, install the APK on a device, and understand the difference between debug, release, APK, and AAB artifacts.

> Repository: `https://github.com/sanskarIN/spendcalc`
>
> Android application ID: `in.sanskar.spendcalc`
>
> Minimum Android version: API 26 (Android 8.0 Oreo)
>
> Compile/target SDK: API 35
>
> Java/JVM target: 17
>
> Primary language: Kotlin
>
> UI toolkit: Jetpack Compose + Material 3
>
> Current app version: `1.0.0` (`versionCode = 1`)

---

## 1. What is an Android executable file?

Android commonly uses two application package formats:

### APK — Android Package Kit

An `.apk` file is an installable Android application package. It can be installed directly on a compatible Android phone, tablet, emulator, Android TV device if the app supports that form factor, or another Android environment.

For SpendCalc, the most useful APK types are:

- **Debug APK** — intended for development and testing. Gradle signs it automatically with a debug key.
- **Release APK** — optimized for release. In this repository, production signing credentials are intentionally not stored in Git, so the default release artifact must be signed outside the repository before normal distribution.

### AAB — Android App Bundle

An `.aab` file is a publishing bundle, primarily used for Google Play and other tooling that creates device-specific APKs. Users normally do not install an AAB directly by tapping it.

The store or `bundletool` turns an AAB into one or more APKs appropriate for a device.

### Which one should I create?

Use:

- `APK` for local testing, direct installation, internal sharing, or sideloading.
- `AAB` for Google Play publishing.
- a **signed release APK/AAB** for actual production distribution.

---

## 2. SpendCalc build configuration

The Android configuration is defined in `app/build.gradle.kts`.

Important values are:

```kotlin
android {
    namespace = "in.sanskar.spendcalc"
    compileSdk = 35

    defaultConfig {
        applicationId = "in.sanskar.spendcalc"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

Meaning:

| Setting | Meaning |
| --- | --- |
| `namespace` | Kotlin/Android generated-code namespace used by the module. |
| `applicationId` | Unique Android package identity installed on the device and used by app stores. |
| `minSdk = 26` | The app is declared compatible with Android API 26 and newer. |
| `targetSdk = 35` | The Android behavior level against which the app declares that it has been tested. |
| `compileSdk = 35` | Android SDK API level used to compile the source. |
| `versionCode = 1` | Internal monotonically increasing integer used by Android/store upgrade logic. |
| `versionName = "1.0.0"` | Human-readable release version. |

The release build also enables:

```kotlin
release {
    isMinifyEnabled = true
    isShrinkResources = true
}
```

`isMinifyEnabled = true` enables release code optimization/shrinking through R8. `isShrinkResources = true` removes resources that can be proven unused after code shrinking.

The project compiles Java/Kotlin bytecode for Java 17:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = "17"
}
```

---

## 3. Required development tools

### 3.1 Git

Git downloads the source repository and tracks code changes.

Check installation:

```bash
git --version
```

Example output:

```text
git version 2.x.x
```

### 3.2 JDK 17

The Java Development Kit supplies the JVM and Java tools required by Gradle and Android build tooling.

Check Java:

```bash
java -version
```

Check the Java compiler:

```bash
javac -version
```

Both should resolve to a JDK 17 installation for this project.

`java` runs Java applications. `javac` is the Java compiler. Android Studio may include its own JetBrains Runtime/JDK, but command-line Gradle must also be able to find a compatible Java installation.

### 3.3 Android Studio

Android Studio provides:

- Android SDK Manager;
- emulator/AVD management;
- Android debugging;
- Logcat;
- APK/App Bundle generation UI;
- project sync and editing;
- device deployment.

Install Android SDK Platform 35 and compatible Build-Tools from Android Studio's SDK Manager.

### 3.4 Android SDK Platform 35

SpendCalc uses `compileSdk = 35`, so the Android 35 platform must be installed on the build machine.

### 3.5 Android SDK Build-Tools

Build-Tools contain utilities such as `aapt2`, `zipalign`, and `apksigner` used during packaging/signing workflows.

### 3.6 Gradle 8.9

The repository currently does **not** commit a Gradle wrapper JAR. The documented local build therefore uses a compatible local Gradle installation. The project uses Android Gradle Plugin `8.7.3`; this repository standardizes development/CI documentation on Gradle `8.9`.

Check Gradle:

```bash
gradle --version
```

The command prints the Gradle version, JVM, operating system, and other environment information.

---

## 4. Clone the project

Run:

```bash
git clone https://github.com/sanskarIN/spendcalc.git
```

Meaning:

- `git` starts the Git command-line client.
- `clone` copies a remote repository and its history to the local machine.
- the URL identifies the SpendCalc GitHub repository.

Enter the project directory:

```bash
cd spendcalc
```

`cd` means **change directory**.

Confirm repository status:

```bash
git status
```

This reports the current branch and any modified/untracked files.

Optional requested local commit identity:

```bash
git config user.email "sanskarin@outlook.in"
```

This sets the Git commit email for this repository clone only.

---

## 5. Configure the Android SDK path

Android Studio usually creates `local.properties` automatically.

A typical file contains only the local SDK path.

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

Do not commit `local.properties`. It is machine-specific.

---

## 6. Understand the Gradle command structure

A command such as:

```bash
gradle assembleDebug
```

has two important parts:

- `gradle` — starts Gradle using the current project.
- `assembleDebug` — runs the task that assembles the debug variant.

To view available tasks:

```bash
gradle tasks
```

To see all tasks, including less commonly displayed tasks:

```bash
gradle tasks --all
```

To ask Gradle for help:

```bash
gradle help
```

To see detailed build information:

```bash
gradle assembleDebug --info
```

To see stack traces after a failure:

```bash
gradle assembleDebug --stacktrace
```

For an even more verbose stack trace:

```bash
gradle assembleDebug --full-stacktrace
```

`--stacktrace` is especially useful when an error message is too short to identify its source.

---

## 7. First recommended verification build

From the repository root, run:

```bash
gradle clean
```

`clean` removes Gradle-generated build output for the project so the next build does not depend on old compiled artifacts.

Then run unit tests:

```bash
gradle testDebugUnitTest
```

This executes local JVM unit tests for the debug variant.

Run Android lint:

```bash
gradle lintDebug
```

Android Lint performs static analysis for Android correctness, API, resource, accessibility, and other issues.

Build the debug APK:

```bash
gradle assembleDebug
```

A successful command normally ends with a Gradle success message.

---

## 8. Build the debug APK

Command:

```bash
gradle assembleDebug
```

Expected output file:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug APK is automatically signed with a development/debug signing key and can normally be installed directly on a test device.

### Windows PowerShell — verify that the APK exists

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

## 9. Build the release APK

Command:

```bash
gradle assembleRelease
```

Because production signing material is deliberately not stored in the repository, the release output should be treated as an unsigned release artifact until a maintainer signs it securely.

Expected output directory:

```text
app/build/outputs/apk/release/
```

Common output name for an unsigned Android release is:

```text
app-release-unsigned.apk
```

Always inspect the actual directory instead of assuming a file name:

### Windows PowerShell

```powershell
Get-ChildItem .\app\build\outputs\apk\release\
```

### Windows Command Prompt

```cmd
dir app\build\outputs\apk\release\
```

### macOS/Linux

```bash
ls -lh app/build/outputs/apk/release/
```

---

## 10. Build the Android App Bundle (AAB)

For store publishing, build the release bundle:

```bash
gradle bundleRelease
```

Expected output directory:

```text
app/build/outputs/bundle/release/
```

Typical file:

```text
app-release.aab
```

Inspect it:

### Windows PowerShell

```powershell
Get-ChildItem .\app\build\outputs\bundle\release\
```

### macOS/Linux

```bash
ls -lh app/build/outputs/bundle/release/
```

An AAB is a publishing artifact. It is not normally installed directly with `adb install`.

---

## 11. Build everything important in one command

Gradle can run multiple tasks in one invocation:

```bash
gradle clean testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease
```

Meaning, in order:

1. `clean` — delete old build outputs.
2. `testDebugUnitTest` — run debug JVM unit tests.
3. `lintDebug` — run Android lint for debug.
4. `assembleDebug` — build debug APK.
5. `assembleRelease` — build release APK.
6. `bundleRelease` — build release AAB.

This is a useful pre-release local verification command, but it does not replace connected-device tests.

---

## 12. Build only the app module explicitly

SpendCalc currently contains one Android module named `app`.

You can qualify a task with the module path:

```bash
gradle :app:assembleDebug
```

The leading `:` represents the Gradle project path. `:app:assembleDebug` means “run the `assembleDebug` task belonging specifically to the `app` module.”

Equivalent release examples:

```bash
gradle :app:assembleRelease
```

```bash
gradle :app:bundleRelease
```

---

## 13. Install the debug build using Gradle

Connect an Android device with USB debugging enabled, or start an Android emulator.

Then run:

```bash
gradle installDebug
```

This builds the debug variant when required and deploys it to a compatible connected device/emulator.

If more than one device is connected, deployment may require choosing a device through Android Studio or using `adb` directly.

---

## 14. Android Debug Bridge (ADB)

`adb` means **Android Debug Bridge**. It lets a development computer communicate with Android devices/emulators.

Check the tool:

```bash
adb version
```

List connected devices:

```bash
adb devices
```

Example:

```text
List of devices attached
R58...    device
emulator-5554    device
```

Install the debug APK:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Reinstall/update while preserving application data when Android permits it:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

`-r` means reinstall an existing application while keeping its data when package/signature rules allow.

Uninstall SpendCalc:

```bash
adb uninstall in.sanskar.spendcalc
```

Launch the app through Android's monkey utility for a simple launcher start:

```bash
adb shell monkey -p in.sanskar.spendcalc 1
```

Open Logcat from the command line:

```bash
adb logcat
```

Stop Logcat with `Ctrl+C`.

---

## 15. Connected Android instrumentation tests

Start an emulator or connect a device, then run:

```bash
gradle connectedDebugAndroidTest
```

This compiles and installs the debug app/test packages and executes Android instrumentation/UI tests on connected Android targets.

Use this in addition to local unit tests because Android framework/database/UI behavior cannot be fully proven by JVM-only tests.

---

## 16. Create a release signing key

A production release must use a securely controlled signing key.

**Never commit the `.jks`/`.keystore` file or passwords to Git.**

Java's `keytool` can create a keystore:

```bash
keytool -genkeypair -v -keystore spendcalc-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias spendcalc
```

Meaning:

| Part | Meaning |
| --- | --- |
| `keytool` | JDK key/certificate management utility. |
| `-genkeypair` | Generate a public/private key pair. |
| `-v` | Verbose output. |
| `-keystore spendcalc-release.jks` | Output keystore file. |
| `-keyalg RSA` | Use the RSA key algorithm. |
| `-keysize 2048` | Generate a 2048-bit RSA key. |
| `-validity 10000` | Certificate validity in days. |
| `-alias spendcalc` | Logical name used to identify the key inside the keystore. |

Store the keystore and passwords in a secure backup location. Losing the production signing identity can create serious update/distribution problems.

List entries in a keystore:

```bash
keytool -list -v -keystore spendcalc-release.jks
```

Do not paste secrets into public terminal logs, issues, screenshots, CI logs, or documentation.

---

## 17. Manually sign a release APK

A safe manual flow is:

1. build the unsigned release APK;
2. align the APK;
3. sign the aligned APK;
4. verify the signature;
5. install/test the final signed APK.

### Step 1 — build

```bash
gradle assembleRelease
```

### Step 2 — find the exact unsigned APK

```text
app/build/outputs/apk/release/
```

Assume the file is named `app-release-unsigned.apk` for the examples below. If your generated filename differs, use the actual filename.

### Step 3 — align with `zipalign`

```bash
zipalign -v -p 4 app/build/outputs/apk/release/app-release-unsigned.apk SpendCalc-release-aligned.apk
```

Meaning:

- `zipalign` optimizes ZIP/APK data alignment.
- `-v` prints verbose information.
- `-p` aligns uncompressed native shared libraries when applicable.
- `4` specifies 4-byte alignment for other uncompressed data.
- first path = input APK.
- second path = aligned output APK.

Verify alignment:

```bash
zipalign -c -v 4 SpendCalc-release-aligned.apk
```

`-c` means check existing alignment instead of creating a new file.

### Step 4 — sign with `apksigner`

```bash
apksigner sign --ks spendcalc-release.jks --ks-key-alias spendcalc --out SpendCalc-1.0.0-release.apk SpendCalc-release-aligned.apk
```

Meaning:

- `apksigner sign` performs APK signing.
- `--ks` specifies the keystore.
- `--ks-key-alias` selects the signing key alias.
- `--out` names the signed output APK.
- final argument is the aligned unsigned input APK.

The tool may securely prompt for passwords instead of placing them directly in the shell command.

### Step 5 — verify signing

```bash
apksigner verify --verbose --print-certs SpendCalc-1.0.0-release.apk
```

This checks APK signatures and prints signing certificate information.

### Step 6 — install the signed APK

```bash
adb install SpendCalc-1.0.0-release.apk
```

If replacing an already installed build signed with the same key:

```bash
adb install -r SpendCalc-1.0.0-release.apk
```

A debug build and production release build generally use different signing keys, so Android may require uninstalling the debug-signed package before installing a production-signed package with the same application ID.

---

## 18. Sign a release AAB manually

First build the bundle:

```bash
gradle bundleRelease
```

Assuming the generated file is:

```text
app/build/outputs/bundle/release/app-release.aab
```

You can sign the bundle with `jarsigner`:

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore spendcalc-release.jks app/build/outputs/bundle/release/app-release.aab spendcalc
```

Meaning:

- `jarsigner` signs JAR/ZIP-style Java archives, including Android App Bundles.
- `-verbose` prints detailed information.
- `-sigalg SHA256withRSA` selects the signature algorithm.
- `-digestalg SHA-256` selects the digest algorithm.
- `-keystore` identifies the keystore.
- next argument is the AAB.
- final argument is the key alias.

Verify:

```bash
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

For production store workflows, follow the store's current app-signing requirements and keep upload/app-signing keys separate where appropriate.

---

## 19. Android Studio: generate APK without the command line

You can also use Android Studio.

For a development APK, use the Build menu's APK build action. Android Studio/Gradle produces the debug APK in the module build output directory.

For a signed release:

1. open the project in Android Studio;
2. allow Gradle sync to finish;
3. choose the signed APK/App Bundle generation action from the Build menu;
4. choose **APK** or **Android App Bundle**;
5. select/create a keystore outside the repository;
6. choose the key alias;
7. choose the `release` build variant;
8. complete the wizard;
9. verify and test the resulting artifact before distribution.

The exact menu wording can differ between Android Studio releases; the important concept is to use Gradle's release variant with protected signing credentials.

---

## 20. Where generated files are stored

Important Gradle output locations:

```text
app/build/outputs/apk/debug/
app/build/outputs/apk/release/
app/build/outputs/bundle/release/
app/build/reports/
```

Common examples:

```text
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release-unsigned.apk
app/build/outputs/bundle/release/app-release.aab
```

Build output is generated content and should normally not be committed to source control.

---

## 21. Inspect the application package information

Android SDK tooling can inspect built packages.

For example, if `aapt` is available on your PATH:

```bash
aapt dump badging app/build/outputs/apk/debug/app-debug.apk
```

This can show package name, SDK requirements, version values, permissions, and related package metadata.

You can also inspect a device package with ADB:

```bash
adb shell dumpsys package in.sanskar.spendcalc
```

This is useful when diagnosing installation/version/signature behavior.

---

## 22. Verify that SpendCalc does not require Internet permission

After building, inspect the merged manifest or APK permissions. One simple SDK-tool approach is to inspect APK metadata, or inspect the merged manifest under the Gradle intermediates for the selected variant.

The source manifest intentionally does not request Android Internet permission for core functionality.

Never add permissions merely to silence unrelated tooling warnings. Every permission should have a real product requirement and privacy review.

---

## 23. Version a new Android release

Edit in `app/build.gradle.kts`:

```kotlin
versionCode = 2
versionName = "1.0.1"
```

Rules:

- `versionCode` must increase for each published upgrade accepted by Android stores.
- `versionName` is the user-facing version string.
- update `CHANGELOG.md`.
- update release documentation when behavior changes.
- create artifacts only from the exact reviewed release commit/tag.

Check the change:

```bash
git diff -- app/build.gradle.kts
```

`git diff` displays uncommitted content changes. `--` separates Git options/revisions from the file path.

---

## 24. Clean rebuild after changing dependencies or Gradle configuration

Use:

```bash
gradle clean assembleDebug --refresh-dependencies
```

`--refresh-dependencies` tells Gradle to refresh dependency metadata/artifacts instead of relying entirely on cached resolution state. It should not be used for every ordinary build because caching is an important performance feature.

If Gradle daemons need to be stopped:

```bash
gradle --stop
```

Then retry the build.

---

## 25. Offline Gradle builds

Once required dependencies are already available in the Gradle cache, you can ask Gradle not to access the network:

```bash
gradle assembleDebug --offline
```

`--offline` prevents Gradle from trying network dependency resolution. It will fail if a required dependency is not already cached locally.

This is separate from SpendCalc's runtime offline-first behavior. The app can operate without a required network connection, but the first source build may need internet access to download Gradle/Android/Kotlin dependencies.

---

## 26. Dependency information

Display dependencies for the app module:

```bash
gradle :app:dependencies
```

Display a specific configuration, for example debug runtime dependencies:

```bash
gradle :app:dependencies --configuration debugRuntimeClasspath
```

This is useful for understanding transitive libraries and diagnosing version conflicts.

---

## 27. Build scan/logging options

Useful Gradle diagnostic flags include:

```bash
gradle assembleDebug --info
```

More detailed log level:

```bash
gradle assembleDebug --debug
```

Use `--debug` carefully because extremely verbose logs can expose local paths or environment information when pasted publicly.

Standard stack trace:

```bash
gradle assembleDebug --stacktrace
```

Full stack trace:

```bash
gradle assembleDebug --full-stacktrace
```

---

## 28. Common build failures

### `gradle` is not recognized / command not found

Cause: Gradle is not installed or its `bin` directory is not on PATH.

Check:

```bash
gradle --version
```

Fix the Gradle installation/PATH, then open a new terminal.

### Wrong Java version

Check:

```bash
java -version
```

```bash
javac -version
```

Use JDK 17 for the documented SpendCalc build environment.

### Android SDK not found

Check `local.properties` and Android Studio SDK settings.

### API 35 missing

Install Android SDK Platform 35 using Android Studio SDK Manager, then sync/build again.

### Dependency download failure

Check internet access, proxy configuration, repository availability, and Gradle cache state.

### `adb: command not found`

Add the Android SDK `platform-tools` directory to PATH or invoke `adb` using its full path.

### `INSTALL_FAILED_UPDATE_INCOMPATIBLE`

A common cause is trying to update an installed package using an APK signed with a different key. Uninstall the existing test build if appropriate, or install an update signed with the same identity.

### Release APK will not install

A release APK may be unsigned. Sign and verify it before installation/distribution.

### Instrumentation tests say no connected devices

Check:

```bash
adb devices
```

Start an emulator or authorize USB debugging on a physical device.

---

## 29. Recommended development command sequence

For ordinary changes:

```bash
git status
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
```

When a device/emulator is available:

```bash
gradle connectedDebugAndroidTest
```

Then inspect changes:

```bash
git diff
```

---

## 30. Recommended release-candidate command sequence

From a clean reviewed working tree:

```bash
git status
gradle clean
gradle testDebugUnitTest
gradle lintDebug
gradle connectedDebugAndroidTest
gradle assembleDebug
gradle assembleRelease
gradle bundleRelease
```

Then:

1. inspect generated artifacts;
2. sign production artifacts outside Git;
3. verify signatures;
4. install/test the signed APK on a real device;
5. test upgrade behavior from the previous release when applicable;
6. verify About/version information;
7. verify calculations, history, templates, text export, CSV export, PDF export, themes, large text, and reduced motion;
8. review privacy/security documentation;
9. publish only the artifact created from the approved release commit.

---

## 31. Shell differences: Windows vs macOS/Linux

This repository currently uses a local `gradle` executable, so these Gradle commands are intentionally the same when `gradle` is correctly on PATH:

```bash
gradle assembleDebug
```

If a Gradle Wrapper is added to the repository in the future, preferred wrapper syntax would be:

### Windows Command Prompt / PowerShell

```text
gradlew.bat assembleDebug
```

or in PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

### macOS/Linux

```bash
./gradlew assembleDebug
```

Do not assume those wrapper files exist in the current repository; use the current documented local Gradle 8.9 setup unless the repository later adds a wrapper.

---

## 32. Environment checks before reporting a build bug

Include the output of these commands, after removing sensitive/private information:

```bash
git status
git rev-parse --short HEAD
java -version
gradle --version
adb version
adb devices
```

Meanings:

- `git status` — working tree and branch state.
- `git rev-parse --short HEAD` — abbreviated commit ID currently checked out.
- `java -version` — active Java runtime.
- `gradle --version` — Gradle/JVM/OS build environment.
- `adb version` — Android Debug Bridge version.
- `adb devices` — connected Android targets.

Never include keystore passwords, signing credentials, access tokens, personal device data, or private source data in bug reports.

---

## 33. Artifact security rules

Production release hygiene:

- never commit `.jks` or `.keystore` files;
- never commit signing passwords;
- never store raw secrets in `gradle.properties` if that file is tracked;
- use protected local/CI secret storage;
- do not upload unreviewed debug builds as production releases;
- verify the final signing certificate;
- preserve the production signing key securely;
- do not overwrite historical release tags;
- increase `versionCode` for new distributed versions;
- retain reproducible source/tag information for every release.

---

## 34. Quick command cheat sheet

### Clone

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
```

### Environment

```bash
java -version
gradle --version
adb version
```

### Unit tests

```bash
gradle testDebugUnitTest
```

### Lint

```bash
gradle lintDebug
```

### Debug APK

```bash
gradle assembleDebug
```

Output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Install debug build

```bash
gradle installDebug
```

or:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Android device/UI tests

```bash
gradle connectedDebugAndroidTest
```

### Release APK

```bash
gradle assembleRelease
```

### Release AAB

```bash
gradle bundleRelease
```

### Full local build set

```bash
gradle clean testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease
```

### Verify connected devices

```bash
adb devices
```

### Remove installed app

```bash
adb uninstall in.sanskar.spendcalc
```

### APK signature verification

```bash
apksigner verify --verbose --print-certs SpendCalc-1.0.0-release.apk
```

---

## 35. Final distinction: build, sign, install, publish

These words mean different stages:

**Build** converts source code and resources into Android package artifacts.

```bash
gradle assembleDebug
```

**Sign** attaches a trusted cryptographic application identity to a release artifact.

```bash
apksigner sign ...
```

**Install** places an APK on an Android device/emulator.

```bash
adb install ...
```

**Publish** distributes a reviewed, signed release to users, for example through an app store or approved release channel.

Do not treat a successful compilation alone as proof that a production release is ready. SpendCalc release readiness also requires tests, lint, device verification, signing verification, privacy/security review, version checks, and final functional testing.

---

## Related documentation

- [`setup.md`](setup.md) — initial workstation/project setup.
- [`command-reference.md`](command-reference.md) — detailed command dictionary.
- [`development.md`](development.md) — development architecture and quality workflow.
- [`testing.md`](testing.md) — testing strategy.
- [`release.md`](release.md) — release policy/checklist.
- [`troubleshooting.md`](troubleshooting.md) — common failures and diagnostics.
- [`../SECURITY.md`](../SECURITY.md) — security policy.
- [`../PRIVACY.md`](../PRIVACY.md) — privacy model.

**Made by the Sanskar**
