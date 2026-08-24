# Privacy

_Last updated: 2026-08-19_

SpendCalc is designed to perform its core functions locally on the Android device.

## Data SpendCalc stores

When you choose to save information, SpendCalc can store:

- calculation history summaries, including an optional history label you enter when saving a result;
- saved calculation templates;
- app preferences such as theme, large text, reduced motion, history-retention choice, and onboarding completion.

The current application does not require an account, analytics SDK, advertising SDK, remote database, or cloud API for its core features.

## Storage locations

- History and templates: private Android app database using Room/SQLite.
- Preferences: Android DataStore in private app storage.
- Temporary exports: the app's cache `exports/` directory.

Android's normal application sandbox applies. Temporary export files are shared through a non-exported `FileProvider` and receive temporary read permission only when the user invokes a share action.

## Network behavior

Core calculations, history, templates, settings, CSV export, text receipt export, and PDF receipt creation are offline-capable. SpendCalc does not request the Android `INTERNET` permission in the current manifest.

Links on the About/Settings screens can open external apps or websites only after the user chooses them. Those external destinations have their own privacy practices.

## Backups

The Android manifest permits system-managed backup/device transfer of the local database and preference files when the device and operating-system backup settings allow it. Users control platform-level backup availability through Android/device settings.

Explicit user-created SpendCalc backups include saved history labels because those labels are part of the local history records. A user-created backup leaves app-private storage only when you choose a destination through Android's document picker.

## Data deletion

Users can delete individual history records, clear all history, delete templates, and configure history auto-deletion after 30 or 90 days. Uninstalling the app normally removes its private local storage subject to Android backup/restore behavior.

## Sensitive information

SpendCalc is a general expense-calculation utility. Avoid entering information you do not want stored in calculation labels or exported files. Exported/shared data leaves the app sandbox when you explicitly share it with another application.

## Contact

Privacy/support questions:

- `supportramsandesh@gmail.com`
- `sanskarin@outlook.in`
- `sanskarin.business@gmail.com`

Repository: https://github.com/sanskarIN/spendcalc

**Made by the Sanskar**
