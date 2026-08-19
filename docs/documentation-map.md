# SpendCalc Documentation Map

SpendCalc has multiple documentation audiences: end users, contributors, maintainers, security reviewers, release operators, and future continuation sessions. This map defines which document is authoritative for which question, how documents should reference each other, and what must be updated when behavior changes.

The goal is to prevent documentation drift. A longer document is not automatically more authoritative; authority follows the categories below.

## Documentation principles

1. **Source behavior wins over prose when there is a contradiction.** Treat a contradiction as a documentation or implementation bug and reconcile it before release.
2. **Permanent design documentation is separate from current-work continuity.** `what_changed.md` records current engineering state; it must not become the only explanation of an architectural rule.
3. **Release status is exact-commit based.** A configured, queued, pending, cancelled, skipped, or superseded workflow is not a successful check.
4. **Privacy and security claims are intentionally conservative.** Do not imply encryption, authentication, network isolation, or atomicity guarantees stronger than the implementation provides.
5. **Every tracked file is documented.** [`codebase-reference.md`](codebase-reference.md) is checked against `git ls-files` by `scripts/check_documentation_coverage.py`.
6. **User-visible behavior changes require changelog consideration.** Internal refactors that preserve behavior may not need a user-facing changelog entry, but permanent engineering docs still need reconciliation when contracts move.

## Public product documentation

### `README.md`

**Audience:** first-time users, contributors, repository visitors.

**Authoritative for:** concise public positioning, supported platform, high-level features, quick-start commands, project links, funding/support, and links into deeper documentation.

**Not authoritative for:** detailed parser/security invariants, exact release checklist state, or implementation ownership of every file.

### `docs/features.md`

**Audience:** users, QA, contributors.

**Authoritative for:** implemented product behavior at feature level: calculator, history, templates, export, backup/restore, settings, accessibility, onboarding, and privacy baseline.

Update it when a user-visible capability, limit, fallback, workflow, or destructive-action behavior changes.

### `PRIVACY.md`

**Audience:** users and privacy reviewers.

**Authoritative for:** what user data the app stores, where it is stored, account/network/analytics baseline, explicit backup privacy, Android-managed backup/device transfer, and external user-triggered actions.

### `SECURITY.md`

**Audience:** vulnerability reporters and maintainers.

**Authoritative for:** security-reporting process and public-disclosure expectations.

### `SUPPORT.md`

**Audience:** users and contributors.

**Authoritative for:** where to ask for help and how support differs from bug reports, feature requests, and vulnerability reports.

### `LICENSE` and `CODE_OF_CONDUCT.md`

**Audience:** all repository participants.

**Authoritative for:** license rights/conditions and community behavior expectations respectively.

## Architecture and implementation documentation

### `docs/architecture.md`

**Audience:** contributors and maintainers.

**Authoritative for:** layer boundaries, dependency direction, application composition, calculation order, persistence split, export architecture, error-handling direction, and architectural links.

Architecture changes should be reflected here even when an ADR also exists.

### `docs/codebase-reference.md`

**Audience:** contributors, reviewers, continuation sessions.

**Authoritative for:** file-by-file ownership and purpose. Every tracked path must appear exactly once.

This is the fastest answer to “what does this file own?” or “which test protects this source?” It is not a replacement for detailed behavior/security documents.

### `docs/development.md`

**Audience:** active contributors.

**Authoritative for:** day-to-day change rules, source layout, finance/UI/persistence/export/logging guidance, quality commands, dependency expectations, and Git workflow.

### `docs/design-system.md`

**Audience:** UI contributors and accessibility reviewers.

**Authoritative for:** Compose visual tokens, layout conventions, typography/theme behavior, component consistency, and design-system-level accessibility rules.

### `docs/accessibility.md`

**Audience:** UI contributors, QA, release reviewers.

**Authoritative for:** accessibility implementation choices and manual TalkBack/font-scale/motion/contrast/touch/layout checks.

### `docs/performance.md`

**Audience:** performance reviewers and maintainers.

**Authoritative for:** bounded-work decisions, off-main-thread I/O expectations, measurement-before-optimization policy, and thresholds/backlog for future profiling.

### `docs/logging.md`

**Audience:** developers and security/privacy reviewers.

**Authoritative for:** structured logging boundaries, allowed metadata, sensitive categories, and redaction expectations.

## Persistence, backup, privacy, and security documentation

### `docs/persistence-invariants.md`

**Audience:** persistence/backup contributors and reviewers.

**Authoritative for:** the contract that repository-accepted history/templates must remain exportable by the backup codec, including saved-name, identifier, timestamp, canonical currency, split, decimal magnitude, finance-setting, and duplicate-ID rules.

Repository or backup validation changes must reconcile this document and its tests together.

### `docs/backup-restore.md`

**Audience:** users, contributors, QA.

**Authoritative for:** explicit backup/restore behavior, user flow, data scope, confirmation, rollback model, and format compatibility at a functional level.

### `docs/security-backup.md`

**Audience:** security reviewers and maintainers.

**Authoritative for:** backup threat model, parser limits, checksum semantics, fail-closed validation, text/Unicode rules, numeric bounds, rollback caveats, and safe format evolution.

### `docs/privacy-backup.md`

**Audience:** privacy reviewers and users who need deeper backup detail.

**Authoritative for:** privacy differences between user-created backup files and Android-managed backup/device transfer.

## Testing and verification documentation

### `docs/testing.md`

**Audience:** developers, reviewers, QA.

**Authoritative for:** the test pyramid, unit/fuzz/instrumentation/Compose/repository guard coverage, exact commands, regression policy, migration-testing expectations, and manual test categories.

### `docs/verification.md`

**Audience:** release reviewers and maintainers.

