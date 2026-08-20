# SpendCalc Command Reference

This reference explains the commands used to download, inspect, build, test, package, install, sign, verify, and troubleshoot SpendCalc.

Current release-candidate metadata is `versionName = "2.0.12"` and `versionCode = 20012`. The application release number is independent from the Room database version and explicit backup schema version, which both remain at version `1` until their compatibility contracts actually change.

The project currently documents a local Gradle 8.9 installation because the repository does not commit a Gradle wrapper JAR. If a Gradle Wrapper is added later, use `gradlew.bat` on Windows or `./gradlew` on macOS/Linux instead of the global `gradle` command.

---

## Command syntax basics

A command generally has this structure:

```text
program subcommand arguments options
```

Example:

```bash
git clone https://github.com/sanskarIN/spendcalc.git
```

- `git` = program.
- `clone` = Git subcommand.
- repository URL = argument.

Another example:

```bash
gradle assembleDebug --stacktrace
```

- `gradle` = program.
- `assembleDebug` = Gradle task.
- `--stacktrace` = Gradle option.

---

## Git commands

### `git clone`

```bash
git clone https://github.com/sanskarIN/spendcalc.git
```

Downloads the repository, files, branches/history metadata, and creates a local `spendcalc` directory.

### `cd`

```bash
cd spendcalc
```

Changes the current terminal directory to the cloned repository.

### `git status`

```bash
git status
```

Shows:

- current branch;
- modified files;
- staged files;
- untracked files;
- whether the local branch differs from its upstream branch.

Run this before and after substantial work.

### `git diff`

```bash
git diff
```

Shows unstaged textual changes.

For one file:

```bash
git diff -- app/build.gradle.kts
```

The `--` explicitly separates Git revisions/options from a file path.

### `git diff --staged`

```bash
git diff --staged
```

Shows changes already added to the Git staging area.

### `git log`

```bash
git log --oneline -10
```

- `--oneline` prints compact commit entries.
- `-10` limits output to ten commits.

### `git rev-parse --short HEAD`

```bash
git rev-parse --short HEAD
```

Prints the abbreviated commit SHA currently checked out. Use it when recording exactly which source revision produced an APK/AAB.

### Configure commit email

```bash
git config user.email "sanskarin@outlook.in"
```

Sets the email for commits in this clone.

Check it:

```bash
git config user.email
```

### Configure commit name

```bash
git config user.name "Sanskar"
```

Sets the local repository commit display name.

### Stage files

```bash
git add README.md
```

Stages one file.

Stage selected paths:

```bash
git add docs/ README.md
```

Avoid blindly staging secrets or generated signing files.

### Commit

```bash
git commit -m "docs: improve Android build documentation"
```

Creates a Git commit from staged changes.

### Pull latest changes

```bash
git pull --ff-only
```

Downloads and integrates upstream changes only when the update can be performed as a fast-forward. This avoids silently creating a merge commit.

### Push

```bash
git push origin <branch-name>
```

Pushes the named local branch to the remote named `origin`. Replace `<branch-name>` with the branch you intentionally want to publish; do not assume `main` when working in a pull-request branch.

---

## Java/JDK commands

### Java runtime version

```bash
java -version
```

SpendCalc's documented build environment uses JDK 17.

### Java compiler version

```bash
javac -version
```

Confirms the active Java compiler.

### Java environment variable

#### Windows PowerShell

```powershell
$env:JAVA_HOME
```

#### Windows Command Prompt

```cmd
echo %JAVA_HOME%
```

#### macOS/Linux

```bash
echo "$JAVA_HOME"
```

`JAVA_HOME` should normally point to the intended JDK installation when command-line tools depend on it.

---

## Gradle environment commands

### Version and environment

```bash
gradle --version
```

Shows Gradle version, Kotlin/Groovy environment, JVM, Java home, and OS information.

### Gradle help

```bash
gradle help
```

Checks that Gradle can initialize the project and displays help.

### List tasks

```bash
gradle tasks
```

Shows commonly relevant tasks.

All tasks:

```bash
gradle tasks --all
```

### List projects/modules

```bash
gradle projects
```

SpendCalc currently contains the root project and `:app` Android application module.

### Stop Gradle daemons

```bash
gradle --stop
```

Stops running Gradle daemon processes owned by the current user. Useful when troubleshooting stale daemon/JDK state.

---

## Gradle build commands

### Clean generated outputs

```bash
gradle clean
```

