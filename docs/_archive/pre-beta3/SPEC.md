# GearUI Kit Specs

`docs/` 只保留长期有效、可执行的规范文档。

## 必读（对外有效）

1. `GEARUI_SPEC_2026.md`
2. `SPEC_CI_MAPPING.md`

说明：
- `ARCHITECTURE_GUARDRAILS.md` 已并入 `GEARUI_SPEC_2026.md` 第 11 节，仅保留兼容跳转。

## 主题与用色规范

1. `GEARUI_TOKEN_SEMANTIC_FREEZE_V2.md`
2. `GEARUI_COMPONENT_COLOR_ROLE_MATRIX.md`

## 开发模板

1. `COMPONENT_TEMPLATE.md`（kotlin 代码骨架）
2. `COMPONENT_DOC_TEMPLATE.md`（对外文档结构 — SPEC 6.2）

## 组件文档

1. `components/README.md`（核心 10 组件文档索引与状态）

## 跨库接入

1. `I18N_INTEGRATION.md`（上层库接入分层 i18n — SPEC 2026 §12）

## 历史文档

迁移批次计划、一次性审计、RC 快照、spike 结论已移入 `_archive/`，仅供追溯，
**不具规范效力**。见 `_archive/README.md`。

## 清理原则

- 过程性文档（阶段计划、审计快照、一次性修复记录）不放在 `docs/` 主路径。
- 当规范被 CI 承接时，以 `SPEC_CI_MAPPING.md` 为准。
- 同一交互语义只保留一个核心组件入口，禁止长期并行维护同义组件。
