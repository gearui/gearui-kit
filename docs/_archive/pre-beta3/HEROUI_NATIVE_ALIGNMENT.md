# HeroUI Native Alignment

Status: Approved direction; staged implementation in progress.

## Scope

Default appearance AND default control feedback follow the pinned open-source
HeroUI Native source. Keep GearUI's runtime and Kotlin architecture. Preserve
the sample's original mobile page layout, navigation, sections and example
combinations. The Tamagui-era HomePage/ExamplePage and Button/Input/Select/Textarea
page rewrites are reverted; the temporary DesignReferencePage is removed.
Library component changes must flow into the existing sample naturally, not be
simulated by restyling the demo pages. Retain theme-token plumbing and correct
diagnostic documentation. Never substitute a screenshot fixture
for the installed sample's normal entry.

Read source, not promotional adjectives. Do not add universal glow, springs,
haptics, or bottom sheets that the reference does not use. Preserve attribution
and applicable notices when incorporating upstream code.

## Source Map

Paths below are relative to the pinned HeroUI Native repository.

| Concern | Authoritative source | Porting requirement |
| --- | --- | --- |
| Light/dark semantic colors, fields, shadows | `src/styles/variables.css` | Preserve foreground/surface relationships; explicitly convert OKLCH to supported renderer space |
| Derived colors and radius roles | `src/styles/theme.css` | Resolve aliases and color mixing before generation; do not substitute guessed HEX values |
| Button anatomy and sizing | `src/styles/components/button.css` | sm/md/lg heights are spacing times 10/12/14; resolve upstream spacing/rem configuration before fixing logical dimensions |
| Button defaults | `src/components/button/button.tsx` | Primary variant and scale-highlight feedback, not the previous neutral/instant default |
| Press feedback | `src/components/pressable-feedback/pressable-feedback.animation.ts` | Base scale .985, width coefficient 300/width, 300ms out-ease scale, 200ms highlight; port timing, cancellation and interruption, not duration alone |
| Input surface and geometry | `src/styles/components/input.css` | Minimum height spacing times 12, horizontal padding times 3, independent field radius/border roles |
| Field composition | `src/components/text-field/text-field.tsx` | Label, input, description and error share disabled/invalid/required state without forcing every control into seven states |
| Select presentation | `src/components/select/select.tsx` | Default popover; bottom-sheet/dialog are explicit presentations, not automatic mobile substitutions |
| Sheet interaction | `src/helpers/internal/components/bottom-sheet-content.tsx` | Verify drag, pan-to-close, keyboard, insets and reduced motion against a running reference |

## Token Contract

- DTCG 2025.10 is the mandatory token exchange format, not the runtime behavior engine.
  The existing generator supports only a documented subset; do not claim full
  conformance, resolver support, or wide-gamut rendering from JSON support alone.
- Keep primitive, semantic and component roles separate. Safe area and keyboard
  measurements remain runtime environment, not theme literals.
- Application overrides must propagate through App/Theme into descendants AND
  overlays. Preserve explicit overrides when deriving component defaults.
- Rounded/square presets must affect fields, buttons, cards and overlays, not
  only their outer frames. Intentional circular indicators are separate roles.
- Theme switching must not recreate native input, lose focus/text, dismiss open
  overlays or reset navigation. Motion suppression retains state feedback.
- Verify colors after conversion, including alpha and light/dark contrast.
  Material/shadow limitations require documented fallbacks and visual checks.

## Execution Stages

| Stage | Deliverable | Exit gate | Status |
| --- | --- | --- | --- |
| S0 | Source lock and component source map | Reference verifier succeeds; Pro/Web separated from Native | Implemented |
| S1 | Theme/token mapping and replaceable component metrics | Reproducible generation; override tests for palette, type, radius and motion; no static default bypass | Partial: palette and press feedback connected |
| S2 | Button, Input, Textarea and field composition | Default/pressed/disabled/loading/focus/error where applicable; reference comparison and callback tests | Partial: Button/Input device smoke checks |
| S3 | Select, menus, Dialog, BottomSheet, ActionSheet | Placement, selection, scroll, dismissal, gesture interruption, keyboard and safe area | Pending |
| S4 | Remaining component families and sample | Full inventory with per-component evidence; no unreviewed global substitutions | Partial: selection-control batch; other families pending |
| S5 | Cross-target acceptance | iOS/Android visual and interaction checks; Web/Harmony evidence or explicit unresolved gaps | Pending |

