# GearUI Design Audit

Status: Draft  
Branch: `codex/design-system-audit-2026`  
Mode: Read-only audit. No component code changes.  
Baseline observed: KuiklyUI `2.19.0-2.1.21`.

## Method

This audit reviewed source structure, token definitions, and representative components. It focuses on design-system governance before 1.0 rather than visual tweaks.

The working tree already contained unrelated modified files from ongoing work. This audit does not attempt to classify or revert those changes.

## Executive Summary

GearUI Kit already has useful foundations: `GearApp` owns theme/runtime/i18n/overlay/keyboard dismissal, the current color values are converging on a clean surface/content/interaction semantic model, and several token/spec files exist.

The main issue is governance: token layers are not yet enforceable. Components can still mix `Theme.colors`, component specs, direct dp values, local focus behavior, and one-off runtime logic. That makes the design language fragile even if individual screens look acceptable.

The highest-risk work before 1.0 is not "make every component pretty"; it is to freeze token ownership, remove business-specific tokens from core, and establish component-family rollout rules.

## P0: Architecture-Level Issues

### P0-1 Core `Colors` Contained Business Chat Tokens

Evidence:

- Historical core `Colors` fields: `bubbleSelf`, `onBubbleSelf`, `bubbleOther`, and `onBubbleOther`.
- Current product extension: `/Users/zoujiaqing/projects/privchat/privchat-ui/src/commonMain/kotlin/com/netonstream/privchat/ui/common/base/PrivChatThemeWidget.kt`

Current state:

The chat bubble fields have been removed from GearUI core `Colors`. PrivChat now owns these semantics through a product-level `ChatColors` extension.

Impact:

The public theme model couples a generic UI kit to IM/chat product semantics. This makes GearUI harder to use outside PrivChat and makes 1.0 token names harder to freeze.

Recommendation:

Keep chat-specific tokens outside GearUI core. Core GearUI should expose generic semantic tokens only; product libraries should add their own domain extensions when needed.

### P0-2 Token Layer Ownership Is Overlapping

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/Spacing.kt:8`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/layout/SpacingTokens.kt:25`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/ComponentSpecs.kt:19`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/tokens/ComponentTokens.kt:28`

Current state:

Spacing exists both as float constants and as `Dp` values. Component dimensions exist in `ComponentSpecs`, `foundation/list/*Tokens`, and `foundation/tokens/ComponentTokens`.

Impact:

There is no single source of truth. A component author can choose whichever layer is convenient, so audit rules like "use tokens" are ambiguous.

Recommendation:

Adopt the four-layer token model from `DESIGN_SYSTEM_SPEC.md`: primitive, semantic, component, runtime. Then mark old overlapping layers as migration targets.

### P0-3 Radius Scale and Shape Contract Conflict

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/theme/Shapes.kt:14`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/theme/Shapes.kt:55`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/button/Button.kt:85`

Current state:

`Shapes.kt` says components must not hardcode `RoundedCornerShape(xx.dp)`, but `Button.kt` directly uses `RoundedCornerShape(8.dp)`. The current shape scale is `3/6/9/12/full`, while the GearUI mobile design language requires a first-class `8dp` level.

Impact:

The system says one thing and components do another. This causes inconsistent rounding across Card, Button, Cell, Input, Select, and overlays.

Recommendation:

Freeze a radius scale of `0/4/6/8/12/full`, then route component radius through component tokens. Treat direct radius construction in components as an exception.

### P0-4 Runtime Interaction Boundary Is Blurry

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/App.kt:54`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:63`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:101`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:118`

Current state:

`GearApp` provides a global keyboard dismiss policy, but `SearchBar` also owns local `LocalFocusManager`, keyboard controller, pointer gesture handling, drag detection, and click-to-focus behavior.

Impact:

Runtime behavior can diverge per component. Fixes for keyboard dismissal or gesture interaction may need to be repeated across Input, Textarea, SearchBar, overlay contents, and pages.

Recommendation:

Runtime owns global tap/scroll keyboard dismissal. Input-family components should only expose focus intent and local IME action behavior. Any local workaround should be documented as Kuikly-specific.

Constraint:

Do not assume runtime can intercept every gesture before verifying Kuikly's native handler priority. Recent gesture defects suggest some native scroll/input handlers may outrank Compose `Modifier` handlers, so local component workarounds may still be necessary. The governance rule should be "centralize by default, document platform workarounds," not "delete all local handling."

### P0-5 Overlay and Safe Area Must Be Centralized

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/overlay/OverlayHost.kt`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/App.kt:92`

Current state:

Overlay hosting and safe-area behavior exist in runtime, but recent defects show Drawer/ActionSheet/sheet safe area is still easy to misapply at page level or content level.

Impact:

If every overlay component solves safe area independently, some overlays will cover the wrong area, start from the wrong edge, or leave status/nav gaps.

Recommendation:

Define one overlay coordinate model: scrim/root covers the full app viewport; safe area is applied only inside overlay content where the component contract requires it. Audit all overlay-family components together.

### P0-6 Token Model Must Remain Target-Agnostic Until Web Path Is Confirmed

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/sample/jsApp/src/jsMain/kotlin/Main.kt`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/sample/h5App/src/jsMain/kotlin/Main.kt`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/sample/jsApp/src/jsMain/kotlin/components/KuiklyRenderView.kt`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/sample/h5App/src/jsMain/kotlin/components/KuiklyRenderView.kt`

Current state:

The repository contains JS/H5 renderer scaffolding, but the design-system token model is currently discussed mostly through iOS/Android behavior.

Impact:

If Web uses renderer-specific components or KRComponent fallback paths, component tokens that assume native Compose behavior may not transfer cleanly. Overlay, pointer, focus, safe area, and input behavior are the first likely divergence points.

Recommendation:

Keep Primitive/Semantic/Component tokens target-agnostic. Put platform-specific behavior into Runtime tokens and platform adapters until the Web Compose path is confirmed.

## P1: User-Visible Consistency Issues

### P1-1 Input Family State and Hit-Area Rules Are Not Unified

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:90`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:100`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:141`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/textarea/Textarea.kt:309`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/textarea/Textarea.kt:317`

