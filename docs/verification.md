# Release Candidate Verification

This checklist is the source of truth for deciding whether an exact SpendCalc commit is ready to tag and publish.

The current application release target is **2.15.4** with Android `versionCode` **21504**. The Room database version and explicit backup schema version remain **1** because those compatibility versions are independent from the application release number.

A configured workflow is not counted as passed until GitHub reports a successful conclusion for the exact final release-candidate commit. Source review, automated CI, connected Android execution, manual runtime checks, accessibility review, signing, screenshots, and final artifact inspection are separate evidence classes.

---

## Exact-head rule

- [ ] Record the exact candidate commit SHA.
- [ ] Confirm the candidate branch is not behind its intended base branch.
- [ ] Confirm no newer commit exists after the workflow evidence being used.
- [ ] If any source/documentation/version commit is added, discard older runs as final release proof and verify the new exact head.
- [ ] Tag only the exact commit that passed every blocking gate.

---

## Release metadata

- [ ] `app/build.gradle.kts` contains `versionName = "2.15.4"`.
- [ ] `app/build.gradle.kts` contains `versionCode = 21504`.
- [ ] Application ID remains `in.sanskar.spendcalc`.
- [ ] `minSdk` remains 26 unless an intentional compatibility decision changes it.
- [ ] `targetSdk`/`compileSdk` remain 35 unless an intentional platform upgrade changes them.
- [ ] Room schema remains version 1 unless a real database compatibility change requires a migration.
- [ ] Explicit backup schema remains version 1 unless a real serialized-format compatibility change requires it.
- [ ] Documentation index/build guide/command reference agree on 2.15.4 / 21504.
- [ ] No stale versioned signed-APK example is presented as the current candidate.

---

## Repository guard checks

Run from the repository root:

```bash
python3 scripts/check_format.py
python3 scripts/check_kotlin_namespace.py
python3 scripts/check_documentation_coverage.py
python3 scripts/check_android_resources.py
python3 scripts/check_android_security.py
python3 scripts/check_repository.py
python3 scripts/scan_secrets.py
```

Required evidence:

- [ ] Formatting/text-hygiene guard passes.
- [ ] Kotlin namespace/package guard passes.
- [ ] Tracked-file documentation coverage passes with no missing/stale/duplicate inventory entries.
- [ ] Android default string-resource/reference audit passes.
- [ ] Android local-first manifest/FileProvider security guard passes.
- [ ] Repository required-file/metadata/local-link/release-version audit passes.
- [ ] Common secret-pattern scan passes.

---

## JVM/unit/regression coverage

Run:

```bash
gradle --no-daemon testDebugUnitTest
```

Verify:

- [ ] Finance engine arithmetic/rounding/validation tests pass.
- [ ] Deterministic finance fuzz/regression tests pass.
- [ ] History repository tests pass.
- [ ] Template repository tests pass.
- [ ] Settings/state feedback tests pass.
- [ ] Backup codec/validation/corruption tests pass.
- [ ] Persistence invariant/duplicate-ID tests pass.
- [ ] Saved-name Unicode-boundary tests pass.
- [ ] Strict malformed/unmappable UTF-8 backup input tests pass.
- [ ] Noncanonical persisted-currency rejection tests pass.
- [ ] CSV formula-neutralization tests pass.
- [ ] PDF Unicode truncation regressions pass.
- [ ] Path-containment and SafeLogger redaction tests pass.

---

## Android test compilation and lint

Run:

```bash
gradle --no-daemon assembleDebugAndroidTest
gradle --no-daemon lint
```

Verify:

- [ ] Instrumentation test APK compiles.
- [ ] Full Android lint passes.
- [ ] No new release-blocking lint warning is ignored without rationale.

---

## Debug/release compilation

Run:

```bash
gradle --no-daemon assembleDebug
gradle --no-daemon assembleRelease
gradle --no-daemon bundleRelease
```

Verify:

- [ ] Debug APK compiles.
- [ ] Release APK compiles under the current shrinking/minification configuration.
- [ ] Release AAB compiles.
- [ ] Generated artifacts are taken from the exact candidate source SHA.

---

## GitHub Actions gates

For the exact final head require:

- [ ] CI succeeds.
- [ ] CodeQL succeeds without a release-blocking finding.
- [ ] Dependency Review succeeds without a release-blocking finding.
- [ ] Repository Audit succeeds.
- [ ] Android Instrumentation succeeds.

Do not use an older successful commit as final release evidence after the branch advances.

---

## Connected Android instrumentation

The automated `Android Instrumentation` workflow runs an API 35 Google APIs x86_64 emulator.

Required evidence:

