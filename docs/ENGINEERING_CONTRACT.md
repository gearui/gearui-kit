# GearUI Kit Engineering Contract

Status: current beta3 engineering rules. [SPEC.md](SPEC.md) is the entry point.
Section numbers retained where practical for existing source comments. The full
pre-consolidation document is archived; its plans are not implementation evidence.

## 1. Scope

Applies to the kit, sample, integration resources, CI and release artifacts.
The design system owns appearance; this contract owns runtime and engineering safety.

## 2. Non-goals

Do not rewrite the renderer, import product session logic into GearUI, or change
public semantics without migration. No framework ranking or line-count target
justifies a refactor by itself.

## 3. Responsibility Boundaries

### 3.5 Runtime Responsibility Boundary

Runtime owns theme/i18n propagation, environment, keyboard/focus policy, overlay
stack/lifecycle and navigation. Components request policies and render state.
Local control gestures are allowed; private global event listeners duplicating
runtime dismissal/scroll/Back ownership are not. Application auth/session/network
coordination remains outside GearUI.

## 4. Architecture

### 4.1 Token Governance

Use semantic/component tokens. [Token contract](../tokens/README.md) owns DTCG
format and adapter policy. Source JSON, generated Kotlin and reviewed token
snapshots must agree. Explicit overrides inherit/derive missing values according
to the documented API; do not promise a generic ThemeSpec patch/merge API where
only a complete data-class value exists. Color/radius/spacing/elevation axes must
not be coupled just because their numeric values happen to match.

### 4.2 API Compatibility

Review JVM and KLib API diffs on macOS. A baseline refresh is an intentional API
change, not proof that old binaries still link. Pre-1.0 breaking changes require
release notes, a migration path and consumer recompilation checks. Stable APIs
require a deprecation/compatibility strategy; emergency exceptions need justification.
Do not rewrite an existing public parameter's meaning silently.

### 4.3 Theme Overrides

Support documented global, component and instance overrides. Track current
runtime-injection gaps explicitly. Theme updates must not reset navigation,
editor text/focus or dismiss an open overlay. Native rendering limitations must
have scoped evidence, not an unconditional parity claim.

### 4.4 Overlay Architecture

Create/destroy through Overlay Runtime. The host owns full-viewport scrim, stacking,
Back/outside/route/timeout policy and removal lifecycle. Local panel gestures request
host dismissal. Keep callbacks and content current; document policies captured
when shown. Test exactly-once removal, cancellation, reopen, keyboard and focus.
Nonmodal banners must pass outside interaction through rather than freeze the page.

### 4.5 Runtime Environment And Insets

Host measurements flow through RuntimeEnvironment; do not create another component
safe-area source. `safeArea` excludes the keyboard. `keyboard` is separate geometry
from platform host callbacks, not inferred from bottom-inset deltas or an input's
mount-dependent callback. Unified safe-area pipeline is the default; legacy mode
is an integration rollback path.

One `App` root per page tree. Sample pages must not mount another App/OverlayRoot
or construct a competing Theme runtime. Ordinary pages use PageScaffold; NavBar
must not consume the same top inset again. Fullscreen media can draw edge-to-edge
while foreground controls consume insets. Preserve left/right insets too.

Use the shared inset resolver. Parsing belongs to runtime; application position
belongs to the component: a sheet may extend its surface to the bottom while
insetting content. Moving that padding outside the surface can expose the scrim.

Hosts need initial bootstrap and dynamic updates. iOS uses the actual host window,
not global keyWindow, and reports safe-area changes even without a size change.
Android merges relevant system/gesture/tappable/stable sources. Never reuse IME
height as bottom navigation chrome. Shared runtime flags own consumption policy;
do not revive page-level `useSafeArea` arguments.

### 4.6 Fullscreen Container Contract

Hosts attach to match-parent, edge-to-edge containers. App/Overlay roots remain
full-sized; safe-area padding belongs to content, not the root canvas. Debug
violations should fail visibly; release diagnostics should identify the integration
problem without deliberately crashing the user's app. Instrumentation coverage is
an acceptance item, not assumed from this requirement.

### 4.7 Runtime Compatibility

Preserve Kuikly event/field semantics including safeAreaInsets, size changes and
density. Add capabilities compatibly with a documented fallback and rollout;
never reinterpret an existing field. Verify platform workarounds before removing
native scroll/input behavior that ordinary Compose callbacks may not intercept.

### 4.8 Built-in Assets

