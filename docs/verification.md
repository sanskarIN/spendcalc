# Release Candidate Verification

This checklist is the source of truth for deciding whether an exact SpendCalc commit is ready to tag. A configured workflow is not counted as passed until GitHub reports success for the final commit.

## Automated pull-request checks

- [ ] Formatting guard passes.
- [ ] Kotlin namespace/package guard passes.
- [ ] Android default string-resource reference/duplicate-name audit passes.
- [ ] Android local-first manifest/FileProvider security audit passes.
- [ ] Repository metadata and local Markdown-link audit passes.
- [ ] Common secret-pattern scan passes.
- [ ] JVM unit and deterministic fuzz/regression tests pass, including UTF-16 saved-name boundary coverage.
- [ ] Persistence-invariant tests pass for history/template IDs, timestamps, names, currencies, split/result bounds, template finance settings, and duplicate replacement IDs.
- [ ] Backup codec rejects invalid persisted-record envelopes and noncanonical in-memory backup records.
- [ ] Repository replace-all tests prove invalid or duplicate candidates are rejected before existing data is replaced.
- [ ] Debug instrumentation tests compile with `assembleDebugAndroidTest`.
- [ ] Full Android lint passes.
- [ ] Debug APK compiles.
- [ ] Release APK compiles with the repository's current release configuration.
- [ ] CodeQL Java/Kotlin analysis completes without a release-blocking finding.
- [ ] Dependency review completes without a release-blocking finding.
- [ ] Repository Audit workflow passes, including the Android string-resource guard and required persistence-invariant documentation.

## Android device/emulator checks

These require a connected Android runtime and are intentionally not claimed by a compile-only CI run.

- [ ] `connectedDebugAndroidTest` passes.
- [ ] Room history/template/backup integration tests pass.
- [ ] Compose calculator, named-history-save/Unicode-boundary dialog, template-name/Unicode-boundary dialog, History label-filter, and Settings busy-state tests pass.
- [ ] Real-activity calculate -> named save -> History journey passes and verifies both the saved label and amount.
- [ ] Fresh install shows the branded SpendCalc launch splash and then onboarding.
- [ ] Calculator, History, Templates, Settings, and About navigation work.
- [ ] Saving with a meaningful history label stores that label and History search finds the entry by it; blank labels fall back to `Calculation`.
- [ ] Pasting a Unicode-heavy saved name near the 120-character boundary does not leave malformed text, and the resulting record can be backed up and restored successfully.
- [ ] History search, individual delete + Undo, clear-all confirmation, and retention settings work.
- [ ] History search stops at the documented 120-character limit without splitting a valid surrogate pair.
- [ ] Template save/load/delete + Undo work, including a Unicode-heavy template name near the saved-name boundary.
- [ ] Template save dialog explains the 120-character name limit and its `Save` confirmation is distinct from the underlying `Save template` action.
- [ ] Calculator stops at 100 editable line items, disables Add item, and explains the limit.
- [ ] Text, CSV, and PDF exports open the expected Android share flow.
- [ ] Backup export opens the document creator; backup restore opens the document picker and requires confirmation before replacement.
- [ ] Backup progress is visible during real work and duplicate backup actions remain disabled until completion.
- [ ] Restored history, including exact accepted history labels, templates, theme/accessibility preferences, and retention preference match the selected backup.
- [ ] Core calculation/history/template behavior works with network disabled.

## Accessibility and responsive-layout checks

- [ ] Light theme reviewed.
- [ ] Dark theme reviewed.
- [ ] System theme reviewed.
- [ ] App large-text preference reviewed.
- [ ] Large Android system font scale reviewed, including named-history and template save dialogs.
- [ ] Reduced-motion preference verified to remove navigation transitions.
- [ ] TalkBack order, labels, buttons, dialogs, navigation, progress state, and list actions reviewed.
- [ ] Named-history save dialog title, optional label field, supporting text, Save, and Cancel are announced in a logical order.
- [ ] Template save dialog title, name field, length guidance, Save, and Cancel are announced in a logical order without duplicate `Save template` ambiguity.
- [ ] Primary navigation destinations are announced once rather than duplicating icon + label names.
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
- [ ] Saved history/template names produced through normal save operations stay within the shared 120-character contract and remain well-formed UTF-16.
- [ ] Valid accepted history/template names entering restore/replace paths are preserved exactly rather than silently trimmed or rewritten.
- [ ] Persisted record IDs are nonblank, bounded, valid Unicode, and unique inside replacement collections.
- [ ] Persisted timestamps are nonnegative and stored currencies are canonical uppercase three-letter values.
- [ ] Stored history results remain nonnegative and inside the supported saved-result shape/split bounds.
- [ ] Template finance settings are validated through `CalculatorEngine` even when repository callers bypass the ViewModel.
- [ ] Invalid or duplicate replacement records fail before DAO replacement and do not erase existing data.
- [ ] Backup encoding applies the same persisted-record envelope rules as repositories instead of silently canonicalizing malformed in-memory backup objects.
- [ ] Malformed Unicode saved names fail closed, while a valid emoji crossing the saved-name boundary is truncated safely and still round-trips through backup encoding/decoding.
- [ ] SafeLogger redaction tests pass.
- [ ] Android system-managed backup/device-transfer behavior matches `PRIVACY.md`.

## Release checks

- [ ] `README.md`, `CHANGELOG.md`, `ROADMAP.md`, `docs/`, and `what_changed.md` match actual behavior.
- [ ] `docs/persistence-invariants.md` matches the repository and backup codec implementation.
- [ ] Version code/name are correct for the intended release.
- [ ] Production signing material remains outside source control and is supplied by the release environment only.
- [ ] Real release screenshots are captured from the verified build and use fictional data only.
- [ ] The signed artifact is produced from the exact verified commit.
- [ ] `v1.0.0` is created only after all release-blocking automated and manual items above are complete.

## Current status

The feature implementation and source-level hardening are complete on the release-candidate branch. The final pull-request automation and manual Android/accessibility/signing/screenshot gates remain open until explicitly completed; this document must not be edited to claim success before those results exist.