### Whole-library rollout coverage

All existing component directories are in scope, not only the components with
identical upstream names. The following is an execution inventory, not a claim
of completed visual parity. Components without an upstream equivalent inherit
the same surface, typography and feedback language while retaining their mobile
behavior; do not invent a nonexistent HeroUI implementation to copy.

| Family | GearUI directories / primitives | Reference / status |
| --- | --- | --- |
| Actions and fields | button, input, textarea, select | Initial batches implemented; full visual/interaction acceptance pending |
| Selection | checkbox, radio, switch | Geometry, state propagation and feedback batch in progress; small/large and card variants are GearUI extensions |
| Additional fields | searchbar, slider, stepper, rate, form, picker, calendar, cascader, treeselect, transfer | Search-field/slider/control-field where present; remaining components need mobile-specific mapping |
| Surfaces and lists | cell, cellgroup, collapse, grid, table, tree; foundation Card/Avatar | list-group, accordion, card, avatar; dense data views need extension rules |
| Overlays | dialog, bottomsheet, actionsheet, drawer, popup, popover, contextmenu, tooltip, tour | dialog/bottom-sheet/popover/menu; preserve GearUI runtime and safety contracts |
| Navigation | navbar, bottomnavbar, navigationmenu, tabs, segmented, scaffold, anchor, pagination, steps, swiper, swipecell, backtop | tabs/menu where present; navigation and gesture components require GearUI-specific review |
| Feedback and display | loading, skeleton, toast, snackbar, notification, progress, empty, result, tag, timeline, icon, image, imageviewer, watermark; Text/Badge/Divider | spinner/skeleton/toast/alert/chip/text/separator where present; remaining roles need explicit mapping |

Selection batch: default Checkbox/Radio marks are 24px with a 44px interaction
target; Radio uses accent fill and a 10px foreground dot. Switch defaults to a
48x24 track and 28x20 thumb with 2px insets, normalized 120/1600/2 spring and
175ms color timing. DTCG source owns these metrics and feedback values. Labelled
controls have one interaction owner; disabled flags propagate through groups.
Loading Switch rejects activation and uses the shared spinner (static when
motion is suppressed). Existing public parameter names and sample layouts stay.
Checkbox and Radio use a semantic thin outline as a deliberate fallback for
HeroUI's layered field shadow: a transparent border with no matching shadow made
unchecked controls invisible against white sample surfaces in simulator testing.
Switch size captions are updated to the new 28/24/20 track heights only; sample
structure and navigation remain unchanged.
Checkbox radius morph, exact shadow/material parity, fully replaceable per-control
metrics, and non-default variants remain unverified; do not mark S4 complete.

Selection verification: 132 Android unit tests and 29 token compiler tests pass;
Android sample and iOS Xcode builds pass. On iPhone 17 Pro / iOS 26.2, Checkbox
and its label toggle, unchecked marks remain visible after the outline fix,
Radio selection moves from the first to second labelled row, and the disabled
Radio example stays unchanged after tapping. Switch toggles normally and its
text changes On/Off; loading and disabled examples retain their state. Those
examples do not count callbacks, so unchanged screenshots alone are not proof
of callback suppression. Group disabled propagation was inspected in source.
No dark-mode, RTL, gesture interruption or frame-curve parity acceptance yet.

Slider/SearchBar batch: Slider's default track is 20px with a 28x20 accent
thumb containing a foreground knob inset by 2px. The knob scales to 0.9 while
dragging, with the reference's 15/200/0.5 spring normalized for Compose.
The surrounding track is 44px high for interaction. Radius follows the active
theme; disabled controls use shared opacity. Capsule and labelled/range variants
remain GearUI extensions. Zero-width measurement no longer produces negative
thumb offsets; the existing range, step-origin and drag algorithms are retained.
SearchBar uses the field radius, a stable 48px trailing text reservation and a
24px clear button with a 14px icon. Disabling now reaches BasicTextField;
SearchBarWithAction forwards enabled and actually invokes its action callback.
Native focus/keyboard workarounds and the sample page structure are preserved.
Geometry and spring parameters come from DTCG tokens, not component literals.
Exact thumb shadows, clear-button press feedback/hit slop, dark/RTL states and
frame-by-frame animation parity remain open; this is not full S4 acceptance.

