<p align="center">
  <img src="docs/assets/spendcalc-logo.svg" alt="SpendCalc logo" width="128" height="128" />
</p>

<h1 align="center">SpendCalc</h1>

<p align="center"><strong>A precision-safe, private, offline-first expense calculator for Android.</strong></p>

<p align="center">
  <a href="https://github.com/sanskarIN/spendcalc/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/sanskarIN/spendcalc/actions/workflows/ci.yml/badge.svg" /></a>
  <a href="https://github.com/sanskarIN/spendcalc/actions/workflows/codeql.yml"><img alt="CodeQL" src="https://github.com/sanskarIN/spendcalc/actions/workflows/codeql.yml/badge.svg" /></a>
  <img alt="Android API 26+" src="https://img.shields.io/badge/Android-API%2026%2B-3DDC84?logo=android&logoColor=white" />
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?logo=kotlin&logoColor=white" />
  <a href="LICENSE"><img alt="MIT License" src="https://img.shields.io/badge/License-MIT-blue.svg" /></a>
</p>

<p align="center">
  <a href="https://buymeacoffee.com/sanskarIN"><img alt="Buy Me a Coffee" src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000" /></a>
</p>

> **Made by the Sanskar**

SpendCalc is built for real everyday bills rather than classroom-demo arithmetic. It keeps the finance engine separate from Android UI/infrastructure, uses `BigDecimal` for money, works without a required network connection, and provides local history, templates, accessibility settings, and export options.

## Documentation

The repository now includes a deep, command-by-command Android documentation set.

Start with:

- [`docs/README.md`](docs/README.md) — complete documentation index and recommended reading paths.
- [`docs/setup.md`](docs/setup.md) — Windows/macOS/Linux workstation setup, JDK 17, Android SDK 35, Gradle 8.9, ADB, and first build.
- [`docs/android-build-guide.md`](docs/android-build-guide.md) — complete source-to-APK/AAB workflow, debug/release builds, artifact paths, device installation, signing, verification, and release preparation.
- [`docs/command-reference.md`](docs/command-reference.md) — detailed meanings for Git, Java, Gradle, ADB, `keytool`, `zipalign`, `apksigner`, `jarsigner`, and repository quality commands.
- [`docs/troubleshooting.md`](docs/troubleshooting.md) — Gradle/JDK/SDK/dependency/ADB/APK/AAB/signing/test/release diagnosis.
- [`docs/release.md`](docs/release.md) — release gates, versioning, APK/AAB signing, checksums, upgrade testing, and publication rules.

## Screenshots

Verified screenshots are intentionally captured from real release-candidate builds rather than presented as fake production images. The capture checklist is in [`docs/assets/screenshots/README.md`](docs/assets/screenshots/README.md). Until those captures are added, the editable brand artwork at [`docs/assets/spendcalc-logo.svg`](docs/assets/spendcalc-logo.svg) is the repository's visual placeholder.

## Features

### Expense calculation

- Itemized expense lines with quick totals.
- Discount, tax, tip, and service-charge percentages.
- Split-bill calculation for one or more people.
- Manual exchange-rate conversion with three-letter currency codes.
- Explicit charge order documented in the architecture guide.
- Precision-safe `BigDecimal` arithmetic and centralized rounding policy.
- Live receipt-style result view.

### Save and reuse

- Room-backed calculation history.
- Individual history deletion and clear-all confirmation.
- Optional history auto-delete after 30 or 90 days.
- Saved templates for common discount/tax/tip/service/split/currency settings.

### Export

- Plain-text receipt sharing.
- CSV export with quote escaping and spreadsheet-formula neutralization for text cells.
- Offline PDF receipt creation using Android `PdfDocument`.
- Cache-file sharing through a non-exported Android `FileProvider` with temporary read permission.

### UI and accessibility

- Jetpack Compose + Material 3.
- Responsive phone/tablet layout.
- Light, dark, and system theme modes.
- Large-text preference.
- Reduced-motion preference and no fake loading delays.
- Externalized user-facing strings for localization readiness.
- Clear validation text in addition to color/state styling.
- First-run onboarding.
- About screen with version, license, support, repository, funding, and credit.

### Privacy

- Core calculation requires no account.
- Core calculation requires no network.
- Current manifest does not request Android Internet permission.
- History/templates/preferences live in app-local storage.
- No analytics or advertising SDK is required by the current implementation.

See [`PRIVACY.md`](PRIVACY.md) and [`SECURITY.md`](SECURITY.md).

## Supported platform

