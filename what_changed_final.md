# SpendCalc — Final Current Handoff

Date: 2026-08-19

The canonical engineering and continuation record is [`what_changed.md`](what_changed.md). Use it together with [`docs/verification.md`](docs/verification.md) and the current GitHub pull-request checks.

`what_changed_latest.md` is now only a compatibility pointer and must not be treated as a newer source than the root work log.

The source implementation includes the Android Kotlin/Jetpack Compose application, precision-safe bounded finance engine, Room/DataStore persistence, searchable/undoable history, templates, text/CSV/PDF exports, explicit local backup/restore, preferences/accessibility behavior, automated tests/fuzz regressions, CI/security automation, and repository documentation.

The active application release target is **2.0.12** with Android `versionCode` **20012**. Room database and explicit backup schema versions remain independent compatibility versions and are not changed solely to mirror the app release number.

Do not describe `v2.0.12` as fully verified until the exact release commit has successful automated checks plus the documented manual connected-device, accessibility, export/restore, screenshot, and external-signing gates. Production signing material and store credentials intentionally remain outside source control.