Slider/SearchBar verification: 136 Android unit tests and 29 token compiler
tests pass; Android sample and iOS Xcode builds pass. On iPhone 17 Pro / iOS
26.2, tapping the slider changes thumb position and the labelled value (10 to
84.3). SearchBar accepts text, clears it while retaining focus, and its action
button updates the sample's search-result text. Rounded and square fields render
with distinct shapes. Hardware-keyboard input was used; software-keyboard
dismissal, disabled native editing and drag interruption were not device-tested.
Of 22 shell checks, 20 pass; English comments in other files and the accumulated
token snapshot changes still fail their guards. No baseline is refreshed merely
to hide those failures. Source reference verification covers 26 pinned files.

Collapse batch: map Block to Native's transparent default accordion and Card
to its surface variant. Header/content horizontal padding is 12/20px, header
vertical padding and content bottom padding are 16px. Card no longer imposes
an external margin. Separators remain between items, including expanded items;
there is no separator inserted between a header and its own content. Card uses
the theme's 24px default surface radius. Custom header slots are preserved.
The indicator rotates to -180 using the 140/1000/4 spring; content size uses
140/1600/4 and fades over 200ms. DTCG stores dimensions, spring numbers, duration
and cubic-Bezier easing. Reanimated's ease is (0.42,0,1,1); its out transform
gives (0,0,0.58,1), checked against the official Easing implementation:
https://github.com/software-mansion/react-native-reanimated/blob/main/packages/react-native-reanimated/src/Easing.ts
The Compose adapter honors app motion speed/zero-motion, but per-content height
animation is not a claim of Reanimated whole-layout transition parity.
CollapseGroup now forwards item.enabled, preventing disabled rows from opening.
Exact layered surface shadows, native accessibility expansion semantics and
interrupted transition/lifecycle parity remain open. Sample layouts are unchanged.

Collapse verification: 140 Android tests, 29 token compiler tests and both
Android sample/iOS Xcode builds pass. Pinned reference verification covers
30 files. The final build was installed on iPhone 17 Pro / iOS 26.2: opening
the first panel preserves readable wrapping, keeps the inter-item separator
and rotates the indicator; repeated open/close in the initial build returned to
the collapsed layout without a blank gap. No frame-by-frame parity, dark mode,
Card variant or disabled-row device acceptance is claimed. Shell guards remain
20/22 with the previously recorded English-comment/token-snapshot failures.

Cell/CellGroup batch: standard rows use 16px padding and 12px inter-part gaps
from DTCG. Prefix content is centered against the complete row without imposing
an avatar height; title and description slots remain supported. Group radius
uses Theme.shapes.xl, with existing inset separators and header policy retained
as GearUI extensions. Compact rows retain their 44px minimum/8px vertical inset.
Disabled opacity applies to all row content, including custom slots, while clicks
remain disabled. No extra scale/ripple is invented: the Native ListGroup item
uses Pressable without a built-in animated feedback wrapper.
Cell no longer bypasses Theme.typography with private 17/13sp styles. Title,
description and note use titleMedium/bodySmall/bodyMedium respectively. This
deliberately preserves app typography replacement, but the current default Native
profile is not an exact match for upstream text-base Medium/text-sm metrics.
That typography-role mapping and layered surface shadow are still acceptance
gaps. Card's Float radius/Surface coupling is not changed in this batch.

List verification: 143 Android tests and 29 token compiler tests pass; Android
sample and iOS Xcode builds pass. Reference verification covers 34 pinned files.
On iPhone 17 Pro / iOS 26.2, Cell displays title/note, two-line content and
leading/trailing slots; clicking its interactive example displays the Toast.
CellGroup retains header alignment, inter-row separators and no trailing divider.
The final sample's icon-row separator aligns with the text: its old 48px inset
assumed a 20px icon but actually used a 16px icon. The sample now derives that
inset from the row padding, actual icon size and gap (44px); this is a parameter
correction, not a page reorganization. Dark/disabled, custom font and long-note
device scenarios remain unverified. Shell guards remain 20/22; no baseline was
refreshed to hide the existing comment/token-snapshot failures.

Textarea hierarchy correction supersedes the earlier transparent-input-border
acceptance. Copying Native's transparent border without its layered field shadow
made white fields disappear inside the sample's white example container.
The built-in light/dark input border now uses the reference semantic border as
an explicit no-shadow fallback. Original reference field-border values stay
unchanged in the source palette; app-provided InputColors (including transparent
borders) are not overridden. This is not exact Native material parity.
Textarea defaults to a vertical label with a 6px gap; horizontal remains an
explicit variant. Standalone default four-line fields have a 128px minimum and
8px vertical padding, expanding for larger text. Autosize/explicit line counts
retain their compact sizing. Helper text and counters are outside the editing
surface. Native focus handling is preserved.
The sample's Textarea sections no longer wrap multiple fields in a white card;
the raw BasicTextField diagnostic moves to the end. The disabled example now
actually sets enabled=false instead of mislabelling readOnly as disabled.
This is a targeted example-composition correction, not a global sample redesign.

