# Troubleshooting

## `gradlew` or `gradlew.bat` is missing

The current SpendCalc repository intentionally does **not** commit a Gradle wrapper. Use a compatible local Gradle 8.9 installation or Android Studio's configured Gradle environment, as described in [`setup.md`](setup.md).

Verify:

```bash
gradle --version
```

Do not create/commit wrapper files merely to make an undocumented local command work. If the project deliberately adopts the wrapper later, add all wrapper files, update setup/CI guidance, and document every newly tracked path in `codebase-reference.md`.

## Gradle uses the wrong Java version

SpendCalc targets Java/JVM 17 bytecode. Confirm your Gradle JVM is JDK 17 or a compatible newer JDK capable of targeting 17:

```bash
java -version
gradle --version
```

In Android Studio, check **Settings/Preferences → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**.

## Android SDK 35 is missing

Install Android SDK Platform 35 and compatible Build-Tools from Android Studio's SDK Manager. CI installs `platforms;android-35` and `build-tools;35.0.0`. Ensure `local.properties` points to the correct SDK path.

## `SDK location not found`

Create local `local.properties` with `sdk.dir=...`. This file is intentionally ignored by Git and must not be added to `codebase-reference.md` because it is not a tracked repository file.

## Documentation coverage audit fails

Run:

```bash
python3 scripts/check_documentation_coverage.py
```

The guard compares `git ls-files` with the marked file index in [`codebase-reference.md`](codebase-reference.md). Typical failures mean:

- **Tracked files missing from the reference** — add one descriptive entry for each new tracked path.
- **Documented paths that are not tracked** — a file was deleted/renamed; remove or rename its old reference entry.
- **Paths documented more than once** — keep exactly one authoritative file entry.
- **File-index marker error** — preserve exactly one `<!-- FILE-INDEX:START -->` and `<!-- FILE-INDEX:END -->` pair in the correct order.
- **Git unavailable** — run inside a real Git checkout with the `git` executable installed.

Do not “fix” this guard by ignoring source/test/resource/configuration files. Its purpose is to make complete documentation mechanically verifiable.

## Repository link/required-file audit fails

Run:

```bash
python3 scripts/check_repository.py
```

This guard verifies required release/project documents and local Markdown links. If a local link is broken, update the source link or restore the intended tracked file; do not replace a valid local link with an external URL simply to bypass the check.

It also requires the exhaustive codebase reference, documentation map, persistence/release/security docs, and key Android/documentation guard scripts.

## Android string-resource audit fails

Run:

```bash
python3 scripts/check_android_resources.py
```

A failure usually means a Kotlin/XML `R.string.*`/`@string/*` reference lacks a default resource, or the same default string name exists in more than one `app/src/main/res/values/*.xml` file. String resources are intentionally split across focused files but still share one Android namespace.

## Android local-first security audit fails

Run:

```bash
python3 scripts/check_android_security.py
```

Check `AndroidManifest.xml` and `res/xml/file_paths.xml`. The current security contract requires no Android `INTERNET` permission for core operation, a non-exported `FileProvider`, and sharing limited to the private `cache/exports` subtree.

## KSP/Room schema errors

Run a clean build:

```bash
gradle clean assembleDebug
```

Room schema export is configured under `app/schemas/`. Database version 1 currently has no released migration predecessor. When generated schema snapshots become part of migration history, keep old released schemas and add each tracked schema file to `codebase-reference.md`. Do not delete released schemas simply to silence a migration error.

## Unit test failures around decimal values

Use decimal strings when creating `BigDecimal` test fixtures:

```kotlin
BigDecimal("0.10")
```

Do not construct money values from binary floating-point literals such as `BigDecimal(0.1)`.

## Saved-history/template persistence throws `IllegalArgumentException`

Repositories intentionally validate persisted records even when callers bypass the UI/ViewModel. Review [`persistence-invariants.md`](persistence-invariants.md).

Common causes include:

- blank/oversized/malformed IDs or saved names;
- negative timestamps;
- invalid/canonical currency values;
- unsupported history split/result decimal shapes;
- invalid template finance settings;
- duplicate IDs in a replacement batch.

For restore/batch failures, validation occurs before DAO replacement so existing valid data should remain untouched.

## App shows validation instead of a result

Check:

- item amounts are non-negative bounded decimals;
- discount is between 0 and 100;
- tax/tip/service-charge percentages are within their supported bounded ranges;
- people/split count is between 1 and 1,000,000;
- currency fields normalize to exactly three letters;
- exchange rate is greater than 0 and inside the supported numeric shape.

## History does not contain a calculation

History is opt-in. Use **Save result** on a valid calculation, optionally provide a label, then press the dialog **Save** action. Blank labels fall back to `Calculation`. Check the auto-delete setting if older entries disappear.

## History search does not accept more text

History search is intentionally capped at 120 UTF-16 characters and truncates safely without splitting a valid surrogate pair. This bounds repeated in-memory filtering work and matches the documented UI contract.

## Template cannot be saved

A template can only be saved while the active calculator state produces a valid result. Template persistence also revalidates the settings it stores. Check discount/tax/tip/service percentages, split count, currencies, and exchange rate. Line items are not stored by templates.

## Backup file is rejected

Explicit backups are treated as untrusted input. Rejection can be caused by payload/line/record bounds, checksum mismatch, unsupported schema, malformed Base64/UTF-8/Unicode, invalid IDs/names/timestamps/currencies/decimals/splits/template settings, or duplicate IDs.

See [`security-backup.md`](security-backup.md). The SHA-256 checksum detects accidental corruption but is not a signature or proof of authorship.

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

Remember that CI's `assembleDebugAndroidTest` only proves the instrumentation suite compiles; it is not a substitute for connected-device execution.

## Release shrinking issue

If a release-only failure appears, reproduce with:

```bash
gradle assembleRelease
```

Do not add broad `-keep class ** { *; }` rules. Identify the specific reflection/consumer-rule requirement and add the narrowest rule with a regression note.

## GitHub Actions remain queued/pending

A queued/pending workflow is not a source failure and not a success. Confirm the run belongs to the exact current PR head. Avoid speculative commits solely to “refresh” Actions because concurrency cancellation will supersede older runs again.

If a run finishes with failure, inspect the failing job/step/log for that exact SHA and make the smallest evidence-driven fix.

## Need more help

See [`SUPPORT.md`](../SUPPORT.md) and include sanitized logs plus the exact command, commit SHA, and environment details. Never include credentials, signing material, private backup contents, or real receipt/history data.
