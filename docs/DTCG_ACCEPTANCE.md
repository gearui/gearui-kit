# DTCG 2025.10 Alignment Evidence

> This is batch evidence, not a release verdict. See [current beta3 readiness](BETA3_RELEASE_READINESS.md) for the latest verification and limitations.

## Scope

Implement the standard data model and deterministic build-time composition,
without silently changing current GearUI defaults or conflating renderer limits
with token validity. The implementation policy and remaining renderer work are
listed in `tokens/README.md`.

## Current Verification

- 119 Python tests pass, covering generation, Format, Resolver, color conversion,
  adapter policy, font lists/tracking and border styles.
- All five production Kotlin defaults match the resolver-generated output
  byte-for-byte. This change does not alter current default colors or geometry.
- 20 nontrivial color vectors evaluated independently with CSSWG JavaScript
  match Python output to eight decimal places. The fixture stores the reference
  source hash. Existing OKLab primary/polar and roundoff tests remain enabled.
- The shipped example checks all four brand/shape combinations; changing shape
  leaves the accent token unchanged, and changing brand leaves radius unchanged.
- The surface follow-up adds runtime rendering and font tests. See
  `SURFACE_RENDERING_ACCEPTANCE.md` for current device and build evidence.
  Public API additions include TextStyle family/tracking fields; this is not a
  binary-compatible constructor change and consumers must recompile.
- CI configuration now discovers every `test_*.py`, not only `test_tokens.py`.
  These new changes have not been pushed or verified in a remote CI run.

## Negative Verification

Five isolated in-memory mutations were checked against the relevant tests;
production files were not rewritten for these checks:

| Mutation | Test that fails |
| --- | --- |
| Give group type precedence over an alias's inferred type | `test_alias_type_inferred_before_group_type` |
| Skip composite member alias type checking | `test_composite_member_alias_type_not_just_shape` |
| Remove local-file root containment | `test_symlink_cannot_escape_local_root` |
| Return merged tokens without resolving their final aliases | `test_contexts_orthogonal_and_aliases_after_merge` |
| Silently clip colors under the default strict policy | `test_explicit_clipping` |

Other negative cases include group/reference cycles, missing targets, invalid
metadata, duplicate JSON keys, unknown context inputs, invalid unselected source
references, unselected final alias failures, cross-file cycles and array alias
flattening. Whole-token replacement is tested separately from resolver reference
sibling overrides because their merge semantics differ.

## Remaining Acceptance

Do not label this as certified/exhaustive Format/Color/Resolver conformance.
The normative specifications remain authoritative when an untested edge case is
reported. In particular, the gradient section's examples contain array-reference
ambiguity: we follow the explicit no-flattening requirement and reject nested
arrays rather than guessing how to expand them.

Remaining renderer work is concrete:

- Broader gradient rendering/interpolation acceptance.
- Arbitrary-path shadows and exact two-dimensional blur convolution.
- Host font-file registration and device font availability beyond generic fallbacks.
- Native wide-gamut rendering and any perceptual gamut-mapping requirement.
- Runtime injection of every geometry role, independent from this build-time
  Resolver and existing Theme/brand/shape APIs.

The surface follow-up has real iOS/Android/Web fixture verification, documented
separately; HarmonyOS visual verification remains unavailable without a target.
The gradient follow-up adds Picker mask data without changing existing palette or
geometry values; previous component gesture, keyboard, hot-shape and material
acceptance items remain open.

## Reference and Publication Follow-up

- Group extension and token-level JSON references now reach paths introduced by
  inheritance, independent of source declaration order. Missing paths and cycles
  still fail. Both inherited-path regression tests failed before the fix.
- All five Kotlin conversions finish before any output is modified. Complete
  files are then staged, and each destination is atomically replaced. A conversion
  failure or staging failure leaves existing outputs untouched; temporary files
  are cleaned up. A crash between replacements is NOT a cross-file transaction.
- Unchanged outputs retain their modification time and changed files retain
  permissions. Check mode reports every stale output without writing anything.
- Resolver and generator command-line context inputs use the same strict JSON
  loader as source files; duplicate keys are errors, not last-value wins.
- Command-line integration tests cover invalid root-font values, duplicate
  contexts, and a resolved export that passes standard-data validation again.
- Defaults and public Kotlin signatures remain unchanged; this follow-up adds
  no new claims about simulator or native material acceptance.

## Gradient Consumer Follow-up

- Standard gradient values now generate Kotlin stop lists, retaining color alpha,
  repeated positions and alias resolution. Position clamping follows the data
  specification; unsupported rendering inputs are not silently approximated.
- A vertical brush adapter preserves stops and renders a single stop as a solid
  color. It explicitly rejects empty lists and decreasing positions. These are
  adapter restrictions, not additional restrictions on valid standard data.
- Multi-column and linked Picker masks now consume generated gradient tokens.
  Their explicit alpha-mask operation reads the current theme surface while
  rendering, instead of embedding gradient colors inside the component.
- Five runtime tests cover stop preservation, hard edges, single-stop rendering,
  invalid inputs and theme/alpha mask behavior. Four Python tests cover lowering,
  references, invalid values and source-to-generated-mask propagation.
- No public component signature changes. The KLib baseline adds only Compose
  stability metadata for the internal gradient-stop type.
- Simulator appearance, native interpolation and platform parity remain separate
  acceptance tasks; passing these tests is not visual acceptance.
