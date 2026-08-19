# SpendCalc 1.0.0 Release-Candidate Final Audit

Date: 2026-08-19

This file is the final source-audit checklist for the first public release candidate. It complements `docs/verification.md` and the root work-continuity documents.

## Source completeness

- [x] Android app module and release/debug build configuration exist.
- [x] Kotlin/Compose application entry point exists.
- [x] Domain finance engine uses `BigDecimal` rather than floating-point arithmetic.
- [x] Itemized expenses, discount, tax, tip, service charge, manual currency conversion, and split calculation are implemented.
- [x] Receipt-style output is implemented.
- [x] Local Room history and saved templates are implemented.
- [x] History retention controls are implemented.
- [x] Local history search is implemented.
- [x] Text, CSV, and PDF export paths are implemented.
- [x] Explicit local backup/restore is implemented for history, templates, and preferences.
- [x] Settings, onboarding, About, light/dark/system themes, large text, and reduced-motion preference are implemented.
- [x] Required support, GitHub, funding, license, and `Made by the Sanskar` identity are present in product/repository documentation.

## Reliability and security

- [x] Finance inputs are validated centrally.
- [x] Currency normalization is locale-stable.
- [x] CSV text cells neutralize common spreadsheet-formula prefixes.
- [x] Export files use app cache + FileProvider rather than broad storage access.
- [x] Core app does not require Android Internet permission.
- [x] Backup parser is versioned, bounded, checksum-validated, and avoids arbitrary object deserialization.
- [x] Backup restore requires confirmation.
- [x] Room backup replacement is transactional.
- [x] Common secret-pattern scanning exists.
- [x] CodeQL and dependency-review workflows exist.
- [x] Structured logging boundary redacts common sensitive field categories.

## Regression coverage

- [x] Finance arithmetic and rounding tests.
- [x] Invalid finance input tests.
- [x] Locale normalization regression test.
- [x] CSV escaping/formula-prefix tests and deterministic fuzz coverage.
- [x] Receipt formatter test.
- [x] History/template repository tests.
- [x] Backup codec round-trip, corruption, version, malformed/oversized input, and deterministic fuzz coverage.
- [x] Room round-trip and transactional replace-all instrumentation tests.
- [x] Compose calculator smoke test.
- [x] Primary calculate/save/history instrumentation journey coverage.
- [x] Kotlin reserved-namespace regression guard.
- [x] Repository required-file/local-link audit.

## Repository engineering

- [x] MIT license.
- [x] Contributor guide and code of conduct.
- [x] Security and support policies.
- [x] Privacy documentation.
- [x] Changelog, roadmap, and work-continuity documents.
- [x] Architecture/setup/development/testing/release/troubleshooting/accessibility/performance documentation.
- [x] ADR set includes precision arithmetic, local-first behavior, persistence, and backup-format decisions.
- [x] Issue and pull-request templates.
- [x] Dependabot configuration.
- [x] CI build/test/lint/static/security checks.
- [x] CodeQL workflow.
- [x] Dependency review workflow.
- [x] Release-candidate artifact workflow.
- [x] Funding configuration and Buy Me a Coffee documentation.
- [x] Editable logo source and real-screenshot policy.

## Verification boundary

Automated Android build/lint/test checks must be considered authoritative only after the configured GitHub-hosted jobs finish. If GitHub-hosted jobs are queued or unavailable, the source may be merged only with that limitation recorded; the `v1.0.0` tag must not be represented as a fully verified release until those jobs and the manual Android accessibility/export/backup checks complete.

Production signing material, store credentials, and personal test data must remain outside source control.
