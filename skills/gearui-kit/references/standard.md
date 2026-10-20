# The GearUI standard

What "following the standard" means for an app, rule by rule, with the reason for each.
The checker IDs (GU…) are what `scripts/check_gearui_app.py` reports. The kit holds its
own code to the same rules in CI; the source documents are the kit's `docs/ARCHITECTURE.md`,
`VISUAL_SPEC.md`, `COMPONENT_SPEC.md`, `DESIGN_SYSTEM.md` and `I18N.md`.

## Contents

1. Imports (GU001–GU003)
2. Colour (GU101–GU102)
3. Geometry (GU201–GU207)
4. Typography (GU301)
5. Interaction and press feedback (GU401–GU402)
6. Runtime contracts (GU501–GU507)
7. Icons (GU601)
8. Text and language (GU701)
9. Rules the checker cannot see
10. Exceptions

## 1. Imports

- **GU001** `androidx.compose.ui/foundation/material…` → `com.tencent.kuikly.compose.*`.
  KuiklyUI ships its own Compose UI and foundation under that package; only
  `androidx.compose.runtime` is shared. An androidx UI import either fails to resolve or
  compiles against a Compose that no renderer draws.
- **GU002** Kuikly's Material components → GearUI components. Material carries Material's
  look and none of the kit's states, metrics, accessibility or Kuikly workarounds.
- **GU003** raw `LazyColumn` → `GearLazyColumn`. It dismisses anchored overlays and the
  keyboard on scroll and keeps a focused field above the keyboard; a raw list leaves a
  dropdown floating away from its field.

## 2. Colour

- **GU101** `Color(0x…)`, **GU102** `Color.White/Black/…` → `Theme.colors.<role>`.
  Light, dark, a brand accent or a customer's own `ThemeSpec` restyle every screen only
  if no screen holds its own colour. Read `val colors = Theme.colors` as the first line of
  a composable.
- Roles are semantic: page `background`, `surface` for cards/rows/bars, `card`,
  `popover`, `muted` / `mutedForeground` for secondary text, `primary` for the brand
  action, `destructive` / `success` / `warning` / `info` for status (each with
  `…Foreground`, which is guaranteed readable on it, and a `…Soft` tinted fill), and
  `separator` / `border` for lines. Text on a filled `primary` is `primaryForeground`,
  not "white".
- Error is validation, not a red brand accent; disabled, read-only, loading and
  selected are different states with different looks.
- Product concepts (a chat bubble colour) belong to an app-level theme extension, not a
  literal in a screen.

## 3. Geometry

- **GU201/GU202** spacing → `Spacing` (4/8 grid: xs 4, sm 8, md 12, lg 16, xl 24, xxl 32,
  xxxl 40, huge 48, massive 64). `md` is the most used step; page side padding is `lg`.
- **GU203** corners → `Theme.shapes` (sm 8, md 12, lg 14, controlLarge 16, xl 24, full).
  Shape is a theme axis: a square-corner brand flips every control at once.
- **GU204** borders → `BorderWidth.hairline/thin/thick`. There is no focus step: a border
  that grows on focus moves the content.
- **GU205** shadows → `Theme.elevation.raised/floating/modal`, normally owned by `Card`
  or the overlay. Do not use a `Spacing` value as an elevation or a radius: the numbers
  coincide today and diverge the day a scale is retuned.
- **GU206** icon sizes → `IconSizes.Default.xs…xl` beside text,
  `IconSizes.Display.sm/md/lg` for illustrations.
