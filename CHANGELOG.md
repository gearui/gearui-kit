# Changelog

## [Unreleased]

### beta7 candidate (not released)

- Added stable-value `PickerOption` wheel entry points; cancellable, revision-keyed
  Cascader loading with explicit empty and failure states; inclusive Gregorian
  date/time bounds, precision, filters and time steps. The index-based
  `Picker.Single` / `Picker.Multi` remain; the untyped `Picker.Linked(data: Map)` is
  removed in favour of `Picker.Linked(options: List<PickerOption>)`.
- Date and time wheels are computed column by column as they are shown (a few hundred
  checks for the default 1900–2100 range, none while the sheet is closed), constraints
  compare by value so building them inline no longer resets a turning wheel, and stored
  values are read leniently ("2024-3-5", "09:30:00" under "HH:mm"). A coarse time slot
  is offered only when it starts within the bounds, so a picked time never precedes `min`.
- `Picker.Linked` confirms a branch shallower than `columnNum` (a region with no
  districts) at its leaf. Sibling options sharing a value keep the first instead of
  throwing during composition.
- Added typed asynchronous form fields, validation generations and server errors;
  exact `DecimalValue`-based `NumberField` with matching formatter/parser and
  an optionally editable integer Stepper (`editable = false` by default). The existing
  `Double` overload remains source-compatible.
- Number fields ask for KuiklyUI's digit pad only when they take non-negative whole
  numbers; KuiklyUI has no decimal or signed pad, so fields taking "." or "-" use the
  text keyboard. Number fields commit on leaving the field, not on the unfocused state
  reported when they first appear.
- A typed field's check joins one already running for the same value instead of
  replacing it, so a submit that coincides with a blur check no longer returns false
  silently; `rememberTypedFormFieldState` keeps what the user typed when its
  `initialValue` changes (a record that finished loading) and moves untouched fields.
- Added ListBox, YearPicker, DateField/TimeField, Toolbar, SubMenu, Kbd,
  ColorPicker family, Meter, User and Code/Snippet, with sample entries.
- `InputFormat` now defers formatting while the native IME reports active
  composition; formatted input no longer uses the rendered length as a native
  paste limit. `Avatar` load/failure fallbacks and `AvatarGroup` overlap were
  corrected.
- **Custom language packs:** `DateTimeStrings.secondSuffix` is a trailing
  defaulted field. Source callers using named/default arguments can recompile;
  precompiled packs must be rebuilt against the new binary API.
- `Navigator` keeps the entry beneath the top composed and hidden, so a swipe back or
  a back press reveals it at once. It was built when the gesture was recognised,
  which stalled the main thread for about 450 ms on an Android device while the page
  ignored the finger; the next frame now follows recognition in about 12 ms. A
  parked page is hidden from screen readers and cannot receive taps.
- The architecture docs now require the single-page `Navigator` model for apps and
  say what it costs.
- No beta7 artifact has been uploaded and no version tag has been created.

### Migration from 1.0.0-beta6

- **Source-compatible, binary-incompatible.** New trailing parameters with defaults:
  `contentDescription` on `Button`, `Checkbox`, `RadioButton`, `Switch`,
  `PressableFeedback`, `InputOTP` and `NavBarItem`; `accessibilityLabel` on
  `BasicTextField`; `surface` on `Popup.Host`. Callers recompile unchanged; a library
  compiled against beta6 that calls these must be rebuilt.
- **Source-breaking for custom language packs.** `CommonStrings` gains `back`, `on`,
  `off` and `partiallySelected`; `FieldStrings` gains `clear`, `showPassword`,
  `hidePassword`, `selectAll`, `selectRowFormat` and `verificationCode`;
  `DateTimeStrings` gains `previousMonth` and `nextMonth`; `MediaStrings` gains
  `previousSlide` and `nextSlide`. A pack that constructs these classes in full must
  supply them. `Strings` also gains the `format` and `lunar` domains, and `CommonStrings`
  gains `loadMoreFailed` and `noMoreData`. Packs built from `*Patch` over a shipped pack
  need no change.
