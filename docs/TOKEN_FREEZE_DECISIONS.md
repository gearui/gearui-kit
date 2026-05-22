# GearUI Token Freeze Decisions

Status: Approved for 1.0 token freeze  
Branch: `codex/design-system-audit-2026`  
Mode: Decision document only. No component implementation changes are implied here. Component-family rollout begins next.

## Approval Status

Approved for 1.0 token freeze implementation.

After Batch 1 starts, changes to public token names require explicit migration notes and API baseline review. Amendments to this document must be recorded as new dated entries below, not in-place edits to approved sections.

## Background

GearUI Kit 1.0 must ship with a frozen public token model. After 1.0 RC, public token names, field lists, scales, and ownership boundaries are not allowed to change without a major version bump.

This document records the decisions taken before component-family implementation begins, so that downstream libraries (`privchat-ui`, `live-chat`, `lms-app`, etc.) can plan against a stable target.

The design language behind these decisions is defined in `DESIGN_SYSTEM_SPEC.md` §0.1 (GearUI Design Language).

## Decision 1: Colors — GearUI semantic color model, frozen once

**Decision:** GearUI core `Colors` is normalized into the GearUI semantic color model below. This is the final shape for 1.0. No further reshuffles are planned after RC.

**Final public field list:**

```kotlin
data class Colors(
    val background: Color,
    val foreground: Color,
    val surface: Color,
    val surfaceForeground: Color,
    val card: Color,
    val cardForeground: Color,
    val popover: Color,
    val popoverForeground: Color,
    val primary: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val secondaryForeground: Color,
    val muted: Color,
    val mutedForeground: Color,
    val accent: Color,
    val accentForeground: Color,
    val destructive: Color,
    val destructiveForeground: Color,
    val border: Color,
    val input: Color,
    val ring: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
)
```

**Explicitly removed from core `Colors`:**

- `primaryHover`, `primaryActive`, `primaryLight`, `primaryDisabled` — interaction states belong to component-state tokens, not the core palette. Mobile has no Web-style hover; pressed/active/selected/focused are component concerns.
- `bubbleSelf`, `onBubbleSelf`, `bubbleOther`, `onBubbleOther` — already moved to product-level extensions (e.g. `privchat-ui` `ChatColors`).
- TDesign-style direct state colors (`*Hover`, `*Active`, `*Click`) — replaced by `ComponentTokens.<role>.<state>` where needed.

**State handling rule:**

- All component states (`default / hover / pressed / focused / disabled / invalid / selected / loading`) live in their component's token class, never in core `Colors`.
- A component derives its state colors by composing core semantic colors plus opacity / blend rules.

**Migration impact:** This is a large public API break versus the existing 39-field `Colors`. It is acceptable because 1.0 has not shipped publicly yet, and this is the only window to redefine.

---

## Decision 2: Shapes — `0 / 4 / 6 / 8 / 12 / full`

**Decision:** GearUI public radius scale is six values:

```kotlin
data class Shapes(
    val none: Shape,    // 0.dp
    val sm: Shape,      // 4.dp
    val md: Shape,      // 6.dp
    val lg: Shape,      // 8.dp
    val xl: Shape,      // 12.dp
    val full: Shape,    // 9999.dp / CircleShape
)
```

**Rationale:**

- Adds a first-class `8.dp` step (`lg`) — currently `Button.kt` already uses `RoundedCornerShape(8.dp)` directly, violating the existing `3/6/9/12` scale (`DESIGN_AUDIT.md` P0-3).
- `8.dp` is the GearUI mobile design language default for buttons, cards, inputs, sheets. Without it on the public scale, components keep hardcoding values.
- `4.dp` replaces `3.dp` — `3.dp` was odd and visually indistinguishable from `4.dp` on most devices.

**Migration impact:** Replaces the existing `small=3 / default=6 / large=9 / extraLarge=12 / round=9999 / circle=CircleShape` set. Field names change (`small → sm`, `default → md`, `large → lg`, `extraLarge → xl`, `round/circle → full`), so this is a source-breaking rename. Acceptable for 1.0.

---

## Decision 3: Spacing — `foundation.layout.SpacingTokens` is the only source

**Decision:** `foundation/layout/SpacingTokens.kt` is the single public spacing source for 1.0. `Spacing.kt` and the spacing portions of `ComponentSpecs.kt` are migration-only and removed before RC.

**Public scale (already exists in `SpacingTokens.kt`, reaffirmed):**

```
0 / 2 / 4 / 6 / 8 / 12 / 16 / 20 / 24 / 32 / 40 / 48
```

**Token flow:**

```
Primitive spacing (SpacingTokens)
  → ComponentTokens (e.g. ButtonTokens.paddingHorizontal = Spacing.md)
    → Components
```

