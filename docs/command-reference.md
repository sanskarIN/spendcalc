# SpendCalc Command Reference

This reference explains the commands used to download, inspect, build, test, package, install, sign, verify, troubleshoot, and release SpendCalc.

Current release-candidate metadata:

```text
versionName = "2.15.4"
versionCode = 21504
```

The application release number is independent from the Room database version and explicit backup schema version, which both remain at version `1` until their compatibility contracts actually change.

The repository documents Gradle 8.9 through the global `gradle` command because a Gradle wrapper JAR is intentionally not committed at present.

---

## 1. Command syntax basics

Typical shape:

```text
program subcommand arguments options
```

Example:

```bash
git clone https://github.com/sanskarIN/spendcalc.git
```

- `git` is the program.
- `clone` is the subcommand.
- the repository URL is the argument.

Another example:

```bash
gradle assembleDebug --stacktrace
```

- `gradle` starts Gradle.
- `assembleDebug` is the task.
- `--stacktrace` is an option.

---

## 2. Git commands

### Clone

```bash
git clone https://github.com/sanskarIN/spendcalc.git
```

Copies the repository to a local `spendcalc` directory.

### Enter the repository

```bash
cd spendcalc
```

### Repository status

```bash
git status
```

Shows the current branch and modified/staged/untracked files.

Compact status:

```bash
git status --short
```

### View changes

```bash
git diff
```

One file:

```bash
git diff -- app/build.gradle.kts
```

Staged changes:

```bash
git diff --staged
```

### Recent history

```bash
git log --oneline -10
```

### Exact source SHA

```bash
git rev-parse HEAD
```

Short SHA:

```bash
git rev-parse --short HEAD
```

Record the exact SHA that produces a release artifact.

### Configure repository-local identity

```bash
git config user.name "Sanskar"
git config user.email "sanskarin@outlook.in"
```

Read it back:

```bash
git config user.name
git config user.email
```

### Stage

```bash
git add app/build.gradle.kts
```

### Commit

```bash
git commit -m "release: prepare SpendCalc 2.15.4"
```

### Pull without accidental merge commits

```bash
git pull --ff-only
```

### Push a branch

```bash
git push origin <branch-name>
```

Do not assume `main` when working on a release-candidate pull request.

---

## 3. Java/JDK commands

### Runtime version

```bash
java -version
```

### Compiler version

```bash
javac -version
```

SpendCalc's documented build environment uses JDK 17.

### `JAVA_HOME`

Windows PowerShell:

```powershell
$env:JAVA_HOME
```

Windows Command Prompt:

```cmd
echo %JAVA_HOME%
```

macOS/Linux:

```bash
echo "$JAVA_HOME"
```

---

## 4. Gradle environment commands

### Version/environment

```bash
gradle --version
```

Shows Gradle, JVM, Java home, Kotlin/Groovy environment, and operating system information.

### Help

```bash
gradle help
```

### Common tasks

```bash
gradle tasks
```

All tasks:

```bash
gradle tasks --all
```

### Project/module list

```bash
gradle projects
```

SpendCalc currently has the `:app` Android application module.

### Stop daemons

```bash
gradle --stop
```

Useful when troubleshooting stale Gradle/JDK state.

---

## 5. Gradle build commands

### Clean

```bash
gradle --no-daemon clean
```

Deletes generated build output.

### Debug APK

```bash
gradle --no-daemon assembleDebug
```

Expected artifact:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Explicit module form:

```bash
gradle --no-daemon :app:assembleDebug
```

### Release APK

```bash
gradle --no-daemon assembleRelease
```

Expected directory:

```text
app/build/outputs/apk/release/
```

Default release output is not production-ready merely because it compiles; production signing material is intentionally external to Git.

### Release AAB

```bash
gradle --no-daemon bundleRelease
```

Expected directory:

```text
app/build/outputs/bundle/release/
```

### Install debug build

```bash
gradle --no-daemon installDebug
```

### Compile instrumentation tests

```bash
gradle --no-daemon assembleDebugAndroidTest
```

Builds the instrumentation test APK but does not run it.

---

## 6. Test and lint commands

### JVM tests

```bash
gradle --no-daemon testDebugUnitTest
```

### Full Android lint

```bash
gradle --no-daemon lint
```

### Debug lint

```bash
gradle --no-daemon lintDebug
```

### Connected Android instrumentation tests

```bash
gradle --no-daemon connectedDebugAndroidTest
```

Requires an attached device/emulator.

### High-confidence local sequence

```bash
gradle --no-daemon clean testDebugUnitTest assembleDebugAndroidTest lint assembleDebug assembleRelease bundleRelease
```

Run connected tests separately because they require a runtime target.

---

## 7. Gradle diagnostic options

### Stack trace

```bash
gradle assembleDebug --stacktrace
```

### Full stack trace

```bash
gradle assembleDebug --full-stacktrace
```

### Informational logging

```bash
gradle assembleDebug --info
```

### Debug logging

```bash
gradle assembleDebug --debug
```

Review verbose logs before sharing them publicly because they may expose local paths/environment details.

### Refresh dependency metadata/artifacts