Hierarchy batch verification: 148 Android unit tests and 29 token compiler tests
pass; Android sample and iOS Xcode builds pass. On iPhone 17 Pro / iOS 26.2,
the default examples show separate editing surfaces, labels above fields and
counters below them. Entering two lines reports 22/500; tapping the disabled
field and attempting input leaves its text unchanged. Input was supplied via
the simulator hardware-keyboard path, not a software-keyboard dismissal test.
Dark mode, large-font device rendering and exact layered-shadow parity remain
unverified. The source lock verifies 40 reference files. Shell guards remain
20/22 with the existing English-comment and token-snapshot failures; no baseline
was raised or refreshed to hide them.

The follow-up applies the correction to the whole field family. Input defaults
to top labels with the same 6px label gap as Textarea and Select; explicit left
labels remain available. Its metadata is below the editor, and reaching the
maximum length is not itself an error. SearchBar now consumes LocalInputColors
for background, border, text, placeholder and cursor. Selection trigger text
also honors that palette. Date/time triggers reuse FieldTrigger rather than
maintaining a separate disabled/background/click implementation. All six
affected sample pages remove redundant ExampleSection card containers (41
sections); page ordering and navigation are unchanged.

Focus decoration targets both touch and keyboard, with validation taking
precedence and disabled focus suppressed. Device inspection found that the
drawWithContent ring was invisible, including after moving it inside the bounds.
FieldFocusOverlay isolates reactive border changes in a sibling decoration;
focus does not rebuild the native input modifier chain. This changes feedback,
not the focus requester or keyboard dismissal ownership. The light/dark semantic
outline is still a declared renderer fallback, not a claim of matching Native's
three-layer shadow. No synthetic elevation value is presented as that shadow.

Whole-family follow-up verification: 149 Android tests and 29 compiler tests
pass; Android sample and iOS builds pass. iPhone 17 Pro / iOS 26.2 confirms
Input top labels, metadata placement, error and read-only presentation; a
10-character field rejects an eleventh character and reports 10/10 without
false error coloring. The final sibling feedback layer visibly follows focus
between Input fields and around Textarea while typing two lines; the home
SearchBar also shows the focus ring and filters normally. Dark-mode Input and
Select are visually checked; Select commits Shanghai and closes. DatePicker
opens and confirms a value, and Cascader's sample surface hierarchy is checked.
These are simulator checks, not exhaustive gesture or software-keyboard tests.
TreeSelect's final device interaction, custom-theme device rendering, layered
shadow fidelity and frame-by-frame motion parity are not claimed as accepted.

Card work was interrupted by this hierarchy issue: the working tree contains
its shape-aware Surface path and new default geometry, but Card-specific visual
and compatibility acceptance is still pending. Do not mark that batch complete.

The full apiCheck currently fails on unrelated BottomSheet, AutoResizeTextarea,
swipeDismiss and Overlay contract/baseline differences. This batch does not
refresh those baselines and must not be reported as an all-green release gate.

S1 starts by auditing the existing App typography/shapes/motion forwarding and
static spacing/control geometry. Existing forwarding is not evidence that every
consumer honors it. Add missing plumbing rather than a second theme system.

S1 progress: local chained aliases, inherited token types and JSON Pointer
property references now run before all three existing Kotlin generators.
Cycle, missing-target and whole-token alias type checks are tested. Generated
Kotlin is unchanged. Theme propagation and HeroUI value migration remain open.
Numeric OKLab/OKLCH-to-sRGB translation is now implemented with explicit
out-of-gamut rejection. All 26 distinct OKLCH literals in the pinned upstream
variables file passed conversion. This is source-level verification, not device
visual acceptance; semantic palette mapping is still pending.
That palette mapping is now connected for background, surface, overlay, neutral,
primary, status, field border and focus roles in both themes. GearUI `muted` is
a surface role mapped to upstream `default`; upstream `muted` maps to GearUI
`mutedForeground`. Do not equate same-spelled names with identical meanings.

Current integration:

