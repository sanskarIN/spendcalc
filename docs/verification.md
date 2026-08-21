# Release Candidate Verification

This checklist is the source of truth for deciding whether an exact SpendCalc commit is ready to tag. A configured workflow is not counted as passed until GitHub reports an acceptable successful conclusion for the final commit. Source completeness, automated checks, connected-emulator checks, manual Android checks, signing, and screenshots are distinct evidence classes.

The current application release target is **2.0.12** with Android `versionCode` **20012**. Room database and explicit backup schema versions remain separate compatibility dimensions.

## Automated pull-request checks

- [ ] Formatting guard passes.
- [ ] Kotlin namespace/package guard passes.
- [ ] Tracked-file documentation coverage guard passes: every `git ls-files` path appears exactly once in `docs/codebase-reference.md`, with no stale entries.
- [ ] Android default string-resource reference/duplicate-name audit passes.
- [ ] Android local-first manifest/FileProvider security audit passes.
- [ ] Repository required-file/metadata/local Markdown-link audit passes, including the documentation index, Android build guide, command reference, exhaustive codebase reference, documentation map, and documentation-coverage guard.
- [ ] Repository audit derives application `versionName`/`versionCode` from `app/build.gradle.kts`, confirms the build/index/command documents match them, and finds no stale semantic-version name in signed-APK examples.
- [ ] Common secret-pattern scan passes.
- [ ] JVM unit and deterministic fuzz/regression tests pass, including UTF-16 saved-name boundary coverage.
- [ ] Persistence-invariant tests pass for history/template IDs, timestamps, names, currencies, split/result bounds, template finance settings, and duplicate replacement IDs.
- [ ] Backup codec rejects invalid persisted-record envelopes, noncanonical in-memory backup records, and checksum-valid decoded records whose persisted currency text is not canonical.
- [ ] Strict backup byte-decoder regression rejects malformed/unmappable UTF-8 rather than replacing invalid bytes.
- [ ] PDF line-truncation regression preserves valid UTF-16 when a supplementary Unicode character crosses the ellipsis boundary.
- [ ] Repository replace-all tests prove invalid or duplicate candidates are rejected before existing data is replaced.
- [ ] Debug instrumentation tests compile with `assembleDebugAndroidTest`.
- [ ] Full Android lint passes.
- [ ] Debug APK compiles.
- [ ] Release APK compiles with the repository's current release configuration.
- [ ] CodeQL Java/Kotlin analysis completes without a release-blocking finding.
- [ ] Dependency review completes without a release-blocking finding.
- [ ] Repository Audit workflow passes, including documentation coverage, required-file/link/release-metadata consistency, Android string-resource checks, and the Android local-first security guard.

## Automated connected Android runtime checks

These checks execute the real Android instrumentation suite instead of only compiling it. The `Android Instrumentation` workflow uses a hardware-accelerated API 35 `google_apis` x86_64 emulator and must succeed for the exact release-candidate commit.

- [ ] Android Instrumentation workflow completes successfully for the exact final commit.
- [ ] `connectedDebugAndroidTest` passes on the automated API 35 emulator.
- [ ] Room history/template/backup integration tests pass on the emulator.
- [ ] Compose calculator, named-history-save/Unicode-boundary dialog, template-name/Unicode-boundary dialog, History label-filter, and Settings busy-state tests pass on the emulator.
- [ ] Real-activity calculate -> named save -> History journey passes on the emulator and verifies both the saved label and amount.
- [ ] Failed connected-test runs preserve Android instrumentation reports as workflow artifacts for diagnosis.

Automated emulator execution strengthens runtime evidence but does not replace the manual accessibility, layout, system-picker/share, splash, offline, or representative physical-device checks below.

## Documentation consistency checks

These are source/repository checks and do not replace runtime/manual verification.

