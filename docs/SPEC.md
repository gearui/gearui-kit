# GearUI Kit Specification

Status: current rules for the beta3 candidate. This is the single specification
entry point, not a release approval or a claim of completed visual parity.

## Document Ownership

| Document | Owns | Does not own |
| --- | --- | --- |
| [Design system](DESIGN_SYSTEM_SPEC.md) | Default appearance, component anatomy, state and theme rules | Runtime lifecycle or test results |
| [Engineering contract](GEARUI_SPEC_2026.md) | Runtime, insets, API, resources, i18n and release gates | Token values or upstream appearance |
| [Token format and adapter](../tokens/README.md) | DTCG 2025.10 data support, conversion and renderer limits | Component lifecycle or an exhaustive conformance claim |
| [Reference map](HEROUI_NATIVE_ALIGNMENT.md) | Pinned open-source HeroUI Native source locations | Paid/Web preset promises or completion claims |
| [CI mapping](SPEC_CI_MAPPING.md) | Exact executable checks and their limits | Repeating design rules |
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

- [Kuikly 2.28 dependency audit](DEPENDENCY_UPGRADE_2_28.md): dependency selections,
  compatibility pins and new verification results.

- [Beta3 readiness](BETA3_RELEASE_READINESS.md): current release decision and blockers.
- [Component matrix](COMPONENT_ACCEPTANCE_MATRIX.md): component-specific gaps.
- [DTCG evidence](DTCG_ACCEPTANCE.md): token compiler tests and limits.
- [Surface evidence](SURFACE_RENDERING_ACCEPTANCE.md): focused material/font device checks.
- [Standardization log](STANDARDIZATION_ACCEPTANCE.md): dated implementation evidence,
  not the source of current rules or test totals.
- [Component guides](components/README.md): documented examples and gaps.

## Contributor Route

Read the owning rule, change token source before generated code, add regression
coverage, check the sample and consumer, then update migration/evidence as needed.
Use [component code](COMPONENT_TEMPLATE.md) and [documentation](COMPONENT_DOC_TEMPLATE.md)
templates. Resource, keyboard and overlay fixes require runtime verification, not
only a screenshot. See [CI mapping](SPEC_CI_MAPPING.md) for the actual checks.

## History

The exact pre-consolidation documents are retained in
[`_archive/pre-beta3/`](./_archive/pre-beta3/README.md). They explain past decisions
but have no current normative authority. Legacy document paths remain as redirects.