| Platform | Status |
| --- | --- |
| Android API 26+ | Primary supported runtime target |
| Android phone | Supported |
| Android tablet / wide screen | Responsive layout supported |
| Windows | Development/build host supported with Android toolchain |
| macOS | Development/build host supported with Android toolchain |
| Linux | Development/build host supported with Android toolchain |
| iOS / iPadOS runtime | Not part of this repository |
| Windows/macOS/Linux desktop runtime | Not part of this repository |
| Web/browser runtime | Not part of this repository |

## Language and tech stack

Primary application language: **Kotlin**.

Main technologies:

- Kotlin `2.0.21`
- Jetpack Compose
- Material 3
- AndroidX Navigation Compose
- AndroidX Lifecycle/ViewModel
- Room + KSP
- Preferences DataStore
- Kotlin coroutines/Flow
- Android `PdfDocument`
- JUnit + AndroidX/Compose UI tests
- Android Gradle Plugin `8.7.3`
- documented Gradle `8.9`
- Java/JVM 17
- GitHub Actions + CodeQL + Dependabot

## Android configuration

Current `app/build.gradle.kts` baseline:

```text
applicationId = in.sanskar.spendcalc
minSdk = 26
targetSdk = 35
compileSdk = 35
versionCode = 1
versionName = 1.0.0
Java/JVM target = 17
```

The release build enables code minification and resource shrinking.

## What Android executable files can be built?

### Debug APK

```bash
gradle assembleDebug
```

Expected output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The debug APK is intended for development/testing and is automatically debug-signed by Android tooling.

### Release APK

```bash
gradle assembleRelease
```

Inspect:

```text
app/build/outputs/apk/release/
```

The repository deliberately does not store production signing credentials. A release output must be securely signed and verified before production distribution.

### Release Android App Bundle (AAB)

```bash
gradle bundleRelease
```

Inspect:

```text
app/build/outputs/bundle/release/
```

AAB is primarily a publishing format. It is not normally installed directly with `adb install`.

Full details: [`docs/android-build-guide.md`](docs/android-build-guide.md).

## Architecture

SpendCalc follows a small layered modular-monolith approach inside one Android module:

```text
Compose UI
   ↓
SpendCalcViewModel
   ↓
Domain calculation + repositories
   ↓
Room / DataStore

Platform adapters: FileProvider, PDF, share intents, external links
```

The domain layer contains finance rules and does not depend on Compose, Room, Activity, or Android resources.

Full details: [`docs/architecture.md`](docs/architecture.md)

Architecture decisions:

- [`ADR 0001 — BigDecimal finance arithmetic`](docs/adr/0001-use-bigdecimal-for-finance.md)
- [`ADR 0002 — Local-first core`](docs/adr/0002-local-first-core.md)
- [`ADR 0003 — Room and DataStore`](docs/adr/0003-room-and-datastore.md)

## Calculation rule

The initial calculation order is intentionally deterministic:

1. Sum item amounts.
2. Calculate discount from subtotal.
3. Subtract discount.
4. Calculate tax, tip, and service charge from the discounted base.
5. Sum the discounted base and charges.
6. Apply the manual exchange rate.
7. Round monetary outputs using the configured policy.
8. Divide by split count.

Any behavior change to this order should include exact regression tests and changelog notes.

## Quick start

### Requirements

- Git
- JDK 17
- Android Studio
- Android SDK Platform 35
- compatible Android SDK Build-Tools and Platform-Tools
- Gradle 8.9 for the current command-line setup

The repository currently does not commit a Gradle wrapper JAR, so examples use the local `gradle` executable.

Clone:

```bash
git clone https://github.com/sanskarIN/spendcalc.git
cd spendcalc
```

Check environment:

```bash
java -version
gradle --version
adb version
```

Run tests and lint:

```bash
gradle testDebugUnitTest
gradle lintDebug
```

Build the debug APK:

```bash
gradle assembleDebug
```

Install on a connected Android device/emulator:

```bash
gradle installDebug
```

or:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

For Android Studio, open the repository, use JDK 17 for Gradle, allow sync to complete, select an API 26+ device/emulator, and run the `app` configuration.

Detailed setup: [`docs/setup.md`](docs/setup.md)

## Complete local verification

A useful grouped source/build verification command is:

```bash
gradle clean testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease
```

With an emulator/device:

```bash
gradle connectedDebugAndroidTest
```

Repository utility checks:

```bash
python3 scripts/check_format.py
python3 scripts/scan_secrets.py
```

On Windows environments that expose Python as `python` rather than `python3`, use `python` for those scripts.

## Development

