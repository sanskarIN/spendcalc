# Release Guide

## Release principles

SpendCalc releases must be reproducible from public source without committing private signing material. Signing keys and credentials stay outside Git.

## Pre-release checklist

1. Confirm `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` are current.
2. Verify versionCode/versionName in `app/build.gradle.kts`.
3. Run a clean build:

```bash
gradle clean assembleDebug assembleRelease
```

4. Run JVM tests and lint:

```bash
gradle testDebugUnitTest lintDebug
```

5. Run Android tests on an emulator/device:

```bash
gradle connectedDebugAndroidTest
```

6. Review dependency/security automation results.
7. Test fresh install and upgrade from the previous public release when one exists.
8. Verify history/templates remain intact across supported upgrades.
9. Verify text, CSV, and PDF export.
10. Perform accessibility and dark/large-text checks.
11. Scan the Git diff for secrets, personal data, keystores, and generated local configuration.

## Versioning

Use semantic versioning for public releases:

- MAJOR: incompatible behavior/data-contract changes;
- MINOR: backwards-compatible features;
- PATCH: backwards-compatible fixes.

Android `versionCode` must always increase for a store/distribution release.

## Signing

Do not place signing passwords, keystore files, or encoded signing material in the repository.

A maintainer can provide signing configuration through local Gradle properties or a protected CI secret store. The repository's default release build intentionally does not embed production signing credentials.

## Tagging

After verification and merge to the protected default branch:

```bash
git tag -s v1.0.0 -m "SpendCalc 1.0.0"
git push origin v1.0.0
```

Use an unsigned tag only if signed tagging is not available and document that limitation.

## Release artifacts

A release workflow may attach verified APK/AAB artifacts. Artifacts should be produced from the tagged commit and identified by version and commit SHA.

Before publishing an artifact:

- verify checksum;
- install it on a test device;
- confirm About shows the expected version;
- verify it does not contain debug-only credentials or endpoints;
- confirm the privacy documentation matches behavior.

## Rollback

If a release has a blocker defect:

1. stop promotion/distribution where possible;
2. open a tracked regression issue;
3. fix on a short-lived branch;
4. add a regression test;
5. issue a new patch version rather than rewriting an existing tag/artifact.
