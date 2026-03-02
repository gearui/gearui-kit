# GearUI Architecture Guardrails

## 1. 使用方式

本文档是 PR 审查硬规则。命中任一 `REJECT` 条款，默认拒绝合并，除非有架构 owner 明确豁免。

---

## 2. REJECT 条款（不可违规）

### 2.1 Runtime 边界

- `REJECT`：组件直接监听系统级事件（scroll / back / route）。
- `REJECT`：组件自行实现 Overlay 关闭策略。
- `REJECT`：组件绕过 Runtime 直接修改全局主题、语言或路由状态。

### 2.2 Token 与样式

- `REJECT`：组件层新增硬编码颜色值（`Color(0x...)` 等）替代语义 Token。
- `REJECT`：整体替换 `ThemeSpec` 造成未声明 Token 丢失。
- `REJECT`：绕过 Token 体系直接写死样式分支。
- `REJECT`：交互组件缺失统一状态映射（`default/hover/active/focus/disabled/invalid`）。
- `REJECT`：组件直接耦合全局语义色，未通过组件角色 Token（`container/content/interactive/border/focus`）中转。
- `REJECT`：新增 `Theme.colors` 字段不遵循语义成对规则（如 `primary/primaryForeground`、`card/cardForeground`）。

### 2.3 Overlay

- `REJECT`：Overlay 不经过 Runtime 创建与销毁。
- `REJECT`：Overlay 内直接操作路由或全局状态。
- `REJECT`：Overlay 通过组件私有监听逻辑实现关闭。

### 2.4 API 兼容

- `REJECT`：公共 API 破坏性变更无兼容方案与迁移说明。
- `REJECT`：同名参数在不同组件语义不一致。
- `REJECT`：已公开参数语义被改写但未升级版本等级。

### 2.5 Sample 与文档

- `REJECT`：新增 Runtime 能力未在 `sample` 提供真实使用路径。
- `REJECT`：新增组件无最小示例和迁移说明。
- `REJECT`：sample 中出现绕过 Runtime 的临时代码。
- `REJECT`：主题/配色改造 PR 未更新语义映射文档（`GEARUI_TOKEN_SEMANTIC_FREEZE_V2.md`）。
- `REJECT`：组件配色改造 PR 未更新角色用色矩阵（`GEARUI_COMPONENT_COLOR_ROLE_MATRIX.md`）。

---

## 3. WARN 条款（需说明）

- `WARN`：核心路径新增对象分配，可能扩大重组范围。
- `WARN`：Overlay 行为依赖平台分支，未给出一致性验证。
- `WARN`：主题扩展点新增但无可回归样例。

---

## 4. 审查清单（Reviewer Checklist）

1. 是否遵守 Runtime 责任边界。
2. 是否保持 Token 合并制与语义完整性。
3. 是否通过 Overlay Runtime 统一策略。
4. 是否满足 API 兼容与迁移约束。
5. 是否补齐 sample 验证与文档说明。
6. 是否给出性能影响说明（至少定性）。

---

## 5. 建议 CI 映射（最小集）

1. `api-compat-check`：阻断破坏性 API 变更。
2. `token-compat-check`：阻断 Token 删除/重命名与合并破坏。
3. `sample-smoke`：验证 Runtime/Overlay/Theme 关键路径可运行。
4. `benchmark-nightly`：跟踪结构性与结果性性能指标。
