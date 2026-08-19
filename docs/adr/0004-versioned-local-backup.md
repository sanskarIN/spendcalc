# ADR 0004 — Use a versioned bounded local backup format

- Status: Accepted
- Date: 2026-08-19

## Context

SpendCalc needs explicit user-driven backup/restore without requiring an account, backend, cloud API, or broad storage permission. Backup files are untrusted input during restore and may contain arbitrary Unicode user labels.

Using Java/Kotlin object serialization would couple files to implementation details and introduces avoidable deserialization risk. A remote sync protocol would contradict the local-first core requirement.

## Decision

Use a small deterministic text format with:

- a `SPENDCALC_BACKUP` magic header;
- an explicit integer schema version;
- an export timestamp;
- typed preference/history/template records;
- URL-safe Base64 encoding for text fields;
- decimal values represented by their plain decimal strings;
- maximum payload/record/line/field lengths;
- a SHA-256 checksum over the canonical body.

Android's Storage Access Framework chooses export/import document locations. The app requests no broad storage permission.

Restore accepts only known schema versions and validates values before replacing local records.

## Consequences

### Positive

- Offline and account-free.
- Human-debuggable structure without allowing raw text to break record boundaries.
- Deterministic decimal persistence.
- Bounded parser resource use.
- Easy to version and regression test.
- Avoids arbitrary object deserialization.

### Negative

- The current format is not encrypted.
- The checksum detects accidental corruption but does not authenticate the file.
- Room and DataStore cannot participate in one cross-storage transaction.
- Future schema revisions require explicit compatibility work.

## Rejected alternatives

- Java/Kotlin object serialization: too implementation-coupled and unnecessary for untrusted input.
- Mandatory cloud synchronization: violates the offline-first product baseline.
- SQLite database-file copying: ties restore to internal database layout and complicates preference inclusion/version compatibility.

## Security rule

Do not add executable content, reflection-based object reconstruction, network references, or unbounded fields to the backup format.
