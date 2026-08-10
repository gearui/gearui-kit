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
- 强制 `MainDemo` 使用 `App` 入口。
- 禁止 sample 直接挂 `Theme(...)` 和 `OverlayRoot(...)`。

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

4. Single App Root 护栏
SPEC 映射：
- 4.5 Runtime Environment & Insets Pipeline（App 入口约束）
- 11.1 REJECT / Runtime 边界

实现：
- 脚本：`scripts/ci/check_single_app_root.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- sample 树只允许一个 `App` 入口，且必须在 `MainDemo.kt`。
- 禁止 sample 业务页面新增第二个 `App` 根入口，避免 runtimeFlags 被覆盖。

5. Token Schema 兼容检查（P0）  
SPEC 映射：
- 4.1 Token 治理（schema + 语义漂移）
- 11.1 REJECT / 2. Token 与样式

实现：
- 脚本：`scripts/ci/dump_token_snapshot.py`（生成）+ `scripts/ci/check_token_compat.sh`（diff 检查）
- 基线：`gearui-kit/api/tokens.api`
- CI：`.github/workflows/guardrails.yml`

策略：
- 覆盖 `Colors` / `Typography` / `Shapes` 三个 data class 的字段集合（删除/重命名 → fail）。
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

7. i18n 默认文案护栏（P0，硬门禁）
SPEC 映射：
- 12 I18n 分层架构
- 11.1 REJECT / 组件不得内嵌本地化文案

实现：
- 脚本：`scripts/ci/check_i18n_default_text.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- baseline = 0，不是债务冻结：库源码（`com/gearui/i18n/` 以外）出现任何中文字面量即 fail。
- 注释不计入；用 `perl -CSD` 解码，`$.` 每文件重置以保证行号可点击。

8. 圆角标度护栏（P0，硬门禁）
SPEC 映射：
- 4.1 Token 治理（Shapes 六档标度）
- 11.1 REJECT / 2. Token 与样式

实现：
- 脚本：`scripts/ci/check_component_hardcoded_radius.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- baseline = 0。除 `theme/Shapes.kt`（标度定义）与 `overlay/OverlayDefaults.kt`（sheet 形状派生）
  外，禁止 `RoundedCornerShape(...)` 里出现字面量 dp 或 `Spacing.*`。
- 组件读 `Theme.shapes.*`；浮层读 `OverlayDefaults.{panelShape,modalShape,sheetShape}`；
  需要 Dp 的 token 层读 `foundation.layout.Radius.*`。
- 离档值必须吸附到最近档位，不得新增档位。
- **拦 `Spacing.*` 当圆角**：两条轴都是 Dp 且数值撞车（`Spacing.md` 与 `Radius.xl` 同为 12dp），
  写成 `RoundedCornerShape(Spacing.md)` 今天渲染正确，但把圆角绑死在间距标度上。
- 匹配覆盖具名参数形式（`RoundedCornerShape(topStart = 12.dp, ...)`）——早期只匹配紧跟
  左括号的数字，漏掉了 CalendarPopup 的贴边 sheet。

9. 组件层间距护栏（P1，债务冻结）
SPEC 映射：
- 4.1 Token 治理（Spacing）

实现：
- 脚本：`scripts/ci/check_component_hardcoded_spacing.sh`
- 基线：`scripts/ci/hardcoded_spacing_baseline.txt`
- CI：`.github/workflows/guardrails.yml`

策略：
- 与颜色护栏同构：冻结现存 `<n>.dp`，只阻断新增。
- 仅覆盖 `components/`；`foundation/primitives` 的几何值按设计归其所有。
- 基线只允许缩小。

10. 遗留 Float token 池护栏（P1，扩散阻断）
SPEC 映射：
- 4.1 Token 治理（单一来源）

实现：
- 脚本：`scripts/ci/check_legacy_token_pool.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- **池子已删除**（root `Radius.kt` / root `Typography.kt` / `foundation/tokens/ComponentTokens.kt`），
  护栏语义从「冻结扩散」翻转为「防止复活」：三个文件存在即 fail，任何 import 即 fail。
- Input / Tag 已迁到 `foundation/input` / `foundation/tag` 的 Dp token。
- 曾经的风险点：那套 Float 池的圆角标度是 3/6/9/12，与 `theme/Shapes` 的 0/4/6/8/12 不一致。

11. 阴影标度护栏（P0，硬门禁）
SPEC 映射：
- 4.1 Token 治理（Elevation 四档标度）
- 11.1 REJECT / 2. Token 与样式

实现：
- 脚本：`scripts/ci/check_component_hardcoded_elevation.sh`
- 标度：`foundation/elevation/Elevation.kt`
- CI：`.github/workflows/guardrails.yml`

策略：
- baseline = 0。拦两类：字面量 dp 当 elevation、以及 `Spacing.*` 当 elevation。
- 后者是重点：两条轴都是 Dp 且数值撞车（`Spacing.xs` 与 `Elevation.raised` 同为 4dp），
  编译通过、肉眼也对，但间距标度一调就会把阴影一起带偏。
- border-first：Card/Cell/输入框保持无阴影，只有浮层可以有 elevation。

12. 描边宽度标度护栏（P0，硬门禁）
SPEC 映射：
- 4.1 Token 治理（BorderWidth 三档标度）
- 11.1 REJECT / 2. Token 与样式

