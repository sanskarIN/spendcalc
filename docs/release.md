# Release Guide

## Release principles

SpendCalc releases must be reproducible from public source without committing private signing material. Signing keys and credentials stay outside Git. A workflow definition or queued run is not evidence of a passing release; verification is tied to the exact commit being released.

## Pre-release checklist

1. Confirm `README.md`, `CHANGELOG.md`, `ROADMAP.md`, `docs/`, and `what_changed.md` are current.
2. Verify `versionCode` and `versionName` in `app/build.gradle.kts`.
3. Run repository/static checks:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

4. Run a clean build and automated JVM/compile checks:

```bash
gradle clean
gradle testDebugUnitTest
gradle assembleDebugAndroidTest
gradle lint
gradle assembleDebug
gradle assembleRelease
```

5. Run Android tests on an emulator/device:

```bash
gradle connectedDebugAndroidTest
```

6. Confirm CI, CodeQL, dependency review, and repository-audit results are successful for the exact release commit.
7. Test fresh install and upgrade from the previous public release when one exists.
8. Verify the branded splash, onboarding, primary navigation icons/labels, light/dark/system themes, large text, and reduced motion.
9. Verify calculator validation, the 100-item editor limit, history search/retention/delete+Undo, and template save/load/delete+Undo.
10. Verify text, CSV, and PDF export.
11. Verify backup export/restore, progress state, restore confirmation, and restored history/templates/preferences.
12. Perform TalkBack, large-system-font, phone, and tablet/wide layout checks.
13. Scan the Git diff for secrets, personal data, keystores, and generated local configuration.
14. Capture release screenshots from the verified build using fictional data only.

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

Do not create `v1.0.0` while required automated checks are pending/queued/failed or while the documented manual release gates remain incomplete.

## Release artifacts

The repository's tag workflow builds an unsigned release candidate. Production signed APK/AAB artifacts must be produced from the exact verified/tagged source with protected signing credentials outside Git.

Before publishing an artifact:

- verify checksum;
- install it on a test device;
- confirm About shows the expected version;
- verify it does not contain debug-only credentials or endpoints;
- confirm the privacy documentation matches behavior;
- confirm the artifact corresponds to the tagged commit SHA.

## Rollback

If a release has a blocker defect:

1. stop promotion/distribution where possible;
2. open a tracked regression issue;
3. fix on a short-lived branch;
4. add a regression test;
5. issue a new patch version rather than rewriting an existing tag/artifact.
