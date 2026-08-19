# Release Candidate Verification

This checklist is used for the final source and automation audit before the first tagged release.

## Automated checks

- [ ] Repository formatting guard passes.
- [ ] Common secret-pattern guard passes.
- [ ] JVM unit tests pass.
- [ ] Android lint passes.
- [ ] Debug APK compiles.
- [ ] Release APK compiles with shrinking enabled.
- [ ] CodeQL analysis completes without a blocker finding.
- [ ] Dependency review completes for pull-request changes.

## Android checks

When an emulator/device is available:

- [ ] Room integration tests pass.
- [ ] Compose smoke test passes.
- [ ] Fresh install launches onboarding.
- [ ] Calculator, history, templates, settings, and About navigation work.
- [ ] Text, CSV, and PDF exports open the Android share sheet.
- [ ] Core calculation works with network disabled.

## Manual quality checks

- [ ] Light theme reviewed.
- [ ] Dark theme reviewed.
- [ ] Large text/system font scaling reviewed.
- [ ] TalkBack order/labels reviewed.
- [ ] Small phone layout reviewed.
- [ ] Tablet/wide layout reviewed.
- [ ] Destructive history clear confirmation reviewed.
- [ ] No private test data appears in committed assets.

## Release checks

- [ ] `README.md`, `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` match actual behavior.
- [ ] Production signing material remains outside source control.
- [ ] Release screenshots use fictional data only.
- [ ] Version code/name are correct for the intended release.
- [ ] Tag/release artifact is produced from the verified commit.
