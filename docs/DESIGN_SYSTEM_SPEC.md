# GearUI Design System Spec

Status: current beta3 design contract. Implementation and acceptance are separate.
Start at [SPEC](SPEC.md); platform evidence lives in the release and acceptance reports.

## 0. Design Decisions

### 0.1 Visual Identity - Decided

GearUI adopts the default appearance and control feedback of pinned open-source
HeroUI Native 1.0.9. The source lock and component mapping are in
[HeroUI Native Alignment](HEROUI_NATIVE_ALIGNMENT.md). This supersedes the
Tamagui and iOS-only default decisions preserved in the archive.

Alignment includes anatomy, density, typography, surfaces, selection indicators,
pressed/focus/disabled feedback and motion interruption. Color matching alone is
not acceptance. Do not invent universal glow, haptics, scaling or bottom sheets.
Components without a reference counterpart remain explicit GearUI extensions.

GearUI retains its KMP/Kuikly runtime and Kotlin API conventions. Do not import
paid templates or claim HeroUI Pro/Web presets belong to open-source Native.
Retain applicable notices when incorporating upstream code.

### 0.2 Independent Theme Axes

- Brand accent and overall style are independent. Changing accent must not change
  shapes, metrics, typography, motion or navigation; changing shape must not reset accent.
- Blue is an acceptable built-in accent, not a platform restriction.
- Rounded/square presets share one component state machine and anatomy. Intentional
  circles, capsules and fixed indicators have separate semantic roles.
- Application overrides propagate to descendants and overlays. Preserve explicit
  component overrides rather than silently re-deriving them on each theme change.
- Tokens tune style; a JSON file does not replace layout, gestures or lifecycle.
- Not every geometry role is runtime-injectable today. Treat that as an implementation
  gap, not a claim that all JSON values can already be replaced live.

### 0.3 Platform Boundary

Mobile-first does not mean browser behavior is ignored. iOS, Android, Web and
HarmonyOS have separate evidence. Compile success on one target is not visual or
interaction acceptance on another. Fonts, blur and rendering require explicit adapters.

## 1. Token Ownership

Primitive values feed semantic roles, which feed component roles. DTCG 2025.10
is the exchange/data contract; [tokens/README.md](../tokens/README.md) owns exact
format, resolver, color and Kotlin conversion policy. Runtime insets and keyboard
geometry are environment measurements, never style constants.

Edit source JSON and regenerate; do not hand-edit generated Kotlin. Unknown or
unsupported values must fail explicitly or use a documented, selected conversion
policy. A renderer limit must not masquerade as an invalid standard token.

## 2. Color And Surface Roles

Use business-neutral `Colors` pairs for background/content, surface/content,
card/content, popover/content, brand/content and semantic status/content. Public
field names are defined by source and API snapshots, not a duplicate prose schema.
Product concepts such as chat bubbles belong in application theme extensions.

- Page background, content surface and floating surface have distinct roles.
- Secondary text is not a border color. Disabled borders must not become darker
  than enabled borders by reusing muted text colors.
- Error is semantic validation, not a red brand accent. Loading, selection,
  read-only and disabled are different states.
- Custom `ThemeSpec.buttonColors`/`inputColors` remain explicit overrides. When
  deriving from new base colors, use the appropriate derivation API rather than
  copying a built-in theme and accidentally retaining an old component palette.

## 3. Geometry And Typography

Numeric defaults live in `ControlGeometry`, token JSON and generated profiles.
Components use semantic shape/size/spacing/border roles, not copied literals or
Spacing values masquerading as radius/elevation. Spacing describes layout;
component size roles describe control geometry. Never move unrelated axes together.

Components read `Theme.typography`. `TextStyle` carries font family candidates,
weight, size, line height and tracking. `LocalFontRegistry` maps custom names to
fonts installed by the host; it does not load assets. Unsupported custom fonts
fall through to generic/system candidates. Identical font metrics across OSes
are not promised. Large text and long translations must not truncate controls
or obscure actions; verify them separately.

## 4. Feedback And Accessibility

Model only applicable states; do not force every control into seven named states.

