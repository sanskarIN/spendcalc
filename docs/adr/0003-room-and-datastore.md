# ADR 0003 — Use Room for records and DataStore for preferences

- Status: Accepted
- Date: 2026-08-19

## Context

SpendCalc persists two different classes of data: structured lists of history/templates and small application preferences.

## Decision

Use Room for calculation history and templates. Use Preferences DataStore for theme, accessibility, retention, and onboarding settings.

Room schemas are versioned and destructive migration fallback is not enabled. Decimal finance values are stored as plain strings and mapped back to `BigDecimal`.

## Consequences

Room provides typed queries, transactional database behavior, migration support, and observable flows. DataStore provides asynchronous preference persistence without introducing a database table for simple settings.

Future Room schema changes require explicit migrations and migration tests after a released schema exists.
