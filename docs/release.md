# Release Guide

## Release principles

SpendCalc releases must be reproducible from public source without committing private signing material. Signing keys and credentials stay outside Git. A workflow definition, mergeable pull request, or queued/pending run is not evidence of a passing release; verification is tied to the exact commit being released.

The release process has four distinct evidence classes:

1. **source completeness** — implementation, tests, docs, workflows, and repository guards exist and agree;
2. **automated exact-head verification** — CI/CodeQL/dependency/repository checks actually report acceptable successful conclusions for the candidate SHA;
3. **manual Android verification** — connected-device tests, accessibility, real picker/share/export/restore, layout/theme/offline checks are actually performed;
4. **distribution evidence** — real screenshots, protected signing, artifact verification, and publication happen from the verified source.

Do not collapse these classes into one “done” statement.

## Pre-release checklist

1. Confirm `README.md`, `CHANGELOG.md`, `ROADMAP.md`, permanent `docs/`, and `what_changed.md` match actual behavior/status.
2. Confirm [`docs/codebase-reference.md`](codebase-reference.md) documents every tracked file and [`docs/documentation-map.md`](documentation-map.md) reflects current source-of-truth relationships.
3. Verify `versionCode` and `versionName` in `app/build.gradle.kts`.
4. Run all fast repository/static checks:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

5. Run a clean build and automated JVM/compile checks:

```bash
gradle clean
gradle testDebugUnitTest
gradle assembleDebugAndroidTest
gradle lint
gradle assembleDebug
gradle assembleRelease
```

6. Run Android tests on an emulator/device:

```bash
gradle connectedDebugAndroidTest
```

7. Confirm CI, CodeQL, dependency review, and Repository Audit results are successful for the **same exact release commit**.
8. Test fresh install and upgrade from the previous public release when one exists.
9. Verify the branded splash, onboarding, primary navigation icons/labels, light/dark/system themes, large text, and reduced motion.
10. Verify calculator validation, 100-item editor limit, named-history save/search/retention/delete+Undo/clear, and template save/load/delete+Undo.
11. Exercise saved-name/search input at the 120-character Unicode boundary and confirm the resulting saved record remains backup-exportable/restorable.
12. Verify text, CSV, and PDF export through real Android share flows.
13. Verify explicit backup export/restore through Android document pickers, visible progress state, duplicate-action blocking, confirmation, and restored history/templates/preferences.
14. Perform TalkBack, large-system-font, phone, and tablet/wide layout checks, including both save-name dialogs and backup progress/confirmation.
15. Scan the Git diff for secrets, personal data, keystores, generated local configuration, and fabricated assets.
16. Capture release screenshots from the verified build using fictional data only.
17. Complete the exact checklist in [`verification.md`](verification.md); unresolved release-blocking boxes remain blockers.

## Documentation verification

`codebase-reference.md` contains a marked file index. `scripts/check_documentation_coverage.py` compares it with `git ls-files` and must pass for the release commit. This catches tracked implementation/test/resource/configuration/workflow/document files that otherwise could be skipped by narrative documentation.

`documentation-map.md` defines which permanent docs should change for finance, persistence, backup/security/privacy, UI/accessibility, performance, build/CI, and release changes. Use it before declaring documentation reconciled.

`what_changed.md` may contain volatile PR/head/check state, but it does not replace permanent architecture/feature/security/test documentation.

## Versioning

Use semantic versioning for public releases:

- MAJOR: incompatible behavior/data-contract changes;
- MINOR: backwards-compatible features;
- PATCH: backwards-compatible fixes.

Android `versionCode` must always increase for a store/distribution release. Backup schema and Room database versions are separate compatibility dimensions and must not be changed casually to match the marketing/app version.

## Source and schema checks

Before a release that changes persistence:

- ensure Room schema version/migrations are correct;
- preserve/export schema history as required by the Room migration policy;
- ensure any newly tracked schema files are individually documented by `codebase-reference.md`;
- run migration tests once version 2+ exists;
- ensure repository and backup persisted-record contracts remain aligned.

Before a release that changes backup format:

- preserve bounded parsing and fail-closed behavior;
- update/supersede ADR 0004 deliberately if the fundamental design changes;
- update `backup-restore.md`, `security-backup.md`, `privacy-backup.md`, `persistence-invariants.md`, tests, and changelog as applicable;
- never claim the SHA-256 checksum authenticates backup authorship—it detects accidental corruption, not a malicious recomputation.

## Signing

Do not place signing passwords, keystore files, encoded signing material, or store credentials in the repository.

A maintainer can provide signing configuration through local protected properties or a protected CI secret store. The repository's default release build intentionally does not embed production signing credentials.

The public/tag workflow may prove that source compiles into an unsigned release candidate. It does **not** prove the final store artifact was signed correctly unless the protected signing process is separately executed and verified.

## Tagging

After the verified candidate is merged to the protected default branch and all release-blocking automated/manual gates are complete:

```bash
git tag -s v1.0.0 -m "SpendCalc 1.0.0"
git push origin v1.0.0
```

Use an unsigned tag only if signed tagging is unavailable and document that limitation.

Do not create `v1.0.0` while required automated checks are pending/queued/failed/cancelled, while a newer unverified commit supersedes the checked SHA, or while documented manual release gates remain incomplete.

## Release artifacts

The repository's tag workflow builds an unsigned release candidate. Production signed APK/AAB artifacts must be produced from the exact verified/tagged source with protected signing credentials outside Git.

Before publishing an artifact:

- record/verify the source commit SHA;
- verify artifact checksum(s);
- install the exact artifact on a test device;
- confirm About shows the expected version;
- verify it does not contain debug-only credentials/endpoints or private test data;
- confirm privacy/security documentation matches behavior;
- confirm the artifact corresponds to the tagged commit SHA;
- capture/store release notes and screenshots from the same verified version.

## Screenshots

Follow [`assets/screenshots/README.md`](assets/screenshots/README.md). Release screenshots must be captured from a real verified build and use fictional/non-private data. Do not generate or fabricate application screenshots merely to make the repository appear release-ready.

## Rollback

If a release has a blocker defect:

1. stop promotion/distribution where possible;
2. open a tracked regression issue without exposing private/security-sensitive information;
3. fix on a short-lived branch;
4. add a regression test at the lowest practical layer;
5. rerun the exact-commit automated/manual gates appropriate to the fix;
6. issue a new patch version rather than rewriting an existing tag/artifact.

## Release status sources

- [`verification.md`](verification.md) — authoritative gate checklist;
- [`release-candidate-final-audit.md`](release-candidate-final-audit.md) — source-completeness audit, not runtime evidence;
- [`ROADMAP.md`](../ROADMAP.md) — planning/open release work;
- [`what_changed.md`](../what_changed.md) — current active branch/PR/check handoff;
- GitHub Actions for the exact candidate SHA — authoritative automated outcomes.