- [ ] Emulator boots successfully.
- [ ] `connectedDebugAndroidTest` completes successfully.
- [ ] Room history/template/backup integration tests pass.
- [ ] Calculator Compose tests pass.
- [ ] History search/filter tests pass.
- [ ] Settings busy/progress state tests pass.
- [ ] Named-history save dialog tests pass.
- [ ] Template-name dialog tests pass.
- [ ] Unicode-boundary dialog cases pass.
- [ ] Real-activity calculate → named save → History journey passes.
- [ ] The journey verifies both the saved name and expected amount without assuming amount text appears in exactly one semantics node.
- [ ] Failed runs, if any, preserve instrumentation reports for diagnosis.

Automated emulator success does not replace manual representative-device/accessibility/system-picker/share checks.

---

## Manual Android smoke checks

- [ ] Re-run connected tests on a representative local emulator or physical device.
- [ ] Fresh install shows the branded splash and onboarding.
- [ ] Returning install avoids a false onboarding flash while preferences load.
- [ ] Calculator navigation works.
- [ ] History navigation works.
- [ ] Templates navigation works.
- [ ] Settings navigation works.
- [ ] About navigation works.
- [ ] About reports version 2.15.4.

---

## Calculator behavior

- [ ] Add/edit/remove expense items.
- [ ] Calculator enforces the documented 100-item limit.
- [ ] Nonnegative amount validation works.
- [ ] Percentage validation works.
- [ ] Currency-code validation works.
- [ ] Exchange-rate validation works.
- [ ] Split-count validation works.
- [ ] Discount/tax/tip/service-charge ordering matches documented finance policy.
- [ ] Currency conversion is manual/local and precision-safe.
- [ ] Split display/rounding remains consistent with domain results.

---

## History behavior

- [ ] Save a calculation with a meaningful label.
- [ ] Blank save label falls back to the safe default name.
- [ ] History search finds the saved label.
- [ ] History search respects its documented length bound without splitting valid surrogate pairs.
- [ ] Individual deletion works.
- [ ] Undo after individual deletion works.
- [ ] Clear-all requires confirmation.
- [ ] Retention settings/purge behavior work.
- [ ] Unicode-heavy labels near the saved-name boundary remain well-formed and restorable.

---

## Template behavior

- [ ] Save a template.
- [ ] Load a template and verify its finance fields.
- [ ] Delete a template.
- [ ] Undo template deletion.
- [ ] Template name limit/guidance is correct.
- [ ] Dialog confirmation wording is not confused with the underlying `Save template` action.
- [ ] Unicode-heavy template names near the boundary remain well-formed and restorable.

---

## Export/share behavior

- [ ] Plain-text receipt export opens the expected Android share flow.
- [ ] CSV export opens the expected Android share flow.
- [ ] CSV text cells remain protected against spreadsheet formula interpretation.
- [ ] PDF receipt export opens the expected share flow.
- [ ] Long Unicode item names do not create malformed/dangling surrogate text in PDF truncation.
- [ ] Shared cache files use only the intended non-exported FileProvider path.
- [ ] No unintended broad filesystem path becomes shareable.

---

## Backup/restore behavior

- [ ] Backup export opens Android's document creator.
- [ ] Restore opens Android's document picker.
- [ ] Restore requires confirmation before replacing current data.
- [ ] Backup/restore busy/progress state is visible during actual work.
- [ ] Duplicate backup actions remain disabled while work is active.
- [ ] History round-trips.
- [ ] Exact accepted history labels round-trip.
- [ ] Templates round-trip.
- [ ] Theme preference round-trips.
- [ ] Accessibility preferences round-trip.
- [ ] Retention preference round-trips.
- [ ] Malformed UTF-8 backup input is rejected without replacing current data.
- [ ] Checksum-invalid backup input is rejected.
- [ ] Checksum-valid but structurally invalid persisted records are rejected.
- [ ] Noncanonical persisted currency text is rejected rather than silently repaired.
- [ ] Duplicate IDs in replacement collections are rejected before destructive replacement.
- [ ] Failed multi-store restore does not silently erase valid existing state.

The backup SHA-256 protects against accidental corruption; it is not a signature/MAC/authorship proof.

---

## Offline/local-first behavior

With network disabled verify:

- [ ] Calculation works.
- [ ] History works.
- [ ] Templates work.
- [ ] Settings work.
- [ ] Text/CSV/PDF generation works.
- [ ] Core functionality requires no account/API key.
- [ ] Manifest still has no unintended `INTERNET` permission.

---

## Accessibility

