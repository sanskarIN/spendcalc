# SpendCalc Documentation

This directory is the technical documentation index for SpendCalc.

SpendCalc is an Android-first, Kotlin + Jetpack Compose expense calculator with precision-safe `BigDecimal` finance arithmetic, local Room/DataStore persistence, offline core functionality, receipt export, testing, CI, privacy/security guidance, and release engineering documentation.

Current application release candidate: **2.0.12** (`versionCode = 20012`). Room database version and explicit backup schema version both remain **1** because compatibility schema versions are independent from the application release number.

## Start here

Choose the path that matches what you want to do.

### I only want to build/install the Android app

1. [`setup.md`](setup.md) — install and verify the required development environment.
2. [`android-build-guide.md`](android-build-guide.md) — build APK/AAB files, find outputs, install with ADB, sign release artifacts, inspect versions, and verify them.
3. [`command-reference.md`](command-reference.md) — understand the important commands, options, and repository guard scripts used by the project.
4. [`troubleshooting.md`](troubleshooting.md) — diagnose common failures.

### I want to contribute code

1. [`setup.md`](setup.md)
2. [`architecture.md`](architecture.md)
3. [`development.md`](development.md)
4. [`testing.md`](testing.md)
5. [`command-reference.md`](command-reference.md)
6. [`documentation-map.md`](documentation-map.md)
7. [`codebase-reference.md`](codebase-reference.md)
8. [`../CONTRIBUTING.md`](../CONTRIBUTING.md)

### I want to understand storage, backup, privacy, or security

1. [`persistence-invariants.md`](persistence-invariants.md) — stored-record contract shared by repositories and backups.
2. [`backup-restore.md`](backup-restore.md) — user/developer backup and restore behavior.
3. [`security-backup.md`](security-backup.md) — parser limits, validation, corruption/integrity model, and threat boundaries.
4. [`privacy-backup.md`](privacy-backup.md) — explicit backup versus Android-managed backup/device transfer privacy.
5. [`logging.md`](logging.md) — safe logging and redaction rules.
6. [`../PRIVACY.md`](../PRIVACY.md)
7. [`../SECURITY.md`](../SECURITY.md)

### I want to prepare or verify a release

1. [`verification.md`](verification.md) — authoritative blocking automated/manual/distribution checklist.
2. [`release.md`](release.md) — exact-commit release, signing, artifact, screenshot, and publication workflow.
3. [`release-candidate-final-audit.md`](release-candidate-final-audit.md) — source-level 2.0.12 completeness audit.
4. [`android-build-guide.md`](android-build-guide.md) — APK/AAB/build/install/signing details.
5. [`command-reference.md`](command-reference.md) — command meanings and verification commands.
6. [`testing.md`](testing.md) — automated and connected-device verification strategy.
7. [`accessibility.md`](accessibility.md) — accessibility implementation and manual checks.
8. [`performance.md`](performance.md) — performance/bounded-work expectations.
9. [`../CHANGELOG.md`](../CHANGELOG.md)
10. [`../ROADMAP.md`](../ROADMAP.md)
11. [`../what_changed.md`](../what_changed.md) — exact current continuation state.

## Documentation map