Current state:

SearchBar has local height, border, padding, icon sizes, and focus logic. Textarea has separate hardcoded radius and padding. Input has had repeated hit-area defects.

Impact:

Input, SearchBar, and Textarea can look and behave like separate products. Users notice this immediately through focus rings, padding, placeholder position, and click target behavior.

Recommendation:

Create one Input-family state model: default, focused, disabled, invalid, readonly. Then map Input/SearchBar/Textarea to shared input component tokens.

### P1-2 Button Size, Radius, and Icon Rules Are Partly Direct-Coded

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/button/Button.kt:55`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/button/Button.kt:65`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/button/Button.kt:85`

Current state:

Button still defines multiple sizes and shapes directly in component code.

Impact:

Button becomes a local design system. Any future spacing/radius/token decision has to be manually reconciled with this component.

Recommendation:

Use `ButtonTokens` as the only internal source for button height, padding, icon size, loading size, and radius.

### P1-3 Card and Cell Density Need One Policy

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/ComponentSpecs.kt:64`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/ComponentSpecs.kt:168`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/list/CardTokens.kt:28`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/list/CellTokens.kt:28`

Current state:

Card and Cell tokens exist, but padding/min-height/radius are split between `ComponentSpecs` and component-specific token files. Card default elevation is `2f` while the intended visual language prefers border-first cards.

Impact:

Cards and list rows can feel oversized or uneven. This is especially visible on iPhone-sized screens.

Recommendation:

Define density roles: `comfortable`, `default`, `compact`. Card default should be border-first with no heavy shadow. Cell min height and padding should be documented as list-family tokens.

### P1-4 Overlay Family Needs One Visual Contract

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/overlay/OverlayHost.kt`

Current state:

Dialog, Popup, BottomSheet, ActionSheet, Drawer, Select, and Cascader all need consistent scrim, surface, radius, border, shadow, safe area, and entrance motion behavior.

Impact:

Overlay bugs are high-visibility and high-friction. They also compound because overlays are often nested over scrollable pages and input fields.

Recommendation:

Audit overlay components as a family before changing any one overlay. Define shared `OverlayTokens`, `SheetTokens`, `PopoverTokens`, and runtime safe-area behavior.

### P1-5 Primitive Reuse Needs Stricter Boundaries

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/badge/BadgeTokens.kt`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/typography/IconTokens.kt`

Current state:

Badge, Icon, Avatar, and Text primitives have token files, but downstream components can still implement local sizes/colors.

Impact:

Small inconsistencies accumulate: icon grids, nav icons, badges, loading indicators, and control icons drift apart.

Recommendation:

Freeze primitive tokens first, then force component-family mappings.

## P2: Token and Numeric Consistency Issues

### P2-1 Hardcoded Numeric Usage Is Widespread

Evidence:

- Component source file count: `60`.
- Component files matching numeric/style patterns: `57`.
- Total component matches for `\d+.dp`, `RoundedCornerShape(number)`, `durationMillis = number`, `tween(number)`, or `shadow(`: `615`.
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/backtop/BackTop.kt:102`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/backtop/BackTop.kt:118`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/backtop/BackTop.kt:238`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/select/Select.kt:59`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/select/Select.kt:207`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/select/Select.kt:213`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:90`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:141`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/textarea/Textarea.kt:309`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/textarea/Textarea.kt:317`

Current state:

This is a broad scan, not a final violation count. Some matches are legitimate component geometry, but the density of matches shows that component-local numbers are common and need classification.

Impact:

Without classifying allowed vs disallowed locations, "no hardcoded values" is not enforceable.

Recommendation:

Add an audit rule:

- Allowed: token files, theme files, documented primitives.
- Discouraged: component implementation files.
- Rejected: hardcoded colors in components.

Then run a second pass that classifies each match as token definition, geometry requirement, or design-system violation.

