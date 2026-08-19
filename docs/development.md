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

## Finance changes

All money arithmetic belongs in `CalculatorEngine` or another domain type that uses `BigDecimal`. Never convert a monetary value to `Float` or `Double` for calculations.

When changing calculation behavior:

1. document the intended order/rule;
2. add or update a failing unit test first;
3. implement the smallest domain change;
4. verify rounding at boundary cases;
5. update `CHANGELOG.md` when user-visible behavior changes.

## UI changes

- Keep strings under `app/src/main/res/values/`.
- Use `SpendCalcTokens` for common spacing/radius/touch-target decisions where applicable.
- Check both phone and wide/tablet layouts.
- Test light, dark, system, and large-text modes.
- Do not hide required meaning behind color alone.
- Prefer existing Material components and semantic roles.

## Persistence changes

Room database version begins at 1. For any schema change after release:

1. increment the Room version;
2. add an explicit migration;
3. preserve exported schemas;
4. add migration tests;
5. document compatibility in the changelog.

Do not enable destructive migration as a shortcut for production schema evolution.

## Export changes

Platform-independent export serialization implements `ExportFormatter`. Android-only file/PDF/share work stays in `platform/`.

CSV changes must continue to escape quotes and defend text cells against spreadsheet formula interpretation.

## Dependencies

Before adding a dependency, confirm it provides clear value that cannot be reasonably achieved with the platform or existing libraries. Prefer maintained AndroidX/Kotlin libraries.

Dependency updates should be isolated when practical and verified with build, tests, and lint.

## Logging

The current app does not require verbose persistent logging. If structured logging is introduced:

- never log secrets, authentication headers, signing data, or raw sensitive user content;
- redact calculation labels/content by default;
- keep release logging minimal;
- document each log category and retention behavior.

## Quality commands

```bash
gradle testDebugUnitTest
gradle lintDebug
gradle assembleDebug
gradle assembleRelease
```

With an emulator/device:

```bash
gradle connectedDebugAndroidTest
```

## Git workflow

Prefer small Conventional Commits. Configure the requested email in a local clone:

```bash
git config user.email "sanskarin@outlook.in"
```

`what_changed.md` is the handoff document for multi-session work. Update it after meaningful milestones, validation runs, bug fixes, or release-state changes.
