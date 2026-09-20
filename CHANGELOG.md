# Changelog

## [Unreleased]

### Added

- `PressableFeedback`: HeroUI Native press feedback for any content. It uses the
  width-compensated 0.985 scale and a 10% `#3f3f46` / `#d4d4d8` highlight stacked
  above the content. Also `Modifier.pressScale` and `pressedSurfaceColor` for nodes
  that paint their own fill.
- `CloseButton`: a 32dp circular icon-only tertiary button with an 18dp muted icon.
  CalendarPopup, Snackbar, Notification and ImageViewer now use it.
- `Link` and `LinkButton`. Link is foreground medium text with a separator-colour
  underline and optional icons. LinkButton is a ghost button with no padding and
  no highlight.
- Field text primitives `FieldLabel` and `FieldDescription`, next to `FieldErrorText`,
  plus `FieldDefaults.labelGap`.

- `Alert`: an inline status message on a surface card, with five statuses, an
  action slot and an optional close button.
- `InputOTP`: one-time code entry. A single hidden native field owns the value,
  the keyboard and paste; the slots only display it, with an accent outline and
  a blinking caret on the active slot.
- `SwitchGroup`: a labelled set of settings, each with an optional description,
  toggled by tapping anywhere in the row.
- `TagGroup`: wrapping tags with single, multiple or no selection, three sizes,
  and optional removal.
- `ScrollShadow`: fades the edges of a scrolling area while content is hidden
  past them, vertical or horizontal.

- `ComboBox`: a text field with filtered suggestions, using Select's panel. Optional
  `autoFocus` opens the field and the suggestions with the screen.
- `NumberField`: a number that can be typed or stepped, with decimals, negatives,
  bounds and the field text stack.
- `ToggleButton` and `ToggleButtonGroup`: buttons that stay pressed, single or
  multiple selection.
- `ButtonGroup`: related actions joined into one pill, with `ButtonGroupDivider`.
- `AvatarGroup`: overlapping avatars with a "+N" counter.
- `pullRefreshItem` and `rememberPullRefreshState`: pull-to-refresh with a themed
  indicator and translated copy, over the platform list gesture.
- `CloseButton` takes an `icon`, so other icon-only controls share its shape.

- `InputGroup` with `InputGroupAddon` and `InputGroupDivider`: a field with attached
  blocks (a country code, a unit, a "send code" action) sharing one frame. An Input
  inside a group drops its own surface.
- `NoticeBar`: a running announcement strip with tones, an action slot and a close
  button. It scrolls only when the text does not fit, and never under reduced motion.
- `Upload`: attachment tiles with thumbnails, progress, retry and remove. It owns no
  platform access; picking and transfer stay with the host.
- `DateRangePickerInput`: a field trigger over the calendar's range selection, with
  placeholders per end, a clear button and an error line.
- Soft status colour roles (`primarySoft`, `successSoftForeground`, …) on `Colors`,
  generated in OKLab from the reference mix ratios. Alert, TagGroup and NoticeBar read
  them instead of mixing colours themselves, and `withBrandAccent` carries the soft pair.

- `Typographies.Platform` (the previous iOS scale, 17 body with emphasis at 600) and
  `Typographies.Web`, for apps that want them back.
- Accessibility pass: CloseButton, rating stars and upload tiles carry translated
  labels; tags, toggle buttons, segmented controls and tabs announce their state;
  decorative icons are silent. The contract is in the engineering spec.

### Changed

- **Every tap target answers the finger.** Cell was a bare `clickable`, and so were
  tags, stepper buttons, accordion headers, pagination pages, anchor links, transfer
  rows, cascader options, navigation menu items, tabs, the notice bar, notifications,
  the empty-state action, the picker's confirm and cancel, the image viewer's delete
  and the back-to-top button: the action ran with nothing on screen acknowledging the
  touch. Radio built an interaction source and never read it. All of them now use the
  shared feedback (`rowPressFeedback`, `pressScale`), and `check_press_feedback.sh`
  keeps it that way.
- `menuItemFeedback` is now `rowPressFeedback`: the same row press, no longer named
  after menus alone (internal).