| Document | Purpose |
| --- | --- |
| [`setup.md`](setup.md) | Workstation prerequisites, Android SDK/JDK/Gradle setup, cloning, first build, and platform-specific paths. |
| [`android-build-guide.md`](android-build-guide.md) | Complete APK/AAB/executable workflow: variants, outputs, installation, version inspection, signing, verification, and release-candidate commands. |
| [`command-reference.md`](command-reference.md) | Git, Java, Gradle, ADB, keytool, zipalign, apksigner, jarsigner, every repository quality script, options, meanings, and examples. |
| [`architecture.md`](architecture.md) | Application architecture, domain/data/UI/platform boundaries, persistence, export design, and finance rules. |
| [`development.md`](development.md) | Coding practices, project layout, finance/UI/persistence/export/logging rules, dependency expectations, and contributor quality workflow. |
| [`documentation-map.md`](documentation-map.md) | Which document is authoritative for each topic, update triggers, and anti-drift/anti-duplication rules. |
| [`codebase-reference.md`](codebase-reference.md) | Exhaustive tracked-file ownership inventory enforced against `git ls-files`. |
| [`features.md`](features.md) | Implemented product behavior and user-visible limits/workflows. |
| [`testing.md`](testing.md) | Unit, deterministic fuzz, instrumentation, Compose, Room, export/security test strategy and commands. |
| [`verification.md`](verification.md) | Exact release-candidate automated, Android runtime, accessibility, security/privacy, signing, screenshot, and artifact gates. |
| [`release.md`](release.md) | Versioning, exact-source release workflow, unsigned/signed artifacts, screenshots, tag/publication sequence, and rollback considerations. |
| [`release-candidate-final-audit.md`](release-candidate-final-audit.md) | Source-level audit for 2.0.12; not a substitute for pending runtime/release evidence. |
| [`troubleshooting.md`](troubleshooting.md) | JDK, SDK, Gradle, dependency, KSP/Room, emulator/device, APK/install, signing, export, and release diagnosis. |
| [`accessibility.md`](accessibility.md) | Accessibility implementation expectations plus TalkBack/font-scale/motion/contrast/touch/layout release checks. |
| [`design-system.md`](design-system.md) | Material/Compose design tokens, layout conventions, typography/theme behavior, and UI consistency rules. |
| [`performance.md`](performance.md) | Bounded-work choices, threading expectations, measurement policy, and performance review guidance. |
| [`persistence-invariants.md`](persistence-invariants.md) | Shared saved-record rules required for repository/backup compatibility. |
| [`backup-restore.md`](backup-restore.md) | Explicit backup/restore scope, UX, format behavior, rollback, and compatibility boundary. |
| [`security-backup.md`](security-backup.md) | Backup threat model, parser limits, fail-closed validation, checksum semantics, and safe format evolution. |
| [`privacy-backup.md`](privacy-backup.md) | Privacy model for explicit backups compared with Android system backup/device transfer. |
| [`logging.md`](logging.md) | Structured logging and sensitive-data redaction policy. |
| [`github-maintenance.md`](github-maintenance.md) | Actions, Dependabot, issue/PR hygiene, documentation upkeep, handoff discipline, and repository maintenance. |
| [`adr/`](adr/) | Architecture Decision Records documenting durable design choices. |
| [`assets/`](assets/) | Repository-owned brand artwork and verified screenshot-capture guidance. |

## Important root-level documents

| Document | Purpose |
| --- | --- |
| [`../README.md`](../README.md) | Main product/repository overview and quick start. |
| [`../CONTRIBUTING.md`](../CONTRIBUTING.md) | Contributor workflow and expectations. |
| [`../SECURITY.md`](../SECURITY.md) | Vulnerability reporting and security policy. |
| [`../PRIVACY.md`](../PRIVACY.md) | Runtime data/privacy behavior. |
| [`../SUPPORT.md`](../SUPPORT.md) | Support routes and issue guidance. |
| [`../CHANGELOG.md`](../CHANGELOG.md) | User-visible/reliability/security changes by release state. |
| [`../ROADMAP.md`](../ROADMAP.md) | Release planning and still-open gates. |
| [`../what_changed.md`](../what_changed.md) | Canonical multi-session engineering handoff and exact verification state. |

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

Full explanations are in [`android-build-guide.md`](android-build-guide.md). Production signing material is intentionally kept outside source control.

## Current technical baseline

- Application release candidate: 2.0.12
- Android versionCode: 20012
- Room database schema: 1
- Explicit backup schema: 1
- Language: Kotlin 2.0.21
- UI: Jetpack Compose + Material 3
- Android min SDK: 26
- Android target/compile SDK: 35
- JVM/Java target: 17
- Android Gradle Plugin: 8.7.3
- KSP: 2.0.21-1.0.28
- Documented local Gradle: 8.9
- Database: Room
- Preferences: DataStore
- Async/state: Kotlin Coroutines + Flow
- Testing: JUnit, AndroidX Test, Espresso, Compose UI Test
- Distribution formats: APK and AAB
- Core runtime network requirement: none
- License: MIT

## Documentation maintenance rules

When changing a command, application version, Android SDK target, output path, signing process, architecture rule, persistence/backup contract, or release behavior, update the authoritative documentation in the same change.

The repository guards intentionally verify required documentation, local Markdown links, tracked-file coverage, Android resources/security policy, source formatting, namespaces, and common secret patterns. See [`command-reference.md`](command-reference.md) for every guard command and [`documentation-map.md`](documentation-map.md) for ownership rules.

Documentation examples must never include real signing passwords, tokens, private keys, production secrets, or sensitive user data.

**Made by the Sanskar**
