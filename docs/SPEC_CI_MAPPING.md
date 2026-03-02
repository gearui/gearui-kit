# SPEC -> CI 检查项映射表

## 目的

把 `GEARUI_SPEC_2026.md` 中的规则映射为可执行 CI 检查，避免规范停留在文档层。

---

## 已落地（本次）

1. 组件硬编码颜色护栏  
SPEC 映射：
- 4.1 Token 治理（禁止组件层硬编码颜色）
- ARCHITECTURE_GUARDRAILS.md 2.2

实现：
- 脚本：`scripts/ci/check_component_hardcoded_colors.sh`
- 基线：`scripts/ci/hardcoded_color_baseline.txt`
- CI：`.github/workflows/guardrails.yml`

策略：
- 基线冻结历史债务，仅阻断新增违规。

2. Sample Runtime 边界护栏  
SPEC 映射：
- 3.5 Runtime Responsibility Boundary
- 6.4 Sample 即架构验证

实现：
- 脚本：`scripts/ci/check_sample_runtime_boundary.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- 强制 `MainDemo` 使用 `GearApp` 入口。
- 禁止 sample 直接挂 `Theme(...)` 和 `GearOverlayRoot(...)`。

---

## 下一批（建议 4 周内完成）

1. API 兼容检查（P0）  
SPEC 映射：
- 4.2 API 兼容治理

计划：
- 引入 public API baseline。
- PR 自动输出 API diff，并阻断破坏性变更。

2. Token 兼容检查（P0）  
SPEC 映射：
- 4.1 Token 治理（schema version + diff）

计划：
- 生成 Token 快照（字段、语义、默认值）。
- 检查删除/重命名与语义漂移。

3. Overlay 行为冒烟（P1）  
SPEC 映射：
- 4.4 Overlay Architecture

计划：
- sample 增加关键 Overlay 回归场景。
- CI 冒烟校验展示、关闭与路由切换行为。

4. 结构性性能指标（P1）  
SPEC 映射：
- 5.2.1 结构性性能指标

计划：
- 记录主题切换重组范围、Overlay 影响范围。
- 先夜跑告警，再逐步升级阻断阈值。

