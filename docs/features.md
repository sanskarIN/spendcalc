# Product Features

This document records the implemented product behavior that must remain covered by tests and release verification.

## Calculator

- Itemized expense lines with decimal amounts.
- Precision-safe `BigDecimal` subtotal and charge arithmetic.
- Discount applied to subtotal before tax, tip, and service charge.
- Tax, tip, and service charge percentages calculated from the discounted base.
- Split-bill calculation with explicit half-up monetary rounding.
- Manual positive exchange-rate conversion between validated three-letter currency codes.
- Receipt-style source and converted totals.

## History

- Explicit save-to-history action.
- Room-backed local persistence.
- Newest-first presentation.
- Local search by label, source/converted currency code, and source/converted total.
- Per-entry deletion.
- Confirmed clear-all action.
- Optional automatic retention of never, 30 days, or 90 days.

## Templates

- Save reusable discount, tax, tip, service-charge, split, currency, and exchange-rate settings.
- Load a template into the active calculator.
- Delete templates locally.

## Export

- Plain-text receipt share.
- CSV export with quoting and neutralization of common spreadsheet-formula prefixes in text cells.
- Offline PDF receipt generation.
- Android share intents using app-cache files and a non-exported `FileProvider`.

## Backup and restore

- Explicit local backup document creation from Settings.
- Backup contains history, templates, and preferences.
- Versioned format with bounded parsing and a SHA-256 corruption-detection checksum.
- User-controlled Android document destination; no cloud account or required network service.
- Restore confirmation before replacing saved data.
- Room history/template replacement performed transactionally.
- Unsupported or corrupted backup files are rejected.

See [`backup-restore.md`](backup-restore.md) for the format and privacy model.

## Appearance and accessibility

- System, light, and dark themes.
- System-bar icon contrast adjusted with theme.
- Large-text preference.
- Reduced-motion preference; the current UI intentionally avoids decorative animation/fake delays.
- Material semantic roles for radio/toggle controls.
- Numeric/decimal keyboard hints for finance inputs.
- Scrollable small-screen layouts and a two-column calculator/receipt presentation on wider screens.
- Externalized user-visible strings for localization readiness.

## Onboarding, settings, and About

- First-run onboarding with local/offline defaults.
- Settings for appearance, accessibility, privacy/retention, backup/restore, repository updates, and About.
- About includes application version, MIT license, GitHub/repository, support/business email actions, Buy Me a Coffee, and **Made by the Sanskar**.

## Privacy baseline

The core product does not require an account, remote API key, analytics SDK, advertising SDK, or Android Internet permission. External URLs/email applications open only after explicit user actions.
