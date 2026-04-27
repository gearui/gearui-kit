# SPEC -> CI 检查项映射表

## 目的

把 `GEARUI_SPEC_2026.md` 中的规则映射为可执行 CI 检查，避免规范停留在文档层。

---

## 已落地（本次）

1. 组件硬编码颜色护栏  
SPEC 映射：
- 4.1 Token 治理（禁止组件层硬编码颜色）
- 11.1 REJECT / 2. Token 与样式

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

3. SafeArea Runtime Contract 护栏  
SPEC 映射：
- 4.5 Runtime Environment & Insets Pipeline
- 4.6 Fullscreen Container Contract
- 11.1 REJECT / Runtime 边界

实现：
- 脚本：`scripts/ci/check_safearea_runtime_contract.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- 禁止在组件/样例层重新引入 `useSafeArea` 参数或调用。
- 限制 `safeAreaInsets` 直接访问仅能出现在 Runtime 与受控 fallback 文件。

4. Single GearApp Root 护栏
SPEC 映射：
- 4.5 Runtime Environment & Insets Pipeline（GearApp 入口约束）
- 11.1 REJECT / Runtime 边界

实现：
- 脚本：`scripts/ci/check_single_gearapp_root.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- sample 树只允许一个 `GearApp` 入口，且必须在 `MainDemo.kt`。
- 禁止 sample 业务页面新增第二个 `GearApp` 根入口，避免 runtimeFlags 被覆盖。

5. Token Schema 兼容检查（P0）  
SPEC 映射：
- 4.1 Token 治理（schema + 语义漂移）
- 11.1 REJECT / 2. Token 与样式

实现：
- 脚本：`scripts/ci/dump_token_snapshot.py`（生成）+ `scripts/ci/check_token_compat.sh`（diff 检查）
- 基线：`gearui-kit/api/tokens.api`
- CI：`.github/workflows/guardrails.yml`

策略：
- 覆盖 `GearColors` / `GearTypography` / `GearShapes` 三个 data class 的字段集合（删除/重命名 → fail）。
- 覆盖 `Themes.Light` / `Themes.Dark` 颜色预设字面值（语义漂移 → fail）。
- `ColorTokens`（`const val` 调色板）由 BCV 覆盖，不重复。
- 任何变更必须显式刷新 baseline：`./scripts/ci/dump_token_snapshot.sh > gearui-kit/api/tokens.api`。

6. 公共 API 兼容检查（P0）  
SPEC 映射：
- 4.2 API 兼容治理
- 11.1 REJECT / 5. API 兼容

实现：
- 插件：`org.jetbrains.kotlinx.binary-compatibility-validator` 0.17.0（根 `build.gradle.kts` 内 apply）
- 基线：
  - `gearui-kit/api/gearui-kit.api`（Android JVM 字节码 API）
  - `gearui-kit/api/gearui-kit.klib.api`（iOS targets 的 KLib ABI）
- CI：`.github/workflows/ci.yml` job `api-compat-check`（macOS runner，跑 `:gearui-kit:apiCheck`）

策略：
- 任何对外 API（含 KLib ABI）变化必须通过 `./gradlew :gearui-kit:apiDump` 显式更新 baseline，PR 中可审计。
- KLib dump 含 iOS 全部 target，必须在 macOS 上执行；CI 与本地刷新基线时同样要求 macOS。
- `sample` 模块在 `apiValidation.ignoredProjects` 中排除。

---

## 下一批（建议 4 周内完成）

1. Overlay 行为冒烟（P1）  
SPEC 映射：
- 4.4 Overlay Architecture

计划：
- sample 增加关键 Overlay 回归场景。
- CI 冒烟校验展示、关闭与路由切换行为。

2. 结构性性能指标（P1）  
SPEC 映射：
- 5.2.1 结构性性能指标

计划：
- 记录主题切换重组范围、Overlay 影响范围。
- 先夜跑告警，再逐步升级阻断阈值。

3. SafeArea / Fullscreen Contract 检查（P1）  
SPEC 映射：
- 4.5 Runtime Environment & Insets Pipeline
- 4.6 Fullscreen Container Contract

计划：
- 静态检查禁止在 `GearApp` 根容器与 `GearOverlayHost` 根层引入 safeArea padding。
- 静态检查关键组件（NavBar/BottomNavBar/Drawer/ActionSheet）优先读取 `LocalGearRuntimeEnvironment`。
- 先告警模式上线，稳定后升级为阻断。

4. App 层 Single Root 扩展检查（P1）
SPEC 映射：
- 4.5 Runtime Environment & Insets Pipeline（GearApp 入口约束）
- 11.1 REJECT / Runtime 边界

计划：
- 将 single-root 检查从 sample 扩展到业务 App 代码仓（跨仓 guard）。
- 若页面覆盖 `View` 自动包装，必须显式声明（如 `autoWrapGearApp=false`）并保留唯一 `GearApp` 入口。
