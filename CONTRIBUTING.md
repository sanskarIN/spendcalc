# Contributing to SpendCalc

Thank you for helping improve SpendCalc. Contributions should preserve the app's offline-first, privacy-friendly, precision-safe behavior.

## Development principles

- Keep finance arithmetic in the domain layer and use `BigDecimal`; do not introduce floating-point money math.
- Keep Android UI/infrastructure concerns out of domain rules.
- Prefer small, cohesive changes with tests.
- Do not add network requirements for core calculations.
- Do not commit credentials, signing material, personal data, generated secrets, or production-only endpoints.
- Externalize user-visible strings and keep accessibility semantics intact.
- Preserve the visible credit `Made by the Sanskar`.

## Local setup

See [`docs/setup.md`](docs/setup.md). The baseline is JDK 17, Android SDK 35, and Gradle 8.9.

## Before opening a pull request

Run the relevant checks from the repository root:

```bash
gradle clean testDebugUnitTest lintDebug assembleDebug
```

When an Android emulator/device is available, also run:

```bash
gradle connectedDebugAndroidTest
```

Review the changed files for accidental secrets and personal data before committing.

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

Configure the requested local commit identity when contributing from a clone:

```bash
git config user.email "sanskarin@outlook.in"
```

Use your correct Git author name.

## Pull requests

A pull request should:

1. Explain the user problem and solution.
2. Keep scope focused.
3. Include tests for new behavior or bug fixes.
4. Update documentation when behavior/configuration changes.
5. Pass build, unit tests, lint, and security checks.
6. Avoid unrelated formatting churn.

## Bugs

Use the bug report template. Include reproducible steps, Android version, device/emulator details, expected behavior, actual behavior, and logs only after removing personal or sensitive information.

## Security issues

Do not publicly disclose a vulnerability before maintainers have had a reasonable opportunity to investigate. Follow [`SECURITY.md`](SECURITY.md).

## Conduct

Participation is governed by [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