- **GU207** (warning) any other bare `dp`. A dimension that is the content itself (an
  image placeholder's height) is a named constant at the top of the file
  (`private val CoverHeight = 180.dp`); control sizes come from the components.
- Every control takes taps over at least 44×44 whatever its visual size; kit controls
  already do. A custom tappable thing gets it by being a kit component.

## 4. Typography

- **GU301** `fontSize`, `TextStyle(...)`, `.sp` → `Theme.typography.<style>`:
  `displayLarge/Medium`, `headlineLarge/Medium/Small`, `titleExtraLarge/Large/Medium/Small`,
  `bodyExtraLarge/Large/Medium/Small/ExtraSmall`, `markLarge/Medium/Small/ExtraSmall` (bold
  body), `link…`, `caption`, `label`. `Text` defaults to `bodyMedium` and
  `colors.foreground`. Adjusting a style with `.copy(fontWeight = …)` is fine; changing its
  size is not — pick another style.
- Long translations and large text must not truncate a control or hide an action: give
  text room (`weight(1f)`, `maxLines` + `TextOverflow.Ellipsis` only where a single line
  is the design).

## 5. Interaction and press feedback

- **GU401** `.clickable`, **GU402** `detectTapGestures` → a component. Every tappable
  thing shows a press: a `Button`; a list row is a `Cell` (or `Modifier.rowClickable` for a
  row of your own layout — pass the row's colour as `background`, since a native view has
  one background); a link is `Link`; anything else is wrapped in `PressableFeedback`.
- Disabled is applied once, by the component's own parameter, and also blocks input —
  alpha alone is not disabled. Field-family components take `enabled = …`; action-family
  components (`Button`, `Tag`, sheet items) take `disabled = …`. Do not invent the other.
- Fields on a white card or sheet use `variant = FieldVariant.SECONDARY` (outlined); the
  default field is borderless with a field shadow for the page background.
- Selections use the right component for the data (SKILL.md, "Which component").
- Dialog actions are laid out by the dialog from their roles: pass `destructive = true`
  for a destructive confirm; Cancel is never styled like the destructive action.

## 6. Runtime contracts

- **GU501** `RouterModule` / `openPage` → `Navigator` routes. **GU502** `when(page)` screen
  switching → `Navigator` (screens) or `TabHost` (tabs). One page holding one stack gives
  swipe back on every platform, page-by-page Android BACK, typed parameters and a single
  runtime that theme and language changes reach at once.
- **GU503** `safeAreaInsets` → `PageScaffold` (top), `BottomNavBar` / sheets (bottom),
  `LocalRuntimeEnvironment.current.safeArea` for a custom edge. The runtime filters iOS's
  transient zero inset and keeps the keyboard out of the bottom inset.
- **GU504** mounting `I18nRoot`, `OverlayRoot`, `ToastHost` by hand, **GU506** `App` inside a
  `View` that still wraps one, **GU507** a second `App` → exactly one `App`. A second root
  re-provides runtime flags and the overlay host for everything beneath it, so overlays
  and toasts appear twice or in the wrong layer.
- **GU505** (warning) hiding pages with `alpha`/`graphicsLayer` → `TabHost`.
- Overlays (`ConfirmDialog`, `Dialog.Host`, `BottomSheet`, `ActionSheet.showList`,
  `Popover`, `Toast`, `Snackbar`, `Notification`, `Tour`) come from the overlay runtime
  `App` mounts: it owns the scrim, stacking, Back, outside tap, scroll and route-change
  dismissal. A hand-made scrim `Box` gets none of that.
- State that must outlive a screen's composition goes in `retain { … }` on its entry
  (app-structure.md §6), not at the app root.

## 7. Icons

- **GU601** emoji or symbol characters as icons → `Icon(Icons.<name>)`. Characters ignore
  `tint` and the icon size scale, render differently per platform and font, and are read
  aloud literally. `Icons.*` is every Phosphor icon, regular and fill
  (`fill = true`, or `.filled` where a parameter takes an `IconSource`); names are in
  `references/icons.txt`. An app's own icons are an `object` of `IconSource` getters
  (`VectorIcon` path data or `ImageIcon`), declared with `get() =` so unused ones are
  stripped. Emoji that *are* content (a reaction picker) are not icons: allow them with a
  reason.

## 8. Text and language

- **GU701** a literal string shown to the user → the app's typed pack
  (`references/i18n-for-apps.md`). Includes `contentDescription`, placeholders, toast and
  dialog text. Not user-facing, so no pack: logs, analytics names, animation labels,
  route names, keys.
- Never concatenate translated fragments; use `{name}` placeholders and `formatArgs`.
- Icon-only controls carry a `contentDescription` from the pack; decorative icons inside
  a labelled control pass none (`null`, not `""`).

## 9. Rules the checker cannot see

Check these yourself before you finish:

- **No hand-built lookalikes.** A row of `Text` + `Spacer` + chevron `Icon` is a `Cell`; a
  card is `Card`; an empty page is `EmptyState`; a header with back is `NavBar`. No card
  inside a card; a `CellGroup` is already a surface.
- **Pages:** `PageScaffold` → `NavBar` → `GearLazyColumn` with `Spacing.lg` side padding and
  the bottom safe area added to its end padding (SKILL.md template).
- **States:** a screen that loads shows `Loading`/`Skeleton`, an empty result shows
  `EmptyState`, a failure shows an `Alert` or `EmptyState` with a retry action — not a
  blank page.
- **Destructive actions** ask first (`ConfirmDialog(destructive = true)`), and say what
  they destroy.
- **Touch:** nothing interactive smaller than the kit's controls; no gesture that fights
  the `Navigator`'s swipe back (a horizontal scroller uses the kit's pagers, which
  arbitrate with it).
- **Verification:** compiling is not the same as working. Say which platform you ran on,
  if any.

## 10. Exceptions

A line that must break a rule carries the reason, on the line or the line above:

```kotlin
private val Brand = Color(0xFF7C3AED)   // gearui-allow: GU101 brand accent, applied once with withBrandAccent
```

Several rules: `// gearui-allow: GU101, GU207 <reason>`. An allow without a reason is
reported as GU000. Keep exceptions rare and specific: if the same exception repeats, the
code is probably rebuilding something the kit provides.
