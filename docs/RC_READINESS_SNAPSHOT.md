# GearUI Kit 1.0 RC Readiness Snapshot

Status: **盘点快照，不改代码。** 用于把本轮（token 立法 → 全库迁移 → bridge 删除 → 暗色 P0 → Radius 收口）正式封口。
Snapshot HEAD: `b282171`（refactor(layout): align Radius scale with frozen Shapes）
Date: 2026-05-23

## 1. 质量闸门（当前实测）

| 闸门 | 结果 |
|---|---|
| gearui-kit deprecation warnings | **0** |
| `check_token_compat` | **PASS** |
| `check_component_hardcoded_colors`（components 层） | **PASS**（new=0） |
| `:gearui-kit:apiCheck`（BCV JVM + KLib） | **PASS** |
| `:gearui-kit:compileKotlinMetadata` | **PASS** |
| `:gearui-kit:compileKotlinIosSimulatorArm64` | **PASS** |
| `:sample:compileDebugKotlinAndroid` | **PASS** |
| privchat-app（设备 local platform 运行） | **PASS**（SDK 连本机、界面正常） |

## 2. 本轮已完成（主线）

- **Token 立法**：`TOKEN_FREEZE_DECISIONS.md` 5 项冻结决策（Colors 24 字段语义模型 / Shapes 6 档 / Spacing 单源 / Motion / ComponentTokens 取代 ComponentSpecs）。
- **四层 token 体系**落地（Primitive → Semantic → Component → Runtime），新增 `MotionTokens`、`OverlayDefaults.scrimColor`。
- **全库组件族迁移**（Batch 1–12）+ sample 71 文件：deprecation 788 → 0（burn-down 表全程追踪）。
- **破坏性删除**（Batch 13）：Colors/Shapes/root Spacing/ComponentSpecs 全部 `@Deprecated` 桥接 + 死代码（TagColorTokens/TabColors）+ 过时枚举（OverlayPlacement.TopStart/BottomStart）。BCV 基线已刷新。
- **暗色 P0 修复**：新增 `successForeground/warningForeground/infoForeground` 主题感知 token，修正 Button/Toast/Tag/Progress/Badge/ActionSheet 彩色实底文字误用 `primaryForeground`；scrim 改纯黑 55%。
- **Radius 双标准收口**：`foundation.layout.Radius` 对齐 `Shapes`（0/4/6/8/12/9999）。
- **下游适配**：privchat-ui / privchat-app / live-chat / lms-app 四仓全部迁到 1.0 token。

## 3. 1.0 Breaking Changes

全部记录于 `docs/MIGRATION_1_0.md`：
- Colors 过时 bridge 字段移除（28 项映射）
- Shapes 过时 bridge 字段移除（6 项）
- root-package `Spacing` 移除（→ `foundation.layout.Spacing`）
- `ComponentSpecs` 移除（9 个 `*Specs` → 对应 `XxxTokens`）
- `TagColorTokens` / `TabColors` / `TabColorTokens` 死代码移除
- `OverlayPlacement.TopStart/BottomStart` → `TopLeft/BottomLeft`
- 追加：`successForeground/warningForeground/infoForeground`（additive）
- 追加：`Radius` scale 对齐 Shapes（small 3→sm 4 / large 9→lg 8）

## 4. RC blocker vs Known debt

判定原则：**RC blocker = 不修不能发**；**Known debt = 已登记，可进 1.0.x / 1.1**。

### 4.1 RC blocker（必须在 1.0 RC / GA 前做）

| 项 | 性质 | 状态 |
|---|---|---|
| RC-1 视觉 smoke（Android + iOS，明暗 + DarkPurple） | 验证，非重构 | 待做 |
| CHANGELOG / release notes | 发布物料 | 待做 |
| 发布 dry-run（Sonatype Central，首发走 beta） | 发布流程 | 待做 |
| README / RC note 诚实标注 Web/鸿蒙暂不支持 | 文档 | 待做 |

> 当前**代码层无 RC blocker**：deprecation=0、token 冻结、编译全绿、下游已适配。剩余 blocker 都是验证 / 文档 / 发布动作，不是代码重构。

### 4.2 Known debt（不阻断 RC，除非 RC-1 smoke 发现真实问题）

| # | 项 | 性质 | 目标里程碑 | 来源 |
|---|---|---|---|---|
| 1 | Overlay Family 视觉契约：Popover/Tooltip 暗色下沉 PopoverTokens；Dialog/Popup/Sheet/ActionSheet 的 elevated surface/border/shadow/radius/motion 统一成一套 contract | 视觉 polish | **1.0.x** | DESIGN_AUDIT P1-4 |
| 2 | 暗色 destructive 实底白字偏弱（F87171 浅红 + 白字 ~2.3）；属暗色 palette 取色，非内容 token | 暗色 polish | **1.0.x** | DARK_MODE_CONTRAST_AUDIT |
| 3 | 两套 Cell（composite/Cell、components/cell/Cell）/ 两套 CardTokens / ComponentTokens legacy pool（Float 老池子）合并 | 结构债 | **1.0.x** | 本轮迁移延后项 |
| 4 | Input 族状态模型：input/ring + focused/invalid/disabled/readonly + keyboard dismiss / local focus；体量中、易引入交互回归 | 交互模型 | **1.1**（除非 RC-1 发现输入框明显问题） | DESIGN_AUDIT P0-4/P1-1 |
| 5 | Density 模式（comfortable/default/compact），需先在 Card/Cell/List 验证 | 新能力 | **1.1** | TOKEN_FREEZE Open Items |
| 6 | Elevation token（e0/e1/e2/floating），需设计输入；当前 Card 走 border-first | 新能力 | **1.1**（待设计） | TOKEN_FREEZE Open Items |
| 7 | Brand pack 机制（SPEC §7.1），独立决策 | 新能力 | **1.1** | TOKEN_FREEZE Open Items |
| 8 | Upload 组件（gearui-kit 未实现，tdesign-flutter 有参考） | 新增功能 | **1.1** | 能力缺口 |
| 9 | Web / 鸿蒙：Web 被 KuiklyUI Compose Web entry 阻塞（core-ksp 无 JsTargetEntryBuilder），鸿蒙放后面 | 平台支持 | **1.1 spike**（README 须诚实标注） | 上游阻塞 |

## 5. 后续 RC 流程（建议顺序）

- **RC-0 Snapshot** ← 本文件（done）
- **RC-1 Visual smoke**：Android + iOS 手动过一遍 sample（明暗 + DarkPurple），重点看本轮改动点（feedback 实底文字、scrim、Badge、Radius 视觉）
- **RC-2 Package publish dry-run**：vanniktech + Sonatype Central Portal 验证（首次外部发布走 beta）
- **RC-3 CHANGELOG / release notes**

## 6. Post-1.0 Roadmap（优先级汇总）

- **RC 前必须**：RC-1 visual smoke · CHANGELOG/release notes · 发布 dry-run · README 标注 Web/鸿蒙状态
- **1.0.x**：Overlay visual contract · 暗色 polish（destructive 浅红等）· Cell/Card/ComponentTokens 结构收敛
- **1.1**：Input state model · Density · Elevation · Brand Pack · Upload · Web/Harmony spike

> 结论：仍有不规范，但**当前都是 Known debt，不是 RC blocker**。停止继续顺手重构，先走 RC-1 视觉 smoke——只有 smoke 发现真实阻断才回头修，否则按上方 roadmap 排期。