- **The default type scale is now the reference scale** (12/16, 14/20, 16/24, 18/28,
  emphasis at 500), the same on every platform. It was per-platform — 17/15/20 on iOS,
  a denser scale on the web — so one screen was a different size on each and neither
  matched the reference. Text shifts by a step in consuming apps; `Typographies.Platform`
  restores the old metrics.
- Sheets: BottomSheet and ActionSheet space their options apart as the reference does
  for a sheet-presented menu, instead of stacking them flush, and the header sits on the
  same left edge as the option text.
- ActionSheet renders through BottomSheet's chrome rather than its own copy, so it now
  has the grabber, the entrance animation and drag-to-dismiss it was missing.
- BottomSheet and Dialog titles are text-lg as the reference specifies. Both used the
  body size, which left the heading the same size and weight as the text under it.
- Rate redrawn: both layers use the same star glyph, the active layer is clipped per
  star, and the track is muted rather than a heavy outline. A half star now lines up
  exactly with the star under it. Tapping the leading half of a star gives the half
  score (`allowHalf`), `allowClear` resets on a second tap, fractional scores snap to
  the nearest half or whole star, and stars take the press scale. `icon` / `emptyIcon`
  now take icon names rather than text glyphs.
- Input, Textarea and Form item labels render through `FieldLabel`. The required
  asterisk now follows the label, as in the reference.
- Field clear buttons tint with the reference press highlight instead of a
  foreground mix.
- Fixed: press scale for icon controls measured width in pixels, not dp, which
  made the shrink about three times too weak on 3x screens.
- Fixed: `Input(autoFocus = true)` did nothing inside a lazy list. The request ran
  during the first composition, before the field was attached, and never retried.

## [1.0.0-beta3] - 2026-09-20

Published to Maven Central. See [release readiness](docs/BETA3_RELEASE_READINESS.md)
for the verification record and the known limitations. Earlier implementation notes are preserved
in [the pre-beta3 archive](docs/_archive/pre-beta3/CHANGELOG.md).

### Design system and components

- Align the default design with the pinned open-source HeroUI Native reference,
  not Tamagui or paid HeroUI Pro presets. Brand accent and shape remain independent.
- Unify field hierarchy, focus/press feedback and disabled appearance. Input and
  Textarea labels default above the editor; explicit horizontal layout remains.
- Improve Select/tree selection presentation, navigation and sample composition;
  remove redundant example wrappers and misleading platform-specific labels.
- Introduce shared layered surface decoration, border styling and platform-aware
  font resolution. Rendering limitations remain documented; not every legacy
  component has migrated to the shared surface renderer.
- Preserve PRIMARY as Button's default theme; DEFAULT explicitly selects neutral.
- Match sample status-area backgrounds to their navigation bars. Keep the Android
  system navigation bar transparent, including after theme changes.

### Data and runtime

- Add DTCG 2025.10 Format/Resolver validation and deterministic Kotlin generation,
  including composite material tokens, color conversion and font fallback lists.
  See [adapter policy](tokens/README.md); data support is not universal pixel parity.
- Consolidate safe-area and overlay contracts and typed route navigation.
- Cover calendar, slider, grid, navigation and token/runtime behavior with regression
  tests. Calendar uses the local Gregorian date; slider steps are range-relative.

### Compatibility and packaging

- Removed ButtonType.GHOST: migrate to TEXT. Navigator uses NavRoute-typed entries
  instead of String routes and external payload maps; controller identity is owned
  by the framework. See [migration notes](docs/MIGRATION_1_0.md).
- Public theme/text/surface API additions require downstream recompilation; a
  refreshed API baseline does not establish binary compatibility with beta2.
- Kuikly 2.28.0 (from 2.27.0); align consuming renderers, KSP bindings, iOS Pods and
  the ohos package to the same version.
- CI now explicitly runs Android Kotlin tests/lint and browser Kotlin tests, in
  addition to existing generation, guardrail, build and API checks.

### Overlays aligned with HeroUI Native

- Dialog follows the reference dialog: start-aligned title and muted description,
  20 padding, radius 24, width capped at 384 with 20 from each edge, and real
  Buttons (stacked danger + neutral Cancel, or an end-aligned row). The iOS
  alert layout with hairline-split action cells is gone; `DialogAction` roles are
  unchanged.