- **Removed, with their replacements.** One way to do each thing:
  - `CollapseItem`, `CollapseGroup`, `CollapseItemData` → `Collapse` with `CollapsePanel`s.
  - `DrawerWithHeader` → `Drawer`.
  - `Notification(visible = …)`, `NotificationData`, `NotificationHost`,
    `NotificationHostState`, `rememberNotificationHostState` → `rememberNotificationController()`.
  - `SearchBar(showCancel = true)` → `cancel = SearchBarCancel.Always` (or leave the
    default, `WhileEditing`).
  - `Select` / `MultiSelect` `panelMode` and `SelectPanelMode` → nothing: the panel always
    opens anchored to the trigger, below it or above when there is no room. The
    item-aligned mode was a desktop pattern.
  - `MotionTokens` → `Theme.motion`.
  - `Text(fontSize = …, fontWeight = …)` → `style = Theme.typography.x.copy(fontSize = …)`;
    `Text(secondary = true)` / `tertiary = true` → `color = Theme.colors.mutedForeground`;
    `Text(color = null)` → omit it (the default is `Theme.colors.foreground`).
  - `RuntimeFlags.unifiedSafeAreaPipeline` → nothing: the stabilised safe area is the only
    path, and the configuration-inset fallback is gone.
- **Behavioral, this release.** `Cascader` opens a bottom sheet with a tab per level and
  calls `onSelect` once, with the full path, when a leaf is chosen (it used to report
  every level); `dropdownHeight` is gone. `Calendar.firstDayOfWeek` defaults to the
  language's convention, so Simplified Chinese calendars now start on Monday.
- **One `Avatar`.** `com.gearui.primitives.Avatar` and `com.gearui.components.image.Avatar`
  are replaced by `com.gearui.components.avatar.Avatar(fallback, url, painter, size,
  shape, …)`; `AvatarGroup` moves to the same package and `AvatarGroupItem` takes
  `fallback` and `url`. `text` → `fallback`; `image` → `painter`, or better `url`;
  `radius` → `shape` (`CircleShape` by default); `icon` and `badgeVisible` are gone.
- **Behavioral.** `DatePickerInput` / `TimePickerInput` open a bottom sheet instead of
  a dialog. `SearchBar`'s Cancel is text, not a filled button. Remembered notification
  and snackbar controllers dismiss what they showed when their screen leaves.
  `Skeleton`'s default corner radius is `Radius.sm` (8) instead of 4.

### Added

- `Avatar` loads a `url` itself (KuiklyUI coil3), crops it to the shape, keeps the
  initials while it loads or when it fails and hides them once it shows, and draws the
  unread badge and a new `online` dot above the picture. Apps no longer stack their own
  image over it.

- Chinese-market features, following GearUI's own design rules:
  - `loadMoreItem` / `LoadMoreFooter`: the footer of a paged list, with the states of
    KuiklyUI's `FooterRefresh` (idle, loading, failed with tap-to-retry, no more). It
    asks for the next page only once the footer is really on screen.
  - `AgreementCheckbox`: the consent line under a sign-up button — unchecked by
    default, as the Personal Information Protection Law and app-store reviews require;
    each 《document》 opens on its own, the rest of the sentence toggles the box.
  - `LinkedText`: a sentence with tappable phrases, defined by the whole sentence and
    its phrases so translators keep their word order. Lines never start with "，" or
    end with "《".
  - `InputFormat` for `Input(format = …)`: `ChinaMobile` (3-4-4, drops a pasted +86),
    `BankCard` (groups of four) and `IdCard` (6-8-4, X check character, GB 11643
    checksum in `isComplete`). The value stays raw; the caret keeps its place.
  - `IndexBar`: the letter strip of a contact or city list, with the drag bubble.
  - Chinese lunar calendar: `Lunar` converts dates offline for 1900–2100 and gives solar
    terms, traditional festivals, stems, branches and zodiac. `Calendar(lunar = true)`
    shows the lunar day, festival or solar term under each date.
  - Language-pack conventions (`Strings.format`): `compactNumber` (1.2万, 3.5亿; 12.3K),
    `relativeTime` (刚刚, 5分钟前, 昨天 14:30, 9月28日) and `firstDayOfWeek`. Lunar names
    live in `Strings.lunar`.

