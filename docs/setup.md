# Setup

## Prerequisites

- Git
- JDK 17
- Android Studio with Android SDK Platform 35
- Android SDK Build-Tools compatible with API 35
- Gradle 8.9 when the Gradle wrapper is not being used

The app's minimum Android SDK is 26 and target/compile SDK is 35.

## Clone

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
```

Optionally configure the requested local Git email for commits:

```bash
git config user.email "sanskarin@outlook.in"
```

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

## Build

With a locally installed Gradle 8.9:

```bash
gradle assembleDebug
```

Run unit tests and lint:

```bash
gradle testDebugUnitTest lintDebug
```

## Run

1. Open the repository in Android Studio.
2. Allow Gradle sync to complete.
3. Select an Android API 26+ emulator or connected device.
4. Run the `app` configuration.

SpendCalc needs no API key, account, remote backend, or Android Internet permission for core operation.

## Instrumentation tests

With an emulator/device available:

```bash
gradle connectedDebugAndroidTest
```

## Release build

The repository does not contain signing secrets. A local unsigned/shrunk release build can be compiled with:

```bash
gradle assembleRelease
```

For distributable signing, configure a keystore outside the repository and follow `docs/release.md`.

## Common setup failures

See [`troubleshooting.md`](troubleshooting.md) for JDK, SDK, Gradle, Room/KSP, and emulator diagnostics.