- DTCG feedback data generates Kotlin duration, scale, opacity and easing defaults.
- Button defaults to primary, rounded/capsule appearance, no filled border,
  width-adjusted press scale and animated highlight. Disabled/loading still
  reject callbacks; disabled opacity belongs to the component, not sample CSS.
- Existing Theme.motion.normal scales feedback durations relative to its default;
  zero suppresses scaling and transitions while retaining instantaneous highlight.
  This avoids changing the public Motion constructor. Automatic OS reduced-motion
  observation is not yet verified.
- Regular controls use 48 logical units, button padding 16 and field padding 12,
  under an explicit 16-logical-unit rem / 4-unit spacing translation policy.
  Field regular radius is 14; extra-small remains a GearUI extension.
- Input focus/keyboard handling is unchanged. Input and shared field triggers
  receive disabled opacity; transparent field borders preserve error feedback.
- iPhone 17 Pro/iOS 26.2: normal sample navigation, Button/Input layout, ordinary
  activation, disabled/loading rejection (count remains 1 after all three taps),
  and input text persistence after Return were observed.

Button feedback follow-up: the reference Button overrides generic Pressable's
0.1-opacity highlight with a full-opacity mixed surface. This now draws below
content. Neutral/default mixes 4% foreground, accent/status mixes 10%; outline
and text use the neutral highlight with 0.3 alpha. Runtime OKLab mixing supports
application palettes and premultiplied alpha. Explicit ButtonColors hover/press
overrides remain honored. The highlight curve follows
[Reanimated's in-out quadratic default](https://docs.swmansion.com/react-native-reanimated/docs/animations/withTiming/).
Textarea now consumes the same disabled opacity as Input. These changes do not
modify the restored sample layouts.

Still open: typography migration, default field shadows, complete field-family disabled
semantics, runtime rem parity with the reference app, and recorded motion parity.
Theme propagation into open overlays, square presets and dark-mode visual
acceptance are not established by the light-mode smoke check. S3 is not complete.
See [DTCG translation contract](../tokens/README.md) for implemented boundaries.

Each implementation batch updates this table with evidence. Do not refresh API
or debt baselines to conceal unrelated failures. Avoid API-breaking renames for
visual alignment; any unavoidable change needs a separate migration decision.

## Acceptance

### Composite token follow-up

DTCG 2025.10 border/transition/shadow lowering is now implemented for the
declared px/sRGB-compatible adapter subset. Native's ordered light shadow layers
and dark inset shadows are recorded in materials.tokens.json and generated as
internal data, not approximated as elevation. Collapse now consumes complete
transition tokens, including independently scaled delay and exit duration.
Default timing remains 200ms with zero delay. Card geometry tests cover active
theme shape, explicit radius overrides, negative-radius clamping and the current
outline fallback; these tests do not close Card visual/shadow acceptance.
Layered shadow rendering, remaining component families and cross-target visual
acceptance remain open. This batch does not change public API or refresh API and
token debt baselines.

Verification: 42 token compiler tests and 154 Android unit tests pass; generated
outputs match their sources, the 40-file reference lock passes, and Android
sample/iOS Xcode builds succeed. iPhone 17 Pro / iOS 26.2: Collapse expands,
collapses and opens another item with readable content and separators. This is
a default-timing smoke check, not frame-by-frame reference parity. Custom delay
and zero-motion behavior are covered by unit tests, not device recordings.

The following panel batch replaces the earlier Tamagui default: Select and
MultiSelect now default to the separate anchored mode, prefer below when the
content fits, and flip above when necessary. Explicit ITEM_ALIGNED remains.
The popover uses themed overlay surface and xl shape, DTCG content/item insets
of 12/8 logical px, and a 20px indicator slot. Selected items use a check rather
than persistent fill. Native source uses accent-soft-foreground for that check;
the adapter mixes primary/foreground in OKLab at 80/20. Transient pressed/hover
fill is retained as a GearUI behavior, not claimed as upstream equivalence.
Gradient scroll buttons from the earlier reference are removed. Layout tests
cover padding, bottom preference, flip, selected-row visibility and explicit
item alignment. Group row metrics, typography and popup transitions still need
reference-device comparison. Sample pages are unchanged.

Panel batch acceptance on iPhone 17 Pro / iOS 26.2: single selection opens
below the trigger, commits Shanghai and closes; reopening shows its check
without persistent fill. MultiSelect flips above its lower trigger, accepts
two choices, and preserves them after outside dismissal. Tapping disabled
Orange leaves the fruit panel open and the placeholder unchanged; Apple then
commits normally. Android tests: 128 passing; token compiler tests: 29 passing;
Android sample and iOS Xcode build pass. Radius/spacing guards pass with no
baseline increase. Light-mode smoke checks do not establish dark-mode, keyboard,
large-font, long-list gesture, or frame-by-frame motion parity.

Select indicator feedback now follows the Native reference's 0/-180 degree
spring (damping 140, stiffness 1000, mass 4). These are DTCG number tokens;
the adapter normalizes mass for Compose's unit-mass spring. App motion speed
scales stiffness by the inverse squared time scale; zero motion snaps directly.
Single and multiple selection share the implementation. This does not establish
panel anatomy, scrolling or transition parity; the existing item-aligned panel
and selection lifecycle remain unchanged pending separate acceptance.

Batch verification: Android unit tests and sample build, iOS sample Xcode build,
29 token compiler tests, and radius/spacing guards pass. On iPhone 17 Pro iOS
26.2, the unchanged sample opens Select, commits Shanghai and closes; MultiSelect
keeps its panel open after two selections, shows both checks, and dismisses on
outside tap. The item-aligned panel covers the trigger, so this smoke check does
not establish the spring's visible motion parity. Panel structure remains open.

Use a running reference with the pinned source, equal logical control size,
content, theme and font configuration. Record reference and GearUI captures,
device/OS, scenario and known renderer differences. Screenshots establish static
appearance only; recordings and event assertions establish interaction.

First device: iOS Simulator iPhone 17 Pro. Exercise the normal sample pages in
light/dark and rounded/square themes. Check text baseline, padding, borders,
surface hierarchy, selected indicators and clipping. Exercise press/release,
drag-out cancellation, repeated activation, disabled/loading rejection, focus,
typing, keyboard dismissal, open-overlay theme changes and reduced motion.

No universal pixel tolerance is invented here: record measured differences per
component and resolve or explicitly approve them. A build, unit test, or visually
similar palette alone never closes a component's acceptance. Other targets must
not be labelled verified from shared-code compilation alone.

## Consolidation: brand, live overlays and real sample controls

- `ThemeSpec.withBrandAccent` updates primary/on-primary/ring and explicit focus
  palettes while retaining surface roles. Sample has independent light/dark,
  accent and rounded/square controls; all three sample language packs cover them.
  Navigation/status-bar backgrounds always use surface, including DarkPurple.
- Dialog, Popup, Drawer, ActionSheet and Popover retain their overlay instance
  while reading current content/color/callback state. Dismiss-policy and placement
  option changes are not promised to update while open by this change.
- Tag now uses Native chip size/soft-color rules and rejects click/close while
  disabled. Pagination and RadioCardGroup share the disabled-opacity convention.
  The sample's former private `SimpleTag` and text-only size placeholders were
  removed; it now exercises the real Tag, all variants, sizes and disabled toggle.
- Dynamic rounded-to-square switching has a renderer limitation: existing native
  borders can retain an old radius when the border color is unchanged. A tested
  zero-radius rounded-outline adapter did not fix it: KNode also skips isRect.
  The ineffective adapter was removed. Newly mounted square controls render
  correctly; no page-remount workaround is used because it would lose state.
- Public additions/default changes are recorded in MIGRATION_1_0.md. API snapshots
  are updated intentionally; this does not claim binary compatibility with the
  previous pre-1.0 artifact. App's dial-code block follows the large field geometry.

Remaining acceptance must not be confused with compilation:

| Area | Still open |
| --- | --- |
| Material | Multi-layer/inset/spread shadows are token data, not fully rendered; border fallback remains |
| Full visual parity | Per-component reference recordings at matching dimensions/fonts; all variants and interruptions |
| Theme | Runtime custom component geometry is not a globally injectable axis yet |
| Overlay | Full keyboard, safe-area, drag cancellation and every open-overlay configuration permutation |
| Platforms | Android/Web/HarmonyOS device visual and gesture acceptance; iOS smoke checks do not substitute |
| DTCG | Supported adapter subset only; unsupported units/styles/color handling remain explicit errors, not silent conversion |

Latest consolidation results and remaining acceptance boundaries are recorded in
`STANDARDIZATION_ACCEPTANCE.md`. This report supersedes older batch test counts;
older sections remain as historical, scoped evidence rather than an all-green
component matrix.
