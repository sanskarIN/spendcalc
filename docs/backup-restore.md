# Backup and Restore

SpendCalc supports an explicit, offline backup file for local history, saved templates, and application preferences.

## Export

Open **Settings → Backup & restore → Export backup**. Android's document picker lets you choose where the file is stored. SpendCalc does not upload the backup to a server.

The backup file uses the `.spendcalc` extension and contains:

- saved calculation history;
- saved calculation templates;
- theme, accessibility, retention, and onboarding preferences;
- a schema version;
- a SHA-256 checksum for accidental-corruption detection.

Text values are encoded so tabs, line breaks, and Unicode cannot change record boundaries. The checksum is an integrity check, not an authenticity signature or encryption mechanism. Anyone who can read the file can potentially read the backed-up data, so store and share it accordingly.

## Restore

Open **Settings → Backup & restore → Restore backup**. SpendCalc asks for confirmation because restoration replaces the current saved history, templates, and settings with the selected backup.

Before restoration, SpendCalc validates:

- file size and record-count limits;
- format and schema version;
- SHA-256 checksum;
- encoded field boundaries;
- enum values;
- currency-code format;
- split counts, percentages, and exchange rates for templates;
- decimal parsing.

History and template replacement occur inside one Room database transaction. Preferences are then updated in DataStore. Because Room and DataStore are separate storage engines, there is no single cross-storage transaction spanning both systems; a rare preference-write failure after a successful database transaction is reported as a restore failure and should be retried from the same backup.

## Compatibility

The initial backup schema is version `1`. Future format changes must either remain backward compatible or add an explicit decoder/migration path. Unsupported future/older schemas must be rejected rather than guessed.

## Privacy

A backup leaves SpendCalc's private app sandbox only because the user explicitly chooses a document destination. Treat the resulting file as potentially sensitive if calculation labels or saved history contain information you would not want others to see.

No account, cloud service, network connection, or API key is required for backup or restore.
