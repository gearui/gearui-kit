# GearUI Kit SPEC (2026)

## 1. 目标与范围

本 SPEC 定义 GearUI Kit 在 2026 年度的统一工程标准，覆盖：
- 架构先进性
- 性能
- 易用性
- 灵活性

适用范围：
- `gearui-kit`（组件库）
- `sample`（示例与验证入口）
- CI/CD 与文档系统

---

## 2. 非目标

- 不在 2026 年内追求一次性重写组件库。
- 不引入与 KuiklyUI Runtime 冲突的底层渲染方案。
- 不允许在无兼容策略的前提下进行大规模 Breaking Changes。

---

## 3. 设计原则

1. Token First：组件样式能力优先通过语义 Token 建模。
2. Runtime Neutral：组件能力对 iOS/Android/鸿蒙/H5 等价可用。
3. Backward Compatibility：公共 API 与 Token 变更必须可审计。
4. Measurable Quality：性能与质量必须有可量化指标并入 CI。
5. Progressive Delivery：先建立护栏，再逐步优化实现。

### 3.5 Runtime Responsibility Boundary

GearUI Kit 严格区分 Runtime 能力与组件能力。

Runtime 层负责：
- Theme / DarkMode / I18n 的合并与传播。
- Overlay 的生命周期、关闭策略与事件监听。
- 路由切换、返回键、系统事件的统一处理。
- 性能相关的全局策略（重组边界、状态收敛）。

组件层禁止：
- 直接监听系统级事件（scroll / back / route）。
- 自行实现 Overlay 关闭逻辑。
- 绕过 Runtime 直接操作全局状态。

违规处理：
- 违反以上约束的实现视为架构缺陷，不允许合并。

---

## 4. 架构先进性规范

### 4.1 Token 治理

- 引入 Token Schema 版本：`major.minor.patch`。
- 每次 Token 变更必须生成 diff 报告（新增/删除/语义变更）。
- 禁止组件层直接硬编码颜色；必须通过 `Theme.colors`。
- Token 采用语义合并制（merge-based）而非整体替换：
  - 上层仅覆盖声明的语义 Token。
  - 未覆盖部分必须从父级继承。
  - 禁止通过整体替换 `ThemeSpec` 绕过语义层。

验收标准：
- CI 中存在 `token-compat-check` 任务。
- 删除或重命名语义 Token 时，CI 默认失败。
- Theme / Component / Instance 覆盖不会导致未声明 Token 丢失。
- 合并过程不产生可观额外对象分配（需纳入 benchmark 验证）。

### 4.2 API 兼容治理

- 对 `gearui-kit` 对外 API 做兼容检查（binary/source API diff）。
- 破坏性变更必须附迁移说明与废弃周期。

验收标准：
- CI 中存在 `api-compat-check` 任务。
- 每次 release 产出变更级别标记：`patch/minor/major`。

### 4.3 主题覆盖体系

- 保留当前覆盖式 `ThemeSpec` 方案作为一等能力。
- 允许三层覆盖：
1. Global（全局主题）
2. Component（组件级 Token）
3. Instance（单实例参数）

验收标准：
- 至少 3 个核心组件支持 Component 级 Token 覆盖并有示例。

### 4.4 Overlay Architecture

Overlay 属于 Runtime 一等能力，而非组件实现细节。

规范要求：
- 所有 Overlay 必须通过 Overlay Runtime 创建与销毁。
- 组件仅声明 `OverlayOptions`（位置、模态、DismissPolicy）。
- Overlay 关闭策略（点击、滚动、超时、路由变化）统一由 Runtime 执行。

禁止行为：
- 组件直接监听 scroll / pointer 事件以关闭自身。
- Overlay 内部修改路由或全局状态。

验收标准：
- 删除 Overlay Runtime 后，相关组件无法编译。
- Overlay 行为在 sample 中可统一验证。

---

## 5. 性能规范

### 5.1 基准场景

必须长期追踪以下场景：
- 长列表（1000+ item）滚动与加载。
- 表单页（20+ 组件）输入与校验交互。
- 主题切换（Light/Dark/Custom）切换耗时。
- Overlay 叠加（Toast/Dialog/Popup）展示与销毁。

