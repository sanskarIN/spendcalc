# Setup

## Prerequisites

- Git
- JDK 17
- Android Studio with Android SDK Platform 35
- Android SDK Build-Tools compatible with API 35
- Gradle 8.9 for command-line builds

The app's minimum Android SDK is 26 and target/compile SDK is 35.

## Gradle model

This repository currently **does not commit a Gradle wrapper** (`gradlew`, `gradlew.bat`, or `gradle/wrapper/*`). That is intentional repository state, not a missing-file setup mistake.

Command examples therefore use a locally available `gradle` executable. GitHub Actions pins Gradle 8.9 with `gradle/actions/setup-gradle`. Android Studio may manage the compatible Gradle environment during sync, but command-line contributors should install/use Gradle 8.9 explicitly so local behavior matches CI as closely as practical.

If a Gradle wrapper is introduced later, update this guide, CI/development/troubleshooting documentation, and [`codebase-reference.md`](codebase-reference.md) for every newly tracked wrapper file.

## Clone

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
```

Optionally configure the requested local Git email for commits:

```bash
git config user.email "sanskarin@outlook.in"
```

Verify the key tools:

```bash
git --version
java -version
gradle --version
```

The Gradle JVM should resolve to JDK 17 for this project.

## Android SDK location

Android Studio normally writes `local.properties` automatically. If using command-line Gradle, create a local-only `local.properties` that points to your SDK. Do not commit that file.

Windows example:

```properties
sdk.dir=C:\\Users\\YOUR_USER\\AppData\\Local\\Android\\Sdk
```

Linux example:

```properties
sdk.dir=/home/YOUR_USER/Android/Sdk
```

macOS example:

```properties
sdk.dir=/Users/YOUR_USER/Library/Android/sdk
```

Ensure Android SDK Platform 35 and compatible Build-Tools are installed. CI explicitly installs `platforms;android-35` and `build-tools;35.0.0`.

## First local verification

Before an Android build, the fast repository guards can catch many setup/source problems without an emulator:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

`check_documentation_coverage.py` invokes Git and therefore must run inside a Git checkout. It verifies that every tracked file appears exactly once in [`codebase-reference.md`](codebase-reference.md).

## Build

With locally installed Gradle 8.9:

```bash
gradle assembleDebug
```

Run JVM unit tests and full Android lint:

```bash
gradle testDebugUnitTest
gradle lint
```

Compile the instrumentation suite even when no emulator is available:

```bash
gradle assembleDebugAndroidTest
```

Compile the release configuration:

```bash
gradle assembleRelease
```

## Run

1. Open the repository in Android Studio.
2. Configure JDK 17 for Gradle if Android Studio did not select it automatically.
3. Allow Gradle sync to complete.
4. Select an Android API 26+ emulator or connected device.
5. Run the `app` configuration.

SpendCalc needs no API key, account, remote backend, or Android Internet permission for core operation. `.env.example` intentionally contains documentation comments rather than required credentials.

## Instrumentation tests

With an emulator/device available:

```bash
gradle connectedDebugAndroidTest
```

This is a manual/runtime release gate in addition to CI's compile-only `assembleDebugAndroidTest` step.

## Release build

The repository does not contain production signing secrets or a keystore. A local unsigned/shrunk release configuration can be compiled with:

```bash
gradle assembleRelease
```

A distributable artifact must be signed with protected credentials supplied outside source control and must come from the exact commit that satisfied the documented release gates. Follow [`release.md`](release.md) and [`verification.md`](verification.md).

## Documentation orientation

After setup:

- [`development.md`](development.md) — daily change rules and commands;
- [`architecture.md`](architecture.md) — layer/dependency design;
- [`codebase-reference.md`](codebase-reference.md) — purpose of every tracked file;
- [`documentation-map.md`](documentation-map.md) — which documentation is authoritative for each topic;
- [`testing.md`](testing.md) — what each test/guard layer proves.

## Common setup failures

See [`troubleshooting.md`](troubleshooting.md) for JDK, SDK, Gradle, Room/KSP, repository-guard, emulator, export, and release diagnostics.
