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

## 4. Open Items（不阻断 RC，排入 1.0.x / 1.1，除非 smoke 发现真实阻断）

| 项 | 说明 | 来源 |
|---|---|---|
| Elevation token | 占位 e0/e1/e2/floating，需设计师定阴影值 | TOKEN_FREEZE Open Items |
| Density 模式 | comfortable/default/compact，需先用 Cell/Card 验证 | 同上 |
| Brand pack 机制 | SPEC §7.1，独立决策 | 同上 |
| Overlay Family 视觉契约（#4） | Popover dark-tooltip 下沉 PopoverTokens；暗色弹层 elevated surface 方案 | DESIGN_AUDIT P1-4 |
| Input 族状态模型 + SearchBar 本地 focus（#5） | `input`/`ring` 语义 token 尚未真正用起来 | DESIGN_AUDIT P0-4/P1-1 |
| 两套 Cell / 两套 CardTokens / ComponentTokens legacy pool（#6/#7） | 组件合并 + Float 老池子 Dp 化，结构债 | 本轮迁移延后项 |
| 暗色 destructive 实底白字偏弱 | 暗色 destructive=F87171（浅红）+白字对比 ~2.3，属暗色 palette 取色，非内容 token 问题 | DARK_MODE_CONTRAST_AUDIT |
| Web / 鸿蒙 | KuiklyUI Compose Web 上游不完整（core-ksp 无 JsTargetEntryBuilder），1.0 暂不可行 | 上游阻塞 |
| Upload 组件 | gearui-kit 未实现（tdesign-flutter 有参考实现），属新增 | 能力缺口 |
| CHANGELOG / release notes | 1.0.0 release 时补 | 发布流程 |

## 5. 后续 RC 流程（建议顺序）

- **RC-0 Snapshot** ← 本文件（done）
- **RC-1 Visual smoke**：Android + iOS 手动过一遍 sample（明暗 + DarkPurple），重点看本轮改动点（feedback 实底文字、scrim、Badge、Radius 视觉）
- **RC-2 Package publish dry-run**：vanniktech + Sonatype Central Portal 验证（首次外部发布走 beta）
- **RC-3 CHANGELOG / release notes**

> 结论：主线已接近 RC 前状态，质量闸门全绿。停止继续顺手重构（#4/#5/#6/#7 放 1.0.x/1.1），先走 RC-1 视觉 smoke 再决定是否有真实阻断。