### 5.2 指标（默认目标）

- 首屏可交互时间（TTI）：
  - Android：<= 1200ms（中端机）
  - iOS：<= 1000ms（中端机）
  - H5：<= 1800ms（4G）
- 滚动丢帧率：
  - 列表滑动掉帧比例 < 3%
- 主题切换完成耗时：
  - <= 120ms（无明显闪烁）

### 5.2.1 结构性性能指标（必须跟踪）

除耗时与帧率外，需长期追踪：
- 主题切换时的重组节点数量。
- Overlay 出现/消失时受影响的重组范围。
- 重组深度（最大嵌套层级）。

目标：
- Overlay 操作不触发非 Overlay 子树重组。
- 局部 Token 覆盖不导致全局重组。

### 5.3 工程要求

- 建立 `benchmark` 任务并入 CI 夜跑。
- 提供性能回归阈值（超阈值失败或告警）。
- 对关键组件标注稳定性（`@Immutable/@Stable`）并减少无效重组。

---

## 6. 易用性规范

### 6.1 API 一致性

- 参数顺序统一：`modifier`、核心参数、行为回调、可选样式。
- 默认值与命名语义统一，避免同义参数并存。
- 参数语义一经公开不得变更含义（即使类型不变）。
- 同名参数在不同组件中的语义必须一致。

### 6.2 文档与示例

每个组件文档至少包含：
- 最小可用示例
- 生产推荐示例
- 常见问题与边界条件

`sample` 要求：
- 成为回归验证入口（主题、语言、平台行为）。
- 新增组件必须附 sample 页面与截图/录屏。

### 6.3 迁移体验

- 版本升级必须提供迁移清单。
- `@Deprecated` 至少保留一个小版本周期（紧急修复除外）。

### 6.4 Sample 即架构验证

`sample` 不仅是展示工具，也是架构回归入口。

要求：
- 所有 Runtime 能力必须在 sample 中被真实使用。
- sample 中禁止出现临时代码或绕过 Runtime 的实现。
- CI 需通过 sample 行为验证 Overlay / Theme / DarkMode。

---

## 7. 灵活性规范

### 7.1 品牌主题包（Brand Pack）

- 支持将“特黑/暗紫”等主题抽离为独立主题包。
- 主题包遵循统一 Token 接口并可按需加载。

### 7.2 局部扩展能力

- 允许组件级 Token 覆盖而不影响全局主题。
- 保证扩展点不会破坏平台一致性与可维护性。
- 禁止通过扩展点注入任意 Composable 改变组件结构。
- 禁止替换组件内部状态机。
- 禁止绕过 Token 体系直接操作样式。

### 7.3 设计到代码链路

- 规划 Token 生成链路（设计源 -> Token 代码）。
- 保证生成结果可审计、可回滚、可比对。

---

## 8. CI/CD 规范

默认流水线要求：
1. `build`：多模块编译通过。
2. `unit-test`：单元测试。
3. `api-compat-check`：公共 API 兼容检查。
4. `token-compat-check`：Token Schema 兼容检查。
5. `sample-smoke`：示例应用冒烟（关键页面）。
6. `benchmark-nightly`：性能夜跑。

---

## 9. 发布规范

- 发布前必须通过上述 CI 基线。
- 发布说明必须包含：
1. 新增能力
2. 兼容性变化
3. 性能变化
4. 迁移指引
- 若版本包含 Runtime / Overlay / Theme 行为变化，必须在发布说明中显式标注。

---

## 10. 里程碑验收（绝对日期）

### 截止 2026-04-30
- API 兼容检查与 Token 兼容检查上线 CI。
- 核心 10 个组件完成文档模板化。

### 截止 2026-08-31
- 性能基准体系上线并形成趋势看板。
- 主题切换与长列表性能达成基线目标。

### 截止 2026-12-31
- Brand Pack 机制可用。
- 组件级 Token 覆盖能力在核心组件稳定落地。