| Family | Required behavior |
| --- | --- |
| Action | Default, pressed, release/cancel, disabled; loading where supported |
| Field | Focus, editing, disabled, read-only where supported, validation and helper content |
| Selection | Checked/unchecked, disabled, press/drag/cancel; mixed state where supported |
| Overlay | Enter/exit, dismissal, interruption, ownership and focus restoration |

Use shared token-driven feedback. Disabled appearance is applied once and must
also suppress interaction; alpha alone is not disabling. Read-only retains readable
content without pretending it is disabled. Reduced motion removes movement, not
state feedback. Hover applies where a pointer exists; it is not a mobile gesture.

Aim for at least 44 logical units of touch area even when the visual mark is
smaller. Compact exceptions must be explicit. Keyboard, semantics and screen-reader
support need platform checks; decorative polish does not establish accessibility.

## 5. Component Anatomy

### Fields

Labels are above standalone fields by default; explicit horizontal form layouts
remain supported. The editor owns its surface and focus treatment. Helper/error
and count text sit below it. Sample descriptions must not repeat placeholders
or wrap each field in redundant card/cell frames. Limit counters are not errors.

### Select And Hierarchical Selection

Use a field trigger and a layered option surface with clear selected/disabled
states, aligned indicators, bounded scrolling and safe placement. Popover is the
reference Select default; sheet/dialog are explicit presentations, not an automatic
substitution for every phone. TreeSelect/Cascader are GearUI extensions: preserve
hierarchy, expansion and selection semantics rather than imitating a desktop tree.

### Cards And Lists

A card owns one surface, shape, padding and optional tokenized shadow/border.
CellGroup owns grouping and separators; Cell does not create another card around
its control. Avoid nested decorative frames in sample pages. Reuse Text/Icon/Badge
primitives, including their sizing and count-overflow logic.

### Navigation Bars

NavBar is a cross-platform page component, not an H5-specific widget. Sample
previews do not wrap it in Cells. Titles remain single-line with bounded actions
and ellipsis. Top safe-area background follows the top bar. Android's bottom
system navigation bar is transparent over the page, with legible system icons;
it must not inherit a separate top-bar-colored strip.

### Dialogs, Sheets And Menus

Use Overlay Runtime for placement, scrim, dismissal and lifecycle. Define content
anatomy by the reference where one exists; ActionSheet/Tour remain GearUI patterns.
Destructive and cancel actions must be distinct. Content stays reachable with long
lists, keyboard and safe areas. Test outside tap, Back, drag cancellation and rapid
reopen; one screenshot of an open sheet is not behavioral acceptance.

## 6. Shadows, Borders And Materials

Card and MaterialSurface use shared `DecoratedSurface`; it supports ordered outer
and inset shadows, spread and complex borders on rectangular/rounded outlines.
Blur is a bounded Gaussian edge approximation, not an exact two-dimensional blur.
`LocalSurfaceShadowStyles` overrides stacks independently of accent and shape.

Legacy single-elevation consumers must use named Elevation roles until explicitly
migrated. Neither a blanket "all cards must be flat" rule nor "every component
has new shadows" is correct. Record each migration and its visual evidence.

Glass is optional, subject to material policy and an opaque fallback. It is not
Liquid Glass refraction or proof of platform parity. Do not introduce blur where
it hurts text contrast or motion performance. See [surface evidence](SURFACE_RENDERING_ACCEPTANCE.md).

## 7. Sample And Acceptance

Keep the sample's mobile navigation and page organization. Library changes must
flow into real components, not screenshot-only substitutes. Diagnostic controls
belong in named probes; normal builds must retain the home entry.

For each changed component, record the reference revision, state/variant, platform,
light/dark and accent/shape checks, input cancellation, long content, keyboard and
safe-area outcomes. Distinguish source-reviewed, compiled, unit-tested and
visually/interactively verified. Use the [component matrix](COMPONENT_ACCEPTANCE_MATRIX.md)
and [release gate](BETA3_RELEASE_READINESS.md); never infer library-wide completion
from a shared primitive or a successful compiler run.