### P2-2 Motion Tokens Are Not the Default Source

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/animation/Animation.kt`

Current state:

Animation helpers exist, but components can still use local durations.

Impact:

Interactions feel inconsistent, especially across overlays, select menus, sliders, and switches.

Recommendation:

Define `MotionTokens` and update components by family after the spec is approved.

### P2-3 Icon Sizes Need Semantic Roles

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:151`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/searchbar/SearchBar.kt:214`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/ComponentSpecs.kt:29`

Current state:

Icon sizes appear in Button specs and directly in SearchBar.

Impact:

Control icons, nav icons, inline icons, and decorative icons can drift.

Recommendation:

Start with only `sm`, `md`, `lg`, and optional `xl`. Component tokens should map to these base sizes. Avoid position-specific icon roles such as `nav` or `avatarAccessory` until multiple components require them.

### P2-4 Color Names Are Not Yet Fully Aligned to the Target Model

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/theme/Colors.kt:16`

Current state:

The values are converging on the GearUI semantic surface model, but names still mix TDesign-style states (`primaryHover`, `primaryActive`), content roles (`textPrimary`), and surface roles (`surfaceComponent`).

Impact:

It is hard to decide whether a component should use `surface`, `surfaceComponent`, `input`, `card`, or `muted`. That leads to local taste decisions.

Recommendation:

Freeze a GearUI semantic color model before RC. If mobile still needs hover/active variants, they should be component-state tokens rather than core semantic colors.

## P3: Long-Term Cleanup

### P3-1 Naming Conventions Need One Rule

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/button/ButtonEnums.kt:6`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/components/button/ButtonEnums.kt:8`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/theme/Theme.kt:10`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/theme/Theme.kt:12`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/overlay/OverlayPlacement.kt:11`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/overlay/OverlayPlacement.kt:13`

Current state:

`ButtonSize` uses uppercase enum values such as `LARGE`, while `ThemeMode` and `OverlayPlacement` use PascalCase values such as `Light` and `TopLeft`.

Impact:

This is not visually urgent, but it matters before public API freeze.

Recommendation:

Adopt one Kotlin-facing convention before 1.0 RC.

### P3-2 Deprecated and Transitional Token Classes Need Review

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/badge/BadgeColors.kt:13`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/badge/BadgeColors.kt:22`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/avatar/AvatarColors.kt:12`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/tab/TabColors.kt:14`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/gearui-kit/src/commonMain/kotlin/com/gearui/foundation/tokens/ComponentTokens.kt:351`

Current state:

Badge, Avatar, Tab, and Tag-related token/color abstractions include deprecated migration paths toward `Theme.colors`.

Impact:

Users may copy stale examples or depend on transitional APIs.

Recommendation:

Inventory deprecated APIs after P0/P1 decisions. Remove or clearly mark transitional APIs before RC.

### P3-3 Samples Should Become Design-System Verification Tools

Evidence:

- `/Users/zoujiaqing/projects/privchat/gearui-kit/sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt:27`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt:119`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/sample/src/commonMain/kotlin/com/gearui/sample/pages/HomePage.kt:75`
- `/Users/zoujiaqing/projects/privchat/gearui-kit/sample/src/commonMain/kotlin/com/gearui/sample/pages/HomePage.kt:123`

Current state:

The sample registers many individual component examples in `ComponentConfig`, and the home page is a component index. It is not yet structured as a design-system verification gallery with required cross-cutting scenarios.

Impact:

Visual regressions are found by accident rather than by repeatable review.

Recommendation:

Add sample sections for light/dark, states, density, overlays, input focus, safe areas, and runtime keyboard behavior.

## Rollout Cost Matrix

This matrix is an initial planning estimate. It should be refined after Open Decisions in `DESIGN_SYSTEM_SPEC.md` are resolved.

| Batch | Scope | Existing Scope Signal | Public API Impact | Risk |
|---|---|---:|---|---|
| 1 | Theme, spacing, radius, motion, elevation | Multiple overlapping token/spec files | Breaking if public token names change | High |
| 2 | Text, icon, badge, avatar primitives | Primitive token files already exist | Medium, mostly token/API cleanup | Medium |
| 3 | Button, checkbox, radio, switch, slider | 5 control families | Medium, size/state token changes likely | Medium |
| 4 | Input, search bar, textarea, form | 4 input/form families | High, focus/state behavior and tokens | High |
| 5 | Overlay, dialog, popup, bottom sheet, action sheet, select | 6+ overlay/select families | High, runtime and safe-area behavior | High |
| 6 | Card, cell, list item, section header | 4 list/container families | Medium, density and padding defaults | Medium |
| 7 | NavBar, BottomNavBar, PageScaffold, runtime insets | Navigation/runtime surface | High, safe area and page shell contracts | High |

## Recommended Next Steps

1. Review and approve `/Users/zoujiaqing/projects/privchat/gearui-kit/docs/DESIGN_SYSTEM_SPEC.md`.
2. Decide whether GearUI core should remove IM/chat-specific tokens before 1.0 RC.
3. Expand this audit into a full component-family matrix after the design baseline is approved.
4. Start implementation with Batch 1 only: theme, spacing, radius, motion, elevation.
5. Freeze public token names before `1.0.0-rc1`.

## Explicit Non-Action

This audit intentionally does not modify component code. It is a governance baseline for deciding what to change next.
