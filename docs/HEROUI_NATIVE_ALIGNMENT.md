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
