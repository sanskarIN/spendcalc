# Accessibility

SpendCalc aims for practical WCAG-oriented mobile accessibility rather than treating accessibility as a final visual polish step.

## Current design

- Material text fields expose visible labels.
- Major actions use text labels rather than icon-only meaning.
- Primary navigation uses vector icons together with persistent text labels; decorative icon content descriptions are suppressed so screen readers do not announce the same destination twice.
- Radio choices use semantic radio-button roles.
- Switches include nearby explanatory text.
- Minimum touch targets rely on Material component sizing.
- The app supports light, dark, and system appearance.
- A large-text preference increases core typography sizes while system font scaling continues to apply.
- A reduced-motion preference removes navigation transitions.
- Validation uses both error styling and explanatory text, not color alone.
- Backup operations display a progress indicator plus visible progress text, and duplicate backup actions are disabled while work is active.
- The calculator uses a single-column layout on smaller screens and a wider two-column layout when space permits.
- The editable calculator is bounded to 100 line items and exposes the limit through visible text and disabled action state.
- User-visible copy is externalized to resources for localization readiness.

## Manual release checks

### TalkBack

Verify the following in logical order:

1. Calculator heading and subtitle.
2. Each item name/amount field.
3. Add/remove item controls and the item-limit state.
4. Percentage, split, currency, and exchange-rate fields.
5. Save/reset/export actions.
6. Receipt labels and values.
7. Bottom navigation destinations are announced once with clear names.
8. History/template action buttons and Undo feedback.
9. Settings radio controls/switches.
10. Backup progress text, disabled backup actions, and restore confirmation dialog.
11. About contact/funding actions.

### Font scaling

Test at system font scales including at least 1.0×, 1.3×, and a large accessibility setting. Content must remain scrollable without hiding critical controls.

### Orientation and screen size

Test a small phone and a tablet/wide emulator. Ensure the receipt remains reachable and forms do not clip.

### Contrast

Review text and controls in light/dark themes. Do not rely solely on the custom primary color for status meaning.

### Touch target and dexterity

Ensure destructive and adjacent actions are not so close that accidental activation is likely. Keep Material buttons/toggles at platform-friendly target sizes.

## Reduced motion

Normal navigation uses short fade transitions. Enabling **Reduced motion** switches those transitions off. No fake loading delay is used; backup progress reflects actual application work.

Future animations must consult the reduced-motion preference and provide a non-animated or materially reduced alternative.

## Accessibility testing backlog

- Add further automated semantic assertions for primary controls when they improve regression confidence.
- Add screenshot/golden checks only if they can remain stable across toolchain updates.
- Add manual keyboard/focus-order checks if Android large-screen/desktop keyboard workflows become a primary target.
- Document any known TalkBack issue as a tracked bug rather than hiding it.

## Reporting an issue

Use the GitHub bug template and prefix the title with `accessibility:` when appropriate. Include device, Android version, assistive technology, font/display scale, and reproducible steps without posting private information.