- Accessibility names. `Button`, `Checkbox`, `RadioButton`, `Switch` and
  `PressableFeedback` take a `contentDescription`, for icon-only buttons and controls
  with no text of their own. A bare switch, checkbox or radio in a `Cell`'s leading or
  trailing slot, or in a `FormItem`, is named by the row title or field label without
  one. `NavBarItem` takes a `contentDescription`; the NavBar back button reads "Back".
- New strings: `common.back`, `on`, `off`, `partiallySelected`; `field.verificationCode`; `field.clear`, `showPassword`, `hidePassword`,
  `selectAll`, `selectRowFormat`; `dateTime.previousMonth` / `nextMonth`;
  `media.previousSlide` / `nextSlide`.

### Fixed

- Screen readers could not reach the contents of a dialog, sheet or popover: the
  overlay host used `clickable` containers to catch taps, and a clickable node is one
  accessibility element that hides its children on iOS. It uses tap gestures now.
- Stateful controls now speak their state, after the label and in the current
  language: switches "Wi-Fi, on" (Kuikly dropped the toggle state), checkboxes
  "selected" / "partially selected", and radios, tabs, segments, toggle buttons,
  selectable tags and Select options "selected" (Kuikly's own suffix was hardcoded
  Chinese and read first). Also `SwitchWithLabel`, `CheckboxWithLabel`,
  `RadioButtonWithLabel` and `SwitchGroup` rows.
- Text fields have a name: `Input`, `SearchBar` and `InputOTP` ("Verification code",
  or a new `contentDescription`) are read by their label, a `FormItem`'s label, or
  their placeholder. `Textarea` and
  `SearchBar` no longer wrap their field in a clickable, which hid it from screen
  readers behind an unnamed button; on iOS a `Textarea` is still unnamed (KuiklyUI).
- Unlabeled controls: Calendar month arrows, Swiper arrows, Table selection
  checkboxes ("Select all", "Select row 3"), Input and SearchBar clear buttons, the
  SearchBar search icon, and TagGroup remove buttons ("Remove Kotlin"). A disabled
  SearchBar no longer shows up as an unnamed button.

- The Android sample crashed with a `StackOverflowError` on the Form, Input, Textarea
  and Switch pages in debug builds: Kuikly's context thread has a 1 MB stack. The
  sample now gives it 8 MB through an `IKRThreadAdapter`; the README shows hosts how.

### Changed

- `DatePickerInput` and `TimePickerInput` open the same bottom-sheet wheels as `Picker`,
  with one selection band across all columns, instead of a dialog whose columns each
  drew their own. Days follow the Gregorian leap-year rule (2100 has 28 days in
  February), years run 1900–2100 instead of 2020–2030, an empty value starts on today,
  and Cancel discards what was scrolled.
- `SearchBar`'s Cancel is tinted text, as on iOS and in HeroUI Native, not a filled
  block; `SearchBarWithAction`'s button is a kit `Button` with its press feedback.
- Notifications and snackbars shown through `rememberNotificationController` /
  `rememberSnackbarController` are dismissed when the screen that owns the controller
  leaves, instead of staying over the next screen.
- `Popup.Host` takes `surface = false` for content that draws its own surface.
- `Skeleton` and `SkeletonImage` default to `Radius.sm`, the radius of the image they
  stand in for, instead of spacing values used as radii.

- The kit no longer depends on JetBrains `compose.foundation` or `compose.ui`. It never
  used them — every UI import is KuiklyUI's own — but on Android they pulled the whole
  androidx Compose UI and foundation stack into every consumer, and pinned its version
  against the consumer's own. Only `compose.runtime` remains. An app that uses androidx
  Compose directly declares it itself.

## [1.0.0-beta6] - 2026-09-28

