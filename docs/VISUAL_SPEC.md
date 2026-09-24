# GearUI Visual Design Specification

[English](./VISUAL_SPEC.md) | [简体中文](./VISUAL_SPEC.zh-Hans.md)

This document owns GearUI's visual rules: what the kit looks like and how every
control behaves on screen. These are GearUI's own rules — the default look ships
coherent today and is built to absorb the product's own brand direction over
time. Token mechanics and theming live in
[DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md); the component inventory lives in
[COMPONENT_COVERAGE.md](./COMPONENT_COVERAGE.md).

## 1. Visual Language

- **Content-first, flat surfaces.** Depth comes from tokenized shadows and
  separators, not heavy chrome or bezels.
- **Consistent density and rhythm.** Control geometry, spacing and type scale
  come from tokens; a component never carries its own bare design literals.
- **Feedback is part of the language.** Selection, press, focus and disabled
  states are modeled per state family (§4). Color is never the only signal.
- **Restraint.** No universal glow, haptics, gratuitous scaling or decorative
  motion. Every movement has a purpose and a motion token.
- **The default is a floor, not a ceiling.** Brand accent, shape style and
  light/dark are independent axes ([DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md) §2),
  so a product can restyle without touching component anatomy or behavior. Any
  future custom style is expressed through tokens and theme axes, not by
  forking components.

## 2. Where Values Come From

GearUI keeps no second prose schema of visual numbers.

- Every concrete value — color, radius, spacing, elevation, motion — lives in
  the token sources under `tokens/` and the generated code.
  [DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md) owns that pipeline.
- Component-specific numbers live in that component's own `XxxTokens` class,
  derived from semantic tokens.
- Prose describes rules and intent; it never hardcodes a number that belongs in
  a token. A visual change is a token/source change — reviewed, regenerated and
  snapshotted — not an edit to a component literal.
- Screenshots illustrate a state; they are never the source of a value.

## 3. Component Anatomy

**Fields.** Labels sit above standalone fields by default; explicit horizontal
form layouts remain supported. The editor owns its surface and focus
treatment; helper/error and count text sit below it. Limit counters are not
errors. Fields are borderless with a field shadow by default — on white
cards/surfaces use the filled variant (`cardStyle = true`).

**Select and hierarchical selection.** A field trigger plus a layered option
surface with clear selected/disabled states, aligned indicators, bounded
scrolling and safe placement. Popover is the default presentation; sheet and
dialog are explicit alternatives, not automatic phone substitutions.
TreeSelect/Cascader preserve hierarchy, expansion and selection semantics for
touch rather than imitating a desktop tree.

**Cards and lists.** A card owns one surface, shape, padding and optional
tokenized shadow/border. CellGroup owns grouping and separators; a Cell does
not create another card around its control. No nested decorative frames.
Reuse Text/Icon/Badge primitives including their sizing and count-overflow
logic.

**Navigation bars.** NavBar is a cross-platform page component. Titles stay
single-line with bounded actions and ellipsis. The top safe-area background
follows the top bar. Android's bottom system bar is transparent over the page
with legible system icons; it must not inherit a top-bar-colored strip.

**Dialogs, sheets and menus.** Placement, scrim, dismissal and lifecycle come
from the Overlay runtime. Destructive and cancel actions must be distinct.
Content stays reachable with long lists, keyboard and safe areas. One
screenshot of an open sheet is not behavioral acceptance: test outside tap,
Back, drag cancellation and rapid reopen.

**Dialog actions.** The layout is a property of the question, decided by
`DialogActionLayout` from the actions' roles and count, never by the screen:
one action fills the card (BLOCK); two ordinary actions split the row at equal
width with Cancel leading (SPLIT); three or more, any destructive action, or a
label too long for half a card stack full-width with Cancel last (STACKED).
These are the platform alert's answers — a lone small button in the corner of
a wide card is decoration the thumb has to hunt for. The HeroUI form footer
(small buttons on the trailing edge, TRAILING) exists only as an explicit
choice for a dialog whose body is the point and whose buttons only close it.
Full-width actions share one height, the medium control (48), which is also
the touch floor; text sits 20 above the actions, the reference example's own
spacing.

## 4. Feedback States

Model only applicable states; do not force every control into seven named
states.

| Family | Required behavior |
| --- | --- |
| Action | Default, pressed, release/cancel, disabled; loading where supported |
| Field | Focus, editing, disabled, read-only where supported, validation and helper content |
| Selection | Checked/unchecked, disabled, press/drag/cancel; mixed state where supported |
| Overlay | Enter/exit, dismissal, interruption, ownership and focus restoration |