Deletes module build outputs so the next build starts from a clean generated-output state.

### Debug APK

```bash
gradle assembleDebug
```

Compiles and packages the debug application.

Expected artifact:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Explicit app-module debug APK

```bash
gradle :app:assembleDebug
```

Same intent, but explicitly targets the `app` module.

### Release APK

```bash
gradle assembleRelease
```

Builds the optimized release APK variant. Production signing secrets are not stored in the repository, so default release output must not be assumed production-ready/signed.

### Release App Bundle

```bash
gradle bundleRelease
```

Builds the release Android App Bundle.

Expected directory:

```text
app/build/outputs/bundle/release/
```

### Debug install

```bash
gradle installDebug
```

Builds/installs the debug app on a connected compatible Android device/emulator.

### Run several tasks

```bash
gradle clean testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease
```

Gradle accepts multiple task names and executes the required task graph.

---

## Gradle testing commands

### JVM unit tests

```bash
gradle testDebugUnitTest
```

Runs local debug unit tests on the JVM.

### Android lint

```bash
gradle lintDebug
```

Runs Android static analysis for the debug variant.

### Connected Android tests

```bash
gradle connectedDebugAndroidTest
```

Runs Android instrumentation/UI tests on connected devices/emulators.

### Test plus lint

```bash
gradle testDebugUnitTest lintDebug
```

Runs both tasks in one Gradle invocation.

---

## Gradle diagnostic options

### Stack trace

```bash
gradle assembleDebug --stacktrace
```

Adds an exception stack trace to failed build output.

### Full stack trace

```bash
gradle assembleDebug --full-stacktrace
```

Prints a more complete stack trace.

### Info logging

```bash
gradle assembleDebug --info
```

Enables informational build logging.

### Debug logging

```bash
gradle assembleDebug --debug
```

Enables very verbose logging. Review logs before sharing publicly because local paths and environment information may appear.

### Refresh dependencies

```bash
gradle assembleDebug --refresh-dependencies
```

Requests a refresh of dependency resolution state. Use for dependency/cache diagnosis rather than every normal build.

### Offline mode

```bash
gradle assembleDebug --offline
```

Prevents network dependency resolution. Works only when all required dependencies/plugins are already cached.

### No daemon

```bash
gradle assembleDebug --no-daemon
```

Runs without a persistent Gradle daemon for that invocation. Useful for some CI or troubleshooting cases, though usually slower across repeated local builds.

---

## Dependency commands

### Entire app dependency report

```bash
gradle :app:dependencies
```

Prints dependency trees for app configurations.

### Debug runtime dependencies

```bash
gradle :app:dependencies --configuration debugRuntimeClasspath
```

Restricts output to the debug runtime classpath.

### Dependency insight

```bash
gradle :app:dependencyInsight --dependency kotlin --configuration debugRuntimeClasspath
```

Explains why a matching dependency/version appears in a configuration.

---

## Android Debug Bridge (`adb`) commands

`adb` is installed with Android SDK Platform-Tools.

### Version

```bash
adb version
```

Shows installed ADB version.

### Connected devices

```bash
adb devices
```

Lists visible physical devices and emulators.

More details:

```bash
adb devices -l
```

`-l` includes additional device metadata when available.

### Install APK

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Installs an APK on the selected/only connected target.

### Reinstall/update APK

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

`-r` requests reinstall while retaining data when package/signature rules permit.

### Uninstall SpendCalc

```bash
adb uninstall in.sanskar.spendcalc
```

Removes the application and its app-local data from the target device.

### Select a specific device

```bash
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
```

- `-s emulator-5554` selects a target by serial.
- remaining command runs only for that device.

### Logcat

```bash
adb logcat
```

Streams Android system/app log output.

Clear Logcat buffer:

```bash
adb logcat -c
```

### Package information

```bash
adb shell dumpsys package in.sanskar.spendcalc
```

Runs Android's package dump service and prints detailed installed package state. For the 2.0.12 release candidate, verify the installed package reports application versionName `2.0.12` and versionCode `20012` before release.

### Start application launcher activity indirectly

```bash
adb shell monkey -p in.sanskar.spendcalc 1
```

- `shell` runs an Android shell command.
- `monkey` is an Android event utility.
- `-p` limits it to the specified package.
- `1` requests one event, commonly enough to trigger a launcher start for a basic smoke launch.

### Force stop

```bash
adb shell am force-stop in.sanskar.spendcalc
```

