# Security Notes — Backup and Restore

The explicit SpendCalc backup format is treated as untrusted input during restore. The local persistence repositories are also treated as validation boundaries so a caller that bypasses UI/ViewModel code cannot manufacture records that later fail backup export.

## Current defenses

- Maximum total payload size is checked while reading and again before decoding.
- Backup document bytes are decoded with a strict UTF-8 `CharsetDecoder` configured with `CodingErrorAction.REPORT`, so malformed or unmappable byte sequences fail instead of being silently replaced before parser validation.
- Newline count is bounded before `split()` so a small-but-line-dense input cannot create an excessive record list.
- Record count, line length, text length, encoded-field length, identifier length, saved-name length, and decimal text length are bounded.
- Numeric fields accept bounded plain-decimal shapes only; exponent forms that could expand dramatically through `toPlainString()` are rejected.
- History monetary fields must be non-negative, scale 0 through 12, and within the bounded 34-integer-digit result range that covers the calculator's legitimate worst-case output.
- Split counts are bounded to 1 through 1,000,000.
- History/template identifiers must be nonblank, no longer than 128 characters, and valid Unicode; duplicate IDs inside a backup record type are rejected.
- History labels/template names must be nonblank and no longer than the application's 120-character saved-name limit.
- New saved names use a shared UTF-16-safe truncation policy that never splits a valid surrogate pair at the 120-character boundary; malformed surrogate input still fails closed.
- Valid decoded/restored saved names are preserved exactly rather than being silently trimmed or rewritten during repository replacement.
- Timestamps must be non-negative.
- Persisted currency codes are canonical uppercase three-letter values. Repository writes normalize valid user/caller currency forms before persistence, while backup encoding requires the already-persisted canonical form and backup decoding validates the decoded form without first uppercasing or otherwise repairing it.
- The history repository rejects negative/out-of-contract stored results, invalid split counts, bad identifiers, or invalid timestamps before any DAO write.
- The template repository revalidates the exact settings it persists through `CalculatorEngine`, even when a caller bypasses `SpendCalcViewModel`.
- Template repository restore/replace also validates identifier/timestamp/name/canonical-currency envelope fields before DAO writes.
- Repository `replaceAll` paths map and validate every supplied record before invoking replacement, so one invalid candidate cannot clear valid existing data first.
- The backup codec reuses the same persisted-record envelope policy as repositories, reducing the chance that local storage and export rules drift apart.
- URL-safe Base64 field encoding keeps tabs/newlines in text from changing record boundaries.
- Decoded Base64 text fields must be valid UTF-8 and re-encode to the exact original bytes; malformed UTF-8 fails closed.
- Exported text must also round trip through UTF-8 exactly, so malformed Unicode surrogate data is rejected instead of normalized.
- Schema version is explicit and unknown versions are rejected.
- SHA-256 checksum text is strictly shaped as 64 hexadecimal characters and compared with `MessageDigest.isEqual`.
- Enums and three-letter currency codes are parsed explicitly.
- Template finance fields are revalidated through `CalculatorEngine` during backup validation as well as repository persistence.
- Restore requires an explicit Android UI confirmation before current data is replaced.
- Active backup/restore application work is modal so saved-data mutations cannot race database replacement.
- History/templates are captured under one Room transaction and replaced under one Room transaction.
- Batch DAO inserts are used during replacement to avoid thousands of independent write calls.
- Settings are stored separately in DataStore; restore therefore snapshots the existing state first and performs compensating rollback of both Room and DataStore if the multi-store operation throws.
- Backup file reading/writing is performed on `Dispatchers.IO`; bounded checksum/Base64/parser CPU work is performed on `Dispatchers.Default` rather than blocking the UI thread.
- Android Storage Access Framework selection is used; no broad storage permission is required.

## Persistence and backup invariant

A valid record produced by normal repository operations should be acceptable to explicit backup encoding without requiring a second cleanup pass. To preserve that invariant:

1. new user-entered names are normalized before persistence;
2. currencies are canonicalized before persistence;
3. record identifiers, timestamps, names, splits, and history result shapes are checked against the persisted-record policy;
4. template finance settings are checked through `CalculatorEngine`;
5. accepted restore records are validated before DAO replacement;
6. backup encoding applies the same persisted-record envelope checks again and fails closed if an in-memory object bypassed repository rules.

This layered validation intentionally duplicates the *enforcement point* without duplicating the underlying structural rules.

## Threat model boundaries

The SHA-256 checksum is **not** a digital signature or MAC. A malicious party who can edit a backup can recompute the checksum. It detects accidental corruption and malformed/tampered content that has not been recomputed; it does not prove authorship.

Backups are not encrypted by SpendCalc. Confidentiality depends on the destination selected by the user and the security of the device/account/provider storing that file.

Restore intentionally does not deserialize arbitrary Java/Kotlin objects, execute embedded content, resolve network URLs, or treat backup text as code.

## Failure semantics

A document-decoding, backup decoding, or validation failure does not enter the repository restore path. Repository prevalidation rejects an invalid replacement collection before its DAO replacement method is invoked. A database replacement failure rolls back through Room's transaction. If a later DataStore write fails, SpendCalc attempts a non-cancellable compensating restore of the pre-restore Room and preference snapshot, then reports failure to the UI.

This is a defensive best-effort atomicity strategy across two independent persistence technologies; it is not equivalent to one database transaction spanning both systems.

## Format evolution

Future backup schema revisions must preserve bounded parsing, explicit validation, shared persistence/export invariants, no code execution, exact text encoding semantics, and fail-closed handling for unknown records/versions. Compatibility changes require targeted regression tests before the new schema is accepted.
