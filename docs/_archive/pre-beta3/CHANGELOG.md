# Changelog

All notable changes to gearui-kit are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] — targeting 1.0.0-beta3

### Disabled control consistency

- Stepper keeps its normal border and surface with shared disabled opacity;
  individual bound-limited buttons dim only when the overall control is enabled.
- Radio and Stepper labels use the same disabled opacity rather than a separate
  muted text palette. Form's custom origin trigger and rating now follow that
  appearance while retaining their interaction guards; read-only ratings remain
  visually distinct from disabled ratings.
- Expose `Modifier.disabledAppearance` for custom control decoration, without
  implying that a visual modifier alone disables interaction.

### Status bar surface

- Add an explicit PageScaffold top-safe-area color overload, preserving the
  original entry point and inset policy. Sample Home, Example and Settings
  pages use their navigation-bar color behind the system status bar, while
  content and bottom safe-area backgrounds remain unchanged.

### Input hierarchy correction

- Apply the hierarchy correction across Input, SearchBar, Select, Cascader,
  TreeSelect and date/time triggers and their sample sections, not only Textarea.
- Input labels default to above the field (explicit `labelPosition = "left"`
  remains supported). Counters and helper/error messages sit below the editor;
  reaching the character limit alone is not a validation error.
- SearchBar uses the app input palette and shared draw-only focus feedback.
  Touch focus now displays the same ring as keyboard focus without rebuilding
  the native text field. Date/time triggers use shared press/disabled feedback.

- Use explicit semantic outline fallbacks for built-in input themes until
  layered field shadows are implemented; custom InputColors remain untouched.
- Textarea now defaults to a label above the editor, with helper/counter text
  below it. Explicit horizontal layout is retained; default four-line standalone
  fields have a 128px minimum, without imposing it on autosize.
- Remove redundant Textarea example card wrappers and move the raw diagnostic
  to the end; correct the disabled example to use enabled=false.

### List alignment

- Move Cell padding/gaps to DTCG geometry, center leading content without
  constraining avatars, and apply disabled opacity to the whole row.
- Replace Cell's private fixed typography with app theme roles; keep rich-text
  slots. CellGroup uses the shared surface radius, retaining separator policy.
  Compact rows and sample page structure remain supported.

### Collapse alignment

- Align collapse spacing, separators and surface radius with the pinned Native
  accordion reference; add token-driven indicator, height and fade animations.
  Card no longer adds its own outer horizontal margin.
- Forward disabled state through CollapseGroup and keep long legacy titles
  from displacing the expansion indicator. Sample layouts and public APIs stay.

### Slider and SearchBar alignment

- Align default Slider track/thumb anatomy and drag feedback to the pinned
  HeroUI Native reference through DTCG geometry and spring tokens. Keep range
  and step behavior; clamp offsets before track measurement.
- Align SearchBar field radius and clear-button geometry; propagate disabled
  state to its native input and wire SearchBarWithAction's action callback.
  Existing sample page layouts are unchanged.

### Breaking changes

- `Shapes.controlLarge` and `ThemeSpec.inputColors` add public constructor and
  data-class fields; consumers must rebuild. Custom shapes default the new role
  to `lg`; custom colors derive input states unless explicitly overridden.

- `ThemeSpec` adds an optional `buttonColors` property. Kotlin callers can omit
  it, but the constructor/data-class binary signatures change; rebuild consumers.
- Button's default theme is neutral (`DEFAULT`), not `PRIMARY`. Pass
  `theme = ButtonTheme.PRIMARY` explicitly to keep a primary action appearance.

- Removed `ButtonType.GHOST`. It behaved identically to `ButtonType.TEXT`;
  use `ButtonType.TEXT` instead. This is a source-breaking change.

### Changed

- Checkbox, Radio and Switch default geometry and feedback now follow the
  pinned HeroUI Native reference through DTCG-generated values. Labelled
  controls use one interaction owner; CheckboxGroup and RadioGroup propagate
  `enabled` to every child. Switch uses a capsule thumb with spring travel.
  The 44px interaction target is distinct from the smaller visual mark.
  Checkbox/Radio keep a semantic outline until layered field shadows are
  available. Sample structure is retained; Switch size captions are corrected.

- Select and MultiSelect use a HeroUI Native-inspired padded popover with
  trailing selection indicators, rather than a persistent selected-row fill.
  The default is now the separately anchored TRIGGER_OVERLAID mode, preferring
  below the trigger; callers can explicitly retain ITEM_ALIGNED. Available root
  viewport space replaces a fixed panel height. Tamagui-style gradient scroll
  buttons are removed; the option list remains touch-scrollable. This changes
  default appearance and placement, not public parameter names.

- Sample previews use a neutral page-colored frame rather than filling every
  section with the control surface. Button examples teach defaults, size pairs,
  callback rejection and explicit brand/status extensions. Input demonstrates
  size-matched actions; the raw native field probe no longer leads Textarea.
- Semantic secondary/accent colors share the generated neutral palette.
  LIGHT buttons now use neutral surface-step colors and the same state resolver
  as DEFAULT. This intentionally changes LIGHT text/disabled/pressed appearance;
  do not use LIGHT as an inverse-text workaround on an arbitrary dark surface.

