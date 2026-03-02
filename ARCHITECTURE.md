# GearUI Kit Architecture

本文档描述当前可执行架构，不记录阶段性过程。

## 分层结构

```text
Application Layer
  - View
  - GearApp

Component Layer
  - com.gearui.components.*

Foundation Layer
  - tokens / primitives / interaction / layout

Runtime Services
  - Theme
  - I18n
  - Overlay

Kuikly Compose
```

## 关键约束

1. Runtime 边界：页面入口统一通过 `GearApp`，避免在业务层重复挂载 Theme/Overlay 根。
2. Token 约束：组件层禁止新增硬编码颜色，使用语义 Token。
3. API 兼容：公开 API 变更要有迁移说明，不做静默破坏。
4. Sample 约束：`sample/` 只验证用法，不承载框架内部实现逻辑。

## 模块与目录

- 核心模块：`gearui-kit/`
- 示例模块：`sample/`
- 组件代码：`gearui-kit/src/commonMain/kotlin/com/gearui/components`
- 主题与 Token：`gearui-kit/src/commonMain/kotlin/com/gearui/theme`、`.../foundation`

## 规范文档

统一入口：[`docs/SPEC.md`](./docs/SPEC.md)

- 总体规范：`docs/GEARUI_SPEC_2026.md`
- 审查护栏：`docs/ARCHITECTURE_GUARDRAILS.md`
- CI 对应：`docs/SPEC_CI_MAPPING.md`
- Token 语义冻结：`docs/GEARUI_TOKEN_SEMANTIC_FREEZE_V2.md`
- 颜色角色矩阵：`docs/GEARUI_COMPONENT_COLOR_ROLE_MATRIX.md`
- 组件模板：`docs/COMPONENT_TEMPLATE.md`