- Menus (ContextMenu, PopoverMenu), ActionSheet and BottomSheet lists use the
  reference menu row: radius 16, animated press fill (default, or danger at 10%)
  with a 0.98 press scale over 150ms, no separators. ActionSheet is one sheet with
  Cancel as a neutral button inside it; BottomSheet headers are start-aligned.
- Every overlay surface drops its extra border and uses the overlay colour and
  shadow stack. Panels and dialogs are radius 24, sheets 32. Toast is an overlay
  surface with soft status label colours instead of a solid colour block.
- Scrim is the reference backdrop (black 20%, was 55%). Overlays enter over 200ms
  and leave over 150ms; dialogs scale from 0.96, anchored panels slide up to 12
  from their trigger and scale from 0.97. Menu/popover offset is 9.
- New `Colors.separator` role (reference `--separator`) for Divider and the sheet
  handle; it was drawn with the lighter border colour.
- All values come from new DTCG tokens (overlay geometry, menu geometry, overlay
  motion, backdrop, separator).
- Sample: `MainDemo` mounted two nested `App`s (the base `View` wrapper plus its
  own), so toasts and the imperative ActionSheet rendered on the light theme above
  the real one. The guard now rejects that shape.

### Controls aligned with HeroUI Native

- SegmentedControl follows the Tabs primary variant: a `default` pill track with
  a white `segment` pill that springs between options (stiffness 1200, damping
  120). It was a bordered white track with a gray selected cell.
- Tabs underline is one 2px accent indicator as wide as the selected tab and
  spring-animated, instead of a fixed 20dp stub; labels are medium weight.
- Text fields and field triggers (Input, Textarea, SearchBar, Select, DatePicker,
  Cascader, TreeSelect) drop the 1dp border for the reference field shadow;
  `cardStyle` remains the filled variant for use on surfaces.
- Checkbox, Radio and Switch labels and Cell titles use the base size at medium
  weight (were the large body size or semibold title).
- New `Colors.segment` role and DTCG tokens for tabs geometry and indicator spring.

### Fixes

- Input/Textarea enforce `maxLength` inside the native field; previously the
  platform editor kept rejected characters while the counter stopped at the limit.
- DecoratedSurface no longer throws under unbounded (intrinsic) constraints; on iOS
  this terminated the app when a ContextMenu opened.
- Sample Settings page handles BACK and edge swipe like other pages.
- iOS Kotlin tests link against the real Kuikly host (`scripts/ios_native_tests.sh`,
  `-PgearuiIosTestHostDir`); CI's iOS job runs them.
- SearchBar gains `variant: FieldVariant` and defaults to the filled SECONDARY
  field: with field borders gone, a white search bar vanished into white headers.
- Single-line `cardStyle` Inputs take the fixed field height; they measured their
  content row to zero and showed an empty pill (text, placeholder, prefix, suffix).
- DecoratedSurface (Card and other decorated surfaces) redraws its outline and
  shadow when resized, instead of keeping the first size's border.
- NavBar defaults to `surface`, matching the bottom navigation bar.
- Textarea keeps the caret after existing text when the field is rebuilt.
- Sample pages run their lists under the iOS home indicator instead of painting a
  solid strip over it.
- Input: revealing a password (`isPassword` true -> false) now unmasks on iOS.
- HeroUI alignment: Card draws no outline by default (surface shadow only; pass
  `borderColor` to keep one); CellGroup gets the surface shadow; Skeleton defaults
  to shimmer (1500ms) on the muted text colour at 30%; Avatar fallback text is
  12/14/16 at medium weight in the foreground colour; Loading sizes are 16/24/32.
- Input and SearchBar clear buttons get the Button press feedback (width-compensated
  scale and neutral highlight), as the reference's icon-only tertiary buttons.

### Documentation

- [SPEC](docs/SPEC.md) is the authoritative entry. Visual rules, runtime contracts,
  DTCG adapter policy, source provenance and acceptance evidence have distinct owners.
- Superseded rules are retained losslessly in a non-normative archive.

## [1.0.0-beta2]
