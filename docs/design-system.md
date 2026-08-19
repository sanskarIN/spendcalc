# SpendCalc Design System

SpendCalc uses a small Material 3 design layer rather than screen-specific styling constants. Product-level accessibility requirements are expanded in [`accessibility.md`](accessibility.md), while file ownership is indexed in [`codebase-reference.md`](codebase-reference.md).

## Spacing

`SpendCalcTokens` defines the shared spacing scale:

- XS: 4 dp
- SM: 8 dp
- MD: 16 dp
- LG: 24 dp
- XL: 32 dp

Screens use these tokens for section gaps, card padding, and responsive composition.

## Shape

Shared corner-radius tokens:

- small: 8 dp
- medium: 16 dp
- large: 24 dp

Material components keep platform-consistent default elevation unless a component needs a documented exception.

## Touch targets

The design target is at least 48 dp for interactive controls. Material 3 Button, TextButton, Switch, RadioButton, NavigationBarItem, and text-field components are preferred because they provide platform semantics and touch sizing.

## Typography

The standard Material 3 typography scale is the base. The in-app **Large text** preference increases important body/title sizes while Android system font scaling continues to apply.

Finance output uses stronger title weights only for totals; secondary values remain visually quieter so the hierarchy stays readable.

## Color and themes

Custom light and dark color schemes use the same semantic Material color roles. System theme follows Android dark-mode state. Status/navigation-bar icon appearance changes with the selected theme to preserve readability.

Validation does not rely on color alone: invalid fields also display supporting error text.

The launch splash uses a dedicated light brand background and the editable SpendCalc icon, then hands off to the normal Compose theme after stored preferences are available.

## Motion and progress

Normal primary navigation uses short fade transitions. **Reduced motion** replaces them with no transition. SpendCalc does not use fake loading delays.

Backup operations display an indeterminate Material progress indicator plus explanatory text only while real backup read/write/restore work is active. Backup actions are disabled during that work to prevent duplicate operations.

Future animations must:

- be non-essential;
- respect the reduced-motion preference;
- not delay input or result presentation;
- avoid rapid flashing or large unexpected movement.

## Icons and labels

Primary navigation uses repository-owned 24 dp vector assets for Calculator, History, Templates, and Settings. Each destination also keeps a persistent text label. The vector itself is decorative in accessibility semantics because the text label supplies the destination name, preventing duplicate screen-reader announcements.

New graphical icons should use repository-owned/platform-consistent vector assets or a maintained icon source and must not be the only way a state is communicated.

## Dialogs and saved-name input

Saved history and template names share one domain policy, so their dialogs intentionally share interaction conventions:

- a visible dialog title;
- a visible text-field label;
- supporting guidance that states the 120-character limit;
- single-line input;
- UTF-16-safe truncation that never leaves a valid surrogate pair split at the limit;
- a concise `Save` confirmation action;
- a separate `Cancel` path;
- state reset when save/cancel/dismiss completes.

The template dialog uses `Save` for its dialog confirmation rather than another `Save template` label. This avoids ambiguity with the underlying calculator action in screen-reader navigation and Compose tests.

Blank history/template names are a repository concern, not a visual error state: blank history labels fall back to `Calculation`, while blank template names fall back to `Template`.

## Search input

History search is a single local text query with visible guidance. It is capped at 120 characters using surrogate-safe truncation. Search should remain responsive and should not gain decorative debounce/loading UI unless measured data access changes justify it.

A no-match state is distinct from an empty-history state so the user knows whether no records exist or the current query simply has no matches.

## Responsive behavior

The calculator uses a single scrolling column on phones and a two-column calculator/receipt arrangement on wide screens. Maximum content/form widths prevent controls from stretching excessively on tablets.

The current calculator editor eagerly composes its editable line-item cards, so it has a deliberate 100-item UI budget. At the limit, the Add item action is disabled and explanatory text is shown. A future higher-volume editor should use virtualization instead of merely raising the limit.

Dialogs, destructive confirmations, and backup progress must remain usable under large Android font scale and both phone/wide layouts.

## Empty, validation, destructive, and recovery states

- Empty history/templates use dedicated explanatory states.
- Search has a distinct no-match state.
- Invalid calculations suppress result output and show field-level guidance.
- Individual history and template deletion provide Snackbar Undo.
- Clear-history and backup restore require confirmation because they replace/delete stored data at larger scope.
- Backup work exposes modal progress and disables conflicting duplicate backup actions.
- Routine non-destructive actions do not add unnecessary confirmation dialogs.

## Branding and release imagery

The application launch icon/primary navigation vectors and `docs/assets/spendcalc-logo.svg` are repository-owned source artwork. Product screenshots are different: they are release evidence and must be captured from a real verified build using fictional data according to [`assets/screenshots/README.md`](assets/screenshots/README.md), not generated/fabricated to fill documentation.
