# Privacy — Backup Paths

SpendCalc has two distinct backup/transfer paths. They should not be confused.

## Explicit user-driven backup

The **Settings > Backup and restore** controls use Android's Storage Access Framework. SpendCalc asks the system picker for a destination when exporting and for a document when restoring. The app does not require broad storage permission and does not upload the selected file itself.

The explicit backup contains saved history summaries, templates, and preferences. It can contain user-entered labels and financial values. It is not encrypted by SpendCalc, so the user controls confidentiality by choosing where the file is stored and who can access that location.

Restore is destructive replacement and therefore requires an explicit confirmation dialog before current saved data is replaced.

## Android system-managed backup/device transfer

The Android manifest enables platform backup. The repository's backup/data-extraction rules limit the included app data to the Room database and DataStore directory. Depending on Android version, device configuration, account settings, OEM behavior, and user backup settings, Android may copy that private app data through its system backup/device-transfer mechanism.

This path is controlled by Android rather than by a SpendCalc account or SpendCalc server.

## Network behavior

The current application manifest does not request Android's `INTERNET` permission. Core calculation, history, templates, settings, explicit backup encoding/decoding, and receipt generation are local operations.

Opening repository/funding links or composing an email hands an intent to another installed application. What that external application does is governed by that application's permissions and privacy terms.

## Cache exports

CSV/PDF export files are created inside the app-private cache export directory and shared through a non-exported `FileProvider` with temporary read permission. They are not part of the explicit backup format. Android may clear cache files at any time.

## Maintainer rule

Any future feature that adds direct networking, telemetry, analytics, cloud sync, authentication, or a SpendCalc-operated backend must update the manifest, privacy/security documentation, UI disclosure, tests, and release review before shipping.
