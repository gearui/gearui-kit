# HeroUI Native Reference Map

This document owns source provenance only. Current rules live in
[Design System Spec](DESIGN_SYSTEM_SPEC.md); current gaps live in the
[component matrix](COMPONENT_ACCEPTANCE_MATRIX.md) and [beta3 report](BETA3_RELEASE_READINESS.md).

## Source Lock

Open-source HeroUI Native **1.0.9**, commit
`b9fa5410b5fbb875475127386166f4c029e9e73f`.
`tokens/reference/heroui-native.lock.json` records 42 file hashes.
Run `node scripts/check_heroui_reference.mjs /path/to/heroui-native` to verify a
local checkout. This optional source audit must not make CI depend on a sibling
repository or paid templates. A passing hash check proves provenance, not visual parity.

## Source Map

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

Paths are relative to the locked repository. Resolve upstream units and aliases
before generating Kotlin. Do not transcribe promotional screenshots as source values.

## GearUI Boundaries

GearUI owns API families, safe areas, keyboard/focus policy, overlay lifetime and
navigation. Tokens do not replace these responsibilities. GearUI components with
no exact counterpart (including ActionSheet, TreeSelect and Tour) adopt shared
visual roles while retaining documented mobile behavior. HeroUI Pro/Web editor
screenshots illustrate customization goals, not shipped Native presets.

The former stage-by-stage execution diary and Tamagui-era implementation counts
are retained in `_archive/pre-beta3/`; they are not current acceptance.

## Recorded Deviations

Values that do not match the locked reference, with the reason and the measurement
behind them. A deviation not listed here is a bug.

### Overlay elevation on a full-bleed `surface` page

The reference separates a floating surface from the page by **colour**: its pages sit
on `--background`, so `--overlay` (= `--surface` in the default theme) already reads a
step above them, and `--overlay-shadow` is decorative — 2–3% black in light, a 20% white
inset hairline in dark. That holds only while the page is `background`. A page that
paints `surface` edge to edge — a conversation list whose every row is a surface — puts
the menu on exactly its own colour, and what is left is a shadow that was never doing
the work.

The reference's own worked theme in `docs/theming.md` shows the intended lever: it
gives `--overlay` a value distinct from and lighter than `--surface` in both modes
(light `0.998` vs `0.98`, dark `0.23` vs `0.2`). The two roles are meant to differ;
the default theme collapsing them is a property of its page layering, not a rule.

So:

| | Reference default | GearUI | Why |
| --- | --- | --- | --- |
| Dark `overlay` | `oklch(0.2103 …)`, = `surface` | `oklch(0.243 …)` | The documented lever. Over a `surface` card, measured (32,32,35) against (24,24,27) — was identical. |
| Light `overlay` | `white`, = `surface` | `white` | Unchanged: `surface` is already pure white, so light has no headroom above it. |
| Light `overlay-shadow` | 2% / 1% / 3% black | 6% / 3% / 10% | The only cue left in light. Measured: the edge on white reaches level 231, from 247 at reference values. Geometry (offsets, blur) is unchanged. |

Both modes also paint the reference's `inset` layer as a real border rather than
through the shadow renderer. Run as a blurred inset shadow it lands on the edge pixel
at roughly a fifth of its declared alpha — the Gaussian spreads it and the shape's own
antialiasing takes the rest — so on a white page a menu had no edge at all, and the
diffuse drop shadow alone did not read as a boundary. Painted as a 1dp border it is
what the declaration says: light measures 219 on white, dark 77 against a (32,32,35)
menu, both on device.
