# ADR 0002 — Keep core SpendCalc local-first

- Status: Accepted
- Date: 2026-08-19

## Context

Expense calculation, saved templates, history, and receipt export work naturally on the device. A remote service is not required for the main product experience.

## Decision

SpendCalc's core feature set remains usable without sign-in and without a network connection. Core persistence uses Room and DataStore in app-private storage. Manual exchange rates keep currency conversion usable offline.

## Consequences

Benefits include offline availability, simpler setup, and a smaller privacy surface. The tradeoff is that automatic cross-device synchronization and live exchange rates are not part of the core app.

Any future online feature should be optional and must not prevent offline calculation.
