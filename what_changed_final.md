# SpendCalc — Final Current Handoff

Date: 2026-08-19

The detailed release-candidate implementation and verification handoff is recorded in:

- `what_changed.md` — primary continuity file;
- `what_changed_latest.md` — expanded release-candidate work performed after the original checkpoint;
- `docs/release-candidate-final-audit.md` — source-completeness and release audit;
- `docs/verification.md` — automated/manual release checklist.

## Current implementation state

The repository contains the Android Kotlin/Jetpack Compose SpendCalc implementation, precision-safe finance domain, Room/DataStore persistence, history/templates, local search, text/CSV/PDF export, explicit local backup/restore, settings/onboarding/About UI, accessibility/theme support, automated tests, CI/security workflows, repository templates, and documentation required by the project plan.

Release signing secrets and store credentials intentionally remain outside source control. Real release screenshots must be captured from a verified Android build using fictional data.

## Verification rule

Do not describe `v1.0.0` as fully verified until the configured Android CI jobs and the manual device/emulator accessibility/export/backup checks have completed successfully. If GitHub-hosted jobs are queued or temporarily unavailable, preserve that exact limitation in the handoff rather than claiming a green build.

## Commit identity

Repository work for this project uses the requested commit email `sanskarin@outlook.in` where the connected GitHub operation permits author metadata.