```bash
gradle assembleDebug --refresh-dependencies
```

Use for dependency/cache diagnosis rather than every build.

### Offline mode

```bash
gradle assembleDebug --offline
```

Works only when all required dependencies/plugins are already cached.

### Disable persistent daemon for one run

```bash
gradle assembleDebug --no-daemon
```

CI uses this style for reproducibility/isolation.

---

## 8. Dependency inspection

### Dependency tree

```bash
gradle :app:dependencies
```

### Debug runtime classpath

```bash
gradle :app:dependencies --configuration debugRuntimeClasspath
```

### Explain a resolved dependency

```bash
gradle :app:dependencyInsight --dependency kotlin --configuration debugRuntimeClasspath
```

Use dependency reports before accepting major dependency upgrades, especially AGP/Kotlin/KSP/Room/Compose changes.

---

## 9. Android Debug Bridge (`adb`)

### Version

```bash
adb version
```

### Devices

```bash
adb devices
```

More detail:

```bash
adb devices -l
```

### Install APK

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Reinstall/update while preserving data when Android permits

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Select a device

```bash
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
```

### Uninstall SpendCalc

```bash
adb uninstall in.sanskar.spendcalc
```

### Start launcher smoke flow

```bash
adb shell monkey -p in.sanskar.spendcalc 1
```

### Force stop

```bash
adb shell am force-stop in.sanskar.spendcalc
```

### Clear app data on a test target

```bash
adb shell pm clear in.sanskar.spendcalc
```

### Package metadata

```bash
adb shell dumpsys package in.sanskar.spendcalc
```

For the 2.15.4 candidate, the installed package must report `versionName 2.15.4` and `versionCode 21504` before release.

### Logcat

```bash
adb logcat
```

Clear Logcat:

```bash
adb logcat -c
```

Never publish logs that contain private user data or secrets.

### Restart ADB server

```bash
adb kill-server
adb start-server
adb devices -l
```

Useful for offline/unauthorized device troubleshooting.

---

## 10. Artifact file inspection

### Windows PowerShell

```powershell
Get-Item .\app\build\outputs\apk\debug\app-debug.apk
Get-ChildItem .\app\build\outputs\apk\release\
Get-ChildItem .\app\build\outputs\bundle\release\
```

Recursive output search:

```powershell
Get-ChildItem .\app\build\outputs\ -Recurse
```

### Windows Command Prompt

```cmd
dir app\build\outputs\apk\debug\app-debug.apk
dir app\build\outputs\apk\release\
```

### macOS/Linux

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
find app/build/outputs -maxdepth 5 -type f -print
```

---

## 11. APK metadata inspection

When Android Build-Tools expose `aapt`:

```bash
aapt dump badging app/build/outputs/apk/debug/app-debug.apk
```

For 2.15.4 verify:

```text
applicationId/package = in.sanskar.spendcalc
versionName = 2.15.4
versionCode = 21504
minSdk = 26
targetSdk = 35
```

Inspect permissions:

```bash
aapt dump permissions app/build/outputs/apk/debug/app-debug.apk
```

The local-first core must not silently gain an Internet permission.

---

## 12. `keytool`

### Generate a signing key

```bash
keytool -genkeypair -v -keystore spendcalc-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias spendcalc
```

Options:

- `-genkeypair`: create key pair/certificate entry;
- `-keystore`: keystore path;
- `-keyalg RSA`: algorithm;
- `-keysize 2048`: key size;
- `-validity 10000`: certificate validity in days;
- `-alias spendcalc`: entry alias.

### Inspect keystore metadata

```bash
keytool -list -v -keystore spendcalc-release.jks
```

Never commit the keystore or passwords.

---

## 13. `zipalign`

Align an unsigned release APK:

```bash
zipalign -v -p 4 app/build/outputs/apk/release/app-release-unsigned.apk SpendCalc-release-aligned.apk
```

Verify:

```bash
zipalign -c -v 4 SpendCalc-release-aligned.apk
```

Run alignment before manual APK signing.

---

## 14. `apksigner`

Sign the current candidate:

```bash
apksigner sign --ks spendcalc-release.jks --ks-key-alias spendcalc --out SpendCalc-2.15.4-release.apk SpendCalc-release-aligned.apk
```

Verify signature/certificate:

```bash
apksigner verify --verbose --print-certs SpendCalc-2.15.4-release.apk
```

Install signed artifact:

```bash
adb install SpendCalc-2.15.4-release.apk
```

Upgrade with matching signing identity:

```bash
adb install -r SpendCalc-2.15.4-release.apk
```

Prefer secure prompts/secret stores over passwords embedded in command history.

---

## 15. `jarsigner` for AAB

Sign a bundle when required by the chosen distribution workflow:

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore spendcalc-release.jks app/build/outputs/bundle/release/app-release.aab spendcalc
```

Verify:

```bash
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

For Google Play, follow the current Play App Signing/upload-key workflow.

---

## 16. SHA-256 checksum commands

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

Record the checksum together with the exact Git SHA.

---

## 17. Repository quality scripts

Run these from the repository root.

### Formatting/text hygiene

```bash
python3 scripts/check_format.py
```

Checks UTF-8/text formatting rules including final newline/trailing whitespace/tab policy.

### Kotlin namespace/package guard

```bash
python3 scripts/check_kotlin_namespace.py
```

Detects namespace/package regressions.

### Tracked-file documentation coverage

```bash
python3 scripts/check_documentation_coverage.py
```

Verifies every `git ls-files` path is documented exactly once in `docs/codebase-reference.md` and rejects stale entries.

### Android resource audit

```bash
python3 scripts/check_android_resources.py
```

Checks Android default string-resource names/references and duplicate/resource integrity rules implemented by the repository guard.

### Android local-first security policy

```bash
python3 scripts/check_android_security.py
```

Guards manifest/FileProvider/local-first security assumptions such as no unintended Internet permission and constrained export sharing.

### Repository metadata/link/release audit

```bash
python3 scripts/check_repository.py
```

Checks:

- required files;
- README identity/support/funding/license markers;
- current `versionName`/`versionCode` alignment between `app/build.gradle.kts`, `docs/README.md`, this command reference, and the Android build guide;
- stale semantic-versioned signed-APK examples;
- local Markdown links.

For the current branch it must recognize `2.15.4` and `versionCode = 21504`.

### Common secret-pattern scan

```bash
python3 scripts/scan_secrets.py
```

Conservative repository scan for common credential/secret patterns. It does not replace proper secret management or GitHub secret scanning.

---

## 18. Run all repository guards

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

Do this before expensive Android compilation so cheap structural failures are detected early.

---

## 19. GitHub Actions verification model

For the exact final release candidate, require successful conclusions from:

- CI;
- CodeQL;
- Dependency Review;
- Repository Audit;
- Android Instrumentation.

Each new commit creates a new exact head. An older successful run is diagnostic evidence, not release proof for a newer head.

CI is expected to cover repository guards, JVM tests, instrumentation-test compilation, Android lint, debug build, and release compilation.

Android Instrumentation must execute the connected Android test suite on the configured emulator, not merely compile it.

---

## 20. GitHub workflow maintenance commands

Most workflow maintenance occurs through GitHub's UI/API rather than a local command, but locally you should inspect workflow changes before committing:

```bash
git diff -- .github/workflows/
```

Do not accept major action upgrades blindly. Review runner requirements, Node/runtime changes, licensing/terms, cache behavior, and permissions.

---

## 21. Version preparation

Current values:

```kotlin
versionCode = 21504
versionName = "2.15.4"
```

When preparing a later version:

1. choose a `versionCode` greater than `21504`;
2. update `app/build.gradle.kts`;
3. update `docs/README.md`;
4. update `docs/android-build-guide.md`;
5. update `docs/command-reference.md`;
6. update release/checklist/changelog/roadmap continuity documents as applicable;
7. run `python3 scripts/check_repository.py`;
8. rerun all exact-head workflows.

Do not modify Room/backup schema versions merely because the app version changes.

---

## 22. Fresh-install command flow

```bash
adb uninstall in.sanskar.spendcalc || true
gradle --no-daemon assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p in.sanskar.spendcalc 1
```

On Windows Command Prompt/PowerShell, omit or adapt `|| true` because shell syntax differs.

Use fresh installs to verify splash/onboarding/initial-state behavior.

---

## 23. Production candidate command flow

After exact-head automated/manual verification:

```bash
gradle --no-daemon clean testDebugUnitTest assembleDebugAndroidTest lint assembleRelease bundleRelease
zipalign -v -p 4 app/build/outputs/apk/release/app-release-unsigned.apk SpendCalc-release-aligned.apk
apksigner sign --ks spendcalc-release.jks --ks-key-alias spendcalc --out SpendCalc-2.15.4-release.apk SpendCalc-release-aligned.apk
apksigner verify --verbose --print-certs SpendCalc-2.15.4-release.apk
adb install SpendCalc-2.15.4-release.apk
```

Then inspect package metadata and record the artifact checksum/source SHA before tagging/publishing.

---

## 24. Useful failure diagnostics

### Build fails unexpectedly

```bash
gradle assembleDebug --stacktrace --info
```

### Dependency resolution looks wrong

```bash
gradle :app:dependencies --configuration debugRuntimeClasspath
gradle :app:dependencyInsight --dependency <name> --configuration debugRuntimeClasspath
```

### Emulator/device offline

```bash
adb kill-server
adb start-server
adb devices -l
```

### Connected test failure report

Open:

```text
app/build/reports/androidTests/connected/debug/index.html
```

### Repository version/docs drift

```bash
python3 scripts/check_repository.py
```

### Documentation file inventory drift

```bash
python3 scripts/check_documentation_coverage.py
```

---

## 25. Release rule

Build success alone is not a release.

For `v2.15.4`, require the exact source commit to pass repository guards, JVM tests, Android lint, debug/release compilation, connected Android tests, CodeQL, dependency review, repository audit, manual Android/accessibility/export/backup/offline/layout checks, real screenshot review, production signing verification, signed-artifact installation, metadata inspection, and checksum/source-SHA recording.

Only then create/publish the `v2.15.4` tag/release.

**Made by the Sanskar**