The app uses Android SDK 35 and Java 17 bytecode. `local.properties` is intentionally excluded from Git.

Development guide: [`docs/development.md`](docs/development.md)

Command dictionary: [`docs/command-reference.md`](docs/command-reference.md)

## Testing

The repository includes:

- finance arithmetic and validation unit tests;
- decimal/rounding regression tests;
- history repository tests;
- template repository tests;
- CSV security/escaping tests;
- receipt formatter tests;
- Room Android integration tests;
- a Compose calculator screen smoke test.

Testing strategy: [`docs/testing.md`](docs/testing.md)

## Android APK installation and ADB

Check devices:

```bash
adb devices
```

Install/reinstall debug APK:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Uninstall SpendCalc from the selected test device:

```bash
adb uninstall in.sanskar.spendcalc
```

Deep ADB/build instructions: [`docs/android-build-guide.md`](docs/android-build-guide.md).

## Release signing

Production signing keys and passwords are **not** committed.

The documented manual release flow is:

1. `gradle assembleRelease`
2. inspect the generated unsigned release APK
3. align it with `zipalign`
4. sign it with `apksigner`
5. verify its signing certificate
6. install/test the final signed APK
7. build/sign an AAB when store publishing is required

See [`docs/android-build-guide.md`](docs/android-build-guide.md) and [`docs/release.md`](docs/release.md) for exact commands, flag meanings, security rules, and verification steps.

## CI and repository automation

- `CI`: format guard, common-secret-pattern guard, JVM tests, Android lint, debug build, and release compilation.
- `CodeQL`: Java/Kotlin static analysis.
- `Dependency Review`: pull-request dependency change review.
- `Dependabot`: weekly Gradle and GitHub Actions updates.
- `Release Candidate`: tag-triggered verified unsigned release build.

Repository workflow files live under [`.github/workflows/`](.github/workflows/).

## Security

SpendCalc intentionally minimizes permissions and remote dependencies. The export provider is non-exported and only grants temporary read access during a user-selected share action. CSV text cells are escaped and common formula-leading characters are neutralized.

Do not report exploitable vulnerability details in a public issue. Follow [`SECURITY.md`](SECURITY.md).

Production keystores, private keys, signing passwords, access tokens, and credentials must never be committed.

## Privacy and data

History/templates use Room, while preferences use DataStore. Users can clear history and delete templates, and history can automatically expire after 30 or 90 days. Android system backup/device transfer may include app-local database/preferences according to OS/device backup settings.

Read [`PRIVACY.md`](PRIVACY.md).

## Accessibility

Release checks include TalkBack traversal, large system font scale, light/dark themes, touch-target review, small/wide screen behavior, and non-color-only validation.

Read [`docs/accessibility.md`](docs/accessibility.md).

## Performance

The app avoids network initialization and keeps ordinary calculation work in memory. Performance work should be based on profiling rather than replacing correct decimal math with faster but unsafe primitives.

Read [`docs/performance.md`](docs/performance.md).

## Troubleshooting

The troubleshooting guide now covers:

- Gradle/JDK/PATH problems;
- Android SDK 35 configuration;
- dependency/cache failures;
- ADB/device authorization;
- APK/AAB output locations;
- installation/signature/version errors;
- `zipalign`/`apksigner` problems;
- Room/KSP issues;
- lint/unit/instrumentation failures;
- release-only shrinking problems.

Read [`docs/troubleshooting.md`](docs/troubleshooting.md).

## Contributing

Contributions are welcome. Start with [`CONTRIBUTING.md`](CONTRIBUTING.md) and the pull-request checklist.

For local commits, the requested project commit email is:

```bash
git config user.email "sanskarin@outlook.in"
```

The connected GitHub contents API may attribute commits to the authenticated GitHub identity; local contributors can explicitly set the requested Git email as shown above.

## Roadmap and changes

- [`ROADMAP.md`](ROADMAP.md)
- [`CHANGELOG.md`](CHANGELOG.md)
- [`what_changed.md`](what_changed.md) — multi-session engineering handoff/current verification state

## Support and contact

- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
- Support: `supportramsandesh@gmail.com`
- GitHub: https://github.com/sanskarIN
- Repository: https://github.com/sanskarIN/spendcalc

Funding is optional and never required to use SpendCalc:

[![Buy Me a Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-sanskarIN-FFDD00?logo=buy-me-a-coffee&logoColor=000000)](https://buymeacoffee.com/sanskarIN)

## License

SpendCalc is open source under the [`MIT License`](LICENSE).

**Made by the Sanskar**
