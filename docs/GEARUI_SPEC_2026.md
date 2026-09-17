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

[CI mapping](SPEC_CI_MAPPING.md) lists implemented guards, Kotlin/Python/browser
tests, builds, API and lint checks. Manual gesture/screen-reader/device tests and
performance goals remain separately recorded. Never count compilation as interaction.

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