Stops the running application process through Android Activity Manager.

### Clear app data

```bash
adb shell pm clear in.sanskar.spendcalc
```

Deletes app data and returns the installed package to a fresh-data state. Use only on test devices when that destructive reset is intended.

---

## File inspection commands

### Windows PowerShell

List debug artifact:

```powershell
Get-Item .\app\build\outputs\apk\debug\app-debug.apk
```

List release directory:

```powershell
Get-ChildItem .\app\build\outputs\apk\release\
```

Recursive build output search:

```powershell
Get-ChildItem .\app\build\outputs\ -Recurse
```

### Windows Command Prompt

```cmd
dir app\build\outputs\apk\debug\app-debug.apk
```

```cmd
dir app\build\outputs\apk\release\
```

### macOS/Linux

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

```bash
find app/build/outputs -maxdepth 4 -type f -print
```

`find` recursively finds files under the specified path. `-maxdepth 4` limits recursion depth; `-type f` selects files.

---

## APK inspection commands

### AAPT package metadata

When `aapt` is available:

```bash
aapt dump badging app/build/outputs/apk/debug/app-debug.apk
```

Can show package/version/SDK-related APK metadata.

### AAPT permissions

```bash
aapt dump permissions app/build/outputs/apk/debug/app-debug.apk
```

Shows permissions declared by the APK.

Depending on installed Android Build-Tools, `aapt2` or Android Studio's APK Analyzer may be preferable for some inspection tasks.

---

## `keytool` commands

### Generate release key pair

```bash
keytool -genkeypair -v -keystore spendcalc-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias spendcalc
```

Important options:

- `-genkeypair` = create key pair/certificate entry;
- `-v` = verbose output;
- `-keystore` = keystore path;
- `-keyalg RSA` = RSA key algorithm;
- `-keysize 2048` = 2048-bit RSA key;
- `-validity 10000` = certificate validity days;
- `-alias spendcalc` = key alias.

### List keystore

```bash
keytool -list -v -keystore spendcalc-release.jks
```

Displays certificate/key metadata without revealing the private key itself.

Never commit the keystore or expose passwords.

---

## `zipalign` commands

### Align unsigned APK

```bash
zipalign -v -p 4 app/build/outputs/apk/release/app-release-unsigned.apk SpendCalc-release-aligned.apk
```

- `-v` = verbose.
- `-p` = page-align uncompressed shared libraries when applicable.
- `4` = 4-byte alignment for other uncompressed data.

### Verify alignment

```bash
zipalign -c -v 4 SpendCalc-release-aligned.apk
```

- `-c` = check alignment rather than write output.

Run alignment before manual `apksigner` signing.

---

## `apksigner` commands

### Sign an APK

```bash
apksigner sign --ks spendcalc-release.jks --ks-key-alias spendcalc --out SpendCalc-2.0.12-release.apk SpendCalc-release-aligned.apk
```

- `sign` = signing operation.
- `--ks` = keystore path.
- `--ks-key-alias` = key alias.
- `--out` = output APK.
- final path = input APK.

Prefer password prompts or protected secret injection instead of putting passwords directly into shell history.

### Verify signed APK

```bash
apksigner verify --verbose --print-certs SpendCalc-2.0.12-release.apk
```

- `verify` = verify APK signature.
- `--verbose` = detailed verification.
- `--print-certs` = print signing certificate details.

---

## `jarsigner` commands for AAB

### Sign bundle

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore spendcalc-release.jks app/build/outputs/bundle/release/app-release.aab spendcalc
```

- `-sigalg` = signature algorithm.
- `-digestalg` = digest algorithm.
- `-keystore` = keystore.
- AAB path = bundle to sign.
- `spendcalc` = key alias.

### Verify bundle signature

```bash
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

Checks archive signatures and certificate data.

---

## Project utility commands

The repository includes Python-based quality scripts. Run them from the repository root.

On Windows where Python is exposed as `python` instead of `python3`, replace `python3` with `python` in the examples below.

### Formatting / text-hygiene guard

```bash
python3 scripts/check_format.py
```

Checks repository text files for UTF-8/format hygiene such as final newlines, trailing whitespace, and tab-policy violations.

### Kotlin namespace guard

```bash
python3 scripts/check_kotlin_namespace.py
```

Checks Kotlin package/namespace declarations for invalid or reserved namespace regressions before Android compilation.

### Tracked-file documentation coverage

```bash
python3 scripts/check_documentation_coverage.py
```