Icons and other bundled assets must load on the first clean build/install. A
compiler pass is insufficient. Check actual AAR, framework app bundle and Web
resources. Consumers must not guess internal asset paths. Share integration logic
where available; shared-plugin/resource-verification proposals are not shipped APIs.
Keep iOS resource-copy phases after framework/resource synchronization; no destructive
resource sync that races another build. Maintain the icon registry and allowlist.

## 5. Performance

Track long lists (1000+ items), dense forms (20+ controls), theme switching and
stacked overlays. Existing target budgets remain goals, not measured achievements:
Android TTI <=1200ms, iOS <=1000ms, Web <=1800ms under a specified network/device;
scroll drops <3%; theme change <=120ms. Record hardware, workload, sample size and
measurement method before comparing. Track recomposition scope and allocation too.
No current static guard establishes these budgets; performance/nightly automation
remains an open gate until implemented and measured. Do not claim a performance
improvement or full conformance from line counts or light-load screenshots.

## 6. Component API And Sample

### 6.1 API Consistency

Keep consistent parameter meanings and controlled state/callback ownership.
Do not mechanically reorder existing APIs merely for uniformity.

- Field family (Input, Textarea, Select/MultiSelect, Cascader, TreeSelect and
  date/time input triggers): `enabled = true`, `error: String?`.
- SearchBar: `enabled`, no invented validation error parameter.
- Action family (Button, Tag, SwipeCell actions and sheet items) may retain
  `disabled = false`; do not rename solely for mechanical consistency.
- No opposite-polarity pair on the same component. Option-model `disabled`
  describes the option, not the containing control.
- Error text and display variant enums are different semantics; parameter-name
  counting alone cannot classify them.

### 6.2 Documentation

Code comments are English; UI text uses typed i18n packs. Component guides include
minimal/recommended use and boundaries. Documentation language/status is explicit;
legacy Chinese documents are not mislabeled as synchronized English editions.

### 6.3 Migration

List source, binary and behavioral changes separately. Include before/after calls
where needed. Do not imply recompilation fixes a removed symbol or changed behavior.

### 6.4 Sample As Integration Evidence

Preserve normal mobile page navigation. Demonstrate actual library components,
not copied visuals. Every public index entry must resolve to a real example.
No ComingSoon placeholders in new coverage. Diagnostic fixtures are named, opt-in
and not the normal release entry. Insets, theme, i18n and overlays use the same
runtime as consumers. Verify controls after removing obsolete wrappers.

### 6.5 Input And Keyboard

Single-line Done clears focus/hides the keyboard by default unless explicitly
configured otherwise. Global tap/scroll dismissal shares runtime ownership.
Verify focus persistence across theme changes, cancellation, disabled inputs and
native IME behavior. Some screenshot tools cannot composite native input overlays;
use an appropriate recording/device check instead of guessing from a black image.

## 7. Extension Boundaries

Keep product-specific colors/language packs in product layers. Public slots may
compose content, but must not replace runtime ownership or bypass state contracts.
A token compiler exchanges values; it is not a runtime layout/behavior compiler.

## 8. Executable Quality Gates

Workflows implement checks; reports record results. No static check establishes
complete visual, accessibility or performance parity.

### 8.1 Static Gates

Each path below is under `scripts/ci/`. All `check_*.sh` gates are executed in
Guardrails except sample-index, which runs in CI's SDK Guard job.

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

Ratchets may shrink, not grow to conceal a regression. Zero-baseline checks remain
hard gates. Exact scope/allowlists are in each script; do not duplicate their
historical counts here.

### 8.2 Generated Data And Unit Tests

- Guardrails: `python3 scripts/generate_tokens.py --check` and
  `python3 -m unittest discover -s scripts/tests -p 'test_*.py'`.
- Android CI: `:gearui-kit:testDebugUnitTest` and `:gearui-kit:lintDebug`.
- Web CI: `:gearui-kit:jsBrowserTest` with Chrome available. Absence of Chrome is
  a setup failure, never permission to skip the tests silently.
- macOS CI: `:gearui-kit:apiCheck` checks JVM and KLib baselines. Do not run apiDump
  in a verification job; it would bless the change under test.
- Local source-reference audit: `node scripts/check_heroui_reference.mjs <checkout>`.
  The sibling reference checkout is not a mandatory CI dependency.

### 8.3 Build Gates

CI builds Android kit/sample, links the iOS simulator sample, runs the iOS
simulator Kotlin tests against the real Kuikly host (`scripts/ios_native_tests.sh`)
and builds the Web host. Release packaging additionally checks all three iOS artifacts, metadata,
Android AAR, JS artifact and assets from the exact candidate. HarmonyOS uses the
separate build and needs explicit compile/package/device evidence.

