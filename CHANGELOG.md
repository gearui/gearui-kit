# Changelog

## [Unreleased] - targeting 1.0.0-beta3

This is an unpublished candidate. See [release readiness](docs/BETA3_RELEASE_READINESS.md)
for verified gates and outstanding work. Earlier implementation notes are preserved
in [the pre-beta3 archive](docs/_archive/pre-beta3/CHANGELOG.md).

### Design system and components

- Align the default design with the pinned open-source HeroUI Native reference,
  not Tamagui or paid HeroUI Pro presets. Brand accent and shape remain independent.
- Unify field hierarchy, focus/press feedback and disabled appearance. Input and
  Textarea labels default above the editor; explicit horizontal layout remains.
- Improve Select/tree selection presentation, navigation and sample composition;
  remove redundant example wrappers and misleading platform-specific labels.
- Introduce shared layered surface decoration, border styling and platform-aware
  font resolution. Rendering limitations remain documented; not every legacy
  component has migrated to the shared surface renderer.
- Preserve PRIMARY as Button's default theme; DEFAULT explicitly selects neutral.
- Match sample status-area backgrounds to their navigation bars. Keep the Android
  system navigation bar transparent, including after theme changes.

### Data and runtime

- Add DTCG 2025.10 Format/Resolver validation and deterministic Kotlin generation,
  including composite material tokens, color conversion and font fallback lists.
  See [adapter policy](tokens/README.md); data support is not universal pixel parity.
- Consolidate safe-area and overlay contracts and typed route navigation.
- Cover calendar, slider, grid, navigation and token/runtime behavior with regression
  tests. Calendar uses the local Gregorian date; slider steps are range-relative.

### Compatibility and packaging

- Removed ButtonType.GHOST: migrate to TEXT. Navigator uses NavRoute-typed entries
  instead of String routes and external payload maps; controller identity is owned
  by the framework. See [migration notes](docs/MIGRATION_1_0.md).
- Public theme/text/surface API additions require downstream recompilation; a
  refreshed API baseline does not establish binary compatibility with beta2.
- Kuikly 2.27.0; align consuming renderers and generated bindings to the same version.
- CI now explicitly runs Android Kotlin tests/lint and browser Kotlin tests, in
  addition to existing generation, guardrail, build and API checks.

### Documentation

- [SPEC](docs/SPEC.md) is the authoritative entry. Visual rules, runtime contracts,
  DTCG adapter policy, source provenance and acceptance evidence have distinct owners.
- Superseded rules are retained losslessly in a non-normative archive.

## [1.0.0-beta2] — last published release