**Authoritative for:** exact release-candidate gate status categories and the complete automated/manual checklist. This file must never claim a gate passed before the exact release commit has evidence.

### `docs/release-candidate-final-audit.md`

**Audience:** maintainers/release reviewers.

**Authoritative for:** source-level completeness audit of the current `1.0.0` candidate. Its checked source items mean “implemented/audited in source,” not “all runtime/release gates passed.”

## Build, setup, and operations documentation

### `docs/setup.md`

**Audience:** new contributors.

**Authoritative for:** local prerequisites and getting the Android project building/running.

The current repository does not commit a Gradle wrapper; commands therefore assume a compatible local Gradle installation or Android Studio-managed Gradle environment as documented.

### `docs/troubleshooting.md`

**Audience:** contributors encountering setup/build/runtime issues.

**Authoritative for:** known JDK/SDK/Gradle/KSP/Room/emulator/export/release troubleshooting paths.

### `docs/github-maintenance.md`

**Audience:** repository maintainers.

**Authoritative for:** GitHub Actions/Dependabot/templates/repository hygiene, dependency maintenance, documentation upkeep, handoff discipline, and release-check handling.

### `docs/release.md`

**Audience:** release operators/maintainers.

**Authoritative for:** versioning, exact-commit verification, tag workflow, unsigned artifact handling, external signing, screenshot requirements, and publication sequence.

## Architecture Decision Records

ADRs record durable decisions and the reason alternatives were rejected. They should be changed by superseding/amending the decision deliberately, not silently rewritten to match convenience.

- `docs/adr/0001-use-bigdecimal-for-finance.md` — finance arithmetic uses `BigDecimal`.
- `docs/adr/0002-local-first-core.md` — the core is local-first and does not require an account/network service.
- `docs/adr/0003-room-and-datastore.md` — Room stores structured records; DataStore stores preferences.
- `docs/adr/0004-versioned-local-backup.md` — explicit backups use a versioned, bounded, validated local format.

If implementation direction changes one of these decisions, add a new ADR or clearly amend/supersede the prior one and update architecture/security/privacy/testing docs in the same change.

## Release history and planning

### `CHANGELOG.md`

**Authoritative for:** notable user-visible/security/reliability changes by release state.

### `ROADMAP.md`

**Authoritative for:** planned/completed phases and explicitly open release work. It is planning/status documentation, not evidence that a check actually passed.

## Current-work continuity

### `what_changed.md`

**Audience:** continuation sessions and maintainers resuming active work.

**Authoritative for:** active branch/PR/head, recently completed work, current verification truth, known environment limitations, and exact next actions.

It should link permanent documentation rather than duplicating entire design rationales.

### `what_changed_final.md` and `what_changed_latest.md`

Compatibility files retained for older handoffs. They must point to the canonical root handoff and must not carry an independent “newer” release state.

## Documentation assets

- `docs/assets/spendcalc-logo.svg` is editable source artwork used by repository documentation.
- `docs/assets/screenshots/README.md` defines the policy for real release screenshots.
- Real screenshots are intentionally not fabricated. Capture them only from a verified build, using fictional/non-private data.

## Change-to-document matrix

| Change type | Minimum permanent docs to review |
| --- | --- |
| Finance formula/order/rounding | `architecture.md`, `features.md`, relevant ADR, `testing.md`, `CHANGELOG.md` |
| Finance validation/bounds | `features.md`, `development.md`, `testing.md`, `CHANGELOG.md` |
| History/template persistence | `architecture.md`, `persistence-invariants.md`, `testing.md`, backup docs if exportability changes |
| Backup format/parser/restore | `backup-restore.md`, `security-backup.md`, `privacy-backup.md` if data/privacy changes, ADR 0004, `testing.md`, `CHANGELOG.md` |
| Android manifest/provider/backup policy | `PRIVACY.md`, `SECURITY.md` when applicable, `security-backup.md`/`privacy-backup.md`, `testing.md`, `verification.md` |
| UI/UX workflow | `features.md`, `accessibility.md`, `design-system.md` when conventions change, `testing.md`, `CHANGELOG.md` |
| Performance/bounds/threading | `performance.md`, `development.md`, `testing.md`, `CHANGELOG.md` when user-visible |
| Logging/redaction | `logging.md`, `SECURITY.md` if reporting posture changes, `testing.md` |
| Build/dependency/toolchain | `setup.md`, `development.md`, `troubleshooting.md`, `github-maintenance.md`, `CHANGELOG.md` when release/user impact exists |
| CI/repository guard | `development.md`, `testing.md`, `github-maintenance.md`, `verification.md`, `codebase-reference.md` |
| New/renamed/deleted tracked file | `codebase-reference.md` is mandatory; nearest architecture/testing/docs should also be reviewed |
| Release status | `what_changed.md`, `verification.md`, `ROADMAP.md`, `release-candidate-final-audit.md` as applicable |

## Anti-drift rules

### Do not duplicate volatile status

Workflow run IDs, exact PR heads, queued/pending states, and transient runner conditions belong primarily in `what_changed.md`. Permanent documentation should describe the rule, not repeatedly copy volatile IDs.

### Do not hide a source-of-truth conflict

If the source, tests, README, feature docs, privacy/security docs, and release checklist disagree, resolve the disagreement. Do not add a new document that merely explains both contradictory versions.

### Do not turn manual gates into source checkboxes

A source-level audit can confirm that a screen/test/checklist exists. It cannot claim TalkBack, connected-device execution, external signing, store upload, or screenshot capture happened unless those activities actually occurred.

### Keep file-level coverage mechanical

`scripts/check_documentation_coverage.py` reads the marked file index in `codebase-reference.md` and compares it with tracked files from Git. This makes “without skipping files” an enforceable repository invariant rather than a one-time promise.
