# GearUI Component Encapsulation Specification

[English](./COMPONENT_SPEC.md) | [简体中文](./COMPONENT_SPEC.zh-Hans.md)

How a component is built, named, documented, guarded and accepted. Visual
values come from [VISUAL_SPEC.md](./VISUAL_SPEC.md); token mechanics from
[DESIGN_SYSTEM.md](./DESIGN_SYSTEM.md).

## 1. Structure

- One directory per component under
  `gearui-kit/src/commonMain/kotlin/com/gearui/components/<name>/`.
- Components compose Foundation primitives (Text, Icon, Badge,
  DecoratedSurface, PressableFeedback) instead of copying badge overflow,
  typography or surface implementations.
- Component-specific values live in the component's own `XxxTokens` class,
  derived from semantic tokens.
- The first line of a component body reads the theme:

```kotlin
@Composable
fun MyComponent(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val colors = Theme.colors          // always first
    // semantic tokens only — never Color(0x...), never bare .dp design values
}
```

Forbidden in the component layer: hardcoded colors, radius, elevation, border
widths, icon sizes, spacing literals and static typography. Six CI ratchets
enforce frozen baselines; new violations fail the build.

## 2. API Consistency (frozen boundary)

Keep consistent parameter meanings and controlled state/callback ownership.
Do not mechanically reorder existing APIs merely for uniformity. The
enabled/disabled polarity is a frozen semantic boundary, enforced by
`check_state_param_naming.sh`:

- **Field family** (Input, Textarea, Select/MultiSelect, Cascader,
  date/time input triggers): `enabled: Boolean = true` plus `error: String?`.
- **SearchBar**: `enabled`, no invented validation-error parameter.
- **Action family** (Button, Tag, SwipeCell actions, sheet items) deliberately
  keeps `disabled: Boolean = false`.
- Never expose both `enabled` and `disabled` on one component family.
- Option-model `disabled` (e.g. `SelectOption.disabled`) describes the option,
  not the containing control; it is not a component switch.
- Error text and display-variant enums are different semantics; parameter-name
  counting alone cannot classify them.

Do not propose renaming these — it is a frozen boundary, not legacy
inconsistency.

## 3. State And Interaction

- State feedback follows the family table in
  [VISUAL_SPEC.md](./VISUAL_SPEC.md); model only applicable states.
- Every tappable target has real press feedback (component-owned; see the
  `LocalIndication` finding in VISUAL_SPEC §4). `check_press_feedback.sh`
  fails a tap target with no response; `check_sample_uses_components.sh`
  fails a raw tap modifier in the sample.
- Local control gestures are allowed; private global event listeners that
  duplicate runtime dismissal/scroll/Back ownership are not.
- Overlays go through the Overlay runtime; a component never installs its own
  scrim or Back handling.
- Insets are read from `RuntimeEnvironment`; a component never re-parses host
  measurements or consumes an inset its scaffold already consumed.

## 4. Accessibility

Icon-only controls carry `contentDescription` from `I18n`; state goes in
`stateDescription`; roles per VISUAL_SPEC §7. Decorative icons inside labelled
controls pass a null description.

## 5. Documentation

- Code comments are English only (`check_english_comments.sh`); UI text uses
  typed i18n packs (`check_i18n_default_text.sh`, zero-literal baseline).
- Every component gets a guide with: one-line purpose, minimal use,
  recommended use, parameter table, boundaries ("does not own…"), and common
  questions. Runtime-boundary questions (Overlay / safeArea / Theme) point at
  [ARCHITECTURE.md](./ARCHITECTURE.md) instead of re-explaining.
- Document language/status explicitly; a Chinese document is never
  mislabeled as a synchronized English edition.

## 6. Sample As Integration Evidence

- The sample keeps normal mobile page navigation and demonstrates actual
  library components, not copied visuals.
- Every public index entry resolves to a real example; no ComingSoon
  placeholders in new coverage (`check_sample_index.sh`).
- One App root (`check_single_app_root.sh`); sample pages never mount another
  App/OverlayRoot or a competing Theme runtime
  (`check_sample_runtime_boundary.sh`).
- Insets, theme, i18n and overlays use the same runtime as consumers.
- Diagnostic fixtures are named, opt-in, and never the normal release entry.

**Page standard.** The sample is the code people copy, so a page is held to the
same rules as a component:

- **Frame.** `ExamplePage` with `ExampleSection`s. A section is `Card` for loose
  controls and `Plain` for a component that is a surface itself (CellGroup,
  Card, List, Collapse, Alert, NoticeBar, Calendar, Table, Result…). Never a
  card inside a card.
- **Tokens only.** Spacing, shapes, border widths, icon sizes, typography and
  colours come from the theme and scales — no `Color(0x…)`, no bare `dp`. A
  dimension that *is* the demo (an image placeholder's height, a custom size
  being shown off) is a named constant at the top of the file.
- **Real components only.** A button is a `Button`, a row is a `Cell`, an icon
  is `Icons.*`. No hand-built lookalikes, no letters or emoji standing in for
  icons, no bare `clickable` without press feedback.
- **Fields on cards** use the filled variant (`cardStyle = true`), as the
  visual spec requires.
