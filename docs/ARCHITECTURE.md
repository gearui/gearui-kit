# GearUI Kit Architecture

[English](./ARCHITECTURE.md) | [简体中文](./ARCHITECTURE.zh-Hans.md)

GearUI Kit is a Kotlin Multiplatform UI framework built on Kuikly Compose. One
codebase renders to native views on iOS, Android, Web (JS) and HarmonyOS. It is
published as `com.gearui:gearui-kit` (currently `1.0.0-beta8`) and consumed by
`privchat-ui` and other product layers.

## Layer Stack

```text
Application Layer          View / GearApp — one App root per page tree
Component Layer            com.gearui.components.* — 70+ components
Foundation Layer           tokens / primitives / interaction / layout
Runtime Services           Theme · I18n · Overlay · Navigator
─────────────────────────────────────────────────────────────
Kuikly Compose             renderer bridge: each Compose node becomes a
                           native view (UIView / android.view / DOM / ArkUI)
```

Each layer only depends on the ones below it. Components never mount their own
Theme/Overlay roots; runtime services never reach into component internals.

## Kuikly Integration

- **Dependency direction.** GearUI compiles against the published Kuikly
  artifacts (`com.tencent.kuikly-open:*`, 2.28.0 across runtime, renderers, KSP
  and iOS Pods). Kuikly source is never patched or vendored; behavior gaps are
  worked around in GearUI or recorded for upstream (see the blur findings in
  [VISUAL_SPEC.md](./VISUAL_SPEC.md)).
- **Rendering model.** Kuikly maps every Compose node to a real platform view.
  Consequences GearUI designs around: `LocalIndication` never reaches the view
  (press feedback is component-owned), heavy off-screen subtrees are destroyed
  and rebuilt on the main thread (pager keep-alive is explicit), and semantics
  are copied to native views automatically by `KuiklySemantisHandler`.
- **Event and field semantics.** `safeAreaInsets`, size-change and density
  fields keep Kuikly's meaning. New capabilities arrive with a documented
  fallback; existing fields are never reinterpreted.

## How Consumers Reference GearUI

| Consumer | Mechanism |
| --- | --- |
| `privchat-app` | `includeBuild("../gearui-kit")` in `settings.gradle.kts` with dependency substitution — source-level composite build for kit, `privchat-ui` and `privchat-sdk-kotlin` |
| External libraries | Maven Central artifact `com.gearui:gearui-kit:<version>` (Android AAR, JS, iOS arm64/simulatorArm64/x64 KLibs) |
| HarmonyOS | Parallel configuration in `settings.ohos.gradle.kts`; separate build and explicit device evidence |

A composite build does not test Maven consumption: releases are verified from
staged artifacts by an independent consumer fixture as well.

## Module And Directory Map

- `gearui-kit/` — the library. Source under
  `src/commonMain/kotlin/com/gearui/`: `components/` (one directory per
  component), `foundation/` (tokens, primitives, interaction, layout, motion,
  material), `theme/`, `i18n/`, `navigation/`, `runtime/`, `App.kt` / `View`.
- `sample/` — integration evidence, not framework internals. One App root,
  real library components only, no ComingSoon placeholders.
- `tokens/` — DTCG 2025.10 source JSON, generated Kotlin and the token
  contract (`tokens/README.md`).
- `scripts/` — token generator, icon generator and `scripts/ci/check_*.sh`
  guards.

## Runtime Contracts

**Single App root.** Pages enter through `GearApp`; business code never mounts
a second Theme/Overlay runtime. A `View` subclass that calls `App(...)` itself
must override `autoWrapApp() = false`.

**Insets.** Host measurements flow through `RuntimeEnvironment` — the only
safe-area source. `safeArea` excludes the keyboard; `keyboard` is separate
geometry from platform host callbacks. Roots stay full-size; safe-area padding
belongs to content (`PageScaffold`), never to the root canvas.

**Overlays.** Created and destroyed through the Overlay runtime, which owns
the scrim, stacking, Back/outside/route/timeout dismissal and exactly-once
removal. Local panel gestures request host dismissal; they do not duplicate
global listeners.

