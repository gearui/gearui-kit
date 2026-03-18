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

### 4.5 Runtime Environment & Insets Pipeline（新增基线）

GearUI Kit 的系统环境数据采用统一 Runtime 管线，禁止业务侧分散处理。

统一数据流：
1. Platform（Android/iOS/Web）采集原始系统信息。
2. Runtime 归一化为 `RuntimeEnvironment`。
3. Compose/Kit 通过 `LocalRuntimeEnvironment`（或等价上下文）读取。
4. 组件按默认规则自动应用（NavBar/BottomNavBar/PageScaffold）。

职责边界：
- Runtime 负责采集与同步：
  - window metrics（width/height/activity size/density/orientation）
  - insets（status/navigation/gesture/ime）
  - safeArea（top/bottom/left/right，作为 insets 的派生视图）
  - theme state（dark mode / contrast）
- GearUI Kit 负责消费规则：
  - 不直接调用平台 API
  - 不在组件中实现平台分支采集逻辑

强约束：
- 安全区主来源必须是 Runtime；初始化传入仅可作为 override/fallback。
- 业务页面禁止手写系统安全区补丁（如 `padding(top = safeArea.top)` 作为常态方案）。
- 横屏与异形屏必须支持 `left/right` 安全区，不得只实现 `top/bottom`。

建议数据模型（规范性）：
- `RuntimeEnvironment`
  - `window`: `widthPx/heightPx/activityWidthPx/activityHeightPx/density/orientation`
  - `insets`: `statusBarTop/navigationBarBottom/gestureBottom/imeBottom/left/right`
  - `safeArea`: `top/bottom/left/right`
  - `theme`: `darkMode/contrastMode`

验收标准：
- sample 中可看到 `LocalRuntimeEnvironment` 驱动的 NavBar/BottomNavBar 自动安全区行为。
- Runtime insets 变化（旋转、系统栏变化、键盘变化）可触发组件正确重算。
- 业务 demo 不再依赖页面级 safe area 手工补丁。

### 4.6 Fullscreen Container Contract（新增硬约束）

GearUI Kit 默认运行前提：根容器必须具备全屏渲染优先级（edge-to-edge compatible）。

规范要求：
- GearApp/Root Host 必须 attach 到 fullscreen container（match-parent）。
- 非全屏容器会导致 insets/safeArea/overlay/keyboard 计算失真，视为架构错误。

运行时策略：
- Debug：检测到非全屏容器时，直接 fail-fast（抛错或阻断渲染）。
- Release：输出严重告警日志并上报 telemetry（`fullscreen_contract_violation`）。

验收标准：
- sample 提供全屏契约检测开关与可视化日志。
- Android/iOS Host 模板文档明确 edge-to-edge 与 fullscreen 必填项。

### 4.7 KuiklyUI Runtime Compatibility（新增）

GearUI Kit 在推进 RuntimeEnvironment / Insets 体系时，必须保证与 KuiklyUI Runtime 的兼容稳定性。

1. Single Source of Truth
- 系统环境数据唯一来源为 KuiklyUI Runtime（`LocalConfiguration` / `pageData`）。
- 禁止在 gearui-kit 内并行维护第二套 insets/safeArea 状态源。

2. Backward Compatibility
- 不得破坏现有 runtime 事件与字段语义，包括但不限于：
  - `safeAreaInsets`
  - `rootViewSizeDidChanged`
  - `windowSizeDidChanged`
  - `densityInfo`
- 若需扩展字段，必须采用向后兼容新增，不允许重定义旧字段含义。

3. Progressive Rollout
- 新的系统环境能力按“可选开关 -> 默认开启”渐进发布：
  - 阶段 1：feature flag（默认关闭）+ fallback 到旧路径
  - 阶段 2：灰度开启并监控
  - 阶段 3：默认开启，保留回退开关一个小版本周期

4. Fail Strategy
- Fullscreen Contract 冲突处理必须分环境：
  - Debug：fail-fast（抛错/阻断渲染）
  - Release：telemetry + 显式告警 + 可降级渲染
- 禁止在 Release 直接 crash 作为默认策略。

