# Persistence Invariants

SpendCalc treats repositories as validation boundaries, not passive DAO wrappers. This document defines the contract between domain objects, Room persistence, and explicit backup/export validation.

## Core invariant

> Data accepted by normal repository persistence should also satisfy the structural rules required by explicit backup encoding.

This prevents a local record from being accepted into app storage and only failing later when the user attempts to create a backup.

## Shared policy

`domain/model/SavedRecordPolicy.kt` defines structural rules shared by history/template repositories and backup validation.

### Identifiers

- Must be nonblank.
- Must be at most 128 UTF-16 code units.
- Must be well-formed UTF-16.
- Batch replacement lists must not contain duplicate IDs within the same record type.

Single-record `restore` may intentionally use an existing ID because Undo restores the deleted record through an upsert. Duplicate rejection therefore belongs to replacement collections rather than single-record restore.

### Creation timestamps

- Must be zero or positive.
- A negative injected/test clock value is rejected before persistence.

### Saved names

History labels and template names use `SavedNamePolicy.kt`:

- Maximum length: 120 UTF-16 code units.
- New user input is trimmed and safely truncated.
- Blank new input uses the stable fallback (`Calculation` or `Template`).
- Truncation does not split a valid surrogate pair.
- Malformed UTF-16 fails closed.
- An already-valid restored name is preserved exactly, including intentional surrounding whitespace.

### Currency codes

Persisted currency codes are canonical three-letter uppercase values such as `INR` or `USD`.

Repository save/restore paths normalize otherwise valid caller input with `trim().uppercase(Locale.ROOT)` before persistence. The backup codec expects persisted/in-memory backup records to already be canonical and rejects noncanonical objects during encoding instead of silently changing backup semantics. During decoding, the exact decoded currency text is validated before repository restore; lowercase or whitespace-padded backup currency values are rejected rather than silently normalized.

### History records

Stored history summaries must additionally satisfy:

- split count from 1 through 1,000,000;
- nonnegative subtotal, discount, tax, tip, service charge, total, converted total, per-person, and converted-per-person values;
- stored numeric scale from 0 through 12;
- at most 34 integer digits for stored result fields.

History does not contain original line-item inputs after save, so restore cannot reconstruct and re-evaluate the full original calculation formula. Structural/result-shape validation is therefore the appropriate persistence boundary.

### Template records

Templates persist settings rather than expense line items. Repository validation intentionally validates only settings that are actually stored:

- discount;
- tax;
- tip;
- service charge;
- split count;
- base currency;
- exchange rate;
- converted currency.

These settings are revalidated using the same `CalculatorEngine` rules used by the calculator. Line items in a `CalculationInput` passed directly to `TemplateRepository.save` are ignored for validation because templates deliberately do not persist them.

## Replacement semantics

`HistoryRepository.replaceAll` and `TemplateRepository.replaceAll` follow this order:

1. reject duplicate IDs;
2. map and validate every supplied record into an entity list;
3. only after the entire list succeeds, call the DAO replacement method.

This ordering matters. An invalid candidate cannot clear valid existing data before the validation failure is discovered.

Room's database-level replacement transaction still provides the final database atomicity boundary during backup restore.

## Backup relationship

`BackupCodec` validates backup records independently because in-memory backup objects can be constructed without repository calls. The codec reuses shared persisted-record structural predicates and additionally enforces backup-format limits, checksum rules, schema compatibility, field encoding, record counts, and template finance validation.

Duplicate IDs are rejected by both encode/decode logic and repository batch replacement. Backup document bytes are also decoded strictly as UTF-8 before codec parsing, so malformed byte sequences cannot be repaired into a different payload before structural checks.

The same layered rule is intentional:

- **shared policy** defines what a structurally valid persisted record is;
- **repository validation** protects local storage;
- **backup validation** protects export/import boundaries;
- **Room transaction + compensating DataStore rollback** protects multi-store restore behavior.

## Regression expectations

Changes to any persisted field must update all affected layers in one pull request:

1. domain model;
2. shared policy;
3. repository mapping/validation;
4. backup codec format/validation if the field is exported;
5. repository unit tests;
6. backup unit tests;
7. Room migration/schema tests when the database version changes;
8. documentation and changelog.

A bug that allows locally persisted data to become un-backupable should be treated as a release-blocking persistence-contract defect.
