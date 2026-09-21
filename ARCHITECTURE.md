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

统一入口：[`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md)

- 设计系统：`docs/DESIGN_SYSTEM.md`
- 多语言系统：`docs/I18N.md`
- 视觉设计规范：`docs/VISUAL_SPEC.md`
- 组件封装规范：`docs/COMPONENT_SPEC.md`
- 组件覆盖对比：`docs/COMPONENT_COVERAGE.md`
- 质量状态与发布：`docs/QUALITY_STATUS.md`
