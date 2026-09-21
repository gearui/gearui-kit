# GearUI Visual Design Specification

[English](./VISUAL_SPEC.md) | [简体中文](./VISUAL_SPEC.zh-Hans.md)

This document owns the visual reference and its rules: what GearUI looks like,
where each value comes from, and the recorded deviations. Token mechanics and
theming live in [DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md).

## 1. Visual Identity

GearUI adopts the default appearance and control feedback of pinned
open-source **HeroUI Native 1.0.9**. This supersedes the Tamagui and iOS-only
identities preserved in git history. Alignment covers anatomy, density,
typography, surfaces, selection indicators, pressed/focus/disabled feedback
and motion interruption. Color matching alone is not acceptance. Do not invent
universal glow, haptics, scaling or bottom sheets; components without a
reference counterpart remain explicit GearUI extensions.

GearUI keeps its KMP/Kuikly runtime and Kotlin API conventions. Paid
templates are never imported; HeroUI Pro/Web editor screenshots illustrate
customization goals, not shipped Native presets. Applicable notices are
retained when incorporating upstream code.

## 2. Source Lock And Provenance

- Reference: HeroUI Native **1.0.9**, commit
  `b9fa5410b5fbb875475127386166f4c029e9e73f`.
- `tokens/reference/heroui-native.lock.json` records 42 file hashes. Verify a
  local checkout with
  `node scripts/check_heroui_reference.mjs /path/to/heroui-native`. This
  optional audit must not make CI depend on a sibling repository. A passing
  hash check proves provenance, not visual parity.

| Concern | Reference path |
| --- | --- |
| Semantic palette, fields, shadow values | `src/styles/variables.css` |
| Derived colors and radius roles | `src/styles/theme.css` |
| Button geometry | `src/styles/components/button.css` |
| Button default variant | `src/components/button/button.tsx` |
| Press feedback and interruption | `src/components/pressable-feedback/pressable-feedback.animation.ts` |
| Input surface/geometry | `src/styles/components/input.css` |
| Field composition and state | `src/components/text-field/text-field.tsx` |
| Select presentation | `src/components/select/select.tsx` |
| Bottom-sheet gestures | `src/helpers/internal/components/bottom-sheet-content.tsx` |

Resolve upstream units and aliases before generating Kotlin. Never transcribe
promotional screenshots as source values.

## 3. Component Anatomy

**Fields.** Labels sit above standalone fields by default; explicit horizontal
form layouts remain supported. The editor owns its surface and focus
treatment; helper/error and count text sit below it. Limit counters are not
errors. Fields are borderless with a field shadow by default — on white
cards/surfaces use the filled variant (`cardStyle = true`).

**Select and hierarchical selection.** A field trigger plus a layered option
surface with clear selected/disabled states, aligned indicators, bounded
scrolling and safe placement. Popover is the reference default; sheet/dialog
are explicit presentations, not automatic phone substitutions.
TreeSelect/Cascader are GearUI extensions: preserve hierarchy, expansion and
selection semantics rather than imitating a desktop tree.

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
its own surface may use `rowPressFeedback` (fill) or `pressScale`. The
reference values are HeroUI Native's: width-compensated 0.985 scale and a 10%
`#3f3f46` / `#d4d4d8` highlight above the content.

## 5. Shadows, Borders And Materials

`DecoratedSurface` renders ordered outer/inset shadow stacks, signed spread
and complex borders (solid, dashed, dotted, double, groove, ridge, outset,
inset, custom dash) on rectangular/rounded outlines without changing measured
content size. Blur is a bounded Gaussian edge approximation, not an exact 2D
blur.

Glass is optional, off by default (`RuntimeFlags.materialPolicy =
MaterialPolicy.Never`) with an opaque fallback. It is not Liquid Glass
refraction and not proof of platform parity. KuiklyUI supports Gaussian blur
on all four renderers through the core `BlurView`, but four gaps block the
default (findings pending an upstream report):

| # | Gap | Severity | Effort |
| --- | --- | --- | --- |
| 1 | `blurRadius` is scaled differently by every renderer | blocking | small |
| 2 | iOS is locked to `UIBlurEffectStyleLight` — no dark material | blocking | small |
| 3 | iOS radius saturates: anything ≥ 10 renders identically | high | small |
| 4 | No `Modifier.blur` / backdrop-blur in the compose layer | low (workaroundable) | medium / impossible on iOS |

The GearUI side is built and verified (`foundation/material/` and the sample's
Material Probe page); turning it on is one default. Do not introduce blur
where it hurts text contrast or motion performance.

## 6. Recorded Deviations

Values that do not match the locked reference, with the reason and measurement
behind them. A deviation not listed here is a bug.

**Overlay elevation on a full-bleed `surface` page.** The reference separates
a floating surface from the page by colour: its pages sit on `--background`,
so `--overlay` (= `--surface` in the default theme) already reads a step
above them and `--overlay-shadow` is decorative. That fails on a page that
paints `surface` edge to edge — the menu lands on exactly its own colour. The
reference's own worked theme (`docs/theming.md` upstream) shows the intended
lever: `--overlay` distinct from and lighter than `--surface` in both modes.

| | Reference default | GearUI | Why |
| --- | --- | --- | --- |
| Dark `overlay` | `oklch(0.2103 …)`, = `surface` | `oklch(0.243 …)` | The documented lever. Over a `surface` card, measured (32,32,35) against (24,24,27) — was identical. |
| Light `overlay` | `white`, = `surface` | `white` | Unchanged: `surface` is already pure white. |
| Light `overlay-shadow` | 2% / 1% / 3% black | 6% / 3% / 10% | The only cue left in light. Measured edge on white: level 231 vs 247 at reference values. Geometry unchanged. |

Both modes also paint the reference's `inset` layer as a real 1dp border
rather than a blurred inset shadow: run through the shadow renderer it lands
on the edge pixel at roughly a fifth of its declared alpha, leaving a menu
with no visible edge on a white page. As a border it measures 219 on white
(light) and 77 against a (32,32,35) menu (dark), both on device.

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
