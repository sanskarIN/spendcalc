# SpendCalc — Final Current Handoff

Date: 2026-08-24

The canonical engineering and continuation record is [`what_changed.md`](what_changed.md). Use it together with [`docs/verification.md`](docs/verification.md) and the current GitHub pull-request/workflow checks.

`what_changed_latest.md` is only a compatibility pointer and must not be treated as a newer source than the root work log.

The source implementation includes the Android Kotlin/Jetpack Compose application, precision-safe bounded finance engine, Room/DataStore persistence, searchable/undoable history, templates, text/CSV/PDF exports, explicit local backup/restore, preferences/accessibility behavior, automated tests/fuzz regressions, CI/security automation, and repository documentation.

The active application release target is **2.15.4** with Android `versionCode` **21504**. Room database and explicit backup schema versions remain **1** because compatibility versions are independent and must not be changed solely to mirror the app release number.

The earlier activity-journey instrumentation failure caused by requiring `INR 25.00` to be unique in the Compose semantics tree has been corrected: the test now requires at least one valid amount node while preserving the saved-history-name assertion.

PR `#12` on `complete/v1-finalization` is the active release-candidate path and is retargeted to 2.15.4. Do not merge/tag/publish `v2.15.4` as fully verified until the exact final commit has successful CI, CodeQL, Dependency Review, Repository Audit, Android Instrumentation, and the documented manual connected-device, accessibility, export/restore, offline, screenshot, signing, and artifact-verification gates.

Production signing material and store credentials intentionally remain outside source control. Major dependency upgrades and browser-extension implementation remain isolated post-release work unless a specific dependency change becomes necessary to resolve a release blocker.
