# ADR 0001 — Use BigDecimal for finance arithmetic

- Status: Accepted
- Date: 2026-08-19

## Context

SpendCalc handles decimal currency values, percentages, manual exchange rates, and split-bill rounding. Binary floating-point values such as `Double` cannot exactly represent many ordinary decimal fractions and can create surprising money totals.

## Decision

Use `java.math.BigDecimal` for domain money, percentage, rate, and intermediate arithmetic.

Inputs are parsed from decimal text. Persistence stores decimal values as plain strings. `RoundingPolicy` centralizes the final money scale and rounding mode.

The initial policy uses:

- money scale: 2;
- intermediate scale: 12;
- rounding mode: `HALF_UP`.

## Consequences

### Positive

- Predictable decimal behavior.
- Explicit rounding.
- Tests can assert exact decimal results.
- Persistence avoids binary floating-point drift.

### Negative

- More verbose than primitive numeric arithmetic.
- Domain code must avoid accidental conversions to `Double`/`Float`.
- Some currencies use non-two-decimal conventions; future support may require currency-specific scale configuration.

## Guardrail

Any change that introduces floating-point finance arithmetic must be rejected unless it is strictly non-financial presentation code and cannot affect calculated values.