验收标准：
- 新能力上线后，历史 sample 与业务 demo 无需改代码即可运行。
- 兼容回归覆盖 `safeAreaInsets`、尺寸变化事件与密度变化事件。
- 文档中存在明确回退方案与开关生命周期说明。

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

### 6.5 输入焦点与键盘交互基线

输入组件（Input/SearchBar/基于输入原语封装的单行输入）必须遵循统一交互基线：
- 单行输入在 IME `Done`（或等价完成动作）触发时，默认执行 `clearFocus(force = true)` 并收起软键盘。
- 点击输入框外部空白区域、滚动容器等全局失焦策略，与 `Done` 行为保持一致，不允许出现状态分叉。
- 禁止把 `Done` 失焦逻辑下沉到业务页面；必须由组件或 Runtime 默认实现。

验收标准：
- iOS 与 Android 上，单行 Input 输入后点击输入法“完成”，键盘立即收起且光标失焦。
- 如业务需要保留焦点，必须通过显式参数关闭默认行为，默认值保持“完成即失焦”。

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

---

## 11. Architecture Guardrails（PR Gate）

本文档内规则即 PR 审查硬规则。命中任一 `REJECT`，默认拒绝合并（除非架构 owner 明确豁免）。

### 11.1 REJECT（不可违规）

1. Runtime 边界
- `REJECT`：组件直接监听系统级事件（scroll / back / route）。
- `REJECT`：组件自行实现 Overlay 关闭策略。
- `REJECT`：组件绕过 Runtime 直接修改全局主题、语言或路由状态。
- `REJECT`：组件或业务页面直接调用平台 API 采集 insets/safeArea（应由 Runtime 统一提供）。
- `REJECT`：业务页面长期手写 safe area padding 作为系统安全区主方案。

2. Token 与样式
- `REJECT`：组件层新增硬编码颜色值（`Color(0x...)`）替代语义 Token。
- `REJECT`：整体替换 `ThemeSpec` 造成未声明 Token 丢失。
- `REJECT`：绕过 Token 体系直接写死样式分支。
- `REJECT`：交互组件缺失统一状态映射（`default/hover/active/focus/disabled/invalid`）。
- `REJECT`：组件直接耦合全局语义色，未通过组件角色 Token 中转。
- `REJECT`：新增 `Theme.colors` 字段不遵循语义成对规则。

3. Overlay
- `REJECT`：Overlay 不经过 Runtime 创建与销毁。
- `REJECT`：Overlay 内直接操作路由或全局状态。
- `REJECT`：Overlay 通过组件私有监听逻辑实现关闭。
- `REJECT`：Overlay 遮罩不覆盖全屏容器（状态栏/手势区未纳入同一遮罩层级）。

4. Fullscreen Contract
- `REJECT`：GearApp/Root Host 未以全屏容器（match-parent/edge-to-edge）挂载。
- `REJECT`：在非全屏前提下继续以“页面补丁”规避系统栏与安全区问题。
- `REJECT`：缺失全屏契约检测（Debug fail-fast 或 Release telemetry 其一都没有）。

5. API 兼容
- `REJECT`：公共 API 破坏性变更无兼容方案与迁移说明。
- `REJECT`：同名参数在不同组件语义不一致。
- `REJECT`：已公开参数语义被改写但未升级版本等级。

6. Sample 与文档
- `REJECT`：新增 Runtime 能力未在 sample 提供真实使用路径。
- `REJECT`：新增组件无最小示例和迁移说明。
- `REJECT`：sample 中出现绕过 Runtime 的临时代码。
- `REJECT`：主题/配色改造 PR 未更新语义映射文档。
- `REJECT`：组件配色改造 PR 未更新角色用色矩阵。

### 11.2 WARN（需说明）

- `WARN`：核心路径新增对象分配，可能扩大重组范围。
- `WARN`：Overlay 行为依赖平台分支，未给出一致性验证。
- `WARN`：主题扩展点新增但无可回归样例。

### 11.3 Reviewer Checklist

1. 是否遵守 Runtime 责任边界。
2. 是否保持 Token 合并制与语义完整性。
3. 是否通过 Overlay Runtime 统一策略。
4. 是否满足 API 兼容与迁移约束。
5. 是否补齐 sample 验证与文档说明。
6. 是否给出性能影响说明（至少定性）。