Disabled appearance is applied once and must also suppress interaction —
alpha alone is not disabling. Read-only stays readable without pretending to
be disabled. Hover applies only where a pointer exists. Touch targets aim for
at least 44 logical units even when the visual mark is smaller; compact
exceptions must be explicit.

**Press feedback is component-owned.** `LocalIndication` cannot deliver it:
Kuikly creates and delegates the indication node, but nothing it does reaches
the view — measured on the simulator, a draw over the content, a draw under it
and a layer transform from a `LayoutModifierNode` all produced zero changed
pixels with an opaque red probe. So every tappable thing is a component: a row
is a `Cell`, a button is a `Button`, a choice is a `SegmentedControl` or
`ToggleButton`; anything else wraps `PressableFeedback`, and a component with
its own surface may use `rowPressFeedback` (fill) or `pressScale`. The shipped
values are a width-compensated 0.985 scale plus a 10% `#3f3f46` / `#d4d4d8`
highlight drawn above the content.

## 5. Shadows, Borders And Materials

`DecoratedSurface` renders ordered outer/inset shadow stacks, signed spread
and complex borders (solid, dashed, dotted, double, groove, ridge, outset,
inset, custom dash) on rectangular/rounded outlines without changing measured
content size. Blur is a bounded Gaussian edge approximation, not an exact 2D
blur.

Glass is optional, off by default (`RuntimeFlags.materialPolicy =
MaterialPolicy.Never`) with an opaque fallback. It is not a refraction effect
and not proof of platform parity. KuiklyUI supports Gaussian blur on all four
renderers through the core `BlurView`, but four gaps block turning it on by
default (findings pending an upstream KuiklyUI report):

| # | Gap | Severity | Effort |
| --- | --- | --- | --- |
| 1 | `blurRadius` is scaled differently by every renderer | blocking | small |
| 2 | iOS is locked to `UIBlurEffectStyleLight` — no dark material | blocking | small |
| 3 | iOS radius saturates: anything ≥ 10 renders identically | high | small |
| 4 | No `Modifier.blur` / backdrop-blur in the compose layer | low (workaroundable) | medium / impossible on iOS |

The GearUI side is built and verified (`foundation/material/` and the sample's
Material Probe page); turning it on is one default. Do not introduce blur
where it hurts text contrast or motion performance.

## 6. Overlay And Surface Elevation

A floating surface must read as a step above the page it floats over —
including on a page that paints `surface` edge to edge (a conversation list
whose every row is `surface`, for example). If a menu lands on exactly its own
colour, separation is lost, so GearUI's own values enforce it:

| | GearUI rule | Measured |
| --- | --- | --- |
| Dark `overlay` | Lighter than `surface`, never identical | (32,32,35) against (24,24,27) over a `surface` card |
| Light `overlay` | `surface` is already pure white, so separation relies on shadow | — |
| Light `overlay-shadow` | Strengthened to 6% / 3% / 10% black; geometry (offset, blur) unchanged | edge on white measures 231, versus 247 at weaker values |

Both modes draw the top `inset` layer as a real 1dp border rather than a
blurred inset shadow: run through the shadow renderer it lands on the edge
pixel at roughly a fifth of its declared alpha, leaving a menu with no visible
edge on a white page. As a border it measures 219 on white (light) and 77
against a (32,32,35) menu (dark), both on device. These are GearUI's own
values; a change is a token change with a recorded measurement, not an edit to
a component literal.

## 7. Accessibility

Every control a user can operate must be reachable by name and by state.

- **Name.** Text content is the name. Icon-only controls (CloseButton, a
  rating star, an upload tile) carry `contentDescription` from `I18n` — never
  a literal.
- **State.** Selection and on/off state go in `stateDescription` from `I18n`.
  Do not rely on the `Selected` semantics flag: Kuikly's bridge appends a
  hardcoded Chinese "已选择" for it, repeating the state and ignoring the app's
  language.
- **Role.** `Role.Button` on tappable non-text targets, `Role.Tab` on
  segmented and tab cells.
- **Decoration stays silent.** An icon inside a labelled control passes a null
  description; an empty string is not the same thing — it joins the parent's
  announcement as a stray separator.

Semantics reach the platform automatically (`KuiklySemantisHandler`), so no
host wiring is needed, and the result is checkable without a screen reader —
`idb ui describe-all` lists every label, role and state.

## 8. Platform Boundary

Mobile-first does not mean browser behavior is ignored. iOS, Android, Web and
HarmonyOS have separate evidence; compile success on one target is not visual
or interaction acceptance on another. Fonts, blur and rendering require
explicit adapters. Known renderer limits: hot rounded-to-square native border
refresh does not propagate (reload required), and the Web host's live
viewport-resize behavior needs separate validation.
