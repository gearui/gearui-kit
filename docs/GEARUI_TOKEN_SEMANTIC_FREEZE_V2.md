# GearUI Token Semantic Freeze v2

Status: FROZEN (Phase 0)
Date: 2026-02-21
Scope: GearUI theme and component token usage

## 1. Purpose

This document freezes the semantic token contract for shadcn-style alignment. Component refactors must use this contract.

## 2. Global Semantic Color Tokens (Layer A)

Required token pairs:

- `background` / `foreground`
- `card` / `cardForeground`
- `popover` / `popoverForeground`
- `primary` / `primaryForeground`
- `secondary` / `secondaryForeground`
- `muted` / `mutedForeground`
- `accent` / `accentForeground`
- `destructive` / `destructiveForeground`

Required singletons:

- `border`
- `input`
- `ring`

Domain extensions (kept):

- `success`
- `warning`
- `info`
- `mask`
- `inverseSurface`
- `inverseOnSurface`

## 3. Transitional Mapping (Current -> v2 Semantic)

| v2 token | current GearColors source |
|---|---|
| `background` | `background` |
| `foreground` | `textPrimary` |
| `card` | `surface` |
| `cardForeground` | `textPrimary` |
| `popover` | `overlay` (fallback: `surface`) |
| `popoverForeground` | `textPrimary` |
| `primary` | `primary` |
| `primaryForeground` | `onPrimary` |
| `secondary` | `surfaceVariant` |
| `secondaryForeground` | `textPrimary` |
| `muted` | `surfaceComponent` |
| `mutedForeground` | `textSecondary` |
| `accent` | `primaryLight` |
| `accentForeground` | `primary` |
| `destructive` | `danger` |
| `destructiveForeground` | `textAnti` (fallback: `onPrimary`) |
| `border` | `border` |
| `input` | `stroke` |
| `ring` | `primary` |

## 4. Component Role Tokens (Layer B)

Components must consume role tokens, not raw global tokens.

Required role set:

- `container`
- `contentPrimary`
- `contentSecondary`
- `interactiveDefault`
- `interactiveHover`
- `interactiveActive`
- `focusRing`
- `disabledContainer`
- `disabledContent`
- `borderDefault`
- `borderStrong`

## 5. State Rules (All Interactive Components)

- `default`: role token default value
- `hover`: same semantic family, stronger contrast or opacity
- `active`: one step stronger than hover
- `focus`: must show `ring`
- `disabled`: must use `disabledContainer + disabledContent`
- `invalid`: must use `destructive` family + destructive ring

## 6. Hard Constraints

1. No new `Color(0x...)` in components (transparent-only exceptions allowed by whitelist).
2. No direct `Theme.colors.*` in business component bodies after migration; use component role tokens.
3. No replacing whole theme spec to emulate local overrides; semantic merge only.
4. New components must define state mapping for `default/hover/active/focus/disabled/invalid`.

## 7. Compatibility Policy

- Keep old color fields for one minor version.
- New fields are additive first.
- Old fields are deprecated with migration notes before removal.
