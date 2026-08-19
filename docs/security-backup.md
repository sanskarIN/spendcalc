# Security Notes — Backup and Restore

The explicit SpendCalc backup format is treated as untrusted input during restore.

## Current defenses

- Maximum total payload size is checked while reading and again before decoding.
- Newline count is bounded before `split()` so a small-but-line-dense input cannot create an excessive record list.
- Record count, line length, text length, encoded-field length, identifier length, saved-name length, and decimal text length are bounded.
- Numeric fields accept bounded plain-decimal shapes only; exponent forms that could expand dramatically through `toPlainString()` are rejected.
- History monetary fields must be non-negative, scale 0 through 12, and within the bounded 34-integer-digit result range that covers the calculator's legitimate worst-case output.
- Split counts are bounded to 1 through 1,000,000.
- History/template identifiers must be nonblank, no longer than 128 characters, and unique inside their record type.
- History labels/template names must be nonblank and no longer than the application's 120-character saved-name limit.
- New saved names use a shared UTF-16-safe truncation policy that never splits a valid surrogate pair at the 120-character boundary; malformed surrogate input still fails closed.
- Valid decoded/restored saved names are preserved exactly rather than being silently trimmed or rewritten during repository replacement.
- Timestamps must be non-negative.
- URL-safe Base64 field encoding keeps tabs/newlines in text from changing record boundaries.
- Decoded text must be valid UTF-8 and re-encode to the exact original bytes; malformed UTF-8 fails closed.
- Exported text must also round trip through UTF-8 exactly, so malformed Unicode surrogate data is rejected instead of normalized.
- Schema version is explicit and unknown versions are rejected.
- SHA-256 checksum text is strictly shaped as 64 hexadecimal characters and compared with `MessageDigest.isEqual`.
- Enums and three-letter currency codes are parsed explicitly.
- Template finance fields are revalidated through `CalculatorEngine`.
- Restore requires an explicit Android UI confirmation before current data is replaced.
- Active backup/restore application work is modal so saved-data mutations cannot race database replacement.
- History/templates are captured under one Room transaction and replaced under one Room transaction.
- Batch DAO inserts are used during replacement to avoid thousands of independent write calls.
- Settings are stored separately in DataStore; restore therefore snapshots the existing state first and performs compensating rollback of both Room and DataStore if the multi-store operation throws.
- Backup file reading/writing is performed on `Dispatchers.IO`; bounded checksum/Base64/parser CPU work is performed on `Dispatchers.Default` rather than blocking the UI thread.
- Android Storage Access Framework selection is used; no broad storage permission is required.

## Threat model boundaries

The SHA-256 checksum is **not** a digital signature or MAC. A malicious party who can edit a backup can recompute the checksum. It detects accidental corruption and malformed/tampered content that has not been recomputed; it does not prove authorship.

Backups are not encrypted by SpendCalc. Confidentiality depends on the destination selected by the user and the security of the device/account/provider storing that file.

Restore intentionally does not deserialize arbitrary Java/Kotlin objects, execute embedded content, resolve network URLs, or treat backup text as code.

## Failure semantics

A decode/validation failure does not enter the repository restore path. A database replacement failure rolls back through Room's transaction. If a later DataStore write fails, SpendCalc attempts a non-cancellable compensating restore of the pre-restore Room and preference snapshot, then reports failure to the UI.

This is a defensive best-effort atomicity strategy across two independent persistence technologies; it is not equivalent to one database transaction spanning both systems.

## Format evolution

Future backup schema revisions must preserve bounded parsing, explicit validation, no code execution, exact text encoding semantics, and fail-closed handling for unknown records/versions. Compatibility changes require targeted regression tests before the new schema is accepted.
