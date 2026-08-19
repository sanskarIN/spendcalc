# Release Candidate Verification

This checklist is the source of truth for deciding whether an exact SpendCalc commit is ready to tag. A configured workflow is not counted as passed until GitHub reports success for the final commit.

## Automated pull-request checks

- [ ] Formatting guard passes.
- [ ] Kotlin namespace/package guard passes.
- [ ] Repository metadata and local Markdown-link audit passes.
- [ ] Common secret-pattern scan passes.
- [ ] JVM unit and deterministic fuzz/regression tests pass.
- [ ] Debug instrumentation tests compile with `assembleDebugAndroidTest`.
- [ ] Android lint passes.
- [ ] Debug APK compiles.
- [ ] Release APK compiles with the repository's current release configuration.
- [ ] CodeQL Java/Kotlin analysis completes without a release-blocking finding.
- [ ] Dependency review completes without a release-blocking finding.
- [ ] Repository Audit workflow passes.

## Android device/emulator checks

These require a connected Android runtime and are intentionally not claimed by a compile-only CI run.

- [ ] `connectedDebugAndroidTest` passes.
- [ ] Room history/template/backup integration tests pass.
- [ ] Compose smoke tests pass.
- [ ] Real-activity calculate -> save -> History journey passes.
- [ ] Fresh install launches onboarding.
- [ ] Calculator, History, Templates, Settings, and About navigation work.
- [ ] History search, individual delete + Undo, clear-all confirmation, and retention settings work.
- [ ] Text, CSV, and PDF exports open the expected Android share flow.
- [ ] Backup export opens the document creator; backup restore opens the document picker and requires confirmation before replacement.
- [ ] Restored history, templates, theme/accessibility preferences, and retention preference match the selected backup.
- [ ] Core calculation/history/template behavior works with network disabled.

## Accessibility and responsive-layout checks

- [ ] Light theme reviewed.
- [ ] Dark theme reviewed.
- [ ] System theme reviewed.
- [ ] App large-text preference reviewed.
- [ ] Large Android system font scale reviewed.
- [ ] Reduced-motion preference verified to remove navigation transitions.
- [ ] TalkBack order, labels, buttons, dialogs, navigation, and list actions reviewed.
- [ ] Small phone layout reviewed.
- [ ] Tablet/wide layout reviewed.
- [ ] Touch targets and destructive-action wording reviewed.

## Data, privacy, and security checks

- [ ] No private test data appears in committed assets.
- [ ] No production signing material, secrets, tokens, or local configuration are committed.
- [ ] App manifest still has no `INTERNET` permission unless a future feature explicitly requires and documents it.
- [ ] FileProvider remains non-exported and limited to the private export cache path.
- [ ] CSV formula-neutralization regression tests pass.
- [ ] Backup size/line/record/decimal/schema/checksum validation tests pass.
- [ ] SafeLogger redaction tests pass.
- [ ] Android system-managed backup/device-transfer behavior matches `PRIVACY.md`.

## Release checks

- [ ] `README.md`, `CHANGELOG.md`, `ROADMAP.md`, `docs/`, and `what_changed.md` match actual behavior.
- [ ] Version code/name are correct for the intended release.
- [ ] Production signing material remains outside source control and is supplied by the release environment only.
- [ ] Real release screenshots are captured from the verified build and use fictional data only.
- [ ] The signed artifact is produced from the exact verified commit.
- [ ] `v1.0.0` is created only after all release-blocking automated and manual items above are complete.

## Current status

The feature implementation and source-level hardening are complete on the release-candidate branch. The final pull-request automation and manual Android/accessibility/signing/screenshot gates remain open until explicitly completed; this document must not be edited to claim success before those results exist.
