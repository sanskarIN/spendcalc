# Backup and Restore

SpendCalc provides an explicit user-driven local backup format for calculation history, templates, and application preferences. This is separate from Android's optional system-managed app backup/device-transfer mechanism.

## Export a backup

1. Open **Settings**.
2. In **Backup and restore**, choose **Export backup**.
3. Android opens the system document creator.
4. Choose a destination and file name.
5. SpendCalc captures history/templates under one Room transaction, reads preferences, encodes the versioned backup away from the UI thread, and writes it to the selected URI on an I/O dispatcher.

The suggested name is `spendcalc-backup.spendcalc`. The file is not encrypted by SpendCalc, so choose a storage location appropriate for the sensitivity of your saved expense labels/history.

While active backup work is applying, SpendCalc displays a modal progress state. This prevents history/template/settings actions from racing a restore or being silently overwritten underneath it.

## Restore a backup

1. Open **Settings**.
2. Choose **Restore backup**.
3. Select a SpendCalc backup through Android's document picker.
4. SpendCalc reads the file on an I/O dispatcher and parses/validates it on a background CPU dispatcher without broad storage permission.
5. Review the destructive replacement warning.
6. Choose **Restore** only when you want the selected file to replace current saved history, templates, and preferences.

Malformed, corrupted, unsupported, oversized, or semantically invalid records are rejected before repository replacement begins.

## What is included

- calculation history summaries, including the optional label saved with each history record;
- calculation templates;
- theme preference;
- large-text preference;
- reduced-motion preference;
- history auto-delete preference;
- onboarding-completed preference.

Transient calculator form edits, cache exports, app binaries, signing credentials, and external files are not part of the explicit backup.

## Integrity and format

`BackupCodec` uses a versioned line-oriented format. User text fields are URL-safe Base64 encoded so tabs/newlines cannot change record boundaries. Text must round trip through valid UTF-8; malformed byte sequences and malformed Unicode strings fail closed rather than being normalized to replacement characters. The payload ends with a SHA-256 checksum.

The checksum is corruption detection, not authentication. A party capable of editing the file can compute a new checksum. Do not treat a valid checksum as proof that the file came from SpendCalc or from a trusted person.

The decoder enforces payload/line/record/field limits, strict checksum shape, schema version, unique IDs, timestamps, currencies, split limits, bounded plain-decimal numeric fields, and template finance validation. Imported history labels/template names follow the same 120-character saved-name limit used by the app, and imported IDs are capped at 128 characters.

Newly entered saved names are truncated with a UTF-16-safe boundary helper so a valid surrogate pair such as an emoji is never cut in half at the 120-character boundary. Malformed Unicode supplied programmatically still fails closed. A decoded backup record that is already valid is restored exactly; repository replacement validates its saved name rather than trimming or silently rewriting it.

History result decimals allow at most 34 integer digits and scale 0 through 12. The 34-digit result bound is deliberate: it accepts the bounded worst-case output that the supported 100-item calculator, charge ranges, and exchange-rate range can legitimately produce while still rejecting arbitrary huge values.

## Replacement and rollback behavior

History and templates are captured and replaced under Room transactions. Restore uses batch inserts to avoid issuing one DAO write call for every imported record. Preferences live in DataStore and cannot participate in the Room transaction, so SpendCalc snapshots the previous complete state first. If the cross-store restore throws after database replacement, it makes a non-cancellable best-effort compensating restore of the prior Room and DataStore state before surfacing failure.

Valid saved labels/template names are preserved exactly during replacement, including intentional surrounding whitespace present in an accepted backup. Name normalization is reserved for new user-entered saves; restore validates instead of mutating already-valid records.

The UI does not claim success until the restore repository returns successfully. A restore that changes the history-retention preference subsequently applies that restored policy through the normal settings observer.

## Compatibility rule

The current explicit backup schema is version 1. Unknown schema versions fail closed. Future format changes must be accompanied by compatibility/security tests and must not silently reinterpret unknown records.

## Android system backup

The manifest also permits Android's system-managed backup/device-transfer behavior for the private Room database and DataStore files when enabled by the device/OS. See `PRIVACY.md` and `docs/privacy-backup.md` for that separate path.
