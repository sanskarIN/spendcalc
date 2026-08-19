# Troubleshooting

## Gradle uses the wrong Java version

SpendCalc targets Java/JVM 17 bytecode. Confirm your Gradle JVM is JDK 17 or a compatible newer JDK capable of targeting 17:

```bash
java -version
gradle --version
```

In Android Studio, check **Settings/Preferences → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**.

## Android SDK 35 is missing

Install Android SDK Platform 35 and a compatible Build-Tools version from Android Studio's SDK Manager. Ensure `local.properties` points to the correct SDK path.

## `SDK location not found`

Create local `local.properties` with `sdk.dir=...`. This file is intentionally ignored by Git.

## KSP/Room schema errors

Run a clean build:

```bash
gradle clean assembleDebug
```

If a schema directory is generated, keep committed schema snapshots in `app/schemas/` once release migration history exists. Do not delete old released schemas simply to silence a migration error.

## Unit test failures around decimal values

Use decimal strings when creating `BigDecimal` test fixtures:

```kotlin
BigDecimal("0.10")
```

Do not construct money values from binary floating-point literals such as `BigDecimal(0.1)`.

## App shows validation instead of a result

Check:

- item amounts are non-negative decimals;
- percentages are between 0 and 1000;
- people/split count is at least 1;
- currency fields are exactly three letters;
- exchange rate is greater than 0.

## History does not contain a calculation

History is opt-in. Use **Save result** on a valid calculation. Check the auto-delete setting if older entries disappear.

## CSV/PDF share target is missing

The Android share sheet only lists installed apps that can accept the exported MIME type. The export is generated locally before the share intent is opened.

## External GitHub/BMC/email action does nothing

The device must have an application capable of handling the selected URL or email intent. Core SpendCalc calculation functionality remains available without those external apps.

## Instrumentation tests fail to find a device

Verify:

```bash
adb devices
```

Start an emulator or connect an authorized Android device, then rerun:

```bash
gradle connectedDebugAndroidTest
```

## Release shrinking issue

If a release-only failure appears, reproduce with:

```bash
gradle assembleRelease
```

Do not add broad `-keep class ** { *; }` rules. Identify the specific reflection/consumer-rule requirement and add the narrowest rule with a regression note.

## Need more help

See [`SUPPORT.md`](../SUPPORT.md) and include sanitized logs plus the exact command and environment details.