**Migration rule:**

- Components must not import `Spacing.kt`. Use `ComponentTokens.<role>.<role-specific-spacing>` instead.
- Direct `padding(8.dp)` / `Spacer(width = 12.dp)` inside component implementation files is an audit finding unless the component is itself defining a token.

**Migration impact:** Existing call sites that reference `Spacing.spacer8` etc. will need to migrate. Component-family rollout (Batch 1 onwards) handles this.

---

## Decision 4: Motion — new `MotionTokens`

**Decision:** Introduce a new public `MotionTokens` with four named durations:

```kotlin
data class MotionTokens(
    val instant: Int = 0,       // No animation; immediate state change
    val fast: Int = 100,        // Subtle micro-interactions: button press feedback, icon toggle
    val normal: Int = 150,      // Default for most state changes: tab selection, input focus ring
    val slow: Int = 200,        // Overlay/dialog reveal, content swap
    val emphasized: Int = 250,  // Bottom sheet drag/drop settle, major scene transition
)
```

(Stored as `Int` milliseconds for direct use with `tween(durationMillis = ...)`; consumers wrap into `Duration` only if needed.)

**Rationale:**

- Overlay, Switch, Select, Dialog, BottomSheet, Tabs currently use one-off `200ms` / `250ms` / `300ms` values (`DESIGN_AUDIT.md` P2-2). Unified scale removes "designer roulette."
- Apple/UIKit-style mobile motion is restrained and predictable. The shorter scale (max 250ms) reflects mobile interaction expectations.

**Rule:**

- Components must not define inline `tween(durationMillis = <number>)`. Use `MotionTokens.<role>` or `ComponentTokens.<role>.motion`.
- Overlay entry/exit animations are owned by Runtime, which selects `MotionTokens.slow` or `.emphasized` by default.

---

## Decision 5: ComponentTokens kept, ComponentSpecs deprecated

**Decision:** `ComponentTokens` is the canonical home for component-level role tokens (height, padding, icon size, radius, motion, state colors). `ComponentSpecs.kt` becomes migration-only and is removed before 1.0 RC.

**Why ComponentTokens wins:**

- Aligns with the four-layer model in `DESIGN_SYSTEM_SPEC.md` §2 (Primitive → Semantic → Component → Runtime).
- Already in active use for tokenized components.
- Naming reflects design-system intent ("tokens"), not just sizing constants.

**Why ComponentSpecs goes:**

- Early-stage sizing constant pool; values duplicate what `ComponentTokens` should own.
- Two parallel sources mean component authors pick whichever is convenient, defeating governance (`DESIGN_AUDIT.md` P0-2).

**Migration path:**

- Each component family migrates its `ComponentSpecs` entries into the matching `XxxTokens` class during its Batch rollout.
- `ComponentSpecs.kt` is marked `@Deprecated("Migrated to component tokens; will be removed before 1.0 RC")` while migration is in progress.
- Once empty, the file is removed.

---

## Rollout Cost (informational)

These decisions affect the following ownership areas. Implementation happens batch-by-batch per `DESIGN_SYSTEM_SPEC.md` §12, not all at once.

| Decision | Files touched (rough) | BCV impact | Component-family batches that pick it up |
|---|---|---|---|
| Colors model freeze | `theme/Colors.kt`, `theme/Theme.kt`, light/dark presets, every component that reads state colors | Major — public field list changes | All batches; primitives first (Batch 2) |
| Shapes scale rename | `theme/Shapes.kt`, every component using shape tokens | Major — field rename | Batch 1 |
| Spacing single source | `Spacing.kt` → deprecated, `ComponentSpecs.kt` spacing entries migrated | Source-breaking for direct `Spacing.spacer*` users | Batch 1, then per-component |
| Motion tokens | New `MotionTokens.kt`, every component with inline `tween` | Additive at first; component motion refactor follows | Batch 5 (overlays) first, then per-component |
| ComponentTokens vs ComponentSpecs | `ComponentSpecs.kt` deprecated; per-component token files filled out | Source-breaking for direct `ComponentSpecs.*` users | Batch 2 onwards |

## Acceptance

These five decisions are the GearUI 1.0 token freeze. This document is now signed off:

- Public token names defined here cannot change before 1.0 GA without a separate decision record amending this file.
- Component-family rollout (Batch 1: Theme/Spacing/Radius/Motion/Elevation) starts on the basis of these decisions.
- The next `DESIGN_AUDIT.md` revision (P3 cleanup, P2 numeric stats) can reference these decisions as fixed assumptions.

## Open Items After Token Freeze

These remain to be resolved during or after Batch rollout, not before:

- Elevation token names (placeholder: `e0 / e1 / e2 / floating`) — needs designer review of actual shadow values.
- Density modes (`comfortable / default / compact`) — needs at least one component family (Cells / Cards) to validate the abstraction before promoting it to public API.
- Brand pack mechanism (`SPEC 2026 §7.1`) — separate decision, not part of token freeze.

## Deprecation Debt Burn-down

Single source of truth for tracking 1.0 token migration progress.

Counts are `:gearui-kit:compileDebugKotlinAndroid --rerun-tasks 2>&1 | grep "is deprecated" | wc -l` after each batch lands.

| Batch | Scope | Warnings Before | Warnings After | Δ |
|---|---|---|---|---|
| Batch 1 (token-law) | Colors / Shapes / Spacing / Motion / ComponentSpecs deprecation | n/a | **788** (baseline) | — |
| Batch 2 | Text / Icon / Badge / Avatar primitives | 788 | 771 | −17 |
| Batch 3 | Button / Checkbox / Radio / Switch / Slider | 771 | 688 | −83 |
| Batch 4 | Input / SearchBar / Textarea / Form | 688 | 633 | −55 |
| Batch 5 | Overlay 族: Dialog / Popup / BottomSheet / ActionSheet / Select / Popover / ContextMenu | 633 | 483 | −150 |
| Batch 6 | 容器/列表族: Card / Cell / ListItem / SectionHeader | 483 | 460 | −23 |

Notes:

- Batch 1 introduces the deprecation surface; the 788 baseline reflects the layered bridges covering existing call sites.
- Batch 2 Δ comes from `BadgeTokens` / `AvatarTokens` dropping `BadgeSpecs` / `AvatarSpecs` references (11) plus deleting unused legacy `BadgeColors` / `AvatarColors` files (6). Text and Icon primitives were already clean.
- Batch 3 maps the five form controls onto the new semantic color names (`textPrimary→foreground`, `danger→destructive`, `surfaceVariant→muted`, disabled/state colors → `mutedForeground`/`muted`), shape scale (`small→sm`, `default→md`, `circle→CircleShape`), and spacing source (root `Spacing.spacer8.dp` → `foundation.layout.Spacing.sm`). No public API change, so BCV baseline is unaffected.
- Batch 4 maps the input family (Input/SearchBar/Textarea/Form) with the same color/shape rules (adds `surfaceComponent→surface`, `round→full`, `large→lg`). Pure token migration only — the deeper input-family state model and SearchBar local-focus rework (AUDIT P0-4 / P1-1) are intentionally NOT done here; `input` and `ring` semantic tokens stay reserved for that later polish. No public API change.
- Batch 6 maps the container/list family. Card (`primitives/composite/Card.kt` + `foundation/list/CardTokens.kt`) drops `CardSpecs`; `CardDefaults.Default` inlines the frozen values (cornerRadius 9→8dp = the frozen `large→lg` step, padding 12dp, borderWidth 0.5dp) and its `elevation` default 2f→0f to match the border-first contract (Card never renders a shadow, only a hairline border — no new elevation semantic introduced). Cell (`components/cell/Cell.kt`, `primitives/composite/Cell.kt`, `foundation/list/CellTokens.kt`) and ListItem map colors onto semantic tokens (`textPrimary→foreground`, `textSecondary/textPlaceholder/textDisabled→mutedForeground`), pressed bg `surfaceVariant→muted`, and drop `CellSpecs` (paddingHorizontal inlined to 16dp). SectionHeader is pure color mapping, no API change. Intentionally deferred: the two parallel Cell APIs (`composite/Cell` vs `components/cell/Cell`) have divergent density/title-subtitle typography ramps — unifying them is component-consolidation, not token migration, so it is a later polish. Table/Timeline/Tree are explicitly out of scope. No public API change.
- Batch 5 maps the overlay family (Dialog/Popup/BottomSheet/ActionSheet/Select/Popover/ContextMenu) with the same color/shape/spacing rules, plus three overlay-specific moves: (1) `colors.mask` (deprecated, "scrim is a runtime token") → new `OverlayDefaults.scrimColor` in the overlay layer, and `OverlayHost`'s scrim fallback now reads it too — this is Decision 1's runtime-owned scrim landing; (2) `OverlayPlacement.BottomStart` (deprecated) → `BottomLeft`; (3) `colors.inverseSurface` in Popover → `foreground` (behavior-preserving; the dark-tooltip → `PopoverTokens` rework per AUDIT P1-4 "Overlay Family 统一视觉契约" is intentionally deferred). ContextMenu's archived design changes (leading `icon`, `IntrinsicSize.Max` width, padding 8→12/6→10, `BodySmall→BodyMedium`) land in this batch alongside its token migration. One additive public API: `OverlayDefaults` (BCV baseline updated, no break).
