# Product Features

This document records implemented product behavior that must remain aligned with tests, privacy/security documentation, and release verification. It describes what the current application does; roadmap ideas are not presented here as shipped features.

## Calculator

- Itemized expense lines with names and decimal amounts.
- Up to 100 editable expense items per calculation. At the limit, **Add item** is disabled and the UI explains why.
- Precision-safe `BigDecimal` subtotal and charge arithmetic; finance calculations do not use binary floating-point values.
- Discount applied to subtotal before tax, tip, and service charge.
- Discount is bounded to 0–100%, preventing a valid discount from making the discounted base negative.
- Tax, tip, and service charge percentages are calculated from the discounted base and validated against bounded percentage shapes/ranges.
- Monetary/exchange-rate inputs are bounded by text length, precision, scale, and integer-digit rules so pathological decimal expansion is rejected before calculation/export.
- Split-bill calculation supports 1 through 1,000,000 people with explicit half-up monetary rounding.
- Manual positive exchange-rate conversion between validated three-letter currency codes.
- Currency input is normalized deterministically with `Locale.ROOT` before calculation/persistence where normalization is part of the input contract.
- Receipt-style source and converted totals plus per-person values.
- Invalid form/domain input does not produce a result and is explained with text in addition to error styling.

## History

- Explicit **Save result** action after a valid calculation exists.
- Save flow opens a named-history dialog. The history label is optional; a blank/whitespace-only label safely falls back to `Calculation`.
- New history labels are normalized and bounded to the shared 120-character saved-name contract.
- UI truncation is UTF-16 safe: a valid surrogate pair such as an emoji is never split at the 120-character boundary.
- Room-backed local persistence with exact decimal-string storage.
- Newest-first presentation.
- Local search by label, source/converted currency code, totals, and per-person values.
- Search input is bounded to 120 characters and uses the same surrogate-safe truncation helper.
- Per-entry deletion with Snackbar **Undo** recovery.
- Confirmed clear-all action for destructive bulk deletion.
- Optional automatic retention of never, 30 days, or 90 days.
- Valid history records entering restore/replacement keep accepted label text exactly rather than being silently trimmed again.
- Repository validation rejects invalid persisted-record envelopes and duplicate IDs in batch replacement before current data is replaced.

## Templates

- Save reusable discount, tax, tip, service-charge, split, currency, and exchange-rate settings.
- Template names use the same 120-character, UTF-16-safe saved-name policy as history labels; blank names safely fall back to `Template`.
- The save dialog explains the name bound and uses an unambiguous `Save` confirmation action.
- Template persistence revalidates the settings that are actually stored through the same `CalculatorEngine` validation rules used by the product rather than trusting only the ViewModel caller.
- Line items are intentionally not stored in templates and therefore do not participate in template-setting validation.
- Load a template into the active calculator.
- Delete templates locally with Snackbar **Undo** recovery.
- Valid restored template names are preserved exactly.
- Invalid IDs, timestamps, names, currencies, finance settings, or duplicate IDs in a batch fail before replacement can mutate existing template data.

## Export

- Plain-text receipt sharing.
- CSV export with standards-oriented quoting and neutralization of common spreadsheet-formula prefixes in text cells.
- Offline PDF receipt generation using Android `PdfDocument`.
- Android share intents use app-private `cache/exports` files and a non-exported `FileProvider` with temporary read access.
- Canonical path containment prevents export sharing from escaping the intended cache subtree through string-prefix/sibling-path mistakes.
- Blocking CSV/PDF/file/document work is dispatched away from the main UI thread.

## Backup and restore

- Explicit local backup document creation from Settings.
- Backup contains history, templates, and user preferences.
- User chooses the destination/source through Android's Storage Access Framework; no broad storage permission, cloud account, or required network service is needed.
- Backup UI exposes real busy/progress state and disables duplicate backup/restore actions while work is active.
- Versioned deterministic format with URL-safe Base64 text fields, bounded parsing, and a SHA-256 accidental-corruption checksum.
- Backup parser validates schema/version, payload/line/record sizes, checksum shape/value, identifiers, timestamps, saved names, canonical currencies, split counts, decimal shapes/magnitudes, template finance settings, Unicode/UTF-8 text, and duplicate IDs.
- Repository persistence and backup serialization share the same saved-record/name contract so application-created local data is not allowed to drift into a state the backup codec cannot export.
- Valid decoded history labels/template names are restored exactly instead of being silently rewritten.
- Restore confirmation is required before replacing saved data.
- Room history/template snapshot/replacement is transactional and uses batch DAO operations.
- Preferences live in DataStore, so the multi-store restore takes a pre-restore snapshot and attempts non-cancellable compensating rollback if a later cross-store step throws.
- Unsupported, malformed, over-limit, checksum-invalid, or semantically invalid backup files fail closed before repository replacement.

See [`backup-restore.md`](backup-restore.md), [`persistence-invariants.md`](persistence-invariants.md), [`security-backup.md`](security-backup.md), and [`privacy-backup.md`](privacy-backup.md).

## Appearance and accessibility

- System, light, and dark themes.
- System-bar icon contrast adjusted with theme.
- App large-text preference while Android system font scaling remains effective.
- Reduced-motion preference removes navigation transitions rather than inserting fake delays.
- Primary navigation uses repository-owned vector icons plus persistent text labels; decorative icon descriptions are suppressed to avoid duplicate screen-reader announcements.
- Material semantic roles for radio/toggle controls.
- Numeric/decimal keyboard hints for finance inputs.
- Validation uses explanatory text rather than color alone.
- Named-history/template dialogs use titled/labeled fields, visible length guidance, concise confirmation actions, and separate cancel paths.
- Backup activity is represented with both progress UI and visible explanatory text.
- Scrollable small-screen layouts and a two-column calculator/receipt presentation on wider screens.
- Externalized user-visible strings for localization readiness.

See [`accessibility.md`](accessibility.md) and [`design-system.md`](design-system.md).

## Onboarding, settings, and About

- First-run onboarding centered on the local/offline/privacy model.
- Returning users stay on the AndroidX launch splash until stored preferences load, preventing a false onboarding flash.
- Settings for appearance, accessibility, history retention, explicit backup/restore, privacy information, repository updates, and About.
- About includes application version, MIT license, GitHub/repository, support/business email actions, Buy Me a Coffee, and **Made by the Sanskar**.
- Repository/funding/email actions occur only after explicit user interaction.

## Privacy baseline

The core product does not require an account, remote API key, analytics SDK, advertising SDK, or Android Internet permission. Calculation, local history/templates, explicit backup serialization, and receipt generation remain available without network connectivity. Android system-managed backup/device transfer is a separate OS-managed mechanism documented in [`PRIVACY.md`](../PRIVACY.md) and [`privacy-backup.md`](privacy-backup.md).

## Documentation and verification behavior

- Every tracked repository file is documented in [`codebase-reference.md`](codebase-reference.md).
- [`documentation-map.md`](documentation-map.md) defines which documents are authoritative for product behavior, architecture, persistence/security, testing, release state, and active-work continuity.
- CI fails when tracked files are missing from the exhaustive codebase reference, stale paths remain documented, or a path appears more than once.
- A configured or queued workflow is never described as a successful release gate; exact-commit automated and manual release requirements are maintained in [`verification.md`](verification.md).
