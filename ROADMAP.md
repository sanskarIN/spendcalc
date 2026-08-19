# SpendCalc Roadmap

The roadmap prioritizes correctness, privacy, accessibility, and maintainability over feature count.

## Phase 0 — Repository foundation

- [x] Android/Kotlin/Compose build configuration.
- [x] Repository standards and policy files.
- [x] Architecture, privacy, security, and support direction.
- [ ] CI green on clean checkout.

## Phase 1 — Core calculator MVP

- [x] Precision-safe decimal arithmetic.
- [x] Itemized expenses.
- [x] Discount, tax, tip, service charge, split bill.
- [x] Manual currency exchange rate.
- [x] Receipt-style result view.
- [x] Unit coverage for finance arithmetic and rounding.

## Phase 2 — Persistence and reusable workflows

- [x] Room history.
- [x] Saved templates.
- [x] DataStore settings.
- [x] Optional history auto-delete.
- [x] CSV/text/PDF export paths.
- [x] Onboarding, appearance, accessibility, About.
- [ ] History search/filter for large datasets.
- [ ] Explicit user-driven backup/restore bundle for templates/preferences.

## Phase 3 — UX, reliability, and platform polish

- [x] Responsive phone/tablet calculator composition.
- [x] Local-first/no-account core experience.
- [x] FileProvider-based export sharing.
- [ ] Add real release screenshots from verified builds.
- [ ] Add optional receipt notes/categories without compromising simplicity.
- [ ] Add history restore/undo after delete where practical.
- [ ] Profile very large history/template collections.

## Phase 4 — Verification depth

- [x] Domain unit tests.
- [x] Repository tests.
- [x] Room integration tests.
- [x] Compose smoke test.
- [ ] Add database migration tests when schema version 2 exists.
- [ ] Add property/fuzz coverage for decimal-input parsing and CSV edge cases.
- [ ] Add macrobenchmark/profile module if measured performance warrants it.

## Phase 5 — Release engineering

- [ ] Confirm CI build/lint/test/security jobs from a clean checkout.
- [ ] Produce signed release artifacts outside source control.
- [ ] Capture final screenshots.
- [ ] Finalize 1.0.0 release notes.
- [ ] Tag `v1.0.0` after release-candidate audit.

## Phase 6 — Final audit

- [ ] Clean setup using `docs/setup.md`.
- [ ] Debug and release compilation.
- [ ] Unit, instrumentation, lint, and security checks.
- [ ] Documentation-link review.
- [ ] Accessibility manual pass with TalkBack and large font scale.
- [ ] Verify no secrets/private data are committed.
- [ ] Confirm `CHANGELOG.md` and `what_changed.md` match the repository.

Future work should only move into a release after the current milestone remains buildable and tested.
