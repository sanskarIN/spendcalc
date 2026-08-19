## What changed

Describe the focused change and the user/developer problem it solves.

## Verification

- [ ] Relevant unit tests pass.
- [ ] `gradle lintDebug` passes.
- [ ] `gradle assembleDebug` passes.
- [ ] Release compilation was checked when the change can affect release builds.
- [ ] Android instrumentation tests were run when an emulator/device was required.
- [ ] `python3 scripts/check_format.py` passes.
- [ ] `python3 scripts/scan_secrets.py` passes.

## Finance correctness

- [ ] No money calculation was changed, or finance behavior has exact `BigDecimal` regression tests.
- [ ] Rounding/order changes are documented in `CHANGELOG.md`.

## Privacy and security

- [ ] No secrets, credentials, signing material, or personal data are committed.
- [ ] New permissions/storage/network behavior is documented.
- [ ] Exported/untrusted text is encoded or escaped for its context.

## Accessibility and UX

- [ ] User-visible strings are externalized.
- [ ] Large text/dark theme/responsive behavior was considered.
- [ ] Controls have clear labels/semantics and are not color-only.

## Documentation

- [ ] Documentation and `what_changed.md` are updated when behavior, setup, architecture, or release state changed.
