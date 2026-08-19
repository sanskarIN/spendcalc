# Development Guide

## Project layout

```text
app/src/main/java/in/sanskar/spendcalc/
├── data/                 repositories and settings
│   └── local/            Room database, entities, DAOs
├── domain/               finance rules and export contracts
│   ├── export/
│   └── model/
├── platform/             Android-specific adapters
├── ui/                   ViewModel, navigation, state mapping
│   ├── components/
│   ├── screens/
│   └── theme/
├── AppContainer.kt
├── MainActivity.kt
└── SpendCalcApplication.kt
```

For ownership of every tracked file—not only Kotlin production code—use [`codebase-reference.md`](codebase-reference.md). It covers build/configuration files, `.github` automation, Android resources, source/test files, scripts, policies, assets, and documentation. [`documentation-map.md`](documentation-map.md) defines which document is authoritative for each kind of engineering or release question.

## Finance changes

All money arithmetic belongs in `CalculatorEngine` or another domain type that uses `BigDecimal`. Never convert a monetary value to `Float` or `Double` for calculations.

When changing calculation behavior:

1. document the intended order/rule;
2. add or update a failing unit test first;
3. implement the smallest domain change;
4. verify rounding at boundary cases;
5. update `CHANGELOG.md` when user-visible behavior changes;
6. reconcile `features.md`, `architecture.md`, and the relevant ADR when the contract changes.

## UI changes

- Keep user-facing strings under `app/src/main/res/values/` and group feature-specific copy in an existing focused string file where practical.
- Run `python3 scripts/check_android_resources.py` after adding, renaming, moving, or deleting app string resources. It verifies Kotlin/XML references against default strings and rejects duplicate default string names.
- Use `SpendCalcTokens` for common spacing/radius/layout decisions where applicable.
- Keep the editable calculator within `MAX_EXPENSE_ITEMS`; use a virtualized design if a future requirement genuinely needs substantially larger editable bills.
- Keep interactive search/name input work bounded; History search and saved names currently use a 120-character policy with surrogate-safe truncation.
- Check both phone and wide/tablet layouts.
- Test light, dark, system, large-text, and reduced-motion modes.
- Do not hide required meaning behind color or an icon alone.
- Prefer existing Material components and semantic roles.
- For visible navigation labels, keep decorative icon descriptions null so screen readers do not announce duplicate destination names.
- When a dialog action shares wording with a control behind it, prefer concise distinct confirmation wording where that improves accessibility/test targeting.

## Persistence and backup changes

Room database version begins at 1. For any schema change after release:

1. increment the Room version;
2. add an explicit migration;
3. preserve exported schemas;
4. add migration tests;
5. document compatibility in the changelog.

Do not enable destructive migration as a shortcut for production schema evolution.

History/template repositories are trust boundaries rather than passive DAO wrappers. Direct repository callers must not be able to manufacture local records that explicit backup export rejects. Before changing repository/backup validation, read [`persistence-invariants.md`](persistence-invariants.md) and preserve its shared contracts for saved names, identifiers, timestamps, canonical currencies, split/result bounds, template finance validation, and duplicate IDs.

Changes to explicit backup/restore must preserve versioned decoding, bounded input handling, fail-closed validation, exact accepted saved-name restore behavior, duplicate-ID rejection, transactional Room replacement, compensating cross-store rollback, and regression coverage. Security-sensitive format changes also require review of [`security-backup.md`](security-backup.md), [`privacy-backup.md`](privacy-backup.md), and ADR 0004.

## Export changes

Platform-independent export serialization implements `ExportFormatter`. Android-only file/PDF/share work stays in `platform/`.

CSV changes must continue to escape quotes and defend text cells against spreadsheet formula interpretation. FileProvider sharing must remain restricted to the private `cache/exports` subtree, canonical path containment must remain intact, and blocking file/PDF/document work must stay off the main thread.

## Android manifest and resources

- The core manifest intentionally has no Android `INTERNET` permission.
- `FileProvider` must remain non-exported and path-limited by `res/xml/file_paths.xml`.
- `backup_rules.xml` and `data_extraction_rules.xml` must stay aligned with `PRIVACY.md` and `privacy-backup.md`.
- Run `scripts/check_android_security.py` after manifest/provider/backup-policy changes.
- Run `scripts/check_android_resources.py` after string/resource-reference changes.

## Dependencies and build configuration

Before adding a dependency, confirm it provides clear value that cannot be reasonably achieved with the platform or existing libraries. Prefer maintained AndroidX/Kotlin libraries.

Dependency updates should be isolated when practical and verified with build, tests, lint, CodeQL/dependency review where relevant, and documentation updates when requirements or behavior change.

The repository currently does not commit a Gradle wrapper. Developer commands use a compatible local Gradle installation (CI pins Gradle 8.9 through `gradle/actions/setup-gradle`). If a wrapper is introduced later, document every new wrapper file in `codebase-reference.md` and update setup/CI guidance deliberately.

## Logging

Use `SafeLogger` for structured event metadata when logging is justified. Never log secrets, authentication headers, signing data, backup payloads, receipt contents, or raw sensitive user content. Keep release logging minimal and update redaction tests when new sensitive categories are introduced. See [`logging.md`](logging.md).

## Documentation changes and tracked files

Documentation is a maintained repository invariant, not a cleanup-only activity.

- Every tracked path must appear exactly once inside the marked file index in [`codebase-reference.md`](codebase-reference.md).
- Adding, deleting, or renaming a tracked file requires updating that index in the same change.
- Run `python3 scripts/check_documentation_coverage.py`; it compares the reference against `git ls-files` and rejects missing, stale, or duplicate entries.
- Use [`documentation-map.md`](documentation-map.md) to decide which permanent documents must change when behavior/architecture/testing/security/privacy/release rules change.
- Do not place volatile workflow IDs or temporary runner states throughout permanent docs; those belong in `what_changed.md`.
- Do not claim manual device/accessibility/signing/screenshot gates in source documentation unless they were actually performed.

## Quality commands

```bash
gradle testDebugUnitTest
gradle assembleDebugAndroidTest
gradle lint
gradle assembleDebug
gradle assembleRelease
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

With an emulator/device:

```bash
gradle connectedDebugAndroidTest
```

See [`testing.md`](testing.md) for what each layer proves and what still requires a real Android runtime.

## Git workflow

Prefer small Conventional Commits that isolate a coherent behavior, regression test, guard, or documentation contract. Configure the requested email in a local clone:

```bash
git config user.email "sanskarin@outlook.in"
```

Before pushing a new tracked file, update `codebase-reference.md` so the documentation guard can pass on the same commit series. When a change intentionally updates a volatile release-candidate head, expect concurrency cancellation to supersede older PR workflow runs.

`what_changed.md` is the handoff document for multi-session work. Update it after meaningful milestones, validation runs, bug fixes, or release-state changes. It supplements—not replaces—the permanent documents linked through `documentation-map.md`.