- [ ] `docs/README.md` is the current documentation index and links the build, command, setup, testing, security/privacy, and release paths without creating a competing source of truth.
- [ ] `docs/android-build-guide.md` identifies the current `2.0.12` / `versionCode 20012` candidate, uses current signed-artifact examples, and keeps application versioning separate from Room/backup schema compatibility.
- [ ] `docs/command-reference.md` identifies the current release metadata where relevant and documents every repository guard command used by the engineering workflow.
- [ ] `docs/codebase-reference.md` describes every tracked root/configuration/GitHub/build/source/test/resource/script/policy/documentation file exactly once.
- [ ] `docs/documentation-map.md` correctly identifies the authoritative document for public behavior, architecture, build/commands, persistence/backup/security/privacy, testing, maintenance, release, and active-work continuity.
- [ ] New/renamed/deleted tracked files have matching codebase-reference changes in the same release candidate.
- [ ] `README.md` and `docs/features.md` describe implemented behavior and limits rather than roadmap-only work.
- [ ] `docs/architecture.md`, `docs/development.md`, and `docs/testing.md` agree on dependency boundaries, persistence validation, build tooling, repository guards, and quality commands.
- [ ] `docs/persistence-invariants.md`, `docs/security-backup.md`, and backup/repository tests agree on the persisted-record contract, strict UTF-8 document decoding, and canonical persisted-currency behavior.
- [ ] `PRIVACY.md`, `docs/privacy-backup.md`, manifest backup/data-extraction XML, and explicit-backup docs do not contradict each other.
- [ ] `CHANGELOG.md`, `ROADMAP.md`, `docs/release.md`, and this checklist all identify `2.0.12` as the current target without treating the app version as a Room/backup schema migration.
- [ ] `what_changed_final.md` and `what_changed_latest.md` remain compatibility pointers to canonical `what_changed.md`, not independent/current release-state documents.
- [ ] No permanent document falsely promotes a queued/pending/cancelled/superseded workflow to successful verification.

## Android device/emulator manual checks

These are human/representative-runtime release checks. Automated API 35 instrumentation success does not by itself satisfy them.

- [ ] Re-run `connectedDebugAndroidTest` on a representative local emulator or physical device.
- [ ] Fresh install shows the branded SpendCalc launch splash and then onboarding.
- [ ] Returning install does not show a false onboarding flash while preferences load.
- [ ] Calculator, History, Templates, Settings, and About navigation work.
- [ ] Saving with a meaningful history label stores that label and History search finds the entry by it; blank labels fall back to `Calculation`.
- [ ] Pasting a Unicode-heavy saved name near the 120-character boundary does not leave malformed text, and the resulting record can be backed up and restored successfully.
- [ ] History search, individual delete + Undo, clear-all confirmation, and retention settings work.
- [ ] History search stops at the documented 120-character limit without splitting a valid surrogate pair.
- [ ] Template save/load/delete + Undo work, including a Unicode-heavy template name near the saved-name boundary.
- [ ] Template save dialog explains the 120-character name limit and its `Save` confirmation is distinct from the underlying `Save template` action.
- [ ] Calculator stops at 100 editable line items, disables Add item, and explains the limit.
- [ ] Text, CSV, and PDF exports open the expected Android share flow.
- [ ] PDF export with a long Unicode item name near the truncation boundary renders without malformed/dangling surrogate text.
- [ ] Exported cache files remain shareable only through the intended FileProvider flow.
- [ ] Backup export opens the document creator; backup restore opens the document picker and requires confirmation before replacement.
- [ ] Backup progress is visible during real work and duplicate backup actions remain disabled until completion.
- [ ] Restored history, including exact accepted history labels, templates, theme/accessibility preferences, and retention preference match the selected backup.
- [ ] A deliberately malformed UTF-8 backup document is rejected without replacing current data.
- [ ] A checksum-valid backup edited to use noncanonical persisted currency text is rejected without replacing current data.
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
- [ ] Validation/error meaning remains understandable without color alone.
- [ ] Small phone layout reviewed.
- [ ] Tablet/wide layout reviewed.
- [ ] Touch targets and destructive-action wording reviewed.

## Data, privacy, and security checks

