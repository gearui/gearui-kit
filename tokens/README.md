# Token Data Contract

GearUI token data targets DTCG **2025.10**, not a permanent proprietary subset.
The normative references are the [Format](https://www.designtokens.org/tr/2025.10/format/),
[Color](https://www.designtokens.org/tr/2025.10/color/), and
[Resolver](https://www.designtokens.org/tr/2025.10/resolver/) modules. These are
Community Group specifications, not W3C Recommendations.

Use standard `$type`, `$value`, composite structures and references. Vendor
metadata belongs in namespaced `$extensions`. `reference/` provenance manifests
are build metadata, not token documents.

## Processing Boundary

The production path is:

```
gearui.resolver.json + five token sources
  -> ordered sets / selected modifier contexts
  -> group inheritance / references / type validation
  -> resolved standard token data
  -> explicit Kotlin renderer adapters
  -> generated defaults consumed by GearUI
```

`dtcg_format.py` has no dependency on Kotlin or rendering. A valid dashed border,
font fallback list, fractional duration, wide-gamut color, or inset shadow is
valid interchange data even when the renderer cannot display it. Source documents
are never mutated; aliases and extension metadata remain in the originals.
Resolved JSON export retains data not consumed by the Kotlin generator.

## Implemented Coverage

| Module | Implemented behavior |
| --- | --- |
| Format structure | Tokens vs groups, inherited and alias-inferred types, explicit `$root`, group extension/deep merge, atomic token replacement, description/extensions/deprecation validation and preservation |
| Format references | Local brace aliases, chains, JSON Pointers including array indices and escaped names, whole-token and composite-member type checking, missing/cyclic reference errors |
| Format values | All 13 standard types: color, dimension, fontFamily, fontWeight, duration, cubicBezier, number, strokeStyle, border, transition, shadow, gradient, typography |
| Composite values | Ordered shadow arrays, optional inset, signed dimensions, custom dash arrays/caps, full transitions, typography members, gradient positions clamped on resolved output; arrays never flattened |
| Color data | All 14 specified spaces, numeric or `none` components, optional alpha and six-digit hex fallback, per-space range validation |
| Resolver | Version, sets, modifiers, contexts/defaults, inline named entries, ordered overrides, final-graph alias resolution, reference sibling shallow overrides, duplicate names and forbidden/cyclic references |
| Resolver validation | Every source reference is loaded, including unselected contexts; production generation validates every final context combination before lowering |
| Local imports | Relative files/fragments, nested relative imports, strict JSON, duplicate-key errors, finite numbers, symlink/path containment |

The capability matrix is backed by repository tests, not a certification claim.
Keep adding interoperability fixtures rather than equating a green build with
exhaustive conformance. See [acceptance evidence](../docs/DTCG_ACCEPTANCE.md).

## Deliberate Policies

- Modifier names and contexts are **case-sensitive**. A single-context modifier
  is accepted; zero contexts are rejected. Unknown inputs fail.
- Local imports are allowed only under the resolver's declared token root.
  Network, query-bearing and non-local URI schemes are rejected, not fetched.
  The Resolver specification permits tools to choose supported URI schemes.
- `$defs` is accepted and otherwise ignored; source pointers into it can resolve
  to ordinary token objects. This tool is not a JSON Schema validator.
- Context validation has an explicit 256-combination ceiling. Exceeding it fails
  instead of silently validating only the default; the Python API accepts a
  caller-supplied limit for larger reviewed matrices.
- Malformed references fail with the token path or context. Unknown vendor data
  inside `$extensions` is preserved and never treated as executable references.

## Kotlin Adapter Policy

- Dimensions: `px` becomes logical `dp`; `rem` uses an explicit build-time root of
  **16 logical px**, overridable with `--root-font-px`. This is fixed lowering,
  not dynamic device font scaling. Geometry sizes must remain nonnegative.
- Typography: px/rem sizes and fractional line heights are preserved to six
  decimal places, rather than whole-pixel rounding. Named and integer weights
  from 1 through 1000 are supported. `TextStyle` preserves ordered font families
  and letter spacing. Hosts register installed family names through
  `LocalFontRegistry`; unavailable names fall through to a generic/system font.
  Fractional weights remain explicit adapter errors, not format errors.
- Colors: all 14 numeric spaces convert to sRGB using CSS Color 4 transfer/matrix
  equations (and the published OKLab inverse). Alpha is retained. The checked-in
  numeric fixture records the CSSWG reference hash, including its SDR Rec2020
  gamma-2.4 transfer convention. Output retains nine decimal places.
- Default color policy is **error** for out-of-gamut colors and `none` components.
  `--color-gamut clip` explicitly opts into channel clipping, not perceptual gamut
  mapping. `--missing-color zero` is for static rendering only, never for color
  interpolation. Original data remains unchanged. Tiny matrix roundoff within
  1e-6 in linear RGB is clamped under either gamut policy.
- Durations: the current animation API requires integer milliseconds. Fractional
  milliseconds are preserved in standard JSON but rejected in Kotlin lowering.
  Negative timing values likewise cannot be used by this adapter.
- Materials: ordered shadow layers, inset/spread and full transition data are
  preserved. All eight standard border keywords and custom dash arrays/caps lower
  to `SurfaceBorder`. `DecoratedSurface` paints shadows and borders using shared
  Canvas geometry; `LocalSurfaceShadowStyles` overrides surface/field/overlay stacks.
- Gradients: Kotlin lowering preserves ordered stops, alpha and repeated stops
  for hard edges, with standard position clamping. The vertical brush adapter
  renders one stop as a solid color and rejects empty or descending stop lists
  explicitly. Direction is a rendering choice, not a DTCG gradient property.
  Picker consumes dedicated alpha-mask tokens tinted with the current theme
  surface; ordinary gradients retain their source colors.

**Rendering remains a separate acceptance gate.** The shared surface renderer
supports rectangular/rounded outlines, not arbitrary paths. Blur uses a bounded
Gaussian edge approximation, not exact two-dimensional convolution. JSON support does not establish
native wide-gamut display, custom-font availability, gradient interpolation,
material fidelity, or cross-platform animation parity. These are tracked renderer
adapter tasks, not reasons to narrow the standard data model.

## Commands

```
python3 scripts/generate_tokens.py --check
python3 -m unittest discover -s scripts/tests -p 'test_*.py'
python3 scripts/dtcg_resolver.py tokens/gearui.resolver.json --all-contexts --output /tmp/gearui.tokens.json
python3 scripts/dtcg_resolver.py tokens/examples/theme.resolver.json --all-contexts --input '{"brand":"green","shape":"square"}'
```

The first two commands run in Guardrails CI. Generation uses the actual resolver,
not a separate validation-only pass. `--resolver` and `--input` let a build choose
its own manifest/context. The shipped theme example tests the independence of
brand color and shape; it does not replace the runtime application's Theme API.

Safe-area measurements, gesture ownership, cancellation and lifecycle rules are
runtime behavior, not design tokens. Numeric motion settings can be tokens;
a JSON file cannot specify correct interaction ownership.

## Output Publication

All Kotlin outputs are converted before any file is changed. Complete replacements
are staged first; each file is replaced atomically and unchanged files are left
untouched. Conversion/staging failures preserve existing outputs. This is not a
cross-file transaction in the event of power loss between replacements. Check
mode reports all stale destinations without writing. CLI context JSON follows
strict duplicate-key rules, just like token source files.
