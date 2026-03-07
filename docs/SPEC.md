# GearUI Kit Specs

`docs/` 只保留长期有效、可执行的规范文档。

## 必读（对外有效）

1. `GEARUI_SPEC_2026.md`
2. `ARCHITECTURE_GUARDRAILS.md`
3. `SPEC_CI_MAPPING.md`

## 主题与用色规范

1. `GEARUI_TOKEN_SEMANTIC_FREEZE_V2.md`
2. `GEARUI_COMPONENT_COLOR_ROLE_MATRIX.md`

## 开发模板

1. `COMPONENT_TEMPLATE.md`

## 清理原则

- 过程性文档（阶段计划、审计快照、一次性修复记录）不放在 `docs/` 主路径。
- 当规范被 CI 承接时，以 `SPEC_CI_MAPPING.md` 为准。
- 同一交互语义只保留一个核心组件入口，禁止长期并行维护同义组件。
