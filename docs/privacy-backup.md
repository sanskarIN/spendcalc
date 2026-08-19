# Privacy Addendum — Explicit Backup Files

This document supplements [`../PRIVACY.md`](../PRIVACY.md) for the explicit user-driven backup/restore feature.

## Data included

A SpendCalc backup can contain:

- calculation history, including user-entered labels and monetary results;
- saved calculation templates;
- theme, accessibility, retention, and onboarding preferences.

## When data leaves app-private storage

SpendCalc creates a backup only after the user selects **Export backup** and then chooses a destination through Android's system document picker. The app does not automatically upload the backup and does not require an account or network connection.

Once exported, the backup is outside SpendCalc's private app sandbox. Its privacy then depends on the destination and any application or service the user chooses to use with that file.

## Confidentiality

The current backup format is not encrypted. Its SHA-256 checksum is for accidental-corruption detection, not secrecy or proof of who created the file. Anyone with access to the file may be able to inspect its contents.

## Restore

Restoring is explicit and requires confirmation because current local history, templates, and preferences are replaced by the selected backup. SpendCalc validates format, limits, schema, and checksum before accepting the file.

## Deletion

Deleting app history/templates does not automatically delete backup files that the user previously exported to another location. Those files must be removed from their chosen destination separately.