**Application page model (required).** An app built on GearUI has one Kuikly
`@Page` whose `App(...)` holds one `Navigator`; every screen is a typed `NavRoute`
entry (its parameters live in the route, such as `Chat(channelId)`), reached with
`push` and left with `pop`. Not allowed: switching screens with
`when(currentPage)`, a Kuikly page per screen (`RouterModule.openPage`), or a
hand-written swipe back. The reference is privchat-app's `PrivChatRouteHost` and
`PrivChatNavGraph`.

What this buys:

- The same finger-tracking back on all four platforms. Android, the Web and
  HarmonyOS have no system swipe back, and iOS's starts only at the screen edge;
  `Navigator` tracks the finger 1:1 from anywhere, reveals the previous page, and
  arbitrates with horizontal pagers inside the page.
- One runtime. Theme, language, overlays and session state live in one
  composition, so a theme change restyles every page at once, and an overlay
  belongs to the entry that opened it and closes when that page leaves.
- No start-up cost per navigation: no new native controller or Activity, no Kuikly
  page bootstrap, and parameters are typed objects rather than serialized strings.
- One BACK path: Android BACK pops page by page and reaches the host only at the
  bottom of the stack.

Costs and limits: the top entry and the one right beneath it stay composed (the
latter hidden, parked, so a swipe reveals it without building it mid-gesture);
deeper entries leave composition and keep their state through
`SaveableStateHolder` and `RetainedEntry`. The iOS back feel is GearUI's, not the
system `interactivePopGestureRecognizer`. A deep link pushes its route at start-up,
with Home still at the bottom.

**Navigation and swipe back.** `Navigator` is a real stack with per-entry
render. The right-swipe is arbitrated so exactly one owner consumes it:

1. On touch-down the router asks the front page through `PageSwipeBackGate`.
   A paged surface (e.g. `TabPager`) answers "I can still scroll" while its
   current page is not the first, and the router stands down completely.
2. At the page's first page — or when the page has no horizontal paging — the
   router claims the drag in the Initial pass across the full width and moves
   the card 1:1 with the finger.
3. At the bottom of the stack the router lets go entirely, so the pager keeps
   its overscroll tension in both directions.
4. `TabPager` keeps one page on each side composed
   (`beyondViewportPageCount = 1`); with Kuikly's default of 0 a heavy page
   re-entering the viewport costs ~180 ms of main-thread native view creation
   mid-drag.
5. The entry beneath the top stays composed and hidden (parked). It used to be
   built only once the gesture was recognised, which on a device stalled the main
   thread for about 450 ms while the page ignored the finger; parked, the next frame
   comes about 12 ms after recognition. A parked page is hidden from screen readers,
   and the front page takes the hit test, so a tap on blank space never reaches it.

System BACK is bridged by the runtime, not by `androidx.activity` handlers.

## Hard Constraints (PR Gate)

Reject: duplicated global runtime ownership, root-canvas inset clipping,
hidden API breaks, unreviewed baseline changes, silently ignored token values,
new hardcoded design values, missing packaged assets, unverified
compatibility/parity claims, clipped icon+badge bounds in navigation slots,
screen switching or swipe back built around `Navigator`.

Require evidence for: extra allocations, platform branches, motion/blur cost,
new theme extension points, new assets, new platform targets. Ratchet
baselines freeze known debt; passing them is not proof the debt is gone.

Full encapsulation rules: [COMPONENT_SPEC.md](./COMPONENT_SPEC.md).

## Document Index

| Document | Owns |
| --- | --- |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Layers, Kuikly integration, consumption model, runtime contracts |
| [DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md) | Token pipeline, theme axes, color/geometry/typography/motion rules |
| [I18N.md](./I18N.md) | Layered language runtime and typed string packs |
| [VISUAL_SPEC.md](./VISUAL_SPEC.md) | GearUI visual language, anatomy, feedback, materials, accessibility |
| [COMPONENT_SPEC.md](./COMPONENT_SPEC.md) | Component encapsulation, API consistency, CI gates, review guardrails |
| [COMPONENT_COVERAGE.md](./COMPONENT_COVERAGE.md) | Complete component inventory, coverage against HeroUI Native, gaps and priorities |
| [QUALITY_STATUS.md](./QUALITY_STATUS.md) | 1.0.0 goals, verified evidence, open risks, release procedure |
