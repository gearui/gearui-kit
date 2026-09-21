# GearUI Kit Specification

Status: current rules for the beta3 candidate. This is the single specification
entry point, not a release approval or a claim of completed visual parity.

## Document Ownership

| Document | Owns | Does not own |
| --- | --- | --- |
| [Design system](DESIGN_SYSTEM_SPEC.md) | Default appearance, component anatomy, state and theme rules, HeroUI Native source provenance | Runtime lifecycle or test results |
| [Engineering contract](ENGINEERING_CONTRACT.md) | Runtime, insets, API, resources, i18n, release gates and the executable CI gate list | Token values or upstream appearance |
| [Token format and adapter](../tokens/README.md) | DTCG 2025.10 data support, conversion and renderer limits | Component lifecycle or an exhaustive conformance claim |
| [Component coverage](COMPONENT_COVERAGE_SPEC.md) | Component inventory against HeroUI v3 and HeroUI Native, absorption priorities | Per-component visual values or acceptance results |
| [Migration](MIGRATION_1_0.md) | Consumer source/binary changes | Release approval |
| [Release procedure](RELEASING.md) | Candidate, artifact and publication workflow | Automatic permission to publish |

## Precedence And Change Control

1. These documents have separate ownership; a rule must have one owner.
2. Current design identity is HeroUI Native's pinned open-source default.
   Tamagui, the old iOS-only identity and Phase-0 shadcn migration plans are
   historical, not competing current defaults.
3. Numeric defaults come from `tokens/` and generated source. Public signatures
   come from reviewed API dumps. Do not maintain a second numeric/API schema in prose.
4. A passing baseline check means the source matches that baseline. Refreshing a
   baseline does not establish compatibility with a previously published binary.
5. When a rule and the implementation differ, record a gap; do not silently call
   the implementation compliant or change the rule to make a check green.
6. Acceptance records describe a revision, platform and test scope. They cannot
   override a normative rule or establish a pass for an untested component.
7. Code comments are English. User-facing text belongs in typed language packs.
   Public guides can have `.zh-Hans.md` companions; do not claim translations
   are synchronized when they are not.

## Implementation And Evidence

- [Beta3 readiness](BETA3_RELEASE_READINESS.md): current release decision and blockers.
- [Component matrix](COMPONENT_ACCEPTANCE_MATRIX.md): component-specific gaps.
- [Component guides](components/README.md): documented examples and gaps.
- [Navigator swipe-back design](NAVIGATOR_SWIPE_BACK_DESIGN.md): the implemented
  edge/full-width gesture arbitration and its rationale.

Dated acceptance batches and completed upgrade audits live in
[`_archive/`](_archive/README.md): [Kuikly 2.28 dependency audit](_archive/DEPENDENCY_UPGRADE_2_28.md),
[beta3 device acceptance](_archive/BETA3_DEVICE_ACCEPTANCE.md),
[DTCG evidence](_archive/DTCG_ACCEPTANCE.md),
[surface evidence](_archive/SURFACE_RENDERING_ACCEPTANCE.md) and the
[standardization log](_archive/STANDARDIZATION_ACCEPTANCE.md). They describe a
revision, platform and scope; they cannot override a normative rule.

## Contributor Route

Read the owning rule, change token source before generated code, add regression
coverage, check the sample and consumer, then update migration/evidence as needed.
Use [component code](COMPONENT_TEMPLATE.md) and [documentation](COMPONENT_DOC_TEMPLATE.md)
templates. Resource, keyboard and overlay fixes require runtime verification, not
only a screenshot. See the engineering contract's
[executable quality gates](ENGINEERING_CONTRACT.md#8-executable-quality-gates) for the actual checks.

## History

The exact pre-consolidation documents are retained in
[`_archive/pre-beta3/`](./_archive/pre-beta3/README.md). They explain past decisions
but have no current normative authority. Superseded standalone documents were
merged into the owning specification or moved to `_archive/`; the redirect stubs
are gone.