- Button radii follow 5/7/9/10 and Input radii 7/9/10. Extra-small button text
  and button icons follow the selected platform type size. Omitted icon spacing
  now follows the size's 5/7/9/10 gap; explicit caller spacing is preserved.
- Input and standalone Textarea use reference state colors and draw-only
  hover/focus feedback. Standalone Textarea uses 9px corners and 16px/13px
  horizontal/vertical insets. Compact AutoResizeTextarea keeps its geometry
  as an explicit GearUI extension.
- Standalone Textarea reserves empty `minLines` geometry even where the native
  renderer does not reserve it, avoiding the first two lines changing the height.

- Neutral page, surface, border and button-state colors now follow the v5
  reference. Neutral buttons have distinct pressed/hovered colors and an
  out-of-layout keyboard focus ring. Disabled/loading ignore transient states.
- Default typography uses Native and Web profiles, with body text 17/22 and
  15/23 respectively. Brand typography overrides remain supported.
- Began the open-source v5 visual migration. Regular Button and Field height
  changes from 40 to 44 logical units; their horizontal padding is 18 and 16
  respectively. Small/Large heights are now 36/52. Default semantic radii are
  0/5/7/9/16 plus the capsule role. These are intentional layout changes,
  not an appearance-preserving refactor. Full state/motion parity is pending.
- Control geometry is generated from a checked-in DTCG dimension profile;
  CI checks source/output consistency and rejects unsupported input values.

- KuiklyUI upgraded from 2.25.0 to 2.27.0 on every channel: the Kotlin
  artifacts (`2.27.0-2.1.21`), the ohos artifacts (`2.27.0-2.0.21-ohos`),
  the web renderer, the iOS pod and the ohpm `@kuikly-open/render` package.
  The toolchain is unchanged (Kotlin 2.1.21, Compose 1.7.3, KSP 2.1.21-2.0.1),
  so this is a dependency bump, not a migration. Verified: Android, iOS
  simulator, Web bundle, ohos link, apiCheck, and privchat-ui compiling
  against the result.
- Dropped the Compose Material dependency. Loading states use the new GearUI
  `LoadingIndicator` primitive, and `Radio`/`Progress` labels use the
  foundation `Text` with typography tokens instead of Material3 widgets.
- `Cell.showArrow` renders a real `chevron_right` icon instead of an
  invisible placeholder.

### Fixed

- Tapping a button no longer leaves the keyboard up. The dismiss container had
  been judging whether a child consumed the lift, which is true of anything
  interactive, so a login button counted as "inside the input". An input region
  now declares itself through the new `Modifier.keyboardDismissExempt`, which
  claims the gesture on the Initial pass; everything else dismisses. GearUI's
  text inputs carry it, and a composite composer can mark its whole bar so its
  send button does not close the keyboard mid-conversation.

- Showing an overlay now dismisses the soft keyboard first. The keyboard is
  drawn by the system on top of the app, so a menu, sheet or picker opened
  while typing came up underneath it, out of sight and out of reach. The new
  `OverlayOptions.dismissKeyboardOnShow` is on by default and handled in
  `OverlayHost`, so a new overlay component inherits it. Toast, Snackbar and
  the notification banner opt out: they only report something and never take
  focus, and closing someone's keyboard to say "Copied" is worse than the
  banner overlapping it.
- `ContextMenu` no longer closes itself when its anchor moves. It shows the
  overlay from a `DisposableEffect` keyed on the trigger bounds, and the
  `onDispose` dismissal ran the caller's `onDismiss`, which hides the menu, so
  any layout change that moved the anchor closed the menu for good. Re-anchoring
  is now told apart from a real dismissal. Hiding the keyboard before an overlay
  opens makes this reliably reproducible.

- Tapping or long-pressing a focused text field no longer dismisses the
  keyboard. The app-wide keyboard-dismiss container judged the gesture in the
  Main pass with `requireUnconsumed = false`, so it never saw whether a child
  had handled the touch and treated every tap — including one on the field that
  owns the focus — as a tap outside. A long press was swallowed the same way,
  which took the system paste menu with it. The gesture now starts tracking in
  the Initial pass and decides in the Final pass, where consumption by children
  is visible. Scroll-to-dismiss is unchanged and still ignores consumption,
  since a list scroll is consumed by the list and should still hide the
  keyboard.

- The ohos build gained the `currentCalendarDate()` actual it was missing
  (POSIX `localtime_r`; HarmonyOS has no Foundation). The calendar fix added
  an expect to commonMain with actuals for Android/iOS/JS only, which broke
  the parallel ohos configuration and nothing else.
- `CalendarDate.today()` returns the actual device date instead of a
  hardcoded constant, and the platform clock is pinned to the Gregorian
  calendar on Android, iOS and JS so locale calendars (e.g. Thai Buddhist)
  can no longer feed era years into the month-grid math.
- `Slider` and `RangeSlider` step snapping now origins the step grid at
  `valueRange.start` instead of zero, so ranges that do not start at zero
  snap onto the correct stops; zero-span ranges with steps no longer divide
  by zero.
- `ResponsiveGrid` computes its column count from the measured container
  width instead of a fixed three columns, and stays invisible until the
  first measurement so the initial frame never shows a wrong layout.
- `LoadingIndicator` normalises degenerate arguments: a non-positive size
  renders nothing, the stroke width is clamped into `0..size`, and a
  non-positive duration falls back to the default.

## [1.0.0-beta2] — last published release
