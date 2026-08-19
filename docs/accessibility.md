# Accessibility

SpendCalc aims for practical WCAG-oriented mobile accessibility rather than treating accessibility as a final visual polish step.

## Current design

- Material text fields expose visible labels.
- Major actions use text labels rather than icon-only meaning.
- Radio choices use semantic radio-button roles.
- Switches include nearby explanatory text.
- Minimum touch targets rely on Material component sizing.
- The app supports light, dark, and system appearance.
- A large-text preference increases core typography sizes.
- A reduced-motion preference is stored and the current UI avoids unnecessary animation by default.
- Validation uses both error styling and explanatory text, not color alone.
- The calculator uses a single-column layout on smaller screens and a wider two-column layout when space permits.
- User-visible copy is externalized to resources for localization readiness.

## Manual release checks

### TalkBack

Verify the following in logical order:

1. Calculator heading and subtitle.
2. Each item name/amount field.
3. Add/remove item controls.
4. Percentage, split, currency, and exchange-rate fields.
5. Save/reset/export actions.
6. Receipt labels and values.
7. Bottom navigation destinations.
8. History/template action buttons.
9. Settings radio controls/switches.
10. About contact/funding actions.

### Font scaling

Test at system font scales including at least 1.0×, 1.3×, and a large accessibility setting. Content must remain scrollable without hiding critical controls.

### Orientation and screen size

Test a small phone and a tablet/wide emulator. Ensure the receipt remains reachable and forms do not clip.

### Contrast

Review text and controls in light/dark themes. Do not rely solely on the custom primary color for status meaning.

### Touch target and dexterity

Ensure destructive and adjacent actions are not so close that accidental activation is likely. Keep Material buttons/toggles at platform-friendly target sizes.

## Reduced motion

The current experience intentionally avoids decorative motion and fake loading delays. Future animations must consult the reduced-motion preference and provide a non-animated or materially reduced alternative.

## Accessibility testing backlog

- Add automated semantic assertions for primary controls.
- Add screenshot/golden checks only if they can remain stable across toolchain updates.
- Add manual keyboard/focus-order checks if Android large-screen/desktop keyboard workflows become a primary target.
- Document any known TalkBack issue as a tracked bug rather than hiding it.

## Reporting an issue

Use the GitHub bug template and prefix the title with `accessibility:` when appropriate. Include device, Android version, assistive technology, font/display scale, and reproducible steps without posting private information.