Published to Maven Central. Not binary- or source-compatible with 1.0.0-beta5:
components and parameters are removed (see "Removed" and "Removed (API)", which
include the migrations). KuiklyUI 2.x resolves from Tencent's Maven mirror, not
Maven Central; the README's install section now lists it.

### Removed

- `Transfer`, `Tree`, `TreeSelect`, `Pagination`, `Anchor`, `NavigationMenu` and
  `RadioCardGroup`. Desktop and web patterns with no counterpart in HeroUI Native or iOS
  and no user in the products built on the kit. `FieldStrings.transferSourceTitle` /
  `transferTargetTitle` and the unused `treeIndent` / `tabsTriggerPaddingInline` tokens
  go with them.
- Breadcrumb, Sidebar, FAB, Message and DropdownMenu leave the component index. The
  first two only ever existed in the sample; the other three were Button, Snackbar and
  Select demos under another name. The index now lists 71 components, all real.

### Fixed

- A tappable row lost its own background. Kuikly gives a view one background colour, and
  the press-feedback modifier's transparent idle fill replaced it: the current page in
  Pagination was white text on white, a clickable Tag lost its fill, an interactive Cell,
  a clickable Notification or NoticeBar, BackTop, the Empty action and the Stepper
  buttons went transparent. `rowPressFeedback` now paints the row's `base` colour itself
  with the press fill composited over it.
- BACK during a `Tour` popped the page with the tour still running; it now leaves the
  tour, as Skip does.
- `ImageViewer` was a translucent box the caller had to place at the root, and BACK
  popped the page under it. It is now hosted by the overlay runtime on an opaque black
  backdrop, and BACK closes the viewer.
- Anchored panels (Tooltip, Popover, menus) near the screen edge were pushed flush
  against the glass; they keep `OverlayDefaults.screenEdgeMargin` (12) from it.
- Horizontal `Steps` gave its connector a third of every column and centred it on the
  labels, so four-character titles wrapped. Steps now take equal columns with the
  connector at the icon's centre line and the labels on the full width.
- An unselected Checkbox or Radio was outlined in the divider colour, about 1.3:1 on a
  card. It now uses the iOS system grey (`lightSelectionOutline` /
  `darkSelectionOutline`), which clears the 3:1 non-text contrast minimum.

- `CellGroup` kept each row's press state by position, so removing a row handed its
  state to the next one. Rows can now pass `key`, and state follows the item.
- `ImageGallery` reported index 0 for every repeated painter (all nulls while
  loading); it uses the item's position.
- `StepItem.icon` and `ImagePlaceholder(icon = …)` drew the icon *name* as text; both
  now draw the icon.
- `Result`: FORBIDDEN showed a crossed-out camera and QUESTION the info glyph; they now
  use a lock and a question mark.
- `EmptyState`'s default action was a hand-built box; it is a `Button`.
- `Avatar` and `GearImage` ran `onClick` with no press feedback; they scale on press.
  Avatar initials take their size from the type scale instead of fixed `sp`.
- `Radius` still carried the pre-beta5 4 / 6 / 8 / 12 while `Theme.shapes` had moved
  to 8 / 12 / 14 / 24. It is now read from the same generated geometry.

