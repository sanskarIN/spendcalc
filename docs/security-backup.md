# Security Notes — Backup and Restore

The explicit SpendCalc backup format is treated as untrusted input during restore.

## Current defenses

- Maximum total payload size before full parsing.
- Maximum record count.
- Maximum line, text-field, encoded-field, and decimal lengths.
- URL-safe Base64 field encoding to keep record boundaries deterministic.
- Schema-version validation.
- SHA-256 corruption-detection checksum.
- Strict enum parsing.
- Three-letter currency-code validation.
- Positive exchange-rate and split validation through the finance engine for templates.
- Decimal parsing through `BigDecimal` rather than floating point.
- Restore confirmation before replacing local data.
- Room history/template replacement inside a database transaction.
- Storage Access Framework file selection; no broad storage permission is required.

## Threat model boundaries

The SHA-256 checksum is **not** a digital signature. A malicious party who can edit a backup can also recompute its checksum. The checksum protects against accidental corruption, not hostile modification or authorship spoofing.

Backups are not encrypted by the app. Confidentiality depends on where the user stores the exported file.

The restore parser must never deserialize arbitrary Java/Kotlin objects, execute embedded content, resolve network URLs, or treat backup text as code.

## Format evolution

Future backup schema revisions must preserve bounded parsing and explicit validation. Do not silently accept unknown records or guess newer schema semantics. Add regression tests for every compatibility/security defect fixed.