- **Overlay triggers.** A set of entries that open a non-anchored overlay
  (Dialog, ActionSheet, BottomSheet, Toast, Notification, Snackbar, Drawer,
  Tour, full-screen Loading) is a `CellGroup` of rows with an arrow, one row
  per variant, on a `Plain` section — the platform catalogue pattern. An
  anchored overlay (Tooltip, Popover, ContextMenu, an anchored Popup) needs a
  real trigger to point at, so it keeps a `Button` at its default width.
- **Copy.** Titles and descriptions in Simplified Chinese, the sample's default
  language; API and component names verbatim. One capability per section; the
  description says what to look at in one line, not how the code works.

## 7. Migration Notes

Public API changes require migration notes: list source, binary and behavioral
changes separately, with before/after calls where needed. Do not imply that
recompilation fixes a removed symbol or a changed behavior. Deleting or
renaming public fields is blocked by `binary-compatibility-validator`
(`apiCheck` on macOS CI verifies JVM and KLib baselines; never run `apiDump`
in a verification job).

## 8. Executable Quality Gates

Workflows implement checks; reports record results. No static check
establishes complete visual, accessibility or performance parity.

### 8.1 Static gates (`scripts/ci/`)

All `check_*.sh` run in the Guardrails job except sample-index (SDK Guard).

| Check | Enforces | Limit |
| --- | --- | --- |
| `check_component_hardcoded_colors.sh` | Semantic colors, frozen legacy baseline | Does not prove contrast |
| `check_component_hardcoded_radius.sh` | Named shapes; no spacing-as-radius | Not live-shape rendering |
| `check_component_hardcoded_elevation.sh` | Named legacy elevations | Does not ban token-driven layered Card shadows |
| `check_component_hardcoded_border.sh` | Named border widths | Not every geometric separator |
| `check_component_hardcoded_spacing.sh` | No growth of legacy literals | Existing ratchet debt remains |
| `check_component_hardcoded_icon_size.sh` | Named icon sizes | Avatar dimensions are a different role |
| `check_component_static_typography.sh` | Theme typography consumption | Does not install fonts |
| `check_legacy_token_pool.sh` | No resurrection of removed Float token pools | Not all token governance |
| `check_token_compat.sh` | Reviewed token snapshot matches source | Updated baseline is not old-binary compatibility |
| `check_state_param_naming.sh` | No enabled/disabled polarity conflict | Model flags and display enums are distinct |
| `check_material_surfaces.sh` | Shared material ownership | Not a blur/visual test |
| `check_sheet_grabber.sh` | Shared sheet grabber use | Not drag cancellation |
| `check_safearea_runtime_contract.sh` | Insets resolver and floating-bar contract | Requires device verification too |
| `check_sample_runtime_boundary.sh` | App runtime entry, no competing wrappers | Source check, not runtime proof |
| `check_single_app_root.sh` | One sample App root | Does not scan every external application |
| `check_i18n_default_text.sh` | No localized literals outside library packs | Not translation quality |
| `check_english_comments.sh` | English source comments | Code strings handled separately |
| `check_emoji_as_icon.sh` | No text glyph substitutes for icons | Not actual packaged rendering |
| `check_icon_registry.sh` | Constants/registry/assets agreement | Installed app still needs inspection |
| `check_ios_pod_resources.sh` | iOS resource-copy build phase | Not proof of clean-install assets |
| `check_readme_component_index.sh` | Generated README index matches registry | Counts are not acceptance |
| `check_sample_index.sh` | Routes/examples/component coverage | Not per-example behavior |

Ratchets may shrink, not grow to conceal a regression. Exact scope and
allowlists live in each script.

### 8.2 Tests and builds

- Guardrails: `python3 scripts/generate_tokens.py --check` and the
  `scripts/tests` Python suite.
- Android CI: `:gearui-kit:testDebugUnitTest`, `:gearui-kit:lintDebug`.
- Web CI: `:gearui-kit:jsBrowserTest` (missing Chrome is a setup failure,
  never a silent skip).
- macOS CI: `:gearui-kit:apiCheck`; iOS simulator Kotlin tests via
  `scripts/ios_native_tests.sh` against the real Kuikly host.
- Builds: Android kit/sample, iOS simulator sample link, Web host. Release
  packaging checks all six Maven modules from the exact candidate.

### 8.3 Manual gates

Input/keyboard behavior, overlay dismissal and gesture interruption, live
theme changes, large text, real asset loading, screen readers and frame
performance need runtime evidence. Track them in
[QUALITY_STATUS.md](./QUALITY_STATUS.md); never count compilation as
interaction.

## 9. New Component Checklist

1. Directory + tokens class; theme read first; semantic tokens only.
2. API polarity per §2; controlled state and callbacks per family.
3. Press feedback and accessibility per §3–4.
4. Sample example wired into the index; guide written per §5.
5. Registry entry (icons/assets if any) so `check_icon_registry.sh` and
   `check_readme_component_index.sh` pass.
6. Update [COMPONENT_COVERAGE.md](./COMPONENT_COVERAGE.md) and the acceptance
   status in [QUALITY_STATUS.md](./QUALITY_STATUS.md).
7. Device verification on at least one platform before claiming acceptance.
