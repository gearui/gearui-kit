# GearUI Component Color Role Matrix (Phase 0)

Status: FROZEN
Date: 2026-02-21

## 1. Usage Model

Each component maps to role tokens first, then role tokens map to semantic global tokens.

## 2. Matrix

| Component group | container | contentPrimary | contentSecondary | interactiveDefault | interactiveHover | interactiveActive | borderDefault | focusRing |
|---|---|---|---|---|---|---|---|---|
| Page / NavBar | `card` | `foreground` | `mutedForeground` | `accent` | `accent`(stronger) | `accent`(strongest) | `border` | `ring` |
| Card / Cell / List / Table | `card` | `cardForeground` | `mutedForeground` | `secondary` | `accent` | `accent`(stronger) | `border` | `ring` |
| Button (primary) | `primary` | `primaryForeground` | `primaryForeground` | `primary` | `primary`(90%) | `primary`(80%) | `primary` | `ring` |
| Button (secondary/ghost) | `secondary`/transparent | `secondaryForeground` | `mutedForeground` | `secondary` or `accent` | `accent` | `accent`(stronger) | `border` | `ring` |
| Input / Textarea / Select | `background` | `foreground` | `mutedForeground` | `input` | `border` | `ring` | `input` | `ring` |
| Checkbox / Radio / Switch / Slider | `background`/`input` | `foreground` | `mutedForeground` | `primary` (checked) | `primary`(90%) | `primary`(80%) | `input` | `ring` |
| Dialog / Popover / Sheet | `popover` | `popoverForeground` | `mutedForeground` | `accent` | `accent`(stronger) | `accent`(strongest) | `border` | `ring` |
| Toast / Snackbar | `inverseSurface` or `popover` | `inverseOnSurface` or `popoverForeground` | `mutedForeground` | `accent` | `accent`(stronger) | `accent`(strongest) | `border` | `ring` |
| Success/Warning/Error feedback | `success`/`warning`/`destructive` (light bg variants allowed) | `foreground` or paired foreground | `mutedForeground` | semantic-matched | semantic-matched | semantic-matched | semantic-matched | semantic-matched |

## 3. Priority Components (Mandatory in first migration wave)

- `button`
- `input`
- `select`
- `textarea`
- `dialog`
- `toast`
- `popover`
- `backtop`
- `imageviewer`
- `tour`

## 4. Explicit Exceptions (Phase 0)

Allowed temporarily:

- `Color.Transparent`
- legacy fields in `Theme.colors` consumed by old components

Not allowed:

- hardcoded opaque color values in component files
- ad-hoc local color rules bypassing role tokens
