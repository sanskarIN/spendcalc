# SpendCalc Documentation

This directory is the complete technical documentation set for SpendCalc.

SpendCalc is an Android-first, Kotlin + Jetpack Compose expense calculator with precision-safe `BigDecimal` finance arithmetic, local Room/DataStore persistence, offline core functionality, receipt export, testing, CI, privacy/security guidance, and release engineering documentation.

## Start here

Choose the path that matches what you want to do.

### I only want to build/install the Android app

1. [`setup.md`](setup.md) — install/verify the required development environment.
2. [`android-build-guide.md`](android-build-guide.md) — build APK/AAB files, find outputs, install with ADB, sign release artifacts, and verify them.
3. [`command-reference.md`](command-reference.md) — understand every important command and flag used in the project.
4. [`troubleshooting.md`](troubleshooting.md) — diagnose common failures.

### I want to contribute code

1. [`setup.md`](setup.md)
2. [`architecture.md`](architecture.md)
3. [`development.md`](development.md)
4. [`testing.md`](testing.md)
5. [`command-reference.md`](command-reference.md)
6. [`../CONTRIBUTING.md`](../CONTRIBUTING.md)

### I want to prepare a release

1. [`release.md`](release.md)
2. [`android-build-guide.md`](android-build-guide.md)
3. [`testing.md`](testing.md)
4. [`accessibility.md`](accessibility.md)
5. [`performance.md`](performance.md)
6. [`../SECURITY.md`](../SECURITY.md)
7. [`../PRIVACY.md`](../PRIVACY.md)
8. [`../CHANGELOG.md`](../CHANGELOG.md)

## Documentation map

| Document | Purpose |
| --- | --- |
| [`setup.md`](setup.md) | Workstation prerequisites, Android SDK/JDK/Gradle setup, cloning, first build, platform-specific paths. |
| [`android-build-guide.md`](android-build-guide.md) | Complete APK/AAB/executable workflow including build variants, outputs, installation, signing, verification, and release-candidate command sequences. |
| [`command-reference.md`](command-reference.md) | Git, Java, Gradle, ADB, keytool, zipalign, apksigner, jarsigner, Python quality scripts, options, meanings, and examples. |
| [`architecture.md`](architecture.md) | Application architecture, domain/data/UI/platform boundaries, persistence, export design, finance rules. |
| [`development.md`](development.md) | Coding practices, project layout, finance/UI/persistence/export rules, quality workflow. |
| [`testing.md`](testing.md) | Unit, instrumentation, Compose, Room, export/security test strategy and commands. |
| [`release.md`](release.md) | Versioning, release gates, build/sign/publish process, artifacts, rollback. |
| [`troubleshooting.md`](troubleshooting.md) | JDK, SDK, Gradle, dependency, KSP/Room, emulator/device, APK/install, signing, and build diagnosis. |
| [`accessibility.md`](accessibility.md) | Accessibility expectations and release checks. |
| [`performance.md`](performance.md) | Performance philosophy, profiling, regression considerations. |
| [`adr/`](adr/) | Architecture Decision Records documenting important design choices. |
| [`assets/`](assets/) | Brand artwork and verified screenshot-capture guidance. |

## Important root-level documents

| Document | Purpose |
| --- | --- |
| [`../README.md`](../README.md) | Main product/repository overview and quick start. |
| [`../CONTRIBUTING.md`](../CONTRIBUTING.md) | Contributor workflow and expectations. |
| [`../SECURITY.md`](../SECURITY.md) | Vulnerability reporting and security policy. |
| [`../PRIVACY.md`](../PRIVACY.md) | Runtime data/privacy behavior. |
| [`../SUPPORT.md`](../SUPPORT.md) | Support routes and issue guidance. |
| [`../CHANGELOG.md`](../CHANGELOG.md) | User-visible version changes. |
| [`../ROADMAP.md`](../ROADMAP.md) | Planned direction and future work. |
| [`../what_changed.md`](../what_changed.md) | Multi-session engineering handoff and verification state. |

## Build outputs at a glance

Debug APK:

```bash
gradle assembleDebug
```

Expected file:

```text
app/build/outputs/apk/debug/app-debug.apk
```

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

Full explanations are in [`android-build-guide.md`](android-build-guide.md).

## Current technical baseline

- Language: Kotlin
- UI: Jetpack Compose + Material 3
- Android min SDK: 26
- Android target/compile SDK: 35
- JVM/Java target: 17
- Android Gradle Plugin: 8.7.3
- Kotlin: 2.0.21
- KSP: 2.0.21-1.0.28
- Documented local Gradle: 8.9
- Database: Room
- Preferences: DataStore
- Async/state: Kotlin Coroutines + Flow
- Testing: JUnit, AndroidX Test, Espresso, Compose UI Test
- Distribution formats: APK and AAB
- License: MIT

## Documentation maintenance rule

When changing a command, build version, Android SDK target, output path, signing process, architecture rule, or release behavior, update the relevant documentation in the same change whenever practical.

Documentation examples must never include real signing passwords, tokens, private keys, production secrets, or sensitive user data.

**Made by the Sanskar**
