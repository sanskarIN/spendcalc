# SpendCalc Design System

SpendCalc uses a small Material 3 design layer rather than screen-specific styling constants.

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

## Motion

SpendCalc avoids decorative animation and fake loading delays. The current UI therefore already satisfies the reduced-motion preference for core journeys. If animation is introduced later, it must:

- be non-essential;
- respect the reduced-motion preference;
- not delay input or result presentation;
- avoid rapid flashing or large unexpected movement.

## Icons and labels

Primary navigation and actions must always include a clear text label/semantic meaning. Decorative visuals must not replace accessible labels. New graphical icons should use platform-consistent vector assets or Compose Material icons and must not be the only way a state is communicated.

## Responsive behavior

The calculator uses a single scrolling column on phones and a two-column calculator/receipt arrangement on wide screens. Maximum content/form widths prevent controls from stretching excessively on tablets.

## Empty, validation, and destructive states

- Empty history/templates use dedicated explanatory states.
- Search has a distinct no-match state.
- Invalid calculations suppress result output and show field-level guidance.
- Clear-history and backup restore require confirmation because they replace/delete stored data.
- Routine non-destructive actions do not add unnecessary confirmation dialogs.