实现：
- 脚本：`scripts/ci/check_component_hardcoded_border.sh`
- 标度：`foundation/border/BorderWidth.kt`
- CI：`.github/workflows/guardrails.yml`

策略：
- baseline = 0。拦 `.border(<n>.dp` 与 `thickness/borderWidth = <n>.dp`。
- border-first 设计语言里描边承担层次表达，理应与 Shapes/Elevation/Spacing 一样有命名标度；
  在此之前是 87 处散落字面量，且已漂移（Radio/Checkbox 用 1.5dp，其余控件用 1dp）。
- 分隔线的 `height/width` **不拦**：1dp 高的 Box 与 1dp 高的留白在文本扫描下无法区分，
  猜错的两种代价（误报布局代码 / 放过真描边）都不可接受，交给评审。
- 禁止新增焦点态档位（会导致内容盒尺寸变化 → 布局跳动）。

13. iOS pod 资源打包护栏（P0，硬门禁）
SPEC 映射：
- 6.4 Sample 即架构验证

实现：
- 脚本：`scripts/ci/check_ios_pod_resources.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- 断言 `project.pbxproj` 保留 `[CP] Copy Pods Resources` build phase。
- 成因：`syncSharedAssetsToPodResources` 只是 `:sample:syncFramework` 的 finalizer，
  在真正构建之前跑 `pod install`，资源目录是空的 → CocoaPods 判定「无资源」并静默删掉拷贝阶段。
- 失败形态极具迷惑性：**构建成功、应用能跑、只是 101 个图标全部变空白**。
  图标经 coil3 从 `assets://icons/<name>.png` 加载，缺资源时渲染成尺寸正确的空盒而不是报错。
- 正确顺序：先跑一次触发 `syncFramework` 的构建，再 `pod install`。

14. Emoji 当图标护栏（P0，硬门禁）
SPEC 映射：
- 4.1 Token 治理（Icon 系统单一入口）
- 11.1 REJECT / 组件不得绕过 Icon primitive

实现：
- 脚本：`scripts/ci/check_emoji_as_icon.sh`
- CI：`.github/workflows/guardrails.yml`

策略：
- baseline = 0。拦字符串字面量里的 emoji / dingbat / 符号箭头。
- emoji 是文本字形：**不吃 `tint`**（无法跟随主题色）、由平台字体绘制（iOS/Android/Web 三样）、
  绕过 Icon primitive 因而对图标工具链与 allowlist 不可见。
- 它还会掩盖故障：iOS 资源拷贝阶段丢失、所有真图标渲染成空白时，
  DatePicker 的 emoji 仍在，整族看起来"半正常"而不是明显坏掉。
- 顺带清掉两处字符串哨兵（Rate 的 `"★"/"☆"`、Image 的 `"📷"`）：它们从不上屏，
  只是"用内置图标"的暗号，改成 `null` 后语义自明。

15. 图标尺寸标度护栏（P0，硬门禁）
SPEC 映射：
- 4.1 Token 治理（IconSizes 标度）

实现：
- 脚本：`scripts/ci/check_component_hardcoded_icon_size.sh`
- 标度：`foundation/typography/IconTokens.kt`
- CI：`.github/workflows/guardrails.yml`

策略：
- baseline = 0。`IconSizes.Default.{xs,sm,md,lg,xl}` = 12/14/16/18/24（行内图标），
  `IconSizes.Display.{sm,md,lg}` = 28/36/40（空状态/结果页插画）。
- **标度按实际用法重定**：旧标度 `small/medium/large = 14/18/24` 缺 16dp，
  而整个 field 家族的尾部箭头都用 16 —— 标度缺了真正需要的档位，被绕过的就是标度本身。
  IconTokens 的 KDoc 一直写着「禁止硬编码」，实测 37 处里 34 处是字面量。
- 行内与插画分成两组：两者尺寸区间（12-24 vs 28-40）不重叠，混成一条会让档位失真。
- `foundation/avatar/AvatarTokens.kt` 豁免：那是头像尺寸，不是图标。

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
- 静态检查禁止在 `App` 根容器与 `OverlayHost` 根层引入 safeArea padding。
- 静态检查关键组件（NavBar/BottomNavBar/Drawer/ActionSheet）优先读取 `LocalRuntimeEnvironment`。
- 先告警模式上线，稳定后升级为阻断。

4. App 层 Single Root 扩展检查（P1）
SPEC 映射：
- 4.5 Runtime Environment & Insets Pipeline（App 入口约束）
- 11.1 REJECT / Runtime 边界

计划：
- 将 single-root 检查从 sample 扩展到业务 App 代码仓（跨仓 guard）。
- 若页面覆盖 `View` 自动包装，必须显式声明（如 `autoWrapApp=false`）并保留唯一 `App` 入口。

5. Built-in assets verification（P1/P2）
SPEC 映射：
- 4.8 Built-in Assets Integration Contract
- 11.1 REJECT / Built-in Assets

计划：
- P1：抽出共享 Gradle 脚本 `gradle/gearui-resources.gradle.kts`，sample 与业务 App 共用。
- P2：发布 `gearui-gradle-plugin`（`id("com.gearui.kit.integration")`），统一注册资源复制 + 平台 bundle 接入。
- P2：新增 `verifyGearuiResources` 任务，校验 iOS `.app/compose-resources/icons/`、Android assets、Web static 资源数量与 GearUI 内置清单一致。
- 先夜跑告警，稳定后升级为发布阻断。
