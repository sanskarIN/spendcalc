# GitHub Repository Maintenance

## Default branch protection guidance

For `main`, enable repository rules/branch protection that require:

- pull requests for normal feature work;
- the `CI` quality job before merge;
- CodeQL/security checks when available;
- conversations/review threads resolved before merge;
- branches to be up to date before merge when the repository has multiple active contributors;
- force pushes and branch deletion disabled for the protected default branch.

Repository administrators may retain an emergency bypass for critical recovery, but bypass use should be documented afterward.

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

## Milestone guidance

Use milestones only when they improve planning. Suggested milestones:

- `1.0.0` — first verified production-ready release
- `1.1.0` — first post-release feature set
- `maintenance` — optional recurring fixes when a version milestone would be misleading

Every release milestone should reference its definition of done in `ROADMAP.md` and keep blocker issues visible.

## GitHub Discussions guidance

If Discussions are enabled, suggested categories are:

- Announcements — maintainer release/project notices
- Q&A — setup and usage questions
- Ideas — early feature discussion before an issue is justified
- Show and tell — screenshots/workflows built with fictional demo data

Do not use Discussions for vulnerability details; use the private process in `SECURITY.md`.

## Merge strategy

Prefer squash merge for small pull requests with noisy fixup history and regular merge/rebase when preserving a sequence of meaningful atomic commits improves project history. Do not rewrite already-published release tags.

## Release hygiene

- Build release artifacts from a tagged verified commit.
- Keep signing material outside the repository.
- Attach only artifacts produced by the release workflow or a documented equivalent build.
- Record user-visible changes in `CHANGELOG.md`.
- Keep `what_changed.md` synchronized with the latest verification state.
