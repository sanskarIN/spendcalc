# Contributing to SpendCalc

Thank you for helping improve SpendCalc. Contributions should preserve the app's offline-first, privacy-friendly, precision-safe behavior and the repository's test/documentation invariants.

## Development principles

- Keep finance arithmetic in the domain layer and use `BigDecimal`; do not introduce floating-point money math.
- Keep Android UI/infrastructure concerns out of domain rules.
- Treat history/template repositories as validation boundaries, not passive DAO wrappers.
- Keep repository-accepted saved data compatible with explicit backup export; read [`docs/persistence-invariants.md`](docs/persistence-invariants.md) before changing stored-record rules.
- Prefer small, cohesive changes with tests at the lowest practical layer.
- Do not add network requirements for core calculations.
- Do not commit credentials, signing material, personal data, generated secrets, or production-only endpoints.
- Externalize user-visible strings and keep accessibility semantics intact.
- Preserve the visible credit `Made by the Sanskar`.
- Keep every tracked file documented in [`docs/codebase-reference.md`](docs/codebase-reference.md).

## Repository orientation

- [`README.md`](README.md) — public overview and quick start.
- [`docs/architecture.md`](docs/architecture.md) — layer/dependency design.
- [`docs/codebase-reference.md`](docs/codebase-reference.md) — purpose of every tracked file.
- [`docs/documentation-map.md`](docs/documentation-map.md) — documentation authority and update triggers.
- [`docs/development.md`](docs/development.md) — detailed change rules.
- [`docs/testing.md`](docs/testing.md) — verification strategy and commands.

## Local setup

See [`docs/setup.md`](docs/setup.md). The baseline is JDK 17, Android SDK 35, and Gradle 8.9.

The repository currently does not commit a Gradle wrapper; use a compatible local Gradle executable or Android Studio environment as documented.

## Before opening a pull request

Run the fast repository guards:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

Run the Android/JVM compile/test checks:

```bash
gradle testDebugUnitTest
gradle assembleDebugAndroidTest
gradle lint
gradle assembleDebug
gradle assembleRelease
```

When an Android emulator/device is available, also run:

```bash
gradle connectedDebugAndroidTest
```

Review changed files for accidental secrets, signing material, private test data, and undocumented tracked paths before committing.

## Complete documentation rule

Adding, deleting, or renaming **any tracked file** requires updating the marked file index in `docs/codebase-reference.md` in the same change. The `scripts/check_documentation_coverage.py` guard compares the index with `git ls-files` and fails on:

- tracked files missing from the reference;
- stale documented paths that are no longer tracked;
- duplicate documented paths.

A path-only entry is not enough: describe what the file owns and the product/test/security/release invariant it supports.

For behavior changes, use the change-to-document matrix in `docs/documentation-map.md` to determine which permanent docs must be reconciled. Update `CHANGELOG.md` when the change is notable to users, security, reliability, or release behavior.

## Commit style

Conventional Commit prefixes are preferred:

- `feat:` new behavior
- `fix:` defect correction
- `test:` test-only work
- `docs:` documentation
- `refactor:` behavior-preserving code restructuring
- `perf:` measured performance improvement
- `build:` build-system work
- `ci:` automation changes
- `chore:` maintenance

Prefer multiple meaningful atomic commits when changes have independent review value, but do not split one invariant into artificial/noise commits.

Configure the requested local commit identity when contributing from a clone:

```bash
git config user.email "sanskarin@outlook.in"
```

Use your correct Git author name.

## Pull requests

A pull request should:

1. Explain the user/engineering problem and solution.
2. Keep scope focused.
3. Include tests for new behavior or confirmed bug fixes.
4. Update permanent documentation when behavior/configuration/invariants change.
5. Update the exhaustive file reference for tracked file changes.
6. Pass repository guards, JVM tests, instrumentation compilation, lint, and build/security checks.
7. Avoid unrelated formatting churn.
8. Clearly identify any manual verification that still remains rather than representing it as completed.

A PR being conflict-free/mergeable does not by itself mean it is release-ready. Release readiness is tied to the exact commit and the checklist in [`docs/verification.md`](docs/verification.md).

## Finance and persistence changes

Finance changes require exact `BigDecimal` regression tests and review of calculation order/rounding bounds. Persistence/backup changes require tests proving invalid direct repository calls cannot create data that later fails backup export.

Batch history/template replacement must validate all candidate records and duplicate IDs before DAO replacement. Valid accepted restore names are preserved exactly; new user input normalization is a separate path.

## UI and Android-resource changes

- Keep user-visible strings in default resource files under `app/src/main/res/values/`.
- Run the Android resource guard after string/reference changes.
- Preserve the manifest's local-first/no-Internet baseline unless a future feature deliberately changes the documented privacy model.
- Keep `FileProvider` non-exported and limited to the private export cache subtree.
- Test accessible labels, large text, reduced motion, phone/wide layouts, and TalkBack impact for meaningful UI changes.

## Bugs

Use the bug report template. Include reproducible steps, Android version, device/emulator details, expected behavior, actual behavior, and logs only after removing personal or sensitive information.

Confirmed defects should receive a regression test before or with the fix whenever technically practical.

## Security issues

Do not publicly disclose a vulnerability before maintainers have had a reasonable opportunity to investigate. Follow [`SECURITY.md`](SECURITY.md). Never paste live credentials, signing keys, private backup contents, or personal receipts into an issue.

## Conduct

Participation is governed by [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