### 8.4 Manual And Unimplemented Gates

Critical input/keyboard, overlay dismissal and gesture interruption, live theme
changes, large text, real asset loading, screen readers and frame performance need
runtime evidence. Nightly performance and full-device visual automation are not
implemented by the shell guards. Track them in the release report and component
matrix rather than repeatedly assigning obsolete milestone dates.

## 9. Release Contract

Use [RELEASING.md](RELEASING.md). Release the exact tested commit, not a dirty tree
or a different SHA with earlier green checks. Check version, migration, release
notes, all platform artifacts, dependencies and built-in resources. Local staging
is not Central publication. Signing/Portal validation and final publication require
the maintainer's explicit release action. Beta limitations must be disclosed;
known crash/data-loss/input-blocking regressions are not acceptable beta caveats.

## 10. Evidence Inventory

Use the current [beta3 report](BETA3_RELEASE_READINESS.md), [component matrix](COMPONENT_ACCEPTANCE_MATRIX.md)
and scoped acceptance records. Old milestone dates are archived, not active deadlines.

## 11. Architecture Guardrails (PR Gate)

### 11.1 REJECT

Reject duplicated global runtime ownership, root canvas inset clipping, hidden
API breaks, baseline changes without review, silently ignored token values,
new hardcoded design values bypassing tokens, missing packaged assets, and
unverified claims of compatibility/parity. Reuse primitives rather than copying
badge overflow, typography or surface implementations. Fixed navigation slots
must contain the complete icon+badge bounds; clipped `99+` badges are not acceptable.

### 11.2 WARN

Require evidence for additional allocations, platform branches, motion/blur cost,
new theme extension points, new assets and new platform targets. Ratchet baselines
freeze known debt; passing them is not proof the debt is gone.

### 11.3 Reviewer Checklist

Runtime ownership, token authority, migration, sample integration, targeted negative
cases, actual platform evidence and performance impact must be reviewed separately.

## 12. Layered I18n

One language runtime (`LocalLanguageTag` and fallback) serves independent typed
library packs. App owns I18nRoot. Product providers read that runtime, not a second
language parameter/root. Cache resolution/normalization; empty patches return the
original instance. Do not replace typed strings with a global string-key registry.
See [I18N_INTEGRATION.md](I18N_INTEGRATION.md) for the concrete contract.

## Accessibility

Every control a user can operate must be reachable by name and by state.

- **Name.** A control whose visible content is text needs nothing: the text is the
  name. A control that shows only an icon (CloseButton, a rating star, an upload tile
  with a thumbnail) carries `contentDescription`, from `I18n`, never a literal.
- **State.** Selection and on/off state go in `stateDescription`, from `I18n`. Do not
  rely on the `Selected` semantics flag for what the user hears: Kuikly's bridge
  appends a hardcoded Chinese "已选择" for it, which repeats the state and ignores the
  app's language.
- **Role.** `Role.Button` on tappable non-text targets, `Role.Tab` on segmented and tab
  cells. Kuikly maps the rest to plain text.
- **Decoration stays silent.** An icon inside a labelled control passes a null
  description. An empty string is not the same thing: it joins the parent's
  announcement as a stray separator.

Semantics reach the platform automatically: Kuikly's `KuiklySemantisHandler` copies
them onto the native views whenever the semantics tree changes, so no host wiring is
needed. The result is checkable on a simulator without a screen reader — `idb ui
describe-all` lists every label, role and state.

### Press feedback belongs to the component

`LocalIndication` cannot deliver it. Kuikly creates and delegates the indication node —
the interaction source fires, the node attaches — but nothing it does reaches the view:
measured on the simulator, neither a draw over the content, nor a draw under it, nor a
layer transform from a `LayoutModifierNode` produced a single changed pixel, with an
opaque red fill as the probe. The platform's own default indication draws over the
content, which is why a plain `Modifier.clickable {}` answers a press with nothing.

So the response lives inside the components, and every tappable thing is one:

- A row is a `Cell`; a button is a `Button`; a choice is a `SegmentedControl` or a
  `ToggleButton`.
- Anything else that has to be tappable is wrapped in `PressableFeedback`.
- A component with its own surface may instead use `rowPressFeedback` (fill) or
  `pressScale`, since it is the one that knows its shape and background.

`check_press_feedback.sh` fails the build on a tap target with no response;
`check_sample_uses_components.sh` fails it on a raw tap modifier anywhere in the sample.