- [ ] Light theme reviewed.
- [ ] Dark theme reviewed.
- [ ] System theme reviewed.
- [ ] App large-text preference reviewed.
- [ ] Large Android system font scale reviewed.
- [ ] Reduced-motion preference removes/reduces navigation transitions as intended.
- [ ] TalkBack traversal order reviewed.
- [ ] Navigation destinations are announced logically without fake duplicate accessibility labels added only for testing.
- [ ] Named-history dialog title/field/supporting text/actions are announced logically.
- [ ] Template dialog title/field/limit guidance/actions are announced logically.
- [ ] Backup progress state is announced appropriately.
- [ ] Validation meaning is understandable without color alone.
- [ ] Touch targets are appropriate.
- [ ] Destructive-action wording is clear.

---

## Responsive layout

- [ ] Small phone layout reviewed.
- [ ] Typical phone layout reviewed.
- [ ] Large phone layout reviewed.
- [ ] Tablet/wide layout reviewed.
- [ ] Large font scale does not hide essential actions.
- [ ] Dialogs remain usable at supported widths/font scales.

---

## Privacy/security

- [ ] No private test data appears in committed screenshots/assets.
- [ ] No production signing key, password, token, or local secret is committed.
- [ ] `.env.example` remains non-secret documentation only.
- [ ] Core runtime requires no remote API key.
- [ ] FileProvider remains non-exported and path-constrained.
- [ ] Android system-managed backup/device-transfer behavior agrees with `PRIVACY.md` and related docs.
- [ ] Explicit backup docs accurately describe checksum limitations.
- [ ] Logging remains redacted for sensitive keys/data.

---

## Production signing

Production signing material stays outside source control.

- [ ] Build unsigned/unsigned-equivalent release artifact from the exact verified source SHA.
- [ ] Align the APK if the chosen signing flow requires it.
- [ ] Sign with the controlled production key.
- [ ] Verify the signature/certificate with `apksigner`.
- [ ] Sign/prepare the AAB according to the chosen store workflow.
- [ ] Do not expose signing passwords in shell history/CI logs/issues/docs.

Current signed APK example:

```text
SpendCalc-2.15.4-release.apk
```

---

## Artifact inspection

- [ ] Inspect package identity: `in.sanskar.spendcalc`.
- [ ] Inspect `versionName`: 2.15.4.
- [ ] Inspect `versionCode`: 21504.
- [ ] Inspect `minSdk`: 26.
- [ ] Inspect `targetSdk`: 35.
- [ ] Inspect intended permissions.
- [ ] Install the exact signed artifact on a representative device.
- [ ] About reports 2.15.4 from the installed signed artifact.
- [ ] Record SHA-256 checksum.
- [ ] Record exact source commit SHA.
- [ ] Preserve the checksum ↔ source-SHA relationship in release records.

---

## Upgrade testing

When a previous production-signed public release exists:

- [ ] Install the prior public release.
- [ ] Create representative local data/preferences.
- [ ] Upgrade in place to the 2.15.4 production-signed artifact.
- [ ] Existing history/templates/preferences survive.
- [ ] Backup/export still work.
- [ ] About reports 2.15.4.

Do not claim upgrade verification before this is executed with real matching production signing identity/artifacts.

---

## Screenshots

- [ ] Capture screenshots only from the verified 2.15.4 build.
- [ ] Use fictional data only.
- [ ] Capture representative Calculator/History/Templates/Settings/About states where needed.
- [ ] Review screenshots for private data, notifications, account identifiers, tokens, local paths, or other accidental leakage.
- [ ] Follow `docs/assets/screenshots/README.md`.

---

## Dependency-update isolation

- [ ] Do not mix unrelated major dependency jumps into 2.15.4 merely because Dependabot opened them.
- [ ] Evaluate AGP/Kotlin/KSP/Room/AndroidX/Actions major updates independently.
- [ ] For each dependency PR, inspect breaking changes, runner/runtime requirements, licenses/terms, build compatibility, and Android runtime regressions.
- [ ] Merge only upgrades that independently pass their compatibility gates.

---

## Final tag/publication gate

Before creating `v2.15.4`:

- [ ] Exact-head CI green.
- [ ] Exact-head CodeQL green.
- [ ] Exact-head Dependency Review green.
- [ ] Exact-head Repository Audit green.
- [ ] Exact-head Android Instrumentation green.
- [ ] Manual Android checks complete.
- [ ] Accessibility/layout checks complete.
- [ ] Export/share checks complete.
- [ ] Backup/restore checks complete.
- [ ] Offline/privacy/security checks complete.
- [ ] Real screenshots complete.
- [ ] Production signing complete.
- [ ] Signed-artifact install/version/permission inspection complete.
- [ ] Artifact SHA-256/source-SHA recorded.
- [ ] Release documentation/changelog/roadmap/continuity state matches actual evidence.

Only then tag and publish **v2.15.4**.

**Made by the Sanskar**