Compares the marked file inventory in `docs/codebase-reference.md` with `git ls-files`. It fails if a tracked file is undocumented, documented more than once, or still listed after deletion/rename.

### Android string-resource audit

```bash
python3 scripts/check_android_resources.py
```

Parses default Android string resources and scans Kotlin/XML usage for missing references and duplicate default string names.

### Android local-first/security audit

```bash
python3 scripts/check_android_security.py
```

Checks manifest/FileProvider/local-first security invariants, including the intentional absence of a core Android Internet permission and the restricted cache export provider configuration.

### Repository/documentation/link audit

```bash
python3 scripts/check_repository.py
```

Checks required repository/release documentation, local Markdown links, and required project identity/contact information.

### Secret-pattern scan

```bash
python3 scripts/scan_secrets.py
```

Scans repository content for conservative common secret/token/signing-material patterns. It supplements, but does not replace, secure secret management and human review.

### Run all fast repository guards

#### macOS/Linux shells

```bash
python3 scripts/check_format.py && \
python3 scripts/check_kotlin_namespace.py && \
python3 scripts/check_documentation_coverage.py && \
python3 scripts/check_android_resources.py && \
python3 scripts/check_android_security.py && \
python3 scripts/check_repository.py && \
python3 scripts/scan_secrets.py
```

`&&` means the next command runs only if the previous command succeeded. The backslash continues the shell command onto the next line.

#### Windows PowerShell

```powershell
python scripts/check_format.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
python scripts/check_kotlin_namespace.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
python scripts/check_documentation_coverage.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
python scripts/check_android_resources.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
python scripts/check_android_security.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
python scripts/check_repository.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
python scripts/scan_secrets.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
```

`$LASTEXITCODE` is PowerShell's exit code from the last native command. The checks stop immediately when a guard fails.

---

## Recommended command groups

### Fresh clone verification

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
java -version
gradle --version
python3 scripts/check_format.py
python3 scripts/check_documentation_coverage.py
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
```

### Before committing a code change

```bash
git status
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
git diff
```

### With an Android device/emulator

```bash
adb devices
gradle connectedDebugAndroidTest
gradle installDebug
```

### Release-candidate build

```bash
git status
git rev-parse --short HEAD
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
gradle clean testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease
gradle connectedDebugAndroidTest
```

Then securely sign/verify the intended production artifact, complete real-device/accessibility/export/backup/offline checks, capture genuine screenshots, and satisfy `docs/verification.md` for the exact source head.

---

## Commands that should not be confused

### `assembleDebug` vs `installDebug`

```bash
gradle assembleDebug
```

Builds the debug APK.

```bash
gradle installDebug
```

Builds if needed and installs it on a connected Android target.

### `assembleRelease` vs `bundleRelease`

```bash
gradle assembleRelease
```

Builds release APK output.

```bash
gradle bundleRelease
```

Builds release AAB output.

### `testDebugUnitTest` vs `connectedDebugAndroidTest`

```bash
gradle testDebugUnitTest
```

Runs local JVM tests.

```bash
gradle connectedDebugAndroidTest
```

Runs Android instrumentation/UI tests on actual Android runtime targets.

### `clean` vs deleting source files

```bash
gradle clean
```

Deletes generated build outputs. It does not intentionally delete project source code.

---

## Exit codes

Most command-line tools return an exit code to the shell:

- `0` usually means success.
- non-zero usually means failure or another non-success condition.

In CI, a failed build/test/lint/guard command normally stops the job because its process exits non-zero.

---

## Security note for terminal commands

Do not put these values directly in commands that may be saved in shell history or CI logs:

- keystore passwords;
- key passwords;
- GitHub tokens;
- API tokens;
- private keys;
- credentials;
- personal data.

Prefer secure prompts, environment-specific secret stores, or protected CI secret injection. The SpendCalc repository intentionally keeps production signing material out of Git.

---

## Related guides

- [`README.md`](README.md) — documentation index and reading paths.
- [`android-build-guide.md`](android-build-guide.md) — full APK/AAB/sign/install workflow.
- [`setup.md`](setup.md) — environment setup.
- [`development.md`](development.md) — development practices.
- [`testing.md`](testing.md) — testing approach.
- [`verification.md`](verification.md) — authoritative release-candidate gates.
- [`release.md`](release.md) — release process.
- [`troubleshooting.md`](troubleshooting.md) — failure diagnosis.

**Made by the Sanskar**