- [ ] No private test data appears in committed assets or screenshots.
- [ ] No production signing material, secrets, tokens, or local configuration are committed.
- [ ] `.env.example` remains non-secret documentation; core operation still requires no remote API key.
- [ ] App manifest still has no `INTERNET` permission unless a future feature explicitly requires and documents it.
- [ ] FileProvider remains non-exported and limited to the private export cache path.
- [ ] Canonical path-containment regression tests pass.
- [ ] CSV formula-neutralization regression/fuzz tests pass.
- [ ] Backup size/line/record/text/decimal/schema/checksum validation tests pass.
- [ ] Backup document bytes are decoded with malformed/unmappable UTF-8 configured to report/fail closed.
- [ ] Backup decode does not uppercase/repair noncanonical persisted currency text before structural validation.
- [ ] Saved history/template names produced through normal save operations stay within the shared 120-character contract and remain well-formed UTF-16.
- [ ] Valid accepted history/template names entering restore/replace paths are preserved exactly rather than silently trimmed or rewritten.
- [ ] Persisted record IDs are nonblank, bounded, valid Unicode, and unique inside replacement collections.
- [ ] Persisted timestamps are nonnegative and stored currencies are canonical uppercase three-letter values.
- [ ] Stored history results remain nonnegative and inside the supported saved-result shape/split bounds.
- [ ] Template finance settings are validated through `CalculatorEngine` even when repository callers bypass the ViewModel.
- [ ] Invalid or duplicate replacement records fail before DAO replacement and do not erase existing data.
- [ ] Backup encoding applies the same persisted-record envelope rules as repositories instead of silently canonicalizing malformed in-memory backup objects.
- [ ] Malformed Unicode saved names fail closed, while a valid emoji crossing the saved-name boundary is truncated safely and still round-trips through backup encoding/decoding.
- [ ] SafeLogger redaction tests pass, including locale-independent sensitive-key normalization.
- [ ] Android system-managed backup/device-transfer behavior matches `PRIVACY.md` and `docs/privacy-backup.md`.
- [ ] Explicit backup documentation correctly states that the SHA-256 checksum detects accidental corruption but is not a signature/MAC/authorship proof.

## Release checks

- [ ] `README.md`, `docs/README.md`, `CHANGELOG.md`, `ROADMAP.md`, permanent `docs/`, and `what_changed.md` match actual behavior/status.
- [ ] `docs/android-build-guide.md` and `docs/command-reference.md` match `app/build.gradle.kts` release metadata and use 2.0.12 signed-artifact examples where a versioned filename is shown.
- [ ] `docs/codebase-reference.md` and `docs/documentation-map.md` are current for the exact release commit.
- [ ] `docs/persistence-invariants.md` matches the repository and backup codec implementation.
- [ ] `app/build.gradle.kts` contains `versionName = "2.0.12"` and `versionCode = 20012`.
- [ ] Room database version and backup schema version were changed only if a real compatibility change required them; the app version bump alone is not such a reason.
- [ ] Any committed Room schema history matches the database version/migration contract; future schema files are individually documented in the file reference.
- [ ] Production signing material remains outside source control and is supplied by the release environment only.
- [ ] Real release screenshots are captured from the verified build and use fictional data only.
- [ ] The signed artifact is produced from the exact verified/tagged commit.
- [ ] Built artifact inspection confirms application ID `in.sanskar.spendcalc`, `versionName 2.0.12`, `versionCode 20012`, expected SDK metadata, and intended permissions before distribution.
- [ ] The signed artifact installs and reports `2.0.12` in About.
- [ ] Artifact checksum/source-SHA relationship is recorded/verified.
- [ ] `v2.0.12` is created only after all release-blocking automated and manual items above are complete.

## Current status

The feature implementation, persistence/export hardening, final platform fixes, complete Android build/command documentation, release-document drift guards, deep source-level documentation work, and API 35 connected-emulator workflow are present on the release-candidate branch. This checklist intentionally leaves boxes unchecked until the exact final commit's corresponding GitHub runs or real manual/distribution activities provide the required evidence. Automated emulator success is not a substitute for manual accessibility, layout, picker/share, screenshot, signing, or artifact verification.
