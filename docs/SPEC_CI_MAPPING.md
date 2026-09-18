# Specification To CI Mapping

Current rules: [SPEC](SPEC.md). Workflows implement checks; reports record results.
No static check establishes complete visual, accessibility or performance parity.

## Static Gates

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

## Generated Data And Unit Tests

- Guardrails: `python3 scripts/generate_tokens.py --check` and
  `python3 -m unittest discover -s scripts/tests -p 'test_*.py'`.
- Android CI: `:gearui-kit:testDebugUnitTest` and `:gearui-kit:lintDebug`.
- Web CI: `:gearui-kit:jsBrowserTest` with Chrome available. Absence of Chrome is
  a setup failure, never permission to skip the tests silently.
- macOS CI: `:gearui-kit:apiCheck` checks JVM and KLib baselines. Do not run apiDump
  in a verification job; it would bless the change under test.
- Local source-reference audit: `node scripts/check_heroui_reference.mjs <checkout>`.
  The sibling reference checkout is not a mandatory CI dependency.

## Build Gates

CI builds Android kit/sample, links the iOS simulator sample, runs the iOS
simulator Kotlin tests against the real Kuikly host (`scripts/ios_native_tests.sh`)
and builds the Web host. Release packaging additionally checks all three iOS artifacts, metadata,
Android AAR, JS artifact and assets from the exact candidate. HarmonyOS uses the
separate build and needs explicit compile/package/device evidence.

## Manual And Unimplemented Gates

Critical input/keyboard, overlay dismissal and gesture interruption, live theme
changes, large text, real asset loading, screen readers and frame performance need
runtime evidence. Nightly performance and full-device visual automation are not
implemented by the shell guards. Track them in the release report and component
matrix rather than repeatedly assigning obsolete milestone dates.
