# SpendCalc — Work Continuity

## Current milestone

- Phase 0 / Phase 1 bootstrap started on 2026-08-19.
- Repository: `sanskarIN/spendcalc`
- Target: Android, Kotlin, Jetpack Compose, offline-first.

## Source prompt analyzed

The repository is being implemented against `15_spendcalc_master_prompt.md`. The required core is an offline expense calculator with precision-safe decimal arithmetic, itemized expenses, tax/discount/tip/service-charge/split calculations, manual currency conversion, templates, receipt-style results, history with optional auto-delete, export architecture, accessibility, tests, polished settings/about UI, security/privacy documentation, CI, and release engineering.

## Implementation plan

1. Bootstrap Gradle/Android/Kotlin/Compose configuration and repository policy files.
2. Implement precision-safe domain models, validation, rounding, and calculation engine.
3. Implement Room persistence for history/templates and DataStore preferences.
4. Wire repositories and application container.
5. Implement Compose navigation, calculator, history, templates, settings, and about screens.
6. Add CSV/text export abstractions and Android share integration.
7. Add unit/instrumentation tests, lint/format/static-analysis CI, CodeQL, dependency updates, and release workflow.
8. Complete README and documentation set, then audit repository state and CI.

## Completed work

- Created the continuity handoff file and recorded the implementation sequence.

## Files/modules added or changed

- `what_changed.md`

## Tests added

- None yet.

## Commands/checks run

- Confirmed the GitHub repository exists, is public, uses `main`, and the connected GitHub account has admin/push permission.

## Known limitations

- The connected GitHub contents API creates commits as the authenticated GitHub identity and does not expose an author-email field. The requested commit email `sanskarin@outlook.in` therefore cannot be forced through this connector. Repository documentation records the requested local Git identity so contributors can configure it when cloning locally.
- Android build verification has not yet run because project scaffolding is still being created.

## Open issues

- None filed yet; implementation is in progress.

## Next exact tasks

- Add Gradle settings and root build configuration.
- Add Android app module configuration and manifest.
- Add domain calculation models and engine with tests.

## Migration notes

- Initial repository; no migrations yet.

## Release notes draft

### Unreleased

- Initial SpendCalc implementation in progress.

## Recent meaningful commits

- This file is the first project bootstrap commit. Subsequent commit hashes/messages will be appended after implementation work.