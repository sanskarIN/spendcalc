# GitHub Repository Maintenance

This guide covers the repository-level controls around source changes. Permanent documentation authority is mapped in [`documentation-map.md`](documentation-map.md), while every tracked file and its purpose is indexed in [`codebase-reference.md`](codebase-reference.md).

## Default branch protection guidance

For `main`, enable repository rules/branch protection that require:

- pull requests for normal feature work;
- the `CI` quality job before merge;
- the lightweight `Repository Audit` when configured as a required check;
- CodeQL/security checks when available and applicable;
- dependency review for pull requests that alter dependencies;
- conversations/review threads resolved before merge;
- branches to be up to date before merge when the repository has multiple active contributors;
- force pushes and branch deletion disabled for the protected default branch.

Repository administrators may retain an emergency bypass for critical recovery, but bypass use should be documented afterward with the reason, affected commit, and follow-up verification.

## Workflow responsibilities

- `CI` performs the complete source/build gate: formatting, namespace, tracked-file documentation coverage, Android resources/security, repository/link audit, secret patterns, JVM tests, instrumentation compilation, lint, debug build, and release compilation.
- `Repository Audit` is intentionally lightweight and fast. It checks required files/links, exhaustive tracked-file documentation, and Android string resources without waiting for the Android build.
- `CodeQL` performs Java/Kotlin static analysis.
- `Dependency Review` evaluates dependency changes introduced by pull requests.
- `Release Candidate` runs from a release tag and produces an unsigned artifact only after its configured verification/build work.

Workflow concurrency cancels superseded pull-request revisions. A cancelled older run is expected when a newer commit is pushed; it must not be interpreted as a failure of the newer head or as successful verification.

## Documentation maintenance

Complete documentation is an enforced repository property:

1. every tracked file appears exactly once in `docs/codebase-reference.md`;
2. `scripts/check_documentation_coverage.py` compares that index with `git ls-files`;
3. adding, deleting, or renaming any tracked file therefore requires updating the index in the same pull request;
4. `scripts/check_repository.py` also requires the codebase reference, documentation map, release-critical docs, and coverage guard to exist;
5. permanent behavioral changes are reconciled according to the change-to-document matrix in `docs/documentation-map.md`.

Do not satisfy the coverage guard with a path-only placeholder. The file reference description should explain ownership and the invariant/workflow that file supports.

When generated Room schema files begin appearing under `app/schemas/`, they are tracked release/migration evidence and must receive individual entries just like hand-written source files.

## Suggested labels

- `bug` — reproducible defect
- `enhancement` — user-facing improvement
- `accessibility` — accessibility defect/improvement
- `security` — non-sensitive security maintenance; private disclosure still follows `SECURITY.md`
- `documentation` — documentation-only work
- `dependencies` — dependency updates
- `ci` — CI/release automation
- `performance` — measured performance work
- `good first issue` — small, well-scoped contributor task
- `help wanted` — maintainer welcomes contributor ownership

## Issue and pull-request templates

The repository keeps structured bug/feature forms plus a pull-request checklist under `.github/`. Maintain them alongside the engineering process:

- bug reports should request reproducible information without encouraging private data uploads;
- feature requests should prompt for real user value and privacy/accessibility impact;
- security reports must redirect away from public issue disclosure;
- PRs should include tests/docs for changed behavior and acknowledge privacy/security/release impact.

If a template file is added/renamed/deleted, update `codebase-reference.md` in the same commit series.

## Dependabot and dependencies

Dependabot checks Gradle and GitHub Actions dependencies on its configured schedule. For update pull requests:

1. read upstream release/security notes when the update is non-trivial;
2. keep unrelated dependency upgrades separate where practical;
3. run the complete CI set rather than assuming a version bump is mechanical;
4. inspect dependency review/CodeQL results where relevant;
5. update setup/development/troubleshooting/release documentation when the required toolchain changes;
6. never weaken validation or security controls solely to make an upgrade pass.

## Milestone guidance

Use milestones only when they improve planning. Suggested milestones:

- `2.0.12` — current verified production-release target
- `2.1.0` — first post-2.0.12 feature set
- `maintenance` — optional recurring fixes when a version milestone would be misleading

Every release milestone should reference its definition of done in `ROADMAP.md` and keep blocker issues visible. Application release milestones must not be treated as Room database or explicit backup schema version numbers; those compatibility versions change only when their own contracts require it.

## GitHub Discussions guidance

If Discussions are enabled, suggested categories are:

- Announcements — maintainer release/project notices
- Q&A — setup and usage questions
- Ideas — early feature discussion before an issue is justified
- Show and tell — screenshots/workflows built with fictional demo data

Do not use Discussions for vulnerability details; use the private process in `SECURITY.md`.

## Merge strategy

Prefer squash merge for small pull requests with noisy fixup history and regular merge/rebase when preserving a sequence of meaningful atomic commits improves project history. Do not rewrite already-published release tags.

For a release-candidate PR, merge only an exact head whose required automated gates are successful and whose remaining manual gates are understood. `mergeable: true` is only a Git conflict/status signal; it does not prove release readiness.

## Release hygiene

- Build release artifacts from the exact verified tagged commit.
- Keep signing material, keystore credentials, and store credentials outside the repository.
- Attach only artifacts produced by the release workflow or a documented equivalent build.
- Capture release screenshots from the verified build using fictional data only; never fabricate screenshots to satisfy documentation.
- Record user-visible/security/reliability changes in `CHANGELOG.md`.
- Reconcile `README.md`, `ROADMAP.md`, `docs/verification.md`, and permanent docs before tagging.
- Keep `what_changed.md` synchronized with the latest exact-head verification state during active finalization.
- Do not describe queued, pending, skipped, cancelled, or superseded runs as successful.

## Multi-session continuation

`what_changed.md` is the canonical current handoff. Keep volatile PR head/check state there instead of copying workflow IDs throughout permanent documentation. `what_changed_final.md` and `what_changed_latest.md` are compatibility pointers, not independent current-state sources.

A continuation should first verify the current PR head and exact-head checks. If checks are merely waiting, avoid speculative source churn that continually cancels runners; documentation or code changes should be driven by concrete remaining work or verified audit gaps.