- `Table` crashed ("vertically scrollable component was measured with an infinity
  maximum height") inside a scrolling page without an explicit height. With an
  unbounded height it now lays its rows out in place; with a bounded one it scrolls.
- `Tour`'s card had a fixed width of 400, wider than a 360 phone; it now fills the
  width with the overlay margin, capped at the dialog width.
- `Picker` added a fixed 20 of fake bottom safe area; `BottomSheet.Host` now applies
  the real bottom inset, as the other sheets do.
- `ActionSheet` drew its badges by hand; they are the kit's `Badge`.
### Added

- `Form(layout = FormLayout.VERTICAL | HORIZONTAL)` drives every `FormItem`, and
  `FormItem` takes `error`, `description`, `required` and `enabled`: label above the
  control (the mobile default) or beside it, with the error under the control in both.
  `Form.scrollable`, `Form.formState`, `FormScope`, `FormField` and `FormItem.name`,
  none of which did anything, are removed; `help` is now `description`.
- `Tab(badge = …, dot = …)`: a count or red dot. Underline tabs raise it at the label's
  top-end without moving the label; capsule and card tabs, which clip their pill, put
  it beside the label.
- `SegmentedOption.icon` is an `Icons` name the control draws in the label's colour,
  so it follows the selection (it was a slot that could not know it was selected).
- `NoticeBar(shape = …)` and `Table(shape = …)` / `SimpleTable(shape = …)`, default
  `Theme.shapes.lg`; pass `RectangleShape` for a full-bleed bar.

### Changed

- The component layer carries no bare `dp` design values any more. Spacing reads
  `Spacing.*`; every size, width, stroke and indicator reads a `ControlGeometry` token
  whose source is recorded in `tokens/controls.tokens.json` (79 new tokens, all
  GearUI-basis with their reason). The spacing guard, a per-file debt freeze with 56
  entries, is now a hard gate with no baseline.
- Values that moved while being tokenised: `Popup` anchor offset 4 → 9 (the overlay
  offset every other anchored panel uses); `ContextMenu` minimum width 140 → 160 (the
  Popover menu's); Watermark offsets 20 → 16 (`Spacing.lg`); ImageViewer and Image
  corner radius 4 → 8 (`Radius.sm`).

### Removed (API)

- `TourStep.targetKey` and `Textarea(labelIcon = …)`: neither was ever read. A
  parameter that silently does nothing is worse than none.
- `Input(cardStyle = …)`, `Textarea(cardStyle = …)` and `Textarea(bordered = …)`. Every
  field-family component now takes `variant: FieldVariant` (default `PRIMARY`: field
  colour with the field shadow, for the page background; `SECONDARY`: `muted` fill, no
  shadow, for a card, sheet or header). Source migration:
  `Input(cardStyle = true)` → `Input(variant = FieldVariant.SECONDARY)`;
  `Textarea(cardStyle = true)` and `Textarea(bordered = false)` →
  `Textarea(variant = FieldVariant.SECONDARY)`. Behaviour: Textarea's `cardStyle`
  wrapper (label and field inside a padded grey box) is gone, and `bordered = false`
  no longer switches to the compact 16sp metrics; SECONDARY is the standard field with
  the filled look, and keeps the border, error outline and focus ring.
- `variant` is new on ComboBox, NumberField, Select, MultiSelect, Cascader,
  DatePickerInput, TimePickerInput, DateTimePickerInput, DateRangePickerInput,
  InputGroup and AutoResizeTextarea (the last defaults to `SECONDARY`, like SearchBar,
  which is what it drew before). `InputOTP.variant` moved to sit after `invalid`;
  positional callers past `invalid` must name their arguments.

## [1.0.0-beta5] - 2026-09-28

Published to Maven Central. Binary-compatible with 1.0.0-beta4: the public API dump is
unchanged. What an unchanged screen draws does change — see "Changed" — and the license
is now Apache-2.0. Verification record and open limits:
[quality status](docs/QUALITY_STATUS.md).

### License

- **GearUI Kit is now licensed under the Apache License 2.0** (was BSD 3-Clause).
  Releases up to and including 1.0.0-beta4 remain under BSD 3-Clause. Copyright
  Shanghai Boyu Information Technology Co., Ltd.; a `NOTICE` file carries the
  attribution. The POM's organisation and developer URLs point to netonstream.com.

### Added

- **Every control value records where it comes from.** Control tokens carry
  `$extensions."com.gearui.source"` — the HeroUI Native value, the measured iOS value,
  which one ships and why — and `scripts/component_spec.py --check` (in CI) rejects a
  value that drifts from its chosen side, a GearUI value without a reason, and a new
  token without provenance. Unsourced tokens sit in a baseline that may only shrink.
  `docs/COMPONENT_METRICS.md` is generated from the same data. 93 of 133 sourced; the
  40 left belong to components HeroUI does not have and each needs a design decision.

- **Performance page and scripts.** The sample's Performance page benchmarks a whole-app
  theme switch (with ~200 components on the page) and records frame intervals while a 1000-row `List` is
  flung; `scripts/perf/ios_perf.py` and `scripts/perf/android_perf.sh` drive it and add
  cold start and `dumpsys gfxinfo`.

### Changed

- **`List` no longer polls its scroll position.** It woke every 16 ms for as long as it
  was on screen, moving or not, to tell anchored overlays to close; it now observes the
  scroll state and runs only when the position changes.
- **Switch takes iOS 26's geometry**, measured on the simulator: 63×28 track, 37×24
  thumb, inset 2 (was HeroUI's 48×24 / 28×20).
- **List rows follow the iOS rhythm**: text starts 20 in and rows are 52 tall
  (was 16 on both axes, a 56 row). `listPadding` is split into `listPaddingInline`
  and `listPaddingBlock`; the separator inset follows the text.
- **Dialog title-to-description gap is 6**, HeroUI's own value (was 4).
- **The off switch reads against the page.** Its track takes iOS 26's colour, measured:
  (197,197,199) light, (90,90,94) dark. HeroUI's default fill sat 12 levels from the
  grouped background and the off switch disappeared on it.
- **Tag remove icon is 12**, HeroUI's value (was 14).
- **The switch thumb is white in every state**, as iOS draws it. It was the brand's
  primary foreground in both states, so a brand with a dark foreground drew a black
  thumb even when the switch was off.
- **An accessory no longer makes its row taller.** A `Cell` padded the whole row, so a
  switch's 44 touch target plus 28 of padding made every switch row 72 tall beside
  52-tall rows in the same card. Padding now belongs to the leading and text blocks;
  the accessory is centred and may overlap it, as on iOS. Text and avatar rows keep
  their height.
- **A `CellGroup` title lines up with the rows' content edge**, not with the separator.
  A group whose rows lead with an icon or avatar insets its separators past it, and
  the title used to move with them, out of line with every other group.
- **Performance: startup is measured to content, not to the first frame.** The sample
  marks process start → first frame of the home list in-app on Android and iOS; a
  `benchmark` build type (release code, not debuggable) is what `scripts/perf` measure.

## [1.0.0-beta4] - 2026-09-25

Published to Maven Central. Verification record and open limits:
[quality status](docs/QUALITY_STATUS.md).

### Behaviour changes to review when upgrading

Source-compatible, but they change what an unchanged screen draws:

- `Tabs` defaults to `TabsOutlineType.CAPSULE` (the segmented track). Pass
  `UNDERLINE` to keep the old look.
- Dialog actions are laid out by `DialogActionLayout`: a single action now fills the
  card and two ordinary actions split the row. Pass `actionLayout = TRAILING` for the
  old trailing row of small buttons.
- `ContextMenu` opens flush against its trigger (no 9dp gap).
- Floating surfaces paint the overlay role (`colors.popover`); in dark mode it now sits
  one step above `surface`. Overlays draw their hairline as a real 1dp border.
- `Input` keeps the caret at the end of existing text instead of before it.

### Binary-incompatible changes

Recompile against beta4; these signatures changed (see `gearui-kit/api`):
`DialogContent` (new `actionLayout`), `Material` (new `role`), `swipeBack`, `Rate`,
`RateWithDescription`, `Colors` (new roles), and the `CommonStrings` /
`DateTimeStrings` / `FieldStrings` string tables and their patches (new keys).


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

- **A pressed row fills its card edge to edge.** The row press scaled by 0.98, which on
  a full-width row left a sliver of card showing down both sides, so the press stopped
  short of the edges. Rows now fill; the corners come from the card's own clip, so the
  first and last rows round with it and the rows between stay square, as the platform's
  lists do. Discrete targets — menu and action sheet options, chips, tiles — keep the
  scale, which is what the reference scales.
- **Dialog actions follow the platform alert, not a screen's taste.** `DialogActionLayout`
  is resolved from the actions' roles and count by a tested pure function: one action
  fills the card (BLOCK), two ordinary ones split the row at equal width with Cancel
  leading (SPLIT), three or more, a destructive one, or a long label stack full-width
  with Cancel last (STACKED). The HeroUI form footer — small buttons on the trailing
  edge — remains as an explicit `actionLayout = TRAILING` for a dialog whose body is the
  point. Full-width actions share the 48 medium height; text sits 20 above the actions
  (was 32), the reference example's own spacing. `DialogContent` gains `actionLayout`.
- `TabPager`: the pages a `Tabs` bar selects between, swipeable side to side, with the
  selection shared so a tap scrolls and a swipe moves the bar. Give it the space it
  should have — `weight(1f)` in a Column — since a pager that claims the parent's whole
  height scrolls back into a viewport that runs off the screen.
- **Tabs default to the reference's `primary` variant**: a `segment`-coloured pill
  sliding on a `default`-coloured track, drawn by the same track as SegmentedControl.
  Ours defaulted to the underlined `secondary` variant and, under CAPSULE, filled the
  selected tab with the brand colour — between them, a plain `Tabs` read as a Material
  tab bar rather than an iOS segmented control.
- **A menu hangs off its trigger.** It carried the 9dp anchor gap that belongs to a
  tooltip or a popover pointing at something; under a NavBar action slot, which is
  `fillMaxHeight`, that gap is measured from the bar's bottom edge and left the menu
  reading as detached from the icon that opened it.
- **A floating surface paints the overlay role, not `surface`.** The reference draws the
  line by elevation — menu, popover, dialog, sheet and toast all paint `--color-overlay`
  — and half of ours had drifted onto `surface`, ContextMenu and BottomSheet among them.
  A `Material` now names its role and `MaterialSurface` resolves it, so the answer lives
  in one place instead of at each call site.
- **A menu is visible over a full-bleed `surface` page.** Dark lifts the overlay role a
  step above surface, the lever the reference's own worked theme uses; light strengthens
  the overlay shadow, since `surface` is already pure white and has no headroom left.
  Both measured on the simulator and recorded in `HEROUI_NATIVE_ALIGNMENT.md`.
- **A pressed row swallows the lines on both sides of it**, as the platform's lists do.
  A separator belongs to the pair of rows it sits between, so the container — CellGroup
  or List — hides it while either of them is pressed; left showing, it cut the highlight
  in two. Rows get their interaction source from the container through
  `LocalRowInteractionSource`, since a row cannot reach a line drawn outside its bounds.
- **The hairline matches the platform.** Measured against the system settings list on the
  same simulator: #E8E8E8 at 1dp in light, (44,44,46) in dark. Ours was #D8D8D8 at 0.5dp
  — thinner, and darker to compensate, which is what read as heavy.
- **Row separators use a lighter hairline.** `Colors.separatorSecondary` (reference
  `--color-separator-secondary`) lands near #D8D8D8 on white; rows were being ruled with
  the strong `separator` near #AAA, which is meant for the sheet grabber and for
  dividers between whole sections, and drew a grid over every card.
- **One row implementation.** There were two: `components.cell.Cell` behind CellGroup,
  and a second internal `Cell` behind `ListItem`, with its own geometry, its own press
  state and a separator drawn by each row — which is why the last row of a ListItem list
  carried a line under it and why rows pressed differently on different screens.
  `ListItem` is now a naming wrapper over `Cell`, and the duplicate is gone.
- `List` honours `ListTokens.divider`. The flag existed, `ListTokens.Settings` promised
  separators, and the DSL drew none. It now uses CellGroup's rule — before every row but
  the first — so a list and a group of the same rows are ruled the same way, and a
  section header starts a fresh run.
- **The sample builds every screen out of the kit.** It had been hand-rolling what the
  components already provide — a row, a segmented choice, an icon button — which is how
  the home list ended up with no press response. Raw tap modifiers are gone from the
  sample and `check_sample_uses_components.sh` keeps them out.
- **Every tap target answers the finger**, including the sample's own screens. The
  press-feedback guard covered the library only, so the demo home page — the first list
  anyone touches — kept a hand-built row with a bare clickable and a "›" character for a
  chevron. It is a Cell now, and the guard covers the sample. Cell was a bare `clickable`, and so were
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
