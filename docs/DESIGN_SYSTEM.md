# GearUI Design System

[English](./DESIGN_SYSTEM.md) | [简体中文](./DESIGN_SYSTEM.zh-Hans.md)

This document owns the token pipeline and theming rules. GearUI's own visual
rules live in [VISUAL_SPEC.md](./VISUAL_SPEC.md); numeric defaults come from
`tokens/` source JSON and generated code — never from a second prose schema.

## 1. Token Pipeline

```text
DTCG 2025.10 source JSON (tokens/)
  → scripts/generate_tokens.py
    → generated Kotlin constants
      → primitive values → semantic roles → component roles
```

- DTCG 2025.10 is the exchange format. `tokens/README.md` owns the exact
  format, resolver, color conversion and renderer limits.
- Edit source JSON and regenerate; do not hand-edit generated Kotlin. Source,
  generated code and reviewed token snapshots must agree (`check_token_compat.sh`).
- Unknown or unsupported values must fail explicitly or use a documented
  conversion policy. A renderer limit must not masquerade as an invalid token.
- Removed Float token pools must not be resurrected (`check_legacy_token_pool.sh`).

## 2. Independent Theme Axes

Brand accent, shape style and light/dark mode are independent axes:

- Changing accent must not change shapes, metrics, typography, motion or
  navigation; changing shape must not reset accent.
- Rounded and square presets share one component state machine and anatomy.
  Intentional circles, capsules and fixed indicators keep separate semantic roles.
- Blue is an acceptable built-in accent, not a platform restriction.
- Application overrides propagate to descendants and overlays. Explicit
  component overrides are preserved, not silently re-derived on theme change.
- Theme updates must not reset navigation, editor text/focus, or dismiss an
  open overlay.
- Tokens tune style; a JSON file does not replace layout, gestures or
  lifecycle. Not every geometry role is runtime-injectable today — that is an
  implementation gap, not a license to claim live replacement works everywhere.

## 3. Color And Surface Roles

Use business-neutral `Colors` pairs: background/content, surface/content,
card/content, popover/content, brand/content and semantic status/content.
Public field names are defined by source and the reviewed API snapshot, not by
a duplicate prose schema.

- Page background, content surface and floating surface are distinct roles.
- Secondary text is not a border color. Disabled borders must not become
  darker than enabled borders by reusing muted text colors.
- Error is semantic validation, not a red brand accent. Loading, selection,
  read-only and disabled are different states.
- Product concepts (chat bubbles etc.) belong in application theme extensions,
  not in the kit.
- Custom `ThemeSpec.buttonColors` / `inputColors` remain explicit overrides.
  When deriving from new base colors use the derivation API; do not copy a
  built-in theme and retain its old component palette by accident.

Components read semantic tokens only: no hardcoded colors, radius, elevation,
border widths, icon sizes or spacing literals (six CI ratchets enforce this;
see [COMPONENT_SPEC.md](./COMPONENT_SPEC.md)).

## 4. Geometry And Typography

Numeric defaults live in `ControlGeometry`, token JSON and generated profiles.

- Components use semantic shape/size/spacing/border roles — never copied
  literals, and never Spacing values masquerading as radius or elevation.
  Spacing describes layout; size roles describe control geometry. Unrelated
  axes must not move together just because numbers coincide.
- Components read `Theme.typography`. `TextStyle` carries font family
  candidates, weight, size, line height and tracking.
- `LocalFontRegistry` maps custom names to fonts installed by the host; it
  does not load assets. Unsupported custom fonts fall through to
  generic/system candidates. Identical font metrics across OSes are not
  promised.
- Large text and long translations must not truncate controls or obscure
  actions; verify separately.

## 5. Motion

Durations come from the motion scale (milliseconds): `instant` 0, `fast` 100,
`normal` 150, `slow` 200, `emphasized` 250 — micro-interactions use fast, most
state changes use normal, overlay/dialog reveal uses slow, bottom-sheet settle
and major scene transitions use emphasized. Overlay entrance/exit is owned by
the runtime. Components must not invent one-off durations; use a token or
expose one through the component's own `XxxTokens` class. Springs are
tokenized. Reduced motion removes movement, not state feedback.

## 6. Materials And Shadows

Card and MaterialSurface share `DecoratedSurface`: ordered outer and inset
shadow stacks, signed spread, complex borders on rectangular/rounded outlines.
Blur is a bounded Gaussian edge approximation, off by default
(`MaterialPolicy.Never`) pending upstream agreement — the four-renderer gap
list lives in [VISUAL_SPEC.md](./VISUAL_SPEC.md). `LocalSurfaceShadowStyles`
overrides stacks independently of accent and shape.

Legacy single-elevation consumers use named Elevation roles until explicitly
migrated. Neither "all cards must be flat" nor "every component has new
shadows" is the rule; each migration records visual evidence.

## 7. Icons

`Icon(Icons.house)` draws an icon; `fill = true` switches to its fill form (an icon
without one keeps its regular form); `tint` defaults to the theme's foreground, so an
icon reads in light and dark without a colour at every call; `size` takes an
`IconSizes` step.

```kotlin
import com.gearui.components.icon.*

Icon(Icons.heart, fill = liked, tint = Theme.colors.destructive)
Button(icon = Icons.star.filled, ...)   // a parameter has no fill switch: `.filled`
```

**Built-in icons are code.** `Icons` is every Phosphor icon, regular and fill, as
path data in `components/icon/generated/`, written by `scripts/gen_vector_icons.py`
from Phosphor's flat SVGs and drawn by GearUI on a canvas — sharp at any size. Each
icon is an extension property (hence the import, as with Compose's Material icons),
so the compiler leaves out every icon an app does not reference: always on iOS and
the Web, on Android when R8 runs. There is no string lookup; a name the code does not
spell cannot be kept.

**Other sets use the same contract.** An `IconSource` resolves to a `VectorIcon`
(path data, drawn) or an `ImageIcon` (an image the app ships). A pack is an object of
getters — getters, not stored properties, so one icon does not keep the pack:

```kotlin
object AppIcons {
    val logo: IconSource get() = ImageIcon("logo", "assets://icons/logo.png", fill = "assets://icons/logo_fill.png")
}
Icon(AppIcons.logo, tint = Color.Unspecified)   // keep an image's own colours
```

## 8. Runtime Insets Are Not Tokens

`safeArea` and `keyboard` geometry are environment measurements from
`RuntimeEnvironment`, never style constants. Do not encode device chrome in
token JSON.

## 9. Governance

- A rule has one owner: this document owns theming rules; VISUAL_SPEC owns
  the visual rules; `tokens/README.md` owns format policy.
- Ratchets may shrink, not grow. Zero-baseline checks are hard gates.
- Public token API changes require migration notes
  ([QUALITY_STATUS.md](./QUALITY_STATUS.md)) and an API baseline review;
  deleting or renaming strings/token fields is a breaking change blocked by
  `binary-compatibility-validator`.
